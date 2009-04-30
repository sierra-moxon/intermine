package org.intermine.web.logic.profile;

/*
 * Copyright (C) 2002-2009 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.objectstore.ObjectStoreWriter;
import org.intermine.web.logic.bag.InterMineBag;
import org.intermine.web.logic.query.SavedQuery;
import org.intermine.web.logic.search.SearchRepository;
import org.intermine.web.logic.search.WebSearchable;
import org.intermine.web.logic.tagging.TagTypes;
import org.intermine.web.logic.template.TemplateQuery;

/**
 * Class to represent a user of the webapp
 *
 * @author Mark Woodbridge
 * @author Thomas Riley
 */
public class Profile
{
    protected ProfileManager manager;
    protected String username;
    protected Integer userId;
    protected String password;
    protected Map<String, SavedQuery> savedQueries = new TreeMap();
    protected Map<String, InterMineBag> savedBags = new TreeMap();
    protected Map<String, TemplateQuery> savedTemplates = new TreeMap();
    protected Map<String, String> userOptions = new TreeMap();
    protected Map queryHistory = new ListOrderedMap();
    private boolean savingDisabled;
    private SearchRepository searchRepository;

    /**
     * Construct a Profile
     * @param manager the manager for this profile
     * @param username the username for this profile
     * @param userId the id of this user
     * @param password the password for this profile
     * @param savedQueries the saved queries for this profile
     * @param savedBags the saved bags for this profile
     * @param savedTemplates the saved templates for this profile
     */
    public Profile(ProfileManager manager, String username, Integer userId, String password,
                   Map<String, SavedQuery> savedQueries, Map<String, InterMineBag> savedBags,
                   Map<String, TemplateQuery> savedTemplates) {
        this.manager = manager;
        this.username = username;
        this.userId = userId;
        this.password = password;
        if (savedQueries != null) {
            this.savedQueries.putAll(savedQueries);
        }
        if (savedBags != null) {
            this.savedBags.putAll(savedBags);
        }
        if (savedTemplates != null) {
            this.savedTemplates.putAll(savedTemplates);
        }
        searchRepository = new SearchRepository(this, SearchRepository.USER);
    }

    /**
     * Return the ProfileManager that was passed to the constructor.
     * @return the ProfileManager
     */
    public ProfileManager getProfileManager() {
        return manager;

    }

    /**
     * Get the value of username
     * @return the value of username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Return true if and only if the user is logged is (and the Profile will be written to the
     * userprofile).
     * @return Return true if logged in
     */
    public boolean isLoggedIn() {
        return getUsername() != null;
    }

    /**
     * Get the value of userId
     * @return an Integer
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * Set the userId
     *
     * @param userId an Integer
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * Get the value of password
     * @return the value of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Disable saving until enableSaving() is called.  This is called before many templates or
     * queries need to be saved or deleted because each call to ProfileManager.saveProfile() is
     * slow.
     */
    public void disableSaving() {
        savingDisabled = true;
    }

    /**
     * Re-enable saving when saveTemplate(), deleteQuery() etc. are called.  Also calls
     * ProfileManager.saveProfile() to write this Profile to the database and rebuilds the
     * template description index.
     */
    public void enableSaving() {
        savingDisabled = false;
        if (manager != null) {
            manager.saveProfile(this);
        }
        reindex(TagTypes.TEMPLATE);
        reindex(TagTypes.BAG);
    }

    /**
     * Get the users saved templates
     * @return saved templates
     */
    public Map<String, TemplateQuery> getSavedTemplates() {
        return Collections.unmodifiableMap(savedTemplates);
    }

    /**
     * Get the users saved templates with specified tag
     * @param tag filter the templates returned by this tag
     * @return saved templates
     */
    public Map<String, TemplateQuery> getSavedTemplates(String tag) {
        Map<String, TemplateQuery> filteredTemplates = new HashMap();
        TagManager tagManager = getTagManager();
        for (String template : savedTemplates.keySet()) {
            Set<String> tags = tagManager.getObjectTagNames(template, TagTypes.TEMPLATE, username);
            if (tags.contains(tag)) {
                filteredTemplates.put(template, savedTemplates.get(template));
            }
        }
        return Collections.unmodifiableMap(filteredTemplates);
    }


    /**
     * Save a template
     * @param name the template name
     * @param template the template
     */
    public void saveTemplate(String name, TemplateQuery template) {
        savedTemplates.put(name, template);
        if (manager != null && !savingDisabled) {
            manager.saveProfile(this);
            reindex(TagTypes.TEMPLATE);
        }
    }

    /**
     * get a template
     * @param name the template
     * @return template
     */
    public TemplateQuery getTemplate(String name) {
        return savedTemplates.get(name);
    }

    /**
     * Delete a template
     * @param name the template name
     */
    public void deleteTemplate(String name) {
        savedTemplates.remove(name);
        if (manager != null) {
            if (!savingDisabled) {
                manager.saveProfile(this);
                reindex(TagTypes.TEMPLATE);
            }
        }
    }

    /**
     * Get the value of savedQueries
     * @return the value of savedQueries
     */
    public Map<String, SavedQuery> getSavedQueries() {
        return Collections.unmodifiableMap(savedQueries);
    }

    /**
     * Save a query
     * @param name the query name
     * @param query the query
     */
    public void saveQuery(String name, SavedQuery query) {
        savedQueries.put(name, query);
        if (manager != null && !savingDisabled) {
            manager.saveProfile(this);
        }
    }

    /**
     * Delete a query
     * @param name the query name
     */
    public void deleteQuery(String name) {
        savedQueries.remove(name);
        if (manager != null && !savingDisabled) {
            manager.saveProfile(this);
        }
    }

    /**
     * Get the session query history.
     * @return map from query name to SavedQuery
     */
    public Map<String, SavedQuery> getHistory() {
        return Collections.unmodifiableMap(queryHistory);
    }

    /**
     * Save a query to the query history.
     * @param query the SavedQuery to save to the history
     */
    public void saveHistory(SavedQuery query) {
        queryHistory.put(query.getName(), query);
    }

    /**
     * Remove an item from the query history.
     * @param name the of the SavedQuery from the history
     */
    public void deleteHistory(String name) {
        queryHistory.remove(name);
    }

    /**
     * Rename an item in the history.
     * @param oldName the name of the old item
     * @param newName the new name
     */
    public void renameHistory(String oldName, String newName) {
        Map<String, SavedQuery> newMap = new ListOrderedMap();
        Iterator<String> iter = queryHistory.keySet().iterator();
        while (iter.hasNext()) {
            String name = iter.next();
            SavedQuery sq = (SavedQuery) queryHistory.get(name);
            if (name.equals(oldName)) {
                sq = new SavedQuery(newName, sq.getDateCreated(), sq.getPathQuery());
            }
            newMap.put(sq.getName(), sq);
        }
        queryHistory = newMap;
    }

    /**
     * Get the value of savedBags
     * @return the value of savedBags
     */
    public Map<String, InterMineBag> getSavedBags() {
        return Collections.unmodifiableMap(savedBags);
    }

    /**
     * Stores a new bag in the profile. Note that bags are always present in the user profile
     * database, so this just adds the bag to the in-memory list of this profile.
     *
     * @param name the name of the bag
     * @param bag the InterMineBag object
     */
    public void saveBag(String name, InterMineBag bag) {
        if (StringUtils.isEmpty(name)) {
            throw new RuntimeException("No name specified for the list to save.");
        }
        savedBags.put(name, bag);
        reindex(TagTypes.BAG);
    }

    /**
     * Create a bag - saves it to the user profile database too.
     *
     * @param name the bag name
     * @param type the bag type
     * @param description the bag description
     * @param os the production ObjectStore
     * @param uosw the ObjectStoreWriter of the userprofile database
     * @throws ObjectStoreException if something goes wrong
     */
    public void createBag(String name, String type, String description, ObjectStore os,
            ObjectStoreWriter uosw) throws ObjectStoreException {
        InterMineBag bag = new InterMineBag(name, type, description, new Date(), os, userId, uosw);
        savedBags.put(name, bag);
        reindex(TagTypes.BAG);
    }

    /**
     * Delete a bag
     * @param name the bag name
     */
    public void deleteBag(String name) {
        savedBags.remove(name);
        TagManager tagManager = getTagManager();
        tagManager.deleteObjectTags(name, TagTypes.BAG, username);
        reindex(TagTypes.BAG);
    }

    private TagManager getTagManager() {
        return new TagManagerFactory(manager).getTagManager();
    }

    /**
     * Create a map from category name to a list of templates contained
     * within that category.
     */
    private void reindex(String type) {
        // We also take this opportunity to index the user's template queries, bags, etc.
        searchRepository.addWebSearchables(type);
    }

    /**
     * Return a WebSearchable Map for the given type.
     * @param type the type (from TagTypes)
     * @return the Map
     */
    public Map<String, ? extends WebSearchable> getWebSearchablesByType(String type) {
        if (type.equals(TagTypes.TEMPLATE)) {
            return savedTemplates;
        }
        if (type.equals(TagTypes.BAG)) {
            return getSavedBags();
        }
        throw new RuntimeException("unknown type: " + type);
    }

    /**
     * Get the SearchRepository for this Profile.
     * @return the SearchRepository for the user
     */
    public SearchRepository getSearchRepository() {
        return searchRepository;
    }

    /**
     * Return the userOption
     * @param name the name
     * @return the value
     */
    public String getUserOption(String name) {
        return userOptions.get(name);
    }

    /**
     * Set the userOption
     * @param name the userOption name
     * @param value the userOption value
     */
    public void setUserOption(String name, String value) {
        userOptions.put(name, value);
    }

    /**
     * @return the userOptions
     */
    public Map<String, String> getUserOptionsMap() {
        return userOptions;
    }
}
