package dk.petlatkea.simplerest.courses;

public class Course {

  private Integer id;
  private String name;
  private String abbreviation;
  private String teacher;
  private Integer schoolYear;

  public Course() {}

  public Course(String name, String abbreviation, String teacher, Integer schoolYear) {
    this.name = name;
    this.abbreviation = abbreviation;
    this.teacher = teacher;
    this.schoolYear = schoolYear;
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

  public String getAbbreviation() {
    return abbreviation;
  }

  public void setAbbreviation(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  public String getTeacher() {
    return teacher;
  }

  public void setTeacher(String teacher) {
    this.teacher = teacher;
  }

  public Integer getSchoolYear() {
    return schoolYear;
  }

  public void setSchoolYear(Integer schoolYear) {
    this.schoolYear = schoolYear;
  }
}
