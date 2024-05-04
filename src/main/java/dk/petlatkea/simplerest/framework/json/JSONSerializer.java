package dk.petlatkea.simplerest.framework.json;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * The JSONSerializer creates JSON from objects.
 * Only use the static .toJSON method - all other functionality is internal
 */
public class JSONSerializer {

  public static String toJSON(Object object) {
    JSONSerializer serializer = new JSONSerializer();
    serializer.add(object);
    return serializer.builder.toString();
  }

  private final StringBuilder builder = new StringBuilder();

  private JSONSerializer() {
    // empty constructor - made private to avoid instantiation of JSONSerializer from outside
  }

  private void add(Object object) {
    // Figure out if object is a list or a single object
    if (object instanceof List<?>) {
      addListOfObjects((List<?>) object);
    } else {
      addSingleObject(object);
    }
  }

  private void addListOfObjects(List<?> list) {
    builder.append("[");
    for (int i = 0; i < list.size(); i++) {
      Object object = list.get(i);
      builder.append(toJSON(object));
      // make sure we don't add a comma after the last item!
      if (i < list.size() - 1) {
        builder.append(", ");
      }
    }
    builder.append("]");
  }

  private void addSingleObject(Object object) {
    builder.append("{");

    try {
      // Find all property descriptors of class
      BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass(), Object.class);
      PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
      for (int i = 0; i < descriptors.length; i++) {
        PropertyDescriptor descriptor = descriptors[i];
        // find the property-name and the getter to ... well ... get it!
        String key = descriptor.getName();
        Method getter = descriptor.getReadMethod();
        // get the value
        Object value = getter.invoke(object);
        // and append it
        addJSONValue(key, value);
        // make sure we don't add a comma after the last item
        if (i < descriptors.length - 1) {
          builder.append(", ");
        }
      }

    } catch (IntrospectionException | InvocationTargetException |
             IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    builder.append("}");
  }

  private void addJSONValue(String key, Object value) {
    // add the property-name
    builder.append("\"").append(key).append("\": ");

    // add the value - format differently depending on the type
    if (value == null) {
      builder.append("null");
    } else if (value instanceof String) {
      builder.append("\"").append(value).append("\"");
    } else if (value instanceof Integer) {
      builder.append(value);
    } else {
      // convert objects to string - and put them inside strings
      // the explicit .toString() call isn't necessary, as it happens by default
      // but I keep it around to remind myself that that's what happens.
      builder.append("\"").append(value.toString()).append("\"");
    }
  }

}