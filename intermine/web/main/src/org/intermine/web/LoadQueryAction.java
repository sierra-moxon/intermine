package org.intermine.web;

/*
 * Copyright (C) 2002-2005 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.StringReader;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

/**
 * Implementation of <strong>Action</strong> that sets the current Query for
 * the session from a saved Query.
 *
 * @author Kim Rutherford
 */
public class LoadQueryAction extends DispatchAction
{
    private static final Logger LOG = Logger.getLogger(LoadQueryAction.class);
    
    /**
     * Load a query ie. take a query from the exampleQueries and make it the current one.
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     *  an exception
     */
    public ActionForward example(ActionMapping mapping,
                              ActionForm form,
                              HttpServletRequest request,
                              HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        Map exampleQueries = (Map) servletContext.getAttribute(Constants.EXAMPLE_QUERIES);
        String queryName = request.getParameter("name");
        
        SessionMethods.logExampleQueryUse(session, queryName);
        SessionMethods.loadQuery((PathQuery) exampleQueries.get(queryName), session, response);
        
        return mapping.findForward("query");
    }
    
    /**
     * Load a query from path query XML passed as a request parameter.
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     *  an exception
     */
    public ActionForward xml(ActionMapping mapping,
                              ActionForm form,
                              HttpServletRequest request,
                              HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        Map exampleQueries = (Map) servletContext.getAttribute(Constants.EXAMPLE_QUERIES);
        String queryXml = request.getParameter("query");
        Boolean skipBuilder = Boolean.valueOf(request.getParameter("skipBuilder"));
        
        Map queries = PathQueryBinding.unmarshal(new StringReader(queryXml));
        PathQuery query = (PathQuery) queries.values().iterator().next();
        SessionMethods.loadQuery((PathQuery) query, session, response);
        
        if (!skipBuilder.booleanValue()) {
            return mapping.findForward("query");
        } else {
            QueryMonitorTimeout clientState
                    = new QueryMonitorTimeout(Constants.QUERY_TIMEOUT_SECONDS * 1000);
            MessageResources messages =
                (MessageResources) request.getAttribute(Globals.MESSAGES_KEY);
            String qid = SessionMethods.startQuery(clientState, session, messages, false);
            Thread.sleep(200); // slight pause in the hope of avoiding holding page
            return new ForwardParameters(mapping.findForward("waiting"))
                                .addParameter("qid", qid).forward();
        }
    }
}
