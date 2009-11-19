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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.intermine.pathquery.PathQuery;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.query.QueryMonitorTimeout;
import org.intermine.web.logic.session.SessionMethods;

/**
 * Action to run constructed query.
 *
 * @author Mark Woodbridge
 * @author Tom Riley
 */
public class ViewAction extends InterMineAction
{
    /**
     * Run the query and forward to the results page.
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     *  an exception
     */
    public ActionForward execute(ActionMapping mapping,
                                 @SuppressWarnings("unused") ActionForm form,
                                 HttpServletRequest request,
                                 @SuppressWarnings("unused") HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();

        ChangeTableSizeForm resultsForm =
            (ChangeTableSizeForm) session.getAttribute("changeTableForm");
        if (resultsForm != null) {
            resultsForm.reset(mapping, request);
        }

        QueryMonitorTimeout clientState
            = new QueryMonitorTimeout(Constants.QUERY_TIMEOUT_SECONDS * 1000);
        MessageResources messages = (MessageResources) request.getAttribute(Globals.MESSAGES_KEY);
        PathQuery pathQuery = ((PathQuery) session.getAttribute(Constants.QUERY)).clone();
        String qid = SessionMethods.startQuery(clientState, session, messages, true, pathQuery);

        Thread.sleep(200); // slight pause in the hope of avoiding holding page

        return new ForwardParameters(mapping.findForward("waiting"))
                            .addParameter("trail", "|query")
                            .addParameter("qid", qid).forward();
    }
}
