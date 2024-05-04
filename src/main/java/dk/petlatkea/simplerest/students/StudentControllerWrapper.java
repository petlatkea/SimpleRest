package dk.petlatkea.simplerest.students;

import dk.petlatkea.simplerest.framework.Controller;
import dk.petlatkea.simplerest.framework.GenericController;
import dk.petlatkea.simplerest.framework.RequestObject;
import dk.petlatkea.simplerest.framework.ResponseObject;
import dk.petlatkea.simplerest.framework.json.JSONDeserializer;
import dk.petlatkea.simplerest.framework.json.JSONSerializer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * The StudentControllerWrapper is a wrapper around the StudentController that makes it an actual REST Controller.
 *
 * It registers the routes with the GenericController, and provides methods that accepts RequestObject and ResponseObject.
 *
 * For now, it also handles the JSON serialization and deserialization - but this isn't really "controller-work".
 */
public class StudentControllerWrapper implements Controller {

  private final StudentController studentController;

  public StudentControllerWrapper(StudentController studentController) {
    this.studentController = studentController;
  }

  @Override
  public String getBasePath() {
    return "/students";
  }

  public void registerRoutes(GenericController genericController) {
    // GET /students
    genericController.registerRoute("GET", "/students", this::getStudents);
    genericController.registerRoute("GET", "/students/{id}", this::getStudent);
    genericController.registerRoute("POST", "/students", this::createStudent);
    genericController.registerRoute("DELETE", "/students/{id}", this::deleteStudent);
  }

  // Request handlers - wraps the StudentController methods

  public void getStudents(RequestObject req, ResponseObject res) {
    List<Student> students = studentController.getStudents();
    String json = JSONSerializer.toJSON(students);
    res.sendJson(json);
  }

  public void getStudent(RequestObject req, ResponseObject res) {
    // Find the student with the given id
    int id = req.getPathVariable_id();
    Optional<Student> student = studentController.getStudent(id);

    // if student exists - return it as json - otherwise return 404
    if(student.isPresent()) {
      String json = JSONSerializer.toJSON(student.get());
      res.sendJson(json);
    } else {
      res.sendNotFound();
    }
  }

  public void createStudent(RequestObject req, ResponseObject res) {
    // Get the json body from the request
    String json = req.getJsonBody();

    Student student = (Student)JSONDeserializer.fromJSON(Student.class, json);
    Student createdStudent = studentController.createStudent(student);
    String jsonResponse = JSONSerializer.toJSON(createdStudent);
    res.sendJson(jsonResponse);
  }

  public void deleteStudent(RequestObject req, ResponseObject res) {
    // find - and delete - the student with the given id
    int id = req.getPathVariable_id();
    Optional<Student> student = studentController.deleteStudent(id);

    // if student exists - return it as json - otherwise return 404
    if(student.isPresent()) {
      String json = JSONSerializer.toJSON(student.get());
      res.sendJson(json);
    } else {
      res.sendNotFound();
    }
  }

}