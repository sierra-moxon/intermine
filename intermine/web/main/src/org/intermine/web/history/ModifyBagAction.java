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

import org.intermine.objectstore.ObjectStore;
import org.intermine.web.Constants;
import org.intermine.web.Profile;
import org.intermine.web.SessionMethods;
import org.intermine.web.WebUtil;
import org.intermine.web.bag.BagHelper;
import org.intermine.web.bag.InterMineBag;
import org.intermine.web.bag.InterMinePrimitiveBag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * Implementation of <strong>Action</strong> to modify bags
 * @author Mark Woodbridge
 */
public class ModifyBagAction extends ModifyHistoryAction
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
                                 HttpServletResponse response)
        throws Exception {
        ActionForward af = super.execute(mapping, form, request, response);
        if (af != null) {
            return af;
        }
        if (request.getParameter("union") != null) {
            union(mapping, form, request, response);
        } else if (request.getParameter("intersect") != null) {
            intersect(mapping, form, request, response);
        } else if (request.getParameter("subtract") != null) {
            subtract(mapping, form, request, response);
        } else if (request.getParameter("delete") != null) {
            delete(mapping, form, request, response);
        }
        
        return mapping.findForward("bag");
    }

    /**
     * Union the selected bags
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     *  an exception
     */
    public ActionForward union(ActionMapping mapping,
                               ActionForm form,
                               HttpServletRequest request,
                               HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        Profile profile = (Profile) session.getAttribute(Constants.PROFILE);
        ModifyBagForm mbf = (ModifyBagForm) form;
        ServletContext servletContext = session.getServletContext();
        ObjectStore os = (ObjectStore) servletContext.getAttribute(Constants.OBJECTSTORE);
        
        Map savedBags = profile.getSavedBags();
        String[] selectedBags = mbf.getSelectedBags();
        
        if (!typesMatch(savedBags, selectedBags)) {
            recordError(new ActionMessage("bag.typesDontMatch"), request);
            return mapping.findForward("history");
        }

        // Now combine
        String name = BagHelper.findNewBagName(savedBags, mbf.getNewBagName());
        ObjectStore profileOs = profile.getProfileManager().getUserProfileObjectStore();
        Collection union = new ArrayList();
        union.addAll((Collection) savedBags.get(selectedBags[0]));
        for (int i = 1; i < selectedBags.length; i++) {
            union.addAll((Collection) savedBags.get(selectedBags[i]));
        }
        InterMineBag combined =
            new InterMinePrimitiveBag(profile.getUserId(), name, profileOs, union);
        
        int defaultMax = 10000;

        int maxBagSize = WebUtil.getIntSessionProperty(session, "max.bag.size", defaultMax);

        if (combined.size () > maxBagSize) {
            ActionMessage actionMessage =
                new ActionMessage("bag.tooBig", new Integer(maxBagSize));
            recordError(actionMessage, request);

            return mapping.findForward("bag");
        }

        profile.saveBag(name, combined);
        
        return mapping.findForward("bag");
    }
    
    /**
     * Given a set of bag names, find out whether they are all of the same type.
     * 
     * @param bags map from bag name to InterMineBag subclass
     * @param selectedBags names of bags to match
     * @return true if all named bags are of the same type, false if not
     */
    private static boolean typesMatch(Map bags, String selectedBags[]) {
        // Check that all selected bags are of the same type
        Class type = bags.get(selectedBags[0]).getClass();
        for (int i = 1; i < selectedBags.length; i++) {
            if (bags.get(selectedBags[i]).getClass() != type) {
                return false;
            }
        }
        return true;
    }

    /**
     * Intersect the selected bags
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     *  an exception
     */
    public ActionForward intersect(ActionMapping mapping,
                                   ActionForm form,
                                   HttpServletRequest request,
                                   HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        ObjectStore os = (ObjectStore) servletContext.getAttribute(Constants.OBJECTSTORE);
        Profile profile = (Profile) session.getAttribute(Constants.PROFILE);
        ModifyBagForm mbf = (ModifyBagForm) form;

        Map savedBags = profile.getSavedBags();
        String[] selectedBags = mbf.getSelectedBags();
        
        if (!typesMatch(savedBags, selectedBags)) {
            recordError(new ActionMessage("bag.typesDontMatch"), request);
            return mapping.findForward("bag");
        }
        
        Collection intersect = new ArrayList();
        intersect.addAll((Collection) savedBags.get(selectedBags[0]));
        for (int i = 1; i < selectedBags.length; i++) {
            intersect.retainAll((Collection) savedBags.get(selectedBags[i]));
        }

        String name = BagHelper.findNewBagName(savedBags, mbf.getNewBagName());
        ObjectStore profileOs = profile.getProfileManager().getUserProfileObjectStore();
        InterMineBag combined =
            new InterMinePrimitiveBag(profile.getUserId(), name, profileOs, intersect);

        profile.saveBag(name, combined);
        
        return mapping.findForward("bag");
    }

    /**
     * Compute the set of objects that are in only one of the selected bags.
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     *  an exception
     */
    public ActionForward subtract(ActionMapping mapping,
                                  ActionForm form,
                                  HttpServletRequest request,
                                  HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        ObjectStore os = (ObjectStore) servletContext.getAttribute(Constants.OBJECTSTORE);
        Profile profile = (Profile) session.getAttribute(Constants.PROFILE);
        ModifyBagForm mbf = (ModifyBagForm) form;

        Map savedBags = profile.getSavedBags();
        String[] selectedBags = mbf.getSelectedBags();
        String name = BagHelper.findNewBagName(savedBags, mbf.getNewBagName());
        
        if (!typesMatch(savedBags, selectedBags)) {
            recordError(new ActionMessage("bag.typesDontMatch"), request);
            return mapping.findForward("bag");
        }
        
        // A map from objects to the number of occurrences of that object
        Map countMap = new HashMap();

        for (int i = 0; i < selectedBags.length; i++) {
            Iterator iter = ((Collection) savedBags.get(selectedBags[i])).iterator();
            while (iter.hasNext()) {
                Object thisObj = iter.next();
                if (countMap.containsKey(thisObj)) {
                    int newVal = ((Integer) countMap.get(thisObj)).intValue() + 1;
                    countMap.put(thisObj, new Integer (newVal));
                } else {
                    countMap.put(thisObj, new Integer(1));
                }
            }
        }

        Collection subtract = new ArrayList();
        Iterator iter = countMap.keySet().iterator();
        while (iter.hasNext()) {
            Object thisObj = iter.next();
            if (countMap.get(thisObj).equals(new Integer(1))) {
                subtract.add(thisObj);
            }
        }
        
        ObjectStore profileOs = profile.getProfileManager().getUserProfileObjectStore();
        InterMineBag resultBag =
            new InterMinePrimitiveBag(profile.getUserId(), name, profileOs, subtract);
        profile.saveBag(name, resultBag);

        return mapping.findForward("bag");
    }

    /**
     * Delete the selected bags
     *
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     *  an exception
     */
    public ActionForward delete(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                HttpServletResponse response)
        throws Exception {
        HttpSession session = request.getSession();
        Profile profile = (Profile) session.getAttribute(Constants.PROFILE);

        ModifyBagForm mbf = (ModifyBagForm) form;
        for (int i = 0; i < mbf.getSelectedBags().length; i++) {
            SessionMethods.invalidateBagTable(session, mbf.getSelectedBags()[i]);
            profile.deleteBag(mbf.getSelectedBags()[i]);
        }

        return mapping.findForward("bag");
    }
}
