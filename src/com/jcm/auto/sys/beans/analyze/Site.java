package com.jcm.auto.sys.beans.analyze;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;


public class Site implements Serializable {
  /**
   * serialVersionUID
   */
  private static final long serialVersionUID = -4130836997537041238L;
  /**
   * Describe name here.
   */
  private String name;
  /**
   * Describe siteHome here.
   */
  private String siteHome;
  /**
   * Describe parseSite here.
   */
  private String parseSite;
  /**
   * Describe host here.
   */
  private String host;
  /**
   * Describe port here.
   */
  private Integer port;
  /**
   * Describe ssl here.
   */
  private Boolean ssl;
  
  private String contentEncoding;
  
  private String domain;

  /**
   * Describe parser here.
   */
  private String parser;

  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  public String getSiteHome() {
    return siteHome;
  }
  
  public void setSiteHome(String siteHome) {
    this.siteHome = siteHome;
  }
  
  public String getParseSite() {
    return parseSite;
  }

  public void setParseSite(String parseSite) {
    this.parseSite = parseSite;
    parseURI(parseSite);
  }

  public String getHost() {
    return host;
  }
  
  public void setHost(String host) {
    this.host = host;
  }
  
  public Integer getPort() {
    return port;
  }
  
  public void setPort(Integer port) {
    this.port = port;
  }
  

  public String getParser() {
    return parser;
  }
  
  public void setParser(String parser) {
    this.parser = parser;
  }

  public Boolean isSsl() {
    return ssl;
  }

  public void setSsl(Boolean ssl) {
    this.ssl = ssl;
  }

  public String getContentEncoding() {
  return contentEncoding;
}

  public void setContentEncoding(String contentEncoding) {
    this.contentEncoding = contentEncoding;
  }
  
  public String getDomain() {
    return domain;
  }
  
  public void setDomain(String domain) {
    this.domain = domain;
  }
  
  private void parseURI(String parseSite) {
      URI uri = null;
      try {
      uri = new URI(parseSite);
      } catch (URISyntaxException e) {
        e.printStackTrace();
        host = null;
        port = null;
        ssl = null;
        return;
      }
      String scheme = uri.getScheme() == null? "http" : uri.getScheme();
      host = uri.getHost() == null? "localhost" : uri.getHost();
      port = uri.getPort();
      if (port == -1) {
          if ("http".equalsIgnoreCase(scheme)) {
              port = 80;
          } else if ("https".equalsIgnoreCase(scheme)) {
              port = 443;
          }
      }
    
      if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
          System.err.println("Only HTTP(S) is supported.");
          return;
      }
    
      ssl = "https".equalsIgnoreCase(scheme);
    }
  }
