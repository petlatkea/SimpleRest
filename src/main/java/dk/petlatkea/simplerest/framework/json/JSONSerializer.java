package dk.petlatkea.simplerest.framework.json;

import dk.petlatkea.simplerest.students.Student;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

/**
 * The JSONSerializer creates JSON from objects.
 *
 *
 */
public class JSONSerializer {

  public String convertObjectToJson(Object object) {
    StringBuilder json = new StringBuilder("{");

    try {
      BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass(), Object.class);

      PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
      for(int i = 0; i < descriptors.length; i++) {
        PropertyDescriptor descriptor = descriptors[i];
        String key = descriptor.getName();
        Method getter = descriptor.getReadMethod();
        Object value = getter.invoke(object);

        json.append(getJSONValue(key, value));
        if(i < descriptors.length - 1) {
          json.append(", ");
        }
      }

      json.append("}");

    } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    return json.toString();
  }

  private String getJSONValue(String key, Object value) {
    StringBuilder jsonLine = new StringBuilder();
    // add the property-name
    jsonLine.append("\"").append(key).append("\": ");

    // add the value - depending on the type
    if(value == null) {
      jsonLine.append("null");
    } else if(value instanceof String) {
      jsonLine.append("\"").append(value).append("\"");
    } else if(value instanceof Integer) {
      jsonLine.append(value);
    } else {
      jsonLine.append("\"").append(value.toString()).append("\"");
    }
    return jsonLine.toString();
  }

}
