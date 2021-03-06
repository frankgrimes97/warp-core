package com.wideplay.warp.internal.pages;

import com.wideplay.warp.module.pages.event.EventHandlerDelegate;
import com.wideplay.warp.util.reflect.FieldDescriptor;
import com.wideplay.warp.util.reflect.ReflectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
* On: 25/03/2007
*
* @author Dhanji R. Prasanna (dhanji at gmail com)
* @since 1.0
*/
class EventHandlerDelegateImpl implements EventHandlerDelegate {
    private final FieldDescriptor delegateFieldDescriptor;
    private final List<Method> anyEventHandlers;
    private final Map<String, Set<Method>> disambiguationEventHandlers;
    private final Set<Class<? extends Annotation>> resolutionAnnotations;
    private final Set<String> resolutionAnnotationsStrings;

    private EventHandlerDelegateImpl(FieldDescriptor delegateFieldDescriptor, List<Method> allEventHandlers,
                                Map<String, Set<Method>> disambiguationEventHandlers,
                                Set<Class<? extends Annotation>> resolutionAnnotations) {

        this.delegateFieldDescriptor = delegateFieldDescriptor;
        this.anyEventHandlers = allEventHandlers;
        this.disambiguationEventHandlers = disambiguationEventHandlers;
        this.resolutionAnnotations = resolutionAnnotations;

        //convert resolution annotations to strings
        resolutionAnnotationsStrings = new LinkedHashSet<String>();

        if (!resolutionAnnotations.isEmpty())
            for (Class<? extends Annotation> resolutionAnnotation : resolutionAnnotations)
                resolutionAnnotationsStrings.add(ReflectUtils.extractAnnotationSimpleName(resolutionAnnotation));
        else
            for (String resolutionAnnotation : disambiguationEventHandlers.keySet())
                resolutionAnnotationsStrings.add(resolutionAnnotation);
    }


    public List<Method> getAnyEventHandlers() {
        return anyEventHandlers;
    }

    public Map<String, Set<Method>> getDisambiguationEventHandlers() {
        return disambiguationEventHandlers;
    }


    public FieldDescriptor getDelegateFieldDescriptor() {
        return delegateFieldDescriptor;
    }

    public boolean isSupported(String eventId) {
        return resolutionAnnotationsStrings.contains(eventId);
    }

    public boolean isAnyHandlingSupported() {
        return !anyEventHandlers.isEmpty();
    }

    public static EventHandlerDelegateImpl newDelegate(FieldDescriptor delegateFieldDescriptor, List<Method> allEventHandlers,
                                Map<String, Set<Method>> disambiguationEventHandlers, Set<Class<? extends Annotation>> resolutionAnnotations) {

        return new EventHandlerDelegateImpl(delegateFieldDescriptor, allEventHandlers, disambiguationEventHandlers, resolutionAnnotations);
    }
}
