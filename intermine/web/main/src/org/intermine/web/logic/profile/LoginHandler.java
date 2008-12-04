package org.intermine.web.logic.profile;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.intermine.objectstore.ObjectStoreException;
import org.intermine.objectstore.ObjectStoreWriter;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.bag.InterMineBag;
import org.intermine.web.logic.query.SavedQuery;
import org.intermine.web.struts.InterMineAction;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

/**
 * @author Xavier Watkins
 *
 */
public abstract class LoginHandler extends InterMineAction
{
    /**
     * Abstract class containing the methods for login in and copying current
     * history, bags,... into profile
     *
     * @param servletContext
     *            The servlet context
     * @param request
     *            The HttpServletRequest
     * @param response
     *            The HttpServletResponse
     * @param session
     *            The session
     * @param pm
     *            The profile manager
     * @param username
     *            The username
     * @param password
     *            The password
     * @return the map containing the renamed bags the user created before they were logged in
     */
    public Map doLogin(ServletContext servletContext, HttpServletRequest request,
            HttpServletResponse response, HttpSession session, ProfileManager pm, String username,
            String password) {
        // Merge current history into loaded profile
        Profile currentProfile = (Profile) session.getAttribute(Constants.PROFILE);
        ObjectStoreWriter uosw = ((ProfileManager) servletContext.getAttribute(
                    Constants.PROFILE_MANAGER)).getProfileObjectStoreWriter();
        String superuser = pm.getSuperuser();
        Map mergeQueries = Collections.EMPTY_MAP;
        Map mergeBags = Collections.EMPTY_MAP;
        if (currentProfile != null && StringUtils.isEmpty(currentProfile.getUsername())) {
            mergeQueries = new HashMap(currentProfile.getHistory());
            mergeBags = new HashMap(currentProfile.getSavedBags());
        }

        Profile profile;
        if (pm.hasProfile(username)) {
            profile = pm.getProfile(username, password);
        } else {
            profile = new Profile(pm, username, null, password, new HashMap(), new HashMap(),
                    new HashMap());
            pm.saveProfile(profile);
        }
        session.setAttribute(Constants.PROFILE, profile);

        if (profile.getUsername().equals(superuser)) {
            session.setAttribute(Constants.IS_SUPERUSER, Boolean.TRUE);
        }

        // Merge in anonymous query history
        for (Iterator iter = mergeQueries.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            SavedQuery query = (SavedQuery) entry.getValue();
            String name = makeUniqueQueryName((String) entry.getKey(), profile.getHistory()
                    .keySet());
            profile.saveHistory(query);
        }
        // Merge anonymous bags
        Map renamedBags = new HashMap();
        for (Iterator iter = mergeBags.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            InterMineBag bag = (InterMineBag) entry.getValue();
            // Make sure the userId gets set to be the profile one
            try {
                bag.setProfileId(profile.getUserId(), uosw);
                String name = makeUniqueQueryName((String) entry.getKey(), profile.getSavedBags()
                        .keySet());
                if (!((String) entry.getKey()).equals(name)) {
                    renamedBags.put((String) entry.getKey(), name);
                }
                bag.setName(name, uosw);
                profile.saveBag(name, bag);
            } catch (ObjectStoreException iex) {
                throw new RuntimeException(iex.getMessage());
            }
        }
        return renamedBags;
    }

    private String makeUniqueQueryName(String name, Set names) {
        String original = name;
        int i = 1;
        while (names.contains(name)) {
            name = original + "_" + i;
            i++;
        }
        return name;
    }

}
