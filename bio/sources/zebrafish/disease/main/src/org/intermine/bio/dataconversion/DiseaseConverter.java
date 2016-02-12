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

import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.xml.full.Item;

import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
public class DiseaseConverter extends BioFileConverter
{
    //
    private static final Logger LOG = Logger.getLogger(DiseaseConverter.class);
    
    private Map<String, Item> dats = new HashMap();
    private Map<String, Item> doTerms = new HashMap();
    private Map<String, Item> pubs = new HashMap();
    private Map<String, Item> fishes = new HashMap();
    private Map<String, Item> exps = new HashMap();
    private static final String DATASET_TITLE = "ZFIN";
    private static final String DATA_SOURCE_NAME = "Disease Curation";

    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public DiseaseConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }

    /**
     * 
     *
     * {@inheritDoc}
     */
    public void process(Reader reader) throws Exception {

	processDisease (reader);

	try {
            for (Item fish: fishes.values()){
                store(fish);
            }

            for (Item exp : exps.values()){
                store(exp);
            }
            for (Item pub : pubs.values()) {
                store(pub);
            }

	    for (Item dat : dats.values()) {
                store(dat);
            }
        }
	catch (ObjectStoreException e) {
	    throw new SAXException(e);
	}

    }
    
    public void processDisease(Reader reader) throws Exception {
	
        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');
	
        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
	    
            if (line.length < 5) {
                throw new RuntimeException("Disease line does not have enough elements: " + line.length + line[0]);
            }
	    
            String primaryIdentifier = line[0];
	    String dotermId = line[1];
            String pubId = line[2];
            String evidenceCode = line[3];
	    String fishId = line[4];
	    String expId = line[5];
	    
            Item diseaseAnnotation = getDiseaseAnnotation(primaryIdentifier);
	    System.out.println(primaryIdentifier);
	    
            if (!StringUtils.isEmpty(dotermId)) {
		Item doT = getDOTerm(dotermId);
      		diseaseAnnotation.setReference("disease",doT);
            }
            if (!StringUtils.isEmpty(pubId)) {
                System.out.println(pubId);
		Item pub = getPub(pubId);
	        diseaseAnnotation.setReference("publication", pub);
            }
            if (!StringUtils.isEmpty(evidenceCode)) {
               diseaseAnnotation.setAttribute("evidenceCode", evidenceCode);
            }
            if (!StringUtils.isEmpty(fishId)) {
                System.out.println("fishId: " + fishId);
		Item fsh = getFish(fishId);
		diseaseAnnotation.setReference("fish", fsh );
		
            }
	    if (!StringUtils.isEmpty(expId)) {
		System.out.println(expId);
		Item Env = getExperiment(expId);
		diseaseAnnotation.setReference("environment",Env);
            }
	   
	}
    }
    
    private Item getDiseaseAnnotation(String primaryIdentifier)
	throws SAXException {
        Item item = dats.get(primaryIdentifier);
        if (item == null) {
            item = createItem("DiseaseAnnotation");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            dats.put(primaryIdentifier, item);
	    
        }
        return item;
    }

    private Item getDOTerm(String primaryIdentifier)
        throws SAXException {
        Item item = doTerms.get(primaryIdentifier);
        if (item == null) {
            item = createItem("DOTerm");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
	    //item.setReference("organism", getOrganism("7955"));
            doTerms.put(primaryIdentifier, item);
	       try {
	    	store(item);
	     } catch (ObjectStoreException e) {
	    		throw new SAXException(e);
	     }
	}
	return item;
    }
    
    private Item getPub(String primaryIdentifier)
        throws SAXException {
        Item item = pubs.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Publication");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
	    pubs.put(primaryIdentifier, item);
	    // item.setReference("organism", getOrganism("7955"));
	    //    try {
            //    store(item);
            //} catch (ObjectStoreException e) {
            //    throw new SAXException(e);
	    // }
	}
	return item;
    }
    
    private Item getExperiment(String primaryIdentifier)
	throws SAXException {
	Item item = exps.get(primaryIdentifier);
	if (item == null) {
	    item = createItem("Environment");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
	    exps.put(primaryIdentifier, item);
	    //try {
	    //		store(item);
	    //} catch (ObjectStoreException e) {
	    //		throw new SAXException(e);
	    //}
	}
	return item;
    }

    private Item getFish(String primaryIdentifier)
	throws SAXException {
	System.out.println(primaryIdentifier);
	Item item = fishes.get(primaryIdentifier);
	if (item == null) {
	    item = createItem("Fish");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
	    item.setReference("organism", getOrganism("7955"));
	    fishes.put(primaryIdentifier, item);
	    //try {
            //    store(item);
            //} catch (ObjectStoreException e) {
            //    throw new SAXException(e);
	    // }
	    
	}
	return item;
    }
    
}
