/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package samza.examples.databasereader.system;

import org.apache.samza.Partition;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.util.BlockingEnvelopeMap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseReaderConsumer extends BlockingEnvelopeMap
{
  private final Connection            databaseConnection;
  private final Statement             statement;
  private final SystemStreamPartition systemStreamPartition;

  /**
   * Sets up the SystemConsumer for reading from the specified database.
   *
   * @param systemName Name of this system.
   * @param outputStreamName Name of the output stream upon which the database connection statement will be placed.
   * @param parameters User defined system parameters for database connection.
   */
  public DatabaseReaderConsumer(final String systemName, final String outputStreamName,
                                final DatabaseReaderParameters parameters)
      throws SQLException, ClassNotFoundException
  {
    this.systemStreamPartition = new SystemStreamPartition(systemName, outputStreamName, new Partition(0));

    // Formulate database URL from parameters
    final String databaseUrl =
        "jdbc:" + parameters.getDbmsType().toString() +
        "://" + parameters.getHost() +
        ":" + parameters.getPort() +
        "/" + parameters.getDatabaseName();

    Class.forName(parameters.getDbmsType().getDriver());

    // Handle username and password parameters
    final Properties properties = new Properties();
    properties.put("user", parameters.getUsername());
    properties.put("password", parameters.getPassword());

    // Make database connection and get statement
    this.databaseConnection = DriverManager.getConnection(databaseUrl, properties);
    this.statement = databaseConnection.createStatement();
  }

  @Override
  public void register(final SystemStreamPartition systemStreamPartition, final String startingOffset)
  {
    super.register(systemStreamPartition, startingOffset);
  }

  @Override
  public void start()
  {
    try
    {
      put(systemStreamPartition, new IncomingMessageEnvelope(systemStreamPartition, null, null, statement));
    } catch (InterruptedException e)
    {
      e.printStackTrace();
      stop();
    }
  }

  @Override
  public void stop()
  {
    try
    {
      databaseConnection.close();
    } catch (SQLException e)
    {
      e.printStackTrace();
    }
  }
}
