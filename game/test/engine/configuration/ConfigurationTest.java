package engine.configuration;

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

import static org.junit.jupiter.api.Assertions.*;

/** Tests for the {@link Configuration} class. */
public class ConfigurationTest {

  @TempDir
  Path tempDir;
  private IPath configIPath;
  private Path temporaryConfigFile;

  private Configuration loadTestConfiguration(Class<?>... configClasses)
    throws IOException {
    return Configuration.loadAndGetConfiguration(configIPath, configClasses);
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> readConfigurationJson(Path file)
    throws IOException {

    String content = Files.readString(file);

    return (Map<String, Object>) JsonHandler.readJson(content);
  }

  /** Create a basic Configuration instance */
  @BeforeEach
  void setUp() throws Exception {
    temporaryConfigFile = tempDir.resolve("test-config.json");
    configIPath = new SimpleIPath(temporaryConfigFile.toString());
  }

  @AfterEach
  void tearDown() {
    BasicTestConfig.reset();
  }

  @Nested
  class InitialisationTests {
    @Test
    void test_initialisation_should_not_be_null() throws IOException {
      Configuration configuration = loadTestConfiguration(BasicTestConfig.class);
      assertNotNull(configuration);
    }

    /**
     * should keep field existing
     */
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

    /**
     * should overwrite json completely
     */
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

    /**
     * should not replace values with defaults
     */
    @Test
    void test_initialisation_with_complete_json() throws IOException {
      Files.writeString(temporaryConfigFile,
        "{" +
          "  \"test02\": {" +
          "    \"test10\": \"35\"" +
          "  }," +
          "  \"test01\": \"30\"," +
          "  \"test00\": \"25\"" +
          "}");

      Configuration config = loadTestConfiguration(BasicTestConfig.class);

      Map<String, Object> json = readConfigurationJson(temporaryConfigFile);

      assertNotNull(config);
      assertEquals("25", json.get("test00"));
      assertEquals("30", json.get("test01"));
      assertEquals("35", ((Map<String, Object>) json.get("test02")).get("test10"));
    }

    /**
     * should be replaced with defaults
     */
    @Test
    void test_initialisation_with_null_values() throws IOException {
      Files.writeString(temporaryConfigFile,
        "{" +
          "  \"test00\": null" +
          "}");

      Configuration config = loadTestConfiguration(BasicTestConfig.class);

      Map<String, Object> json = readConfigurationJson(temporaryConfigFile);

      assertNotNull(config);
      assertEquals("50", json.get("test00"));
    }
  }
}
