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
public class OntologySubsetConverter extends BioFileConverter
{
    //
    private static final String DATASET_TITLE = "Ontology Subsets";
    private static final String DATA_SOURCE_NAME = "ZFIN";

    private static final Logger LOG = Logger.getLogger(OntologySubsetConverter.class);
    private Map<String, Item> subsets = new HashMap<String, Item>(10000);
    private Map<String, Item> terms = new HashMap<String, Item>(10000);
    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public OntologySubsetConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }
   

    /**
     * 
     *
     * {@inheritDoc}
     */
    public void process(Reader reader) throws Exception {

	processSubsets(reader);

	    try{
		for (Item subset : subsets.values()){
                    store(subset);
                }
		for (Item term : terms.values()){
                    store(term);
                }

	    }catch (ObjectStoreException e) {
		throw new SAXException(e);
	    }

    }

    public void processSubsets(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 6) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }

            String subsetId = line[0];
            String subsetName = line[1];
            String subsetDefinition = line[2];
            String subsetType = line[3];
	    String subsetOntologyId = line[4];
	    String termId = line[5];
	    String ontology = line[6];

            if (!StringUtils.isEmpty(subsetId)){
                Item subset = getSubset(subsetId);
		
                if (!StringUtils.isEmpty(subsetName)){
                    subset.setAttribute("name",subsetName);
		}
		if (!StringUtils.isEmpty(subsetDefinition)){
		    subset.setAttribute("definition",subsetDefinition);
		}
		if (!StringUtils.isEmpty(subsetType)){
		    subset.setAttribute("type",subsetType);
		}
	   
		if (!StringUtils.isEmpty(termId)){
		    if (!StringUtils.isEmpty(ontology)){
			Item termT = getType(ontology,termId);
			termT.addToCollection("subsets",subset);
		    }
		}
	    }
        }
    }

    private Item getSubset(String primaryIdentifier)
	throws SAXException {
	Item item = subsets.get(primaryIdentifier);
        if (item == null) {
            item = createItem("OntologySubset");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            subsets.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getType (String ontology, String termId) 
	throws SAXException {
	Item typedItem = null;
	if (ontology.equals("behavior_ontology")){
	    typedItem = getTerm(termId,"MeshTerm");
	}
	else if
            (ontology.equals("disease_ontology")){
            typedItem =getTerm(termId,"DOTerm");
        }
	else if 
	    (ontology.equals("biological_process")){
            typedItem =getTerm(termId,"GOTerm");
        }
        else if
	    (ontology.equals("cellular_component")){
            typedItem =getTerm(termId,"GOTerm");
        }
        else if
	    (ontology.equals("molecular_function")){
            typedItem =getTerm(termId,"GOTerm");
        }
        else if
	    (ontology.equals("mouse_pathology.ontology")){
            typedItem =getTerm(termId,"MPATHTerm");
        }
        else if
	    (ontology.equals("pato.ontology")){
            typedItem =getTerm(termId,"PATOTerm");
        }
        else if
	    (ontology.equals("quality")){
            typedItem =getTerm(termId,"PATOTerm");
        }
        else if
	    (ontology.equals("sequence")){
            typedItem =getTerm(termId,"SOTerm");
        }
        else if
	    (ontology.equals("spatial")){
            typedItem =getTerm(termId,"SPATIALTerm");
        }
        else if
	    (ontology.equals("zebrafish_anatomical_ontology")){
            typedItem =getTerm(termId,"ZFATerm");
        }
        else if
	    (ontology.equals("zebrafish_anatomy")){
            typedItem =getTerm(termId,"ZFATerm");
        }
        else if     
	    (ontology.equals("zebrafish_stages")){
            typedItem =getTerm(termId,"ZFATerm");
        }

	return typedItem;
    }

    private Item getTerm(String primaryIdentifier, String termType)
        throws SAXException {
        Item item = terms.get(primaryIdentifier);
        if (item == null) {
            item = createItem(termType);
            item.setAttribute("identifier", primaryIdentifier);
	    item.setReference("organism", getOrganism("7955"));                                                                                                  
            terms.put(primaryIdentifier, item);
        }
        return item;
    }


   
    
}
