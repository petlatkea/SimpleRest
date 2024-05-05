package dk.petlatkea.simplerest.students;

import dk.petlatkea.simplerest.framework.annotations.*;

import java.util.List;
import java.util.Optional;

/**
 * The StudentControllerWrapper is a wrapper around the StudentController that makes it an actual REST Controller.
 *
 * But as we now use @Annotations to register routes, and the controller doesn't need to handle request and response itself,
 * this class should be replaced by the actual controller in the next version
 */
public class StudentControllerWrapper {

  private final StudentController studentController;

  public StudentControllerWrapper(StudentController studentController) {
    this.studentController = studentController;
  }

  // Request handlers - wraps the StudentController methods
  // - can be replaced by the StudentController itself in the next version!

  @GetMapping("/students")
  public List<Student> getStudents() {
    return studentController.getStudents();
  }

  @GetMapping("/students/{id}")
  public Optional<Student> getStudent(@PathVariable int id) {
    return studentController.getStudent(id);
  }

  @PostMapping("/students")
  public Student createStudent(@RequestBody Student student) {
    return studentController.createStudent(student);
  }

  @DeleteMapping("/students/{id}")
  public Optional<Student> deleteStudent(@PathVariable int id) {
    return studentController.deleteStudent(id);
  }

}