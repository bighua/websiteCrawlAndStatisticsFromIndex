package com.jcm.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 字符集过滤器
 * @author zhaotengfei
 *
 */
public class EncodeFilter implements Filter {

  protected FilterConfig filterConfig = null;
  //默认为utf-8
  protected String encoding = "UTF-8";
  
  @Override
  public void destroy() {
    this.filterConfig = null;
    
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse rsp,
      FilterChain chain) throws IOException, ServletException {
   String _encoding = filterConfig.getInitParameter("encoding");
   //如果配置了就是用配置的编码
   if(_encoding != null) encoding = _encoding;
   req.setCharacterEncoding(encoding);
   rsp.setCharacterEncoding(encoding);
   chain.doFilter(req, rsp);
  }

  @Override
  public void init(FilterConfig arg0) throws ServletException {

    this.filterConfig = arg0;
  }


}
