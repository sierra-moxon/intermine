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
    private Map<String, Item> figs = new HashMap();
    private Map<String, Item> genes = new HashMap();
    private Map<String, Item> terms = new HashMap();
    private Map<String, Item> constructs = new HashMap();
    private Map<String, Item> morphs = new HashMap();
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
            File gfrvFile = new File(directory.getCanonicalPath()+"/2geneFeatureResultView.txt");
            processGfrv(new FileReader(gfrvFile));
        } catch (IOException err) {
            throw new RuntimeException("error reading gfrvFile", err);
        }
        try {
            File figAnatFile = new File(directory.getCanonicalPath()+"/3figureAnat.txt");
            processFigAnat(new FileReader(figAnatFile));
        } catch (IOException err) {
            throw new RuntimeException("error reading figAnatFile", err);
        }
        try {

            for (Item fig : figs.values()) {
                store(fig);
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

    public void processFigAnat(Reader reader) throws Exception {
        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');
        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 5) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }
            String primaryIdentifier = line[0];
            String phenosId = line[4];
            //System.out.println("figAnat: " + primaryIdentifier + " " + phenosId);
            Item fish = null;
            if (!StringUtils.isEmpty(primaryIdentifier)) {
                fish = getFish(primaryIdentifier);
                if (!StringUtils.isEmpty(phenosId)){
                    fish.addToCollection("figures", getFigure(phenosId));
                }
            }

        }
    }

    private void processGfrv(Reader fileReader) throws Exception{
        Iterator lineIter = FormattedTextParser.parseDelimitedReader(fileReader, '|');
        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 9) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }
            String primaryIdentifier = line[0];
            String geneZdbId = line[4];
            String affectorZdbId = line[6];
            String constructZdbId = line[8];
            String featureType = line[9];
            //System.out.println("Gfrv: " + primaryIdentifier);
            Item fish = null;
            if (!StringUtils.isEmpty(primaryIdentifier)) {
                fish = getFish(primaryIdentifier);
                if(!StringUtils.isEmpty(geneZdbId)){
                    fish.addToCollection("genes",getGene(geneZdbId));
                }
                if(!StringUtils.isEmpty(affectorZdbId)){
                    fish.addToCollection("affectors", getTypedItem(affectorZdbId, featureType));
                    if(!StringUtils.isEmpty(constructZdbId)){
			//System.out.println("constructID: " + constructZdbId);
                        fish.addToCollection("constructs", getConstruct(constructZdbId));
                    }
                }
            }
        }
    }


    private void processFish(Reader fileReader) throws Exception{

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(fileReader, '|');
        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 4) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }
            String primaryIdentifier = line[3];
	    String fishId = line[4];
            String genoLongName = line[2];
            String genoName = line[2];
            //System.out.println("fish: " + primaryIdentifier);
            Item fish = null;
            if (!StringUtils.isEmpty(primaryIdentifier)) {
                fish = getFish(primaryIdentifier);
                if (!StringUtils.isEmpty(genoName)) {
                    fish.setAttribute("name",genoName);
                }
		if(!StringUtils.isEmpty(fishId)){
		    fish.setAttribute("fishId",fishId);
		}
                if (!StringUtils.isEmpty(genoLongName)) {
                    fish.setAttribute("longName",genoLongName);
                }
            }

        }
    }

    
    private Item getFigure(String phenosId) throws SAXException{
        Item item = figs.get(phenosId);
        if (item == null) {
            item = createItem("Figure");
            item.setAttribute("primaryIdentifier", phenosId);
            figs.put(phenosId, item);
        }
        return item;

    }

    private Item getGene(String geneZdbId) throws SAXException{
        Item item = genes.get(geneZdbId);
        if (item == null) {
            item = createItem("Gene");
            item.setAttribute("primaryIdentifier", geneZdbId);
            item.setReference("organism", getOrganism("7955"));
	    genes.put(geneZdbId, item);
            try {
                store(item);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }

        return item;
    }
    private Item getConstruct(String constructZdbId) throws SAXException{
        Item item = constructs.get(constructZdbId);
        if (item == null) {
            item = createItem("Construct");
            item.setAttribute("primaryIdentifier", constructZdbId);
	    item.setReference("organism", getOrganism("7955"));
            constructs.put(constructZdbId, item);
            try {
                store(item);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }

        return item;

    }

    private Item getFish(String primaryIdentifier)
            throws SAXException {
        Item item = fishes.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Fish");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            fishes.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getTypedItem(String primaryIdentifier, String type) throws SAXException {
        Item typedItem = null;

	System.out.println("primaryIdentifier " + primaryIdentifier +type); 
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
        } else if (type.equals("str")) {
	    if (StringUtils.substring(primaryIdentifier,0,9).equals("ZDB-TALEN")){
		    typedItem = getReagent(primaryIdentifier);
		    //System.out.println("talen found" + primaryIdentifier);
	    }
	    else if (StringUtils.substring(primaryIdentifier,0,10).equals("ZDB-CRISPR")){
		typedItem = getReagent(primaryIdentifier);
		//System.out.println("crispr found" + primaryIdentifier);
	    }
	    else if (StringUtils.substring(primaryIdentifier,0,11).equals("ZDB-MRPHLNO")){
		System.out.println ("got a morpholino" + primaryIdentifier);    
		typedItem = getMorpholino(primaryIdentifier);
		    // System.out.println("morpholino found" + primaryIdentifier);
	    }
	}
        return typedItem;
    }

    private Item getReagent(String primaryIdentifier) throws SAXException{
        Item item = reagents.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Reagent");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
	    item.setReference("organism", getOrganism("7955"));
            reagents.put(primaryIdentifier, item);
            try {
                store(item);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }
	return item;

    }

    private Item getMorpholino(String primaryIdentifier) throws SAXException{
        Item item = morphs.get(primaryIdentifier);
        if (item == null) {
	    System.out.println("morph: " + primaryIdentifier);
            item = createItem("MorpholinoOligo");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
	    item.setReference("organism", getOrganism("7955"));
            morphs.put(primaryIdentifier, item);
	    System.out.println("morph found: " +primaryIdentifier);
            try {
                store(item);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }

        return item;

    }

    private Item getFeature(String primaryIdentifier, String soTermName) throws SAXException{
        Item item = terms.get(primaryIdentifier);
        if (item == null) {
            item = createItem(soTermName);
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
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
