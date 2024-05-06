package dk.petlatkea.simplerest.framework.scanner;

import dk.petlatkea.simplerest.framework.annotations.Controller;

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

  public Set<Class> getAllBeans() {
    Set<Class> allBeans = new HashSet<>();
    for(List list : beanClasses.values()) {
      allBeans.addAll(list);
    }
    return allBeans;
  }

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

  private void scanForBeans() {
    try {
      // find all classes in the applicationRoot package
      List<Class<?>> classes = getClassesInSamePackageAs(applicationRoot);

      // check if the classes have the @Controller annotation
      for (Class<?> c : classes) {

        checkForAnnotation(c, Controller.class);


      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

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
