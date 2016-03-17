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

import org.apache.log4j.Logger;
import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.util.FormattedTextParser;
import org.xml.sax.SAXException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * 
 * @author
 */
public class ZebrafishChromosomeConverter extends BioFileConverter
{
    //
    private static final String DATASET_TITLE = "Chromosomes";
    private static final String DATA_SOURCE_NAME = "ZFIN";
    private static final Logger LOG = Logger.getLogger(ZebrafishChromosomeConverter.class);
    protected String organismRefId;
    private Map<String, Item> genes = new HashMap();
    private Map<String, Item> chromos = new HashMap();
    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public ZebrafishChromosomeConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }

    /**
     * 
     *
     * {@inheritDoc}
     */
    public void process(Reader reader) throws Exception {

        processChromos(reader);

        try {
            for (Item gene : genes.values()) {
                store(gene);
            }
        }
        catch (ObjectStoreException e) {
            throw new Exception(e);

        }
    }

    public void processChromos(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 2) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }

            String lg = line[0];
            String genePrimaryIdentifier = line[1];

            Item gene = getGene(genePrimaryIdentifier);
            Item chromo = getChromo(lg);
            gene.addToCollection("chromosomes",chromo);

        }
    }

    private Item getGene(String primaryIdentifier)
	throws SAXException {
        Item item = genes.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Gene");
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            genes.put(primaryIdentifier, item);
        }
        return item;
    }
    private Item getChromo(String chromoNumber)
	throws SAXException {
        Item item = chromos.get(chromoNumber);
        if (item == null) {
            item = createItem("Chromosome");
	    item.setAttribute("name", chromoNumber);
            item.setReference("organism", getOrganism("7955"));
            chromos.put(chromoNumber, item);
            try {
                store(item);
            }
            catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }
        return item;
    }

}
