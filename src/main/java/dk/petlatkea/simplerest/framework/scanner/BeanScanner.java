package dk.petlatkea.simplerest.framework.scanner;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public class BeanScanner {
  Class applicationRoot;

  public BeanScanner(Class applicationClass) {
      this.applicationRoot = applicationClass;
      scanForBeans();
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

  public void scanForBeans() {
    try {
      // find all classes in the applicationRoot package
      List<Class<?>> classes = getClassesInSamePackageAs(applicationRoot);

      for (Class<?> c : classes) {
        System.out.println(c.getName());
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
/*
    Package[] packages = classLoader.getDefinedPackages();
    for (Package p : packages) {
      System.out.println(p.getName());
    }

      System.out.println("-------------------");

      InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("dk/petlatkea/simplerest");
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
      reader.lines().forEach(System.out::println);

//      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//      classLoader.
*/
    }
}
