package com.wideplay.warp.internal.componentry;

import com.wideplay.warp.module.componentry.ComponentClassReflection;
import com.wideplay.warp.module.componentry.Renderable;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * On: 17/03/2007
 *
 * @author Dhanji R. Prasanna (dhanji at gmail com)
 * @since 1.0
 */
class ComponentClassReflectionBuilder {
    private final Class<? extends Renderable> componentClass;

    public ComponentClassReflectionBuilder(Class<? extends Renderable> componentClass) {
        this.componentClass = componentClass;
    }

    public ComponentClassReflection build() {
        final Map<String, Method> setters = new LinkedHashMap<String, Method>();
        final Map<String, Method> getters = new LinkedHashMap<String, Method>();

        //add any method starting with "set" and taking one parameter and returning void
        for (Method method : componentClass.getMethods())
            if (method.getName().startsWith("set") && method.getParameterTypes().length == 1 && void.class.equals(method.getReturnType()))
                setters.put(getPropertyNameFromAccessor(method.getName()), method);
        

        //add any method starting with "getValue" and taking no parameter and not returning void
        for (Method method : componentClass.getMethods())
            if (method.getName().startsWith("get") && method.getParameterTypes().length == 0 && !void.class.equals(method.getReturnType()))
                getters.put(getPropertyNameFromAccessor(method.getName()), method);

        return new ComponentClassReflection(componentClass, getters, setters);
    }

    public static String getPropertyNameFromAccessor(String getter) {
        String property = getter.substring(4);
        property = getter.substring(3, 4).toLowerCase() + property;
        
        return property;
    }
}
