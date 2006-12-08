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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.objectstore.ObjectStoreFactory;
import org.intermine.objectstore.ObjectStoreSummary;
import org.intermine.objectstore.ObjectStoreWriter;
import org.intermine.objectstore.ObjectStoreWriterFactory;
import org.intermine.objectstore.intermine.ObjectStoreInterMineImpl;
import org.intermine.objectstore.query.ConstraintSet;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.ResultsInfo;
import org.intermine.web.Profile;
import org.intermine.web.ProfileManager;
import org.intermine.web.TemplateQuery;
import org.intermine.web.TemplateHelper;

/**
 * A Task that reads a list of queries from a properties file (eg. testmodel_precompute.properties)
 * and calls ObjectStoreInterMineImpl.precompute() using the Query.
 *
 * @author Kim Rutherford
 */

public class PrecomputeTemplatesTask extends Task
{
    private static final Logger LOG = Logger.getLogger(PrecomputeTemplatesTask.class);
    
    /**
     * The precomputede category to use for templates.
     */
    public static final String PRECOMPUTE_CATEGORY_TEMPLATE = "template";

    protected String alias;
    protected int minRows = -1;
    protected ObjectStoreSummary oss = null;
    protected ObjectStore os = null;
    protected String userProfileAlias;
    protected String username;

    /**
     * Set the ObjectStore alias
     * @param alias the ObjectStore alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Set the alias of the userprofile object store.
     * @param userProfileAlias the object store alias of the userprofile database
     */
    public void setUserProfileAlias(String userProfileAlias) {
        this.userProfileAlias = userProfileAlias;
    }

    /**
     * Set the minimum row count for precomputed queries.  Queries that are estimated to have less
     * than this number of rows will not be precomputed.
     * @param minRows the minimum row count
     */
    public void setMinRows(Integer minRows) {
        this.minRows = minRows.intValue();
    }

    /**
     * Set the account name to laod template to.
     * @param user username to load templates into
     */
    public void setUsername(String user) {
        username = user;
    }

    /**
     * @see Task#execute
     */
    public void execute() throws BuildException {
        if (alias == null) {
            throw new BuildException("alias attribute is not set");
        }

        if (minRows == -1) {
            throw new BuildException("minRows attribute is not set");
        }

        ObjectStore objectStore;

        try {
            objectStore = ObjectStoreFactory.getObjectStore(alias);
        } catch (Exception e) {
            throw new BuildException("Exception while creating ObjectStore", e);
        }

        if (!(objectStore instanceof ObjectStoreInterMineImpl)) {
            throw new BuildException(alias + " isn't an ObjectStoreInterMineImpl");
        }

        precomputeTemplates(objectStore, oss);
    }

    /**
     * Create precomputed tables for all template queries in the given ObjectStore.
     * @param os the ObjectStore to precompute in
     * @param oss the ObjectStoreSummary for os
     */
    protected void precomputeTemplates(ObjectStore os, ObjectStoreSummary oss) {
        Iterator iter = getPrecomputeTemplateQueries().entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            TemplateQuery template = (TemplateQuery) entry.getValue();

            List indexes = new ArrayList();
            Query q = TemplateHelper.getPrecomputeQuery(template, indexes, null);

            if (q.getConstraint() == null) {
                // see ticket #255
                LOG.warn("ignoring template \"" + template.getName()
                          + "\" because it is unconstrained");
                continue;
            }

            if (q.getConstraint() instanceof ConstraintSet) {
                if (((ConstraintSet) q.getConstraint()).getConstraints().size() == 0) {
                    // see ticket #255
                    LOG.warn("ignoring template \"" + template.getName()
                             + "\" because it is unconstrained");
                    continue;
                }
            }

            ResultsInfo resultsInfo;
            try {
                resultsInfo = os.estimate(q);
            } catch (ObjectStoreException e) {
                throw new BuildException("Exception while calling ObjectStore.estimate()", e);
            }

            if (resultsInfo.getRows() >= minRows) {
                LOG.info("precomputing template " + entry.getKey());
                precompute(os, q, indexes, template.getName());
            }
        }
    }

    /**
     * Call ObjectStoreInterMineImpl.precompute() with the given Query.
     * @param os the ObjectStore to call precompute() on
     * @param query the query to precompute
     * @param indexes the index QueryNodes
     * @param name the name of the query we are precomputing (used for documentation is an exception
     * is thrown 
     * @throws BuildException if the query cannot be precomputed.
     */
    protected void precompute(ObjectStore os, Query query, Collection indexes,
                              String name) throws BuildException {
        long start = System.currentTimeMillis();

        try {
            ObjectStoreInterMineImpl osInterMineImpl = ((ObjectStoreInterMineImpl) os);
            if (!osInterMineImpl.isPrecomputed(query, PRECOMPUTE_CATEGORY_TEMPLATE)) {
                osInterMineImpl.precompute(query, indexes,
                                                       PRECOMPUTE_CATEGORY_TEMPLATE);
            } else {
                 LOG.info("Skipping template " + name + " - already precomputed.");
            }
         } catch (ObjectStoreException e) {
            throw new BuildException("Exception while precomputing query: " + name
                                     + ", " + query + " with indexes " + indexes, e);
        }

        LOG.info("precompute(indexes) of took "
                 + (System.currentTimeMillis() - start) / 1000
                 + " seconds for: " + query);
    }

    /**
     * Get the built-in template queries.
     * @return Map from template name to TemplateQuery
     * @throws BuildException if an IO error occurs loading the template queries
     */
    protected Map getPrecomputeTemplateQueries() throws BuildException {
        ObjectStore os;
        ProfileManager pm;
        ObjectStoreWriter userProfileOS;
        try {
            os = ObjectStoreFactory.getObjectStore(alias);
            userProfileOS = ObjectStoreWriterFactory.getObjectStoreWriter(userProfileAlias);
            pm = new ProfileManager(os, userProfileOS);
        } catch (Exception err) {
            throw new BuildException("Exception creating objectstore/profile manager", err);
        }
        if (!pm.hasProfile(username)) {
            throw new BuildException("user profile doesn't exist for " + username);
        } else {
            LOG.warn("Profile for " + username + ", clearing template queries");
            Profile profile = pm.getProfile(username, pm.getPassword(username));
            return profile.getSavedTemplates();
        }
    }
}
