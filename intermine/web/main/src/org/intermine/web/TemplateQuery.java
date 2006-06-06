package org.intermine.web;

/*
 * Copyright (C) 2002-2005 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * A template query, which consists of a PathQuery, description, category,
 * short name.
 *
 * @author Mark Woodbridge
 * @author Thomas Riley
 */
public class TemplateQuery extends PathQuery
{
    /** Template query name. */
    protected String name;
    /** Template query description. */
    protected String description;
    /** Nodes with templated constraints */
    protected List editableNodes = new ArrayList();
    /** Map from node to editable constraint list */
    protected Map constraints = new HashMap();
    /** True if template is considered 'important' for a related class. */
    protected boolean important = false;
    /** Keywords set for this template. */
    protected String keywords = "";

    /**
     * Construct a new instance of TemplateQuery.
     *
     * @param name the name of the template
     * @param description the template description
     * @param query the query itself
     * @param important true if template is important
     * @param keywords keywords for this template
     */
    public TemplateQuery(String name, String description, PathQuery query,
                         boolean important, String keywords) {
        super((PathQuery) query.clone());
        if (description != null) {
            this.description = description;
        }
        if (name != null) {
            this.name = name;
        }
        if (keywords != null) {
            this.keywords = keywords;
        }
        this.important = important;
        // Find the editable constraints in the query.
        Iterator iter = query.getNodes().entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            PathNode node = (PathNode) entry.getValue();
            Iterator citer = node.getConstraints().iterator();
            while (citer.hasNext()) {
                Constraint c = (Constraint) citer.next();
                if (c.isEditable()) {
                    List ecs = (List) constraints.get(node);
                    if (ecs == null) {
                        ecs = new ArrayList();
                        editableNodes.add(node);
                        constraints.put(node, ecs);
                    }
                    ecs.add(c);
                }
            }
        }
    }

    /**
     * For a PathNode with editable constraints, get all the editable
     * constraints as a List.
     *
     * @param node  a PathNode with editable constraints
     * @return      List of Constraints for Node
     */
    public List getConstraints(PathNode node) {
        if (constraints.get(node) == null) {
            return Collections.EMPTY_LIST;
        } else {
            return (List) constraints.get(node);
        }
    }


    /**
     * Return a clone of this template query with all editable constraints
     * removed - i.e. a query that will return all possible results of executing
     * the template.  The original template is left unaltered.
     * @return a clone of the original tempate without editable constraints.
     */
    public TemplateQuery cloneWithoutEditableConstraints() {
        TemplateQuery clone = (TemplateQuery) this.clone();

        // Find the editable constraints in the query.
        Iterator iter = clone.getNodes().entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            PathNode node = (PathNode) entry.getValue();
            Iterator citer = node.getConstraints().iterator();
            while (citer.hasNext()) {
                Constraint c = (Constraint) citer.next();
                if (c.isEditable()) {
                    citer.remove();
                }
            }
        }
        clone.constraints.clear();
        clone.editableNodes.clear();
        
        return clone;
    }


    /**
     * Return a List of all the Constraints of fields in this template query.
     * @return a List of all the Constraints of fields in this template query
     */
    public List getAllEditableConstraints() {
        List returnList = new ArrayList();

        Iterator iter = constraints.values().iterator();

        while (iter.hasNext()) {
            returnList.addAll((List) iter.next());
        }

        return returnList;
    }

    /**
     * Get the tempalte description.
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the nodes from the description, in order (eg. {Company.name})
     * @return the nodes
     */
    public List getEditableNodes() {
        return editableNodes;
    }

    /**
     * Get the query short name.
     * @return the query identifier string
     */
    public String getName() {
        return name;
    }

    /**
     * @return true if template is important
     */
    public boolean isImportant() {
        return important;
    }

    /**
     * Get the keywords.
     * @return template keywords
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * Convert a template query to XML.
     * @return this template query as XML.
     */
    public String toXml() {
        StringWriter sw = new StringWriter();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();

        try {
            XMLStreamWriter writer = factory.createXMLStreamWriter(sw);
            TemplateQueryBinding.marshal(this, writer);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }

        return sw.toString();
    }

    /**
     * Clone this TemplateQuery
     * @return a TemplateQuery
     */
    public Object clone() {
        TemplateQuery templateQuery = new TemplateQuery(name, description,
                                                        (PathQuery) super.clone(), important,
                                                        keywords);
        return templateQuery;
    }

    /**
     * Return the PathQuery part of the TemplateQuery
     * @return a PathQuery
     */
    public PathQuery getPathQuery() {
        return (PathQuery) super.clone();
    }

}
