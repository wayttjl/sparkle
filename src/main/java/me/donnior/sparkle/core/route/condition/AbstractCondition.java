package me.donnior.sparkle.core.route.condition;

import javax.servlet.http.HttpServletRequest;

import me.donnior.fava.FList;
import me.donnior.fava.Predicate;
import me.donnior.fava.util.FLists;

import com.google.common.base.Joiner;

public abstract class AbstractCondition {

    protected String[] params;
    private FList<ConditionItem> conditionItems = FLists.newEmptyList();

    public AbstractCondition(String[] params) {
        this.params = params;
        parseParams(params);
    }
    
    private void parseParams(String[] params){
        for(String param : params){
            this.conditionItems.add(ConditionItemFactory.createItem(param));
        }
    }

    public boolean match(final HttpServletRequest request) {
        return this.conditionItems.all(new Predicate<ConditionItem>() {
            @Override
            public boolean apply(ConditionItem item) {
                String real = getConditionValueFromRequest(request, item.getName());
                return item.match(real);
            }
        });
    }

    @Override
    public String toString() {
        return "\""+Joiner.on(",").join(this.params)+"\"";
    }

    public abstract String getConditionValueFromRequest(HttpServletRequest request, String key);
    
}