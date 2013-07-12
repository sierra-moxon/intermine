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

import org.intermine.bio.io.gff3.GFF3Record;
import org.intermine.metadata.Model;
import org.intermine.xml.full.Item;

/**
 * A converter/retriever for the TrimZfinClone dataset via GFF files.
 */

public class TrimZfinCloneGFF3RecordHandler extends GFF3RecordHandler
{

    /**
     * Create a new TrimZfinCloneGFF3RecordHandler for the given data model.
     * @param model the model for which items will be created
     */
    public TrimZfinCloneGFF3RecordHandler (Model model) {
        super(model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(GFF3Record record) {
        // This method is called for every line of GFF3 file(s) being read.  Features and their
        // locations are already created but not stored so you can make changes here.  Attributes
        // are from the last column of the file are available in a map with the attribute name as
        // the key.   For example:
        //
             Item feature = getFeature();
             String id = record.getAttributes().get("zdb_id");
             feature.setAttribute("primaryIdentifier", id);
	     String name = record.getAttributes().get("name");
	     feature.setAttribute("name",name);
	     
	     //
        // Any new Items created can be stored by calling addItem().  For example:
        // 
             //String bacIdentifier = record.getAttributes().get("zdb_id");
             //bac = getClone(bacIdentifier);
             //bac.setAttribute("primaryIdentifier", bacIdentifier);
             //addItem(bac);
        //
        // You should make sure that new Items you create are unique, i.e. by storing in a map by
        // some identifier. 

    }

    private Item getClone(String primaryIdentifier) {
	Item item = bacs.get(primaryIdentifier);
        if (item == null) {
            item = createItem("DNAClone");
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            bacs.put(primaryIdentifier, item);
	    try {                                                                                                               
		store(item);
	    } catch (ObjectStoreException e) {                                                                                                                throw new SAXException(e); 
	    }   
	}
	
        return item;
    }


}
