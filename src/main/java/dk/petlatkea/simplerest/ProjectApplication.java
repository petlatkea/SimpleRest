package dk.petlatkea.simplerest;

import dk.petlatkea.simplerest.config.InitData;
import dk.petlatkea.simplerest.students.SimpleServer;
import dk.petlatkea.simplerest.students.StudentController;
import dk.petlatkea.simplerest.students.StudentRepository;

import java.io.IOException;

public class ProjectApplication {
  public static void main(String[] args) throws IOException {
    // Create instances to inject later
    StudentRepository studentRepository = new StudentRepository();
    StudentController studentController = new StudentController(studentRepository);

    // Create and run initData
    InitData initData = new InitData(studentRepository);
    initData.run();

    // Create and start server - with controller
    SimpleServer simpleServer = new SimpleServer();
    simpleServer.setController(studentController);
    simpleServer.start();
  }
}
