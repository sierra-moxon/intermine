package org.intermine.web.logic.template;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.intermine.cache.InterMineCache;
import org.intermine.cache.ObjectCreator;
import org.intermine.metadata.ClassDescriptor;
import org.intermine.metadata.Model;
import org.intermine.model.InterMineObject;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.objectstore.ObjectStoreQueryDurationException;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.QueryNode;
import org.intermine.objectstore.query.Results;
import org.intermine.util.TypeUtil;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.ServletMethods;
import org.intermine.web.logic.bag.BagQueryConfig;
import org.intermine.web.logic.bag.InterMineBag;
import org.intermine.web.logic.profile.Profile;
import org.intermine.web.logic.profile.ProfileManager;
import org.intermine.web.logic.query.Constraint;
import org.intermine.web.logic.query.MainHelper;
import org.intermine.web.logic.query.PathNode;
import org.intermine.web.logic.query.PathQuery;
import org.intermine.web.logic.query.SavedQuery;
import org.intermine.web.logic.results.InlineTemplateTable;
import org.intermine.web.logic.results.PagedTable;
import org.intermine.web.logic.results.WebResults;
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
            } else {
                return tq;
            }
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
                                true, c.getDescription(), c.getCode(), c.getIdentifier(),
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
                                new Constraint(constraintOp, constraintValue, true,
                                    c.getDescription(), c.getCode(), c.getIdentifier(),
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
                            new Constraint(constraintOp, constraintValue, true, c.getDescription(),
                                c.getCode(), c.getIdentifier(), extraValue));
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
            Map<String, ConstraintOp> constraintOpsMap, TemplateQuery template, Map savedBags,
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
                    if (bag != null) {
                        Constraint bagConstraint = new Constraint(constraintOp, bag, true,
                                c.getDescription(), c.getCode(), c.getIdentifier(),
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
                    } else { //why would that happen?
                        nodeCopy.getConstraints().set(
                                node.getConstraints().indexOf(c),
                                new Constraint(constraintOp, bag,
                                        true, c.getDescription(), c.getCode(),
                                        c.getIdentifier(), c.getExtraValue()));
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
                            new Constraint(constraintOp, constraintValue, true, c.getDescription(),
                                c.getCode(), c.getIdentifier(), extraValuesMap.get(pathName)));
                    }
                }
            }
        }
        queryCopy.setEdited(true);
        return queryCopy;
    }

    /**
     * Make a Query from a TemplateQuery and the corresponding TemplateForm.
     * @param tq the TemplateQuery
     * @param tf the TemplateForm with values filled in for all editable constraints in tq
     * @return a Query
     */
/* needs testing:
    public Query queryFromTemplateAndForm(TemplateQuery tq, TemplateForm tf) {
        TemplateQuery filledInTemplate =
            templateFormToTemplateQuery(tf, tq, Collections.EMPTY_MAP);
        Map pathToQueryNode = new HashMap();
        return MainHelper.makeQuery(filledInTemplate, Collections.EMPTY_MAP, pathToQueryNode);
    }
*/
    /**
     * Given a Map of TemplateQuerys (mapping from template name to TemplateQuery)
     * return a string containing each template seriaised as XML. The root element
     * will be a <code>template-queries</code> element.
     *
     * @param templates  map from template name to TemplateQuery
     * @return  all template queries serialised as XML
     * @see  TemplateQuery
     */
    public static String templateMapToXml(Map templates) {
        StringWriter sw = new StringWriter();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        Iterator iter = templates.values().iterator();

        try {
            XMLStreamWriter writer = factory.createXMLStreamWriter(sw);
            writer.writeStartElement("template-queries");
            while (iter.hasNext()) {
                TemplateQueryBinding.marshal((TemplateQuery) iter.next(), writer);
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
     * @param servletContext global ServletContext object
     * @return            Map from template name to TemplateQuery
     * @throws Exception  when a parse exception occurs (wrapped in a RuntimeException)
     */
    public static Map xmlToTemplateMap(String xml, Map savedBags, ServletContext servletContext)
    throws Exception {
        Reader templateQueriesReader = new StringReader(xml);
        return new TemplateQueryBinding().unmarshal(templateQueriesReader, savedBags,
                                                    SessionMethods.getClassKeys(servletContext));
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
                                templateForm.setAttributeValues("1", String.valueOf(
                                                                object.getId()));
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


    /**
     * Make and return an InlineTemplateTable for the given template and interMineObjectId or
     * InterMineIdBag
     * @param servletContext servlet context
     * @param template template
     * @param object object
     * @param bag bag
     * @return created template
     */
    public static InlineTemplateTable makeInlineTemplateTable(ServletContext servletContext,
                                                               TemplateQuery template,
                                                               InterMineObject object,
                                                               InterMineBag bag) {
        try {
            TemplateForm templateForm = new TemplateForm();
            ObjectStore os = (ObjectStore) servletContext.getAttribute(Constants.OBJECTSTORE);
            Map webProperties = (Map) servletContext.getAttribute(Constants.WEB_PROPERTIES);

            if (!fillTemplateForm(template, object, bag, templateForm, os.getModel())) {
                return null;
            }

            templateForm.parseAttributeValues(template, null, new ActionErrors(), false);

            PathQuery pathQuery = TemplateHelper.templateFormToTemplateQuery(templateForm, template,
                    new HashMap());

            Map<String, QueryNode> pathToQueryNode = new HashMap<String, QueryNode>();
            Query query = MainHelper.makeQuery(pathQuery, Collections.EMPTY_MAP, pathToQueryNode,
                    servletContext, null, false,
                    (ObjectStore) servletContext.getAttribute(Constants.OBJECTSTORE),
                    (Map) servletContext.getAttribute(Constants.CLASS_KEYS),
                    (BagQueryConfig) servletContext.getAttribute(Constants.BAG_QUERY_CONFIG));
            Results results = os.execute(query);
            Model model = os.getModel();
            WebResults webResults =
                new WebResults(pathQuery, results, model, pathToQueryNode,
                        (Map) servletContext.getAttribute(Constants.CLASS_KEYS), null);
            PagedTable pagedResults = new PagedTable(webResults);

            InlineTemplateTable itt =
                new InlineTemplateTable(pagedResults, webProperties);

            /*Iterator viewIter = viewNodes.iterator();
            while (viewIter.hasNext()) {
                String path = (String) viewIter.next();
                String className = MainHelper.getTypeForPath(path, pathQuery);
                if (className.indexOf(".") == -1) {
                    // a primative like "int"
                } else {
                    Class nodeClass = Class.forName(className);

                    if (InterMineObject.class.isAssignableFrom(nodeClass)) {
                        // can't display objects inline yet
                        //return null;
                    }
                }
            }*/
            return itt;
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ObjectStoreQueryDurationException) {
                // special case: if there is an object store problem it's probably an
                // ObjectStoreQueryDurationException - returning null will cause the template to
                // be run again later when, hopefully, the genetic query optimiser will choose a
                // better plan
                return null;
            }
        } catch (Throwable e) {
            // probably a template is out of date
            LOG.error("error while getting inline template information", e);
        }

        return null;
    }


    /**
     * The cache tag to use when looking for template tables in the cache.
     */
    public static final String TEMPLATE_TABLE_CACHE_TAG = "template_table_tag";

    private static final String NO_USERNAME_STRING = "__NO_USER_NAME__";

    /**
     * Register an ObjectCreator for creating inline template tables.
     * @param cache the InterMineCache
     * @param servletContext the ServletContext
     */
    public static void registerTemplateTableCreator(InterMineCache cache,
                                                    final ServletContext servletContext) {
        ObjectCreator templateTableCreator = new ObjectCreator() {
            final ObjectStore os =
                (ObjectStore) servletContext.getAttribute(Constants.OBJECTSTORE);

            @SuppressWarnings("unused")
            public Serializable create(String templateName, /*String viewName,*/
                                       InterMineBag interMineIdBag, String userName) {
                if (userName.equals(NO_USERNAME_STRING)) {
                    // the create method can't have a null argument, but null is the signal for
                    // findTemplate() that there is no current user
                    userName = null;
                }
                TemplateQuery template =
                    TemplateHelper.findTemplate(servletContext, null, userName,
                                                templateName, TemplateHelper.ALL_TEMPLATE);

                    if (template == null) {
                        throw new IllegalStateException("Could not find template \""
                                                        + templateName + "\"");
                    }

//                InterMineObject object;
//                try {
//                    object = os.getObjectById(id);
//                } catch (ObjectStoreException e) {
//                    throw new RuntimeException("cannot find object for ID: " + id);
//                }
                return makeInlineTemplateTable(servletContext, template, /*viewName,*/ null,
                                               interMineIdBag);
            }
            @SuppressWarnings("unused")
            public Serializable create(String templateName, /*String viewName,*/
                                       Integer id, String userName) {
                if (userName.equals(NO_USERNAME_STRING)) {
                    // the create method can't have a null argument, but null is the signal for
                    // findTemplate() that there is no current user
                    userName = null;
                }
                TemplateQuery template =
                    TemplateHelper.findTemplate(servletContext, null, userName,
                                                templateName, TemplateHelper.ALL_TEMPLATE);

                    if (template == null) {
                        throw new IllegalStateException("Could not find template \""
                                                        + templateName + "\"");
                    }

                InterMineObject object;
                try {
                    object = os.getObjectById(id);
                } catch (ObjectStoreException e) {
                    throw new RuntimeException("cannot find object for ID: " + id);
                }
                return makeInlineTemplateTable(servletContext, template, /*viewName,*/ object,
                                               null);
            }
        };

        cache.register(TEMPLATE_TABLE_CACHE_TAG, templateTableCreator);
    }

    /**
     * Make (or find in the global cache) and return an InlineTemplateTable for the given
     * template, InterMineIdBag and user name.
     * @param servletContext the ServletContext
     * @param templateName the template name
     * @param interMineIdBag the InterMineIdBag
     * @param userName the user name
     * @return the InlineTemplateTable
     */
    public static InlineTemplateTable getInlineTemplateTable(ServletContext servletContext,
                                                             String templateName,
                                                             InterMineBag interMineIdBag,
                                                             String userName) {
        if (userName == null) {
            // the ObjectCreator.create() method can't have a null argument, but null is the signal
            // for findTemplate() that there is no current user
            userName = NO_USERNAME_STRING;
        }

        InterMineCache cache = ServletMethods.getGlobalCache(servletContext);
        return (InlineTemplateTable) cache.get(TemplateHelper.TEMPLATE_TABLE_CACHE_TAG,
                                               templateName, interMineIdBag, userName);
    }

    /**
     * Make (or find in the global cache) and return an InlineTemplateTable for the given
     * template, interMineObjectId and user name.
     * @param servletContext the ServletContext
     * @param templateName the template name
     * @param id the object Id
     * @param userName the user name
     * @return the InlineTemplateTable
     */
    public static InlineTemplateTable getInlineTemplateTable(ServletContext servletContext,
                                                             String templateName,
                                                             Integer id,
                                                             String userName) {
        if (userName == null) {
            // the ObjectCreator.create() method can't have a null argument, but null is the signal
            // for findTemplate() that there is no current user
            userName = NO_USERNAME_STRING;
        }

        InterMineCache cache = ServletMethods.getGlobalCache(servletContext);
        return (InlineTemplateTable) cache.get(TemplateHelper.TEMPLATE_TABLE_CACHE_TAG,
                                               templateName, id, userName);
    }


    /**
     * Clone for operations that need to alter a template but not change the original,
     * for example when removing constraints for precomputing.
     * @param template the query to clone
     * @return a clone of the original template
     */
    // public static TemplateQuery cloneTemplate(TemplateQuery template) {
    // PathQuery queryClone = (PathQuery) template.clone();
    //
    // TemplateQuery clone = new TemplateQuery(template.getName(),
    // template.getDescription(),
    // queryClone, template.isImportant(),
    // template.getKeywords());
    // return clone;
    // }

    /**
     * Get an ObjectStore query to precompute this template - remove editable constraints
     * and add fields to select list if necessary.  Fill in indexes list with QueryNodes
     * to create additional indexes on (i.e. those added to select list).  Original
     * template is left unaltered.
     *
     * @param template to generate precompute query for
     * @param indexes any additional indexes to be created will be added to this list.
     * @return the query to precompute
     */
    public static Query getPrecomputeQuery(TemplateQuery template, List indexes) {
        return TemplateHelper.getPrecomputeQuery(template, indexes, null);
    }


    /**
     * Get an ObjectStore query to precompute this template - remove editable constraints
     * and add fields to select list if necessary.  Fill in indexes list with QueryNodes
     * to create additional indexes on (i.e. those added to select list).  Original
     * template is left unaltered.
     *
     * @param template to generate precompute query for
     * @param indexes any additional indexes to be created will be added to this list.
     * @param groupByNode a PathNode to group by, for summary data, or null for a precompute query
     * @return the query to precompute
     */
    public static Query getPrecomputeQuery(TemplateQuery template, List indexes,
            PathNode groupByNode) {
        // generate query with editable constraints removed
        TemplateQuery templateClone = template.cloneWithoutEditableConstraints();

        if (template.getBagNames().size() != 0) {
            throw new RuntimeException("Precomputed query can't be created "
                + "for template with list. This functionality is not allowed.");
        }

        List<String> indexPaths = new ArrayList<String>();
        // find nodes with editable constraints to index and possibly add to select list
        Iterator niter = template.getEditableNodes().iterator();
        while (niter.hasNext()) {
            PathNode node = (PathNode) niter.next();
            // look for editable constraints
            List ecs = template.getEditableConstraints(node);
            if (ecs != null && ecs.size() > 0) {
                // NOTE: at one point this exhibited a bug where aliases were repeated
                // in the generated query, seems to be fixed now though.
                Iterator ecsIter = ecs.iterator();
                // LOOKUP constraints already add the object (id) to the select list
                // so we don't want to add it again here.while (ecsIter.hasNext()) {
                Constraint c = (Constraint) ecsIter.next();
                String path = node.getPathString();
                if (!c.getOp().equals(ConstraintOp.LOOKUP)) {
                    if (!templateClone.viewContains(path)) {
                        templateClone.getView().add(MainHelper.makePath(templateClone.getModel(),
                                                                        templateClone, path));
                    }
                    if (!indexPaths.contains(path)) {
                        indexPaths.add(path);
                    }
                }
            }
        }

        HashMap<String, QueryNode> pathToQueryNode = new HashMap<String, QueryNode>();
        Query query = null;
        try {
            query = MainHelper.makeQuery(templateClone, new HashMap(), pathToQueryNode, null,
                                         null, false);
        } catch (ObjectStoreException e) {
            // Not possible if last argument is null
        }
        if (groupByNode != null) {
            query.clearOrderBy();
            query.clearSelect();
            QueryNode qn = pathToQueryNode.get(groupByNode.getPathString());
            query.addToSelect(qn);
            query.addToGroupBy(qn);
        } else {
            // Queries only select objects, need to add editable constraints to select so they can
            // be indexed in precomputed table.  Create additional indexes for fields.
            Iterator<String> indexIter = indexPaths.iterator();
            while (indexIter.hasNext()) {
                String path = indexIter.next();
                query.addToSelect(pathToQueryNode.get(path));
            }
            indexes.addAll(query.getSelect());
        }
        return query;
    }
}
