package dk.petlatkea.simplerest.framework;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

/**
 * A ResponseObject is sent to a RequestHandler together with the RequestObject
 * The ResponseObject is used to "return" responses (in JSON format) to the client.
 *
 * It has methods for sending JSON and for sending a 404 Not Found response.
 */
public class ResponseObject {
  private HttpExchange exchange;

  public ResponseObject(HttpExchange exchange) {
    this.exchange = exchange;
  }

  public void sendNotFound()  {
    try {
      exchange.sendResponseHeaders(404, 0);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    exchange.close();
  }

  public void sendJson(String json) {
    try {
      exchange.getResponseHeaders().set("Content-Type", "application/json");
      exchange.sendResponseHeaders(200, json.length());
      exchange.getResponseBody().write(json.getBytes());
    } catch (IOException e) {
      System.err.println("Error sending JSON response");
      System.err.println(e);
      throw new RuntimeException(e);
    }
    exchange.close();
  }
}
