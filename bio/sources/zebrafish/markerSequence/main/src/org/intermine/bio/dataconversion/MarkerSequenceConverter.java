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
 * @author
 */
public class MarkerSequenceConverter extends BioFileConverter {
    //
    private static final String DATASET_TITLE = "Morpholino and SNP Sequences";
    private static final String DATA_SOURCE_NAME = "ZFIN";

    private static final Logger LOG = Logger.getLogger(MarkerSequenceConverter.class);
    private Map<String, Item> mrkrs = new HashMap();
    private Map<String, Item> seqs = new HashMap();

    /**
     * Constructor
     *
     * @param writer the ItemWriter used to handle the resultant items
     * @param model  the Model
     */
    public MarkerSequenceConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }

    public void process(Reader reader) throws Exception {

        processSeqs(reader);

        try {

            for (Item item : seqs.values()) {
                store(item);
            }
            for (Item item : mrkrs.values()) {
                store(item);
            }
        } catch (ObjectStoreException e) {
            throw new SAXException(e);
        }
    }


    public void processSeqs(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 3) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }

            String dataId = line[0];
            String seq = line[1];
            String startOffset = line[2];
            String stopOffset = line[3];
            String variation = line[4];
            String type = line[5];
	    String sequence2 = line[6];

            if (!StringUtils.isEmpty(dataId)) {
                Item mrkr = getTypedItem(dataId, type);
                if (!StringUtils.isEmpty(seq)) {
                    Item sequence = getSeq(dataId, seq, variation, startOffset, stopOffset);
                    mrkr.addToCollection("sequences", sequence);
		    if (!StringUtils.isEmpty(sequence2)){
			mrkr.addToCollection("sequences",sequence);
		    }
                    if (!StringUtils.isEmpty(variation)) {
                        sequence.setAttribute("variation", variation);
                    }
                    if (!StringUtils.isEmpty(startOffset)) {
                        sequence.setAttribute("offsetStart", startOffset);
                    }
                    if (!StringUtils.isEmpty(stopOffset)) {
                        sequence.setAttribute("offsetEnd", stopOffset);
                    }
                }

            }


        }
    }


    private Item getTypedItem(String primaryIdentifier, String type) throws SAXException {
        Item typedItem;

        if (type.equals("MRPHLNO")) {
            typedItem = getMrkr(primaryIdentifier, "MorpholinoOligo");
        } else if (type.equals("SNP")) {
            typedItem = getMrkr(primaryIdentifier, "SNP");
	} else if (type.equals("TALEN")){
	    typedItem = getMrkr(primaryIdentifier, "Reagent");
	} else if (type.equals("CRISPR")){
	    typedItem = getMrkr(primaryIdentifier, "Reagent");
        } else if (type.equals("SSLP")){
            typedItem = getMrkr(primaryIdentifier, "SimpleSequenceLengthVariation");
        }else {
            typedItem = getMrkr(primaryIdentifier, "Gene");
        }

        return typedItem;
    }

    private Item getMrkr(String primaryIdentifier, String soTermName) throws SAXException {
        Item item = mrkrs.get(primaryIdentifier);
        if (item == null) {
            item = createItem(soTermName);
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            mrkrs.put(primaryIdentifier, item);
        }

        return item;
    }

    private Item getSeq(String primaryIdentifier, String seq, String variation, String offsetStart, String offsetEnd) throws SAXException {
        Item item = seqs.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Sequence");
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("identifier", primaryIdentifier);
            item.setAttribute("residues", seq);

            seqs.put(primaryIdentifier, item);

        }
        return item;
    }

}
