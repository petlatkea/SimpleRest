package dk.petlatkea.simplerest.config;

import dk.petlatkea.simplerest.students.Student;
import dk.petlatkea.simplerest.students.StudentRepository;

import java.time.LocalDate;

public class InitData {

  private final StudentRepository studentRepository;

  public InitData(StudentRepository studentRepository) {
    this.studentRepository = studentRepository;
  }

  public void run() {
    studentRepository.save(new Student("Harry Potter", "hpotter@hogwarts.edu", LocalDate.of(1980, 7, 31)));
    studentRepository.save(new Student("Hermione Granger", "hgranger91@hogwarts.edu", LocalDate.of(1979, 9, 19)));
    studentRepository.save(new Student("Ron Weasley", "ron.weasley@hogwarts.edu", LocalDate.of(1980, 3, 1)));
    studentRepository.save(new Student("Neville Longbottom", "longbottom@hogwarts.edu", LocalDate.of(1980, 7, 30)));
  }



}
