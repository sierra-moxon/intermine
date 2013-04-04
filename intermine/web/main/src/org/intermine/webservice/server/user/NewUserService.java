package org.intermine.webservice.server.user;

/*
 * Copyright (C) 2002-2013 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import org.directwebremoting.util.Logger;
import org.intermine.api.InterMineAPI;
import org.intermine.api.profile.Profile;
import org.intermine.api.profile.ProfileManager;
import org.intermine.util.Emailer;
import org.intermine.util.MailUtils;
import org.intermine.web.context.InterMineContext;
import org.intermine.webservice.server.core.JSONService;
import org.intermine.webservice.server.core.RateLimitHistory;
import org.intermine.webservice.server.exceptions.BadRequestException;
import org.intermine.webservice.server.exceptions.InternalErrorException;
import org.intermine.webservice.server.exceptions.RateLimitException;
import org.intermine.webservice.server.output.JSONFormatter;
import org.json.JSONObject;

/**
 * Service for creating a new user. Requires a new, unique username, and a non-blank
 * password.
 * @author Alex Kalderimis.
 *
 */
public class NewUserService extends JSONService
{

    private static final Logger LOG = Logger.getLogger(NewUserService.class);
    private int maxNewUsersPerAddressPerHour = 1000;
    private static RateLimitHistory requestHistory = null;

    /**
     * Constructor.
     * @param im The InterMine API object.
     */
    public NewUserService(InterMineAPI im) {
        super(im);
        if (requestHistory == null) {
            Properties webProperties = InterMineContext.getWebProperties();
            String rateLimit = webProperties.getProperty("webservice.newuser.ratelimit");
            if (rateLimit != null) {
                try {
                    maxNewUsersPerAddressPerHour = Integer.valueOf(rateLimit.trim()).intValue();
                } catch (NumberFormatException e) {
                    LOG.error("Configured new user rate limit is not a valid integer. Defaulting to 1000 per hour", e);
                    maxNewUsersPerAddressPerHour = 1000;
                }
            }
            requestHistory = new RateLimitHistory((60 * 60), maxNewUsersPerAddressPerHour);
        }
    }

    @Override
    protected void validateState() {
        super.validateState();
        final String ipAddr = request.getRemoteAddr();
        if (!requestHistory.isWithinLimit(ipAddr)) {
            throw new RateLimitException(ipAddr, maxNewUsersPerAddressPerHour);
        }
        // Record this request...
        requestHistory.recordRequest(ipAddr);
    }

    @Override
    protected void execute() throws Exception {
        ProfileManager pm = im.getProfileManager();
        NewUserInput input = new NewUserInput();

        pm.createNewProfile(input.getUsername(), input.getPassword());

        JSONObject user = new JSONObject();
        user.put("username", input.getUsername());
        
        Emailer emailer = InterMineContext.getEmailer();

        try {
            emailer.welcome(input.getUsername());
            String mailingList = null;
            if (input.subscribeToList()) {
            	mailingList = emailer.subscribeToList(input.getUsername());
            }
            user.put("subscribedToList", mailingList != null);
            user.put("mailingList", mailingList);
        } catch (Exception e) {
            LOG.error("Failed to send confirmation email", e);
        }
        Profile p = pm.getProfile(input.getUsername());
        if (p == null) {
            throw new InternalErrorException("Creating profile failed");
        }
        user.put("temporaryToken", pm.generate24hrKey(p));

        output.addResultItem(Arrays.asList(user.toString()));
    }

    @Override
    protected Map<String, Object> getHeaderAttributes() {
        Map<String, Object> retval = super.getHeaderAttributes();
        retval.put(JSONFormatter.KEY_INTRO, "\"user\":");
        return retval;
    }

    private class NewUserInput
    {
        private static final String BAD_REQ_MSG = "missing parameters. name and password required";
        private static final String USER_EXISTS_MSG = "There is already a user with that name";
        private final String username;
        private final String password;
        private final boolean wantsSpam;

        NewUserInput() {
            username = request.getParameter("name");
            password = request.getParameter("password");
            wantsSpam = Boolean.parseBoolean(request.getParameter("subscribe-to-list"));
            validate();
        }

        boolean subscribeToList() {
            return wantsSpam;
        }

        String getUsername() {
            return username;
        }

        String getPassword() {
            return password;
        }

        private void validate() {
            if (isBlank(username) || isBlank(password)) {
                throw new BadRequestException(BAD_REQ_MSG);
            }

            if (im.getProfileManager().hasProfile(username)) {
                throw new BadRequestException(USER_EXISTS_MSG);
            }
        }
    }

}
