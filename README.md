# What is this?

Piper is a miniature workflow engine written in Java/Spring.

# For god's sake, why another workflow engine? 

Many of the workflow engines that i've looked at, claim to be "light" and "simple" but expect you to master BPMN and their 500+ pages documentation just to get going. In this project I'm striving to deliver on these promises and allow developer to cut to the chase.    

# How it works? 

Piper works by executing a set of tasks defined as a YAML document. 

Example:

`pipelines/demo/hello.yaml`

```
name: Hello World
    
tasks: 
  - name: Print a greeting
    handler: log
    text: hello world
    
  - name: Print a greeting
    handler: log
    text: what's up world?
    
  - name: Print a greeting
    handler: log
    text: goodbye world
```

The central interface that is used to execute tasks is the `TaskHandler`:

```
public interface TaskHandler<O> {

  O handle (JobTask aTask);
  
}
```

`TaskHandler`s are resolved according to the `handler` property of each task. Here is the `log` `TaskHandler` implementations seen on the pipeline above: 

```
package com.creactiviti.piper.taskhandler.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.creactiviti.piper.core.TaskHandler;
import com.creactiviti.piper.core.task.JobTask;

@Component // register the implementation with the application
public class Log implements TaskHandler<Object> {

  private Logger log = LoggerFactory.getLogger(getClass());

  @Override
  public Object handle (JobTask aTask) { // receive the task instance to execute
    log.info(aTask.getString("text")); // get the text property from the task and output it
    return null; // don't return anything
  }

}
``` 

# Pipelines

Pipeline definitions are located under the `pipelines/` directory on the root of the project.

# First time start

Prerequisites: JDK 8 and Maven 3

`mvn clean spring-boot:run` 

This will start piper on your local box, running fully in-memory and without relying on any external dependencies like database or a messaging middleware. 

# Jobs 

Jobs can be started from the REST API: 

```
curl -s -X POST -H "Content-Type:application/json" -d '{"pipelineId":"demo/hello"}' http://localhost:8080/job/start
``` 
 
# Licensing

Piper is licensed under the Apache License, Version 2.0. See [LICENSE](https://github.com/creactiviti/piper/blob/master/LICENSE) for the full license text.

