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

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.util.FormattedTextParser;
import org.intermine.xml.full.Item;
import org.xml.sax.SAXException;
import org.zfin.intermine.dataconversion.ZfinDirectoryConverter;


/**
 * 
 * @author Sierra
 */
public class FishConverter extends ZfinDirectoryConverter {
    //
    private static final String DATASET_TITLE = "Fish: Mutants and Morphants";
    private static final String DATA_SOURCE_NAME = "ZFIN";
    private static final Logger LOG = Logger.getLogger(FishConverter.class);
    protected String organismRefId;
    private Map<String, Item> fishes = new HashMap();
    private Map<String, Item> genotypes = new HashMap();
    private Map<String, Item> reagents = new HashMap();
    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public FishConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }

    public void process(File directory) throws Exception {
        try {
            System.out.println("canonical path: "+ directory.getCanonicalPath());
            File fishFile = new File(directory.getCanonicalPath()+"/1fish.txt");
            processFish(new FileReader(fishFile));
        } catch (IOException err) {
            throw new RuntimeException("error reading fishFile", err);
        }
	try {
	    for (Item geno: genotypes.values()){
		store(geno);
	    }

	    for (Item STR : reagents.values()){
		store(STR);
	    }
            for (Item fish : fishes.values()) {
                store(fish);
            }
	}
        catch (ObjectStoreException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            while (e != null) {
                e.printStackTrace(pw);
                //e = ((SQLException) e).getNextException();
            }
            pw.flush();
            throw new Exception(sw.toString());
        }
    }


    private void processFish(Reader fileReader) throws Exception{

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(fileReader, '|');
        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 4) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }
            String fishId = line[0];
	    String fish_name = line[1];
            String fish_handle = line[2];
            String fish_order = line[3];
            String fish_functional_affected_gene_count = line[4];
            String fish_genotype_zdb_id = line[5];
            String fish_str_zdb_id = line[6];
            //System.out.println("fish: " + primaryIdentifier);
            Item fish = null;
            if (!StringUtils.isEmpty(fishId)) {
                fish = getFish(fishId);
                if (!StringUtils.isEmpty(fish_name)) {
                    fish.setAttribute("name",fish_name);
		    if (fishId.equals("ZDB-FISH-150706-1")) {
			    System.out.println("fish found: "+ fishId + fish_name + fish_handle);
			}
                }
                if (!StringUtils.isEmpty(fish_handle)) {
                    fish.setAttribute("handle",fish_handle);
                }
                if (!StringUtils.isEmpty(fish_genotype_zdb_id)) {
                    fish.setReference("genotype",getGenotype(fish_genotype_zdb_id));
                }
		if (!StringUtils.isEmpty(fish_str_zdb_id)) {
		fish.addToCollection("STRs",getReagent(fish_str_zdb_id));
		}
            }

        }
    }

    private Item getFish(String primaryIdentifier)
            throws SAXException {
        Item item = fishes.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Fish");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
	    fishes.put(primaryIdentifier, item);
	    
        }
        return item;
    }

    private Item getReagent(String primaryIdentifier) throws SAXException{
        Item item = reagents.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Reagent");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
	    item.setReference("organism", getOrganism("7955"));
            reagents.put(primaryIdentifier, item);
        }
	return item;

    }



    private Item getGenotype(String primaryIdentifier) throws SAXException{
        Item item = genotypes.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Genotype");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            genotypes.put(primaryIdentifier, item);

        }
        return item;

    }
}
