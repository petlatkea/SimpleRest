package dk.petlatkea.simplerest.courses;

import dk.petlatkea.simplerest.students.Student;
import dk.petlatkea.simplerest.students.StudentRepository;

import java.util.List;
import java.util.Optional;

public class CourseController {

  private final CourseRepository courseRepository;

  public CourseController(CourseRepository courseRepository) {
    this.courseRepository = courseRepository;
  }

  public List<Course> getCourses() {
    return courseRepository.findAll();
  }

  public Optional<Course> getCourse(int id) {
    return Optional.ofNullable(courseRepository.findById(id));
  }

  public Course createCourse(Course course) {
    return courseRepository.save(course);
  }

  public Optional<Course> deleteCourse(int id) {
    return Optional.ofNullable(courseRepository.deleteById(id));
  }


}
