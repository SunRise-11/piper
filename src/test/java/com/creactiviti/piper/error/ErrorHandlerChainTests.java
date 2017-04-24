package com.creactiviti.piper.error;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.creactiviti.piper.core.job.Job;
import com.creactiviti.piper.core.job.MutableJob;
import com.creactiviti.piper.core.job.MutableJobTask;
import com.creactiviti.piper.core.task.JobTask;

public class ErrorHandlerChainTests {

  @Test
  public void test1() {
    ErrorHandler errorHandler = new ErrorHandler<Job>() {
      public void handle(Job j) {
        Assert.assertEquals(MutableJob.class, j.getClass()); 
      }
    };
    ErrorHandlerChain chain = new ErrorHandlerChain(Arrays.asList(errorHandler));
    chain.handle(new MutableJob());
  }

  @Test
  public void test2() {
    ErrorHandler errorHandler1 = new ErrorHandler<Job>() {
      public void handle(Job j) {
        throw new IllegalStateException("should not get here");
      }
    };
    ErrorHandler errorHandler2 = new ErrorHandler<JobTask>() {
      public void handle(JobTask jt) {
        Assert.assertEquals(MutableJobTask.class, jt.getClass()); 
      }
    };
    ErrorHandlerChain chain = new ErrorHandlerChain(Arrays.asList(errorHandler1,errorHandler2));
    chain.handle(MutableJobTask.create());
  }
}
