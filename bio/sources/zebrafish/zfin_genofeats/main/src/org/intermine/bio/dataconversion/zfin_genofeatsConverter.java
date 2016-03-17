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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.util.FormattedTextParser;
import org.intermine.xml.full.Item;
import org.xml.sax.SAXException;

public class zfin_genofeatsConverter extends BioFileConverter {

    private static final Logger LOG = Logger.getLogger(zfin_genofeatsConverter.class);
    protected String organismRefId;
    private Map<String, Item> genofeats = new HashMap();
    private Map<String, Item> features = new HashMap();
    private Map<String, Item> genotypes = new HashMap();
    private Set<String> synonyms = new HashSet();
    private Map<String, Item> terms = new HashMap();
    private Map<String, Item> labs = new HashMap();
    private Map<String, Item> prefixes = new HashMap();
    /**
     * Constructor
     *
     * @param writer the ItemWriter used to handle the resultant items
     * @param model  the Model
     * @throws ObjectStoreException if an error occurs in storing
     */
    public zfin_genofeatsConverter(ItemWriter writer, Model model)
            throws ObjectStoreException {
        super(writer, model, "ZFIN", "Curated Alleles and Genotypes");

    }

    public void process(Reader reader) throws Exception {

        File currentFile = getCurrentFile();

        if (currentFile.getName().equals("1genofeats.txt")) {
            processGenoFeats(reader);
        } else {
            throw new IllegalArgumentException("Unexpected file: " + currentFile.getName());
        }

	try {
            for (Item geno: genotypes.values()){
		store(geno);
            }
            for (Item feature : terms.values()) {
                store(feature);
            }
            
        } catch (ObjectStoreException e) {
            throw new SAXException(e);
        }

    }

    public void processGenoFeats(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();

            String primaryIdentifier = line[0];
            String genoId = line[1];
            String featureId = line[2];
            String featureZygosity = line[3];
            String type = line[4];
            String name=line[5];
            String abbrev =line[6];
	    String labOfOriginId = line[7];
	    String mutagen = line[8];
	    String mutagee = line[9];
	    String featurePrefix = line[10];
	    // System.out.println(type);
            Item geno = getGenotype(genoId);
            Item feature = getTypedItem(featureId, type);

            if (!StringUtils.isEmpty(featureId)) {
                geno.addToCollection("features", feature);
		feature.addToCollection("genotypes",geno);                
		//if (!StringUtils.isEmpty(labOfOriginId)){
		//  feature.setReference("labOfOrigin", getSource(labOfOriginId));
		//	}
	    }
            if (!StringUtils.isEmpty(featureZygosity)) {
                feature.setAttribute("featureZygosity", featureZygosity);
            }
            if (!StringUtils.isEmpty(featureId)) {
                feature.setAttribute("featureId", featureId);
            }
            if  (!StringUtils.isEmpty(name)) {
                feature.setAttribute("name", name);
            }

            if  (!StringUtils.isEmpty(abbrev)) {
                feature.setAttribute("symbol", abbrev);
            }
	    if  (!StringUtils.isEmpty(mutagen)) {
                feature.setAttribute("mutagen", mutagen);
            }
	    if  (!StringUtils.isEmpty(mutagee)) {
                feature.setAttribute("mutagee", mutagee);
            }
	    if (!StringUtils.isEmpty(featurePrefix)){
		Item prefix = getPrefix(featurePrefix);
		feature.setReference("featurePrefix", prefix);
	    }

        }
    }

    private Item getPrefix(String primaryIdentifier) throws SAXException {
	Item item = prefixes.get(primaryIdentifier);
        if (item == null) {
            item = createItem("FeaturePrefix");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            prefixes.put(primaryIdentifier, item);
	    try {
                store(item);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
	}
        return item;
	
    }
    private Item getTypedItem(String primaryIdentifier, String type) throws SAXException {
        Item typedItem = null;

        if (type.equals("INSERTION")) {
            typedItem = getFeature(primaryIdentifier, "Insertion");
        } else if (type.equals("POINT_MUTATION")) {
            typedItem = getFeature(primaryIdentifier, "PointMutation");
        } else if (type.equals("DELETION")) {
            typedItem = getFeature(primaryIdentifier, "Deletion");
        } else if (type.equals("DEFICIENCY")) {
            typedItem = getFeature(primaryIdentifier, "ChromosomalDeletion");
        } else if (type.equals("TRANSLOC")) {
            typedItem = getFeature(primaryIdentifier, "Translocation");
        } else if (type.equals("INVERSION")) {
            typedItem = getFeature(primaryIdentifier, "Inversion");
        } else if (type.equals("TRANSGENIC_INSERTION")) {
            typedItem = getFeature(primaryIdentifier, "TransgenicInsertion");
        } else if (type.equals("SEQUENCE_VARIANT")) {
            typedItem = getFeature(primaryIdentifier, "SequenceAlteration");
        } else if (type.equals("UNSPECIFIED")) {
            typedItem = getFeature(primaryIdentifier, "SequenceAlteration");
        } else if (type.equals("COMPLEX_SUBSTITUTION")) {
            typedItem = getFeature(primaryIdentifier, "ComplexSubstitution");
        } else if (type.equals("TRANSGENIC_UNSPECIFIED")) {
            typedItem = getFeature(primaryIdentifier, "TransgenicInsertion");
        }
	else if (type.equals("INDEL")){
	    typedItem = getFeature(primaryIdentifier,"Indel");
	}

        return typedItem;
    }

    private Item getFeature(String primaryIdentifier, String soTermName)
        throws SAXException {
        Item item = terms.get(primaryIdentifier);
        if (item == null) {
            item = createItem(soTermName);
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            terms.put(primaryIdentifier, item);

        }
        return item;
    }

    private Item getGenotype(String primaryIdentifier)
            throws SAXException {
        Item item = genotypes.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Genotype");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            genotypes.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getSource(String primaryIdentifier)
	throws SAXException {
        Item item = labs.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Lab");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            labs.put(primaryIdentifier, item);
	    try {
                store(item);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
	}
        return item;
    }


}
