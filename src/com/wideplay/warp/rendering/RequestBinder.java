package com.wideplay.warp.rendering;

import com.google.inject.servlet.RequestParameters;

import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * On: 25/03/2007
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
public interface RequestBinder {
    String EVENT_PARAMETER_NAME = "w_event";
    String EXPR_PARAMETER_NAME = "__eval_as_expr_";

    void bindBean(Object bean, @RequestParameters Map<String, String[]> parameters);
}