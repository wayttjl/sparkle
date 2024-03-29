package me.donnior.sparkle.servlet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import me.donnior.fava.FList;
import me.donnior.fava.util.FLists;
import me.donnior.sparkle.config.Config;
import me.donnior.sparkle.core.view.ViewRender;
import me.donnior.sparkle.util.Singleton;

@Singleton
public class ConfigImpl implements Config, ConfigResult {

    //TODO change collection type to Set
    private FList<Class<? extends ViewRender>> viewRenders = FLists.newEmptyList();
    private FList<String> controllerPackages = FLists.newEmptyList();
    private String basePackage = "";
    
    @Override
    public void registerViewRenderClass(Class<? extends ViewRender> viewRenderClass) {
        if(!this.viewRenders.contains(viewRenderClass)){
            this.viewRenders.add(viewRenderClass);
        }
    }

    @Override
    public void registerControllerPackages(String... packages) {
        if(packages != null){
            this.controllerPackages.addAll(Arrays.asList(packages));
        }
    }
    
    @Override
    public void registerBasePackage(String basePackage) {
        if(basePackage != null){
            this.basePackage = basePackage;
        }
    }
    
    @Override
    public FList<Class<? extends ViewRender>> getCustomizedViewRenders() {
        return this.viewRenders.compact();
    }

    @Override
    public String[] getControllerPackages() {
        Set<String> set = new HashSet<String>(this.controllerPackages.compact());
        return set.toArray(new String[set.size()]);
    }
    
    @Override
    public String getBasePackage() {
        return this.basePackage;
    }
    
    @Override
    public ControllerFactory getControllerFactory() {
        return null;
    }
    
    @Override
    public Class<? extends ControllerFactory> getControllerFactoryClass() {
        return null;
    }

}
