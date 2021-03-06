package com.globalhitss.claropay.cercademi.job.storelocation.database;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;


/**
 * This class represents an DBMS connection and includes some method utilities
 * to SQL query performance.
 *
 * @author  Ricardo Bermúdez Bermúdez 
 * @version 1.0.0, Oct 21th, 2019.
 */
public abstract class ConnectionAbstract
{
  Connection    currentConnection = null;
  PreparedStatement lastStatement = null;

  String timeManage     = "useUnicode=true"
                        + "&useJDBCCompliantTimezoneShift=true"
                        + "&useLegacyDatetimeCode=false"
                        + "&serverTimezone=UTC";
  
  String databaseUrl    = "";
  String databaseUser   = "";
  String databasePass   = "";
  String databaseDriver = "";
  
  /**
   * Constructor, initializes the settings to establish a database connection.
   * 
   * @param  databaseDriver ODBC class reference.
   * @param  databaseUrl    URL of database server.
   * @param  databaseUser   User credential.
   * @param  databaseUser   Password credential.
   * @return void
   */
  public ConnectionAbstract(
    String databaseDriver,
    String databaseUrl,
    String databaseUser,
    String databasePass
  ) {
    this.databaseDriver = databaseDriver;
    this.databaseUrl    = databaseUrl;
    this.databaseUser   = databaseUser;
    this.databasePass   = databasePass;
  }

  /**
   * It starts a connection with database server according to established
   * settings by the constructor method.
   * 
   * @return void
   * @throws Exception When class driver not found or occur connection errors.
   */
  public void startConnection()
    throws Exception, SQLException
  {
    Class.forName(databaseDriver);
    
    currentConnection = DriverManager.getConnection(
      databaseUrl + "?" + timeManage,
      databaseUser,
      databasePass
    );
  }

  /**
   * This method closes the connection opened at last time by
   * {@link #startConnection()} method. On the other hand, close the lastest
   * query statement object opened by an "SQL query" method, such as
   * {@link get(String, String)} method.
   *
   * @return void
   * @throws Exception When occur an error to close connection or close
             statement.
   */
  public void closeConnection()
    throws Exception, SQLException
  {
    if (currentConnection!=null) currentConnection.close();
    if (lastStatement!=null) lastStatement.close();
  }

  public ResultSet get(
      String fields, 
      String table, 
      String where
  ) 
    throws SQLException
  {
    
    System.out.println(        "select " + fields + " from " + table +
        (where!=null ? " where " + where : ""));
      lastStatement = currentConnection.prepareStatement(
        "select " + fields + " from " + table +
        (where!=null ? " where " + where : "")
      );
      
      return lastStatement.executeQuery();
  }
  
  /**
   * This method build a SQL select sentence with the next form:
   * <code>select <fields> from <table></code>. It runs the query and returns 
   * results as ResultSet object.
   * 
   * @param  fields Comma separated fields.
   * @param  table  Table reference.
   * @param  updatedFieldRef Reference of timestamp of update action.
   * @return Results of query performance.
   * @throws SQLException When query fall.
   */
  public ResultSet getUpdatedRows(
    String fields, 
    String table, 
    String updatedFieldRef
  ) 
    throws SQLException
  {
    lastStatement = currentConnection.prepareStatement(
      "select " + fields + " from " + table +
      " where " + updatedFieldRef + " between CURRENT_DATE and CURRENT_TIMESTAMP"
    );
    
    return lastStatement.executeQuery();
  }

  /**
   * This method build a SQL insert sentence with the next form:
   * <code>insert into <table>(<fields>) values (<values>)</code>. 
   * It don't trigger a Exception with fall reasons. In case of errors.
   * 
   * @param fields fields of insert statement.
   * @param values values of fields.
   * @param table  table reference.
   * @return void
   */
  public void tryInsert(String fields, String values, String table)
  {
    try {
      lastStatement = currentConnection.prepareStatement(
        "insert into " + table + "(" + fields + ") values (" + values + ")"
      );
      lastStatement.execute();
      lastStatement.close();
    }
    catch(SQLIntegrityConstraintViolationException e) {}
    catch(java.sql.SQLException e1) {}
    
    lastStatement = null;
  }
  
  /***/
  public void tryUpdate(
    String fields,
    String values,
    String table,
    String condition,
    boolean ifnotexistInsert
  ) {
    try {
      lastStatement = currentConnection.prepareStatement(
        "select count(*) as total from " + table + " where " + condition
      );
      ResultSet selectRes = lastStatement.executeQuery();
      selectRes.next();
      int total = selectRes.getInt("total"); 
      
      if (total >  1) { throw new Exception("Mas de un registro."); }
      if (total == 1) { tryUpdate(fields, values, table, condition); }
      if (total == 0 && ifnotexistInsert) { tryInsert(fields, values, table); }
    }
    catch(Exception e2) { e2.printStackTrace(); }
  }
  
  /***/
  public void tryUpdate(
    String fields,
    String values,
    String table,
    String condition
  )  {
    String[] fieldArray   = fields.split(",");
    String[] valuesArray  = values.split(",");
    String   updateFormat = "";
    
    int i=0;
    for(i=0; i<fieldArray.length - 1; i++) {
      updateFormat=fieldArray[i] + "=" + valuesArray[i] + ",";
    }
    updateFormat = fieldArray[i] + "=" + valuesArray[i];
    
    try {
      lastStatement = currentConnection.prepareStatement(
          "update " + table
        + " set "   + updateFormat
        + " where " + condition
      );
      
      lastStatement.execute();
      lastStatement.close();
    } catch(Exception e) {}
  }
}