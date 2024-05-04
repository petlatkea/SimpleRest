package dk.petlatkea.simplerest.framework;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A GenericController handles the actual HTTP communication, but requires an implementation
 * of Controller to register the routes it should handle.
 * The GenericController then receives HTTP requests, matches the registered routes,
 * and calls the RequestHandler-method (with a RequestObject and ResponseObject)
 * when a match is found.
 *
 * This allows for very "Express-like" controllers with methods that receives (req, res)
 *
 */
public class GenericController implements HttpHandler {

  private final Map<String, RequestHandler> routes = new HashMap<>();

  public GenericController(Controller controller) {
    controller.registerRoutes(this);
  }

  public void registerRoute(String method, String path, RequestHandler handler) {
    routes.put(method.toUpperCase() + path, handler);
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
