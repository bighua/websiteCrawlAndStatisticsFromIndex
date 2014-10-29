package com.jcm.auto.sys.beans.analyze;


public class SiteModel extends ZBean {

  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = 1877691488278805683L;
  
  public SiteModel() {}
  
  public SiteModel(String name, String brand, String company, String model) {
    this.name = name;
    this.brand = brand;
    this.company = company;
    this.model = model;

    setId(String.format("%s__%s__%s__%s", brand, company, model, name));
  }

  private String name;
  
  private String brand;

  private String company;
  
  private String model;
  
  private String vehicle;
  
  private String brandUrl;

  private String companyUrl;
  
  private String modelUrl;
  
  private String vehicleUrl;

  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getBrand() {
    return brand;
  }
  
  public void setBrand(String brand) {
    this.brand = brand;
  }
  
  public String getCompany() {
    return company;
  }
  
  public void setCompany(String company) {
    this.company = company;
  }
  
  public String getModel() {
    return model;
  }
  
  public void setModel(String model) {
    this.model = model;
  }
  
  public String getVehicle() {
    return vehicle;
  }
  
  public void setVehicle(String vehicle) {
    this.vehicle = vehicle;
  }
  
  public String getBrandUrl() {
    return brandUrl;
  }
  
  public void setBrandUrl(String brandUrl) {
    this.brandUrl = brandUrl;
  }
  
  public String getCompanyUrl() {
    return companyUrl;
  }
  
  public void setCompanyUrl(String companyUrl) {
    this.companyUrl = companyUrl;
  }
  
  public String getModelUrl() {
    return modelUrl;
  }
  
  public void setModelUrl(String modelUrl) {
    this.modelUrl = modelUrl;
  }
  
  public String getVehicleUrl() {
    return vehicleUrl;
  }
  
  public void setVehicleUrl(String vehicleUrl) {
    this.vehicleUrl = vehicleUrl;
  }
  
}
