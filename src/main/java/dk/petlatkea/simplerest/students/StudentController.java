package dk.petlatkea.simplerest.students;

import dk.petlatkea.simplerest.framework.annotations.*;

import java.util.List;
import java.util.Optional;

public class StudentController {

  private final StudentRepository studentRepository;

  public StudentController(StudentRepository studentRepository) {
    this.studentRepository = studentRepository;
  }

  @GetMapping("/students")
  public List<Student> getStudents() {
    return studentRepository.findAll();
  }

  @GetMapping("/students/{id}")
  public Optional<Student> getStudent(@PathVariable int id) {
    return Optional.ofNullable(studentRepository.findById(id));
  }

  @PostMapping("/students")
  public Student createStudent(@RequestBody Student student) {
    return studentRepository.save(student);
  }

  @DeleteMapping("/students/{id}")
  public Optional<Student> deleteStudent(@PathVariable int id) {
    return Optional.ofNullable(studentRepository.deleteById(id));
  }



}
