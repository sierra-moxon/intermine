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
public class GenotypePubsConverter extends BioFileConverter
{
    //
    private static final String DATASET_TITLE = "Genotype Attribution";
    private static final String DATA_SOURCE_NAME = "ZFIN";

    private static final Logger LOG = Logger.getLogger(GenotypePubsConverter.class);
    private Map<String, Item> pubs = new HashMap();
    private Map<String, Item> genos = new HashMap();


    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public GenotypePubsConverter(ItemWriter writer, Model model) {
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
	    for (Item item : genos.values()){
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
            String pubId = line[0];

		
	    if (!StringUtils.isEmpty(pubId)){
		Item pub = getPub(pubId);
	    
		if (!StringUtils.isEmpty(dataId)){
		    Item genotype = getGenotype(dataId);
		    genotype.addToCollection("publications",pub);
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


    private Item getGenotype(String primaryIdentifier) throws SAXException {
        Item item = genos.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Genotype");
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            genos.put(primaryIdentifier, item);
            //try{
            //    store(item);
            //} catch (ObjectStoreException e) {
            //    throw new SAXException(e);
	    // }
        }

        return item;
    }
    


}

