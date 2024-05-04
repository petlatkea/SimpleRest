package dk.petlatkea.simplerest.framework;

import java.io.IOException;

/**
 * A RequestHandler is method that handles a request, when the Controller has matched a route,
 * it calls the registered RequestHandler with a RequestObject and a ResponseObject.
 *
 */
@FunctionalInterface
public interface RequestHandler {
  void handleRequest(RequestObject requestObject, ResponseObject responseObject) throws IOException;
}
