package org.intermine.metadata;

/*
 * Copyright (C) 2002-2009 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Set;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Class representing a primary key as a list of field names
 *
 * @author Andrew Varley
 * @author Mark Woodbridge
 */
public class PrimaryKey
{
    String name;
    Set<String> fieldNames = new LinkedHashSet<String>();
    ClassDescriptor cld;

    /**
     * Constructor
     * @param name the name to use for the primary key
     * @param fields a comma-delimited list of field names
     * @param cld the ClassDescriptor that this PrimaryKey refers to
     */
    public PrimaryKey(String name, String fields, ClassDescriptor cld) {
        this.name = name;
        this.cld = cld;
        if (fields == null) {
            throw new NullPointerException("fields parameter cannot be null");
        }
        String[] tokens = fields.split(",");
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();
            FieldDescriptor field = cld.getFieldDescriptorByName(token);
            if (field == null) {
                throw new IllegalArgumentException("No such field name " + token + " in class "
                        + cld.getName() + " for primary key " + name);
            }
            if (field instanceof CollectionDescriptor) {
                throw new IllegalArgumentException("Field " + token + " in primary key "
                        + cld.getName() + "." + name + " is a collection - must be an attribute "
                        + "or a reference");
            }
            fieldNames.add(token);
        }
    }


    /**
     * Return the name
     *
     * @return name of this primary key
     */
    public String getName() {
        return name;
    }


    /**
     * Return the Set of field names
     *
     * @return the Set of field names
     */
    public Set<String> getFieldNames() {
        return fieldNames;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (o instanceof PrimaryKey) {
            return fieldNames.equals(((PrimaryKey) o).fieldNames)
                && cld.equals(((PrimaryKey) o).cld);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return fieldNames.hashCode() + cld.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("<fields=\"");
        for (Iterator iter = getFieldNames().iterator(); iter.hasNext();) {
            sb.append(iter.next());
            if (iter.hasNext()) {
                sb.append(",");
            }
        }
        sb.append("\">");

        return sb.toString();
    }
}
