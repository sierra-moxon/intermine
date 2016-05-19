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
public class TranscriptMutationDetailConverter extends BioFileConverter
{
    private Map<String, Item> items = new HashMap<String, Item>(40000);
    private Map<String, Item> terms = new HashMap<String, Item>(40000);
    private Map<String, Item> transcriptConsequences = new HashMap<String, Item>(40000);

    public TranscriptMutationDetailConverter(ItemWriter writer, Model model)
	throws ObjectStoreException {
        super(writer, model, "ZFIN", "Transcript Mutation Detail");
    }


    public void process(Reader reader) throws Exception {

	processTranscriptMutationDetail (reader);
        try {
	    for (Item tcon : transcriptConsequences.values()){
		store(tcon);
	    }
	    for (Item item : items.values()) {
                store(item);
            }
        } catch (ObjectStoreException e) {
            throw new Exception(e);
        }

    }
    public void processTranscriptMutationDetail (Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 4) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }

            String dataId = line[0];
            String featureId = line[1];
            String transcriptConsequenceLoad = line[2];
            String exonNumber = line[3];
	    String intronNumber = line[4];
	    String type = line[5];

	    Item feature ;
	    Item transcriptConsequence;

            if (!StringUtils.isEmpty(featureId)){
                feature = getTypedItem(featureId, type);

                if (!StringUtils.isEmpty(dataId)){
		    transcriptConsequence = getTranscriptConsequence(dataId);
		    if (!StringUtils.isEmpty(exonNumber)){
			transcriptConsequence.setAttribute("exonNumber",exonNumber);
		    }
		    if (!StringUtils.isEmpty(intronNumber)){
			transcriptConsequence.setAttribute("intronNumber",intronNumber);
		    }
		    if (!StringUtils.isEmpty(transcriptConsequenceLoad)){
                        transcriptConsequence.setReference("consequence",getTerm(transcriptConsequenceLoad));
                    }
		    feature.addToCollection("transcriptConsequences", transcriptConsequence);
		}
	    }
	}
    }

    private Item getTranscriptConsequence(String primaryIdentifier) throws SAXException {
        Item item = transcriptConsequences.get(primaryIdentifier);
        if (item == null) {
            item = createItem("TranscriptConsequence");
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            transcriptConsequences.put(primaryIdentifier, item);
	   
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
