package dk.petlatkea.simplerest.courses;

import dk.petlatkea.simplerest.framework.annotations.*;

import java.util.List;
import java.util.Optional;

/**
 * The CourseControllerWrapper is still a wrapper around the CourseController that makes it an actual REST Controller.
 *
 * But as we now use @Annotations to register routes, and the controller doesn't need to handle request and response itself,
 * this class should be replaced by the actual controller in the next version
 *
 */
public class CourseControllerWrapper {

  private final CourseController courseController;

  public CourseControllerWrapper(CourseController courseController) {
    this.courseController = courseController;
  }

  @GetMapping("/courses")
  public List<Course> getCourses() {
    return courseController.getCourses();
  }

  @GetMapping("/courses/{id}")
  public Optional<Course> getCourse(@PathVariable int id) {
    return courseController.getCourse(id);
  }

  @PostMapping("/courses")
  public Course createCourse(@RequestBody Course course) {
    return courseController.createCourse(course);
  }

  @DeleteMapping("/courses/{id}")
  public Optional<Course> deleteCourse(@PathVariable int id) {
    return courseController.deleteCourse(id);
  }

}