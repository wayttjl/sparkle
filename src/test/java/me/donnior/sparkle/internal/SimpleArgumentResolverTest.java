package me.donnior.sparkle.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import me.donnior.sparkle.annotation.Json;
import me.donnior.sparkle.annotation.Param;
import me.donnior.web.adapter.HttpServletRequestAdapter;

import org.junit.Test;

public class SimpleArgumentResolverTest {

    @Test
    public void test_support_ability(){
        SimpleArgumentResolver resolver = new SimpleArgumentResolver();
        
        ActionParamDefinition apd = correctSupportedActionParamDefinition(String.class, "userName");
        
        assertTrue(resolver.support(apd));
        
        apd = createActionParamDefinition(String.class, Json.class, "userName");
        assertFalse(resolver.support(apd));
        
    }
    
    @Test
    public void test_null_value_with_paramname(){
        SimpleArgumentResolver resolver = new SimpleArgumentResolver();
        
        ActionParamDefinition apd = correctSupportedActionParamDefinition(String.class, "userName");
        HttpServletRequest request = createRequestWithParamNameAndValue("userName", null);
        Object result = resolver.resovle(apd, request);
        assertNull(result);

        apd = correctSupportedActionParamDefinition(int.class, "userName");
        request = createRequestWithParamNameAndValue("userName", null);
        
        try {
            result = resolver.resovle(apd, request);
            fail();
        } catch (RuntimeException re) {
            assertEquals("action method argument annotated with @Param not support primitive", re.getMessage());
        }

    }
    
    @Test
    public void test_normal_value_with_paramname(){
        SimpleArgumentResolver resolver = new SimpleArgumentResolver();
        
        ActionParamDefinition apd = correctSupportedActionParamDefinition(Integer.class, "page");
        HttpServletRequest request = createRequestWithParamNameAndValue("page", new String[]{"1"});
        Object result = resolver.resovle(apd, request);
        
        assertEquals(Integer.valueOf(1), (Integer)result);
        
    }    
    
    private HttpServletRequest createRequestWithParamNameAndValue(
            final String name, final String[] values) {
        return new HttpServletRequestAdapter(){
            
            @Override
            public String[] getParameterValues(String arg0) {
                if(arg0.equals(name)){
                    return values;
                }
                return super.getParameterValues(arg0);
            }
        };
    }

    private ActionParamDefinition createActionParamDefinition(
            final Class<?> paramType, final Class<? extends Annotation> annotationType, final String annotationValue) {
        Annotation an = new Annotation() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return annotationType;
            }
            
            public String value() {
                return annotationValue;
            }
        };
        
        return new DefaulActionParamDefinition(paramType, Arrays.asList(an));
    }

    private ActionParamDefinition correctSupportedActionParamDefinition(Class<?> paramType, final String annotationValue) {
        Annotation userNameAnnotation = new Param() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return Param.class;
            }
            
            @Override
            public String value() {
                return annotationValue;
            }
        };
        
        return new DefaulActionParamDefinition(paramType, Arrays.asList(userNameAnnotation));
    }

    
}
