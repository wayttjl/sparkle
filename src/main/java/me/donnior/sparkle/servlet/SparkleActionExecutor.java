package me.donnior.sparkle.servlet;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.donnior.fava.Function;
import me.donnior.fava.util.FLists;
import me.donnior.reflection.ReflectionUtil;
import me.donnior.sparkle.core.ActionMethodDefinition;
import me.donnior.sparkle.core.ActionMethodParamDefinition;
import me.donnior.sparkle.core.SimpleWebRequest;
import me.donnior.sparkle.core.resolver.DefaultParamResolversManager;
import me.donnior.sparkle.core.resolver.ParamResolversManager;

public class SparkleActionExecutor {
    
    //TODO should refactord this params resolver, make it support multi resolvers so programmers can create their own
    // param resolver like param with class type 'Project'; so it should be List<ParamResolver>
    private ParamResolversManager paramResolver = new DefaultParamResolversManager();

    public Object invoke(ActionMethodDefinition adf, Object controller, 
            final HttpServletRequest request, final HttpServletResponse response) {
        
        Method method = adf.method();
        List<ActionMethodParamDefinition> apds = adf.paramDefinitions();
        
        Object[] params = FLists.create(apds).collect(new Function<ActionMethodParamDefinition, Object>() {
            public Object apply(ActionMethodParamDefinition apd) {
                return paramResolver.resolve(apd, new SimpleWebRequest(request, response));
            }
        }).toArray();
        
        return ReflectionUtil.invokeMethod(controller, method, params);
    }

}   
