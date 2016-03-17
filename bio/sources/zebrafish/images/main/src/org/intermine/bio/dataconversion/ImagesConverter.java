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
public class ImagesConverter extends BioFileConverter
{
    //
    private static final String DATASET_TITLE = "Images";
    private static final String DATA_SOURCE_NAME = "ZFIN";

    private static final Logger LOG = Logger.getLogger(ImagesConverter.class);
    private Map<String, Item> figs = new HashMap<String, Item>(10000);
    private Map<String, Item> images = new HashMap<String, Item>(10000);


    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public ImagesConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }

    /**
     * 
     *
     * {@inheritDoc}
     */
    public void process(Reader reader) throws Exception {

	processImages(reader);

	try{
	    for (Item fig : figs.values()){
		store(fig);
	    }
	    for (Item image : images.values()){
		store(image);
	    }

	}catch (ObjectStoreException e) {
	    throw new SAXException(e);
	}

    }

    public void processImages(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 3) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }

            String imageId = line[0];
            String figId = line[1];
            String label = line[2];
            
	    if (!StringUtils.isEmpty(imageId)){
		Item image = getImage(imageId);
		if (!StringUtils.isEmpty(label)){
		    image.setAttribute("label",label);
		}
		if (!StringUtils.isEmpty(figId)){
		    Item fig = getFigure(figId);
		    image.setReference("figure",fig);
		}
	    }
	}	

    }

    private Item getImage(String primaryIdentifier)
        throws SAXException {
        Item item = images.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Image");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            images.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getFigure(String primaryIdentifier)
        throws SAXException {
        Item item = figs.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Figure");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            figs.put(primaryIdentifier, item);
        }
        return item;
    }
}
