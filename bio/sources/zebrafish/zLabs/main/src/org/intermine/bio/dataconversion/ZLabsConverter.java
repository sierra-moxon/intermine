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
public class ZLabsConverter extends BioFileConverter
{
    //
    private static final String DATASET_TITLE = "Labs";
    private static final String DATA_SOURCE_NAME = "ZFIN";

    private static final Logger LOG = Logger.getLogger(ZLabsConverter.class);
    private Map<String, Item> labs = new HashMap<String, Item>(10000);
    private Map<String, Item> people = new HashMap<String, Item>(10000);

    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public ZLabsConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }

    /**
     * 
     *
     * {@inheritDoc}
     */
    public void process(Reader reader) throws Exception {

	processLabs(reader);

	try {
	    for (Item lab : labs.values()){
		store(lab);
	    }
	}
	catch (ObjectStoreException e){
	    throw new SAXException(e);
	}

    }
    public void processLabs (Reader reader) throwse Exception {
    
	Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 6) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }

            String labId  = line[0];
            String name = line[1];
            String contactPerson = line[2];
	    String url = line[3];
	    String email = line[4];
	    String phone = line[5];
	    String phone2 = line[6];

            if (!StringUtils.isEmpty(labId)){
                Item lab = getLab(labId);
                if (!StringUtils.isEmpty(name)){
                    lab.setAttribute("name",name);
                }
                if (!StringUtils.isEmpty(contactPerson)){
                    lab.setReference("contactPerson",getPerson(contactPerson));
                }
                if (!StringUtils.isEmpty(url)){
                    lab.setAttribute("url",url);
                }
                if (!StringUtils.isEmpty(email)){
                    lab.setAttribute("email",email);
                }
                if (!StringUtils.isEmpty(phone)){
                    lab.setAttribute("phone",phone);
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
        }
        return item;
    }
    private Item getPerson(String primaryIdentifier)
        throws SAXException {
        Item item = people.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Person");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            people.put(primaryIdentifier, item);
	    try {
		store(item);
	    } catch (ObjectStoreException e) {
		throw new SAXException(e);
	    }
	}
        return item;
    }


}