package com.globalhitss.claropay.cercademi.job.storelocation.etl;

import java.util.LinkedList;

import com.globalhitss.claropay.cercademi.job.storelocation.database.ConnectionAbstract;
import com.globalhitss.claropay.cercademi.job.storelocation.database.DestinationConnection;
import com.globalhitss.claropay.cercademi.job.storelocation.database.SourceConnection;

import java.lang.Runnable;

/**
 * Represents the errors in fetch data procedure (extract).
 *
 * @author Ricardo Bermúdez Bermúdez
 * @since  Oct 22th, 2019.
 */
class ETLExtractException extends Exception 
{
  /** *
	 */
	private static final long serialVersionUID = 1L;

  public ETLExtractException(String message, Exception cause)
  {
    super(message, cause);
  }
}
 
/**
 * Represents the errors in push data procedure (push or load process).
 *
 * @author Ricardo Bermúdez Bermúdez
 * @since  Oct 22th, 2019.
 */
class ETLLoadException extends Exception 
{
  /** *
	 */
	private static final long serialVersionUID = 1L;

  public ETLLoadException(String message, Exception cause)
  {
    super(message, cause);
  }
}

/**
 * This class is an abstract of the ETL process. The below class defines the ETL
 * executi  process and interfaces to perform this, in other words, it defines
 * the extract and load interfaces. The transformation process is defined by
 * <TransformInterface> that content the link between old model and new model
 * made by <i>link methods</i>.
 * 
 * @author  Ricardo Bermúdez Bermúdez
 * @version Oct 22th, 2019.
 */
public abstract class ETL<TransformInterface> implements Runnable
{
  public  ConnectionAbstract source      = null;
  public  ConnectionAbstract destination = null;
  private String etlClassName = "";
    
  /** 
   * Constructor - Initialize source, destination connection and set the etl
   * name procedure.
   */
  public ETL()
  {
    source       = new SourceConnection();
    destination  = new DestinationConnection();
    etlClassName = getClass().getSimpleName();
  }

  /** 
   * General process to run a ETL process.
   */
  @Override
  public void run()
  {
    System.out.println(etlClassName + ": START");

    try {
      LinkedList<TransformInterface> transformObjectList = extract();
      System.out.println(etlClassName + " - Extract completed!");
      load(transformObjectList);
      System.out.println(etlClassName + " - Charge completed!");
    } 
    catch (ETLExtractException e0) {
      System.out.println(etlClassName + " - Errors with data extraction.");
      e0.printStackTrace();
    }
    catch (ETLLoadException e2) {
      System.out.println(etlClassName + " - Errors with data push process.");
      e2.printStackTrace();
    }

    System.out.println(etlClassName + ": END");
  }
  
  /**
   * Retreive data from source connection or from origin (fetch procedure).
   * 
   * @throws ETLExtractException If this procedure fail.
   */
  abstract public LinkedList<TransformInterface> extract()
    throws ETLExtractException;

  /**
   * Load data over destination store (push procedure).
   * 
   * @throws ETLLoadException If this procedure fail.
   */
  abstract public void load(LinkedList<TransformInterface> transformObjectList)
    throws ETLLoadException;
}