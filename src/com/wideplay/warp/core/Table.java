package com.wideplay.warp.core;

import com.wideplay.warp.annotations.Component;
import com.wideplay.warp.module.components.Renderable;
import com.wideplay.warp.module.components.ClassReflectionCache;
import com.wideplay.warp.module.pages.PageClassReflection;
import com.wideplay.warp.rendering.ComponentHandler;
import com.wideplay.warp.rendering.HtmlWriter;
import com.wideplay.warp.util.beans.BeanUtils;
import com.google.inject.Injector;
import com.google.inject.Inject;

import java.util.List;
import java.util.Map;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * On: 24/03/2007
 *
 * Generates a select box from a collection of values.
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
@Component
public class Table implements Renderable {
    private String items;

    @Inject private ClassReflectionCache classCache;

    public void render(HtmlWriter writer, List<? extends ComponentHandler> nestedComponents, Injector injector, PageClassReflection reflection, Object page) {
        writer.element("table", "border", 1);

        //obtain the bound object
        Object itemsObject = BeanUtils.getFromPropertyExpression(items, page);

        //see if it is an iterable
        if (itemsObject instanceof Iterable) {
            Map<String, String> propertiesAndLabels = null;
            Iterator iter = ((Iterable) itemsObject).iterator();

            boolean isFirst = true;
            while(iter.hasNext()) {
                Object item = iter.next();

                if (isFirst) {
                    propertiesAndLabels = classCache.getPropertyLabelMap(item);
                    writeHeader(writer, propertiesAndLabels);
                    isFirst = false;
                }

                writeRow(item, writer, propertiesAndLabels);
            }

        } else {    //is an array
            Map<String, String> propertiesAndLabels = null;
            Object[] array = ((Object[]) itemsObject);

            for (int i = 0 ; i < array.length; i++) {
                Object item = array[i];

                if (0 == i) {
                    propertiesAndLabels = classCache.getPropertyLabelMap(item);
                    writeHeader(writer, propertiesAndLabels);
                }

                writeRow(item, writer, propertiesAndLabels);
            }
        }
        writer.end("table");
    }

    private void writeHeader(HtmlWriter writer, Map<String, String> propertiesAndLabels) {
        //write out header
        writer.element("tr");
        for (String label : propertiesAndLabels.values()) {
            writer.element("th");
            writer.writeRaw(label);
            writer.end("th");
        }
        writer.end("tr");
    }

    private void writeRow(Object item, HtmlWriter writer, Map<String, String> propertiesAndLabels) {
        writer.element("tr");
        for (String property : propertiesAndLabels.keySet()) {
            writer.element("td");
            writer.writeRaw(BeanUtils.getFromPropertyExpression(property, item).toString());
            writer.end("td");
        }
        writer.end("tr");        
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }
}