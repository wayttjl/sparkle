package me.donnior.srape;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Joiner;

public class FieldBuilderImpl implements ScopedFieldBuilder{

    private String name;
    private Object value;
    private Class<? extends SrapeEntity> clz;

    private boolean condition;
    private boolean hasConditon;
    private boolean hasName;
    
    public FieldBuilderImpl(Object value) {
        this.value = value;
    }

    public ConditionalFieldBuilder withNameAndType(String name, Class<? extends SrapeEntity> entityClass) {
        this.hasName = true;
        this.name = name;
        this.clz = entityClass;
        return this;
    }

    public ConditionalFieldBuilder withName(String string) {
        return this.withNameAndType(string, null);        
    }
    
    public ConditionalFieldBuilder withType(Class<? extends SrapeEntity> entityClass) {
        this.hasName = false;
        this.clz = entityClass;
        return this;        
    }
    
    public String getName() {
        return name;
    }
    
    public Class<? extends SrapeEntity> getEntityClass(){
        return this.clz;
    }
    
    /**
     * whether this field exposition has a valid entity type, not just set entity type manually,
     * the value must a not Map value.
     * 
     * @return
     */
    public boolean hasEntityType(){
        return this.clz != null && !this.isMapValue();
    }

    public boolean isMapValue(){
        return this.value != null && this.value instanceof Map;
    }
    
    public boolean isCollectionValue(){
        return this.value != null && this.value instanceof Collection;
    }
    
    public boolean isArrayValue(){
        return this.value != null && this.value.getClass().isArray();
    }
    
    public boolean isValueIterable(){
        return isArrayValue() || isCollectionValue();
    }
    
    public void unless(boolean condition){
        this.when(!condition);
    }
    
    public void when(boolean condition){
        this.hasConditon = true;
        this.condition = condition;
    }
    
    public boolean conditionMatched(){
        return hasConditon ? condition : true;
    }

    public boolean hasCondition(){
        return hasConditon;
    }
    
    public boolean hasName(){
        return this.hasName;
    }
    
    public Object getValue() {
        return value;
    }
    
    public String toJson(){
        Object name = this.name != null ? this.name :"";
        return contentWithNameAndValue(name, this.value, this.hasName);
    }
    
    private String contentWithNameAndValue(Object name, Object value, boolean hasName){
        if(hasName){
            return StringUtil.quote(name.toString()) + ":" + value0(value);   
        } else {
            return value0(value).toString();
        }
    }
    
    private Object value0(Object value){
        if(value == null){
            //TODO deal with null
            return "null";
        }
        if(value instanceof Boolean){
            return value.toString();
        }
        if(value instanceof Number){
            return value;
        }

        if(value instanceof String){
//            return StringUtil.quote(value.toString());
            return quote(value.toString());      //TODO is this enough? like string \" escaping?
        }
        
        if(!isValueIterable() && hasEntityType()){
            return buildEntity(this.value,this.clz);
        }
        
        if(isArrayValue()) {
            return _array(value);
        }
        
        if(isCollectionValue()){
            return _collection(value);
        } 
        
        if(value instanceof Map){
            return _map(value);
        } 
        
        return quote(value.toString());
    }

    private Object _map(Object value) {
        //TODO map data, for map data, should disable entity mapping, 
        //can explicit set hasEntityType to false
        Iterator<Entry<Object, Object>> it = ((Map)value).entrySet().iterator();
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        List<String> collector = new ArrayList<String>();
        while(it.hasNext()){
            Entry<Object, Object>  entry = it.next();
            collector.add(contentWithNameAndValue(entry.getKey(),   entry.getValue(), true));
        }
        sb.append(Joiner.on(",").join(collector));
        sb.append("}");
        return sb.toString();
    }

    //json value for collection represent
    private Object _collection(Object value) {
        //TODO data with normal type, fall back to gson
        boolean hasEntityType = this.hasEntityType();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        
        List<Object> values = new ArrayList<Object>();
        Iterator<Object> it = ((Collection)value).iterator();
        while(it.hasNext()){
            if(hasEntityType){
                values.add(buildEntity(it.next(), this.clz));
            } else {
                values.add(value0(it.next()));
            }
        }
        sb.append(Joiner.on(",").join(values));
        sb.append("]");
        return sb.toString();
    }

    //json value for array represent
    private Object _array(Object value) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int length = Array.getLength(value);
        List<Object> values = new ArrayList<Object>();
        for (int i = 0; i < length; i ++) {
            Object arrayElement = Array.get(value, i);
            if(hasEntityType()){
                values.add(buildEntity(arrayElement, this.clz));
            } else {
                values.add(value0(arrayElement));
            }
        }
        sb.append(Joiner.on(",").join(values));
        sb.append("]");
        return sb.toString();
    }
    
    
    private Object buildEntity(Object value, Class<? extends SrapeEntity> clz) {
        try {
            SrapeEntity entity = clz.newInstance();
            FieldsExpositionHolder holder = new FieldsExpositionHolder();
            entity.config(value, holder);
            return holder.build();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Is this field exposition a pure iterable value? means it value is data type and don't have a
     * explicit name. You can't use {@link #withName(String)} or {@link #withNameAndType(String, Class)}
     * to define a field exposition if you want to make it pure data, you can use {@link #withType(Class)}
     * or just ignore the 'with' clause. 
     * 
     * <br />
     * <br />
     * 
     * If this field exposition is pure iterable, it would be output as <pre><code>[1,2,3]</code></pre>
     * 
     * 
     * Otherwise it will be output as <pre><code>{"name": xxxx}</code></pre>
     * 
     * @return
     */
    public boolean isPureIterableValue(){
        return this.isValueIterable() && !hasName();
    }
    
    public static String quote(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        char         c = 0;
        int          i;
        int          len = string.length();
        StringBuilder sb = new StringBuilder(len + 4);
        String       t;

        sb.append('"');
        for (i = 0; i < len; i += 1) {
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                sb.append('\\');
                sb.append(c);
                break;
            case '/':
//                if (b == '<') {
                    sb.append('\\');
//                }
                sb.append(c);
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\r':
               sb.append("\\r");
               break;
            default:
                if (c < ' ') {
                    t = "000" + Integer.toHexString(c);
                    sb.append("\\u" + t.substring(t.length() - 4));
                } else {
                    sb.append(c);
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }
}
