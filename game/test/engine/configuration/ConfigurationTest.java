package engine.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import engine.configuration.values.ConfigIntValue;
import engine.utils.JsonHandler;
import engine.utils.components.path.IPath;
import engine.utils.components.path.SimpleIPath;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests for the {@link Configuration} class. */
public class ConfigurationTest {

  @TempDir Path tempDir;
  private IPath configIPath;
  private Path temporaryConfigFile;

  private Configuration loadTestConfiguration(Class<?>... configClasses) throws IOException {
    return Configuration.loadAndGetConfiguration(configIPath, configClasses);
  }

  private Map<String, Object> readConfigurationJson(Path file) throws IOException {
    String content = Files.readString(file);
    return JsonHandler.readJson(content);
  }

  static class BasicTestConfig {
    // Basic configuration keys
    public static final ConfigKey<Integer> TEST_VALUE_SIMPLE_0 =
        new ConfigKey<>(new String[] {"test00"}, new ConfigIntValue(50));

    public static final ConfigKey<Integer> TEST_VALUE_SIMPLE_1 =
        new ConfigKey<>(new String[] {"test01"}, new ConfigIntValue(10));

    // Nested configuration key
    public static final ConfigKey<Integer> TEST_VALUE_NESTED =
        new ConfigKey<>(new String[] {"test02", "test10"}, new ConfigIntValue(75));

    // resets for test isolation
    static void reset() {
      TEST_VALUE_SIMPLE_0.value(50);
      TEST_VALUE_SIMPLE_1.value(10);
      TEST_VALUE_NESTED.value(75);
    }
  }

  @ConfigMap(path = {"first"})
  static class FirstMultipleMapTestConfig {
    static final ConfigKey<Integer> FIRST_KEY =
        new ConfigKey<>(new String[] {"value"}, new ConfigIntValue(10));

    static void reset() {
      FIRST_KEY.value(10);
    }
  }

  @ConfigMap(path = {"second"})
  static class SecondMultipleMapTestConfig {
    static final ConfigKey<Integer> SECOND_KEY =
        new ConfigKey<>(new String[] {"value"}, new ConfigIntValue(20));

    static void reset() {
      SECOND_KEY.value(20);
    }
  }

  /** Create a basic Configuration instance */
  @BeforeEach
  void setUp() {
    temporaryConfigFile = tempDir.resolve("test-config.json");
    configIPath = new SimpleIPath(temporaryConfigFile.toString());
  }

  @AfterEach
  void tearDown() {
    BasicTestConfig.reset();
    FirstMultipleMapTestConfig.reset();
    SecondMultipleMapTestConfig.reset();
  }

  @Nested
  class InitialisationTests {
    @Test
    void test_initialisation_should_not_be_null() throws IOException {
      Configuration configuration = loadTestConfiguration(BasicTestConfig.class);
      assertNotNull(configuration);
    }

    /** should keep field existing */
    @SuppressWarnings("unchecked")
    @Test
    void test_initialisation_with_valid_json() throws IOException {
      Files.writeString(temporaryConfigFile, "{\"existing\": \"10\"}");

      Configuration config = loadTestConfiguration(BasicTestConfig.class);

      Map<String, Object> json = readConfigurationJson(temporaryConfigFile);

      assertNotNull(config);
      assertEquals("10", json.get("existing"));
      assertEquals("50", json.get("test00"));
      assertEquals("10", json.get("test01"));
      assertEquals("75", ((Map<String, Object>) json.get("test02")).get("test10"));
    }

    /** should overwrite json completely */
    @SuppressWarnings("unchecked")
    @Test
    void test_initialisation_with_invalid_json() throws IOException {
      Files.writeString(temporaryConfigFile, "{\"existing\": \"10\"");

      Configuration config = loadTestConfiguration(BasicTestConfig.class);

      Map<String, Object> json = readConfigurationJson(temporaryConfigFile);

      assertNotNull(config);
      assertNull(json.get("existing"));
      assertEquals("50", json.get("test00"));
      assertEquals("10", json.get("test01"));
      assertEquals("75", ((Map<String, Object>) json.get("test02")).get("test10"));
    }

    /** should not replace values with defaults */
    @SuppressWarnings("unchecked")
    @Test
    void test_initialisation_with_complete_json() throws IOException {
      Files.writeString(
          temporaryConfigFile,
          "{"
              + "  \"test02\": {"
              + "    \"test10\": \"35\""
              + "  },"
              + "  \"test01\": \"30\","
              + "  \"test00\": \"25\""
              + "}");

      Configuration config = loadTestConfiguration(BasicTestConfig.class);

      Map<String, Object> json = readConfigurationJson(temporaryConfigFile);

      assertNotNull(config);
      assertEquals("25", json.get("test00"));
      assertEquals("30", json.get("test01"));
      assertEquals("35", ((Map<String, Object>) json.get("test02")).get("test10"));
    }

    /** should be replaced with defaults */
    @Test
    void test_initialisation_with_null_values() throws IOException {
      Files.writeString(temporaryConfigFile, "{" + "  \"test00\": null" + "}");

      Configuration config = loadTestConfiguration(BasicTestConfig.class);

      Map<String, Object> json = readConfigurationJson(temporaryConfigFile);

      assertNotNull(config);
      assertEquals("50", json.get("test00"));
    }
  }

  @Nested
  class ConfigMapTests {

    @SuppressWarnings("unchecked")
    @Test
    void test_config_map_applies_path_prefix() throws IOException {
      Configuration config = loadTestConfiguration(BasicTestConfig.class);

      Map<String, Object> json = readConfigurationJson(temporaryConfigFile);

      assertNotNull(config);
      assertEquals("75", ((Map<String, Object>) json.get("test02")).get("test10"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void test_config_map_shares_path_prefix() throws IOException {
      Files.writeString(temporaryConfigFile, "{ \"test02\": { \"test11\": \"50\"}}");
      Configuration config = loadTestConfiguration(BasicTestConfig.class);

      Map<String, Object> json = readConfigurationJson(temporaryConfigFile);

      assertNotNull(config);
      Map<String, Object> sharedMap = (Map<String, Object>) json.get("test02");
      assertEquals("75", sharedMap.get("test10"));
      assertEquals("50", sharedMap.get("test11"));
    }

    /** should abort and do nothing */
    @Test
    void test_config_map_empty_path() throws IOException {
      Configuration config = loadTestConfiguration(BasicTestConfig.class);

      config.update(new ConfigKey<>(new String[] {}, new ConfigIntValue(10)));

      Map<String, Object> json = readConfigurationJson(temporaryConfigFile);

      assertNotNull(config);
      assertNull(json.get(""));
    }

    /** should add empty string as key */
    @Test
    void test_config_map_empty_string_in_path() throws IOException {
      Configuration config = loadTestConfiguration(BasicTestConfig.class);

      config.update(new ConfigKey<>(new String[] {""}, new ConfigIntValue(10)));

      Map<String, Object> json = readConfigurationJson(temporaryConfigFile);

      assertNotNull(config);
      assertEquals("10", json.get(""));
    }

    @SuppressWarnings("unchecked")
    @Test
    void test_update_nested_key_preserves_sibling_keys() throws IOException {
      Files.writeString(temporaryConfigFile, "{\"test02\": {" + "\"test11\": \"50\"" + "}" + "}");

      Configuration config = loadTestConfiguration(BasicTestConfig.class);

      config.update(new ConfigKey<>(new String[] {"test02", "test10"}, new ConfigIntValue(100)));

      Map<String, Object> json = readConfigurationJson(temporaryConfigFile);

      assertEquals("100", ((Map<String, Object>) json.get("test02")).get("test10"));
      assertEquals("50", ((Map<String, Object>) json.get("test02")).get("test11"));
    }

    /** should add both with their annotated prefixes */
    @SuppressWarnings("unchecked")
    @Test
    void test_multiple_config_map_classes() throws IOException {
      Configuration config =
          loadTestConfiguration(
              FirstMultipleMapTestConfig.class, SecondMultipleMapTestConfig.class);

      Map<String, Object> json = readConfigurationJson(temporaryConfigFile);

      assertNotNull(config);

      assertEquals("10", ((Map<String, Object>) json.get("first")).get("value"));
      assertEquals("20", ((Map<String, Object>) json.get("second")).get("value"));
    }
  }

  @Nested
  class CacheTests {
    @Test
    void test_repeated_configuration_initialization_returns_cached_instance() throws IOException {
      Configuration firstConfig = loadTestConfiguration(BasicTestConfig.class);

      Configuration secondConfig = loadTestConfiguration(BasicTestConfig.class);

      assertNotNull(firstConfig);
      assertNotNull(secondConfig);
      assertSame(firstConfig, secondConfig);
    }
  }
}
