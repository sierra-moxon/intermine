package org.intermine.pathquery;

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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.intermine.metadata.ClassDescriptor;
import org.intermine.metadata.FieldDescriptor;
import org.intermine.metadata.Model;
import org.intermine.metadata.ReferenceDescriptor;
import org.intermine.objectstore.query.BagConstraint;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.objectstore.query.ResultsInfo;
import org.intermine.util.CollectionUtil;
import org.intermine.util.StringUtil;

/**
 * Class to represent a path-based query.
 *
 * @author Mark Woodbridge
 * @author Thomas Riley
 */
public class PathQuery
{
    private static final Logger LOG = Logger.getLogger(PathQuery.class);
    private Model model;
    protected LinkedHashMap<String, PathNode> nodes = new LinkedHashMap<String, PathNode>();
    private List<Path> view = new ArrayList<Path>();
    private Map<Path, String> sortOrder = new LinkedHashMap<Path, String>();
    private ResultsInfo info;
    private List<Throwable> problems = new ArrayList<Throwable>();
    protected LogicExpression constraintLogic = null;
    private Map<Path, String> pathDescriptions = new HashMap<Path, String>();
    protected String description;
    private static final String MSG = "Invalid path - path cannot be a null or empty string";

    /**
     * Version number for the userprofile and PathQuery XML format
     */
    public static final int USERPROFILE_VERSION = 1;

    /**
     * Ascending sort order.
     */
    public static final String ASCENDING = "asc";

    /**
     * Descending sort order.
     */
    public static final String DESCENDING = "desc";

    /**
     * Construct a new instance of PathQuery.
     * @param model the Model on which to base this query
     */
    public PathQuery(Model model) {
        this.model = model;
    }

    /**
     * Construct a new instance of PathQuery from an existing
     * instance.
     * @param query the existing query
     */
    public PathQuery(PathQuery query) {
        this.model = query.model;
        this.nodes = new LinkedHashMap<String, PathNode>(query.nodes);
        this.view = new ArrayList<Path>(query.view);
        this.sortOrder = new HashMap<Path, String>(query.sortOrder);
        this.info = query.info;
        this.problems = new ArrayList<Throwable>(query.problems);
        this.constraintLogic = query.constraintLogic;
        this.pathDescriptions = new HashMap<Path, String>(query.pathDescriptions);
    }


    /*****************************************************************************************/


    private void validateView(List<Path> viewList) {
        Iterator it = viewList.iterator();
        while (it.hasNext()) {
            Path path = (Path) it.next();
            if (path == null || path.equals("")) {
                it.remove();
            }
        }
    }

    private List<Path> makePaths(List<String> viewStrings) {
        List<Path> viewPaths = new ArrayList<Path>();
        Iterator it = viewStrings.iterator();
        while (it.hasNext()) {
            String pathString = (String) it.next();
            if (pathString == null || pathString.trim().equals("")) {
                logPathException(MSG);
                continue;
            }
            Path path = null;
            try {
                path = makePath(model, this, pathString.trim());
            } catch (PathException e) {
                logPathException(e);
            }
            if (path != null) {
                viewPaths.add(path);
            }
        }
        return viewPaths;
    }

    /**
     * Sets the select list of the query to the list of paths given.  Paths can be a single path
     * or a comma or space delimited list of paths.  To append a path to the list instead use
     * addView.
     * @param paths a list of paths to be the view list
     */
    public void setView(String paths) {
        if (paths == null || paths.equals("")) {
            logPathException(MSG);
            return;
        }
        String [] pathStrings = paths.split("[, ]+");
        setView(new ArrayList<String>(Arrays.asList(pathStrings)));
        validateSortOrder();
    }

    /**
     * Clears the view list and sets the value of view to the list of strings given
     * @param viewStrings a list of strings.
     */
    public void setView(List<String> viewStrings) {
        if (viewStrings.isEmpty()) {
            logPathException(MSG);
            return;
        }
        setViewPaths(makePaths(viewStrings));
        validateSortOrder();
    }

    /**
     * Sets the value of view
     * @param view a List of Paths
     */
    public void setViewPaths(List<Path> view) {
        validateView(view);
        this.view = view;
        // sets subclasses nodes
        for (Path path : view) {
            for (Map.Entry<String, String> entry
                            : path.getSubClassConstraintPaths().entrySet()) {
                String stringPath = entry.getKey();
                PathNode node = addNode(getCorrectJoinStyle(stringPath));
                node.setType(entry.getValue());
            }
        }
        validateSortOrder();
    }

    /**
     * Change the join style of a path in the query. All children will also be updated.
     * The path must not end by an attribute.
     *
     * @param path a path with the old join style
     * @return the path, flipped
     */
    public String flipJoinStyle(String path) {
        int lastDotIndex = path.lastIndexOf('.');
        int lastColonIndex = path.lastIndexOf(':');
        if (lastDotIndex > lastColonIndex) {
            return updateJoinStyle(path, true);
        } else {
            return updateJoinStyle(path, false);
        }
    }

    /**
     * Change the join style of a path in the query. All children will also be updated.
     * The path must not end by an attribute.
     *
     * @param path a path with the old join style
     * @param outer whether the update should result in an outter join
     * @return the path, flipped
     */
    public String updateJoinStyle(String path, boolean outer) {
        String oldPath = getCorrectJoinStyle(path);
        if (!oldPath.equals(path)) {
            throw new IllegalArgumentException("Path not found in query: " + path);
        }

        String newPathString;
        int lastDotIndex = path.lastIndexOf('.');
        int lastColonIndex = path.lastIndexOf(':');
        int lastIndex = (lastDotIndex > lastColonIndex ? lastDotIndex : lastColonIndex);
        if (outer) {
            newPathString = path.substring(0, lastIndex) + ':'
                + path.substring(lastIndex + 1);
        } else {
            newPathString = path.substring(0, lastIndex) + '.'
                + path.substring(lastIndex + 1);
        }

        Pattern p = Pattern.compile(path.replaceAll("\\.", "\\\\.") + "((\\.|:)\\w+)*");
        List<Path> newView = new ArrayList<Path>();
        for (Path viewPath : view) {
            String viewPathString = viewPath.toStringNoConstraints();
            Matcher m = p.matcher(viewPathString);
            if (m.matches()) {
                viewPathString = newPathString + viewPathString.substring(path.length());
            }
            try {
                newView.add(new Path(model, viewPathString, viewPath.getSubClassConstraintPaths()));
            } catch (PathException e) {
                // Should never happen, but we should raise hell if it does.
                throw new Error("There must be a bug", e);
            }
        }
        view = newView;

        Map<Path, String> newPathDescriptions = new HashMap<Path, String>();
        for (Map.Entry<Path, String> entry : pathDescriptions.entrySet()) {
            String descPathString = entry.getKey().toStringNoConstraints();
            Matcher m = p.matcher(descPathString);
            if (m.matches()) {
                descPathString = newPathString + descPathString.substring(path.length());
            }
            Path newPath;
            try {
                newPath = new Path(model, descPathString, entry.getKey()
                        .getSubClassConstraintPaths());
            } catch (PathException e) {
                // Should never happen, but we should raise hell if it does.
                throw new Error("There must be a bug", e);
            }
            newPathDescriptions.put(newPath, entry.getValue());
        }
        pathDescriptions = newPathDescriptions;

        PathNode node = getNode(path);
        if (node != null) {
            node.setOuterJoin(outer);
        }

        Map<String, PathNode> origNodes = getNodes();
        List<PathNode> nodes = new ArrayList(origNodes.values());
        origNodes.clear();
        for (PathNode transferNode : nodes) {
            origNodes.put(transferNode.getPathString(), transferNode);
        }

        // Fix up any loop constraints
        for (PathNode loopNode : getNodes().values()) {
            if (!loopNode.isAttribute()) {
                for (Constraint con : loopNode.getConstraints()) {
                    if ((!BagConstraint.VALID_OPS.contains(con.getOp()))
                            && (!con.getOp().equals(ConstraintOp.LOOKUP))) {
                        // We have found a loop constraint.
                        String toPath = (String) con.getValue();
                        toPath = getCorrectJoinStyle(toPath);
                        con.setValue(toPath);
                    }
                }
            }
        }

        // remove any invalid paths from sort order - outer join paths aren't valid for sorting
        validateSortOrder();
        syncLogicExpression("and");

        return newPathString;
    }

    private void validateSortOrder() {
        Iterator<Path> iter = sortOrder.keySet().iterator();
        while (iter.hasNext()) {
            if (!isValidOrderPath(iter.next().toStringNoConstraints())) {
                iter.remove();
            }
        }
    }


    /**
     * Set the joins style of an entire given path to outer/normal.  This changes all joins in the
     * path, updating all the relevant nodes and view elements.
     * e.g. call with (Company:departments.manager, false) -&gt; Company.departments.manager
     * @param path the path to set the join style for
     * @param outer if true change the join style to outer, otherwise to normal
     * @return the updated path
     */
    public String setJoinStyleForPath(String path, boolean outer) {
        String oldPath = getCorrectJoinStyle(path);
        if (!oldPath.equals(path)) {
            throw new IllegalArgumentException("Path not found in query: " + path);
        }

        // iterate over view and set join style
        List<Path> newView = new ArrayList<Path>();
        for (Path viewPath : view) {
            String prefix = viewPath.toStringNoConstraints();
            String lastElement = "";
            if (viewPath.endIsAttribute()) {
                prefix = viewPath.getPrefix().toStringNoConstraints();
                lastElement = "." + viewPath.getLastElement();
            }
            String newPathStr = viewPath.toStringNoConstraints();
            if (path.startsWith(prefix)) {
                if (outer) {
                    newPathStr = prefix.replace('.', ':') + lastElement;
                } else {
                    newPathStr = prefix.replace(':', '.') + lastElement;
                }
            } else if (prefix.startsWith(path)) {
                if (outer) {
                    newPathStr = path.replace('.', ':') + prefix.substring(path.length())
                        + lastElement;
                } else {
                    newPathStr = path.replace(':', '.') + prefix.substring(path.length())
                        + lastElement;
                }
            }
            try {
                newView.add(new Path(model, newPathStr, viewPath.getSubClassConstraintPaths()));
            } catch (PathException e) {
                // Shouldn't ever happen
                throw new Error("There must be a bug", e);
            }
        }
        view = newView;

        // This is a really round-about way to update the join style of each node.  Nodes are stored
        // in a map by their path so they need to be removed and re-added to the map.  This has to
        // be done at the end because updating a parent node alters the path of its children.
        PathNode node = getNode(path);
        List<PathNode> newNodes = new ArrayList<PathNode>();
        PathNode parent = node;
        while ((parent.getParent()) != null) {
            nodes.remove(parent.getPathString());
            parent.setOuterJoin(outer);
            newNodes.add(parent);
            parent = (PathNode) parent.getParent();
        }
        // Also, all the other nodes need to be transferred in the Nodes map, because their paths
        // may have changed.
        for (PathNode nextNode : nodes.values()) {
            newNodes.add(nextNode);
        }
        // now all paths are set can add to nodes map again
        for (PathNode nextNode : newNodes) {
            nodes.put(nextNode.getPathString(), nextNode);
        }
        syncLogicExpression("and");
        return node.getPathString();
    }

    /**
     * Appends the paths to the end of the select list. Paths can be a single path
     * or a comma delimited list of paths.
     * @param paths a list of paths to be appended to the end of the view list
     */
    public void addView(String paths) {
        if (paths == null || paths.equals("")) {
            logPathException(MSG);
            return;
        }
        String [] pathStrings = paths.split(",");
        addView(new ArrayList<String>(Arrays.asList(pathStrings)));
    }

    /**
     * Appends the paths to the end of the select list, ignores any bad paths.
     *
     * @param pathStrs a list of paths to be appended to the end of the view list
     */
    public void addView(List<String> pathStrs) {
        List<Path> paths = makePaths(pathStrs);
        addViewPaths(paths);
    }


    /**
     * Appends the paths to the end of the select list.
     * @param paths a list of paths to be appended to the end of the view list
     */
    public void addViewPaths(List<Path> paths) {
        validateView(paths);
        if (paths.isEmpty()) {
            logPathException(MSG);
            return;
        }

        for (Path p : paths) {
            String path = p.toStringNoConstraints();
            if (!getCorrectJoinStyle(path).equals(path)) {
                throw new IllegalArgumentException("Adding two join types for same path: "
                        + path + " and " + getCorrectJoinStyle(path));
            }
            view.add(p);
        }
    }

    /**
     * Convert a path and prefix to a path.
     *
     * @param prefix the prefix (eg null or Department.company)
     * @param path the path (eg Company, Company.departments)
     * @return the new path
     * @throws PathException if the given path is invalid
     */
    public String toPathDefaultJoinStyle(String prefix, String path) throws PathException {
        if (prefix != null && prefix.length() > 0) {
            if (path.indexOf(".") == -1) {
                path = prefix;
            } else {
                path = prefix + "." + path.substring(path.indexOf(".") + 1);
            }
        }
        return toPathDefaultJoinStyle(path);
    }

    /**
     * Given a path through the model set each join to outer/normal according to the defaults:
     *  - collections and references are outer joins.
     * e.g. Company.departments.name -&gt; Company:departments.name
     *
     * @param path the path to resolve
     * @return the new path
     * @throws PathException if the given path is invalid
     */
    public String toPathDefaultJoinStyle(String path) throws PathException {

        // this will validate the path so we don't have to here
        Path dummyPath = makePath(model, this, path);

        String parts[] = dummyPath.toString().split("[.:]");

        StringBuffer currentPath = new StringBuffer();
        currentPath.append(parts[0]);
        String clsName = model.getPackageName() + "." + parts[0];


        for (int i = 1; i < parts.length; i++) {
            String thisPart = parts[i];

            ClassDescriptor cld = model.getClassDescriptorByName(clsName);

            // cope with sub types specified by [type] in the path, extract the subType and use
            // this to check for fields in the model
            String subType = null;

            if (thisPart.indexOf("[") >= 0) {
                String tmp = thisPart;
                thisPart = tmp.substring(0, tmp.indexOf("["));
                subType = tmp.substring(tmp.indexOf("[") + 1, tmp.indexOf("]"));
            }

            FieldDescriptor fld = cld.getFieldDescriptorByName(thisPart);
            if (fld.isCollection()
                || fld.isReference()
                && (getNode(dummyPath.getPrefix().toStringNoConstraints()) == null
                    || ((getNode(dummyPath.getPrefix().toStringNoConstraints()).getConstraints()
                            == null)
                        || (getNode(dummyPath.getPrefix().toStringNoConstraints()).getConstraints()
                            .size() <= 0)))) {
                currentPath.append(":");
            } else {
                currentPath.append(".");
            }

            currentPath.append(thisPart);
            // if an attribute this will be the end of the path, otherwise get the class of this
            // path element
            if (!fld.isAttribute()) {
                if (subType != null) {
                    // subclass, so find actual class
                    clsName = model.getClassDescriptorByName(subType).getName();
                } else {
                    ReferenceDescriptor rfd = (ReferenceDescriptor) fld;
                    clsName = rfd.getReferencedClassName();
                }
            }
        }
        return currentPath.toString();
    }

    /**
     * Add a path to the view
     * @param viewString the String version of the path to add - should not include any class
     * constraints (ie. use "Departement.employee.name" not "Departement.employee[Contractor].name")
     */
    @Deprecated public void addPathStringToView(String viewString) {
        try {
            if (!getCorrectJoinStyle(viewString).equals(viewString)) {
                throw new IllegalArgumentException("Adding two join types for same path: "
                        + viewString + " and " + getCorrectJoinStyle(viewString));
            }
            view.add(PathQuery.makePath(model, this, viewString));
        } catch (PathException e) {
            logPathException(e);
        }
    }

    /**
     * Gets the value of view.
     *
     * @return a List of paths
     */
    public List<Path> getView() {
        return Collections.unmodifiableList(view);
    }

    /**
     * Return the view as a List of Strings.
     *
     * @return the view as Strings
     */
    public List<String> getViewStrings() {
        List<String> retList = new ArrayList<String>();
        for (Path path: view) {
            retList.add(path.toStringNoConstraints());
        }
        return retList;
    }

    /**
     * Returns the view as a List of Strings, but with dots instead of colons.
     *
     * @return a List of Strings
     */
    public List<String> getDottedViewStrings() {
        List<String> retval = new ArrayList<String>();
        for (Path path : view) {
            retval.add(path.toStringNoConstraints().replace(':', '.'));
        }
        return retval;
    }

    /**
     * Remove the Path with the given String representation from the view.  If the pathString
     * refers to a path that appears in a PathException in the problems collection, remove that
     * problem.
     *
     * @param pathString the path to remove
     */
    public void removeFromView(String pathString) {
        Iterator<Path> iter = view.iterator();
        while (iter.hasNext()) {
            Path viewPath = iter.next();
            if (viewPath.toStringNoConstraints().startsWith(pathString)
                            || viewPath.toString().equals(pathString)) {
                iter.remove();
            }
        }
        Iterator<Throwable> throwIter = problems.iterator();
        while (throwIter.hasNext()) {
            Throwable thr = throwIter.next();
            if (thr instanceof PathException) {
                PathException pe = (PathException) thr;
                if (pe.getPathString().equals(pathString)) {
                    throwIter.remove();
                }
            }
        }
        removeOrderBy(pathString);
        validateSortOrder();
    }

    /**
     * Return true if and only if the view contains a Path that has pathString as its String
     * representation.
     * @param pathString the path to test
     * @return true if found
     */
    public boolean viewContains(String pathString) {
        for (Path viewPath: getView()) {
            if (viewPath.toStringNoConstraints().equals(pathString)
                            || viewPath.toString().equals(pathString)) {
                return true;
            }
        }
        return false;
    }


    /*****************************************************************************************/


    /**
     * Add a constraint to the query, allow code to be automatically assigned - eg A
     * @param constraint constraint to add to the query
     * @param path path to constrain, eg Employee.firstName
     * @return label of constraint
     */
    public String addConstraint(String path, Constraint constraint) {
        String code = getUnusedConstraintCode();
        constraint.code = code;
        return addConstraint(path, constraint, code);
    }

    /**
     * Add a constraint to the query and specify the code
     * @param constraint constraint to add to the query
     * @param code code for constraint
     * @param path path to constrain, eg Employee.firstName
     * @return label of constraint
     */
    public String addConstraint(String path, Constraint constraint, String code) {
        PathNode node = addNode(path);
        constraint.code = code;
        node.getConstraints().add(constraint);
        return code;
    }

    /**
     * Add a constraint to the query, assign a subclass
     * @param constraint constraint to add to the query
     * @param code code for constraint
     * @param path path to constrain, eg Employee.firstName
     * @param subclass type of node
     * @return label of constraint
     */
    public String addConstraint(String path, Constraint constraint, String code, String subclass) {
        PathNode node = addNode(path);
        constraint.code = code;
        node.setType(subclass);
        node.getConstraints().add(constraint);
        return code;
    }

    /**
     * Set the constraint logic expression. This expresses the AND and OR
     * relation between constraints.
     * @param constraintLogic the constraint logic expression
     */
    public void setConstraintLogic(String constraintLogic) {
        if (constraintLogic == null) {
            this.constraintLogic = null;
            return;
        }
        try {
            this.constraintLogic = new LogicExpression(constraintLogic);
        } catch (IllegalArgumentException err) {
            LOG.error("Failed to parse constraintLogic: " + constraintLogic, err);
        }
    }

    /**
     * Get the constraint logic expression as a string.  Will return null of there are < 2
     * constraints
     * @return the constraint logic expression as a string
     */
    public String getConstraintLogic() {
        if (constraintLogic == null) {
            return null;
        }
        return constraintLogic.toString();
    }

    /**
     * Get the LogicExpression. If there are one or zero constraints then
     * this method will return null.
     * @return the current LogicExpression or null
     */
    public LogicExpression getLogic() {
        return constraintLogic;
    }

    /**
     * Make sure that the logic expression is valid for the current query. Remove
     * any unknown constraint codes and add any constraints that aren't included
     * (using the default operator).
     * @param defaultOperator the default logical operator
     */
    public void syncLogicExpression(String defaultOperator) {
        if (getAllConstraints().size() <= 1) {
            setConstraintLogic(null);
        } else {
            Set<String> codes = getConstraintCodes();
            if (constraintLogic != null) {
                // limit to the actual variables
                try {
                    constraintLogic.removeAllVariablesExcept(getConstraintCodes());
                } catch (IllegalArgumentException e) {
                    // The constraint logic is now empty
                    constraintLogic = null;
                }
            }
            if (constraintLogic != null) {
                // add anything that isn't there
                codes.removeAll(constraintLogic.getVariableNames());
            }
            addCodesToLogic(codes, defaultOperator);
            if (constraintLogic != null) {
                constraintLogic = constraintLogic.validateForGroups(getGroupedCodes());
            }
        }
    }

    /**
     * Get all constraint codes.
     * @return all present constraint codes
     */
    private Set<String> getConstraintCodes() {
        Set<String> codes = new HashSet<String>();
        for (Constraint c : getAllConstraints()) {
            codes.add(c.getCode());
        }
        return codes;
    }

    /**
     * Returns a List of Collections of constraint codes, according to the different outer join
     * sections of the query.
     *
     * @return a List of Collections of Strings
     */
    private List<Collection<String>> getGroupedCodes() {
        List<Collection<String>> retval = new ArrayList<Collection<String>>();
        Map<String, Collection<String>> joinToCodes = new HashMap<String, Collection<String>>();
        for (Map.Entry<String, PathNode> entry : getNodes().entrySet()) {
            int lastColon = entry.getKey().lastIndexOf(':');
            String join = lastColon == -1 ? "" : entry.getKey().substring(0, lastColon);
            Collection<String> codesForJoin = joinToCodes.get(join);
            if (codesForJoin == null) {
                codesForJoin = new HashSet<String>();
                joinToCodes.put(join, codesForJoin);
            }
            for (Constraint c : entry.getValue().getConstraints()) {
                codesForJoin.add(c.getCode());
            }
        }
        for (Map.Entry<String, Collection<String>> entry : joinToCodes.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                retval.add(entry.getValue());
            }
        }
        return retval;
    }

    /**
     * Returns a List of logic Strings according to the different outer join sections of the
     * query.
     *
     * @return a List of String
     */
    public List<String> getGroupedConstraintLogic() {
        if (constraintLogic == null) {
            return Collections.EMPTY_LIST;
        }
        List<LogicExpression> groups = constraintLogic.split(getGroupedCodes());
        List<String> retval = new ArrayList<String>();
        for (LogicExpression group : groups) {
            retval.add(group.toString());
        }
        return retval;
    }

    /**
     * Get a constraint code that hasn't been used yet.
     * @return a constraint code that hasn't been used yet
     */
    public String getUnusedConstraintCode() {
        char c = 'A';
        while (getConstraintByCode("" + c) != null) {
            c++;
        }
        return "" + c;
    }

    /**
     * Get a Constraint involved in this query by code. Returns null if no
     * constraint with the given code was found.
     * @param string the constraint code
     * @return the Constraint with matching code or null
     */
    public Constraint getConstraintByCode(String string) {
        Iterator<Constraint> iter = getAllConstraints().iterator();
        while (iter.hasNext()) {
            Constraint c = iter.next();
            if (string.equals(c.getCode())) {
                return c;
            }
        }
        return null;
    }

    /**
     * Add a set of codes to the logical expression using the given operator.
     * @param codes Set of codes (Strings)
     * @param operator operator to add with
     */
    protected void addCodesToLogic(Set<String> codes, String operator) {
        String logic = getConstraintLogic();
        if (logic == null) {
            logic = "";
        } else {
            logic = "(" + logic + ")";
        }
        for (Iterator<String> iter = codes.iterator(); iter.hasNext(); ) {
            if (!StringUtil.isEmpty(logic)) {
                logic += " " + operator + " ";
            }
            logic += iter.next();
        }
        setConstraintLogic(logic);
    }

    /**
     * Get all constraints.
     * @return all constraints
     */
    public List<Constraint> getAllConstraints() {
        List<Constraint> list = new ArrayList<Constraint>();
        for (Iterator<PathNode> iter = nodes.values().iterator(); iter.hasNext(); ) {
            PathNode node = iter.next();
            list.addAll(node.getConstraints());
        }
        return list;
    }


    /*****************************************************************************************/

    /**
     * Returns whether the given path can be used in the Order By list of this query.
     * Outer joined paths cannot be used.
     *
     * @param path a String path to check
     * @return true if the path can be used
     */
    public boolean isValidOrderPath(String path) {
        path = getCorrectJoinStyle(path);
        if (path.indexOf(':') == -1) {
            for (Path viewPath : view) {
                if (viewPath.toStringNoConstraints().equals(path)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sets the order by list to the list of paths given.  Paths can be a single path or a comma
     * delimited list of paths.  To append a path to the list instead use addOrderBy.
     * @param paths paths to create the order by list
     */
    public void setOrderBy(String paths) {
        setOrderBy(paths, ASCENDING);
    }

    /**
     * Sets the order by list of the query to the list of paths given.  Paths can be a single path
     * or a comma delimited list of paths.  To append a path to the list instead use addOrderBy.
     * @param paths paths to create the order by list
     * @param direction the sort direction
     */
    public void setOrderBy(String paths, String direction) {
        if (paths == null || paths.equals("")) {
            logPathException(MSG);
            return;
        }
        Map<Path, String> orderBy = new LinkedHashMap<Path, String>();
        try {
            for (String path : paths.split("[, ]+")) {
                if (isValidOrderPath(path)) {
                    if (direction.equals("desc")) {
                        orderBy.put(makePath(model, this, path), DESCENDING);
                    } else if (direction.equals("asc")) {
                        orderBy.put(makePath(model, this, path), ASCENDING);
                    }
                } else {
                    logPathException("Cannot order by path " + path);
                }
            }
        } catch (PathException e) {
            logPathException(e);
        }
        sortOrder = orderBy;
    }

    /**
     * Sets the order by list of the query to the list of paths given.  Paths can be a single path
     * or a comma delimited list of paths.  To append a path to the list instead use addOrderBy.
     *
     * @param paths paths to create the order by list
     */
    public void setOrderBy(List<String> paths) {
        setOrderBy(paths, ASCENDING);
    }

    /**
     * Sets the order by list of the query to the list of paths given.  Paths can be a single path
     * or a comma delimited list of paths.  To append a path to the list instead use addOrderBy.
     * @param paths paths to create the order by list
     * @param direction the sort direction
     */
    public void setOrderBy(List<String> paths, String direction) {
        if (paths == null || paths.isEmpty()) {
            logPathException(MSG);
            return;
        }
        Map<Path, String> orderBy = new LinkedHashMap<Path, String>();
        for (String path : paths) {
            if (path != null && !path.equals("")) {
                try {
                    if (isValidOrderPath(path)) {
                        orderBy.put(makePath(model, this, path), direction);
                    } else {
                        logPathException("Cannot order by path " + path);
                    }
                } catch (PathException e) {
                    logPathException(e);
                }
            } else {
                logPathException(MSG);
            }
        }
        if (!orderBy.isEmpty()) {
            sortOrder = orderBy;
        }
    }

    /**
     * Sets the order by list of the query to the list of paths given.  Paths can be a single path
     * or a comma delimited list of paths.  To append a path to the list instead use addOrderBy.
     * assumes paths are valid
     * @param paths paths to create the order by list
     */
    public void setOrderByList(Map<Path, String> paths) {
        if (paths.isEmpty()) {
            logPathException(MSG);
            return;
        }
        Map<Path, String> orderByList = new LinkedHashMap<Path, String>();
        for (Path path : paths.keySet()) {
            if (path != null) {
                orderByList.put(path, paths.get(path));
            } else {
                logPathException(MSG);
            }
        }
        if (!orderByList.isEmpty()) {
            sortOrder = orderByList;
        }
    }

    /**
     * Appends the paths to the end of the order by list.  Paths can be a single path
     * or a comma delimited list of paths.
     * @param paths a list of paths to be appended to the end of the order by list
     */
    public void addOrderBy(String paths) {
        addOrderBy(paths, ASCENDING);
    }

    /**
     * Appends the paths to the end of the order by list.  Paths can be a single path
     * or a comma delimited list of paths.
     * @param paths a list of paths to be appended to the end of the order by list
     * @param direction the sort direction
     */
    public void addOrderBy(String paths, String direction) {
        if (paths.equals("")) {
            logPathException(MSG);
            return;
        }
        Map<Path, String> orderBy = new LinkedHashMap<Path, String>();
        for (String path : paths.split("[, ]")) {
            if (path != null && !path.equals("")) {
                try {
                    if (isValidOrderPath(path)) {
                        orderBy.put(makePath(model, this, path), direction);
                    } else {
                        logPathException("Cannot order by path " + path);
                    }
                } catch (PathException e) {
                    logPathException(e);
                }
            } else {
                logPathException(MSG);
            }
        }
        sortOrder.putAll(orderBy);
    }

    /**
     * Appends the paths to the end of the order by list.
     * @param paths a list of paths to be appended to the end of the order by list
     */
    public void addOrderBy(List<String> paths) {
        addOrderBy(paths, ASCENDING);
    }

    /**
     * Appends the paths to the end of the order by list.
     * @param paths a list of paths to be appended to the end of the order by list
     * @param direction the sort direction
     */
    public void addOrderBy(List<String> paths, String direction) {
        if (paths.size() == 0) {
            logPathException(MSG);
            return;
        }
        Map<Path, String> orderBy = new LinkedHashMap<Path, String>();
        try {
            for (String path : paths) {
                if (path != null && !path.equals("")) {
                    if (isValidOrderPath(path)) {
                        orderBy.put(makePath(model, this, path), direction);
                    } else {
                        logPathException("Cannot order by path " + path);
                    }
                } else {
                    logPathException(MSG);
                }
            }
        } catch (PathException e) {
            logPathException(e);
        }
        if (!orderBy.isEmpty()) {
            sortOrder.putAll(orderBy);
        }
    }

    /**
     * Gets the sort order
     * @return a List of paths
     */
    public Map<Path, String> getSortOrder() {
        return sortOrder;
    }

    /**
     * Set a new sort order
     * @param newSortOrder the new sort order.
     */
    public void setSortOrder(Map<Path, String> newSortOrder) {
        sortOrder = newSortOrder;
    }

    /**
     * Return the sort order as a List of Strings.
     * @return the sort order as Strings
     */
    public List<String> getSortOrderStrings() {
        List<String> retList = new ArrayList<String>();
        for (Path path: sortOrder.keySet()) {
            retList.add(path.toStringNoConstraints() + " " + sortOrder.get(path));
        }
        return retList;
    }

    /**
     * Remove a path from the sort order.  If on the order by list, replace with first item
     * on select list.  Used by the querybuilder only, as the querybuilder only ever has one
     * path in the order by clause.
     * @param viewString The string being removed from the view list
     */
    private void removeOrderBy(String pathString) {
        for (Path path : sortOrder.keySet()) {
            if (path.toStringNoConstraints().equals(pathString)) {
                sortOrder.remove(path);
            }
        }
    }

    /**
     * Removes everything from the order by list and adds the first path in the view list
     */
    public void resetOrderBy() {
        sortOrder = new LinkedHashMap<Path, String >();
    }


    /*****************************************************************************/


    /**
     * Gets the value of nodes
     * @return the value of nodes
     */
    public Map<String, PathNode> getNodes() {
        return nodes;
    }

    /**
     * Gets a Map from String path with dots instead of colons to String path with actual join
     * types.
     *
     * @return a Map from String to String
     */
    public Map<String, String> getPathsFromDots() {
        Map<String, String> retval = new LinkedHashMap<String, String>();
        for (Map.Entry<String, PathNode> entry : nodes.entrySet()) {
            retval.put(entry.getKey().replace(':', '.'), entry.getValue().getPathString());
        }
        for (Path p : view) {
            String path = p.toStringNoConstraints();
            int lastIndex;
            do {
                String answerSoFar = retval.get(path.replace(':', '.'));
                if (answerSoFar == null) {
                    retval.put(path.replace(':', '.'), path);
                } else if (!answerSoFar.equals(path)) {
                    throw new IllegalArgumentException("Two join types exist for the same path: "
                            + path + " and " + answerSoFar);
                }
                lastIndex = Math.max(path.lastIndexOf(':'), path.lastIndexOf('.'));
                if (lastIndex != -1) {
                    path = path.substring(0, lastIndex);
                }
            } while (lastIndex != -1);
        }
        return Collections.unmodifiableMap(retval);
    }

    /**
     * Returns a String path with the correct join style, for a given path find and replace the join
     * style according to paths already added to the the query, e.g. if adding
     * Company.departments.name and Company:departments is already part of the query then return
     * Company:departments.name
     *
     * @param path a path string
     * @return the path string, with colons instead of dots in the correct places.
     */
    public String getCorrectJoinStyle(String path) {
        String dotPath = path.replace(':', '.');
        Map<String, String> dots = getPathsFromDots();
        String bestReplacement = null;
        for (String dot : dots.keySet()) {
            if ((dotPath.startsWith(dot + ".") || dotPath.startsWith(dot + ":")
                        || dotPath.equals(dot))
                && (bestReplacement == null || dots.get(dot).startsWith(bestReplacement + ".")
                    || dots.get(dot).startsWith(bestReplacement + ":"))) {
                String temp = dot;
                bestReplacement = dots.get(temp);
            }
        }
        if (bestReplacement != null) {
            return bestReplacement + path.substring(bestReplacement.length());
        } else {
            return path;
        }
    }

    /**
     * Get a PathNode by path.
     *
     * @param path a path
     * @return the PathNode for path path
     */
    public PathNode getNode(String path) {
        return nodes.get(path);
    }

    /**
     * Get a PathNode by path, independantly of join style.
     *
     * @param path the PathNode for path path
     * @return the PathNode for path path
     */
    public PathNode getNodeWhateverJoin(String path) {
        for (String existingPath : getNodes().keySet()) {
            if (path.replaceAll(":", "\\.").equals(existingPath.replaceAll(":", "\\."))) {
                return getNodes().get(existingPath);
            }
        }
        return null;
    }

    /**
     * Add a node to the query using a path, adding parent nodes if necessary.
     *
     * @param path the path for the new Node
     * @return the PathNode that was added to the nodes Map
     */
    public PathNode addNode(String path) {
        if (getNodeWhateverJoin(path) != null) {
            return getNodeWhateverJoin(path);
        }
        if (!getCorrectJoinStyle(path).equals(path)) {
            throw new IllegalArgumentException("Adding two join types for same path: "
                    + path + " and " + getCorrectJoinStyle(path));
        }

        PathNode node;

        if (nodes.get(path) != null) {
            return nodes.get(path);
        }

        // the new node will be inserted after this one or at the end if null
        String previousNodePath = null;
        int lastIndex = Math.max(path.lastIndexOf("."), path.lastIndexOf(":"));

        if (lastIndex == -1) {
            node = new PathNode(path);
            if (model.isGeneratedClassesAvailable()) {
                if (!model.isGeneratedClassAvailable(path)) {
                    logPathException(new ClassNotFoundException("Class "
                            + path + " is not available."));
                }
            }
        } else {
            String prefix = path.substring(0, lastIndex);
            if (nodes.containsKey(prefix)) {
                Iterator<String> pathsIter = nodes.keySet().iterator();

                while (pathsIter.hasNext()) {
                    String pathFromMap = pathsIter.next();
                    if (pathFromMap.startsWith(prefix)) {
                        previousNodePath = pathFromMap;
                    }
                }

                String prefixWithoutConstraints = prefix.replaceAll("\\[[^]]*\\]", "");
                PathNode parent = nodes.get(prefixWithoutConstraints);
                String fieldName = path.substring(lastIndex + 1);
                String fieldNameWithoutConstraints = fieldName.replaceAll("\\[[^]]*\\]", "");
                node = new PathNode(parent, fieldNameWithoutConstraints,
                        path.charAt(lastIndex) == ':');
                try {
                    node.setModel(model);
                } catch (Exception err) {
                    logPathException(err);
                }
            } else {
                addNode(prefix);
                return addNode(path);
            }
        }

        nodes = CollectionUtil.linkedHashMapAdd(nodes, previousNodePath, path, node);

        return node;
    }

    /**
     * Clone this PathQuery
     * @return a PathQuery
     */
    public PathQuery clone() {
        PathQuery query = new PathQuery(model);
        IdentityHashMap<PathNode, PathNode> newNodes = new IdentityHashMap();
        for (Iterator<Entry<String, PathNode>> i = nodes.entrySet().iterator(); i.hasNext();) {
            Entry<String, PathNode> entry = i.next();
            query.getNodes().put(entry.getKey(), cloneNode(query, entry.getValue(), newNodes,
                                                           model));
        }
        query.view.addAll(view);
        query.getSortOrder().putAll(sortOrder);
        if (problems != null) {
            query.problems = new ArrayList<Throwable>(problems);
        }
        query.description = description;
        query.pathDescriptions = new HashMap<Path, String>(pathDescriptions);
        query.setConstraintLogic(getConstraintLogic());
        query.info = info;
        return query;
    }

    /**
     * Clone a PathNode.
     *
     * @param query PathQuery containing cloned PathNode
     * @param node a PathNode
     * @param newNodes a Map from old PathNodes to new PathNodes, to link up parents properly
     * @param model the Model
     * @return a copy of the PathNode
     */
    protected static PathNode cloneNode(PathQuery query, PathNode node,
                                        IdentityHashMap<PathNode, PathNode> newNodes, Model model) {
        if (newNodes.containsKey(node)) {
            return newNodes.get(node);
        }
        PathNode newNode;
        PathNode parent = (PathNode) node.getParent();
        if (parent == null) {
            newNode = new PathNode(node.getType());
        } else {
            parent = cloneNode(query, parent, newNodes, model);
            newNode = new PathNode(parent, node.getFieldName(), node.isOuterJoin());
            try {
                newNode.setModel(model);
            } catch (IllegalArgumentException err) {
                query.addProblem(err);
            } catch (ClassNotFoundException e) {
                query.addProblem(e);
            }
            newNode.setType(node.getType());
        }
        for (Iterator i = node.getConstraints().iterator(); i.hasNext();) {
            Constraint constraint = (Constraint) i.next();
            newNode.getConstraints().add(new Constraint(constraint.getOp(),
                                                        constraint.getValue(),
                                                        constraint.isEditable(),
                                                        constraint.getDescription(),
                                                        constraint.getCode(),
                                                        constraint.getIdentifier(),
                                                        constraint.getExtraValue()));
        }
        newNodes.put(node, newNode);
        return newNode;
    }

    /**
     * Gets the value of model
     * @return the value of model
     */
    public Model getModel() {
        return model;
    }

    /**
     * Return the map from Path objects (from the view) to their descriptions.
     * @return the path descriptions map
     */
    public Map<Path, String> getPathDescriptions() {
        return pathDescriptions;
    }

    /**
     * Set a description for this query.
     * @param description a description of the query
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Returns the  path description for given path. Path description is computed according to the
     * known paths and corresponding path descriptions.
     * @param pathNoConstraints path without constraints
     * @return computed description or original path
     */
    public String getPathDescription(String pathNoConstraints) {
        String path = pathNoConstraints;
        String longestPrefix = "";
        String longestPrefixAlias = "";
        // in pathDescription object are saved prefixes and corresponding aliases
        // (path descriptions). The longest known prefix is searched in path for and corresponding
        // alias replaces the prefix.
        for (Map.Entry<Path, String> entry: pathDescriptions.entrySet()) {
            // can be a bad path
            if (entry.getKey().toStringNoConstraints() != null) {
                String prefix = entry.getKey().toStringNoConstraints();
                if (path.startsWith(prefix) && prefix.length() > longestPrefix.length()) {
                    longestPrefix = prefix;
                    longestPrefixAlias = entry.getValue();
                }
            }
        }
        if (!longestPrefix.equals("")) {
            String restOfPath = path.substring(longestPrefix.length());
            restOfPath = restOfPath.replaceAll("[:.](?!\\s)", " > ");
            return longestPrefixAlias + restOfPath;
        }
        return path.replaceAll("[:.](?!\\s)", " > ");
    }

    /**
     * Return the description for the given path from the view.
     * @return the description Map
     */
    public Map<String, String> getPathStringDescriptions() {
        Map<String, String> retMap = new HashMap<String, String>();
        for (Map.Entry<Path, String> entry: pathDescriptions.entrySet()) {
            retMap.put(entry.getKey().toString(), entry.getValue());
            retMap.put(entry.getKey().toStringNoConstraints(), entry.getValue());
        }
        return retMap;
    }

    /**
     * Add a description to a path in the view.  If the viewString isn't a valid view path, add an
     * exception to the problems list.
     * @param viewString the string form of a path in the view
     * @param description the description
     */
    public void addPathStringDescription(String viewString, String description) {
        try {
            Path path = makePath(model, this, viewString);
            pathDescriptions.put(path, description);
        } catch (PathException e) {
            logPathException(e);
        }
    }

    /**
     * Provide a list of the names of bags mentioned in the query
     * @return the list of bag names
     */
    public List<Object> getBagNames() {
        List<Object> bagNames = new ArrayList<Object>();
        for (Iterator<PathNode> i = nodes.values().iterator(); i.hasNext();) {
            PathNode node = i.next();
            for (Iterator j = node.getConstraints().iterator(); j.hasNext();) {
                Constraint c = (Constraint) j.next();
                if (BagConstraint.VALID_OPS.contains(c.getOp())) {
                    bagNames.add(c.getValue());
                }
            }
        }
        return bagNames;
    }

    /**
     * Given the string version of a path (eg. "Department.employees.seniority"), and a PathQuery,
     * create a Path object.  The PathQuery is needed to find the class constraints that affect the
     * path.
     *
     * @param model the Model to pass to the Path constructor
     * @param query the PathQuery
     * @param fullPathName the full path as a string
     * @return a new Path object
     * @throws PathException if the path is not valid
     */
    public static Path makePath(Model model, PathQuery query,
            String fullPathName) throws PathException {
        Path path = null;
        if (fullPathName.indexOf("[") >= 0) {
            path = new Path(model, fullPathName);
        } else {
            Map<String, String> subClassConstraintMap = new HashMap<String, String>();
            Iterator viewPathNameIter = query.getNodes().keySet().iterator();
            while (viewPathNameIter.hasNext()) {
                String viewPathName = (String) viewPathNameIter.next();
                PathNode pathNode = query.getNode(viewPathName);
                subClassConstraintMap.put(viewPathName.replace(":", "."), pathNode.getType());
            }
            path = new Path(model, fullPathName, subClassConstraintMap);
        }
        return path;
    }

    /**
     * Get info regarding this query
     * @return the info
     */
    public ResultsInfo getInfo() {
        return info;
    }

    /**
     * Set info about this query
     * @param info the info
     */
    public void setInfo(ResultsInfo info) {
        this.info = info;
    }

    /**
     * Get the exceptions generated while deserialising this path query.
     * @return exceptions relating to this path query
     */
    public Throwable[] getProblems() {
        return problems.toArray(new Throwable[0]);
    }

    /**
     * Sets problems.
     * @param problems problems
     */
    public void setProblems(List<Throwable> problems) {
        this.problems = (problems != null ?  problems : new ArrayList<Throwable>());
    }

    /**
     * Find out whether the path query is valid against the current model.
     * @return true if query is valid, false if not
     */
    public boolean isValid() {
        return (problems.size() == 0);
    }

    /**
     * Adds problem to path query.
     * @param err problem
     */
    public void addProblem(Throwable err) {
        problems.add(err);
    }

    /**
     * Serialise this query in XML format.
     * @param name query name to put in xml
     * @param version the version number of the XML format
     * @return PathQuery in XML format
     */
    public String toXml(String name, int version) {
        return PathQueryBinding.marshal(this, name, model.getName(), version);
    }

    /**
     * Serialise to XML with no name.
     *
     * @param version the version number of the XML format
     * @return the XML
     */
    public String toXml(int version) {
        return PathQueryBinding.marshal(this, "", model.getName(), version);
    }

    /**
     * This equals method is expensive - avoid using it, or putting PathQuery objects in Sets or as
     * keys in Maps.
     *
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        return (o instanceof PathQuery)
        && model.equals(((PathQuery) o).model)
        && nodes.equals(((PathQuery) o).nodes)
        && view.equals(((PathQuery) o).view)
        && sortOrder.equals(((PathQuery) o).sortOrder)
        && pathDescriptions.equals(((PathQuery) o).pathDescriptions);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return 2 * model.hashCode()
        + 3 * nodes.hashCode()
        + 5 * view.hashCode()
        + 7 * sortOrder.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "{PathQuery: model=" + model.getName() + ", nodes=" + nodes + ", view=" + view
        + ", sortOrder=" + sortOrder + ", pathDescriptions=" + pathDescriptions + "}";
    }

    private void logPathException(String msg) {
        logPathException(new PathException(msg, null));
    }

    private void logPathException(Throwable e) {
        LOG.error("Path error", e);
        addProblem(e);
    }

    /**
     * Get the template description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
