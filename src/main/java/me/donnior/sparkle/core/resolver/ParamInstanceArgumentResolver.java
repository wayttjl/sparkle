package me.donnior.sparkle.core.resolver;

import javax.servlet.http.HttpServletRequest;

import me.donnior.sparkle.Params;
import me.donnior.sparkle.core.ActionParamDefinition;

/**
 * Argument resolver for argument  with type {@link Params} 
 *
 */
public class ParamInstanceArgumentResolver implements ArgumentResolver {

    @Override
    public boolean support(ActionParamDefinition actionParamDefinition) {
        return Params.class.equals(actionParamDefinition.paramType());
    }

    @Override
    public Object resovle(ActionParamDefinition actionParamDefinition, HttpServletRequest request) {
      return (Params)new HttpRequestParamsWraper(request);
    }

}