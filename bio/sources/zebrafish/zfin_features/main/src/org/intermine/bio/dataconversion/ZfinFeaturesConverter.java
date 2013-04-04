package org.intermine.bio.dataconversion;

/*
 * Copyright (C) 2002-2009 FlyMine
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
 * DataConverter to load ZFIN feature identifiers from text files
 */
public class ZfinFeaturesConverter extends BioFileConverter {

    private static final Logger LOG = Logger.getLogger(ZfinFeaturesConverter.class);
    protected String organismRefId;
    private Map<String, Item> features = new HashMap();
    private Map<String, Item> terms = new HashMap();
    private Set<String> synonyms = new HashSet();


    /**
     * Constructor
     *
     * @param writer the ItemWriter used to handle the resultant items
     * @param model  the Model
     * @throws ObjectStoreException if an error occurs in storing
     */
    public ZfinFeaturesConverter(ItemWriter writer, Model model)
            throws ObjectStoreException {
        super(writer, model, "ZFIN", "ZFIN Alleles and Transgenics Data Set");

    }

    public void process(Reader reader) throws Exception {

	processFeatures(reader);

	    try {
		for (Item feature : features.values()){
		    
		    store(feature);
		}
	    }

	    catch (ObjectStoreException e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		while (e != null) {
		    e.printStackTrace(pw);
		}
		pw.flush();
		throw new Exception(sw.toString());
	    }

	
    }
    

    public void processFeatures(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 4) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }
            String primaryIdentifier = line[0];

            String name = line[1];
            String abbrev = line[2];
            String type = line[3];

            Item feature ;

            if (!StringUtils.isEmpty(primaryIdentifier)){
                feature = getTypedItem(primaryIdentifier,type);

                if (!StringUtils.isEmpty(name)) {
                     feature.setAttribute("name", name);
                }
                if (!StringUtils.isEmpty(abbrev)) {
                    feature.setAttribute("symbol", abbrev);
                }
		if (!StringUtils.isEmpty(primaryIdentifier)) {
                    feature.setAttribute("featureId", primaryIdentifier);
                }

		//                try {
		//  store(feature);
		// } catch (ObjectStoreException e) {
		//   throw new SAXException(e);
                //}
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

    private Item getFeature(String primaryIdentifier, String soTermName) {
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
        return item;
    }

}
