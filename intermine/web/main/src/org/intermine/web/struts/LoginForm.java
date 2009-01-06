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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.profile.ProfileManager;

/**
 * The main form, using for editing constraints
 * @author Mark Woodbridge
 */
public class LoginForm extends ActionForm
{
    protected String username, password, returnToString;

    /**
     * Gets the value of username
     *
     * @return the value of username
     */
    public String getUsername()  {
        return username;
    }

    /**
     * Sets the value of username
     *
     * @param username value to assign to username
     */
    public void setUsername(String username) {
        this.username = username.toLowerCase();
    }

    /**
     * Gets the value of password
     *
     * @return the value of password
     */
    public String getPassword()  {
        return password;
    }

    /**
     * Sets the value of password
     *
     * @param password value to assign to password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the URL to return to if the login is successful.
     * @param returnToString the path to return to after log in
     */
    public void setReturnToString(String returnToString) {
        this.returnToString = returnToString;
    }

    /**
     * Return the returnToString set by setReturnToURL().
     * @return the the path to return to after log in
     */
    public String getReturnToString() {
        return returnToString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ActionErrors validate(@SuppressWarnings("unused") ActionMapping mapping,
                                 @SuppressWarnings("unused") HttpServletRequest request) {
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        ProfileManager pm = (ProfileManager) servletContext.getAttribute(Constants.PROFILE_MANAGER);

        ActionErrors errors = new ActionErrors();

        if (username.equals("")) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("login.emptyusername"));
        } else {
            if (pm.hasProfile(username)) {
                if (!pm.validPassword(username, password)) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE,
                               new ActionMessage("login.wrongpassword"));
                }
            } else {
                errors.add(ActionMessages.GLOBAL_MESSAGE,
                           new ActionMessage("login.invalidusername", username));
            }
        }

        return errors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset(@SuppressWarnings("unused") ActionMapping mapping,
                      @SuppressWarnings("unused") HttpServletRequest request) {
        username = null;
        password = null;
    }
}
