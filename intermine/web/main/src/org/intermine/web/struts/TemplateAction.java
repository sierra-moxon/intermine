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

import org.apache.struts.Globals;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.util.MessageResources;
import org.intermine.api.bag.BagManager;
import org.intermine.api.profile.InterMineBag;
import org.intermine.api.profile.Profile;
import org.intermine.api.search.Scope;
import org.intermine.api.template.TemplateManager;
import org.intermine.api.template.TemplateQuery;
import org.intermine.pathquery.PathQueryUtil;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.query.QueryMonitorTimeout;
import org.intermine.web.logic.session.SessionMethods;
import org.intermine.web.logic.template.TemplateHelper;
import org.intermine.web.util.URLGenerator;
import org.intermine.webservice.server.template.result.TemplateResultLinkGenerator;

/**
 * Action to handle submit from the template page. <code>setSavingQueries</code>
 * can be used to set whether or not queries run by this action are automatically
 * saved in the user's query history. This property is true by default.
 *
 * @author Mark Woodbridge
 * @author Thomas Riley
 */
public class TemplateAction extends InterMineAction
{
    /** Name of skipBuilder parameter **/
    public static final String SKIP_BUILDER_PARAMETER = "skipBuilder";

    /** path of TemplateAction action **/
    public static final String TEMPLATE_ACTION_PATH = "templateAction.do";

    /**
     * Build a query based on the template and the input from the user.
     * There are some request parameters that, if present, effect the behaviour of
     * the action. These are:
     *
     * <dl>
     * <dt>skipBuilder</dt>
     *      <dd>If this attribute is specifed (with any value) then the action will forward
     *      directly to the object details page if the results contain just one object.</dd>
     * <dt>noSaveQuery</dt>
     *      <dd>If this attribute is specifed (with any value) then the query is not
     *      automatically saved in the user's query history.</dd>
     * </dl>
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
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
        throws Exception {
        TemplateForm tf = (TemplateForm) form;
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        String templateName = tf.getName();
        String templateType = tf.getType();
        boolean saveQuery = (request.getParameter("noSaveQuery") == null);
        boolean skipBuilder = (request.getParameter(SKIP_BUILDER_PARAMETER) != null);
        boolean editTemplate = (request.getParameter("editTemplate") != null);
        boolean editQuery = (request.getParameter("editQuery") != null);

        SessionMethods.logTemplateQueryUse(session, templateType, templateName);

        Profile profile = (Profile) session.getAttribute(Constants.PROFILE);
        
        TemplateManager templateManager = SessionMethods.getTemplateManager(session);

        TemplateQuery template = templateManager.getTemplate(profile, templateName, templateType);

        BagManager bagManager = SessionMethods.getBagManager(servletContext);
        Map<String, InterMineBag> savedBags = bagManager.getUserAndGlobalBags(profile);

        if (!editQuery && !skipBuilder && !editTemplate && forwardToLinksPage(request)) {
            TemplateQuery configuredTmpl = TemplateHelper.templateFormToTemplateQuery(tf, template,
                    savedBags);
            TemplateResultLinkGenerator gen = new TemplateResultLinkGenerator();
            String htmlLink = gen.getHtmlLink(new URLGenerator(request).getPermanentBaseURL(),
                    configuredTmpl);
            String tabLink = gen.getTabLink(new URLGenerator(request).getPermanentBaseURL(),
                    configuredTmpl);
            if (gen.getError() != null) {
                recordError(new ActionMessage("errors.linkGenerationFailed",
                        gen.getError()), request);
                return mapping.findForward("template");
            }
            session.setAttribute("htmlLink", htmlLink);
            session.setAttribute("tabLink", tabLink);
            String url = new URLGenerator(request).getPermanentBaseURL();
            session.setAttribute("highlightedLink", gen.getHighlightedLink(url, configuredTmpl));
            String title = configuredTmpl.getTitle();
            title = title.replace("-->", "&nbsp;<img src=\"images/tmpl_arrow.png\" "
                                  + "style=\"vertical-align:middle\">&nbsp;");
            session.setAttribute("pageTitle", title);
            session.setAttribute("pageDescription", configuredTmpl.getDescription());
            return mapping.findForward("serviceLink");

        }

        // We're editing the query: load as a PathQuery
        if (!skipBuilder && !editTemplate) {
            TemplateQuery queryCopy = TemplateHelper.templateFormToTemplateQuery(tf, template,
                                                                                 savedBags);
            SessionMethods.loadQuery(queryCopy.getPathQuery(), request.getSession(), response);
            session.removeAttribute(Constants.TEMPLATE_BUILD_STATE);
            form.reset(mapping, request);
            return mapping.findForward("query");
        } else if (editTemplate) {
            // We want to edit the template: Load the query as a TemplateQuery
            // Don't care about the form
            // Reload the initial template
            
           template = templateManager.getTemplate(profile, templateName, Scope.ALL);

           if (template == null) {
                recordMessage(new ActionMessage("errors.edittemplate.empty"), request);
                return mapping.findForward("template");
            }
            SessionMethods.loadQuery(template, request.getSession(), response);
            if (!template.isValid()) {
                recordError(new ActionError("errors.template.badtemplate",
                        PathQueryUtil.getProblemsSummary(template.getProblems())), request);
            }

            return mapping.findForward("query");
        }

        // Otherwise show the results: load the modified query from the template
        TemplateQuery queryCopy = TemplateHelper.templateFormToTemplateQuery(tf, template,
                                                                             savedBags);
        if (!queryCopy.isValid()) {
            recordError(new ActionError("errors.template.badtemplate",
                                        PathQueryUtil.getProblemsSummary(template.getProblems())),
                                        request);
            return mapping.findForward("template");
        }
        if (saveQuery) {
            SessionMethods.loadQuery(queryCopy, request.getSession(), response);
        }
        form.reset(mapping, request);

        QueryMonitorTimeout clientState = new QueryMonitorTimeout(
                Constants.QUERY_TIMEOUT_SECONDS * 1000);
        MessageResources messages = (MessageResources) request.getAttribute(Globals.MESSAGES_KEY);
        String qid = SessionMethods.startQuery(clientState, session, messages,
                                               saveQuery, queryCopy);
        Thread.sleep(200);

        String trail = "";

        // only put query on the trail if we are saving the query
        // otherwise its a "super top secret" query, e.g. quick search
        // also, note we are not saving any previous trails.  trail resets at queries and bags
        if (saveQuery) {
            trail = "|query";
        } else {
            trail = "";
            //session.removeAttribute(Constants.QUERY);
        }

        return new ForwardParameters(mapping.findForward("waiting"))
                .addParameter("qid", qid)
                .addParameter("trail", trail)
                .forward();
    }

    /**
     * Methods looks at request parameters if should forward to web service links page.
     * @param request request
     * @return true if should be forwarded
     */
    private boolean forwardToLinksPage(HttpServletRequest request) {
        return "links".equalsIgnoreCase(request.getParameter("actionType"));
    }

}
