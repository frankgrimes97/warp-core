package com.wideplay.warp.internal.components;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Attribute;
import org.dom4j.Node;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.wideplay.warp.module.WarpConfigurationException;
import com.wideplay.warp.module.ComponentRegistry;
import com.wideplay.warp.module.components.ComponentClassReflection;
import com.wideplay.warp.module.components.Renderable;
import com.wideplay.warp.module.components.PropertyDescriptor;
import com.wideplay.warp.core.RawText;
import com.wideplay.warp.rendering.ComponentHandler;
import com.wideplay.warp.util.TextTools;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * On: 17/03/2007
 *
 * Builds ComponentHandlers from a root Dom4j document.
 *
 * @author Dhanji R. Prasanna
 * @since 1.0
 */
class ComponentHandlerBuilder {
    private final ComponentRegistry registry;
    private static final String WARP_PREFIX = "w";  //TODO discover this from document namespace (or just test the namespace directly)

    private final Log log = LogFactory.getLog(getClass());

    public ComponentHandlerBuilder(ComponentRegistry registry) {
        this.registry = registry;
    }

    //builds a component handler tree from an xhtml document
    public ComponentHandler build(Document document) {
        Element rootNode = document.getRootElement();

        //validate that this is an html template
        if (null == rootNode || !"html".equals(rootNode.getName()))
            throw new WarpConfigurationException("Warp can only handle html templates--no html root node was found!");

        //frame is generally built as the top level node
        return buildComponentHandler(rootNode.element("body"));
    }




    //builds a component handler for any node having w: prefix, failing which it is treated as a text node
    private ComponentHandlerImpl buildComponentHandler(Node node) {
        boolean isRawText = false;

        //lookup the component name (we only worry about components marked with warp attribs)
        String componentName = node.valueOf("@w:component");
        log.debug("Discovered node " + componentName + " of type: " + node);

        //we treat text and cdata nodes as RawText type
        if (Node.TEXT_NODE == node.getNodeType() || Node.CDATA_SECTION_NODE == node.getNodeType() || Node.COMMENT_NODE == node.getNodeType()

                //there was no w:component attribute so we treat this ELEMENT as a raw text component
                || (Node.ELEMENT_NODE == node.getNodeType() &&
                (null == componentName || "".equals(componentName.trim())) )   ) {

            log.debug("Text component discovered, building as RawText Component...");
            componentName = ComponentRegistry.TEXT_COMPONENT_NAME;
            isRawText = true;

        } else if (Node.ELEMENT_NODE != node.getNodeType() &&
                (null == componentName || "".equals(componentName.trim())) ) //not an element, text or cdata node, we dont care about it
            return null;



        //lookup registered component and build a reflection
        Class<? extends Renderable> componentClass = registry.getComponent(componentName);
        ComponentClassReflection reflection = new ComponentClassReflectionBuilder(componentClass).build();

        //read warp-specific attributes and store them
        Map<String, PropertyDescriptor> propertyValueExpressions = buildPropertyValues(node, isRawText);

        //read text & arbitrary attributes if necessary and store them
        Map<String, Object> arbitraryAttributes = null;
        if (isRawText)
            arbitraryAttributes = buildArbitraryAttributes(node);

        //build nested components that are embedded below this ELEMENT recursively
        List<ComponentHandlerImpl> nestedComponentHandlers = new LinkedList<ComponentHandlerImpl>();
        if (Node.ELEMENT_NODE == node.getNodeType()) {
            Element element = (Element)node;
            log.debug("Processing child nodes for <" + element.getName() + ">");

            //iterate child nodes and recursively build their handlers
            for (Object object : element.content()) {
                Node child = (Node) object;
    
                ComponentHandlerImpl childHandler = buildComponentHandler(child);
                if (null != childHandler)
                    nestedComponentHandlers.add(childHandler);
            }
        }


        //return when done
        return new ComponentHandlerImpl(reflection, propertyValueExpressions, nestedComponentHandlers, arbitraryAttributes);
    }


    //builds arbitrary attributes (random props that are stuck on to a component--really only for RawText components)
    private Map<String, Object> buildArbitraryAttributes(Node node) {
        Map<String, Object> attribs = new LinkedHashMap<String, Object>();

         //text components have a special property we assign called warpRawText
        attribs.put(RawText.WARP_RAW_TEXT_PROP_TOKENS, TextTools.tokenize(buildRawText(node)));

        //text components that are elements have a special property for <start> and <end> tags
        if (Node.ELEMENT_NODE == node.getNodeType()) {
            attribs.put(RawText.WARP_RAW_TEXT_PROP_TAG, node.getName());

            //text components that are elements have a special property that represents their dom attributes
            Element element = (Element)node;
            List<String> elementAttributes = new ArrayList<String>();
            for (Object object : element.attributes()) {
                Attribute attribute = (Attribute)object;

                //store attributes in a flat list
                elementAttributes.add(attribute.getName());
                elementAttributes.add(attribute.getValue());
            }

            attribs.put(RawText.WARP_RAW_TEXT_PROP_ATTRS, elementAttributes.toArray());
        }

        return attribs;
    }


    //builds w: prefixed attributes into a property-injection map
    private Map<String, PropertyDescriptor> buildPropertyValues(Node node, boolean isRawText) {
        Map<String, PropertyDescriptor> propertyValueExpressions = new LinkedHashMap<String, PropertyDescriptor>();

        if (!isRawText) {
            Element element = (Element)node;
            for (Object object : element.attributes()) {
                Attribute attribute = (Attribute) object;

                //store only w: attribs that are NOT component ids
                if (attribute.getNamespacePrefix().equals(WARP_PREFIX) && !attribute.getName().endsWith("component")) {
                    boolean isExpression = attribute.getValue().startsWith("${");

                    PropertyDescriptor descriptor = new PropertyDescriptor(attribute.getName(),
                            isExpression ? stripOgnlExpression(attribute.getValue()) : attribute.getValue(),
                            isExpression);

                    //store the descriptor by property name
                    propertyValueExpressions.put(descriptor.getName(), descriptor);
                }
            }
        }

        return propertyValueExpressions;
    }

    //builds text that can be output from a RawText component specific to the given node
    private String buildRawText(Node node) {
        if (Node.COMMENT_NODE == node.getNodeType()
            || Node.CDATA_SECTION_NODE == node.getNodeType())
            return node.asXML();    //comments and cdata nodes cant have children

        //elements have their own child text nodes, so return nothing as their body
        if (Node.ELEMENT_NODE == node.getNodeType())
            return "";

        return node.getText();
    }

    public static String stripAttributePrefix(String attr, String prefix) {
        System.out.println(attr + " - " + prefix);
        return attr.substring(prefix.length());
    }

    public static String stripOgnlExpression(String ognl) {
        return ognl.substring(2, ognl.length() - 1);
    }
}