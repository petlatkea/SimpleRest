package dk.petlatkea.simplerest.students;

import java.time.LocalDate;

public class Student {

  private Integer id;
  private String name;
  private String email;
  private LocalDate birthday;

  public Student() {}

  public Student(String name, String email, LocalDate birthday) {
    this.name = name;
    this.email = email;
    this.birthday = birthday;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public LocalDate getBirthday() {
    return birthday;
  }

  public void setBirthday(LocalDate birthday) {
    this.birthday = birthday;
  }
}
