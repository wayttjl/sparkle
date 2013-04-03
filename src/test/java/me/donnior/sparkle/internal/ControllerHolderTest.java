package me.donnior.sparkle.internal;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

public class ControllerHolderTest {

    @Test
    public void testGetting(){
        ControllersHolder ch = new ControllersHolder();
        ch.addControllers(sampleControllersMap());
        
        assertEquals(2, ch.namedControllers().size());
        assertTrue(ch.containsController("sampleOne"));
        assertTrue(ch.containsController("sampleTwo"));
        
        assertEquals(SampleOne.class, ch.getControllerClass("sampleOne"));
        
    }
    
    @Test
    public void testAddWithReset(){
        ControllersHolder ch = new ControllersHolder();
        ch.addControllers(sampleControllersMap());
        
        assertEquals(2, ch.namedControllers().size());
        
        ch.addControllers(anotherSampleControllersMap(), true);
        
        assertEquals(1, ch.namedControllers().size());
    }
    
    @Test
    public void testAddWithDuplication(){
        ControllersHolder ch = ControllersHolder.getInstance();
        ch.addControllers(sampleControllersMap());
        
        assertEquals(2, ch.namedControllers().size());
        
        try{
            ch.addControllers(duplicatedControllerMap());
            fail();
        }catch(RuntimeException re){
            
        }
        
        
    }
    

    private Map<String, Class<?>> sampleControllersMap() {
        Map<String, Class<?>> controllersMap = Maps.newConcurrentMap();
        
        controllersMap.put("sampleOne", SampleOne.class);
        controllersMap.put("sampleTwo", SampleTwo.class);
        
        return controllersMap;
    }
    
    private Map<String, Class<?>> duplicatedControllerMap() {
        Map<String, Class<?>> controllersMap = Maps.newConcurrentMap();
        
        controllersMap.put("sampleOne", SampleOne.class);
        
        return controllersMap;
    }    

    private Map<String, Class<?>> anotherSampleControllersMap() {
        Map<String, Class<?>> controllersMap = Maps.newConcurrentMap();
        
        controllersMap.put("sampleThree", SampleOne.class);
        
        return controllersMap;
    }
    
}

class SampleOne{
    
}

class SampleTwo{
    
}