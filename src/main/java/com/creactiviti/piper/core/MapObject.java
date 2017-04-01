/* 
 * Copyright (C) Creactiviti LLC - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Arik Cohen <arik@creactiviti.com>, Mar 2017
 */
package com.creactiviti.piper.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.util.Assert;

/**
 * @author Arik Cohen
 * @since Jun 12, 2016
 */
public class MapObject implements Map<String, Object>, Accessor {

  private final HashMap<String, Object> map;
  
  public MapObject (Map<String, Object> aSource) {
    map = new HashMap<String, Object>(aSource);
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public boolean containsKey(Object aKey) {
    return map.containsKey(aKey);
  }

  @Override
  public boolean containsValue(Object aValue) {
    return map.containsValue(aValue);
  }

  @Override
  public Object get(Object aKey) {
    return map.get(aKey);
  }
  
  @Override
  public <T> List<T> getList(Object aKey, Class<T> aElementType) {
    List list = get(aKey, List.class);
    List<T> typedList = new ArrayList<>();
    for(Object item : list) {
      if(aElementType.equals(Accessor.class)) {
        typedList.add((T)new MapObject((Map<String, Object>) item));
      }
      else {
        typedList.add((T)ConvertUtils.convert(item,aElementType));
      }
    }
    return typedList;
  }
  
  @Override
  public String getString (Object aKey) {
    Object value = get(aKey);
    return ConvertUtils.convert(value);
  }
  
  @Override
  public String getRequiredString(Object aKey) {
    String value = getString(aKey);
    Assert.notNull(value,"Unknown key: " + aKey);
    return value;
  }
  
  @Override
  public String getString (Object aKey, String aDefault) {
    String value = getString(aKey);
    return value != null ? value : aDefault;
  }
  
  @Override
  public Object put(String aKey, Object aValue) {
    return map.put(aKey, aValue);
  }

  @Override
  public Object remove(Object aKey) {
    return map.remove(aKey);
  }

  @Override
  public void putAll(Map<? extends String, ? extends Object> aVariables) {
    map.putAll(aVariables);
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public Set<String> keySet() {
    return map.keySet();
  }

  @Override
  public Collection<Object> values() {
    return map.values();
  }

  @Override
  public Set<java.util.Map.Entry<String, Object>> entrySet() {
    return map.entrySet();
  }

  @Override
  public <T> T get(Object aKey, Class<T> aReturnType) {
    Object value = get(aKey);
    if(value == null) {
      return null;
    }
    return (T) ConvertUtils.convert(value, aReturnType);
  }
  
  @Override
  public <T> T get(Object aKey, Class<T> aReturnType, T aDefaultValue) {
    Object value = get(aKey);
    if(value == null) {
      return aDefaultValue;
    }
    return (T) ConvertUtils.convert(value, aReturnType);
  }

  @Override
  public Long getLong(Object aKey) {
    return get(aKey,Long.class);
  }

  @Override
  public long getLong(Object aKey, long aDefaultValue) {
    return get(aKey,Long.class,aDefaultValue);
  }

  @Override
  public Double getDouble(Object aKey) {
    return get(aKey,Double.class);
  }

  @Override
  public Double getDouble(Object aKey, double aDefaultValue) {
    return get(aKey,Double.class,aDefaultValue);
  }

  @Override
  public Integer getInteger(Object aKey) {
    return get(aKey,Integer.class);
  }

  @Override
  public int getInteger(Object aKey, int aDefaultValue) {
    return get(aKey, Integer.class, aDefaultValue);
  }
  
  @Override
  public Date getDate(Object aKey) {
    return get(aKey, Date.class);
  }
  
  @Override
  public Boolean getBoolean(Object aKey) {
    return get(aKey, Boolean.class);
  }
  
  @Override
  public boolean getBoolean(Object aKey, boolean aDefaultValue) {
    Boolean value = getBoolean(aKey);
    return value!=null?value:aDefaultValue;
  }
  
  @Override
  public MapObject getMapObject (Object aKey) {
    Map<String,Object> value = (Map<String, Object>) get(aKey);
    if(value == null) {
      return null;
    }
    else if(value instanceof MapObject) {
      return (MapObject) value;
    }
    return new MapObject(value);
  }
  
  @Override
  public Map<String, Object> asMap() {
    return SerializationUtils.clone(map);
  }
  
  public String toString() {
    return map.toString();
  }
  
  public static MapObject empty () {
    return new MapObject(Collections.EMPTY_MAP);
  }
  
  public static MapObject of (Map<String,Object> aMap) {
    return new MapObject(aMap);
  }
  
  @Override
  public boolean equals(Object aObj) {
    return map.equals(aObj);
  }
  
}
