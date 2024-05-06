package dk.petlatkea.simplerest.framework.beans;

import dk.petlatkea.simplerest.framework.annotations.Component;
import dk.petlatkea.simplerest.framework.annotations.Controller;
import dk.petlatkea.simplerest.framework.annotations.Repository;

import java.io.*;
import java.net.URL;
import java.util.*;

public class BeanScanner {
  Class applicationRoot;

  // a list of all beans found - be able to get all types, all classes, any class, all classesOfType

  // key is type: one of the annotation-classes
  // value is a list of classes that have that annotation
  Map<Class,List<Class>> beanClasses = new HashMap<>();

  public BeanScanner(Class applicationClass) {
      this.applicationRoot = applicationClass;
      scanForBeans();
  }

  /**
   * Returns a list of classes that have the given annotation
   * @param beantype the annotation class to look for
   * @return a list of classes that have the given annotation
   */
  public List<Class> getBeansOfType(Class beantype) {
    return beanClasses.get(beantype);
  }

  /**
   * returns all the bean-classes that have been found, with any annotation
   * @return a list of classes with 'some' annotation
   */
  public Set<Class> getAllBeans() {
    Set<Class> allBeans = new HashSet<>();
    for(List list : beanClasses.values()) {
      allBeans.addAll(list);
    }
    return allBeans;
  }

  // gets a list of ALL classes in the same package (and subpackages) as the class given
  // does not care about annotations, any .class file is accepted
  private List<Class<?>> getClassesInSamePackageAs(Class application) throws IOException {
    String packageName = application.getPackageName();
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    File rootFolder = null;
    // Find the root folder of the package by asking the classloader for the resources
    Enumeration<URL> resources = classLoader.getResources(packageName.replace(".", "/"));
    while (resources.hasMoreElements()) {
      URL resource = resources.nextElement();
      // TODO: handle multiple resources! - for now we just take the first one
      rootFolder = new File(resource.getFile());
    }

    return getClassesInFolder(rootFolder, packageName);
  }

  // gets a list of ALL classes in the given folder, expected to match the given packagename
  // e.g. if the folder is named "students" the packagename could be "dk.petlatkea.simplerest.students"
  // The package name is used to create Class objects from filenames - and is sent along with
  // recursive calls to the same method with sub-folders
  private List<Class<?>> getClassesInFolder(File folder, String packageName) {
    List<Class<?>> classes = new LinkedList<>();

    for (File file : folder.listFiles()) {
      // hardcoded to avoid the "framework" package - but that shouldn't really be in the same package as the application!
      if (file.isDirectory() && !"framework".equals(file.getName())) {
        classes.addAll( getClassesInFolder(file, packageName + "." + file.getName()) );
      } else {
        // check if the file is a class file
        if (file.getName().endsWith(".class")) {
          // load the class
          try {
            String className = file.getName().substring(0, file.getName().length() - 6); // remove .class from the filename
            classes.add(Class.forName(packageName + "." + className));
          } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
          }
        }
      }
    }

    return classes;
  }

  // scans the applicationRoot for beans ...
  // A bean is a class somewhere down the package-tree that has one of these annotations:
  // @Controller
  // @Repository
  // @Component
  //
  // Found beans are stored in the beanClasses structure
  private void scanForBeans() {
    try {
      // find all classes in the applicationRoot package
      List<Class<?>> classes = getClassesInSamePackageAs(applicationRoot);

      // check if the classes have the @Controller, @Repository og @Component annotation
      for (Class<?> c : classes) {

        checkForAnnotation(c, Controller.class);
        checkForAnnotation(c, Repository.class);
        checkForAnnotation(c, Component.class);

      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // helper method for scanForBeans, to check if a class is annotated with a given annotation class
  // if it is, that class is added to the beanClasses structure.
  private void checkForAnnotation(Class<?> c, Class beanType) {
    if (c.isAnnotationPresent(beanType)) {
      // get list of controllers from the map
      List<Class> beansOfThisType = beanClasses.get(beanType);
      // if the list is null, create a new list
      if (beansOfThisType == null) {
        beansOfThisType = new ArrayList<>();
        beanClasses.put(beanType, beansOfThisType);
      }
      // add this class to the list
      beansOfThisType.add(c);
    }
  }

}
