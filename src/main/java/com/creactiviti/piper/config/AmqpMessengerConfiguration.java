/* 
 * Copyright (C) Creactiviti LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Arik Cohen <arik@creactiviti.com>, Apr 2017
 */
package com.creactiviti.piper.config;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitManagementTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.creactiviti.piper.core.Coordinator;
import com.creactiviti.piper.core.Worker;
import com.creactiviti.piper.core.messenger.AmqpMessenger;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableRabbit
@EnableConfigurationProperties(PiperProperties.class)
@ConditionalOnProperty(name="piper.messenger.provider",havingValue="amqp")
public class AmqpMessengerConfiguration implements RabbitListenerConfigurer {
  
  @Autowired(required=false)
  private Worker worker;
  
  @Autowired(required=false)
  private Coordinator coordinator;
  
  @Autowired
  private ObjectMapper objectMapper;
  
  @Autowired
  private PiperProperties properties;
  
  @Autowired
  private ConnectionFactory connectionFactory;
  
  private final Logger logger = LoggerFactory.getLogger(getClass());
  
  @Bean
  RabbitAdmin admin (ConnectionFactory aConnectionFactory) {
    return new RabbitAdmin(aConnectionFactory);
  }
  
  @Bean
  RabbitManagementTemplate rabbitManagementTemplate () {
    return new RabbitManagementTemplate("http://guest:guest@192.168.59.103:15672/api/");
  }
  
  @Bean
  AmqpMessenger amqpMessenger (AmqpTemplate aAmqpTemplate) {
    AmqpMessenger amqpMessenger = new AmqpMessenger();
    amqpMessenger.setAmqpTemplate(aAmqpTemplate);
    return amqpMessenger;
  }
  
  @Bean 
  MessageConverter jacksonAmqpMessageConverter(ObjectMapper aObjectMapper) {
    Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
    converter.setJsonObjectMapper(aObjectMapper);
    return converter;
  }
  
  @Override
  public void configureRabbitListeners(RabbitListenerEndpointRegistrar aRegistrar) {
    CoordinatorProperties coordinatorProperties = properties.getCoordinator();
    WorkerProperties workerProperties = properties.getWorker();
    if(coordinatorProperties.isEnabled()) {
      registerListenerEndpoint(aRegistrar, Queues.COMPLETIONS, coordinatorProperties.getSubscriptions().getCompletions() , coordinator, "completeTask");
      registerListenerEndpoint(aRegistrar, Queues.ERRORS, coordinatorProperties.getSubscriptions().getErrors(), coordinator, "error");
    }
    if(workerProperties.isEnabled()) {
      Map<String, Object> subscriptions = workerProperties.getSubscriptions();
      subscriptions.forEach((k,v) -> {
        registerListenerEndpoint(aRegistrar, k, Integer.valueOf((String)v), worker, "handle");
      });
    }
  }
  
  
  private void registerListenerEndpoint(RabbitListenerEndpointRegistrar aRegistrar, String aQueueName, int aConcurrency, Object aDelegate, String aMethodName) {
    logger.info("Registring AMQP Listener: {} -> {}:{}", aQueueName, aDelegate.getClass().getName(), aMethodName);

    admin(connectionFactory).declareQueue(new Queue(aQueueName));
    
    MessageListenerAdapter messageListener = new MessageListenerAdapter(aDelegate);
    messageListener.setMessageConverter(jacksonAmqpMessageConverter(objectMapper));
    messageListener.setDefaultListenerMethod(aMethodName);

    SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
    endpoint.setId(aQueueName+"Endpoint");
    endpoint.setQueueNames(aQueueName);
    endpoint.setMessageListener(messageListener);

    aRegistrar.registerEndpoint(endpoint,createContainerFactory(aConcurrency));
  }
  
  private SimpleRabbitListenerContainerFactory createContainerFactory (int aConcurrency) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConcurrentConsumers(aConcurrency);
    factory.setConnectionFactory(connectionFactory);
    return factory;
  }
  
}
