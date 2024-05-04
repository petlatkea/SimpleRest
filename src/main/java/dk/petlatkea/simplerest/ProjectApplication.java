package dk.petlatkea.simplerest;

import dk.petlatkea.simplerest.config.InitData;
import dk.petlatkea.simplerest.courses.CourseController;
import dk.petlatkea.simplerest.courses.CourseControllerWrapper;
import dk.petlatkea.simplerest.courses.CourseRepository;
import dk.petlatkea.simplerest.framework.GenericController;
import dk.petlatkea.simplerest.framework.GenericServer;
import dk.petlatkea.simplerest.students.StudentController;
import dk.petlatkea.simplerest.students.StudentControllerWrapper;
import dk.petlatkea.simplerest.students.StudentRepository;

import java.io.IOException;

public class ProjectApplication {
  public static void main(String[] args) throws IOException {
    // Create instances to inject later
    StudentRepository studentRepository = new StudentRepository();
    StudentController studentController = new StudentController(studentRepository);

    CourseRepository courseRepository = new CourseRepository();
    CourseController courseController = new CourseController(courseRepository);

    // Create and run initData
    InitData initData = new InitData(studentRepository, courseRepository);
    initData.run();

    // Create the controller wrappers to be given to the generic controller
    StudentControllerWrapper studentControllerWrapper = new StudentControllerWrapper(studentController);
    CourseControllerWrapper courseControllerWrapper = new CourseControllerWrapper(courseController);

    // Create server - add controllers - and start
    GenericServer genericServer = new GenericServer();
    genericServer.addController(new GenericController(studentControllerWrapper));
    genericServer.addController(new GenericController(courseControllerWrapper));
    genericServer.start();
  }
}
