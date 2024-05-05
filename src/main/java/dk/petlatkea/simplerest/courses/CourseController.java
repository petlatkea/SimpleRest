package dk.petlatkea.simplerest.courses;

import dk.petlatkea.simplerest.framework.annotations.*;

import java.util.List;
import java.util.Optional;

public class CourseController {

  private final CourseRepository courseRepository;

  public CourseController(CourseRepository courseRepository) {
    this.courseRepository = courseRepository;
  }

  @GetMapping("/courses")
  public List<Course> getCourses() {
    return courseRepository.findAll();
  }

  @GetMapping("/courses/{id}")
  public Optional<Course> getCourse(@PathVariable int id) {
    return Optional.ofNullable(courseRepository.findById(id));
  }

  @PostMapping("/courses")
  public Course createCourse(@RequestBody Course course) {
    return courseRepository.save(course);
  }

  @DeleteMapping("/courses/{id}")
  public Optional<Course> deleteCourse(@PathVariable int id) {
    return Optional.ofNullable(courseRepository.deleteById(id));
  }


}
