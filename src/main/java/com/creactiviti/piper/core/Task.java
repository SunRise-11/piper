package com.creactiviti.piper.core;

public interface Task extends Accessor {

  String getId ();
  
  String getHandler ();
  
  String getName ();
  
  String getNode ();
  
  String getReturns ();
  
  String getStatus ();
  
  Object getOutput ();
  
  <T> T getOutput (Class<T> aType);

}
