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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.profile.Profile;
import org.intermine.web.logic.search.SearchRepository;
import org.intermine.web.logic.session.SessionMethods;
import org.intermine.web.logic.tagging.TagTypes;
import org.intermine.web.logic.template.TemplateQuery;

/**
 * Make some change to a user template.
 *
 * @author Thomas Riley
 */
public class UserTemplateAction extends InterMineDispatchAction
{
    /**
     * Delete a template query.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     *  an exception
     */
    public ActionForward delete(ActionMapping mapping,
                                @SuppressWarnings("unused") ActionForm form,
                                HttpServletRequest request,
                                @SuppressWarnings("unused") HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        String templateName = request.getParameter("name");
        Profile profile = (Profile) session.getAttribute(Constants.PROFILE);

        TemplateQuery template = profile.getSavedTemplates().get(templateName);
        if (template != null) {
            recordMessage(new ActionMessage("templateList.deleted", templateName), request);
            profile.deleteTemplate(templateName);
            // If superuser then rebuild shared templates
            if (SessionMethods.isSuperUser(session)) {
                SearchRepository tr = SessionMethods.getGlobalSearchRepository(servletContext);
                tr.webSearchableRemoved(template, TagTypes.TEMPLATE);
            }
        } else {
            recordError(new ActionMessage("errors.template.nosuchtemplate"), request);
        }

         return new ForwardParameters(mapping.findForward("begin"))
        .addParameter("subtab", "templates").forward();

    }

}
