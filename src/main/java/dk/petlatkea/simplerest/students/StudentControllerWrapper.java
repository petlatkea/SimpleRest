package dk.petlatkea.simplerest.students;

import dk.petlatkea.simplerest.framework.Controller;
import dk.petlatkea.simplerest.framework.GenericController;
import dk.petlatkea.simplerest.framework.RequestObject;
import dk.petlatkea.simplerest.framework.ResponseObject;
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
    String json = convertListOfStudentsToJson(students);
    res.sendJson(json);
  }

  public void getStudent(RequestObject req, ResponseObject res) {
    // Find the student with the given id
    int id = req.getPathVariable_id();
    Optional<Student> student = studentController.getStudent(id);

    // if student exists - return it as json - otherwise return 404
    if(student.isPresent()) {
      String json = convertStudentToJson(student.get());
      res.sendJson(json);
    } else {
      res.sendNotFound();
    }
  }

  public void createStudent(RequestObject req, ResponseObject res) {
    // Get the json body from the request
    String json = req.getJsonBody();

    Student student = createStudentFromJson(json);
    Student createdStudent = studentController.createStudent(student);
    String jsonResponse = convertStudentToJson(createdStudent);
    res.sendJson(jsonResponse);
  }

  public void deleteStudent(RequestObject req, ResponseObject res) {
    // find - and delete - the student with the given id
    int id = req.getPathVariable_id();
    Optional<Student> student = studentController.deleteStudent(id);

    // if student exists - return it as json - otherwise return 404
    if(student.isPresent()) {
      String json = convertStudentToJson(student.get());
      res.sendJson(json);
    } else {
      res.sendNotFound();
    }
  }

  //#region JSON conversion methods

  // Conversion TO JSON - serialization
  private String convertListOfStudentsToJson(List<Student> students) {
    StringBuilder json = new StringBuilder("[");
    for(int i=0; i < students.size(); i++) {
      json.append(convertStudentToJson(students.get(i)));
      if(i < students.size() - 1) {
        json.append(",");
      }
    }
    json.append("]");
    return json.toString();
  }

  private String convertStudentToJson(Student student) {
    JSONSerializer jsonSerializer = new JSONSerializer();
    return jsonSerializer.convertObjectToJson(student);
  }

  // Conversion FROM JSON - deserialization

  private Student createStudentFromJson(String json) {
    // Parse JSON and create Student object
    // search for the string "id": and get the number after it
    String id = getJsonValue(json, "id");
    String name = getJsonString(json, "name");
    String email = getJsonString(json, "email");
    String birthday = getJsonString(json, "birthday");

    Student student = new Student();
    if(id != null) {
      student.setId(Integer.parseInt(id));
    }
    if(name != null) {
      student.setName(name);
    }
    if(email != null) {
      student.setEmail(email);
    }
    if(birthday != null) {
      student.setBirthday(LocalDate.parse(birthday));
    }

    return student;
  }

  // Helper methods for JSON parsing

  private String getJsonString(String json, String key) {
    String value = getJsonValue(json, key);
    if(value == null) {
      return null;
    } else {
      return value.substring(1, value.length()-1);
    }
  }

  private String getJsonValue(String json, String key) {
    int beginIndex = json.indexOf("\""+key+"\":");
    if( beginIndex == -1) {
      return null;
    }
    beginIndex += 3 + key.length();
    int endIndex = json.indexOf(",", beginIndex);
    if(endIndex == -1) {
      endIndex = json.indexOf("}", beginIndex);
    }
    return json.substring(beginIndex, endIndex).trim();
  }

  //#endregion JSON conversion methods

}
