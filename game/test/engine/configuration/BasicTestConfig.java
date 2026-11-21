package engine.configuration;

import engine.configuration.values.ConfigIntValue;

public class BasicTestConfig {
  // Basic configuration keys
  public static final ConfigKey<Integer> TEST_VALUE_SIMPLE_0 =
    new ConfigKey<>(new String[]{"test00"}, new ConfigIntValue(50));

  public static final ConfigKey<Integer> TEST_VALUE_SIMPLE_1 =
    new ConfigKey<>(new String[]{"test01"}, new ConfigIntValue(10));

  // Nested configuration key
  public static final ConfigKey<Integer> TEST_VALUE_NESTED =
    new ConfigKey<>(new String[]{"test02", "test10"}, new ConfigIntValue(75));

  //resets for test isolation
  static void reset() {
    TEST_VALUE_SIMPLE_0.value(50);
    TEST_VALUE_SIMPLE_1.value(10);
    TEST_VALUE_NESTED.value(75);
  }
}
