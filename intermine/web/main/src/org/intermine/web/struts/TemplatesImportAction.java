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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.intermine.pathquery.PathQuery;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.WebUtil;
import org.intermine.web.logic.bag.InterMineBag;
import org.intermine.web.logic.profile.Profile;
import org.intermine.web.logic.search.SearchRepository;
import org.intermine.web.logic.session.SessionMethods;
import org.intermine.web.logic.tagging.TagTypes;
import org.intermine.web.logic.template.TemplateHelper;
import org.intermine.web.logic.template.TemplateQuery;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * Imports templates in XML format.
 *
 * @author Thomas Riley
 */
public class TemplatesImportAction extends InterMineAction
{
    /**
     * {@inheritDoc}
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 @SuppressWarnings("unused") HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        Profile profile = (Profile) session.getAttribute(Constants.PROFILE);
        TemplatesImportForm tif = (TemplatesImportForm) form;
        Map templates = null;
        int deleted = 0, imported = 0, renamed = 0;
        Map<String, InterMineBag> allBags = WebUtil.getAllBags(profile.getSavedBags(), 
                SessionMethods.getGlobalSearchRepository(servletContext));
        templates = TemplateHelper.xmlToTemplateMap(tif.getXml(), allBags,
                PathQuery.USERPROFILE_VERSION);

        try {
            profile.disableSaving();

            if (tif.isOverwriting()
                            && profile.getSavedTemplates().size() > 0) {
                Iterator iter = new HashSet(profile.getSavedTemplates().keySet()).iterator();
                while (iter.hasNext()) {
                    profile.deleteTemplate((String) iter.next());
                    deleted++;
                }
            }

            Iterator iter = templates.values().iterator();
            while (iter.hasNext()) {
                TemplateQuery template = (TemplateQuery) iter.next();

                String templateName = template.getName();
                if (!WebUtil.isValidName(templateName)) {
                    templateName = WebUtil.replaceSpecialChars(templateName);
                    renamed++;
                }
                templateName = validateQueryName(templateName, profile);
                template = renameTemplate(templateName, template);
                profile.saveTemplate(templateName, template);
                imported++;
            }

            if (SessionMethods.isSuperUser(session)) {
                SearchRepository tr = SessionMethods.getGlobalSearchRepository(servletContext);
                tr.globalChange(TagTypes.TEMPLATE);
            }

            recordMessage(new ActionMessage("importTemplates.done",
                                            new Integer(deleted),
                                            new Integer(imported),
                                            new Integer(renamed)),
                                            request);

            return new ForwardParameters(mapping.findForward("mymine"))
            .addParameter("subtab", "templates").forward();

        } finally {
            profile.enableSaving();
        }
    }

    // rebuild the template, but with the new special-character-free name
    private TemplateQuery renameTemplate(String newName, TemplateQuery template) {

        TemplateQuery newTemplate = new TemplateQuery(newName, template.getTitle(),
                                                      template.getDescription(),
                                                      template.getComment(),
                                                      template.getPathQuery(),
                                                      template.getKeywords());

        return newTemplate;
    }

    /**
     * Checks that the query name doesn't already exist and returns a numbered
     * name if it does.
     * @param queryName the query name
     * @param profile the user profile
     * @return a validated name for the query
     */
    private String validateQueryName(String queryName, Profile profile) {
        String newQueryName = queryName;

        if (!WebUtil.isValidName(queryName)) {
            newQueryName = WebUtil.replaceSpecialChars(newQueryName);
        }

        if (profile.getSavedTemplates().containsKey(newQueryName)) {
            int i = 1;
            while (true) {
                String testName = newQueryName + "_" + i;
                if (!profile.getSavedTemplates().containsKey(testName)) {
                    return testName;
                }
                i++;
            }
        } else {
            return newQueryName;
        }
    }

}
