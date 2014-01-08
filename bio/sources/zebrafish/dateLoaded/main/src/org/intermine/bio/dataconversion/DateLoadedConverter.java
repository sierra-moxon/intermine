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
import java.io.Reader;

import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.xml.full.Item;


/**
 * 
 * @author
 */
public class DateLoadedConverter extends BioFileConverter {
    //
    private static final String DATASET_TITLE = "ZebrafishMine Last Loaded Date";
    private static final String DATA_SOURCE_NAME = "ZFIN";

    private static final Logger LOG = Logger.getLogger(DateLoadedConverter.class);
    protected String organismRefId;
    private Map<String, Item> items = new HashMap<String, Item>(25550);

    private Model model;
    private ItemWriter writer;

    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public DateLoadedConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }
    public void process(Reader reader) throws Exception {
	
        processDate(reader);
	
    }

    public void processDate(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 2) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }

            String dataId = line[0];
            String date = line[1];


            if (!StringUtils.isEmpty(dataId)){
                Item dateLoaded = getDateLoaded(dataId, date);
            }

        }
    }

    private Item getDateLoaded(String primaryIdentifier, String date)
        throws SAXException {
        Item item = items.get(primaryIdentifier);
        if (item == null) {
            item = createItem("LoadedDate");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
	    item.setAttribute("timestamp", date);
            //item.setReference("organism", getOrganism("Zebrafish"));                                                                                                 
            items.put(primaryIdentifier, item);

            try {
		store(item);
            } catch (ObjectStoreException e) {
		throw new SAXException(e);
            }
        }
        return item;
    }

}
