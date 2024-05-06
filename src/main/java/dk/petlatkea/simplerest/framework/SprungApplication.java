package dk.petlatkea.simplerest.framework;

import dk.petlatkea.simplerest.framework.annotations.Component;
import dk.petlatkea.simplerest.framework.annotations.Controller;
import dk.petlatkea.simplerest.framework.beans.BeanInstantiator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SprungApplication {
  public static void run(Class applicationClass, String[] args) {
    // instantiate all beans - with dependency injection!
    BeanInstantiator instantiator = new BeanInstantiator(applicationClass);

    try {
      // find any Components - and run them!
      for(Object component : instantiator.getBeansOfType(Component.class)) {
        // if the component has a run method
        Method runMethod = component.getClass().getMethod("run");
        if(runMethod != null) {
          // then call that run method - no interface needed!
          runMethod.invoke(component);
        }
      }

      // Create server - add Controllers (wrapped in GenericController)
      GenericServer genericServer = new GenericServer();
      // Ask instantiator for controllers
      for(Object controller : instantiator.getBeansOfType(Controller.class)) {
        genericServer.addController(new GenericController(controller));
      }

      // and start the server
      genericServer.start();
    } catch (IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }

  }
}
