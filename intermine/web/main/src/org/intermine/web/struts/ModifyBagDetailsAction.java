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

import java.util.Map;

import org.intermine.objectstore.query.BagConstraint;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.QueryClass;
import org.intermine.objectstore.query.SingletonResults;

import org.intermine.metadata.Model;
import org.intermine.model.InterMineObject;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreWriter;
import org.intermine.objectstore.intermine.ObjectStoreWriterInterMineImpl;
import org.intermine.path.Path;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.bag.InterMineBag;
import org.intermine.web.logic.config.WebConfig;
import org.intermine.web.logic.profile.Profile;
import org.intermine.web.logic.profile.ProfileManager;
import org.intermine.web.logic.query.MainHelper;
import org.intermine.web.logic.query.PathQuery;
import org.intermine.web.logic.results.PagedTable;
import org.intermine.web.logic.search.SearchRepository;
import org.intermine.web.logic.search.WebSearchable;
import org.intermine.web.logic.session.SessionMethods;
import org.intermine.web.logic.tagging.TagTypes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author Xavier Watkins
 *
 */
public class ModifyBagDetailsAction extends InterMineAction
{

    /**
     * Forward to the correct method based on the button pressed
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     *  an exception
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 @SuppressWarnings("unused") HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        Profile profile = (Profile) session.getAttribute(Constants.PROFILE);
        ServletContext servletContext = session.getServletContext();
        ProfileManager pm = (ProfileManager) servletContext.getAttribute(Constants.PROFILE_MANAGER);
        ObjectStore os = (ObjectStore) servletContext.getAttribute(Constants.OBJECTSTORE);
        ModifyBagDetailsForm mbdf = (ModifyBagDetailsForm) form;
    
        if (request.getParameter("remove") != null) {
            removeFromBag(mbdf.getBagName(), profile, mbdf, pm.getUserProfileObjectStore(), os,
                    session);
        } else {
            if (request.getParameter("showInResultsTable") != null) {
                return showBagInResultsTable(mbdf.getBagName(), mapping, session);
            } else if (request.getParameter("useBagInQuery").equals("true")) {
                
                String bagName = mbdf.getBagName();

                Map savedBags = profile.getSavedBags();
                InterMineBag imBag = (InterMineBag) savedBags.get(bagName);

                
                if (imBag == null) {
                    SearchRepository searchRepository =
                        SearchRepository.getGlobalSearchRepository(servletContext);
                    Map<String, ? extends WebSearchable> publicBagMap = 
                        searchRepository.getWebSearchableMap(TagTypes.BAG);
                    if (publicBagMap.get(bagName) != null) {
                        imBag = (InterMineBag) publicBagMap.get(bagName);
                    }
                }
                
                if (imBag == null) {
                    return mapping.findForward("errors");                    
                }
                Query q = new Query();
                QueryClass qc = new QueryClass(InterMineObject.class);
                q.addFrom(qc);
                q.addToSelect(qc);
                q.setConstraint(new BagConstraint(qc, ConstraintOp.IN, imBag.getOsb()));
                q.setDistinct(false);
                SingletonResults res = os.executeSingleton(q);

                Model model = os.getModel();
                Map classKeys = (Map) servletContext.getAttribute(Constants.CLASS_KEYS);
                WebConfig webConfig = (WebConfig) servletContext.getAttribute(Constants.WEBCONFIG);
                
                WebPathCollection webPathCollection =
                    new WebPathCollection(os, new Path(model, imBag.getType()), res, model, 
                                          webConfig, classKeys);

                PagedTable pagedColl = new PagedTable(webPathCollection);
                
                PathQuery pathQuery = MainHelper.webTableToPathQuery(pagedColl, model, imBag);
                session.setAttribute(Constants.QUERY, pathQuery);
                session.setAttribute("path", imBag.getType());
                session.setAttribute("prefix", imBag.getType());
                
                return mapping.findForward("query");
            }
        }
        return new ForwardParameters(mapping.findForward("bagDetails"))
                    .addParameter("bagName", mbdf.getBagName()).forward();
    }
    
    /**
     * remove the selected elements from the bag
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @exception Exception if the application business logic throws
     *  an exception
     */
    private void removeFromBag(String bagName, Profile profile, ModifyBagDetailsForm mbdf,
                               @SuppressWarnings("unused") ObjectStoreWriter uosw,
                               ObjectStore os, HttpSession session) throws Exception {
        Map savedBags = profile.getSavedBags();
        InterMineBag interMineBag = (InterMineBag) savedBags.get(bagName);
        ObjectStoreWriter osw = null;
        try {
            osw = new ObjectStoreWriterInterMineImpl(os);
            for (int i = 0; i < mbdf.getSelectedElements().length; i++) {
                osw
                    .removeFromBag(interMineBag.getOsb(),
                                   new Integer(mbdf.getSelectedElements()[i]));
            }
        } finally {
            if (osw != null) {
                osw.close();
            }
        }
        SessionMethods.invalidateBagTable(session, bagName);
    }

    private ActionForward showBagInResultsTable(String bagName, ActionMapping mapping,
                                                @SuppressWarnings("unused") HttpSession session) {
        return new ForwardParameters(mapping.findForward("bagResultsTable"))
            .addParameter("bagName", bagName)
            .addParameter("trail", "|bag." + bagName).forward();
    }
}
