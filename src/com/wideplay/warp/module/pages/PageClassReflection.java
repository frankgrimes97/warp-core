package com.wideplay.warp.module.pages;

import com.wideplay.warp.util.reflect.FieldDescriptor;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * On: 25/03/2007
 *
 * @author Dhanji R. Prasanna (dhanji at gmail com)
 * @since 1.0
 */
public interface PageClassReflection {
    Object getPropertyValue(Object bean, String name);

    void setPropertyValue(Object bean, String name, Object value);

    Class<?> getPageClass();//fires all event handlers (default action)

    Object fireEvent(Object bean, String event, Object topic);

    Set<FieldDescriptor> getManagedFields();

}
