package org.intermine.metadata;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.intermine.modelproduction.MetadataManager;
import org.intermine.util.PropertiesUtil;
import org.intermine.util.TypeUtil;

/**
 * Utility methods for PrimaryKey objects.
 *
 * @author Kim Rutherford
 */

public abstract class PrimaryKeyUtil
{
    protected static Map<String, Properties> modelKeys = new HashMap<String, Properties>();
    
    /**
     * Retrieve a map from key name to PrimaryKey object. The Map contains all the primary keys
     * that exist on a particular class, without performing any recursion.
     *
     * @param cld the ClassDescriptor to fetch primary keys for
     * @return the Map from key names to PrimaryKeys
     */
    public static Map<String, PrimaryKey> getPrimaryKeys(ClassDescriptor cld) {
        Map<String, PrimaryKey> keyMap = new LinkedHashMap<String, PrimaryKey>();
        Properties keys = getKeyProperties(cld.getModel().getName());
        String cldName = TypeUtil.unqualifiedName(cld.getName());
        Properties cldKeys = PropertiesUtil.getPropertiesStartingWith(cldName, keys);
        cldKeys = PropertiesUtil.stripStart(cldName, cldKeys);
        List<String> keyNames = new ArrayList<String>();
        for (Object key : cldKeys.keySet()) {
            if (key instanceof String) {
                keyNames.add((String) key);
            }
        }
        Collections.sort(keyNames);
        for (String keyName : keyNames) {
            PrimaryKey key = new PrimaryKey(keyName, (String) cldKeys.get(keyName), cld);
            keyMap.put(keyName, key);
        }
        return keyMap;
    }

    /**
     * Return the Properties that specify the key fields for the classes in this Model
     *
     * @param model the Model
     * @return the relevant Properties
     */
    public static Properties getKeyProperties(String modelName) {
        Properties keys = null;
        synchronized (modelKeys) {
            keys = (Properties) modelKeys.get(modelName);
            if (keys == null) {
                keys = MetadataManager.loadKeyDefinitions(modelName);
                modelKeys.put(modelName, keys);
            }
        }
        return keys;
    }
}
