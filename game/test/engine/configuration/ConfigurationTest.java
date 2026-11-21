package engine.configuration;

import engine.utils.components.path.IPath;
import engine.utils.components.path.SimpleIPath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for the {@link Configuration} class. */
public class ConfigurationTest {

  @TempDir
  Path tempDir;
  private Configuration configuration;
  private IPath configPath;
  private Path temporaryConfigFile;

  private Configuration loadTestConfiguration(Class<?>... configClasses)
    throws IOException {
    return Configuration.loadAndGetConfiguration(configPath, configClasses);
  }

  /** Create a basic Configuration instance */
  @BeforeEach
  void setUp() throws Exception {
    temporaryConfigFile = tempDir.resolve("test-config.json");

    Files.createFile(temporaryConfigFile);

    configPath = new SimpleIPath(temporaryConfigFile.toString());
  }

  @Test
  void test_initialisation_should_not_be_null() throws IOException {
    configuration = loadTestConfiguration(BasicTestConfig.class);

    assertNotNull(configuration);
  }
}
