package com.wideplay.warp.module.ioc;

import com.google.inject.Module;
import com.wideplay.warp.module.componentry.PropertyDescriptor;
import com.wideplay.warp.module.pages.PageClassReflection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * On: 23/03/2007
 *
 * @author Dhanji R. Prasanna (dhanji at gmail com)
 * @since 1.0
 */
public class IocContextManager {
    //local http context (for managing the current request/scope)
    static final ThreadLocal<Context> localContext = new ThreadLocal<Context>();

    private IocContextManager() {
    }

    public static void setContext(HttpServletRequest request, HttpServletResponse response) {
        localContext.set(new Context(request, response));
    }

    public static void clearContext() {
        localContext.remove();
    }

    static HttpServletRequest getRequest() {
        Context context = localContext.get();
        if (null == context) {
          throw new NotScopeableException("Cannot access scoped object. It appears we"
              + " are not currently inside an HTTP Servlet request");
        }

        return context.getRequest();
    }

    static HttpServletResponse getResponse() {
        Context context = localContext.get();
        if (null == context) {
          throw new NotScopeableException("Cannot access scoped object. It appears we"
              + " are not currently inside an HTTP Servlet request");
        }

        return context.getResponse();
    }

    public static void injectProperties(Collection<PropertyDescriptor> propertyDescriptors, Object target, Object source) {
        ObjectInjector.injectAll(propertyDescriptors, target, source);
    }



    static class Context {
        final HttpServletRequest request;
        final HttpServletResponse response;

        Context(HttpServletRequest request, HttpServletResponse response) {
            this.request = request;
            this.response = response;
        }

        HttpServletRequest getRequest() {
            return request;
        }

        HttpServletResponse getResponse() {
            return response;
        }
    }

    static void throwNotScopeableException(String message) {
        throw new NotScopeableException(message);
    }

    public static Module newDefaultGuiceModule(List<PageClassReflection> pageBindings) {
        DefaultGuiceModule module = new DefaultGuiceModule();
        for (PageClassReflection reflection : pageBindings)
            module.bindPage(reflection);

        return module;
    }
    
    public static IocContextManager newServletIocContext() {
        return new IocContextManager();
    }
}
