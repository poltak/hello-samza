package samza.examples.databasereader.util;

import java.io.Serializable;
import java.util.Properties;

public class DatabaseConnectionMetaData implements Serializable
{
  final private String databaseUrl;
  final private String databaseDriver;
  final private Properties databaseProperties;

  public DatabaseConnectionMetaData(final String databaseUrl, final String databaseDriver,
                                    final Properties databaseProperties)
  {
    this.databaseUrl = databaseUrl;
    this.databaseDriver = databaseDriver;
    this.databaseProperties = databaseProperties;
  }

  public Properties getDatabaseProperties()
  {
    return databaseProperties;
  }

  public String getDatabaseDriver()
  {
    return databaseDriver;
  }

  public String getDatabaseUrl()
  {
    return databaseUrl;
  }

  public String toString()
  {
    return "URL: " + databaseUrl + "\ndriver: " + databaseDriver + "\nuser: " + databaseProperties.getProperty("user") +
        "\npassword: " + databaseProperties.getProperty("password");
  }
}
