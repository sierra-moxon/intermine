package org.intermine.web.task;

/*
 * Copyright (C) 2002-2005 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.intermine.model.userprofile.Tag;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreFactory;
import org.intermine.objectstore.ObjectStoreWriter;
import org.intermine.objectstore.ObjectStoreWriterFactory;
import org.intermine.web.Profile;
import org.intermine.web.ProfileBinding;
import org.intermine.web.ProfileManager;
import org.intermine.web.RequestPasswordAction;
import org.intermine.web.TemplateQuery;

import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Load template queries form an XML file into a given user profile.
 *
 * @author Thomas Riley
 */

public class LoadDefaultTemplatesTask extends Task
{
    private static final Logger LOG = Logger.getLogger(LoadDefaultTemplatesTask.class);

    private String xmlFile;
    private String username;
    private String osAlias;

    private String userProfileAlias;

    /**
     * Set the templates xml file.
     * @param file to xml file
     */
    public void setTemplatesXml(String file) {
        xmlFile = file;
    }

    /**
     * Set the account name to laod template to.
     * @param user username to load templates into
     */
    public void setUsername(String user) {
        username = user;
    }

    /**
     * Set the alias of the main object store.
     * @param osAlias the object store alias
     */
    public void setOSAlias(String osAlias) {
        this.osAlias = osAlias;
    }

    /**
     * Set the alias of the userprofile object store.
     * @param userProfileAlias the object store alias of the userprofile database
     */
    public void setUserProfileAlias(String userProfileAlias) {
        this.userProfileAlias = userProfileAlias;
    }

    /**
     * Load templates from an xml file into a userprofile account.
     *
     * @see Task#execute
     */
    public void execute() throws BuildException {
        log("Loading default templates and tags into profile " + username);

        // Needed so that STAX can find it's implementation classes
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        try {
            ObjectStore os = ObjectStoreFactory.getObjectStore(osAlias);
            ObjectStoreWriter userProfileOS =
                ObjectStoreWriterFactory.getObjectStoreWriter(userProfileAlias);
            ProfileManager pm = new ProfileManager(os, userProfileOS);
            Reader reader = new FileReader(xmlFile);

            // Copy into existing or new superuser profile
            Profile profileDest = null;
            if (!pm.hasProfile(username)) {
                LOG.info("Creating profile for " + username);
                String password = RequestPasswordAction.generatePassword();
                profileDest = new Profile(pm, username, null, password,
                                      new HashMap(), new HashMap(), new HashMap());
                pm.saveProfile(profileDest);
            } else {
                LOG.info("Profile for " + username + ", clearing template queries");
                profileDest = pm.getProfile(username, pm.getPassword(username));
                Map tmpls = new HashMap(profileDest.getSavedTemplates());
                Iterator iter = tmpls.keySet().iterator();
                while (iter.hasNext()) {
                    profileDest.deleteTemplate((String) iter.next());
                }
            }

            // Unmarshal
            Set tags = new HashSet();
            Profile profileSrc = ProfileBinding.unmarshal(reader, pm, os,
                    profileDest.getUsername(), profileDest.getPassword(), tags);

            if (profileDest.getSavedTemplates().size() == 0) {
                Iterator iter = profileSrc.getSavedTemplates().values().iterator();
                while (iter.hasNext()) {
                    TemplateQuery template = (TemplateQuery) iter.next();
                    String append = "";
                    if (!template.isValid()) {
                        append = " [invalid]";
                    }
                    log("Adding template \"" + template.getName() + "\"" + append);
                    profileDest.saveTemplate(template.getName(), template);
                }
                pm.convertTemplateKeywordsToTags(profileSrc.getSavedTemplates(), username);
            }

            // Tags not loaded automatically when unmarshalling profile
             Iterator iter = tags.iterator();
             while (iter.hasNext()) {
                 Tag tag = (Tag) iter.next();
                 if (pm.getTags(tag.getTagName(), tag.getObjectIdentifier(),
                                            tag.getType(), profileDest.getUsername()).isEmpty()) {
                     pm.addTag(tag.getTagName(), tag.getObjectIdentifier(), tag.getType(),
                                        profileDest.getUsername());
                 }
             }

        } catch (Exception e) {
            throw new BuildException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }
}
