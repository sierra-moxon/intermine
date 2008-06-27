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

import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.util.Util;

/**
 * A simple constraint representation
 * @author Mark Woodbridge
 * @author Thomas Riley
 */
public class Constraint
{
    protected ConstraintOp op;
    protected Object value;
    protected boolean editable;
    protected String description = null;
    protected String identifier = null;
    protected String code = null;
    protected Object extraValue = null;

    /**
     * Make a new Constraint with no description or identifier, and which has the editable flag set
     * to false.
     * @param op the constraintOp for this constraint
     * @param value the value for this constraint
     */
    public Constraint(ConstraintOp op, Object value) {
        this.op = op;
        this.value = value;
        this.editable = false;
    }

    /**
     * Make a new Constraint with no description or identifier, and which has the editable flag set
     * to false.  Used for the between constraint.
     * @param op the constraintOp for this constraint
     * @param start the lower value
     * @param end the upper value
     */
    public Constraint(ConstraintOp op, Object start, Object end) {
        this.op = op;
        this.value = start;
        this.extraValue = end;
        this.editable = false;
    }

    /**
     * Make a new Constraint with no description or identifier, and which has the editable flag set
     * to false.  Used for the NULL/NOT NULL constraint.
     * @param op the constraintOp for this constraint
     */
    public Constraint(ConstraintOp op) {
        this.op = op;
        this.editable = false;
    }


    /**
     * Make a new Constraint with a description and an identifier.
     * @param op the constraintOp for this constraint
     * @param value the value for this constraint
     * @param editable set if this constraint should be editable in a template
     * @param description the description of this constraint
     * @param code the constraint code
     * @param identifier a label for this Constraint used for refering to this it in a
     * template. null means that this Constraint has no identifier.
     * @param extraValue an extra value, for LOOKUP constraints
     */
    public Constraint(ConstraintOp op, Object value, boolean editable, String description,
            String code, String identifier, Object extraValue) {
        this.op = op;
        this.value = value;
        this.editable = editable;
        this.description = description;
        this.identifier = identifier;
        this.code = code;
        this.extraValue = extraValue;
    }

    /**
     * Gets the value of op
     *
     * @return the value of op
     */
    public ConstraintOp getOp()  {
        return op;
    }

    /**
     * Gets the value of value
     *
     * @return the value of value
     */
    public Object getValue()  {
        return value;
    }

    /**
     * Return true if and only if this constraint should be editable in a template.
     * @return the editable flag
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Return the description that was passed to the constructor.
     * @return the description or null if unset
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the code for this constraint.
     * @return code for this constraint
     */
    public String getCode() {
        return code;
    }

    /**
     * Return the identifier that was passed to the constructor.
     * @return the identifier or null if unset
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Return the extra value that was passed into the constructor.
     *
     * @return the extra value or null if unset
     */
    public Object getExtraValue() {
        return extraValue;
    }

    /**
     * Return value in display format. This performs conversion between SQL
     * wildcard % symbols and user wildcard * symbols.
     * @return  constraint value translated for the user as a string
     */
    public String getDisplayValue() {
        if (op == ConstraintOp.MATCHES || op == ConstraintOp.DOES_NOT_MATCH
            || op == ConstraintOp.EQUALS || op == ConstraintOp.NOT_EQUALS) {
            return Util.wildcardSqlToUser(getValue().toString());
        } else if (op == ConstraintOp.IS_NOT_NULL || op == ConstraintOp.IS_NULL) {
            return "";
        } else {
            return "" + getValue();
        }
    }

    /**
     * Return the value in <i>really</i> display format. This is like getDisplayValue, but it
     * actually produces a value that you want to look at, rather than a value that is machine-read
     * later on.
     *
     * @return a String
     */
    public String getReallyDisplayValue() {
        if (op == ConstraintOp.MATCHES || op == ConstraintOp.DOES_NOT_MATCH
            || op == ConstraintOp.EQUALS || op == ConstraintOp.NOT_EQUALS) {
            return Util.wildcardSqlToUser(getValue().toString());
        } else if (op == ConstraintOp.IS_NOT_NULL || op == ConstraintOp.IS_NULL) {
            return "";
        } else if ((op == ConstraintOp.LOOKUP) && (extraValue != null)
                && (!"".equals(extraValue))) {
            return getValue() + " IN " + extraValue;
        } else {
            return "" + getValue();
        }
    }

    /**
     * Return true if this constraint can be presented as editable in a
     * template query. This method assumes that the constraint is applied to an
     * attribute.
     * @return true if constraint can be edited in a template query
     */
    public boolean isEditableInTemplate() {
        return (op != ConstraintOp.IS_NOT_NULL
                && op != ConstraintOp.IS_NULL
                && op != ConstraintOp.IN
                && op != ConstraintOp.NOT_IN);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "Constraint(" + op + ", " + value
            + (identifier == null ? "" : ", \"" + identifier + "\"")
            + (description == null ? "" : ", \"" + description + "\"")
            + (extraValue == null ? "" : ", \"" + extraValue + "\"")
            + ")";
    }

    /**
     * @return constraint in XML format.  Just used for testing.
     */
    public String toXML() {
        //"<constraint op=\"=\" value=\"10\" description=\"\" identifier=\"\" " +
        //"code=\"A\"></constraint>"
        return "<constraint op=\"" + op + "\" value=\"" + value + "\" "
            + "description=\"" + (description == null ? "" : description) + "\" "
            + "identifier=\"" + (identifier == null ? "" : identifier) + "\" "
            + "code=\"" + (code == null ? "" : code) + "\" "
            + "extraValue=\"" + (extraValue == null ? "" : extraValue) + "\""
            + "></constraint>";
    }


    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (o instanceof Constraint) {
            Constraint other = (Constraint) o;
            return op.equals(other.op)
                && Util.equals(value, other.value)
                && Util.equals(description, other.description)
                && Util.equals(identifier, other.identifier)
                && Util.equals(extraValue, other.extraValue);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return 2 * op.hashCode()
            + (value == null ? -3 : 3 * value.hashCode())
            + (description == null ? -5 : 5 * description.hashCode())
            + (identifier == null ? -7 : 7 * identifier.hashCode())
            + (extraValue == null ? -11 : 11 * extraValue.hashCode());
    }
}
