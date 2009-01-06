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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.tiles.ComponentContext;
import org.apache.struts.tiles.actions.TilesAction;
import org.intermine.metadata.ClassDescriptor;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.query.BagConstraint;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.pathquery.Constraint;
import org.intermine.pathquery.MetadataNode;
import org.intermine.pathquery.Path;
import org.intermine.pathquery.PathNode;
import org.intermine.pathquery.PathQuery;
import org.intermine.util.TypeUtil;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.config.FieldConfig;
import org.intermine.web.logic.config.FieldConfigHelper;
import org.intermine.web.logic.config.WebConfig;
import org.intermine.web.logic.query.MainHelper;
import org.intermine.web.logic.session.SessionMethods;

/**
 * Controller for the main query builder tile. Generally, request attributes that are required by
 * multiple tiles on the query builder are synthesized here.
 *
 * @author Mark Woodbridge
 * @author Thomas Riley
 * @see org.intermine.web.struts.QueryBuilderConstraintController
 * @see org.intermine.web.struts.QueryBuilderPathsController
 */
public class QueryBuilderController extends TilesAction
{
    /**
     * {@inheritDoc}
     */
    public ActionForward execute(@SuppressWarnings("unused") ComponentContext context,
                                 @SuppressWarnings("unused") ActionMapping mapping,
                                 @SuppressWarnings("unused") ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
                    throws Exception {
        populateRequest(request, response);
        return null;
    }

    /**
     * Populate the request with the necessary attributes to render the query builder page. This
     * method is static so that it can be called from the AJAX actions in MainChange.java
     *
     * @param request
     *            the current request
     * @param response
     *            the current response
     * {@inheritDoc}
     */
    public static void populateRequest(HttpServletRequest request,
                                       @SuppressWarnings("unused") HttpServletResponse response) {
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        ObjectStore os = (ObjectStore) servletContext.getAttribute(Constants.OBJECTSTORE);
        Model model = os.getModel();
        PathQuery query = (PathQuery) session.getAttribute(Constants.QUERY);
        assureCorrectSortOrder(query);

        // constraint display values
        request.setAttribute("lockedPaths", listToMap(findLockedPaths(query)));
        request.setAttribute("loopPaths", listToMap(findLoopConstraints(query)));
        List<Path> pathView = SessionMethods.getEditingView(session);

        // sort order
        Map<Path, String> sortOrder = SessionMethods.getEditingSortOrder(session);
        // create a map of fields to direction
        Map<String, String> sortOrderMap = new HashMap<String, String>();
        if (sortOrder != null) {
            for (Path path: sortOrder.keySet()) {
                sortOrderMap.put(path.toStringNoConstraints(), sortOrder.get(path));
            }
        }

        Integer sortByIndex = new Integer(0); // sort-by-field's index in the select list

        // select list
        Map<String, String> viewStrings = new LinkedHashMap<String, String>();
        for (Path viewPath: pathView) {
            String viewPathString = viewPath.toStringNoConstraints();
            String sortState = new String();
            // outer joins not allowed
            if (!query.isValidOrderPath(viewPathString)) {
                sortState = "disabled";
            //if already sorted get the direction
            } else if (sortOrderMap.containsKey(viewPathString)) {
                sortState = sortOrderMap.get(viewPathString);
            //default
            } else if (sortOrderMap.size() <= 0 && viewStrings.size() <= 0) {
                sortState = "asc";
            }
            else {
                sortState = "none";
            }
            viewStrings.put(viewPathString, sortState);
        }
        request.setAttribute("viewStrings", viewStrings);
        request.setAttribute("sortByIndex", sortByIndex);

        request.setAttribute("viewPaths", listToMap(new ArrayList(viewStrings.keySet())));

        // set up the metadata
        WebConfig webConfig = (WebConfig) servletContext.getAttribute(Constants.WEBCONFIG);
        boolean isSuperUser = SessionMethods.isSuperUser(session);

        String prefix = (String) session.getAttribute("prefix");
        Collection nodes =
            MainHelper.makeNodes((String) session.getAttribute("path"), model, isSuperUser);
        List<String> dottedViewStrings = query.getDottedViewStrings();
        for (Iterator iter = nodes.iterator(); iter.hasNext();) {
            MetadataNode node = (MetadataNode) iter.next();
            // Update view nodes
            String pathName = node.getPathString();
            int firstDot = pathName.indexOf('.');
            String fullPath;
            if (firstDot == -1) {
                fullPath = prefix;
            } else {
                String pathNameWithoutClass = pathName.substring(firstDot + 1);
                fullPath = prefix + "." + pathNameWithoutClass;
            }
            if (dottedViewStrings.contains(fullPath)) {
                node.setSelected(true);
            } else {
                Path path = new Path(model, pathName);
                // If an object has been selected, select its fields instead
                if (path.getEndFieldDescriptor() == null || path.endIsReference()
                    || path.endIsCollection()) {
                    if (viewStrings.keySet().contains(path)) {
                        ClassDescriptor cld = path.getEndClassDescriptor();
                        List cldFieldConfigs =
                            FieldConfigHelper.getClassFieldConfigs(webConfig, cld);
                        Iterator cldFieldConfigIter = cldFieldConfigs.iterator();
                        while (cldFieldConfigIter.hasNext()) {
                            FieldConfig fc = (FieldConfig) cldFieldConfigIter.next();
                            String pathFromField = pathName + "." + fc.getFieldExpr();
                            if (viewStrings.keySet().contains(pathFromField)) {
                                node.setSelected(true);
                            } else {
                                node.setSelected(false);
                            }
                        }
                    }
                }
            }
        }
        request.setAttribute("nodes", nodes);

        Map<String, String> prefixes = getViewPathLinkPaths(query);
        request.setAttribute("viewPathLinkPrefixes", prefixes);
        request.setAttribute("viewPathLinkPaths", getPathTypes(prefixes.values(), query));

        // set up the navigation links (eg. Department > employees > department)
        String current = null;
        Map<String, String> navigation = new LinkedHashMap<String, String>();
        Map<String, String> navigationPaths = new LinkedHashMap<String, String>();
        if (prefix != null && prefix.indexOf(".") != -1) {
            for (StringTokenizer st = new StringTokenizer(prefix, "."); st.hasMoreTokens();) {
                String token = st.nextToken();
                current = (current == null ? token : current + "." + token);
                navigation.put(token, current);
                navigationPaths.put(token, TypeUtil.unqualifiedName(MainHelper
                    .getTypeForPath(current, query)));
            }
        }
        request.setAttribute("navigation", navigation);
        request.setAttribute("navigationPaths", navigationPaths);
     }


    private static void assureCorrectSortOrder(PathQuery pathQuery) {
        Map<Path, String> newSortOrder = new LinkedHashMap<Path, String>();
        for (Path path : pathQuery.getSortOrder().keySet()) {
            if (pathQuery.getView().contains(path)) {
                newSortOrder.put(path, pathQuery.getSortOrder().get(path));
            }
        }
        pathQuery.setSortOrder(newSortOrder);
    }

    /**
     * Given a input List, return a Map from list element value to list index.
     *
     * @param list
     *            a List
     * @return Map from list element values to list index Integer
     */
    protected static Map createIndexMap(List list) {
        HashMap<Object, Integer> map = new HashMap<Object, Integer>();
        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i), new Integer(i));
        }
        return map;
    }

    /**
     * Get a list of paths that should not be removed from the query by the user. This is usually
     * because they are involved in a loop query constraint.
     *
     * @param pathquery
     *            the PathQuery containing the paths
     * @return list of paths (as Strings) that cannot be removed by the user
     */
    protected static List findLockedPaths(PathQuery pathquery) {
        ArrayList<String> paths = new ArrayList<String>();
        // loop query constraint
        List<PathNode> nodes = findLoopConstraints(pathquery);
        for (PathNode node : nodes) {
            Iterator citer = node.getConstraints().iterator();
            while (citer.hasNext()) {
                Constraint con = (Constraint) citer.next();
                String path = (String) con.getValue();
                // get path and superpaths
                while (path != null) {
                    paths.add(path);
                    if (path.indexOf('.') != -1) {
                        path = path.substring(0, path.lastIndexOf('.'));
                    } else {
                        path = null;
                    }
                }
            }
        }
        return paths;
    }

    /**
     * Return a Map from path to unqualified type name.
     *
     * @param paths
     *            collection of paths
     * @param pathquery
     *            related PathQuery
     * @return Map from path to type
     */
    protected static Map getPathTypes(Collection paths, PathQuery pathquery) {
        Map<String, String> viewPathTypes = new HashMap<String, String>();

        Iterator iter = paths.iterator();

        while (iter.hasNext()) {
            String path = (String) iter.next();
            String unqualifiedName = TypeUtil.unqualifiedName(MainHelper.getTypeForPath(path,
                                                                                        pathquery));
            viewPathTypes.put(path, unqualifiedName);
        }

        return viewPathTypes;
    }

    /**
     * Return a Map from path to path/subpath pointing to the nearest not attribute for each path on
     * the select list. practise this results in the same path or the path with an attribute name
     * chopped off the end.
     *
     * @param pathquery
     *            the path query
     * @return mapping from select list path to non-attribute path
     */
    protected static Map<String, String> getViewPathLinkPaths(PathQuery pathquery) {
        Map<String, String> linkPaths = new HashMap<String, String>();
        Iterator<Path> iter = pathquery.getView().iterator();

        while (iter.hasNext()) {
            Path path = iter.next();
            String pathString = path.toStringNoConstraints();
            if (path.endIsAttribute()) {
                linkPaths.put(pathString, pathString.substring(0, pathString.lastIndexOf(".")));
            } else {
                linkPaths.put(pathString, pathString);
            }
        }

        return linkPaths;
    }

    /**
     * Returns a map where every item in <code>list</code> maps to Boolean TRUE.
     *
     * @param list
     *            the list of map keys
     * @return a map that maps every item in list to Boolean.TRUE
     */
    protected static Map listToMap(List list) {
        Map<Object, Boolean> map = new HashMap<Object, Boolean>();
        int n = list.size();
        for (int i = 0; i < n; i++) {
            map.put(list.get(i), Boolean.TRUE);
        }
        return map;
    }
    
    /**
     * Returns a list of paths with loop constrained for a given query
     * 
     * @param pathQuery the PathQuery
     * @return a list of paths
     */
    protected static List<PathNode> findLoopConstraints(PathQuery pathQuery) {
        ArrayList<PathNode> paths = new ArrayList<PathNode>();
        Iterator iter = pathQuery.getNodes().values().iterator();
        while (iter.hasNext()) {
            PathNode node = (PathNode) iter.next();
            Iterator citer = node.getConstraints().iterator();
            while (citer.hasNext()) {
                Constraint con = (Constraint) citer.next();
                // if a node isn't an attribute and isn't a LOOKUP constraint it can only be a
                // bag constraint or loop constraint
                if (!node.isAttribute() && !BagConstraint.VALID_OPS.contains(con.getOp())
                    && !con.getOp().equals(ConstraintOp.LOOKUP)) {
                    // the parents of this node should also be un-editable loop constraint nodes
                    while (node.getParent() != null) {
                        paths.add(node);
                        node = (PathNode) node.getParent();
                    }
                    // also find the target of the loop constraint and disable join arrows for it
                    PathNode otherNode = pathQuery.getNode((String) con.getValue());
                    paths.add(otherNode);
                    while (otherNode.getParent() != null) {
                        paths.add(otherNode);
                        otherNode = (PathNode) otherNode.getParent();
                    }
                }
            }
        }
        return paths;
    }
}
