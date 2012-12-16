package me.donnior.sparkle.internal;

import java.util.HashMap;
import java.util.Map;

public class ControllersHolder {
    
    private Map<String, Class<?>> controllers = new HashMap<String, Class<?>>();
    
    public Map<String, Class<?>> namedControllers(){
        return this.controllers;
    }
    
    public boolean containsController(String controllerName){
        return this.controllers.containsKey(controllerName);
    }
    
    public Class<?> getControllerClass(String controllerName){
        return this.controllers.get(controllerName);
    }

    public void addControllers(Map<String, Class<?>> controllersMap) {
        this.addControllers(controllersMap, false);
    }
    
    public void addControllers(Map<String, Class<?>> controllersMap, boolean reset) {
        if(reset){
            this.controllers.clear();
        }
        for(Map.Entry<String, Class<?>> controller: controllersMap.entrySet()){
            //check duplicated controller name
            if(this.containsController(controller.getKey())){
                throw new RuntimeException("Controller with name " + controller.getKey() + " was duplicated");                
            }else{
                this.controllers.put(controller.getKey(), controller.getValue());
            }
        }
    }
    
}