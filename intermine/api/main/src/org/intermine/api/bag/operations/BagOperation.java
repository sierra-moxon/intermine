package org.intermine.api.bag.operations;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.intermine.api.bag.ClassKeysNotFoundException;
import org.intermine.api.bag.UnknownBagTypeException;
import org.intermine.api.profile.InterMineBag;
import org.intermine.api.profile.Profile;
import org.intermine.metadata.ClassDescriptor;
import org.intermine.metadata.FieldDescriptor;
import org.intermine.metadata.MetaDataException;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.objectstore.query.ObjectStoreBagCombination;
import org.intermine.objectstore.query.Query;

public abstract class BagOperation implements BagProducer {

    protected static final Logger LOG = Logger.getLogger(BagOperation.class);
    private final Profile profile;
    private Collection<InterMineBag> bags;
    private String nameFormat = "%s List (%tc)";
    private Map<String, List<FieldDescriptor>> classKeys = new HashMap<String, List<FieldDescriptor>>();
    private String newBagName = null;
    protected final Model model;
    private InterMineBag combined;
    private Set<ClassDescriptor> classes = new HashSet<ClassDescriptor>();

    public BagOperation(Model model, Profile profile, Collection<InterMineBag> bags) {
        this.model = model;
        this.bags = bags;
        this.profile = profile;
        init();
    }

    private void init() {
        for (InterMineBag bag: bags) {
            ClassDescriptor cd = model.getClassDescriptorByName(bag.getType());
            classes.add(cd);
        }
    }

    protected Collection<ClassDescriptor> getClasses() {
        return new HashSet<ClassDescriptor>(classes);
    }

    public Map<String, List<FieldDescriptor>> getClassKeys() {
        return classKeys;
    }

    public void setClassKeys(Map<String, List<FieldDescriptor>> classKeys) {
        this.classKeys = classKeys;
    }

    public Profile getProfile() {
        return profile;
    }

    public Collection<InterMineBag> getBags() {
        return new HashSet<InterMineBag>(bags);
    }

    public abstract String getNewBagType() throws IncompatibleTypes;

    protected abstract int getOperationCode();

    /**
     * Perform this bag operation. Yes I know that this is a "execution in the kingdom of
     * nouns" method, and I apologise for the horror of it all. This operation will run at most
     * once. Subsequent calls to operate will always return the same created list.
     * @return The new bag. Guaranteed to not be null.
     * @throws BagOperationException
     */
    public synchronized InterMineBag operate() throws BagOperationException {
        if (combined != null) {
            return combined;
        }

        String type = getNewBagType();
        String name = getNewBagName();

        checkCurrency();
        initCombined(type, name);
        buildBag();
        checkSize();

        return combined;
    }

    private void checkSize() throws BagOperationException,
            InternalBagOperationException {
        try {
            if (combined.size() < 1) {
                cleanUp();
                throw new NoContent();
            }
        } catch (ObjectStoreException e) {
            cleanUp();
            throw new InternalBagOperationException("Unable to check bag size.", e);
        }
    }

    private void checkCurrency() throws BagOperationException {
        boolean allAreCurrent = true;
        for (InterMineBag bag: bags) {
            allAreCurrent = bag.isCurrent();
            if (!allAreCurrent) break;
        }
        if (!allAreCurrent) {
            throw new NotCurrent();
        }
    }

    private void buildBag() throws InternalBagOperationException {
        ObjectStoreBagCombination osbc = combineBags();
        Query q = new Query();
        q.addToSelect(osbc);
        LOG.info(q.toString());
        try {
            combined.addToBagFromQuery(q);
        } catch (ObjectStoreException e) {
            cleanUp();
            throw new InternalBagOperationException("Error constructing bag", e);
        }
    }

    protected ObjectStoreBagCombination combineBags() {
        ObjectStoreBagCombination osbc = new ObjectStoreBagCombination(getOperationCode());
        for (InterMineBag bag : bags) {
            osbc.addBag(bag.getOsb());
        }
        return osbc;
    }

    private void initCombined(String type, String name) throws InternalBagOperationException {
        if (combined != null) {
            throw new InternalBagOperationException("combined bag already exists");
        }
        try {
            combined = getProfile().createBag(name, type, "", getClassKeys());
        } catch (UnknownBagTypeException e) {
            throw new InternalBagOperationException(e);
        } catch (ClassKeysNotFoundException e) {
            throw new InternalBagOperationException(e);
        } catch (ObjectStoreException e) {
            throw new InternalBagOperationException("Error saving list", e);
        }
        if (combined == null) {
            throw new InternalBagOperationException("Unknown error - combined is null.");
        }
    }

    private void cleanUp() {
        if (combined == null) {
            // nothing to do.
        } else {
            try {
                getProfile().deleteBag(combined.getName());
                combined = null;
            } catch (ObjectStoreException e) {
                LOG.error("Could not delete " + combined.getName(), e);
            }
        }
    }

    public void setNewBagName(String name) {
        newBagName = name;
    }

    public String getNewBagName() throws BagOperationException {
        if (newBagName == null) {
            String prefix = String.format(nameFormat, getNewBagType(), new Date());
            String name = prefix;
            int i = 1;
            while (profile.getSavedBags().containsKey(name)) {
                name = prefix + " - " + i++;
            }
            return name;
        } else {
            if (getProfile().getSavedBags().containsKey(newBagName)) {
                throw new NonUniqueName(newBagName);
            }
            return newBagName;
        }
    }
}
