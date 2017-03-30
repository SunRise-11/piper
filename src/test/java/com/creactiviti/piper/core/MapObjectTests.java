package com.creactiviti.piper.core;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;


public class MapObjectTests {

  @Test
  public void test1 () {
    MapObject mo = new MapObject(Collections.singletonMap("hello", "world")) {};
    Assert.assertEquals("world", mo.getString("hello"));
  }
  
  @Test
  public void test2 () {
    MapObject mo = new MapObject(Collections.singletonMap("number", "5")) {};
    Assert.assertEquals(Integer.valueOf(5), mo.getInteger("number"));
  }
  
  @Test
  public void test3 () {
    MapObject mo = new MapObject(Collections.singletonMap("number", "5")) {};
    Assert.assertEquals(Double.valueOf(5), mo.getDouble("number"));
  }
  
  @Test
  public void test4 () {
    MapObject mo = new MapObject(Collections.singletonMap("number", "5")) {};
    Assert.assertEquals(Double.valueOf(3), mo.getDouble("double",3));
  }
  
  @Test
  public void test5 () {
    MapObject mo = new MapObject(Collections.singletonMap("number", "5")) {};
    Assert.assertEquals(1, mo.size());
  }

  @Test
  public void test6 () {
    MapObject mo = new MapObject(Collections.singletonMap("number", "5")) {};
    Assert.assertTrue(mo.containsKey("number"));
    Assert.assertFalse(mo.containsKey("none"));
  }
  
  @Test
  public void test7 () {
    MapObject mo = new MapObject(Collections.singletonMap("number", "5")) {};
    Assert.assertTrue(mo.containsValue("5"));
  }
  
  @Test
  public void test8 () {
    MapObject mo = new MapObject(Collections.singletonMap("hello", "world")) {};
    Assert.assertEquals("world", mo.get("hello"));
  }
  
}
