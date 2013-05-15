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

import org.intermine.metadata.Model;
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
public class OmimPhenotypeConverter extends BioFileConverter
{
    //
    private Map<String, Item> genes = new HashMap();
    private Map<String, Item> linkDbs = new HashMap();
    private Map<String, Item> omimphenos = new HashMap();
    private Map<String, Item> links = new HashMap();

    private static final String DATASET_TITLE = "OMIM Phenotype";
    private static final String DATA_SOURCE_NAME = "ZFIN";

    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public OmimPhenotypeConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }

    /**
     * 
     *
     * {@inheritDoc}
     */
    public void process(Reader reader) throws Exception {

	processPhenos(reader);

	try {

	    for (Item link : links.values()){
                store(link);
            }
	    for (Item omimpheno : omimphenos.values()){
		store(omimpheno);
	    }
	    for (Item gene : genes.values()){
		store(gene);
	    }

        } catch (ObjectStoreException e) {
            throw new SAXException(e);
        }

    }
    
    public void processPhenos(Reader reader) throws Exception {
	Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();

            if (line.length < 3) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }

            String geneId = line[0];
            String name = line[1];
	    String omimId = line[2];
	    
	    Item linkDb = getLinkDb("OmimPhenotype","http://zfin.org","phenotype for human disease.");
     
	    Item gene = null;
	    if (!StringUtils.isEmpty(geneId)) {
                gene = getGene(geneId);
            }
	    if (!StringUtils.isEmpty(name)){
		Item omimPheno = getOmimPheno(name);
		if (!StringUtils.isEmpty(omimId)){
		    Item crossRef = getLink(omimId,gene);
		    crossRef.setReference("source",linkDb);
		    omimPheno.setReference("phenotypeLink",crossRef);
		}
		gene.addToCollection("omimPhenotypes",omimPheno);
	    }
	    
        }
    }

    private Item getOmimPheno(String name)
	throws SAXException {
        Item item = omimphenos.get(name);
        if (item == null) {
            item = createItem("OmimPhenotype");
	    item.setAttribute("disease",name);
            omimphenos.put(name, item);
        }
        return item;
    }

    private Item getGene(String primaryIdentifier)
	throws SAXException {
        Item item = genes.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Gene");
	    item.setAttribute("primaryIdentifier",primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            genes.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getLink(String accession, Item typedItem)
            throws SAXException {
        Item item = links.get(accession);
        if (item == null) {
            item = createItem("CrossReference");
            item.setReference("subject",typedItem);
            item.setAttribute("identifier", accession);
            links.put(accession, item);
        }
        return item;
    }

  private Item getLinkDb(String name,String url, String description)
            throws SAXException {
        Item item = linkDbs.get(name);
        if (item == null) {
            item = createItem("DataSource");
	    item.setAttribute("name",name);
	    item.setAttribute("url",url);
	    item.setAttribute("description",description);
            linkDbs.put(name, item);
            try {
                store(item);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }
        return item;
  }




}

