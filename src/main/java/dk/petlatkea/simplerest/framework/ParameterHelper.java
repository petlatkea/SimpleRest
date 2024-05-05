package dk.petlatkea.simplerest.framework;

public class ParameterHelper {
  public enum Source {
    PATH_VARIABLE,
    REQUEST_BODY
  }

  public int index;
  public Class type;
  public String name;
  public Source source;

  public ParameterHelper() {}

  public ParameterHelper(int index, Class type, String name, Source source) {
    this.index = index;
    this.type = type;
    this.name = name;
    this.source = source;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public Class getType() {
    return type;
  }

  public void setType(Class type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
  }
}
