package dk.petlatkea.simplerest.framework;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class GenericServer {
  private static final int PORT = 8080;

  private List<GenericController> controllers = new ArrayList<>();

  public void addController(GenericController controller) {
    this.controllers.add(controller);
  }

  public void start() throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

    for (GenericController controller : controllers) {
      server.createContext(controller.getBasePath(), controller);
    }

    server.start();

    System.out.println("Server listening on http://localhost:" + PORT );
  }

}