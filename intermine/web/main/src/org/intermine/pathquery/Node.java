package org.intermine.pathquery;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import org.intermine.metadata.AttributeDescriptor;
import org.intermine.metadata.ClassDescriptor;
import org.intermine.metadata.FieldDescriptor;
import org.intermine.metadata.Model;
import org.intermine.metadata.ReferenceDescriptor;
import org.intermine.util.TypeUtil;
import org.intermine.util.Util;

/**
 * Superclass of left and right nodes
 * @author Mark Woodbridge
 */
public class Node
{
    private Node parent;
    private String fieldName, pathString, prefix, type;
    private boolean attribute = false, reference = false, collection = false, outer = false;
    private int indentation;

    /**
     * Constructor for a root node
     * @param type the root type of this tree
     */
    public Node(String type) {
        this.type = type;
        pathString = type;
        prefix = "";
        indentation = 0;
    }

    /**
     * Constructor for a non-root node.
     *
     * @param parent the parent node of this node
     * @param fieldName the name of the field that this node represents
     * @param outer true if this node should be an outer join
     */
    public Node(Node parent, String fieldName, boolean outer) {
        this.fieldName = fieldName;
        this.parent = parent;
        this.outer = outer;
        prefix = parent.getPathString();
        pathString = prefix + (outer ? ":" : ".") + fieldName;
        //Exception e = new Exception();
        //e.fillInStackTrace();
        //LOG.error("New Node: " + pathString, e);

        indentation = pathString.split("[.:]").length - 1;
    }

    /**
     * Attach the model. Throws IllegalArgumentExceptions if node doesn't map onto the model.
     *
     * @param model model to attach
     * @throws IllegalArgumentException if class or field are not found in the model
     * @throws ClassNotFoundException if the class name is not in the model
     */
    public void setModel(Model model) throws IllegalArgumentException, ClassNotFoundException {
        ClassDescriptor cld = model.getClassDescriptorByName(TypeUtil.getClass(getParentType(), 
                model).getName());
        if (cld == null) {
            throw new IllegalArgumentException("No class '" + getParentType() + "' found in model"
                                       + " '" + model.getName() + "'");
        }
        FieldDescriptor fd = cld.getFieldDescriptorByName(fieldName);
        if (fd == null) {
            throw new IllegalArgumentException("Class '" + cld.getName() + "' does not have field"
                                        + " '" + fieldName + "'.");
        }
        type = TypeUtil.unqualifiedName(fd.isAttribute()
                                        ? ((AttributeDescriptor) fd).getType()
                                        : ((ReferenceDescriptor) fd)
                                        .getReferencedClassDescriptor().getType().getName());
        attribute = fd.isAttribute();
        reference = fd.isReference();
        collection = fd.isCollection();
    }

    /**
     * Type of parent node. Required for MainController to find field value
     * enumerations with fieldName and parentType.
     *
     * @return  type of parent node
     */
    public String getParentType() {
        if (parent == null) {
            return null;
        } else {
            return parent.getType();
        }
    }

    /**
     * Get the parent node.
     * @return the parent node
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Gets the String value of pathString of this node (eg. "Department.manager.name")
     *
     * @return the String value of pathString
     */
    public String getPathString()  {
        return pathString;
    }

    /**
     * Gets the value of type
     *
     * @return the value of type
     */
    public String getType()  {
        return type;
    }

    /**
     * Set the value of type
     *
     * @param type the value of type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the value of prefix
     *
     * @return the value of prefix
     */
    public String getPrefix()  {
        return prefix;
    }

    /**
     * Gets the value of fieldName
     *
     * @return the value of fieldName
     */
    public String getFieldName()  {
        return fieldName;
    }

    /**
     * Gets the value of attribute
     *
     * @return the value of attribute
     */
    public boolean isAttribute()  {
        return attribute;
    }

    /**
     * Gets the value of reference
     *
     * @return the value of reference
     */
    public boolean isReference()  {
        return reference;
    }

    /**
     * Gets the value of collection
     *
     * @return the value of collection
     */
    public boolean isCollection()  {
        return collection;
    }

    /**
     * Gets the value of indentation
     *
     * @return the value of indentation
     */
    public int getIndentation()  {
        return indentation;
    }

    /**
     * Returns true if this should be an outer join.
     *
     * @return a boolean
     */
    public boolean isOuterJoin() {
        return outer;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return pathString + ":" + type;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        return (o instanceof Node)
            && pathString.equals(((Node) o).pathString)
            && Util.equals(type, ((Node) o).type)
            && Util.equals(parent, ((Node) o).parent);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return 2 * pathString.hashCode()
            + (type == null ? 0 : 3 * type.hashCode())
            + (parent == null ? 0 : 5 * parent.hashCode());
    }
}
