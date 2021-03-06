package com.globalhitss.claropay.cercademi.job.storelocation.model;


/**
 * This class implements the transformation process to Commerce
 * structure from Sears structure (bind cat_sears table in origin 
 * to commerce_list table in destination database).
 *
 * @author  Ricardo Bermúdez Bermudez
 * @version 1.0.0, Oct 22th, 2019.
 * @see     StoreLocation
 * @see     StoreLocationNoCoords
 */
public class StoreLocationSears extends StoreLocationNoCoords
{
  private String no_tienda;
  private String direccion;
  private int brand;
  
  /**
   * Constructor - Initializes the Sanborns object data. Moreover, it starts the
   * process to retrieve the coordinates.
   *
   * @param no_sucursal    Before ID.
   * @param calle_y_numero Location address.
   */
  public StoreLocationSears(
    String no_tienda,
    String direccion,
    int brand
  ) {
    this.no_tienda = no_tienda;
    this.direccion = direccion;
    this.brand = brand;
    loadCoords();
  }


  /**
   * {@inheritDoc}
   */
  public int getBrandId() { return this.brand; }

  /**
   * {@inheritDoc}
   */
  public String getPastId()    { return no_tienda; }

  /**
   * {@inheritDoc}
   */
  public String getAddress()   { return direccion; }
}