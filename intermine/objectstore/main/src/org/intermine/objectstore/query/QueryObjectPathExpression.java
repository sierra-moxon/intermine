package org.intermine.objectstore.query;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.lang.reflect.Method;
import java.util.Collection;

import org.intermine.model.InterMineObject;
import org.intermine.util.TypeUtil;

/**
 * An element that can appear in the SELECT clause of a query, representing extra data to be
 * collected for the Results - namely a object referenced by some other object in the results.
 *
 * @author Matthew Wakeling
 */
public class QueryObjectPathExpression implements QueryPathExpression
{
    private QueryClass qc;
    private QueryObjectPathExpression qope;
    private String fieldName;
    private Class type;

    /**
     * Constructs a QueryObjectPathExpression representing an object reference from the given
     * QueryClass to the given fieldname.
     *
     * @param qc the QueryClass
     * @param fieldName the name of the relevant field
     * @throws IllegalArgumentException if the field is not an object reference
     */
    public QueryObjectPathExpression(QueryClass qc, String fieldName) {
        if (fieldName == null) {
            throw new NullPointerException("Field name parameter is null");
        }
        if (qc == null) {
            throw new NullPointerException("QueryClass parameter is null");
        }
        Method field = TypeUtil.getGetter(qc.getType(), fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Field " + fieldName + " not found in "
                    + qc.getType());
        }
        if (Collection.class.isAssignableFrom(field.getReturnType())) {
            throw new IllegalArgumentException("Field " + fieldName + " is a collection type");
        }
        if (!InterMineObject.class.isAssignableFrom(field.getReturnType())) {
            throw new IllegalArgumentException("Field " + fieldName + " is not an object reference"
                    + " type - was " + field.getReturnType() + " instead");
        }
        this.qc = qc;
        this.qope = null;
        this.fieldName = fieldName;
        this.type = field.getReturnType();
    }

    /**
     * Constructs a QueryObjectPathExpression representing an object reference from the given
     * Object reference to the given fieldname.
     *
     * @param qope the QueryObjectPathExpression
     * @param fieldName the name of the relevant field
     * @throws IllegalArgumentException if the field is not an object reference
     */
    public QueryObjectPathExpression(QueryObjectPathExpression qope, String fieldName) {
        if (qope == null) {
            throw new NullPointerException("QueryObjectPathExpression parameter is null");
        }
        if (fieldName == null) {
            throw new NullPointerException("Field name parameter is null");
        }
        Method field = TypeUtil.getGetter(qope.getType(), fieldName);
        if (field == null) {
            throw new IllegalArgumentException("Field " + fieldName + " not found in "
                    + qope.getType());
        }
        if (Collection.class.isAssignableFrom(field.getReturnType())) {
            throw new IllegalArgumentException("Field " + qope.getType().getName() + "." + fieldName
                    + " is a collection type");
        }
        if (!InterMineObject.class.isAssignableFrom(field.getReturnType())) {
            throw new IllegalArgumentException("Field " + qope.getType().getName() + "."
                    + fieldName + " is not an object reference type - was " + field.getReturnType()
                    + " instead");
        }
        this.qc = null;
        this.qope = qope;
        this.fieldName = fieldName;
        this.type = field.getReturnType();
    }

    /**
     * Returns the QueryClass of which the field is a member.
     *
     * @return the QueryClass
     */
    public QueryClass getQueryClass() {
        return qc;
    }

    /**
     * Returns the QueryObjectPathExpression of which the field is a member.
     *
     * @return the QueryObjectPathExpression
     */
    public QueryObjectPathExpression getQope() {
        return qope;
    }

    /**
     * Returns the name of the field.
     *
     * @return field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * {@inheritDoc}
     */
    public Class getType() {
        return type;
    }
}
