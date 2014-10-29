package com.jcm.util;

import java.util.Arrays;
import java.util.List;

public class VmTool {

  public VmTool(){}
  
  public <T> List<T> asList(T... a){
    return Arrays.asList(a);
  }
}
