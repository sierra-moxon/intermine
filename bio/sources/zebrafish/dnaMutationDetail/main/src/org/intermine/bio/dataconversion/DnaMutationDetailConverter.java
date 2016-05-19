package org.intermine.bio.dataconversion;

/*
 * Copyright (C) 2002-2015 FlyMine
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
public class DnaMutationDetailConverter extends BioFileConverter
{
    //

    private Map<String, Item> items = new HashMap<String, Item>(40000);
    private Map<String, Item> terms = new HashMap<String, Item>(40000);

    public DnaMutationDetailConverter(ItemWriter writer, Model model)
	throws ObjectStoreException {
        super(writer, model, "ZFIN", "DNA Mutation Detail");
    }

    @Override
    public void process(Reader reader) throws Exception {
	processDnaMutationDetail (reader);

	try {
            for (Item item : items.values()) {
		store(item);
	    }
        } catch (ObjectStoreException e) {
            throw new Exception(e);
        }
    }

    public void processDnaMutationDetail (Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 12) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }

            String dataId = line[0];
	    String featureId = line[1];
	    String dnaMutationTerm = line[2];
	    String dnaAccNum = line[3];
	    String fdbcontId = line[4];
	    String dnaStart = line[5];
	    String dnaEnd = line[6];
	    String bpAdd = line[7];
	    String bpMinus = line[8];
	    String exonNumber = line[9];
	    String intronNumber = line[10];
	    String geneLocalization = line[11];
	    String type = line[12];

	    Item feature;

	    if (!StringUtils.isEmpty(featureId)){
		feature = getTypedItem(featureId, type);
		if (!StringUtils.isEmpty(dnaMutationTerm)){
		    feature.setReference("dnaMutation", getTerm(dnaMutationTerm));
		}
		if (!StringUtils.isEmpty(dnaAccNum)){
		    feature.setAttribute("dnaSequenceOfReference",dnaAccNum);
		}
		if (!StringUtils.isEmpty(dnaStart)){
		    feature.setAttribute("dnaPositionStart",dnaStart);
		}
		if (!StringUtils.isEmpty(dnaEnd)){
		    feature.setAttribute("dnaPositionEnd",dnaEnd);
		}
		if (!StringUtils.isEmpty(bpAdd)){
		    feature.setAttribute("dnaBpPlus",bpAdd);
		}
		if (!StringUtils.isEmpty(bpMinus)){
		    feature.setAttribute("dnaBpMinus",bpMinus);
		}
		if (!StringUtils.isEmpty(exonNumber)){
		    feature.setAttribute("dnaExon",exonNumber);
		}
		if (!StringUtils.isEmpty(intronNumber)){
		    feature.setAttribute("dnaIntron",intronNumber);
		}
		if (!StringUtils.isEmpty(geneLocalization)){
		    feature.setReference("dnaGeneLocalization",getTerm(geneLocalization));
		}
	    }
	    
	}
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
	    typedItem = getFeature(primaryIdentifier,"Indel");
	}
	else {
	    typedItem = getFeature(primaryIdentifier,"SequenceAlteration");
	}
	
	return typedItem;
    }
    
    private Item getFeature(String primaryIdentifier, String soTermName) throws SAXException {
	Item item = items.get(primaryIdentifier);
	if (item == null) {
	    item = createItem(soTermName);
	    item.setReference("organism", getOrganism("7955"));
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
	    items.put(primaryIdentifier, item);
	}
	
	return item;
    }
    private Item getTerm(String primaryIdentifier) throws SAXException {
	Item item = terms.get(primaryIdentifier);
	if (item == null) {
	    String termType = "SOTerm";
	    item = createItem(termType);
	    item.setAttribute("identifier", primaryIdentifier);
	    terms.put(primaryIdentifier, item);
	    try {
		store(item);
	    } catch (ObjectStoreException e) {
		throw new SAXException(e);
	    }
	}
	return item;
    }
    
}    

    
