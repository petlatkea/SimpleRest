package dk.petlatkea.simplerest.courses;

import dk.petlatkea.simplerest.courses.Course;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CourseRepository {

  private final HashMap<Integer, Course> database = new HashMap<>();

  private Integer getNextId() {
    // find max integer id in the database, and return it + 1
    return database.keySet().stream().max(Integer::compare).orElse(0) + 1;
  }

  public Course save(Course course) {
    // Save course to database - give it a unique ID, and return it
    if(course.getId() == null) {
      course.setId(getNextId());
    }
    database.put(course.getId(), course);
    return course;
  }

  public Course findById(int id) {
    // Find course in database by ID, and return it
    return database.get(id);
  }

  public List<Course> findAll() {
    // Find all courses in database, and return them
    return new ArrayList<>(database.values());
  }

  public boolean existsById(int id) {
    // Check if course with ID exists in database, and return true or false
    return database.containsKey(id);
  }

  public Course deleteById(int id) {
    // Find course in database by ID, delete it, and return it
    return database.remove(id);
  }
}
