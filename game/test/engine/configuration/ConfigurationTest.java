package engine.configuration;

import engine.utils.components.path.IPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** Tests for the {@link Configuration} class. */
public class ConfigurationTest {


  private Configuration configuration;
  private Method setValueInPath;
  private Method getValueFromPath;

  /** Create a basic Configuration instance
   *  and setup reflection for private methods. */
  @BeforeEach
  void setUp() throws Exception {
    Constructor<Configuration> constructor =
      Configuration.class.getDeclaredConstructor(
        Class[].class, IPath.class);

    constructor.setAccessible(true);

    configuration = constructor.newInstance(
      new Class<?>[0], null);

    // Access private setValueInPath method
    setValueInPath =
      Configuration.class.getDeclaredMethod(
        "setValueInPath",
        String[].class,
        Object.class);

    setValueInPath.setAccessible(true);

    // Access private getValueInPath method
    getValueFromPath =
      Configuration.class.getDeclaredMethod(
        "getValueFromPath",
        String[].class
      );

    getValueFromPath.setAccessible(true);
  }

  private void invokeSetValueInPath(
    String[] path, Object value) throws Exception {
    setValueInPath.invoke(configuration, path, value);
  }

  private Object invokeGetValueFromPath(String[] path)
    throws Exception {
    return getValueFromPath.invoke(configuration, (Object) path);
  }

  /** Test if object was created not if correctly */
  @Test
  void shouldInitializeConfiguration() {
    assertNotNull(configuration);
  }

  @Nested
  class SetValueInPath {
    @Test
    void shouldInitializeConfigRootWhenNull() throws Exception {
      String[] path = new String[]{"name"};
      invokeSetValueInPath(path,"Test");
      assertEquals("Test", invokeGetValueFromPath(path));
    }

    @Test
    void shouldKeepExistingConfigRoot() throws Exception {
      String[] firstPath = {"app", "name"};
      String[] secondPath = {"app", "version"};

      invokeSetValueInPath(firstPath, "MyApp");
      invokeSetValueInPath(secondPath, "1.0");

      assertEquals("MyApp", invokeGetValueFromPath(firstPath));
      assertEquals("1.0", invokeGetValueFromPath(secondPath));
    }
  }
}
