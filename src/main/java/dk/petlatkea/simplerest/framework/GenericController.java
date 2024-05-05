package dk.petlatkea.simplerest.framework;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dk.petlatkea.simplerest.framework.annotations.*;
import dk.petlatkea.simplerest.framework.json.JSONDeserializer;
import dk.petlatkea.simplerest.framework.json.JSONSerializer;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A GenericController handles the actual HTTP communication, but requires an implementation
 * of Controller to register the routes it should handle.
 * The GenericController then receives HTTP requests, matches the registered routes,
 * and calls the RequestHandler-method (with a RequestObject and ResponseObject)
 * when a match is found.
 *
 * The routes are now registered with @GetMapping annotations, and this GenericController
 * builds its own RequestHandler-method for each one, effectively wrapping the controller
 * method in something that automatically serializes and deserializes JSON, as well as
 * filling in @PathVariable and @RequestBody parameters
 *
 * This allows for very "Spring Boot-like" controllers with annotated methods
 *
 */
public class GenericController implements HttpHandler {

  private final Map<String, RequestHandler> routes = new HashMap<>();
  private String basePath = "/"; // Default basePath for any controller is / - this is usually overwritten by routes

  public GenericController(Object controller) {
    registerRouteAnnotations(controller);
  }

  private void registerRoute(String method, String path, RequestHandler handler) {
    routes.put(method.toUpperCase() + path, handler);
  }

  private void registerRouteAnnotations(Object controller) {

    System.out.println("Scanning controller: " + controller.getClass().getSimpleName());

    // Get all methods from the controller
    // For each method - check if it has a Get-, Post- or DeleteMapping annotation
    // If it does - create a handler method and register it with the route
    for(Method method : controller.getClass().getDeclaredMethods()) {

      System.out.println("- scanning method: " + method.getName());

      String httpMethod = null;
      String httpUri = null;
      Annotation requestMapping = null;

      // Check the annotation(s) on this method
      // - if it has GetMapping, PostMapping or DeleteMapping - it is a route
      // - store the http-method and the annotation itself (for details about path)
      Annotation[] methodAnnitations = method.getAnnotations();
      for (Annotation annotation : methodAnnitations) {
        // NOTE: A method should have only one of these ...
        if (annotation instanceof GetMapping anno) {
          httpMethod = "GET";
          httpUri = anno.value();
          requestMapping = annotation;
        } else if (annotation instanceof PostMapping anno) {
          httpMethod = "POST";
          httpUri = anno.value();
          requestMapping = annotation;
        } else if (annotation instanceof DeleteMapping anno) {
          httpMethod = "DELETE";
          httpUri = anno.value();
          requestMapping = annotation;
        }
      }

      // We are only interested in methods that HAVE a request mapping of some sort
      if (requestMapping != null) {

        // Check the path - if none is present, use the basePath of the controller
        if (httpUri == null) {
          httpUri = basePath;
        }

        // Check if the basePath have been improved - and take it from the latest route
        if( basePath.length() < httpUri.length() ) {
          // find the suggested basePath from latest httpUri - the part before the second /
          int secondSlash = httpUri.indexOf('/',1);
          String suggestedBasePath = httpUri;
          if(secondSlash != -1) {
            suggestedBasePath = suggestedBasePath.substring(0,secondSlash);
          }
          basePath = suggestedBasePath;
        }

        // Check parameters - and build a ParameterHelper array
        Parameter[] parameters = method.getParameters();
        ParameterHelper paramHelpers[] = new ParameterHelper[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
          int index = i;
          Parameter parameter = parameters[i];
          Class type = parameter.getType();
          String name = parameter.getName();
          ParameterHelper.Source source = null;

          // There should be only one annotation on each parameter - no more, no less
          // but just in case, we loop through all possibilities
          for (Annotation parameterAnnotation : parameter.getAnnotations()) {
            // if parameterAnnotation is a pathvariable
            if (parameterAnnotation instanceof PathVariable) {
              source = ParameterHelper.Source.PATH_VARIABLE;
            } else if (parameterAnnotation instanceof RequestBody) {
              source = ParameterHelper.Source.REQUEST_BODY;
            }
          }

          // Now we know the name, type and source of this parameter -
          // - store it for use when the method is called
          paramHelpers[i] = new ParameterHelper(index, type, name, source);
        }

        // Now we can register a route with a newly created handler function
        registerRoute(httpMethod, httpUri, (req, res) -> {

          try {
            // Prepare to invoke the method we are analysing
            // But first build an array of args to that method
            Object[] args = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
              // Set a value for each parameter - get them from the request object
              Object value = null;
              // if it is a pathvariable - simply get that path variable
              if (paramHelpers[i].getSource() == ParameterHelper.Source.PATH_VARIABLE) {
                // TODO: Use name, rather than hardcoded to id.
                value = req.getPathVariable_id();
              }
              // If it is the requestbody, deserialize the json into the appropriate object
              if (paramHelpers[i].getSource() == ParameterHelper.Source.REQUEST_BODY) {
                String json = req.getJsonBody();
                value = JSONDeserializer.fromJSON(paramHelpers[i].getType(), json);
              }
              // Put the value into the argument array
              args[i] = value;
            }

            // Invoke the method and maybe receive a return object
            Object object = method.invoke(controller, args);

            // Now, this is our rules - hardcoded into the controller:
            // * if the object is null:
            // - - ?don't return anything?
            // * if the object is an empty Optional:
            // - - respond with 404 NOT_FOUND
            // * if the object is an Optional with something
            // - - serialize the something to json, and return that
            // * if the object is anything else:
            // - - serialize the object, and return it

            if (object instanceof Optional<?> optional) {
              if (optional.isEmpty()) {
                res.sendNotFound();
              } else {
                String jsonResponse = JSONSerializer.toJSON(optional.get());
                res.sendJson(jsonResponse);
              }
            } else {
              res.sendJson((JSONSerializer.toJSON(object)));
            }

          } catch (Exception e) {
            throw new RuntimeException(e);
          }

        });

      }
    }

  }

  public String getBasePath() {
    return basePath;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String requestMethod = exchange.getRequestMethod();
    String requestPath = exchange.getRequestURI().getPath();

    boolean routeFound = false;

    // loop through all the registered routes
    // - check if the requestmethod matches
    // - if the path contains a variable - check that the part before that matches
    // - and check if the request provided a variable
    for (String route : routes.keySet()) {
      // First - check the requestMethod - if that doesn't match - it is not this route
      if (route.startsWith(requestMethod)) {
        // split the route into method, path and (optional) variable
        String routePath = "";
        String variableName = null;
        String variableValue = null;

        // The path is the part after the method
        routePath = route.substring(requestMethod.length());

        // if there is a variable - it would be enclosed in { }s
        // For now: just hardcode it to expect only a single "id" variable if any
        if (routePath.contains("{id}")) {
          variableName = "id";
          // This means that the route path ends just before {
          routePath = routePath.substring(0,routePath.indexOf("{"));
        }

        // Check if the routePath matches the actual path
        if (requestPath.equals(routePath) || (variableName != null && requestPath.startsWith(routePath) && requestPath.length() > routePath.length())) {

          // if there are no variables - the routePath must be an EXACT match

          RequestObject req = new RequestObject();
          // Only now are we (quite) sure that this must be the requested route!
          // If there was supposed to be a variable - extract that
          if (variableName != null) {
            variableValue = requestPath.substring(routePath.length());

            // As we only expect id - just parse it immediately
            int id = Integer.parseInt(variableValue);
            // and store it in the request
            req.setPathVariable_id(id);
          }

          // Get the request body (expect it to be JSON)
          String json = new String(exchange.getRequestBody().readAllBytes());
          // and store that in the request
          req.setJsonBody(json);

          // Create a response object as well
          ResponseObject res = new ResponseObject(exchange);

          // And finally call the function that this route is mapped to
          RequestHandler function = routes.get(route);
          function.handleRequest(req, res);

          // note that we have found a matching route
          routeFound = true;
          // break the loop - we have found the right route
          break;
        }
      }
    }

    // if no matching route was found - respond with a NOT ALLOWED
    if(!routeFound) {
      exchange.sendResponseHeaders(405, 0);
      exchange.close();
    }
  }

}
