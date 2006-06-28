package org.intermine.metadata;

/*
 * Copyright (C) 2002-2005 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import org.intermine.util.Util;

/**
 * Describes a field that references a collection of other objects.
 * getReverseReferenceDescriptor()
 * allows one ot work out the multiplicity of the association's other end.
 *
 * @author Richard Smith
 */

public class CollectionDescriptor extends ReferenceDescriptor
{
    /**
     * Construct a CollectionDescriptor.  name and referencedType may not be null.
     * @param name name of this field in parent class
     * @param referencedType the fully qualified name of the business object type in this collection
     * @param reverseRefName name of field in the referenced class that points back to this class
     *                       (may be null)
     * @throws IllegalArgumentException if arguments are null
     */
    public CollectionDescriptor(String name, String referencedType,
                                   String reverseRefName) {
        // should define type of collection properly somehow
        super(name, referencedType, reverseRefName);
    }

    /**
     * Returns a ClassDescriptor for the type of object contained in this collection.
     * @return ClassDescriptor for the type of object contained within the collection
     * @throws IllegalStateException if the model is not yet set
     */
    public ClassDescriptor getReferencedClassDescriptor() throws IllegalStateException {
        return super.getReferencedClassDescriptor();
    }

    /**
     * Gets the field in the referenced object (i.e. objects in the collection) that
     * refers back to this class.  Note that this will be null in a unidirectional
     * relationship, a ReferenceDescriptor in a N:1 and a CollectionDescriptor in a M:N.
     * @return a FieldDescriptor referring back to this class.
     * @throws IllegalStateException if the model is not yet set
     */
    public ReferenceDescriptor getReverseReferenceDescriptor() throws IllegalStateException {
        return super.getReverseReferenceDescriptor();
    }

    /**
     * @see FieldDescriptor#relationType()
     */
    public int relationType() {
        ReferenceDescriptor rd = getReverseReferenceDescriptor();
        if (rd == null || rd instanceof CollectionDescriptor) {
            return M_N_RELATION;
        } else {
            return ONE_N_RELATION;
        } 
    }

    /**
     * @see Object#equals(Object)
     */
    public boolean equals(Object obj) {
        if (obj instanceof CollectionDescriptor) {
            CollectionDescriptor ref = (CollectionDescriptor) obj;
            return (cld == null || cld.getName().equals(ref.cld.getName()))
                && name.equals(ref.name)
                && referencedType.equals(ref.referencedType)
                && Util.equals(reverseRefName, ref.reverseRefName);
        }
        return false;
    }

    /**
     * @see Object#hashCode()
     */
    public int hashCode() {
        return 2 * (cld == null ? 0 : cld.getName().hashCode())
            + 3 * name.hashCode()
            + 7 * referencedType.hashCode()
            + 11 * Util.hashCode(reverseRefName);
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<collection name=\"" + name + "\" referenced-type=\"" + referencedType + "\"")
            .append(reverseRefName != null ? " reverse-reference=\"" + reverseRefName + "\"" : "")
            .append("/>");
        return sb.toString();
    }
}
