package dk.petlatkea.simplerest.framework.scanner;

import dk.petlatkea.simplerest.framework.annotations.Controller;

import java.io.*;
import java.net.URL;
import java.util.*;

public class BeanScanner {
  Class applicationRoot;

  // key is type: one of the annotation-classes
  // value is a list of classes that have that annotation
  Map<Class,List<Class>> beanClasses = new HashMap<>();

  public BeanScanner(Class applicationClass) {
      this.applicationRoot = applicationClass;
      scanForBeans();
  }

  /**
   * Returns a list of classes that have the given annotation
   * @param beanclass the annotation class to look for
   * @return a list of classes that have the given annotation
   */
  public List<Class> getBeanClasses(Class beanclass) {
    return beanClasses.get(beanclass);
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

        if (c.isAnnotationPresent(Controller.class)) {
          // get list of controllers from the map
          List<Class> controllers = beanClasses.get(Controller.class);
          // if the list is null, create a new list
          if (controllers == null) {
            controllers = new ArrayList<>();
            beanClasses.put(Controller.class, controllers);
          }
          // add this class to the list
          controllers.add(c);
        }

      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
