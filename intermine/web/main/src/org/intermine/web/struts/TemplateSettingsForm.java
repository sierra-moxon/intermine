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

import org.intermine.web.logic.Constants;
import org.intermine.web.logic.WebUtil;
import org.intermine.web.logic.template.TemplateBuildState;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Form used when building a template.
 *
 * @author Thomas Riley
 */
public class TemplateSettingsForm extends ActionForm
{
    private String description = "";
    private String name = "";
    private String title = "";
    private String comment = "";

    /**
     * Return the description.
     * @return the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Return the title
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Return the comment
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * Set the comment
     * @param comment the comment
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Reset the form bean taking initial state from current TemplateBuildState session
     * attribute.
     * {@inheritDoc}
     */
    public void reset(@SuppressWarnings("unused") ActionMapping mapping,
                      HttpServletRequest request) {
        TemplateBuildState tbs =
            (TemplateBuildState) request.getSession().getAttribute(Constants.TEMPLATE_BUILD_STATE);
        setName(tbs.getName());
        setTitle(tbs.getTitle());
        setDescription(tbs.getDescription());
        setComment(tbs.getComment());
    }

    /**
     * {@inheritDoc}
     */
    public ActionErrors validate(@SuppressWarnings("unused")  ActionMapping mapping,
                                 @SuppressWarnings("unused") HttpServletRequest request) {
        ActionErrors errors = null;
        if (!WebUtil.isValidName(name)) {
            errors = new ActionErrors();
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("errors.badChars"));
        }
        return errors;
    }
}
