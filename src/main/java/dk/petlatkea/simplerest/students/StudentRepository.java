package dk.petlatkea.simplerest.students;

import java.util.*;

public class StudentRepository {

  private final HashMap<Integer, Student> database = new HashMap<>();

  private Integer getNextId() {
    // find max integer id in the database, and return it + 1
    return database.keySet().stream().max(Integer::compare).orElse(0) + 1;
  }

  public Student save(Student student) {
    // Save student to database - give it a unique ID, and return it
    if(student.getId() == null) {
      student.setId(getNextId());
    }
    database.put(student.getId(), student);
    return student;
  }

  public Student findById(int id) {
    // Find student in database by ID, and return it
    return database.get(id);
  }

  public List<Student> findAll() {
    // Find all students in database, and return them
    return new ArrayList<>(database.values());
  }

  public boolean existsById(int id) {
    // Check if student with ID exists in database, and return true or false
    return database.containsKey(id);
  }

  public Student deleteById(int id) {
    // Find student in database by ID, delete it, and return it
    return database.remove(id);
  }
}
