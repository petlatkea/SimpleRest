package dk.petlatkea.simplerest.framework.json;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;



public class JSONDeserializer {

  // Create a new object of the given class from the given json
  public static Object fromJSON(Class clazz, String json) {
    JSONDeserializer deserializer = new JSONDeserializer();
    return deserializer.getObjectFromJson(clazz, json);
  }

  private Object getObjectFromJson(Class clazz, String json) {

    try {
      // Create object of type clazz
      Object object = clazz.getDeclaredConstructor().newInstance();

      // Find the expected properties of that kind of object
      BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Object.class);
      PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

      for (PropertyDescriptor descriptor : descriptors) {
        // find the property-name
        String propertyName = descriptor.getName();
        // Check if the property is in the json - ignore it otherwise
        if (hasJsonProperty(json, propertyName)) {
          // find the value of the property in the json
          Method setter = descriptor.getWriteMethod();
          // Depending on the type of the property, we need to either different json values
          Class<?> type = descriptor.getPropertyType();

          // Although, if the value in the json is literally null - invoke the setter with null
          if(propertyIsNull(json,propertyName)) {
            setter.invoke(object, (Object) null);
          } else if (type == String.class) {
            setter.invoke(object, getJsonString(json, propertyName));
          } else if (type == int.class || type == Integer.class) {
            setter.invoke(object, getJsonInteger(json, propertyName));
          } else if (type == LocalDate.class) {
            // property is never null - we know that it exists!
            // But it can throw a DateTimeParseException ... That we ignore for now ...
            setter.invoke(object, LocalDate.parse(getJsonString(json,propertyName)));
          }

        }
      }

      // When all properties are set - return the object
      return object;
    } catch (IntrospectionException | IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
      System.err.println(e);
      throw new RuntimeException(e);
    }
  }

  private boolean hasJsonProperty(String json, String key) {
    int beginIndex = json.indexOf("\"" + key + "\":");
    return beginIndex != -1;
  }

  // If the property exists, but have a litteral value of null:
  private boolean propertyIsNull(String json, String key) {
    return "null".equals(getJsonValue(json,key));
  }

  private String getJsonString(String json, String key) {
    String value = getJsonValue(json, key);
    if(value == null) {
      return null;
    } else {
      return value.substring(1, value.length()-1);
    }
  }

  private Integer getJsonInteger(String json, String key) {
    String value = getJsonValue(json,key);
    if( value == null) {
      return null;
    } else {
      return Integer.parseInt(value);
    }
  }

  private String getJsonValue(String json, String key) {
    int beginIndex = json.indexOf("\""+key+"\":");
    if( beginIndex == -1) {
      return null;
    }
    beginIndex += 3 + key.length();
    int endIndex = json.indexOf(",", beginIndex);
    if(endIndex == -1) {
      endIndex = json.indexOf("}", beginIndex);
    }
    return json.substring(beginIndex, endIndex).trim();
  }

}