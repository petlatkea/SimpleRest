package dk.petlatkea.simplerest.framework;

public interface Controller {
  String getBasePath();
  void registerRoutes(GenericController genericController);
}
