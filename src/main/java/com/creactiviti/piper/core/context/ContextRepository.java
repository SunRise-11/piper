/* 
 * Copyright (C) Creactiviti LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Arik Cohen <arik@creactiviti.com>, Mar 2017
 */
package com.creactiviti.piper.core.context;

import java.util.List;

public interface ContextRepository<T extends Context> {

  void push (String aJobId, T aContext);
  
  T pop (String aJobId);
  
  T peek (String aJobId);
  
  List<T> getStack (String aJobId);
  
  int stackSize (String aJobId);
  
}
