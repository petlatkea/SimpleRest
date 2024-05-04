package dk.petlatkea.simplerest.courses;

import dk.petlatkea.simplerest.framework.Controller;
import dk.petlatkea.simplerest.framework.GenericController;
import dk.petlatkea.simplerest.framework.RequestObject;
import dk.petlatkea.simplerest.framework.ResponseObject;
import dk.petlatkea.simplerest.framework.json.JSONSerializer;

import java.util.List;
import java.util.Optional;

/**
 * The CourseControllerWrapper is a wrapper around the CourseController that makes it an actual REST Controller.
 * It closely resembles the StudentControllerWrapper, but is adapted to the CourseController.
 * Note the many similarities and few differences between the two classes!
 *
 * It registers the routes with the GenericController, and provides methods that accepts RequestObject and ResponseObject.
 *
 * For now, it also handles the JSON serialization and deserialization - but this isn't really "controller-work".
 */
public class CourseControllerWrapper implements Controller {

  private final CourseController courseController;

  public CourseControllerWrapper(CourseController courseController) {
    this.courseController = courseController;
  }

  @Override
  public String getBasePath() {
    return "/courses";
  }

  public void registerRoutes(GenericController genericController) {
    genericController.registerRoute("GET", "/courses", this::getCourses);
    genericController.registerRoute("GET", "/courses/{id}", this::getCourse);
    genericController.registerRoute("POST", "/courses", this::createCourse);
    genericController.registerRoute("DELETE", "/courses/{id}", this::deleteCourse);
  }

  public void getCourses(RequestObject req, ResponseObject res) {
    List<Course> courses = courseController.getCourses();
    String json = JSONSerializer.toJSON(courses);
    res.sendJson(json);
  }

  public void getCourse(RequestObject req, ResponseObject res) {
    // Find the course with the given id
    int id = req.getPathVariable_id();
    Optional<Course> course = courseController.getCourse(id);

    // if course exists - return it as json - otherwise return 404
    if(course.isPresent()) {
      String json = JSONSerializer.toJSON(course.get());
      res.sendJson(json);
    } else {
      res.sendNotFound();
    }
  }

  public void createCourse(RequestObject req, ResponseObject res) {
    String body = req.getJsonBody();
    Course course = createCourseFromJson(body);
    Course newCourse = courseController.createCourse(course);
    String jsonResponse = JSONSerializer.toJSON(newCourse);
    res.sendJson(jsonResponse);
  }

  public void deleteCourse(RequestObject req, ResponseObject res) {
    // find - and delete - the course with the given id
    int id = req.getPathVariable_id();
    Optional<Course> course = courseController.deleteCourse(id);

    // if course exists - return it as json - otherwise return 404
    if(course.isPresent()) {
      String json = JSONSerializer.toJSON(course.get());
      res.sendJson(json);
    } else {
      res.sendNotFound();
    }
  }

  //#region JSON conversion methods

  // Conversion FROM JSON - deserialization

  private Course createCourseFromJson(String json) {
    String id = getJsonValue(json, "id");
    String name = getJsonString(json, "name");
    String abbreviation = getJsonString(json, "abbreviation");
    String teacher = getJsonString(json, "teacher");
    String schoolYear = getJsonValue(json, "schoolYear");

    Course course = new Course();
    if(id != null) {
      course.setId(Integer.parseInt(id));
    }
    if(name != null) {
      course.setName(name);
    }
    if(abbreviation != null) {
      course.setAbbreviation(abbreviation);
    }
    if(teacher != null) {
      course.setTeacher(teacher);
    }
    if(schoolYear != null) {
      course.setSchoolYear(Integer.parseInt(schoolYear));
    }

    return course;
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
