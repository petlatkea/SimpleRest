package dk.petlatkea.simplerest.students;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class SimpleController implements HttpHandler {

  private final StudentController studentController;

  public SimpleController(StudentController studentController) {
    this.studentController = studentController;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String requestMethod = exchange.getRequestMethod();
    String path = exchange.getRequestURI().getPath();

    // Match the method and path with known (hardcoded) routes
    if("GET".equals(requestMethod) && "/students".equals(path)) {
      System.out.println("GET /students");
      List<Student> students = studentController.getStudents();

      sendJsonResponse(exchange, convertListOfStudentsToJson(students));

    } else if("GET".equals(requestMethod) && path.startsWith("/students/")) {
      int id = Integer.parseInt(path.substring(10));
      System.out.println("GET /students/{id} - with id: " + id);
      Optional<Student> student =  studentController.getStudent(id);

      if(student.isPresent()) {
        sendJsonResponse(exchange, convertListOfStudentsToJson(student.get()));
      } else {
        sendNotFoundResponse(exchange);
      }

    } else if("DELETE".equals(requestMethod) && path.startsWith("/students/")) {
      int id = Integer.parseInt(path.substring(10));
      System.out.println("DELETE /students/{id} - with id: " + id);

      Optional<Student> student =  studentController.deleteStudent(id);

      if(student.isPresent()) {
        sendJsonResponse(exchange, convertListOfStudentsToJson(student.get()));
      } else {
        sendNotFoundResponse(exchange);
      }

    } else if("POST".equals(requestMethod) && "/students".equals(path)) {
      System.out.println("POST /students");
      String json = new String(exchange.getRequestBody().readAllBytes());
      System.out.println("Request body: " + json);
      // Create a Request Student object
      Student student = createStudentFromJson(json);

      // Create the student - and receive the actually created student (with id)
      student = studentController.createStudent(student);

      // return the newly created student as json
      sendJsonResponse(exchange, convertListOfStudentsToJson(student));

    } else {
      // No route matched - send METHOD NOT ALLOWED
      sendNotAllowedResponse(exchange);
    }

  }

  private void sendNotAllowedResponse(HttpExchange exchange) throws IOException {
    exchange.sendResponseHeaders(405, 0);
    exchange.close();
  }

  private void sendNotFoundResponse(HttpExchange exchange) throws IOException {
    exchange.sendResponseHeaders(404, 0);
    exchange.close();
  }

  private void sendJsonResponse(HttpExchange exchange, String json) throws IOException {
    exchange.getResponseHeaders().set("Content-Type", "application/json");
    exchange.sendResponseHeaders(200, json.length());
    exchange.getResponseBody().write(json.getBytes());
    exchange.close();
  }

  private String convertListOfStudentsToJson(List<Student> students) {
    StringBuilder json = new StringBuilder("[");
    for(int i=0; i < students.size(); i++) {
      json.append(convertListOfStudentsToJson(students.get(i)));
      if(i < students.size() - 1) {
        json.append(",");
      }
    }
    json.append("]");
    return json.toString();
  }

  private String convertListOfStudentsToJson(Student student) {
    String jsonString = """
    {
      "id": %d,
      "name": "%s",
      "email": "%s",
      "birthday": "%s"
    }
    """;
    return String.format(jsonString, student.getId(), student.getName(), student.getEmail(), student.getBirthday());
  }

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

}