package me.donnior.sparkle.config;

import me.donnior.sparkle.core.view.ViewRender;

public interface Config {

    /**
     * Register customized view renders. 
     */
    void registerViewRenderClass(Class<? extends ViewRender> class1);
    
    
    void registerControllerPackages(String... packages);
    
    
    void registerBasePackage(String basePackage);
    
}
