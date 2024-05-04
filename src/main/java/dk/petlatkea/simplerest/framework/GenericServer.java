package dk.petlatkea.simplerest.framework;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class GenericServer {
  private static final int PORT = 8080;

  private GenericController controller;

  public void setController(GenericController controller) {
    this.controller = controller;
  }

  public void start() throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

    server.createContext("/", controller); // TODO: Register controller and make that register routes ...

    server.start();

    System.out.println("Server listening on http://localhost:" + PORT + "/students");
  }

}