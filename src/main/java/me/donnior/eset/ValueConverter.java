package me.donnior.eset;

import java.lang.reflect.Array;

public class ValueConverter {
    
    public Object convertValue(String[] values, Class expectedType){
        if(values == null){
            throw new RuntimeException("the values to be converted can't be null");
        }
        
        if(values.length == 0){
            if(expectedType.isArray()){
                //create empty Array and return it;
                Class<?> componentType = expectedType.getComponentType();
                int arraySize = 0;
                return Array.newInstance(componentType, arraySize);
            } else {
                //convert empty values to not array type like String
                return null;
            }
        }
        
        //values has more than one value
        if(expectedType.isArray()){
            //create Array and set each value and return the array
            Class<?> componentType = expectedType.getComponentType();
            int arraySize = values.length;
            Object result = Array.newInstance(componentType, arraySize);
            for(int i=0; i<values.length; i++){
                Array.set(result, i, convertSingleVaule(values[i], componentType));
            }
            return result;
        }else{
            String value = values[0];
            //convert the value to object that is not array
            return convertSingleVaule(value, expectedType);
        }
    }

    private Object convertSingleVaule(String string, Class<?> componentType) {
        if(componentType.equals(String.class)){
            return string;
        }
        if(isPrimitiveTypeOrWraped(componentType, Boolean.class, boolean.class)){
            return Boolean.valueOf(string);
        }
        if(isPrimitiveTypeOrWraped(componentType, Byte.class, byte.class)){
            return Byte.valueOf(string);
        }
        if(isPrimitiveTypeOrWraped(componentType, Short.class, short.class)){
            return Short.valueOf(string);
        }
        if(isPrimitiveTypeOrWraped(componentType, Integer.class, int.class)){
            return Integer.valueOf(string);
        }
        if(isPrimitiveTypeOrWraped(componentType, Long.class, long.class)){
            return Long.valueOf(string);
        }
        if(isPrimitiveTypeOrWraped(componentType, Float.class, float.class)){
            return Float.valueOf(string);
        }
        if(isPrimitiveTypeOrWraped(componentType, Double.class, double.class)){
            return Double.valueOf(string);
        }
        else{
            throw new RuntimeException("action method argument not support type of " + componentType.getSimpleName());
        }
    }

    @SuppressWarnings("rawtypes")
    private boolean isPrimitiveTypeOrWraped(Class<?> type, Class primitiveWrapClass, Class primitiveClass){
        return type.equals(primitiveWrapClass) || type.equals(primitiveClass);
    }
    
}
