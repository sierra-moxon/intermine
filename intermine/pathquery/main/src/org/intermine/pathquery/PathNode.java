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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Node used in displaying query
 * @author Mark Woodbridge
 */
public class PathNode extends Node
{
    List<Constraint> constraints = new ArrayList<Constraint>();

    /**
     * Constructor for a root node
     * Constucting a PathNode is not usually necessary - use PathQuery#addNode
     * @param type the root type of this tree
     */
    public PathNode(String type) {
        super(type);
    }

    /**
     * Constructor for a non-root node
     * Constucting a PathNode is not usually necessary - use PathQuery#addNode
     * @param parent the parent node of this node
     * @param fieldName the name of the field that this node represents
     * @param outer true if the node is an outer join
     */
    public PathNode(Node parent, String fieldName, boolean outer) {
        super(parent, fieldName, outer);
    }

    /**
     * Gets the value of constraints
     *
     * @return the value of constraints
     */
    public List<Constraint> getConstraints()  {
        return constraints;
    }

    /**
     * Get Constraint by index.
     * @param index index
     * @return Constraint
     */
    public Constraint getConstraint(int index) {
        return constraints.get(index);
    }

    /**
     * Sets the value of constraints
     *
     * @param constraints value to assign to constraints
     */
    public void setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
    }

    /**
     * Change a constraint value.
     *
     * @param constraint an existing constrain under this node
     * @param value constraint value
     * @return the new Constraint object
     */
    public Constraint setConstraintValue(Constraint constraint, Object value) {
        if (!constraints.contains(constraint)) {
            throw new IllegalArgumentException("constraint not present on node");
        }
        int index = constraints.indexOf(constraint);
        constraints.set(index, new Constraint(constraint.getOp(), value, constraint.isEditable(),
                        constraint.getDescription(), constraint.getCode(),
                        constraint.getIdentifier(), constraint.getExtraValue()));
        return constraints.get(index);
    }

    /**
     * Removes a constraint from list
     * @param constraint to remove
     */
    public void removeConstraint(Constraint constraint) {
        this.constraints.remove(constraint);
    }

    /**
     * Returns the value of isOuterJoin (used by jsp)
     * @return a boolean
     */
    public boolean getIsOuterJoin() {
        return isOuterJoin();
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        //return super.toString() + " " + constraints;
        return "PathNode(pathString = \"" + getPathString() + "\", type = \"" + getType()
            + "\", constraints = \"" + constraints + "\")";
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        return super.equals(o)
            && (o instanceof PathNode)
            && constraints.equals(((PathNode) o).constraints);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return 2 * super.hashCode()
            + 3 * constraints.hashCode();
    }
    
    /**
     * Returns a set of path strings that must be inner joins if the two given path strings are
     * loop constrained onto each other.
     *
     * @param fromPath a path string
     * @param toPath another path string
     * @return a Set of path strings
     */
    public static Set<String> findForcedInnerJoins(String fromPath, String toPath) {
        Set<String> retval = new HashSet<String>();
        String commonPrefix = fromPath;
        while (!toPath.startsWith(commonPrefix)) {
            retval.add(commonPrefix);
            commonPrefix = commonPrefix.substring(0, Math.max(commonPrefix.lastIndexOf("."),
                        commonPrefix.lastIndexOf(":")));
        }
        commonPrefix = toPath;
        while (!fromPath.startsWith(commonPrefix)) {
            retval.add(commonPrefix);
            commonPrefix = commonPrefix.substring(0, Math.max(commonPrefix.lastIndexOf("."),
                        commonPrefix.lastIndexOf(":")));
        }
        return retval;
    }
}
