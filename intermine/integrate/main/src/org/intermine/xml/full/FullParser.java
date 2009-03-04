package org.intermine.xml.full;

/*
 * Copyright (C) 2002-2009 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.intermine.metadata.Model;
import org.intermine.model.FastPathObject;
import org.intermine.model.InterMineObject;
import org.intermine.util.DynamicUtil;
import org.intermine.util.SAXParser;
import org.intermine.util.TypeUtil;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

/**
 * Unmarshal XML Full format data into java business objects.
 *
 * @author Andrew Varley
 * @author Richard Smith
 */
public class FullParser
{
    private static final Logger LOG = Logger.getLogger(FullParser.class);

    /**
     * Parse a InterMine Full XML file
     *
     * @param is the InputStream to parse
     * @return a list of Items
     * @throws Exception if there is an error while parsing
     */
    public static List<Item> parse(InputStream is)
        throws Exception {

        if (is == null) {
            throw new NullPointerException("InputStream cannot be null");
        }

        FullHandler handler = new FullHandler();
        SAXParser.parse(new InputSource(is), handler);

        return handler.getItems();
    }

    /**
     * Create business objects from a collection of Items.  If there are any problems, throw an
     * exception
     * @param items a collection of items to realise
     * @param model the parent model
     * @param useIdentifier if true, set the id of each new object using the identifier of the Item
     * problem and continue if possible
     * @return a collection of realised business objects
     * @throws ClassNotFoundException if one of the items has a class that isn't in the model
     */
    public static List<InterMineObject> realiseObjects(Collection<Item> items, Model model,
                                                       boolean useIdentifier)
        throws ClassNotFoundException {
        return realiseObjects(items, model, useIdentifier, true);
    }

    /**
     * Create business objects from a collection of Items.
     * @param items a collection of items to realise
     * @param model the parent model
     * @param useIdentifier if true, set the id of each new object using the identifier of the Item
     * @param abortOnError if true, throw an exception if there is a problem.  If false, log the
     * problem and continue if possible
     * @return a collection of realised business objects
     * @throws ClassNotFoundException if one of the items has a class that isn't in the model
     */
    public static List<InterMineObject> realiseObjects(Collection<Item> items, Model model,
            boolean useIdentifier, boolean abortOnError) throws ClassNotFoundException {
        // map from id to outline object
        Map<String, FastPathObject> objMap = new LinkedHashMap<String, FastPathObject>();

        List result = new ArrayList();
        for (Item item : items) {
            if (item.getIdentifier() != null) {
                try {
                    objMap.put(item.getIdentifier(), DynamicUtil.instantiateObject(
                            ItemHelper.generateClassNames(item.getClassName(), model),
                            ItemHelper.generateClassNames(item.getImplementations(), model)));
                } catch (ClassNotFoundException e) {
                    if (abortOnError) {
                        throw e;
                    } else {
                        LOG.warn("Not creating object for item: " + item.getIdentifier()
                                 + " class: " + item.getClassName() + " not found in model.");
                    }
                }
            }
        }

        for (Item item : items) {
            FastPathObject instance = null;
            // simple objects don't have identifiers so can't be put in the objMap, need to
            // create again in this loop to get an instance
            if (item.getIdentifier() == null) {
                try {
                    instance = DynamicUtil.instantiateObject(
                            ItemHelper.generateClassNames(item.getClassName(), model),
                            ItemHelper.generateClassNames(item.getImplementations(), model));
                } catch (ClassNotFoundException e) {
                    if (abortOnError) {
                        throw e;
                    } else {
                        LOG.warn("Not creating object for item: " + item.getIdentifier()
                                 + " class: " + item.getClassName() + " not found in model.");
                    }
                }
            } else {
                instance = objMap.get(item.getIdentifier());
            }
            if (instance != null) {
                result.add(populateObject(item, objMap, useIdentifier, abortOnError,
                                          instance));
            }
        }

        return result;
    }

    /**
     * Fill in fields of an outline business object which is in the map under item.identifier
     * Note that this modifies the relevant object in the map
     * It also returns the object for convenience
     * @param item a the Item to read field data from
     * @param objMap a map of item identifiers to outline business objects
     * @param useIdentifier if true, set the id of the new object using the identifier of the Item
     * @param abortOnError if true, throw an exception if there is a problem.  If false, log the
     * problem and continue if possible
     * @param obj the object
     * @return a populated object
     */
    protected static Object populateObject(Item item, Map objMap, boolean useIdentifier,
            boolean abortOnError, FastPathObject obj) {
        try {
            // Set the data for every given attribute except id
            Iterator attrIter = item.getAttributes().iterator();
            while (attrIter.hasNext()) {
                Attribute attr = (Attribute) attrIter.next();
                TypeUtil.FieldInfo info = TypeUtil.getFieldInfo(obj.getClass(), attr.getName());
                if (info == null) {
                    String message = "Field " + attr.getName()
                        + " not found in " + DynamicUtil.decomposeClass(obj.getClass());
                    if (abortOnError) {
                        throw new IllegalArgumentException(message);
                    } else {
                        LOG.warn(message);
                        continue;
                    }
                }
                Class attrClass = info.getType();
                if (!attr.getName().equalsIgnoreCase("id")) {
                    obj.setFieldValue(attr.getName(),
                            TypeUtil.stringToObject(attrClass, attr.getValue()));
                }
            }

            if (useIdentifier) {
                obj.setFieldValue("id", TypeUtil.stringToObject(Integer.class,
                            item.getIdentifier()));
            }

            // Set the data for every given reference
            Iterator refIter = item.getReferences().iterator();
            while (refIter.hasNext()) {
                Reference ref = (Reference) refIter.next();
                Object refObj = objMap.get(ref.getRefId());
                TypeUtil.FieldInfo info = TypeUtil.getFieldInfo(obj.getClass(), ref.getName());
                if (info == null) {
                    String message = "Field " + ref.getName()
                    + " not found in " + DynamicUtil.decomposeClass(obj.getClass());
                    if (abortOnError) {
                        throw new IllegalArgumentException(message);
                    } else {
                        LOG.warn(message);
                        continue;
                    }
                }
                if (refObj == null) {
                    LOG.warn("no field " + ref.getName() + " in object: " + obj);
                } else {
                    try {
                        obj.setFieldValue(ref.getName(), refObj);
                    } catch (IllegalArgumentException e) {
                        if (abortOnError) {
                            throw e;
                        } else {
                            LOG.warn("Failed to set field: " + e);
                        }
                    }
                }
            }

            // Set objects for every collection
            Iterator colIter = item.getCollections().iterator();
            while (colIter.hasNext()) {
                ReferenceList refList = (ReferenceList) colIter.next();
                Collection col = (Collection) obj.getFieldValue(refList.getName());
                for (Iterator i = refList.getRefIds().iterator(); i.hasNext();) {
                    col.add(objMap.get(i.next()));
                }
            }

        } catch (IllegalAccessException e) {
            // ignore
        }

        return obj;
    }
}
