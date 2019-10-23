package com.globalhitss.claropay.cercademi_etl.ETL;

import com.globalhitss.claropay.cercademi_etl.Models.Commerce;

import java.util.LinkedList;
import java.util.NoSuchElementException;


/** */
public abstract class ETLCommerce extends ETL<Commerce>
{
  
  /** */
  @Override
  public void load(LinkedList<Commerce> commerceList)
    throws ETLLoadException
  {
    Commerce commerce = null;

    try {
      destination.startConnection();

      while ( (commerce = commerceList.removeFirst()) != null ) {
        destination.tryInsert(
          "species, class_name, address, lat, lng, pastId",
          "'" + commerce.getSpecies()    + "'," +
          " " + commerce.getClassName()  + "," +
          "'" + commerce.getAddress()    + "'," +
          "'" + commerce.getLatitude()   + "'," +
          "'" + commerce.getLongitude()  + "'," +
          "'" + commerce.getPastId()     + "'",
          "commerce_list"
        );
      }

      destination.closeConnection();
    }
    catch (NoSuchElementException e0) {}
    catch (Exception e1) { throw new ETLLoadException("", e1); }
  }
}