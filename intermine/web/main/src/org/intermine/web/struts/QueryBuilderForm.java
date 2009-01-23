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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.pathquery.ConstraintValueParser;
import org.intermine.pathquery.ParseValueException;
import org.intermine.pathquery.PathNode;
import org.intermine.pathquery.PathQuery;
import org.intermine.util.TypeUtil;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.session.SessionMethods;

/**
 * The main form, using for editing constraints
 * @author Mark Woodbridge
 * @author Thomas Riley
 */
public class QueryBuilderForm extends ActionForm
{
    protected String bagOp, bagValue;
    protected String attributeOp, attributeValue, attributeOptions, extraValue;
    protected String subclassValue;
    protected String loopQueryOp, loopQueryValue;

    protected String path;
    protected String operator = "and";
    protected String nullConstraint;
    protected Object parsedAttributeValue;

    // template builder elements

    protected boolean editable;
    protected String templateLabel;
    protected String templateId;

    /**
     * Gets the value of loopQueryOp
     * @return the value of loopQueryOp
     */
    public String getLoopQueryOp()  {
        return loopQueryOp;
    }

    /**
     * Sets the value of loopQueryOp
     * @param loopOp Value to assign to loopQueryOp
     */
    public void setLoopQueryOp(String loopOp) {
        this.loopQueryOp = loopOp;
    }

    /**
     * Gets the value of loopQueryValue
     * @return the value of loopQueryValue
     */
    public String getLoopQueryValue()  {
        return loopQueryValue;
    }

    /**
     * Sets the value of loopQueryValue
     * @param loopQuery value to assign to loopQueryValue
     */
    public void setLoopQueryValue(String loopQuery) {
        this.loopQueryValue = loopQuery;
    }

    /**
     * Gets the value of bagOp
     * @return the value of bagOp
     */
    public String getBagOp()  {
        return bagOp;
    }

    /**
     * Sets the value of bagOp
     * @param bagOp Value to assign to bagOp
     */
    public void setBagOp(String bagOp) {
        this.bagOp = bagOp;
    }

    /**
     * Gets the value of bagValue
     * @return the value of bagValue
     */
    public String getBagValue()  {
        return bagValue;
    }

    /**
     * Sets the value of bagValue
     * @param bagValue value to assign to bagValue
     */
    public void setBagValue(String bagValue) {
        this.bagValue = bagValue;
    }

    /**
     * Gets the value of attributeOp
     * @return the value of attributeOp
     */
    public String getAttributeOp()  {
        return attributeOp;
    }

    /**
     * Sets the value of attributeOp
     * @param attributeOp value to assign to attributeOp
     */
    public void setAttributeOp(String attributeOp) {
        this.attributeOp = attributeOp;
    }

    /**
     * Gets the value of attributeValue
     * @return the value of attributeValue
     */
    public String getAttributeValue()  {
        return attributeValue;
    }

    /**
     * Sets the value of attributeValue
     * @param attributeValue value to assign to attributeValue
     */
    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    /**
     * Gets the value of extraValue
     * @return the value of extraValue
     */
    public String getExtraValue() {
        return extraValue;
    }

    /**
     * Sets the value of extraValue
     * @param extraValue the value to assign to extraValue
     */
    public void setExtraValue(String extraValue) {
        this.extraValue = extraValue;
    }

    /**
     * Gets the value of attributeValue
     * @return the value of attributeValue
     */
    public String getAttributeOptions()  {
        return attributeOptions;
    }

    /**
     * Sets the value of attributeOptions
     * @param attributeOptions value to assign to attributeOptions
     */
    public void setAttributeOptions(String attributeOptions) {
        this.attributeOptions = attributeOptions;
    }

    /**
     * Gets the value of subclassValue
     * @return the value of subclassValue
     */
    public String getSubclassValue()  {
        return subclassValue;
    }

    /**
     * Sets the value of subclassValue
     * @param subclassValue value to assign to subclassValue
     */
    public void setSubclassValue(String subclassValue) {
        this.subclassValue = subclassValue;
    }

    /**
     * Gets the value of path
     * @return the value of path
     */
    public String getPath()  {
        return path;
    }

    /**
     * Sets the value of path
     * @param path value to assign to path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Get the operator.
     * @return "and" or "or"
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Set the operator, "and" or "or"
     * @param operator the operator
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * Get the null/not null constraint value. Returned value will be
     * either "NULL" or "NotNULL".
     * @return null/not null constraint value
     */
    public String getNullConstraint() {
        return nullConstraint;
    }

    /**
     * Set the null/not null constraint. Parameter should be
     * either "NULL" or "NotNULL".
     * @param nullConstraint null/not null constraint
     */
    public void setNullConstraint(String nullConstraint) {
        this.nullConstraint = nullConstraint;
    }

    /**
     * Gets the value of parsedAttributeValue
     * @return the value of parsedAttributeValue
     */
    public Object getParsedAttributeValue()  {
        return parsedAttributeValue;
    }

    /**
     * Get the template identifier.
     * @return the template identifier
     */
    public String getTemplateId() {
        return templateId;
    }

    /**
     * Set the templat identifier.
     * @param templateId the template identifier
     */
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    /**
     * Get the template label.
     * @return the template label
     */
    public String getTemplateLabel() {
        return templateLabel;
    }

    /**
     * Set the template label.
     * @param templateLabel the template label
     */
    public void setTemplateLabel(String templateLabel) {
        this.templateLabel = templateLabel;
    }

    /**
     * Get the editable flag (when building a template).
     * @return whether this constraint is editable
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Set the editable flag (when building a template).
     * @param editable whether or not this constraint should be editable
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    /**
     * Sets the value of parsedAttributeValue
     *
     * @param parsedAttributeValue value to assign to parsedAttributeValue
     */
    public void setParsedAttributeValue(Object parsedAttributeValue) {
        this.parsedAttributeValue = parsedAttributeValue;
    }

    /**
     * {@inheritDoc}
     */
    public ActionErrors validate(@SuppressWarnings("unused") ActionMapping mapping,
                                 HttpServletRequest request) {
        HttpSession session = request.getSession();
        Locale locale = (Locale) session.getAttribute(Globals.LOCALE_KEY);
        PathQuery query = (PathQuery) session.getAttribute(Constants.QUERY);

        ActionErrors errors = new ActionErrors();
        ConstraintOp constraintOp = (getAttributeOp() == null) ? null
                    : ConstraintOp.getOpForIndex(Integer.valueOf(getAttributeOp()));

        if (request.getParameter("attribute") != null) {
            PathNode node = query.getNodes().get(path);
            Class fieldClass;
            if (node.isAttribute()) {
                fieldClass = TypeUtil.getClass(node.getType());
            } else {
                fieldClass = String.class;
            }
            parsedAttributeValue =
                parseValue(attributeValue, fieldClass, constraintOp, locale, errors);
        }

        if (errors.size() > 0) {
            session.setAttribute("editingNode", query.getNodes().get(path));
        }

        return errors;
    }

    /**
     * Parse an attribute value
     * @param value the value as a String
     * @param type the type of the parsed value
     * @param constraintOp the constraint operator for which value is an intended argument
     * @param locale the user's locale
     * @param errors ActionErrors to which any parse errors are added
     * @return the parsed value
     */
    public static Object parseValue(String value, Class type, ConstraintOp constraintOp,
                                    Locale locale, ActionMessages errors) {
        try {
            return ConstraintValueParser.parse(value, type, constraintOp);    
        } catch (ParseValueException ex) {
            errors.add(ActionErrors.GLOBAL_MESSAGE, 
                    new ActionMessage("errors.message", ex.getMessage()));
            return null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void reset(@SuppressWarnings("unused") ActionMapping mapping,
                      HttpServletRequest request) {
        bagOp = null;
        bagValue = null;
        attributeOp = null;
        attributeValue = null; // can be a checkbox
        subclassValue = null;
        path = null;
        nullConstraint = "NULL";
        templateLabel = "";
        templateId = "";
        editable = false;
        operator = SessionMethods.getDefaultOperator(request.getSession());
    }
}
