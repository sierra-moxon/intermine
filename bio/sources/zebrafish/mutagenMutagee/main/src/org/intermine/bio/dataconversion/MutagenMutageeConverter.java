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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.util.FormattedTextParser;
import org.intermine.xml.full.Item;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.*;



/**
 * 
 * @author
 */
public class MutagenMutageeConverter extends BioFileConverter
{
    //
    private static final String DATASET_TITLE = "SequenceAlteration Mutagen and Mutagee";
    private static final String DATA_SOURCE_NAME = "ZFIN";
    private Map<String, Item> features = new HashMap<String, Item>(30000);

    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public MutagenMutageeConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }

    /**
     * 
     *
     * {@inheritDoc}
     */
    public void process(Reader reader) throws Exception {
	processFeatures(reader);

	try {
            for (Item feature : features.values())
                store(feature);
        } catch (ObjectStoreException e) {
            throw new Exception(e);
        }

	
    }

    public void processFeatures(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] columns = (String[]) lineIter.next();
            if (columns.length < 4) {
                throw new RuntimeException("Line does not have enough elements: " + columns.length + columns[0]);
            }
            String featureId = columns[0];

            String mutagen = columns[1];
            String mutagee = columns[2];
            String type = columns[3];

            Item feature;

            if (!StringUtils.isEmpty(featureId) && !StringUtils.isEmpty(type)) {
                feature = getTypedItem(featureId, type);

                if (!StringUtils.isEmpty(mutagen)) {		    
                    feature.setAttribute("mutagen", mutagen);
                }
                if (!StringUtils.isEmpty(mutagee)) {
                    feature.setAttribute("mutagee", mutagee);
                }

            }

        }
    }
    private Item getTypedItem(String featureId, String type) throws SAXException {
        Item typedItem = null;

	if (type.equals("INSERTION")) {
            typedItem = getFeature(featureId, "Insertion");
        } else if (type.equals("POINT_MUTATION")) {
            typedItem = getFeature(featureId, "PointMutation");
        } else if (type.equals("DELETION")) {
            typedItem = getFeature(featureId, "Deletion");
        } else if (type.equals("DEFICIENCY")) {
            typedItem = getFeature(featureId, "ChromosomalDeletion");
        } else if (type.equals("TRANSLOC")) {
            typedItem = getFeature(featureId, "Translocation");
        } else if (type.equals("INVERSION")) {
            typedItem = getFeature(featureId, "Inversion");
        } else if (type.equals("TRANSGENIC_INSERTION")) {
            typedItem = getFeature(featureId, "TransgenicInsertion");
        } else if (type.equals("SEQUENCE_VARIANT")) {
            typedItem = getFeature(featureId, "SequenceAlteration");
        } else if (type.equals("UNSPECIFIED")) {
            typedItem = getFeature(featureId, "SequenceAlteration");
        } else if (type.equals("COMPLEX_SUBSTITUTION")) {
            typedItem = getFeature(featureId, "ComplexSubstitution");
        } else if (type.equals("TRANSGENIC_UNSPECIFIED")) {
            typedItem = getFeature(featureId, "TransgenicInsertion");
        } else if (type.equals("INDEL")) {
            typedItem = getFeature(featureId, "Indel");
        }

        return typedItem;
    }
    
    private Item getFeature(String featureId, String soTermName) {
        Item item = features.get(featureId);
        if (item == null) {
            item = createItem(soTermName);
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", featureId);
            features.put(featureId, item);
        }
	
        return item;
    }

}
