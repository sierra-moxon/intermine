package org.intermine.web.struts;

/*
 * Copyright (C) 2002-2010 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.tiles.ComponentContext;
import org.apache.struts.tiles.actions.TilesAction;
import org.intermine.api.InterMineAPI;
import org.intermine.api.profile.Profile;
import org.intermine.api.template.TemplateQuery;
import org.intermine.pathquery.Path;
import org.intermine.pathquery.PathConstraint;
import org.intermine.pathquery.PathQuery;
import org.intermine.web.autocompletion.AutoCompleter;
import org.intermine.web.logic.query.DisplayConstraint;
import org.intermine.web.logic.query.DisplayConstraintFactory;
import org.intermine.web.logic.querybuilder.DisplayPath;
import org.intermine.web.logic.session.SessionMethods;

/**
 * Controller for the main constraint editing tile
 * @author Thomas Riley
 */
public class QueryBuilderConstraintController extends TilesAction
{
    /**
     * {@inheritDoc}
     */
    @Override
    public ActionForward execute(@SuppressWarnings("unused") ComponentContext context,
            @SuppressWarnings("unused") ActionMapping mapping,
            @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();

        //to prevent submit twice
        saveToken(request);

        Profile profile = SessionMethods.getProfile(session);
        PathQuery query = SessionMethods.getQuery(session);
        DisplayConstraintFactory factory = getFactory(session);


        if (session.getAttribute("newConstraintPath") != null) {
            // ADDING A NEW CONSTRAINT
            DisplayPath displayPath = (DisplayPath) session.getAttribute("newConstraintPath");
            DisplayConstraint displayConstraint =
                factory.get(displayPath.getPath(), profile, query);
            request.setAttribute("dec", displayConstraint);
            session.removeAttribute("newConstraintPath");
        } else if (session.getAttribute("editingConstraint") != null) {
            // EDITING AN EXISTING CONSTRAINT
            PathConstraint con = (PathConstraint) session.getAttribute("editingConstraint");
            DisplayConstraint displayConstraint;
            if (query instanceof TemplateQuery) {
                TemplateQuery template = (TemplateQuery) query;
                displayConstraint = factory.get(con, profile, template);
            }
            else {
                displayConstraint = factory.get(con, profile, query);
            }

            request.setAttribute("dec", displayConstraint);
            session.removeAttribute("editingConstraint");
            if (session.getAttribute("editingTemplateConstraint") != null) {
                SessionMethods.moveToRequest("editingTemplateConstraint", request);
            } else if (session.getAttribute("editingSwitchableConstraint") != null) {
                SessionMethods.moveToRequest("editingSwitchableConstraint", request);
            }
        } else if (session.getAttribute("joinStylePath") != null) {
            // ONLY EDITING JOIN STYLE
            String joinStylePathStr = (String) session.getAttribute("joinStylePath");
            Path joinStylePath = query.makePath(joinStylePathStr);
            DisplayConstraint displayConstraint = factory.get(joinStylePath, profile, query);
            session.removeAttribute("joinStylePath");

            if (query.getOuterMap().containsKey(joinStylePathStr)) {
                request.setAttribute("joinType", "outer");
            } else {
                request.setAttribute("joinType", "inner");
            }
            request.setAttribute("dec", displayConstraint);
            request.setAttribute("joinStyleOnly", "true");
        }

        return null;
    }

    private DisplayConstraintFactory getFactory(HttpSession session) {
        InterMineAPI im = SessionMethods.getInterMineAPI(session);
        AutoCompleter ac = SessionMethods.getAutoCompleter(session.getServletContext());
        DisplayConstraintFactory factory =
            new DisplayConstraintFactory(ac, im.getObjectStoreSummary(),
                    im.getBagQueryConfig(), im.getBagManager(), im.getClassKeys());
        return factory;
    }
}
