package org.intermine.web.logic.query;

/*
 * Copyright (C) 2002-2007 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.intermine.objectstore.query.BagConstraint;
import org.intermine.objectstore.query.ConstraintOp;

import org.intermine.metadata.Model;
import org.intermine.util.StringUtil;
import org.intermine.util.TypeUtil;
import org.intermine.web.logic.ClassKeyHelper;
import org.intermine.web.logic.Constants;

import javax.servlet.ServletContext;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Extension of DefaultHandler to handle parsing PathQuery objects
 * @author Mark Woodbridge
 * @author Kim Rutherford
 * @author Thomas Riley
 */
public class PathQueryHandler extends DefaultHandler
{
    private Map<String, PathQuery> queries;
    private String queryName;
    private char gencode;
    private PathNode node;
    protected PathQuery query;
    private Model model = null;
    private Map savedBags;
    private List<String> viewStrings = new ArrayList<String>();
    private String sortOrderString = new String();
    private String directionString = new String(); // will be asc or desc
    private Map<String, String> pathStringDescriptions = new HashMap<String, String>();
    private final ServletContext servletContext;
    
    /**
     * Constructor
     * @param queries Map from query name to PathQuery
     * @param savedBags Map from bag name to bag
     * @param servletContext global ServletContext object
     */

    public PathQueryHandler(Map<String, PathQuery> queries, Map savedBags,
                            ServletContext servletContext) {
        this.queries = queries;
        this.savedBags = savedBags;
        this.servletContext = servletContext;
    }

    /**
     * {@inheritDoc}
     */
    public void startElement(String uri, String localName, String qName, Attributes attrs)
    throws SAXException {
        if (qName.equals("query")) {
            // reset things
            gencode = 'A';
            queryName = validateName(attrs.getValue("name"));
            try {
                model = Model.getInstanceByName(attrs.getValue("model"));
            } catch (Exception e) {
                throw new SAXException(e);
            }
            query = new PathQuery(model);
            if (attrs.getValue("view") != null) {
                viewStrings = StringUtil.tokenize(attrs.getValue("view"));
                
            }
            if (attrs.getValue("sortField") != null) {
               String[] s = (attrs.getValue("sortField")).split(" ");
               sortOrderString = s[0];
               directionString = s[1];
            }
            if (attrs.getValue("constraintLogic") != null) {
                query.setConstraintLogic(attrs.getValue("constraintLogic"));
            }
        }
        if (qName.equals("node")) {
            node = query.addNode(attrs.getValue("path"));
            if (attrs.getValue("type") != null) {
                node.setType(attrs.getValue("type"));
            }
        }
        if (qName.equals("constraint")) {
            boolean constrainParent = false;
            int opIndex = toStrings(ConstraintOp.getValues()).indexOf(attrs.getValue("op"));
            ConstraintOp constraintOp = ConstraintOp.getOpForIndex(new Integer(opIndex));
            Object constraintValue;
            // If we know that the query is not valid, don't resolve the type of
            // the node as it may not resolve correctly
            if (node.isReference() || !query.isValid()) {
                constraintValue = attrs.getValue("value");
            } else if (BagConstraint.VALID_OPS.contains(constraintOp)) {
                constraintValue = attrs.getValue("value");
                // bag constraints are now only valid on classes.  If this bag
                // constraint is on another field:
                // a) if a key field move it to parent
                // b) otherwise throw an exception to disable query
                if (node.isAttribute()) {
                    Map classKeys = (Map) servletContext.getAttribute(Constants.CLASS_KEYS);
                    if (ClassKeyHelper.isKeyField(classKeys, node.getParentType(), 
                                                  node.getFieldName())) {
                        constrainParent = true;
                    } else {
                        Exception e = new Exception("Invalid bag constraint - only objects can be"
                                + "constrained to be in bags.");
                        query.problems.add(e);
                    }
                }
            } else {
                Class c = null;
                if (model != null && !node.getType().startsWith(model.getPackageName())) {
                    String type = model.getPackageName() + "." + node.getType();
                    try {
                        c = MainHelper.getClass(type);
                    } catch (RuntimeException e) {
                        // ignore - probably a String/BigDecimal etc.
                    }
                }
                if (c == null) {
                    c = MainHelper.getClass(node.getType());
                }
                constraintValue = TypeUtil.stringToObject(c, attrs.getValue("value"));
            }
            String editable = attrs.getValue("editable");
            boolean editableFlag = false;
            if (editable != null && editable.equals("true")) {
                editableFlag = true;
            }
            String description = attrs.getValue("description");
            String identifier = attrs.getValue("identifier");
            String code = attrs.getValue("code");
            if (code == null) {
                code = "" + gencode;
                gencode++;
            }
            if (constrainParent) {
                PathNode parent = (PathNode) node.getParent();
                parent.getConstraints().add(new Constraint(constraintOp, constraintValue,
                                                           editableFlag, description, code,
                                                           identifier));
            } else {
                node.getConstraints().add(new Constraint(constraintOp, constraintValue,
                                                         editableFlag, description, code,
                                                         identifier));
            }
        }
        if (qName.equals("pathDescription")) {
            String pathString = attrs.getValue("pathString");
            String description = attrs.getValue("description");
            pathStringDescriptions.put(pathString, description);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("query")) {
            query.syncLogicExpression("and"); // always and for old queries
            query.checkValidity(savedBags, servletContext);
            for (String viewElement: viewStrings) {
                query.addPathStringToView(viewElement);
            }
            if (sortOrderString.length() > 0 && directionString.length() > 0) {
                query.addPathStringToSortOrder(sortOrderString, directionString);
            }

            for (Map.Entry<String, String> entry: pathStringDescriptions.entrySet()) {
                query.addPathStringDescription(entry.getKey(), entry.getValue());
            }
            queries.put(queryName, query);
            viewStrings = new ArrayList<String>();
           
            pathStringDescriptions = new HashMap<String, String>();
        }
    }
    
    /**
     * Convert a List of Objects to a List of Strings using toString
     * @param list the Object List
     * @return the String list
     */
    protected List<String> toStrings(List list) {
        List<String> strings = new ArrayList<String>();
        for (Iterator i = list.iterator(); i.hasNext();) {
            strings.add(i.next().toString());
        }
        return strings;
    }
    
    /**
     * Checks that the query has a name and that there's no name duplicates
     * and appends a number to the name if there is.
     * @param name the query name
     * @return the validated query name
     */
    protected String validateName(String name) {
        String validatedName = name;
        if (name == null || name.length() == 0) {
            validatedName = "unnamed_query";
        }
        if (queries.containsKey(validatedName)) {
            int i = 1;
            while (true) {
                String testName = validatedName + "_" + i;
                if (!queries.containsKey((testName))) {
                    return testName;
                }
                i++;
            }
        } else {
            return validatedName;
        }
    }
}
