package dk.petlatkea.simplerest.students;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SimpleServer {
  private static final int PORT = 8080;

  private StudentController studentController;

  public void setController(StudentController studentController) {
    this.studentController = studentController;
  }

  public void start() throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

    server.createContext("/students", new SimpleController(this.studentController));

    server.start();

    System.out.println("Server listening on http://localhost:" + PORT + "/students");
  }

}