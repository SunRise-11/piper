package com.creactiviti.piper.core.task;

import java.util.Objects;

import com.creactiviti.piper.core.Coordinator;
import com.creactiviti.piper.core.context.MapContext;
import com.creactiviti.piper.core.event.EventListener;
import com.creactiviti.piper.core.event.Events;
import com.creactiviti.piper.core.event.PiperEvent;
import com.creactiviti.piper.core.job.Job;
import com.creactiviti.piper.core.job.JobRepository;
import com.creactiviti.piper.core.job.JobStatus;

/**
 * @author Arik Cohen
 * @since Sep 06, 2018
 */
public class SubflowJobStatusEventListener implements EventListener {

  private final JobRepository jobRepository;
  private final TaskExecutionRepository taskExecutionRepository;
  private final Coordinator coordinator;
  private final TaskEvaluator taskEvaluator = new SpelTaskEvaluator();
  
  public SubflowJobStatusEventListener (JobRepository aJobRepository, TaskExecutionRepository aTaskExecutionRepository, Coordinator aCoordinator) {
    jobRepository = Objects.requireNonNull(aJobRepository);
    taskExecutionRepository = Objects.requireNonNull(aTaskExecutionRepository);
    coordinator = Objects.requireNonNull(aCoordinator);
  }
  
  @Override
  public void onApplicationEvent (PiperEvent aEvent) {
    if(aEvent.getType().equals(Events.JOB_STATUS)) {
      
      String jobId = aEvent.getRequiredString("jobId");
      JobStatus status = JobStatus.valueOf(aEvent.getRequiredString("status"));
      Job job = jobRepository.findOne(jobId);
      
      if(job.getParentTaskExecutionId() == null) {
        return; // not a subflow -- nothing to do
      }
      
      switch(status) {
        case CREATED:
        case STARTED:
          break;
        case COMPLETED:{
          SimpleTaskExecution completion = SimpleTaskExecution.createForUpdate(taskExecutionRepository.findOne(job.getParentTaskExecutionId()));
          Object output = job.getOutputs();
          if(completion.getOutput() != null) {
            TaskExecution evaluated = taskEvaluator.evaluate(completion, new MapContext ("execution", new MapContext("output", output)));
            completion = SimpleTaskExecution.createForUpdate(evaluated);
          }
          else {
            completion.setOutput(output);
          }
          coordinator.complete(completion);
          break;
        }
        default:
          throw new IllegalStateException("Unnown status: " + status);
      }
    }
  }

}
