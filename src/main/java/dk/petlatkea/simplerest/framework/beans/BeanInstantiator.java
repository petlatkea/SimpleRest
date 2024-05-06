package dk.petlatkea.simplerest.framework.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * The BeanInstantiator instantiates beans! Hence the name ...
 * It reads the lists of beans found by the BeanScanner, tries to instantiate
 * (i.e. call new) on each one and stores them in a cached list.
 *
 * Anytime anyone needs a bean, they get it from the BeanInstantiator, to
 * ensure that only one instance of every bean is created at all times.
 */
public class BeanInstantiator {

  private BeanScanner beanScanner;
  private Map<Class, Object> beanInstances = new HashMap<>();

  public BeanInstantiator(Class applicationClass) {
    // create BeanScanner, and scan the application
    beanScanner = new BeanScanner(applicationClass);

    // get all the classes found, and instantiate each one
    for(Class beanClass : beanScanner.getAllBeans()) {
      try {
        instantiateBean(beanClass);
      } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }

  }

  public Object getBean(Class beanClass) {
    return beanInstances.get(beanClass);
  }

  public List<Object> getBeansOfType(Class beantype) {
    List<Class> classes = beanScanner.getBeansOfType(beantype);
    List<Object> instances = new ArrayList<>();
    for(Class clazz : classes) {
      instances.add(beanInstances.get(clazz));
    }
    return instances;
  }

  private Object instantiateBean(Class beanClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
    // check if there is an instance of this class
    Object instance = beanInstances.get(beanClass);
    if(instance == null) {
      //System.out.println("Create new instance of " + beanClass);
      // if there isn't - try to create one!
      Constructor[] constructors = beanClass.getDeclaredConstructors();
      if(constructors.length>1) {
        System.err.println("!!!! Multiple possible constructors !!!!");
      }
      for(Constructor constructor: constructors) {
        Parameter[] parameters = constructor.getParameters();

        // if no parameters - create a new instance immediately
        if(parameters.length == 0) {
          instance = constructor.newInstance();
          beanInstances.put(beanClass, instance);
        //  System.out.println("Instantiated bean with no parameters");
        } else {
        //  System.out.println("Expected parameters: " + Arrays.toString(parameters));

          // create an array for all the arguments necessary for the parameters
          Object[] arguments = new Object[parameters.length];
          // go through each parameter
          for(int i=0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            // find an instance of the type required
            arguments[i] = instantiateBean(parameter.getType());
          }
          // and create an instance with the arguments
          instance = constructor.newInstance(arguments);
          beanInstances.put(beanClass, instance);
        //  System.out.println("Instantiated bean with injected beans!");
        }
      }
    //  System.out.println("==================");
    }

    return instance;
  }

}
