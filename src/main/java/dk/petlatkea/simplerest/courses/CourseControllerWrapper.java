package dk.petlatkea.simplerest.courses;

import dk.petlatkea.simplerest.framework.Controller;
import dk.petlatkea.simplerest.framework.RequestObject;
import dk.petlatkea.simplerest.framework.ResponseObject;
import dk.petlatkea.simplerest.framework.annotations.DeleteMapping;
import dk.petlatkea.simplerest.framework.annotations.GetMapping;
import dk.petlatkea.simplerest.framework.annotations.PostMapping;
import dk.petlatkea.simplerest.framework.json.JSONDeserializer;
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

  @GetMapping("/courses")
  public void getCourses(RequestObject req, ResponseObject res) {
    List<Course> courses = courseController.getCourses();
    String json = JSONSerializer.toJSON(courses);
    res.sendJson(json);
  }

  @GetMapping("/courses/{id}")
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

  @PostMapping("/courses")
  public void createCourse(RequestObject req, ResponseObject res) {
    String json = req.getJsonBody();
    Course course = (Course) JSONDeserializer.fromJSON(Course.class,json);
    Course newCourse = courseController.createCourse(course);
    String jsonResponse = JSONSerializer.toJSON(newCourse);
    res.sendJson(jsonResponse);
  }

  @DeleteMapping("/courses/{id}")
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

}