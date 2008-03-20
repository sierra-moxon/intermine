package org.intermine.web.struts;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Map;

import org.intermine.web.logic.Constants;
import org.intermine.web.logic.WebUtil;
import org.intermine.web.logic.bag.InterMineBag;
import org.intermine.web.logic.profile.Profile;
import org.intermine.web.logic.query.PathQueryBinding;
import org.intermine.web.logic.session.SessionMethods;

import java.io.StringReader;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorForm;

/**
 * Form bean representing query import form.
 *
 * @author  Thomas Riley
 */
public class ImportQueriesForm extends ValidatorForm
{
    private String xml;
    private Map map;
    private String queryBuilder;

    /**
     * Creates a new instance of ImportQueriesForm.
     */
    public ImportQueriesForm() {
        reset();
    }



    /**
     * Return a Map from query name to Query object.
     * @param savedBags map from bag name to bag
     * @param servletContext global ServletContext object
     * @return the Map
     */
    public Map getQueryMap(Map savedBags, ServletContext servletContext) {
        if (map == null) {
            try {
                map = PathQueryBinding.unmarshal(new StringReader(getXml()), savedBags,
                        SessionMethods.getClassKeys(servletContext));
            } catch (Exception e) {
                map = PathQueryBinding.unmarshal(new StringReader("<queries>" + getXml()
                         + "</queries>"), savedBags, SessionMethods.getClassKeys(servletContext));
            }
        }
        return map;
    }

    /**
     * Get the xml.
     * @return query in xml format
     */
    public String getXml() {
        return xml;
    }

    /**
     * Set the xml.
     * @param xml query in xml format
     */
    public void setXml(String xml) {
        this.xml = xml;
    }

    /**
     * Get the queryBuilder field.  If true and there is only one query submitted, the action will
     * redirect to the query builder rather than the saved query history page.
     * @return queryBuilder the queryBuilder field
     */
    public String getQueryBuilder() {
        return queryBuilder;
    }

    /**
     * Set the queryBuilder field.
     * @param queryBuilder the queryBuilder field
     */
    public void setQueryBuilder(String queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    /**
     * {@inheritDoc}
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        reset();
    }

    /**
     * Reset the form.
     */
    protected void reset() {
        xml = "";
        queryBuilder = "";
    }

    /**
     * Call inherited method then check whether xml is valid.
     *
     * {@inheritDoc}
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);
        if (errors != null && errors.size() > 0) {
            return errors;
        }
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        Profile profile = (Profile) session.getAttribute(Constants.PROFILE);

        try {
            Map<String, InterMineBag> allBags =
                WebUtil.getAllBags(profile.getSavedBags(), servletContext);
            if (getQueryMap(allBags, servletContext).size() == 0) {
               if (errors == null) {
                   errors = new ActionErrors();
               }
               errors.add(ActionErrors.GLOBAL_MESSAGE,
                           new ActionMessage("errors.importQuery.noqueries"));
           }
        } catch (Exception err) {
            if (errors == null) {
                errors = new ActionErrors();
            }
            errors.add(ActionErrors.GLOBAL_MESSAGE,
                        new ActionMessage("errors.importFailedException", err.getMessage()));
        }
        return errors;
    }
}
