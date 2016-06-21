package com.creactiviti.piper.core;

public interface Task extends Accessor {

  String getJobId ();
  
  String getHandler ();
  
  String getName ();
  
  String getNode ();
  
  String getReturns ();
  
  TaskStatus getTaskStatus ();
  
}
