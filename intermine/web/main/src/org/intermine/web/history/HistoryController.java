package org.intermine.web.history;

/*
 * Copyright (C) 2002-2005 FlyMine
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

import org.intermine.web.Constants;
import org.intermine.web.ProfileManager;
import org.intermine.web.Profile;
import org.intermine.web.SessionMethods;

import org.apache.commons.lang.StringUtils;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.tiles.ComponentContext;
import org.apache.struts.tiles.actions.TilesAction;

/**
 * Tiles controller for history tile (page).
 * 
 * @author Thomas Riley
 */
public class HistoryController extends TilesAction
{
    /**
     * 
     * @see TilesAction#execute
     */
    public ActionForward execute(ComponentContext context,
                                 ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        ProfileManager pm = SessionMethods.getProfileManager(servletContext);
        String page = request.getParameter("page");

        
        if (!StringUtils.isEmpty(page)) {
            session.setAttribute(Constants.HISTORY_PAGE, page);
        }

        if (page != null) {
            if (page.equals("templates")) {
                // prime the tags cache so that the templates tags will be quick to access
                String userName = ((Profile) session.getAttribute(Constants.PROFILE)).getUsername();
                if (userName != null) {
                    // discard result
                    pm.getTags(null, null, "template", userName);
                }
            }
        }

        return null;
    }
}
