package org.intermine.web.struts;

/*
 * Copyright (C) 2002-2007 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.intermine.objectstore.query.ConstraintOp;

import org.intermine.web.logic.Constants;
import org.intermine.web.logic.profile.Profile;
import org.intermine.web.logic.query.PathNode;
import org.intermine.web.logic.query.QueryMonitorTimeout;
import org.intermine.web.logic.session.SessionMethods;
import org.intermine.web.logic.template.TemplateHelper;
import org.intermine.web.logic.template.TemplateQuery;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

/**
 * @author Xavier Watkins
 *
 */
public class QuickSearchAction extends InterMineAction
{

    /**
     * Method called when user has submitted search form.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     *  an exception
     */
    @Override
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        ServletContext context = session.getServletContext();
        QuickSearchForm qsf = (QuickSearchForm) form;
        String qsType = qsf.getQuickSearchType();
        if (qsType.equals("ids")) {
            Map webPropertiesMap = (Map) context.getAttribute(Constants.WEB_PROPERTIES);
            
            String templateName = (String) webPropertiesMap.get("begin.browse.template");
            String templateType = "global";
            
            SessionMethods.logTemplateQueryUse(session, templateType, templateName);
            String userName = ((Profile) session.getAttribute(Constants.PROFILE)).getUsername();
            TemplateQuery template = TemplateHelper.findTemplate(context, session, userName,
                    templateName, templateType);
            QueryMonitorTimeout clientState = new QueryMonitorTimeout(Constants.
                                                                      QUERY_TIMEOUT_SECONDS * 1000);
            MessageResources messages = (MessageResources) request.getAttribute(
                                                                   Globals.MESSAGES_KEY);
            
            Map<String, Object> valuesMap = new HashMap <String, Object> ();
            Map <String, ConstraintOp> constraintOpsMap = new HashMap <String, ConstraintOp> ();

            PathNode node =   ((PathNode) template.getEditableNodes().get(0));

            valuesMap.put(node.getPathString(), qsf.getValue());
            constraintOpsMap.put(node.getPathString(), ConstraintOp.getOpForIndex(6));
            
            TemplateQuery queryCopy = TemplateHelper.editTemplate(valuesMap, 
                                                                  constraintOpsMap, template, null);
            String qid = SessionMethods.startQuery(clientState, session, messages, 
                                                   false, queryCopy);
            Thread.sleep(200);
            return new ForwardParameters(mapping.findForward("waiting"))
            .addParameter("qid", qid)
            .addParameter("trail", "")
            .forward();
        } else {
            Map hitMap = new LinkedHashMap();
            Map scopeMap = new LinkedHashMap();
            Map highlightedMap = new HashMap();
            Map descrMap = new HashMap();
            long time;
            if (qsType.equals("tpls")) {
                time = TemplateHelper.runLeuceneSearch(qsf.getValue(), "ALL", "template", 
                              ((Profile) session.getAttribute(Constants.PROFILE)), 
                              context, hitMap, scopeMap, highlightedMap, null, descrMap);
                request.setAttribute("type", "template");
            } else if (qsType.equals("bgs")) {
                time = TemplateHelper.runLeuceneSearch(qsf.getValue(), "ALL", "bag", 
                              ((Profile) session.getAttribute(Constants.PROFILE)), 
                              context, hitMap, scopeMap, highlightedMap, null, descrMap);
                request.setAttribute("type", "bag");
            } else {
                throw new RuntimeException("Quick search type not valid");
            }
            request.setAttribute("results", hitMap);
            request.setAttribute("resultScopes", scopeMap);
            request.setAttribute("highlighted", highlightedMap);
            request.setAttribute("querySeconds", new Float(time / 1000f));
            request.setAttribute("queryString", qsf.getValue());
            request.setAttribute("resultCount", new Integer(hitMap.size()));
            request.setAttribute("descriptions", descrMap);
            return mapping.findForward("search");
        }
    }
    
}
