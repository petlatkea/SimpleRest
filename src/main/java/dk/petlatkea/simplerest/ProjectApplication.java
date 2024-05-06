package dk.petlatkea.simplerest;

import dk.petlatkea.simplerest.config.InitData;
import dk.petlatkea.simplerest.framework.GenericController;
import dk.petlatkea.simplerest.framework.GenericServer;
import dk.petlatkea.simplerest.framework.annotations.Controller;
import dk.petlatkea.simplerest.framework.beans.BeanInstantiator;

import java.io.IOException;

public class ProjectApplication {
  public static void main(String[] args) throws IOException {
    // instantiate all beans - with dependency injection!
    BeanInstantiator instantiator = new BeanInstantiator(ProjectApplication.class);

    // find the InitData and run that - no-one else knows that that is necessary
    InitData initData = (InitData) instantiator.getBean(InitData.class);
    initData.run();

    // Create server - add controllers - and start it
    GenericServer genericServer = new GenericServer();
    // Ask instantiator for controllers
    for(Object controller : instantiator.getBeansOfType(Controller.class)) {
      genericServer.addController(new GenericController(controller));
    }
    genericServer.start();
  }
}
