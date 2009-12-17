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

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.intermine.api.InterMineAPI;
import org.intermine.api.bag.BagManager;
import org.intermine.api.profile.InterMineBag;
import org.intermine.api.profile.Profile;
import org.intermine.api.search.SearchRepository;
import org.intermine.api.tag.TagTypes;
import org.intermine.api.template.TemplateQuery;
import org.intermine.api.util.NameUtil;
import org.intermine.pathquery.PathQuery;
import org.intermine.web.logic.session.SessionMethods;
import org.intermine.web.logic.template.TemplateHelper;

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
        final InterMineAPI im = SessionMethods.getInterMineAPI(session);
        
        ServletContext servletContext = session.getServletContext();
        Profile profile = SessionMethods.getProfile(session);
        TemplatesImportForm tif = (TemplatesImportForm) form;

        int deleted = 0, imported = 0, renamed = 0;
        BagManager bagManager = im.getBagManager();
        Map<String, InterMineBag> allBags = bagManager.getUserAndGlobalBags(profile);

        Map<String, TemplateQuery> templates = 
            TemplateHelper.xmlToTemplateMap(tif.getXml(), allBags, PathQuery.USERPROFILE_VERSION);

        try {
            profile.disableSaving();

            if (tif.isOverwriting() && profile.getSavedTemplates().size() > 0) {
                for (String templateName : profile.getSavedTemplates().keySet()) {
                    profile.deleteTemplate(templateName);
                    deleted++;
                }
            }

            for (TemplateQuery template : templates.values()) {
                String templateName = template.getName();

                String updatedName = NameUtil.validateName(profile.getSavedTemplates().keySet(),
                        templateName);
                if (!templateName.equals(updatedName)) {
                    template = renameTemplate(updatedName, template);
                }
                profile.saveTemplate(template.getName(), template);
                imported++;
            }

            if (SessionMethods.isSuperUser(session)) {
                SearchRepository sr = SessionMethods.getGlobalSearchRepository(servletContext);
                sr.globalChange(TagTypes.TEMPLATE);
            }

            recordMessage(new ActionMessage("importTemplates.done", new Integer(deleted),
                        new Integer(imported), new Integer(renamed)), request);

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
                                                      template.getPathQuery());

        return newTemplate;
    }
}
