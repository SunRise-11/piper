/* 
 * Copyright (C) Creactiviti LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Arik Cohen <arik@creactiviti.com>, Mar 2017
 */
package com.creactiviti.piper.core.job;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;

import com.creactiviti.piper.core.DSL;
import com.creactiviti.piper.core.MapObject;
import com.creactiviti.piper.core.task.TaskExecution;
import com.creactiviti.piper.error.Error;
import com.creactiviti.piper.error.ErrorObject;
import com.creactiviti.piper.error.Prioritizable;

/**
 * An implementation of the {@link Job} interface
 * based on {@link MapObject}.
 * 
 * @author Arik Cohen
 */
public class SimpleJob extends MapObject implements Job {

  public SimpleJob () {
    super(Collections.EMPTY_MAP);
  }
  
  public SimpleJob (Map<String,Object> aSource) {
    super(aSource);
  }
  
  public SimpleJob (Job aSource) {
    super();
    BeanUtils.copyProperties(aSource, this);
  }
    
  @Override
  public String getId() {
    return getString(DSL.ID);
  }
  
  public void setId(String aId) {
    set(DSL.ID, aId);
  }
  
  @Override
  public int getCurrentTask() {
    return getInteger("currentTask", -1);
  }
  
  public void setCurrentTask (int aCurrentStep) {
    set("currentTask", aCurrentStep);
  }
  
  @Override
  public String getLabel() {
    return getString(DSL.LABEL);
  }
  
  public void setLabel(String aLabel) {
    set(DSL.LABEL, aLabel);
  }
  
  @Override
  public Error getError() {
    if(get(DSL.ERROR)!=null) {
      return new ErrorObject(getMap(DSL.ERROR));
    }
    return null;
  }
  
  public void setError (Error aError) {
    set(DSL.ERROR, aError);
  }
  
  @Override
  public List<TaskExecution> getExecution() {
    List<TaskExecution> list = getList(DSL.EXECUTION, TaskExecution.class);
    return list!=null?list:Collections.EMPTY_LIST;
  }
  
  @Override
  public JobStatus getStatus() {
    String value = getString(DSL.STATUS);
    return value!=null?JobStatus.valueOf(value):null;
  }
  
  public void setStatus (JobStatus aStatus) {
    set(DSL.STATUS, aStatus);
  }
  
  public void setEndTime(Date aEndTime) {
    set(DSL.END_TIME, aEndTime);
  }
  
  public void setStartTime(Date aStartTime) {
    set(DSL.START_TIME,aStartTime);
  }
  
  @Override
  public Date getCreateTime() {
    return getDate(DSL.CREATE_TIME);
  }
  
  public void setCreateTime (Date aCreateTime) {
    set(DSL.CREATE_TIME,aCreateTime);
  }
  
  @Override
  public String getPipelineId() {
    return getString(DSL.PIPELINE_ID);
  }
  
  public void setPipelineId(String aPipelineId) {
    set(DSL.PIPELINE_ID,aPipelineId);
  }
  
  @Override
  public Date getStartTime() {
    return getDate(DSL.START_TIME);
  }
  
  @Override
  public Date getEndTime() {
    return getDate(DSL.END_TIME);
  }
  
  @Override
  public String[] getTags() {
    if(containsKey(DSL.TAGS)) {
      return getArray(DSL.TAGS, String.class);
    }
    else {
      return new String[0];
    }
  }
  
  public void setTags (String[] aTags) {
    set(DSL.TAGS, aTags);
  }
  
  @Override
  public int getPriority() {
    return getInteger(DSL.PRIORTIY, Prioritizable.DEFAULT_PRIORITY);
  }
  
  public void setPriority (int aPriority) {
    set(DSL.PRIORTIY, aPriority);
  }
  
}
