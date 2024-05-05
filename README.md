# SprungBoat - an experiment

This repository contains various stages of an experiment in creating a REST Controller framework somewhat like Spring (and Spring Boot).

**It is only intended as a learning tool, and should never be used as an actual framework for anything!!**

While it might look a bit like Spring Boot and the like, with @Annotations and beans and autowired dependencies, it makes absolutely no claim 
to know anything about how Spring is actually implemented. This is merely some experiments in trying to understand how one could give a
similar developer experience. Please accept them as such: experiments.

---

There are multiple branches - they are not supposed to ever be merged, but are intended to show the various stages that the project has gone through,
in moving from a single hardcoded HttpHandler, to several generic, auto-reflected, annotation-driven components.

*The master branch is unimportant - for now it contains the ```simplerest``` branch, it might change at any time.

All versions uses an entity-repository-controller architecture, first one only for Student, later ones also includes Course, Teacher, House and other entities.

The ```framework``` package contains the classes not intended to be part of the application, but stuff that is re-usable across
multiple applications. I.e. classes that would have to be copied into a new REST API with different entities and controllers.


## The branches

1. ```simplerest``` - A simple hardcoded server.

   This application only handles Student objects, thus all code is in the ```students``` package.
   It wraps the StudentController in a SimpleController, that handles all HTTP requests within a large if-else structure.
   The SimpleController also handles all JSON conversion, and only understands Student objects.
   
2. ```GenericRest``` - Something like Express ...

  This application adds Course objects as well as Student objects, and uses a ```GenericController``` that wraps a ControllerWrapper, that wraps the actual Controller.
  The ControllerWrapper implements the ```Controller``` interface, and registers routes that the GenericController should handle - but
  the ControllerWrapper also supplies individual handler methods, all implementing ```RequestHandler``` that receives a ```RequestObject``` 
  and a ```ResponseObject```. 
  All JSON conversion is done by each ControllerWrapper - each one hardcoded to its own entity-type, Student or Course.
  
3. ```JSONhandling``` - Dynamic JSON de+serializing-

   This application is the same when it comes to ControllerWrapper and handling of requests, but all JSON serialization and deserialization
   has been moved into its own classes. They use reflection (Bean Introspection) to analyse objects, and convert them to and from JSON.
   This means that the ControllerWrappers are somewhat smaller, only implementing handlers for routes.

4. ```Annotations1``` - annotations for route-mapping

   This version does away with the registerRoute code in the ControllerWrappers, and uses method @Annotations instead.
   The GenericController reads all the annotations, and wraps the request handler methods in the ControllerWrapper in
   freshly created lambdas, that then invoke the request handler, once again using reflection.

5. ```CompactController``` - no more ControllerWrapper

   This version does away with the ControllerWrapper and its RequestHandler methods. Now all request handlers are built
   dynamically as lambdas, and is uses reflection and annotation to build arguments and parse return values.
   A controller now looks more or less like a Spring controller - no special interfaces needed.
   The Generic Controller is still what does the actual work, but only the ProjectApplication needs to know.





