package com.wideplay.warp.core;

import com.google.inject.Injector;
import com.google.inject.Inject;
import com.wideplay.warp.annotations.Component;
import com.wideplay.warp.module.components.Renderable;
import com.wideplay.warp.module.pages.PageClassReflection;
import com.wideplay.warp.module.WarpModuleAssembly;
import com.wideplay.warp.rendering.ComponentHandler;
import com.wideplay.warp.rendering.PageHandler;
import com.wideplay.warp.rendering.HtmlWriter;
import com.wideplay.warp.util.beans.BeanUtils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * On: 26/03/2007
 *
 * Viewport is a container component that projects the object bound to "embed" as
 * the current page object to all of its nested components. This object MUST be a top-level page (i.e.
 * must have a matching template and be available to the module).
 *
 * If ajax mode is enabled, Any events fired by nested components are trapped and sent
 * via XHR to the page represented by "embed." The rendered response is then re-inserted
 * as the current content of this viewport.
 *
 * The property expression which is placed in "embed", should be injected into the current containing
 * page via @Inject @Page (the normal way). If the ajax target page sends a redirect request, it is
 * propagated to the containing page as a normal redirect. Thus, forwarding is the only viable option
 * for redirecting WITHIN a viewport.
 *
 * A page ("fragment") wrapped inside a viewport is typically stripped of &lt;body&gt; tags. The page
 * object itself is disambiguated using the containing page's identity as a discriminator (via a request parameter).
 * In this way, a single page can be embedded in multiple viewports concurrently and maintain different
 * states (across different XHRs). This is a bit more RESTful (appearance of statlessness). 
 *
 * Thus, viewport is a slick, transparent way to convert any page into an ajax page-fragment. You can easily
 * see how such fragments, with a bit of behavior abstraction, can be turned into reusable "ajax widgets."
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
@Component
public class Viewport implements Renderable {
    private Object embed; //embedded page
    private boolean ajax;

    @Inject private WarpModuleAssembly assembly;

    public void render(HtmlWriter writer, List<? extends ComponentHandler> nestedComponents, Injector injector, PageClassReflection reflection, Object page) {
        //obtain the embedded page object
        Object embedded = embed;

        //get its component object tree
        PageHandler embeddedPageHandler = assembly.getPage(assembly.resolvePageURI(embedded));

        //strip the frame (or whatever is wrapping) component
        List<? extends ComponentHandler> embeddedContent = embeddedPageHandler.getRootComponentHandler().getNestedComponents();

        if (!ajax)
            //render the embedded content as my children, rather than my own children (which are discarded), using the embedded object as page
            ComponentSupport.renderMultiple(writer, embeddedContent, injector, reflection, embedded);
        else {
            //use an html writer adapter to intercept the event support for the page and turn it into ajax support 
        }
    }


    public boolean isAjax() {
        return ajax;
    }

    public void setAjax(boolean ajax) {
        this.ajax = ajax;
    }

    public void setEmbed(Object embed) {
        this.embed = embed;
    }
}