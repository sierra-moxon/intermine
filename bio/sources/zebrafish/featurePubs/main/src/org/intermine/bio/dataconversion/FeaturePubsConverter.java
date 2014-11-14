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
public class FeaturePubsConverter extends BioFileConverter
{
    //
    private static final String DATASET_TITLE = "Sequence Alteration Attribution";
    private static final String DATA_SOURCE_NAME = "ZFIN";

    private static final Logger LOG = Logger.getLogger(FeaturePubsConverter.class);
    private Map<String, Item> pubs = new HashMap();
    private Map<String, Item> terms = new HashMap();


    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public FeaturePubsConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }

    /**
     * 
     *
     * {@inheritDoc}
     */
    public void process(Reader reader) throws Exception {

	processPubs(reader);

	try{

	    //for (Item item : pubs.values()){
	    //	store(item);
	    //}
	    for (Item item : terms.values()){
		store(item);                                                                                                                
	    }
	}catch (ObjectStoreException e) {                                                                                             
	    throw new SAXException(e);                                           
	}
    }


    public void processPubs(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 3) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }

            String dataId = line[1];
            String pubId = line[2];
	    String type = line[3];
	    String featureType = line[4];
		
	    if (!StringUtils.isEmpty(pubId)){
		Item pub = getPub(pubId);
	    
		if (!StringUtils.isEmpty(dataId)){
		    Item feature = getTypedItem(dataId, featureType);
		    feature.addToCollection("publications",pub);
		}
	    }
	}
    }
 


    private Item getPub(String primaryIdentifier)
	throws SAXException {
        Item item = pubs.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Publication");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            //item.setReference("organism", getOrganism("Zebrafish"));                                                                                  
            pubs.put(primaryIdentifier, item);

            try {
	       store(item);
            } catch (ObjectStoreException e) {
	    		throw new SAXException(e);
            }
	}
        return item;
    }



    private Item getTypedItem(String primaryIdentifier, String type) throws SAXException {
        Item typedItem ;

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
	} else if (type.equals("INDEL")) {
            typedItem = getFeature(primaryIdentifier, "Indel");
        }

	else {
	    typedItem = getFeature(primaryIdentifier,"SequenceAlteration");
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
            //try{
            //    store(item);
            //} catch (ObjectStoreException e) {
            //    throw new SAXException(e);
	    // }
        }

        return item;
    }
    


}

