package org.intermine.bio.dataconversion;

/*
 * Copyright (C) 2002-2011 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.dataconversion.FileConverter;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.util.FormattedTextParser;
import org.intermine.xml.full.Item;
import org.xml.sax.SAXException;

import java.io.Reader;

import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.xml.full.Item;


/**
 * 
 * @author
 */
public class FeaturePrefixConverter extends BioFileConverter
{
    //
    private static final String DATASET_TITLE = "FeaturePrefix Load";
    private static final String DATA_SOURCE_NAME = "ZFIN";
    private static final Logger LOG = Logger.getLogger(FeaturePrefixConverter.class);
    private Map<String, Item> prefixes = new HashMap();

    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public FeaturePrefixConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }

    public void process(Reader reader) throws Exception {
	processFeaturePrefix(reader);
	try{

            for (Item item : prefixes.values()){
                store(item);
            }
        }catch (ObjectStoreException e) {
            throw new SAXException(e);
        }
    }

    private void processFeaturePrefix(Reader reader) throws Exception{
        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] columns = (String[]) lineIter.next();
            if (columns.length < 2) {
                throw new RuntimeException("Line does not have enough elements: " + columns.length + columns[0]);
            }
            String primaryIdentifier = columns[0];

            String prefix = columns[1];
            String instituteDisplay = columns[2];
	    
            Item FeaturePrefix;
	    if (!StringUtils.isEmpty(primaryIdentifier) && !StringUtils.isEmpty(instituteDisplay) && !StringUtils.isEmpty(prefix)) {
                FeaturePrefix =getFeaturePrefix(primaryIdentifier, instituteDisplay, prefix);
            }
	}
    }
    
    private Item getFeaturePrefix(String primaryIdentifier, String instituteDisplay, String prefix) {
        Item item = prefixes.get(primaryIdentifier);
        if (item == null) {
            item = createItem("FeaturePrefix");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
	    item.setAttribute("instituteName", instituteDisplay);
	    item.setAttribute("name", prefix);
            prefixes.put(primaryIdentifier, item);
        }

        return item;
    }
}
