package com.wideplay.warp.internal.pages;

import com.wideplay.warp.rendering.ComponentHandler;
import com.wideplay.warp.rendering.PageHandler;
import com.wideplay.warp.annotations.Template;
import com.wideplay.warp.annotations.URIMapping;
import com.wideplay.warp.module.WarpConfigurationException;
import com.wideplay.warp.module.ComponentRegistry;
import com.wideplay.warp.internal.components.ComponentBuilders;
import com.wideplay.warp.util.TextTools;

import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;

/**
 * Created with IntelliJ IDEA.
 * On: 20/03/2007
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
class PageHandlerBuilder {
    private ServletContext context;

    private final Log log = LogFactory.getLog(getClass());

    public PageHandlerBuilder(ServletContext context) {
        this.context = context;
    }

    public void build(Class<?> pageClass, String packageName, Map<String, PageHandler> pages) {
        //resolve template name from class name
        String template = pageClass.getName().substring(packageName.length() + 1);   //remove leading "."

        //use declared template name
        boolean isSearchForTemplate = true;
        if (pageClass.isAnnotationPresent(Template.class)) {
            template = pageClass.getAnnotation(Template.class).name();
            isSearchForTemplate = false;
        }

        String documentText = loadTemplateText(template, pageClass, isSearchForTemplate);

        //no template was found for the class, so skip it
        if (null == documentText)
            return;

        Document document;
        try {
            document = DocumentHelper.parseText(documentText);
        } catch (DocumentException e) {
            throw new WarpConfigurationException("could not parse xhtml template for: " + template, e);
        }

        String[] uris;

        //check for the explicit presence of a URI mapping or use the conventional name
        if (pageClass.isAnnotationPresent(URIMapping.class)) {
            uris = pageClass.getAnnotation(URIMapping.class).value();

            //validate uris
            for (String uri : uris)
                if (!TextTools.isValidURI(uri))
                    throw new WarpConfigurationException(pageClass.getName() + " specified an invalid URI mapping: " + uri);
        }
        else
            uris = new String[] { "/" + template };

        //store different instances of the pagehandler for the given URIs
        for (String uri : uris)
            pages.put(uri, new PageHandlerImpl(uri, new PageClassReflectionBuilder(pageClass).build(), buildComponentHandler(document)));
    }


    private ComponentHandler buildComponentHandler(Document document) {
        return ComponentBuilders.buildComponentHandler(new ComponentRegistry(), document);
    }


    


    //smartly loads a template from the context filesystem
    private String loadTemplateText(String template, Class<?> pageClass, boolean isSearchForTemplate) {
        String documentText = null;


        //if template is specified literally, no need to search for it
        if (!isSearchForTemplate) {
            try {
                documentText = FileUtils.readFileToString(new File(context.getRealPath(template)), null);
            } catch (IOException e) {
                throw new WarpConfigurationException("could not find/read template for: " + template, e);
            }

            return documentText;
        }



        //otherwise, try to search & load either an .xhtml or .html file as a matching template
        try {
            documentText = FileUtils.readFileToString(new File(context.getRealPath(template + ".html")), null);
        } catch (FileNotFoundException fnfe) {
            //try the .html now
            try {
                documentText = FileUtils.readFileToString(new File(context.getRealPath(template + ".xhtml")), null);
            } catch (IOException e) {
                log.info("Class found that did not have a matching template (not an error or warning, just letting u know) : " + pageClass.getName());
            }

        } catch (IOException e) {
            throw new WarpConfigurationException("could not find/read template for: " + template, e);
        }

        return documentText;
    }
}