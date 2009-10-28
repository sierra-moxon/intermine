package org.intermine.web.struts;

/*
 * Copyright (C) 2002-2009 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.intermine.metadata.ClassDescriptor;
import org.intermine.metadata.FieldDescriptor;
import org.intermine.metadata.Model;
import org.intermine.metadata.ReferenceDescriptor;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.pathquery.Constraint;
import org.intermine.pathquery.Node;
import org.intermine.pathquery.Path;
import org.intermine.pathquery.PathNode;
import org.intermine.pathquery.PathQuery;
import org.intermine.pathquery.PathQueryHelper;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.config.FieldConfig;
import org.intermine.web.logic.config.FieldConfigHelper;
import org.intermine.web.logic.config.WebConfig;
import org.intermine.web.logic.query.MainHelper;
import org.intermine.web.logic.session.SessionMethods;
import org.intermine.web.logic.template.TemplateBuildState;

/**
 * Action to handle links on main query builder tile.
 *
 * @author Mark Woodbridge
 * @author Thomas Riley
 */
public class QueryBuilderChange extends DispatchAction
{
    private static final Logger LOG = Logger.getLogger(QueryBuilderChange.class);

    /**
     * Remove all nodes under a given path
     *
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception
     *                if the application business logic throws
     */
    public ActionForward removeNode(ActionMapping mapping,
                                    @SuppressWarnings("unused") ActionForm form,
                                    HttpServletRequest request,
                                    @SuppressWarnings("unused") HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();

        PathQuery pathQuery = (PathQuery) session.getAttribute(Constants.QUERY);
        String path = request.getParameter("path");

        removeNode(pathQuery, path);
        if (pathQuery.getView().size() > 0) {
            pathQuery.removeFromView(path);
        }
        pathQuery.syncLogicExpression(SessionMethods.getDefaultOperator(session));

        // set prefix and path back to start of the path, so model browser has focus on the top
        String prefix;
        if (MainHelper.getLastJoinIndex(path) == -1) {
            prefix = path;
        } else {
            prefix = path.substring(0, MainHelper.getFirstJoinIndex(path));
            path = path.substring(0, MainHelper.getFirstJoinIndex(path));
        }

        session.setAttribute("prefix", prefix);
        session.setAttribute("path", path);

        return mapping.findForward("query");
    }

    /**
     * Remove the PathNode specified by the given (constraint) path, and it's children, from the
     * PathQuery. Also remove any view nodes would be illegal because they depend on a type
     * constraint that will be removed.
     *
     * @param pathQuery
     *            the PathQuery
     * @param path
     *            the path of the PathNode that should be removed.
     */
    protected static void removeNode(PathQuery pathQuery, String path) {
        // copy because we will be remove paths from the Map as we go
        Set<String> keys = new HashSet<String>(pathQuery.getNodes().keySet());

        // remove the node and it's children
        for (Iterator<String> i = keys.iterator(); i.hasNext();) {
            String testPath = i.next();

            if (testPath.startsWith(path)) {
                removeOneNode(pathQuery, testPath);
            }
        }
    }

    /**
     * Remove the PathNode specified by the given (constraint) path, but not it's children from the
     * PathQuery. Also remove any view nodes would be illegal because they depend on a type
     * constraint that will be removed.
     *
     * @param pathQuery
     *            the PathQuery
     * @param path
     *            the path of the PathNode that should be removed.
     */
    protected static void removeOneNode(PathQuery pathQuery, String path) {
        // ensure removal of any view nodes that depend on a type constraint
        // eg. Department.employees.salary where salary is only defined in a subclass of Employee
        // note that we first have to find out what type Department thinks the employees field is
        // and then check if any of the view nodes assume the field is constrained to a subclass


        String parentType = pathQuery.getNodes().get(path).getParentType();

        Model model = pathQuery.getModel();

        if (parentType != null) {
            ClassDescriptor parentCld;
            try {
                parentCld = MainHelper.getClassDescriptor(parentType, model);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("unexpected exception", e);
            }
            String pathLastField = path.substring(MainHelper.getLastJoinIndex(path) + 1);

            if (parentCld == null) {
                // if the field doesn't exist it means we are editing a PathQuery with errors
            } else {
                FieldDescriptor fd = parentCld.getFieldDescriptorByName(pathLastField);

                if (fd instanceof ReferenceDescriptor) {
                    ReferenceDescriptor rf = (ReferenceDescriptor) fd;
                    ClassDescriptor realClassDescriptor = rf.getReferencedClassDescriptor();

                    List<String> newView = new ArrayList<String>(pathQuery.getViewStrings());
                    List<String> newSortOrder =
                        new ArrayList<String>(pathQuery.getSortOrderStrings());

                    for (String viewPath : pathQuery.getViewStrings()) {
                        if (viewPath.startsWith(path) && !viewPath.equals(path)) {
                            String fieldName = viewPath.substring(path.length() + 1);

                            if (MainHelper.containsJoin(fieldName)) {
                                fieldName = fieldName.substring(0, fieldName.indexOf("."));
                            }

                            if (realClassDescriptor.getFieldDescriptorByName(fieldName) == null) {
                                // the field must be in a sub-class rather than the base class so
                                // remove the viewPath
                                newView.remove(viewPath);
                                newSortOrder.remove(viewPath);
                            }
                        }
                    }
                    if (newView.size() > 0) {
                        pathQuery.setView(newView);
                    }
                    //pathQuery.setOrderBy(newSortOrder);

                    if (pathQuery.getSortOrder() != null) {
                        Path removeSortPath = null;
                        for (Path sortPath : pathQuery.getSortOrder().keySet()) {
                            String sortOrderPath = sortPath.toStringNoConstraints();

                            if (sortOrderPath.startsWith(path) && !sortOrderPath.equals(path)) {
                                String fieldName = sortOrderPath.substring(path.length() + 1);

                                if (MainHelper.containsJoin(fieldName)) {
                                    fieldName = fieldName.substring(0, fieldName.indexOf("."));
                                }

                                FieldDescriptor fieldDescriptor =
                                    realClassDescriptor.getFieldDescriptorByName(fieldName);
                                if (fieldDescriptor == null) {
                                    // the field must be in a sub-class rather than the base class
                                    // so remove the sortPath
                                    removeSortPath = sortPath;
                                }
                            }
                        }
                        if (removeSortPath != null) {
                            pathQuery.getSortOrder().remove(path);
                        }
                    }
                }
            }
        }

        pathQuery.getNodes().remove(path);
        Map<Path, String> sortOrder = pathQuery.getSortOrder();
        pathQuery.getSortOrder().remove(path);
    }

    /**
     * Add a new constraint to this Node
     *
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception
     *                if the application business logic throws
     */
    public ActionForward addConstraint(ActionMapping mapping,
                                       @SuppressWarnings("unused") ActionForm form,
                                       HttpServletRequest request,
                                       @SuppressWarnings("unused") HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        PathQuery query = (PathQuery) session.getAttribute(Constants.QUERY);
        String path = request.getParameter("path");

        session.setAttribute("editingNode", query.getNodes().get(path));

        session.removeAttribute("editingConstraintIndex");
        session.removeAttribute("editingConstraintValue");
        session.removeAttribute("editingConstraintOperand");
        session.removeAttribute("editingConstraintExtraValue");

        return mapping.findForward("query");
    }

    /**
     * Remove a constraint (identified by index) from a Node
     *
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception
     *                if the application business logic throws
     */
    public ActionForward removeConstraint(ActionMapping mapping,
                                          @SuppressWarnings("unused") ActionForm form,
                                          HttpServletRequest request,
                                          @SuppressWarnings("unused") HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        PathQuery query = (PathQuery) session.getAttribute(Constants.QUERY);
        String path = request.getParameter("path");
        int index = Integer.parseInt(request.getParameter("index"));

        query.getNodes().get(path).getConstraints().remove(index);
        query.syncLogicExpression(SessionMethods.getDefaultOperator(session));

        return mapping.findForward("query");
    }

    /**
     * Edit a constraint (identified by index) from a Node
     *
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception
     *                if the application business logic throws
     */
    public ActionForward editConstraint(ActionMapping mapping,
                                        @SuppressWarnings("unused") ActionForm form,
                                        HttpServletRequest request,
                                        @SuppressWarnings("unused") HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        PathQuery query = (PathQuery) session.getAttribute(Constants.QUERY);
        String path = request.getParameter("path");
        int index = Integer.parseInt(request.getParameter("index"));

        session.setAttribute("editingNode", query.getNodes().get(path));
        session.setAttribute("editingConstraintIndex", new Integer(index));

        PathNode pn = query.getNodes().get(path);
        Constraint c = pn.getConstraints().get(index);
        ConstraintOp op = c.getOp();

        if (op != ConstraintOp.IS_NOT_NULL && op != ConstraintOp.IS_NULL) {
            session.setAttribute("editingConstraintValue", c.getDisplayValue());
            session.setAttribute("editingConstraintOperand", c.getOp().getIndex());
            session.setAttribute("editingConstraintExtraValue", c.getExtraValue());
            session.setAttribute("editingConstraintEditable", Boolean.valueOf(c.isEditable()));
        } else {
            session.removeAttribute("editingConstraintValue");
            session.removeAttribute("editingConstraintOperand");
            session.removeAttribute("editingConstraintExtraValue");
            session.removeAttribute("editingConstraintEditable");
        }

        return mapping.findForward("query");
    }

    /**
     * Edit a constraint's template settings (identified by index) from a Node
     *
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception
     *                if the application business logic throws
     */
    public ActionForward editTemplateConstraint(ActionMapping mapping,
                                                @SuppressWarnings("unused") ActionForm form,
                                                HttpServletRequest request,
                                                @SuppressWarnings("unused")
                                                   HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        PathQuery query = (PathQuery) session.getAttribute(Constants.QUERY);
        String path = request.getParameter("path");
        int index = Integer.parseInt(request.getParameter("index"));

        session.setAttribute("editingNode", query.getNodes().get(path));
        session.setAttribute("editingConstraintIndex", new Integer(index));
        session.setAttribute("editingTemplateConstraint", Boolean.TRUE);
        return mapping.findForward("query");
    }

    /**
     * Edit a constraint's join style settings for a path
     *
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception
     *                if the application business logic throws
     */
    public ActionForward editJoinStyle(ActionMapping mapping,
                                                @SuppressWarnings("unused") ActionForm form,
                                                HttpServletRequest request,
                                                @SuppressWarnings("unused")
                                                   HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        PathQuery query = (PathQuery) session.getAttribute(Constants.QUERY);
        String path = request.getParameter("path");
        Node node = query.getNodes().get(path);
        if (node == null) {
            node = query.addNode(path);
        }
        session.setAttribute("joinStylePath", path);
        return mapping.findForward("query");
    }

    /**
     * Add a Node to the query
     *
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception
     *                if the application business logic throws
     */
    public ActionForward addPath(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form,
                                 HttpServletRequest request,
                                 @SuppressWarnings("unused") HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        ObjectStore os = (ObjectStore) servletContext.getAttribute(Constants.OBJECTSTORE);

        PathQuery query = ((PathQuery) session.getAttribute(Constants.QUERY)).clone();
        String prefix = (String) session.getAttribute("prefix");
        String path = request.getParameter("path");

        String prefixWithSubs = getPrefixWithSubclasses(prefix, query, os.getModel());

        // We want an inner join style, as we are about to add a constraint.
        if ((prefixWithSubs != null) && (prefixWithSubs.length() > 0)) {
            if (path.indexOf(".") == -1) {
                path = prefixWithSubs;
            } else {
                path = prefixWithSubs + "." + path.substring(path.indexOf(".") + 1);
            }
        }
        // Now correct the join style deferring to any existing information in the query
        path = query.getCorrectJoinStyle(path);

        // Figure out which path to delete if user cancels operation
        String bits[] = StringUtils.split(path, '.');
        String partialPath = bits[0], deletePath = "";
        for (int i = 1; i < bits.length; i++) {
            partialPath += "." + bits[i];
            if (query.getNodes().get(partialPath) == null) {
                deletePath = partialPath;
                break;
            }
        }

        Node node = query.getNodes().get(path);
        if (node == null) {
            node = query.addNode(path);
        }
        // automatically start editing node
        session.setAttribute("editingNode", node);
        session.removeAttribute("editingConstraintIndex");
        session.removeAttribute("editingConstraintValue");
        session.removeAttribute("editingConstraintOperand");
        session.removeAttribute("editingConstraintExtraValue");

        request.setAttribute("deletePath", deletePath); // for ajax

        return new ForwardParameters(mapping.findForward("query")).addParameter("deletePath",
                                                                                deletePath)
            .forward();
    }

    /**
     * Change the currently active metadata Node
     *
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception
     *                if the application business logic throws
     */
    public ActionForward changePath(ActionMapping mapping,
                                    @SuppressWarnings("unused") ActionForm form,
                                    HttpServletRequest request,
                                    @SuppressWarnings("unused") HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        String path = request.getParameter("path");
        String prefix = request.getParameter("prefix");

        session.setAttribute("path", path);
        if (prefix != null) {
            session.setAttribute("prefix", prefix);
        }

        return new ForwardParameters(mapping.findForward("query")).forward();
    }

    /**
     * Put query builder in template building mode.
     *
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception
     *                if the application business logic throws
     */
    public ActionForward startTemplateBuild(ActionMapping mapping,
                                            @SuppressWarnings("unused") ActionForm form,
                                            HttpServletRequest request,
                                            @SuppressWarnings("unused")
                                                HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        session.setAttribute(Constants.TEMPLATE_BUILD_STATE, new TemplateBuildState());
        return mapping.findForward("query");
    }

    /**
     * Being the query builder out of template building mode and discard any unfinished template
     * building.
     *
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception
     *                if the application business logic throws
     */
    public ActionForward stopTemplateBuild(ActionMapping mapping,
                                           @SuppressWarnings("unused") ActionForm form,
                                           HttpServletRequest request,
                                           @SuppressWarnings("unused") HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        session.removeAttribute(Constants.TEMPLATE_BUILD_STATE);
        return mapping.findForward("query");
    }

    /**
     * Add a Node to the results view
     *
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception
     *                if the application business logic throws
     */
    public ActionForward addToView(ActionMapping mapping,
                                   @SuppressWarnings("unused") ActionForm form,
                                   HttpServletRequest request,
                                   @SuppressWarnings("unused") HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        ObjectStore os = (ObjectStore) servletContext.getAttribute(Constants.OBJECTSTORE);
        Model model = os.getModel();
        WebConfig webConfig = (WebConfig) servletContext.getAttribute(Constants.WEBCONFIG);
        List<Path> view = SessionMethods.getEditingView(session);
        Map<Path, String> sortOrder = SessionMethods.getEditingSortOrder(session);
        String pathName = request.getParameter("path");
        PathQuery query = (PathQuery) session.getAttribute(Constants.QUERY);
        String prefix = (String) session.getAttribute("prefix");

        // we may be adding a long path to the query, there are several outer join considerations:
        // - part of the path may already be in the query, we need to defer to existing join types
        // - there may be several new components added, each one should get the correct default
        //   join type


        // If prefix contains joins there may be subclasses that don't appear in the path, add
        // them here.
        String prefixWithSubs = getPrefixWithSubclasses(prefix, query, model);

        // This call will work out the default join style between prefix and path
        String fullPathName = query.toPathDefaultJoinStyle(prefixWithSubs, pathName);

        // Now correct the join style deferring to any existing information in the query
        fullPathName = query.getCorrectJoinStyle(fullPathName);
        Path path = PathQuery.makePath(model, query, fullPathName);

        // If an object has been selected, select its fields instead
        if (path.getEndFieldDescriptor() == null || path.endIsReference()
            || path.endIsCollection()) {
            ClassDescriptor cld = path.getEndClassDescriptor();
            for (FieldConfig fc : FieldConfigHelper.getClassFieldConfigs(webConfig, cld)) {
                Path pathToAdd = PathQuery.makePath(model, query, query
                                .getCorrectJoinStyle(path.toString() + "." + fc.getFieldExpr()));

                if (pathToAdd.getEndClassDescriptor() == null && !view.contains(pathToAdd)
                    && (fc.getDisplayer() == null && fc.getShowInSummary())) {
                    query.addViewPaths(Collections.singletonList(pathToAdd));
                }
            }
        } else {
            query.addViewPaths(Collections.singletonList(path));
        }

        // if the sort order is empty, sort by the first view element valid for sorting (if any)
        PathQueryHelper.setDefaultSortOrder(query);

        return new ForwardParameters(mapping.findForward("query")).forward();
    }


    /**
     * AJAX request - expand
     *
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception
     *                if the application business logic throws
     */
    public ActionForward ajaxExpand(ActionMapping mapping, ActionForm form,
                                    HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        changePath(mapping, form, request, response);
        QueryBuilderController.populateRequest(request, response);

        // Please improve me - only build relevant Nodes in first place

        List newNodes = new ArrayList();
        Collection nodes = (Collection) request.getAttribute("nodes");
        for (Iterator iter = nodes.iterator(); iter.hasNext();) {
            Node node = (Node) iter.next();
            if (node.getPathString().startsWith(request.getParameter("path") + ".")) {
                newNodes.add(node);
            }
        }
        request.setAttribute("nodes", newNodes);
        request.setAttribute("noTreeIds", Boolean.TRUE);
        return mapping.findForward("browserLines");
    }

    /**
     * AJAX request - collapse
     *
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception
     *                if the application business logic throws
     */
    public ActionForward ajaxCollapse(ActionMapping mapping, ActionForm form,
                                      HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        changePath(mapping, form, request, response);
        return null;
    }

    /**
     * AJAX request - show constraint panel
     *
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception
     *                if the application business logic throws
     */
    public ActionForward ajaxNewConstraint(ActionMapping mapping, ActionForm form,
                                           HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        addPath(mapping, form, request, response);
        return mapping.findForward("mainConstraint");
    }

    /**
     * AJAX request - edit an existing constraint
     *
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception
     *                if the application business logic throws
     */
    public ActionForward ajaxEditConstraint(ActionMapping mapping, ActionForm form,
                                            HttpServletRequest request,
                                            HttpServletResponse response)
        throws Exception {
        editConstraint(mapping, form, request, response);
        return mapping.findForward("mainConstraint");
    }


    /**
     * AJAX request - edit a template constraint
     *
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception
     *                if the application business logic throws
     */
    public ActionForward ajaxEditTemplateConstraint(ActionMapping mapping, ActionForm form,
                                            HttpServletRequest request,
                                            HttpServletResponse response)
        throws Exception {
        editTemplateConstraint(mapping, form, request, response);
        return mapping.findForward("mainConstraint");
    }

    /**
     * AJAX request - render query paths
     *
     * @param mapping
     *            The ActionMapping used to select this instance
     * @param form
     *            The optional ActionForm bean for this request (if any)
     * @param request
     *            The HTTP request we are processing
     * @param response
     *            The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception
     *                if the application business logic throws
     */
    public ActionForward ajaxRenderPaths(ActionMapping mapping,
                                         @SuppressWarnings("unused") ActionForm form,
                                         HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        QueryBuilderController.populateRequest(request, response);
        return mapping.findForward("queryPaths");
    }

    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward ajaxEditJoinStyle(ActionMapping mapping,
                                           @SuppressWarnings("unused") ActionForm form,
                                           HttpServletRequest request, HttpServletResponse response)
                    throws Exception {
        editJoinStyle(mapping, form, request, response);
        return mapping.findForward("mainConstraint");
    }

    // Inspect query to find if prefix is already constrained to a subclass, add subclass
    // constraints to the prefix string - e.g. Department.employees[Manager]
    private String getPrefixWithSubclasses(String prefix, PathQuery query, Model model) {
        String prefixString = "";
        if (prefix != null && MainHelper.containsJoin(prefix)) {
            String prefixJoinStyle = query.getCorrectJoinStyle(prefix);
            //break down in bits to make sure we don't miss any subclass in the path
            StringTokenizer bits = new StringTokenizer(prefixJoinStyle, ".:", true);
            prefixString = bits.nextToken();
            String currentPrefix = prefixString;
            while (bits.hasMoreTokens()) {
                String bitAndSep = bits.nextToken() + bits.nextToken();
                prefixString  += bitAndSep;
                currentPrefix += bitAndSep;
                PathNode prefixNode = query.getNode(currentPrefix);
                if (prefixNode != null) {
                    String parentType = prefixNode.getParentType();

                    ClassDescriptor prefixCld = model.getClassDescriptorByName(parentType);
                    ReferenceDescriptor rfd = (ReferenceDescriptor) prefixCld
                                    .getFieldDescriptorByName(prefixNode.getFieldName());

                    String refType = rfd.getReferencedClassDescriptor().getUnqualifiedName();
                    String prefixType = prefixNode.getType();
                    if (!prefixType.equals(refType)) {
                        prefixString += "[" + prefixType + "]";
                    }
                }
            }
        }
        return prefixString;
    }
}
