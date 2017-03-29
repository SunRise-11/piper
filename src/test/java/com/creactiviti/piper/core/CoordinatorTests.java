package com.creactiviti.piper.core;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.creactiviti.piper.core.context.SimpleContextRepository;
import com.creactiviti.piper.core.job.Job;
import com.creactiviti.piper.core.job.JobStatus;
import com.creactiviti.piper.core.job.SimpleJobRepository;
import com.creactiviti.piper.core.messenger.SimpleMessenger;
import com.creactiviti.piper.core.pipeline.FileSystemPipelineRepository;
import com.creactiviti.piper.core.task.DefaultTaskExecutor;
import com.creactiviti.piper.core.task.JobTask;
import com.creactiviti.piper.taskhandler.io.Print;
import com.creactiviti.piper.taskhandler.time.Sleep;

public class CoordinatorTests {

  @Test
  public void testStartJob () {
    
    Worker worker = new Worker();
    Coordinator coordinator = new Coordinator ();
   
    SimpleMessenger workerMessenger = new SimpleMessenger();
    workerMessenger.receive("completions", (o)->coordinator.complete((JobTask)o));
    worker.setMessenger(workerMessenger);
    DefaultTaskHandlerResolver taskHandlerResolver = new DefaultTaskHandlerResolver();
    
    Map<String,TaskHandler<?>> handlers = new HashMap<>();
    handlers.put("print", new Print());
    handlers.put("sleep", new Sleep());
    
    taskHandlerResolver.setTaskHandlers(handlers);
    
    worker.setTaskHandlerResolver(taskHandlerResolver);
    
    coordinator.setContextRepository(new SimpleContextRepository());
    SimpleJobRepository jobRepository = new SimpleJobRepository();
    coordinator.setJobRepository(jobRepository);
    coordinator.setPipelineRepository(new FileSystemPipelineRepository());
    
    SimpleMessenger coordinatorMessenger = new SimpleMessenger();
    coordinatorMessenger.receive("tasks", (o)->worker.handle((JobTask)o));
    DefaultTaskExecutor taskExecutor = new DefaultTaskExecutor();
    taskExecutor.setMessenger(coordinatorMessenger);
    coordinator.setTaskExecutor(taskExecutor);
        
    Job job = coordinator.start("demo/hello", new HashMap<String, Object> ());
    
    Job completedJob = jobRepository.findOne(job.getId());
    
    Assert.assertEquals(JobStatus.COMPLETED, completedJob.getStatus());
  }
  
}
