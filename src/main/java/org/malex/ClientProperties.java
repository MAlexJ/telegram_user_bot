package org.malex;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public abstract class ClientProperties {

  protected static final Logger LOG = Logger.getGlobal();

  private static final Properties config = loadConfig();

  protected static final int API_ID = Integer.parseInt(config.getProperty("api_id"));
  protected static final String HASH_CODE = config.getProperty("api_hash");
  protected static final String PHONE_NUMBER = config.getProperty("phone_number");

  protected static Properties loadConfig() {
    Properties properties = new Properties();
    try (FileInputStream fileInputStream = new FileInputStream("config.properties")) {
      properties.load(fileInputStream);
    } catch (IOException e) {
      LOG.severe("Config.properties file load error, message - " + e.getMessage());
      System.exit(1);
    }
    return properties;
  }
}
