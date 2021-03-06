package com.globalhitss.claropay.cercademi.job.storelocation.etl;

import java.util.LinkedList;

import com.globalhitss.claropay.cercademi.job.storelocation.model.StoreLocation;
import com.globalhitss.claropay.cercademi.job.storelocation.model.StoreLocationOXXO;

import java.sql.ResultSet;


/**
 * This custom ETL, implements the extract procedure to fetch all data from
 * cat_oxxo table in origin database.
 * 
 * @author  Ricardo Bermúdez Bermúdez
 * @version 1.0.0, Oct 23th, 2019.
 * @see     com.globalhitss.claropay.cercademi.job.storelocation.model.StoreLocation
 * @see     com.globalhitss.claropay.cercademi.job.storelocation.model.StoreLocationOXXO
 * @see     ETL
 * @see     ETLCommerce
 */
public class ETLCommerceOXXO extends ETLCommerce
{

  /**
   * {@inheritDoc}
   * 
   * It's the custom extract procedure implementation to OXXO objects.
   */
  @Override
  public LinkedList<StoreLocation> extract() 
    throws ETLExtractException
  { 
    LinkedList<StoreLocation> oxxoList = new LinkedList<StoreLocation>();
    
    try {
      source.startConnection();
      destination.startConnection();

      ResultSet  oxxoRows = source.getUpdatedRows(
        "concatenado, calle, numero, colonia, codigo, "
       +"ciudad, estado, "
       +"latitud, longitud",
        "cat_oxxo",
        "last_change_ts"
      );
      
      ResultSet brandToken = destination.get(
        "STORE_ID",
        "CAT_STORE",
        "STORE='OXXO'"
      );
      brandToken.next();
      
      while (oxxoRows.next()) {
        oxxoList.add( new StoreLocationOXXO(
          oxxoRows.getString("concatenado"),
          oxxoRows.getString("calle"),
          oxxoRows.getString("numero"),
          oxxoRows.getString("colonia"),
          oxxoRows.getString("codigo"),
          oxxoRows.getString("ciudad"),
          oxxoRows.getString("estado"),
          oxxoRows.getDouble("latitud"),
          oxxoRows.getDouble("longitud"),
          brandToken.getInt("STORE_ID")
        ) );
      }

      source.closeConnection();
      destination.closeConnection();
    }
    catch (Exception e0) { throw new ETLExtractException("", e0); }

    return oxxoList;
  }
}