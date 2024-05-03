package dk.petlatkea.simplerest.students;

import java.util.List;
import java.util.Optional;

public class StudentController {

  private final StudentRepository studentRepository;

  public StudentController(StudentRepository studentRepository) {
    this.studentRepository = studentRepository;
  }

  public List<Student> getStudents() {
    return studentRepository.findAll();
  }

  public Optional<Student> getStudent(int id) {
    return Optional.ofNullable(studentRepository.findById(id));
  }

  public Student createStudent(Student student) {
    return studentRepository.save(student);
  }

  public Optional<Student> deleteStudent(int id) {
    return Optional.ofNullable(studentRepository.deleteById(id));
  }



}
