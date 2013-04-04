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
public class ZebrafishFeatureAliasesConverter extends BioFileConverter
{


    private static final Logger LOG = Logger.getLogger(ZebrafishFeatureAliasesConverter.class);
    protected String organismRefId;
    private Map<String, Item> features = new HashMap();
    private Set<String> synonyms = new HashSet();
    private Map<String, Item> terms = new HashMap();    
    private static final String DATASET_TITLE = "Feature Aliases";
    private static final String DATA_SOURCE_NAME = "ZFIN";

    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public ZebrafishFeatureAliasesConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }

    /**
     * 
     *
     * {@inheritDoc}
     */
    public void process(Reader reader) throws Exception {

	processAliases(reader);

    }



    public void processAliases(Reader reader) throws Exception {

	Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 3) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }

            String dataId = line[0];
            String dataPrimaryIdentifier = line[1];
            String alias = line[2];
            String aliasType = line[3];
	    String featureType = line[4];

            if (!StringUtils.isEmpty(dataId)) {
                Item itemAlias = getTypedItem(dataPrimaryIdentifier, featureType);
                if (aliasType.equals("alias")) {
                    aliasType = "name";
                }
                addSynonym(itemAlias, aliasType, alias, dataId);
		addSynonym(itemAlias, "accession", dataPrimaryIdentifier, dataId);
            }
        }
    }


    private void addSynonym(Item item, String type, String value, String dataId)
	throws SAXException {
        setSynonym(item.getIdentifier(), type, value, dataId);
    }

    private void setSynonym(String subjectRefId, String type, String value, String dataId)
        throws SAXException {
        String key = dataId;
        if (!synonyms.contains(key)) {
            Item synonym = createItem("Synonym");
            synonym.setAttribute("value", value);
            synonym.setReference("subject", subjectRefId);
            synonyms.add(key);
	    try {
                store(synonym);
            } catch (ObjectStoreException e) {
		throw new SAXException(e);
            }
	}
    }

    private Item getTypedItem(String primaryIdentifier, String type) throws SAXException {
        Item typedItem = getFeature(primaryIdentifier,"SequenceAlteration");

        if (type.equals("INSERTION")) {
            typedItem = getFeature(primaryIdentifier,"Insertion");
        } else if (type.equals("POINT_MUTATION")) {
            typedItem = getFeature(primaryIdentifier,"PointMutation");
        } else if (type.equals("DELETION")) {
            typedItem = getFeature(primaryIdentifier,"Deletion");
        } else if (type.equals("DEFICIENCY")) {
            typedItem = getFeature(primaryIdentifier,"ChromosomalDeletion");
        } else if (type.equals("TRANSLOC")) {
            typedItem = getFeature(primaryIdentifier,"Translocation");
        } else if (type.equals("INVERSION")) {
            typedItem = getFeature(primaryIdentifier,"Inversion");
        } else if (type.equals("TRANSGENIC_INSERTION")) {
            typedItem = getFeature(primaryIdentifier,"TransgenicInsertion");
        } else if (type.equals("SEQUENCE_VARIANT")) {
            typedItem = getFeature(primaryIdentifier,"SequenceAlteration");
        } else if (type.equals("UNSPECIFIED")) {
            typedItem = getFeature(primaryIdentifier,"SequenceAlteration");
        } else if (type.equals("COMPLEX_SUBSTITUTION")) {
            typedItem = getFeature(primaryIdentifier,"ComplexSubstitution");
        } else if (type.equals("TRANSGENIC_UNSPECIFIED")) {
            typedItem = getFeature(primaryIdentifier,"TransgenicInsertion");
        }

        return typedItem;
    }
    
    private Item getFeature(String primaryIdentifier, String soTermName) throws SAXException {
	Item item = terms.get(primaryIdentifier);
        if (item == null) {
            item = createItem(soTermName);
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            terms.put(primaryIdentifier, item);
        }
        else {
            if (item.getClassName().equals("SequenceAlteration")) {
                terms.remove(item);
                item = createItem(soTermName);
                item.setReference("organism", getOrganism("7955"));
                item.setAttribute("primaryIdentifier", primaryIdentifier);
                terms.put(primaryIdentifier, item);
	    }
	}
	try{
	    store(item);
	} catch (ObjectStoreException e) {
	    throw new SAXException(e);
	}
        return item;
    }

}
