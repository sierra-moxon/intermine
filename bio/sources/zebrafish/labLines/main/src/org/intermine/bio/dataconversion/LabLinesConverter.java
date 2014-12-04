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

import java.io.Reader;

import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.xml.full.Item;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.util.FormattedTextParser;
import org.intermine.xml.full.Item;
import org.xml.sax.SAXException;

/**
 * 
 * @author
 */
public class LabLinesConverter extends BioFileConverter
{
    //
    private static final String DATASET_TITLE = "Lab Lines";
    private static final String DATA_SOURCE_NAME = "ZFIN";

    private static final Logger LOG = Logger.getLogger(LabLinesConverter.class);
    private Map<String, Item> labs = new HashMap<String, Item>();
    private Map<String, Item> features = new HashMap<String, Item>();

    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public LabLinesConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }

    /**
     * 
     *
     * {@inheritDoc}
     */
    public void process(Reader reader) throws Exception {
	processLabLines(reader);
	try {
	    for (Item feature : features.values()){
		store(feature);
	    }

	}
	catch (ObjectStoreException e){
	    throw new SAXException(e);
	}


    }
    public void processLabLines(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] columns = (String[]) lineIter.next();
            if (columns.length < 6) {
                throw new RuntimeException("Line does not have enough elements: " + columns.length + columns[0]);
            }
            String featureId = columns[0];

            String LabId = columns[1];
	    System.out.println(LabId);
	    String sourceType = columns[4];
	    String suppliedType = columns[5];
	    String featureType = columns[6];

	    Item feature;
	    
            if (!StringUtils.isEmpty(featureId) && !StringUtils.isEmpty(featureType)) {
                if (featureType.equals("ALT")){ 
		    feature = getTypedItem(featureId, featureType);
		    if (!StringUtils.isEmpty(sourceType)){
			if(sourceType.equals("source")){
			    System.out.println(sourceType);
			    if (!StringUtils.isEmpty(LabId) && LabId.startsWith("ZDB-LAB")) {
				System.out.println(featureId);
				feature.addToCollection("labOfOrigin",getLab(LabId));
			    }
			}
			//		    else if (!StringUtils.isEmpty()) {
			//	feature.addToCollection("suppliedBy", );
			//}
		    }
		}
	    }
	}
    }
    private Item getLab(String primaryIdentifier)
        throws SAXException {
        Item item = labs.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Lab");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            labs.put(primaryIdentifier, item);
	    try {
                store(item);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }
        return item;
    }

    private Item getTypedItem(String primaryIdentifier, String type) throws SAXException {
        Item typedItem = null;

        if (type.equals("INSERTION")) {
            typedItem = getFeature(primaryIdentifier, "Insertion");
        } else if (type.equals("POINT_MUTATION")) {
            typedItem = getFeature(primaryIdentifier, "PointMutation");
        } else if (type.equals("DELETION")) {
            typedItem = getFeature(primaryIdentifier, "Deletion");
        } else if (type.equals("DEFICIENCY")) {
            typedItem = getFeature(primaryIdentifier, "ChromosomalDeletion");
        } else if (type.equals("TRANSLOC")) {
            typedItem = getFeature(primaryIdentifier, "Translocation");
        } else if (type.equals("INVERSION")) {
            typedItem = getFeature(primaryIdentifier, "Inversion");
        } else if (type.equals("TRANSGENIC_INSERTION")) {
            typedItem = getFeature(primaryIdentifier, "TransgenicInsertion");
        } else if (type.equals("SEQUENCE_VARIANT")) {
            typedItem = getFeature(primaryIdentifier, "SequenceAlteration");
        } else if (type.equals("UNSPECIFIED")) {
            typedItem = getFeature(primaryIdentifier, "SequenceAlteration");
        } else if (type.equals("COMPLEX_SUBSTITUTION")) {
            typedItem = getFeature(primaryIdentifier, "ComplexSubstitution");
        } else if (type.equals("TRANSGENIC_UNSPECIFIED")) {
            typedItem = getFeature(primaryIdentifier, "TransgenicInsertion");
        } else if (type.equals("INDEL")) {
            typedItem = getFeature(primaryIdentifier, "Indel");
        }

        return typedItem;
    }
    private Item getFeature(String primaryIdentifier, String soTermName) {
        Item item = features.get(primaryIdentifier);
        if (item == null) {
            item = createItem(soTermName);
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            features.put(primaryIdentifier, item);
        }

        return item;
    }

}
