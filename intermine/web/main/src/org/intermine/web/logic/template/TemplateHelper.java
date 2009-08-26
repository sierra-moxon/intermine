package org.intermine.web.logic.template;

/*
 * Copyright (C) 2002-2009 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.intermine.api.bag.InterMineBag;
import org.intermine.api.profile.Profile;
import org.intermine.api.profile.ProfileManager;
import org.intermine.api.profile.SavedQuery;
import org.intermine.api.template.TemplateQuery;
import org.intermine.api.xml.TemplateQueryBinding;
import org.intermine.metadata.ClassDescriptor;
import org.intermine.metadata.Model;
import org.intermine.model.InterMineObject;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.pathquery.Constraint;
import org.intermine.pathquery.PathNode;
import org.intermine.pathquery.PathQuery;
import org.intermine.util.TypeUtil;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.session.SessionMethods;
import org.intermine.web.struts.TemplateForm;

/**
 * Static helper routines related to templates.
 *
 * @author  Thomas Riley
 */
public class TemplateHelper
{
    private static final Logger LOG = Logger.getLogger(TemplateHelper.class);

    /** Type parameter indicating globally shared template. */
    public static final String GLOBAL_TEMPLATE = "global";
    /** Type parameter indicating private user template. */
    public static final String USER_TEMPLATE = "user";
    /** Type parameter indicating ALL templates */
    public static final String ALL_TEMPLATE = "all";
    /** Type parameter indicating temporary templates **/
    public static final String TEMP_TEMPLATE = "temp";

    /**
     * Locate TemplateQuery by identifier. The type parameter
     * @param servletContext the ServletContext
     * @param session the HttpSession
     * @param userName the user name (for finding user templates)
     * @param templateName  template query identifier/name
     * @param type        type of tempate, either GLOBAL_TEMPLATE, SHARED_TEMPLATE or USER_TEMPLATE,
     *                    ALL_TEMPLATE
     * @return            the located template query with matching identifier
     */
    public static TemplateQuery findTemplate(ServletContext servletContext,
                                             HttpSession session,
                                             String userName,
                                             String templateName,
                                             String type) {

        ProfileManager pm =
            (ProfileManager) servletContext.getAttribute(Constants.PROFILE_MANAGER);
        Profile profile = null;
        if (userName != null) {
            profile = pm.getProfile(userName);
        }
        if (profile == null && session != null) {
            profile = (Profile) session.getAttribute(Constants.PROFILE);
        }
        if (profile == null || GLOBAL_TEMPLATE.equals(type)) {
            Map templates =
                SessionMethods.getSuperUserProfile(servletContext).getSavedTemplates();
            return (TemplateQuery) templates.get(templateName);
        } else if (USER_TEMPLATE.equals(type)) {
            return profile.getSavedTemplates().get(templateName);
        } else if (ALL_TEMPLATE.equals(type)) {
            TemplateQuery tq =
                findTemplate(servletContext, session, userName, templateName, USER_TEMPLATE);
            if (tq == null) {
                return findTemplate(servletContext, session, userName,
                                    templateName, GLOBAL_TEMPLATE);
            }
            return tq;
        } else if (TEMP_TEMPLATE.equals(type)) {
            SavedQuery savedQuery = profile.getHistory().get(templateName);
            TemplateQuery t = null;
            if (savedQuery.getPathQuery() instanceof TemplateQuery) {
                t = (TemplateQuery) savedQuery.getPathQuery();
            } else if (session.getAttribute(Constants.QUERY)
                            instanceof TemplateQuery) {
                // see #1435
                t = (TemplateQuery) session.getAttribute(Constants.QUERY);
            }
            return t;
        } else {
            throw new IllegalArgumentException("findTemplate found bad type: " + type);
        }
    }

    /**
     * Create a new TemplateQuery with input submitted by user contained within
     * a TemplateForm bean.
     *
     * @param tf        the template form bean
     * @param template  the template query involved
     * @param savedBags the saved bags
     * @return          a new TemplateQuery matching template with user supplied constraints
     */
    public static TemplateQuery templateFormToTemplateQuery(TemplateForm tf,
                                                            TemplateQuery template,
                                                            Map savedBags) {
        TemplateQuery queryCopy = (TemplateQuery) template.clone();

        // if this query comes from current query it may have been altered
        // to use a bag constraint

        // Step over nodes and their constraints in order, ammending our
        // PathQuery copy as we go
        int j = 0;
        for (Iterator i = template.getEditableNodes().iterator(); i.hasNext();) {
            PathNode node = (PathNode) i.next();
            for (Iterator ci = template.getEditableConstraints(node).iterator(); ci.hasNext();) {
                Constraint c = (Constraint) ci.next();
                String key = "" + (j + 1);
                PathNode nodeCopy = queryCopy.getNodes().get(node.getPathString());

                if (tf.getUseBagConstraint(key)) {
                    // Replace constraint with bag constraint
                    ConstraintOp constraintOp = ConstraintOp.
                    getOpForIndex(Integer.valueOf(tf.getBagOp(key)));
                    Object constraintValue = tf.getBag(key);
                    // if using an id bag need to swap for a constraint on id
                    InterMineBag bag;
                    if (constraintValue instanceof InterMineBag) {
                        bag = (InterMineBag) constraintValue;
                    } else {
                        bag = (InterMineBag) savedBags.get(constraintValue);
                    }
                    if (bag != null) {
                        Constraint bagConstraint = new Constraint(constraintOp, constraintValue,
                                                                  true, c.getDescription(),
                                                                  c.getCode(), c.getIdentifier(),
                                                                  c.getExtraValue());
                        if (nodeCopy.isAttribute()) {
                            // remove the constraint on this node, possibly remove node
                            //nodeCopy.getConstraints().remove(node.getConstraints().indexOf(c));
                            if (nodeCopy.getConstraints().size() == 1) {
                                queryCopy.getNodes().remove(nodeCopy.getPathString());
                            }
                            // constrain parent object of this node to be in bag
                            PathNode parent = queryCopy.getNodes()
                            .get(nodeCopy.getParent().getPathString());
                            parent.getConstraints().add(bagConstraint);
                        } else {
                            nodeCopy.getConstraints().set(node.getConstraints().indexOf(c),
                                                          bagConstraint);
                        }
                    } else {
                        nodeCopy.getConstraints().set(node.getConstraints().indexOf(c),
                                                      new Constraint(constraintOp, constraintValue,
                                                                     true,
                                                                     c.getDescription(),
                                                                     c.getCode(),
                                                                     c.getIdentifier(),
                                                                     c.getExtraValue()));
                    }

                } else {
                    // Parse user input
                    String op = (String) tf.getAttributeOps(key);
                    ConstraintOp constraintOp = ConstraintOp.getOpForIndex(Integer.valueOf(op));
                    Object constraintValue = tf.getParsedAttributeValues(key);
                    Object extraValue = tf.getExtraValues(key);

                    if (c.getOp().equals(ConstraintOp.LOOKUP)
                                    && constraintOp.equals(ConstraintOp.EQUALS)) {
                        // special case: for inline templates we put the object ID in the form
                        // because we don't want to do a lookup - we already know the object
                        nodeCopy.removeConstraint(c);
                        PathNode newNode = queryCopy.addNode(nodeCopy.getPathString() + ".id");
                        Integer valueAsInteger = Integer.valueOf((String) constraintValue);
                        Constraint objectConstraint =
                            new Constraint(ConstraintOp.EQUALS, valueAsInteger, true,
                                           null, c.getCode(), null, null);
                        newNode.getConstraints().add(objectConstraint);
                    } else {
                        // In query copy, replace old constraint with new one
                        nodeCopy.getConstraints().set(node.getConstraints().indexOf(c),
                                                      new Constraint(constraintOp, constraintValue,
                                                                     true, c.getDescription(),
                                                                     c.getCode(), c.getIdentifier(),
                                                                     extraValue));
                    }
                }
                j++;
            }
        }

        queryCopy.setEdited(true);
        return queryCopy;
    }

    /**
     * Create a new TemplateQuery with input submitted by user contained within
     * maps
     *
     * @param valuesMap a mapping between Paths and values (objects and bags)
     * @param constraintOpsMap a mapping between Paths and ConstraintOps
     * @param template  the template query involved
     * @param savedBags the saved bags
     * @param extraValuesMap extra values map
     * @return          a new TemplateQuery matching template with user supplied constraints
     */
    public static TemplateQuery editTemplate(Map <String, Object> valuesMap,
                                             Map<String, ConstraintOp> constraintOpsMap,
                                             TemplateQuery template, Map savedBags,
                                             Map<String, String> extraValuesMap) {
        TemplateQuery queryCopy = (TemplateQuery) template.clone();
        // Step over nodes and their constraints in order, ammending our
        // copy as we go
        for (Iterator i = template.getEditableNodes().iterator(); i.hasNext();) {
            PathNode node = (PathNode) i.next();
            for (Iterator ci = template.getEditableConstraints(node).iterator(); ci.hasNext();) {
                Constraint c = (Constraint) ci.next();
                String pathName = node.getPathString();
                PathNode nodeCopy = queryCopy.getNodes().get(pathName);
                Object obj = valuesMap.get(pathName);
                ConstraintOp constraintOp = constraintOpsMap.get(pathName);
                if (savedBags != null && savedBags.get(obj) != null) {
                    obj = savedBags.get(obj);
                }
                if (obj instanceof InterMineBag) {
                    // Replace constraint with bag constraint
                    InterMineBag bag = (InterMineBag) obj;
                    Constraint bagConstraint = new Constraint(constraintOp, bag, true,
                                                              c.getDescription(), c.getCode(),
                                                              c.getIdentifier(),
                                                              c.getExtraValue());
                    if (nodeCopy.isAttribute()) {
                        // remove the constraint on this node, possibly remove node
                        //nodeCopy.getConstraints().remove(node.getConstraints().indexOf(c));
                        if (nodeCopy.getConstraints().size() == 1) {
                            queryCopy.getNodes().remove(nodeCopy.getPathString());
                        }
                        // constrain parent object of this node to be in bag
                        PathNode parent = queryCopy.getNodes()
                        .get(nodeCopy.getParent().getPathString());
                        parent.getConstraints().add(bagConstraint);
                    } else {
                        nodeCopy.getConstraints().set(node.getConstraints().indexOf(c),
                                                      bagConstraint);
                    }
                } else {
                    // Parse user input
                    Object constraintValue = obj;

                    if (c.getOp().equals(ConstraintOp.LOOKUP)
                                    && constraintOp.equals(ConstraintOp.EQUALS)) {
                        // special case: for inline templates we put the object ID in the form
                        // because we don't want to do a lookup - we already know the object
                        nodeCopy.removeConstraint(c);
                        PathNode newNode = queryCopy.addNode(nodeCopy.getPathString() + ".id");
                        Integer valueAsInteger = Integer.valueOf((String) constraintValue);
                        Constraint objectConstraint =
                            new Constraint(ConstraintOp.EQUALS, valueAsInteger, true,
                                           null, c.getCode(), null, null);
                        newNode.getConstraints().add(objectConstraint);
                    } else {
                        // In query copy, replace old constraint with new one
                        nodeCopy.getConstraints().set(node.getConstraints().indexOf(c),
                                                      new Constraint(constraintOp, constraintValue,
                                                                     true, c.getDescription(),
                                                                     c.getCode(), c.getIdentifier(),
                                                                     extraValuesMap.get(pathName)));
                    }
                }
            }
        }
        queryCopy.setEdited(true);
        return queryCopy;
    }


    /**
     * Given a Map of TemplateQuerys (mapping from template name to TemplateQuery)
     * return a string containing each template seriaised as XML. The root element
     * will be a <code>template-queries</code> element.
     *
     * @param templates  map from template name to TemplateQuery
     * @param version the version number of the XML format
     * @return  all template queries serialised as XML
     * @see  TemplateQuery
     */
    public static String templateMapToXml(Map templates, int version) {
        StringWriter sw = new StringWriter();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        Iterator iter = templates.values().iterator();

        try {
            XMLStreamWriter writer = factory.createXMLStreamWriter(sw);
            writer.writeStartElement("template-queries");
            while (iter.hasNext()) {
                TemplateQueryBinding.marshal((TemplateQuery) iter.next(), writer, version);
            }
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }

        return sw.toString();
    }

    /**
     * Parse templates in XML format and return a map from template name to
     * TemplateQuery.
     *
     * @param xml         the template queries in xml format
     * @param savedBags   Map from bag name to bag
     * @param version the version of the xml format, an attribute on ProfileManager
     * @return            Map from template name to TemplateQuery
     * @throws Exception  when a parse exception occurs (wrapped in a RuntimeException)
     */
    public static Map xmlToTemplateMap(String xml, Map<String, InterMineBag> savedBags,
            int version) throws Exception {
        Reader templateQueriesReader = new StringReader(xml);
        return new TemplateQueryBinding().unmarshal(templateQueriesReader, savedBags, version);
    }

    /**
     * Build a template query given a TemplateBuildState and a PathQuery
     *
     * @param tbs the template build state
     * @param query the path query
     * @return a template query
     */
    public static TemplateQuery buildTemplateQuery(TemplateBuildState tbs, PathQuery query) {
        TemplateQuery template = new TemplateQuery(tbs.getName(),
                                                   tbs.getTitle(),
                                                   tbs.getDescription(),
                                                   tbs.getComment(),
                                                   query.clone(),
                                                   tbs.getKeywords());
        return template;
    }


    /**
     * Try to fill the TemplateForm argument using the attribute values in the InterMineObject
     * arg and return true if successful (ie. all constraints are filled in)
     * @param template template
     * @param object object
     * @param bag bag
     * @param templateForm template form
     * @param model model
     * @return true if successfull
     */
    public static boolean fillTemplateForm(TemplateQuery template, InterMineObject object,
                                           InterMineBag bag, TemplateForm templateForm,
                                           Model model) {
        String equalsString = ConstraintOp.EQUALS.getIndex().toString();
        String inString = ConstraintOp.IN.getIndex().toString();

        int editableConstraintCount = template.getAllEditableConstraints().size();
        if (editableConstraintCount > 1) {
            return false;
        }

        for (Map.Entry<String, PathNode> entry: template.getNodes().entrySet()) {
            PathNode pathNode = entry.getValue();
            for (Constraint c: pathNode.getConstraints()) {
                if (!c.isEditable()) {
                    // this constraint doesn't need to be filled in
                    continue;
                }

                try {
                    if (c.getOp().equals(ConstraintOp.LOOKUP)) {
                        String pathNodeType = model.getPackageName() + "." + pathNode.getType();
                        if (object == null) {
                            Class bagClass = Class.forName(bag.getQualifiedType());
                            Class pathNodeClass = Class.forName(pathNodeType);
                            if (pathNodeClass.isAssignableFrom(bagClass)) {
                                templateForm.setBagOp("1", inString);
                                templateForm.setBag("1", bag);
                                templateForm.setUseBagConstraint("1", true);
                                return true;
                            }
                        } else {
                            if (TypeUtil.isInstanceOf(object, pathNodeType)) {
                                templateForm.setAttributeOps("1", equalsString);
                                templateForm.setAttributeValues("1",
                                                                String.valueOf(object.getId()));
                                return true;
                            }
                        }
                    } else if (c.getOp().equals(ConstraintOp.EQUALS)
                                    || c.getOp().equals(ConstraintOp.IN)) {
                        String constraintIdentifier = c.getIdentifier();
                        String[] bits = constraintIdentifier.split("\\.");

                        if (bits.length == 2) {
                            String className = model.getPackageName() + "." + bits[0];
                            String fieldName = bits[1];

                            Class testClass = Class.forName(className);

                            if (object != null && testClass.isInstance(object)) {
                                ClassDescriptor cd = model.getClassDescriptorByName(className);
                                if (cd.getFieldDescriptorByName(fieldName) != null) {
                                    Object fieldValue = TypeUtil.getFieldValue(object, fieldName);

                                    if (fieldValue != null) {
                                        templateForm.setAttributeOps("1", equalsString);
                                        templateForm.setAttributeValues("1", fieldValue);
                                        return true;
                                    }
                                }
                            }
                            String unqualifiedName = TypeUtil.unqualifiedName(testClass.toString());
                            if (bag != null && unqualifiedName.equals(bag.getType())) {
                                templateForm.setBagOp("1", inString);
                                templateForm.setBag("1", bag);
                                templateForm.setUseBagConstraint("1", true);
                                return true;
                            }

                        }
                    } else {
                        LOG.error("Constraint error:" + c.getOp());
                        return false;
                    }
                } catch (ClassNotFoundException e) {
                    LOG.error(e);
                } catch (IllegalAccessException e) {
                    LOG.error(e);

                }
            }
        }
        return false;
    }



}
