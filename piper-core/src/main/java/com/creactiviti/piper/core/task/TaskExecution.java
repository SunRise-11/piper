/* 
 * Copyright (C) Creactiviti LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Arik Cohen <arik@creactiviti.com>, Mar 2017
 */
package com.creactiviti.piper.core.task;

import java.util.Date;

import com.creactiviti.piper.core.error.Errorable;
import com.creactiviti.piper.core.error.Prioritizable;
import com.creactiviti.piper.core.error.Retryable;

/**
 * <p>Extends the {@link PipelineTask} interface to add execution semantics to 
 * the task.</p> 
 * 
 * <p>{@link TaskExecution} instances capture the life cycle of a single 
 * execution of a task. By single execution is meant that the task
 * goes through the following states:</p> 
 * 
 * <ol>
 *   <li><code>CREATED</code></li>
 *   <li><code>STARTED</code></li>
 *   <li><code>COMPLETED</code> or <code>FAILED</code> or <code>CANCELLED</code></li>
 * </ol>
 * 
 * @author Arik Cohen
 * @since May 8, 2017
 */
public interface TaskExecution extends PipelineTask, Errorable, Retryable, Prioritizable {

  /**
   * Get the unique id of the task instance.
   * 
   * @return String the id
   */
  String getId ();
  
  /**
   * Get the id of the parent task, if this
   * is a sub-task.
   * 
   * @return String the id of the parent task.
   */
  String getParentId ();
  
  /**
   * Get the id of the job for which this task 
   * belongs to.
   * 
   * @return String the id of the job
   */
  String getJobId ();
  
  /**
   * Get the current status of this task.
   * 
   * @return The status of the task.
   */
  TaskStatus getStatus ();
  
  /**
   * Get the result output generated by the task
   * handler which executed this task.
   * 
   * @return Object the output of the task
   */
  Object getOutput ();
  
  /**
   * Get the time when this task instance 
   * was created.
   * 
   * @return Date
   */
  Date getCreateTime ();

  /**
   * Get the time when this task instance 
   * was started.
   * 
   * @return Date
   */
  Date getStartTime ();
  
  /**
   * Return the time when this task instance 
   * ended (CANCELLED, FAILED, COMPLETED)
   * 
   * @return Date
   */
  Date getEndTime ();
  
  /**
   * Returns the total time in ms for this task
   * to execute (excluding wait time of the task
   * in transit). i.e. actual execution time on 
   * a worker node.
   * 
   * @return long
   */
  long getExecutionTime ();
  
}
