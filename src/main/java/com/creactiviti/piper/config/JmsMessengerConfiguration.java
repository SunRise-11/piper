/* 
 * Copyright (C) Creactiviti LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Arik Cohen <arik@creactiviti.com>, Mar 2017
 */
package com.creactiviti.piper.config;

import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import com.creactiviti.piper.core.Coordinator;
import com.creactiviti.piper.core.Worker;
import com.creactiviti.piper.core.job.MutableJobTask;
import com.creactiviti.piper.core.messenger.JmsMessenger;
import com.creactiviti.piper.core.task.JobTask;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;

@Configuration
@EnableConfigurationProperties(PiperProperties.class)
public class JmsMessengerConfiguration {

  @Autowired
  private ConnectionFactory connectionFactory;
  
  @Lazy
  @Autowired
  private Worker worker;
  
  @Autowired
  private Coordinator coordinator;
  
  @Bean
  JmsMessenger jmsMessenger (JmsTemplate aJmsTemplate, ObjectMapper aObjectMapper) {
    JmsMessenger jmsMessenger = new JmsMessenger();
    jmsMessenger.setJmsTemplate(aJmsTemplate);
    jmsMessenger.setObjectMapper(aObjectMapper);
    return jmsMessenger;
  }
  
//  @Beta
//  ObjectMapper objectMapper () {
//    ObjectMapper objectMapper = new ObjectMapper();
//    SimpleModule module = new SimpleModule();
//    module.addSerializer(Throwable.class, new ExceptionSerializer());
//    objectMapper.registerModule(module);
//    return objectMapper;
//  }
  
  @Bean
  JmsTemplate jmsTemplate (PiperProperties piperProperties) {
    JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
    return jmsTemplate;
  }
  
  @Bean
  DefaultMessageListenerContainer workerMessageListener (ObjectMapper aObjectMapper) {
    DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
    container.setConnectionFactory (connectionFactory);
    container.setDestinationName("tasks");
    MessageListener listener = (m) -> worker.handle(toTask(m, aObjectMapper));
    container.setMessageListener(listener);
    return container;
  }
  
  @Bean
  DefaultMessageListenerContainer completionsMessageListener (ObjectMapper aObjectMapper) throws JMSException {
    DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
    container.setConnectionFactory (connectionFactory);
    container.setDestinationName("completions");
    MessageListener listener = (m) -> coordinator.completeTask(toTask(m, aObjectMapper));
    container.setMessageListener(listener);
    return container;
  }
  
  @Bean
  DefaultMessageListenerContainer errorsMessageListener (ObjectMapper aObjectMapper) throws JMSException {
    DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
    container.setConnectionFactory (connectionFactory);
    container.setDestinationName("errors");
    MessageListener listener = (m) -> coordinator.error(toTask(m, aObjectMapper));
    container.setMessageListener(listener);
    return container;
  }
  
  private JobTask toTask (Message aMessage, ObjectMapper aObjectMapper) {
    try {
      String raw = aMessage.getBody(String.class);
      Map<String, Object> task = aObjectMapper.readValue(raw,Map.class);
      return new MutableJobTask(task);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }
    
}
