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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.tiles.ComponentContext;
import org.apache.struts.tiles.actions.TilesAction;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.bag.InterMineBag;
import org.intermine.web.logic.profile.Profile;
import org.intermine.web.logic.results.DisplayObject;
import org.intermine.web.logic.results.InlineTemplateTable;
import org.intermine.web.logic.template.TemplateHelper;
import org.intermine.web.logic.template.TemplateQuery;

/**
 * Controller for an inline table created by running a template on an object details page.
 * @author Kim Rutherford
 */
public class ObjectDetailsTemplateController extends TilesAction
{
    /**
     * {@inheritDoc}
     */
    @Override
    public ActionForward execute(ComponentContext context,
                                 @SuppressWarnings("unused") ActionMapping mapping,
                                 @SuppressWarnings("unused") ActionForm form,
                                 HttpServletRequest request,
                                 @SuppressWarnings("unused") HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        DisplayObject displayObject = (DisplayObject) context.getAttribute("displayObject");
        InterMineBag interMineIdBag = (InterMineBag) context.getAttribute("interMineIdBag");

        if (displayObject == null && interMineIdBag == null) {
            return null;
        }

        TemplateQuery templateQuery = (TemplateQuery) context.getAttribute("templateQuery");
        String templateName = templateQuery.getName();

        Profile profile = (Profile) session.getAttribute(Constants.PROFILE);
        InlineTemplateTable itt = null;

        if (displayObject != null) {
            Integer objectId = displayObject.getObject().getId();
            itt =
                TemplateHelper.getInlineTemplateTable(servletContext, templateName,
                                                  objectId, profile);
        } else {
            itt =
                TemplateHelper.getInlineTemplateTable(servletContext, templateName,
                                                  interMineIdBag, profile);
        }

        if (itt != null) {
            context.putAttribute("table", itt);
        }

        return null;
    }
}
