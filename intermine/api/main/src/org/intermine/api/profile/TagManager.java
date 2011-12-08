package org.intermine.api.profile;

/*
 * Copyright (C) 2002-2011 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.log4j.Logger;
import org.intermine.api.tag.TagNames;
import org.intermine.api.tag.TagTypes;
import org.intermine.metadata.ClassDescriptor;
import org.intermine.metadata.CollectionDescriptor;
import org.intermine.metadata.ReferenceDescriptor;
import org.intermine.model.userprofile.Tag;
import org.intermine.model.userprofile.UserProfile;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.objectstore.ObjectStoreWriter;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.objectstore.query.ConstraintSet;
import org.intermine.objectstore.query.ContainsConstraint;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.QueryClass;
import org.intermine.objectstore.query.QueryField;
import org.intermine.objectstore.query.QueryObjectReference;
import org.intermine.objectstore.query.QueryValue;
import org.intermine.objectstore.query.SimpleConstraint;
import org.intermine.objectstore.query.SingletonResults;
import org.intermine.template.TemplateQuery;
import org.intermine.util.CacheMap;
import org.intermine.util.DynamicUtil;

/**
 * Manager class for tags. Implements retrieving, adding and deleting tags in user profile
 * database.
 * @author Jakub Kulaviak <jakub@flymine.org>
 */
public class TagManager
{
    private static final Logger LOG = Logger.getLogger(TagManager.class);
    protected ObjectStoreWriter osWriter;
    private CacheMap<MultiKey, List<Tag>> tagCache = null;

    /** What we tell users when they give us an invalid tag name **/
    public static final String INVALID_NAME_MSG = "Invalid name. "
            + "Names may only contain letters, "
            + "numbers, spaces, full stops, hyphens and colons.";

    /**
     * Constructor. Use TagManagerFactory for creating tag manager.
     * @param profileOsWriter user profile object store
     */
    public TagManager(ObjectStoreWriter profileOsWriter) {
        this.osWriter = profileOsWriter;
    }

    /**
     * Delete a tag object from the database.
     * @param tag Tag object
     */
    public synchronized void deleteTag(Tag tag) {
        try {
            tagCache = null;
            osWriter.delete(tag);
        } catch (ObjectStoreException e) {
            LOG.error("delete tag failed" + e);
            throw new RuntimeException("Delete tag failed", e);
        }
    }

    private void checkUserExists(String userName) {
        UserProfile profile = getUserProfile(userName);
        if (profile == null) {
            throw new UserNotFoundException("User " + userName + " doesn't exist");
        }
    }

    /**
     * Deletes tag object from the database.
     * @param tagName tag name
     * @param taggedObject object id of tagged object
     * @param type tag type
     * @param userName user name
     */
    public void deleteTag(String tagName, String taggedObject, String type, String userName) {
        List<Tag> tags = getTags(tagName, taggedObject, type, userName);
        if (tags.size() > 0 && tags.get(0) != null) {
            deleteTag(tags.get(0));
        } else {
            throw new RuntimeException("Attempt to delete non existing tag. tagName="
                    + tagName + ", taggedObject=" + taggedObject + ", type=" + type
                    + ", userName=" + userName);
        }
    }

    /**
     * Deletes tags object from the database. Any null arguments will be treated as
     * wildcards.
     * @param tagName tag name
     * @param taggedObject object id of tagged object
     * @param type tag type
     * @param userName user name
     */
    public void deleteTags(String tagName, String taggedObject, String type, String userName) {
        List<Tag> tags = getTags(tagName, taggedObject, type, userName);
        for (Tag tag : tags) {
            deleteTag(tag);
        }
    }

    private static Set<String> tagsToTagNames(List<Tag> tags) {
        Set<String> ret = new TreeSet<String>();
        for (Tag tag : tags) {
            ret.add(tag.getTagName());
        }
        return ret;
    }

    /**
     * Returns names of tags of specified user and tag type. For anonymous user returns empty set.
     * @param type tag type
     * @param userName user name
     * @return tag names
     * @throws UserNotFoundException if user doesn't exist
     */
    public Set<String> getUserTagNames(String type, String userName) {
        return tagsToTagNames(getTags(null, null, type, userName));
    }

    /**
     * Returns tags of specified user and tag type. For anonymous user returns empty set.
     * @param userName user name
     * @return tags
     */
    public List<Tag> getUserTags(String userName) {
        return getTags(null, null, null, userName);
    }

    /**
     * Returns names of tagged tags for specified object. For anonymous user returns empty set.
     * @param taggedObject tagged object, eg. template name
     * @param type tag type, eg. template
     * @param userName user name
     * @return tag names
     */
    public Set<String> getObjectTagNames(String taggedObject, String type, String userName) {
        List<Tag> tags = getTags(null, taggedObject, type, userName);
        return tagsToTagNames(tags);
    }

    /**
     * Get Tag by object id.
     * @param id intermine object id
     * @return Tag
     */
    public synchronized Tag getTagById(int id) {
        try {
            return (Tag) osWriter.getObjectById(new Integer(id), Tag.class);
        } catch (ObjectStoreException e) {
            throw new RuntimeException("Getting tag from database failed", e);
        }
    }

    /**
     * Helper method for getTags.
     * @param cs The constraint set being built up.
     * @param qc The class the constrain on.
     * @param fieldName The field to constrain.
     * @param value The value to constrain to.
     */
    private static void constrain(ConstraintSet cs, QueryClass qc, String fieldName, String value) {
        if (value != null) {
            QueryValue qv = new QueryValue(value);
            QueryField qf = new QueryField(qc, fieldName);
            SimpleConstraint c = new SimpleConstraint(qf, ConstraintOp.EQUALS, qv);
            cs.addConstraint(c);
        }
    }

    /**
     * Return a List of Tags that match all the arguments.  Any null arguments will be treated as
     * wildcards.
     * @param tagName the tag name - any String
     * @param taggedObjectId an object identifier that is appropriate for the given tag type
     * (eg. "Department.name" for the "collection" type)
     * @param type the tag type (eg. "collection", "reference", "attribute", "bag")
     * @param userName the use name this tag is associated with
     * @return the matching Tags
     */
    public synchronized List<Tag> getTags(String tagName, String taggedObjectId, String type,
                        String userName) {
        if (type != null) {
            checkTagType(type);
        }

        Map<MultiKey, List<Tag>> cache = getTagCache();
        MultiKey key = makeKey(tagName, taggedObjectId, type, userName);

        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        // if there isn't a cache for user, than check if user exists
        // for performance reasons don't put this check at the method beginning
        if (userName != null) {
            checkUserExists(userName);
        }

        Query q = new Query();
        QueryClass qc = new QueryClass(Tag.class);

        q.addFrom(qc);
        q.addToSelect(qc);

        QueryField orderByField = new QueryField(qc, "tagName");

        q.addToOrderBy(orderByField);

        ConstraintSet cs = new ConstraintSet(ConstraintOp.AND);

        constrain(cs, qc, "tagName", tagName);
        constrain(cs, qc, "objectIdentifier", taggedObjectId);
        constrain(cs, qc, "type", type);

        if (userName != null) {
            QueryClass userProfileQC = new QueryClass(UserProfile.class);
            q.addFrom(userProfileQC);
            QueryValue qv = new QueryValue(userName);
            QueryField qf = new QueryField(userProfileQC, "username");
            SimpleConstraint c = new SimpleConstraint(qf, ConstraintOp.EQUALS, qv);
            cs.addConstraint(c);

            QueryObjectReference qr = new QueryObjectReference(qc, "userProfile");

            ContainsConstraint cc =
                new ContainsConstraint(qr, ConstraintOp.CONTAINS, userProfileQC);
            cs.addConstraint(cc);
        }
        q.setConstraint(cs);

        ObjectStore userprofileOS = osWriter.getObjectStore();
        SingletonResults results = userprofileOS.executeSingleton(q);
        addToCache(cache, key, ((List) results));
        return ((List) results);
    }

    private MultiKey makeKey(String tagName, String objectIdentifier, String type,
                             String userName) {
        return new MultiKey(tagName, objectIdentifier, type, userName);
    }

    private void addToCache(Map<MultiKey, List<Tag>> cache, MultiKey key, List<Tag> results) {

        cache.put(key, new ArrayList<Tag>(results));

        int keyNullPartCount = 0;

        for (int i = 0; i < 4; i++) {
            if (key.getKey(i) == null) {
                keyNullPartCount++;
            }
        }

        Iterator resIter = results.iterator();

        while (resIter.hasNext()) {
            Tag tag = (Tag) resIter.next();

            Object[] tagKeys = new Object[4];
            tagKeys[0] = tag.getTagName();
            tagKeys[1] = tag.getObjectIdentifier();
            tagKeys[2] = tag.getType();
            tagKeys[3] = tag.getUserProfile().getUsername();
        }

    }

    private Map<MultiKey, List<Tag>> getTagCache() {
        if (tagCache == null) {
            tagCache  = new CacheMap<MultiKey, List<Tag>>();
        }
        return tagCache;
    }

    /**
     * Add a new tag.  The format of objectIdentifier depends on the tag type.
     * For types "attribute", "reference" and "collection" the objectIdentifier should have the form
     * "ClassName.fieldName".
     * @param tagName the tag name - any String
     * @param objectIdentifier an object identifier that is appropriate for the given tag type
     * (eg. "Department.name" for the "collection" type)
     * @param type the tag type (eg. "collection", "reference", "attribute", "bag")
     * @param profile The Profile of the user to associate this tag with.
     * @return the new Tag
     * @throws TagNameException If the tag name is invalid.
     * @throws TagNamePermissionException If the user does not have the required
     *         permissions to add this tag.
     */
    public synchronized Tag addTag(String tagName, String objectIdentifier, String type,
            Profile profile)
        throws TagNameException, TagNamePermissionException {

        if (profile == null) {
            throw new IllegalArgumentException("profile cannot be null");
        }
        if (tagName == null) {
            throw new IllegalArgumentException("tagName cannot be null");
        }
        if (tagName.startsWith(TagNames.IM_PREFIX) && !profile.isSuperuser()) {
            throw new TagNamePermissionException();
        }
        if (!isValidTagName(tagName)) {
            throw new TagNameException();
        }

        return addTag(tagName, objectIdentifier, type, profile.getUsername());
    }

    /**
     * Associate a bag with a certain tag.
     * @param tagName The tag we want to give this bag.
     * @param bag The bag to tag.
     * @param profile The profile to associate this tag with.
     * @return A tag object.
     * @throws TagNameException If the name is invalid (contains illegal characters)
     * @throws TagNamePermissionException If this tag name is restricted.
     */
    public synchronized Tag addTag(String tagName, InterMineBag bag, Profile profile)
        throws TagNameException, TagNamePermissionException {
        return addTag(tagName, bag.getName(), TagTypes.BAG, profile);
    }

    /**
     * Associate a template with a certain tag.
     * @param tagName The tag we want to give this template.
     * @param template The template to tag.
     * @param profile The profile to associate this tag with.
     * @return A tag object.
     * @throws TagNameException If the name is invalid (contains illegal characters)
     * @throws TagNamePermissionException If this tag name is restricted.
     */
    public synchronized Tag addTag(String tagName, TemplateQuery template, Profile profile)
        throws TagNameException, TagNamePermissionException {
        return addTag(tagName, template.getName(), TagTypes.TEMPLATE, profile);
    }

    /**
     * Associate a class with a certain tag.
     * @param tagName The tag we want to give this class.
     * @param cld The descriptor for this class.
     * @param profile The profile to associate this tag with.
     * @return A tag object.
     * @throws TagNameException If the name is invalid (contains illegal characters)
     * @throws TagNamePermissionException If this tag name is restricted.
     */
    public synchronized Tag addTag(String tagName, ClassDescriptor cld, Profile profile)
        throws TagNameException, TagNamePermissionException {
        return addTag(tagName, cld.getName(), TagTypes.CLASS, profile);
    }

    /**
     * Associate a reference with a certain tag.
     * @param tagName The tag we want to give this reference.
     * @param ref the reference.
     * @param profile The profile to associate this tag with.
     * @return A tag object.
     * @throws TagNameException If the name is invalid (contains illegal characters)
     * @throws TagNamePermissionException If this tag name is restricted.
     */
    public synchronized Tag addTag(String tagName, ReferenceDescriptor ref, Profile profile)
        throws TagNameException, TagNamePermissionException {
        String objIdentifier = ref.getReferencedClassName() + "." + ref.getName();
        if (ref instanceof CollectionDescriptor) {
            return addTag(tagName, objIdentifier, TagTypes.COLLECTION, profile);
        } else {
            return addTag(tagName, objIdentifier, TagTypes.REFERENCE, profile);
        }
    }

    /**
     * Add a new tag. This method is meant to only be used from
     * XML unmarshalling. Unlike the publicly visible addTag, it performs its operation,
     * even if the name is invalid.
     *
     *
     * @param tagName the tag name - any String
     * @param objectIdentifier an object identifier that is appropriate for the given tag type
     * (eg. "Department.name" for the "collection" type)
     * @param type the tag type (eg. "collection", "reference", "attribute", "bag")
     * @param username The username of the user to associate this tag with.
     * @return the new Tag
     */
    synchronized Tag addTag(String tagName, String objectIdentifier,
            String type, String username) {

        checkUserExists(username);
        checkTagType(type);
        tagCache = null;
        if (tagName == null) {
            throw new IllegalArgumentException("tagName cannot be null");
        }
        if (objectIdentifier == null) {
            throw new IllegalArgumentException("objectIdentifier cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("type cannot be null");
        }

        UserProfile userProfile = getUserProfile(username);
        Tag tag = (Tag) DynamicUtil.createObject(Collections.singleton(Tag.class));
        tag.setTagName(tagName);
        tag.setObjectIdentifier(objectIdentifier);
        tag.setType(type);
        tag.setUserProfile(userProfile);

        try {
            osWriter.store(tag);
            return tag;
        } catch (ObjectStoreException e) {
            throw new RuntimeException("cannot set tag", e);
        }
    }

    private void checkTagType(String type) {
        if (!isKnownTagType(type)) {
            throw new IllegalArgumentException("unknown tag type: " + type);
        }
    }

    private boolean isKnownTagType(String type) {
        return ("collection".equals(type)
                || "reference".equals(type)
                || "attribute".equals(type)
                || "bag".equals(type)
                || "template".equals(type)
                || "class".equals(type));
    }

    // duplication of ProfileManager method here, so this class is not dependent on ProfileManager
    private UserProfile getUserProfile(String userName) {
        UserProfile profile = new UserProfile();
        profile.setUsername(userName);
        Set<String> fieldNames = new HashSet<String>();
        fieldNames.add("username");
        try {
            profile = (UserProfile) osWriter.getObjectByExample(profile, fieldNames);
        } catch (ObjectStoreException e) {
            throw new RuntimeException("Unable to load user profile", e);
        }
        return profile;
    }

    /**
     * TODO this should use the same validation method the other classes use
     * Verifies that tag name can only contain A-Z, a-z, 0-9, '_', '-', ' ', ':', '.'
     * @param name tag name
     * @return true if the name is valid else false
     */
    public static boolean isValidTagName(String name) {
        if (name == null) {
            return false;
        }
        Pattern p = Pattern.compile("[^\\w\\s\\.\\-:]");
        Matcher m = p.matcher(name);
        return !m.find();
    }

    /**
     * Deletes all tags assigned to a specified object.
     * @param taggedObject tagged object
     * @param type tag type
     * @param userName user name
     */
    public void deleteObjectTags(String taggedObject, String type, String userName) {
        List<Tag> tags = getTags(null, taggedObject, type, userName);
        for (Tag tag : tags) {
            deleteTag(tag);
        }
    }

    public static class TagException extends Exception {
        public TagException(String message) {
            super(message);
        }
    }

    public static class TagNameException extends TagException {

            public TagNameException() {
                super(INVALID_NAME_MSG);
            }
    }

    public static class TagNamePermissionException extends TagException {

        private static final String PERMISSION_MESSAGE = "You cannot add a tag starting with "
                + TagNames.IM_PREFIX + ", "
                + "that is a reserved word.";

        public TagNamePermissionException() {
            super(PERMISSION_MESSAGE);
        }
    }
}
