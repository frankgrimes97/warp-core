package com.wideplay.warp.module.pages;

import com.google.inject.Provider;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Created by IntelliJ IDEA.
 * User: dprasanna
 * Date: 21/03/2007
 * Time: 13:59:57
 * <p/>
 *
 * Provides pages that are injected with the @Page annotation (sets only managed properties and constants).
 *
 * This class must do the injection itself (mirroring guice capabilities) because guice is viral and there
 * is no way to selectively not-inject certain properties.
 *
 * @author dprasanna
 * @since 1.0
 */
public class InjectPageProvider<T> implements Provider<T> {
    private PageClassReflection reflection;
    @Inject private Injector injector;

    public InjectPageProvider(PageClassReflection reflection) {
        this.reflection = reflection;
    }

    public T get() {
        //grab instance from guice
        T page = (T) reflection.instantiateForPageInjection(injector);

        //inject constants?
        //...
        
        return page;
    }

    public static <T> InjectPageProvider<T> provide(PageClassReflection reflection) {
        return new InjectPageProvider<T>(reflection);
    }
}