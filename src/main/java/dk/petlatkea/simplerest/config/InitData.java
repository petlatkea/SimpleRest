package dk.petlatkea.simplerest.config;

import dk.petlatkea.simplerest.courses.Course;
import dk.petlatkea.simplerest.courses.CourseRepository;
import dk.petlatkea.simplerest.framework.annotations.Component;
import dk.petlatkea.simplerest.students.Student;
import dk.petlatkea.simplerest.students.StudentRepository;

import java.time.LocalDate;

@Component
public class InitData {

  private final StudentRepository studentRepository;
  private final CourseRepository courseRepository;

  public InitData(StudentRepository studentRepository, CourseRepository courseRepository) {
    this.studentRepository = studentRepository;
    this.courseRepository = courseRepository;
  }

  public void run() {
    studentRepository.save(new Student("Harry Potter", "hpotter@hogwarts.edu", LocalDate.of(1980, 7, 31)));
    studentRepository.save(new Student("Hermione Granger", "hgranger91@hogwarts.edu", LocalDate.of(1979, 9, 19)));
    studentRepository.save(new Student("Ron Weasley", "ron.weasley@hogwarts.edu", LocalDate.of(1980, 3, 1)));
    studentRepository.save(new Student("Neville Longbottom", "longbottom@hogwarts.edu", LocalDate.of(1980, 7, 30)));

    courseRepository.save(new Course("Defence Against the Dark Arts", "DADA", "Dolores Umbridge", 5));
    courseRepository.save(new Course("Potions", "POT", "Severus Snape", 5));
    courseRepository.save(new Course("Transfiguration", "TRANS", "Minerva McGonagall", 5));
  }



}
