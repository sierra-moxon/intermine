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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.pathquery.Constraint;
import org.intermine.pathquery.PathNode;
import org.intermine.pathquery.PathQuery;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.WebUtil;
import org.intermine.web.logic.bag.InterMineBag;
import org.intermine.web.logic.profile.Profile;
import org.intermine.web.logic.query.MainHelper;
import org.intermine.web.logic.search.SearchRepository;
import org.intermine.web.logic.session.SessionMethods;
import org.intermine.web.logic.template.TemplateBuildState;
import org.intermine.web.logic.template.TemplateHelper;
import org.intermine.web.logic.template.TemplateQuery;

/**
 * Action to create a new TemplateQuery from current query.
 *
 * @author Thomas Riley
 */
public class CreateTemplateAction extends InterMineAction
{
    protected static final Logger LOG = Logger.getLogger(CreateTemplateAction.class);

    /**
     * Take the current query and TemplateBuildState from the session and create a
     * TemplateQuery. Put the query in the user's profile.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     *
     * @exception Exception if the application business logic throws
     *  an exception
     */
    public ActionForward execute(ActionMapping mapping,
                                 @SuppressWarnings("unused") ActionForm form,
                                 HttpServletRequest request,
                                 @SuppressWarnings("unused") HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        Profile profile = (Profile) session.getAttribute(Constants.PROFILE);
        ObjectStore os = (ObjectStore) servletContext.getAttribute(Constants.OBJECTSTORE);
        PathQuery query = (PathQuery) session.getAttribute(Constants.QUERY);
        TemplateBuildState tbs =
            (TemplateBuildState) session.getAttribute(Constants.TEMPLATE_BUILD_STATE);

        boolean seenProblem = false;

        // Check whether query has at least one constraint and at least one output
        if (query.getView().size() == 0) {
            recordError(new ActionMessage("errors.createtemplate.nooutputs"), request);
            seenProblem = true;
        }
        if (query.getBagNames().size() != 0) {
            recordError(new ActionMessage("errors.createtemplate.templatewithlist"), request);
            seenProblem = true;
        }
        Iterator iter = query.getNodes().values().iterator();
        boolean foundEditableConstraint = false;
        boolean foundNonEditableLookup = false;
        while (iter.hasNext()) {
            PathNode node = (PathNode) iter.next();
            Iterator citer = node.getConstraints().iterator();
            while (citer.hasNext()) {
                Constraint c = (Constraint) citer.next();
                if (c.isEditable()) {
                    foundEditableConstraint = true;
                    break;
                }
                if (c.getOp().equals(ConstraintOp.LOOKUP)) {
                    foundNonEditableLookup = true;
                }

            }
        }

        // template must have at least one editable constrain
        if (!foundEditableConstraint) {
            recordError(new ActionMessage("errors.createtemplate.noconstraints"), request);
            seenProblem = true;
        }

        // template cannot have non-editable LOOKUP constraints
        if (foundNonEditableLookup) {
            recordError(new ActionMessage("errors.createtemplate.noneditablelookup"), request);
            seenProblem = true;
        }

        // Check for a name clash with system templates
        Profile superUser = SessionMethods.getSuperUserProfile(servletContext);
        if (!superUser.equals(profile)) {
            if (superUser.getSavedTemplates().containsKey(tbs.getName())) {
                recordError(new ActionMessage("errors.createtemplate.existing", tbs.getName()),
                        request);
                seenProblem = true;
            }
        }

        // Check whether there is a template name clash
        if (profile.getSavedTemplates().containsKey(tbs.getName())
                && (tbs.getUpdatingTemplate() == null
                    || !tbs.getUpdatingTemplate().getName().equals(tbs.getName()))) {
            recordError(new ActionMessage("errors.createtemplate.existing", tbs.getName()),
                    request);
            seenProblem = true;
        }

        if (StringUtils.isEmpty(tbs.getName())) {
            recordError(new ActionMessage("errors.required", "Template name"), request);
            seenProblem = true;
        } else if (!WebUtil.isValidName(tbs.getName())) {
            recordError(new ActionMessage("errors.badChars"), request);
            seenProblem = true;
        }

        // Ensure that we can actually execute the query
        if (!seenProblem) {
            try {
                if (query.getInfo() == null) {
                    Map<String, InterMineBag> allBags =
                        WebUtil.getAllBags(profile.getSavedBags(), 
                        SessionMethods.getGlobalSearchRepository(servletContext));
                    query.setInfo(os.estimate(MainHelper.makeQuery(query, allBags,
                                    servletContext, null)));
                }
            } catch (ObjectStoreException e) {
                recordError(new ActionMessage("errors.query.objectstoreerror"), request, e, LOG);
                seenProblem = true;
            }
        }

        if (seenProblem) {
            return mapping.findForward("query");
        }

        // no problems!  TODO this should be updated somewhere else
        query.setProblems(new ArrayList<Throwable>());

        TemplateQuery template = TemplateHelper.buildTemplateQuery(tbs, query);
        TemplateQuery editing = tbs.getUpdatingTemplate();

        String key = (editing == null) ? "templateBuilder.templateCreated"
                                       : "templateBuilder.templateUpdated";

        recordMessage(new ActionMessage(key, template.getName()), request);

        // Replace template if needed
        if (editing != null) {
            profile.deleteTemplate(editing.getName());
        }
        profile.saveTemplate(template.getName(), template);
        // If superuser then rebuild shared templates
        if (SessionMethods.isSuperUser(session)) {
            SearchRepository tr = SessionMethods.getGlobalSearchRepository(servletContext);
            if (editing != null) {
                tr.webSearchableUpdated(template);
            } else {
                tr.webSearchableAdded(template);
            }
        }

        session.removeAttribute(Constants.TEMPLATE_BUILD_STATE);

        return new ForwardParameters(mapping.findForward("mymine"))
        .addParameter("subtab", "templates").forward();
    }

}
