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

/**
 * parse and load atomic_phenotype table and apato_figure table.
 * assumes ontology zdb-ids have been converted to obo ids.
 */
public class ZfinPhenotypesConverter extends BioFileConverter {

    private static final Logger LOG = Logger.getLogger(ZfinPhenotypesConverter.class);
    protected String organismRefId;
    private Map<String, Item> phenos = new HashMap();
    private Map<String, Item> figures = new HashMap();
    private Map<String, Item> terms = new HashMap();
    private Map<String, Item> genotypes = new HashMap();
    private Map<String, Item> environments = new HashMap();
    /**
     * Constructor
     *
     * @param writer the ItemWriter used to handle the resultant items
     * @param model  the Model
     */
    public ZfinPhenotypesConverter(ItemWriter writer, Model model)
            throws ObjectStoreException {
        super(writer, model, "ZFIN", "ZFIN Curated Phenotype Data Set");

        //create and store organism
        Item organism = createItem("Organism");
        organism.setAttribute("taxonId", "7955");
        store(organism);
        organismRefId = organism.getIdentifier();
    }

    /**
     * {@inheritDoc}
     */
    public void process(Reader reader) throws Exception {

        File currentFile = getCurrentFile();

        if (currentFile.getName().equals("1apato.txt")) {
            processPheno(reader);

        } /*else if (currentFile.getName().equals("2apatofig.txt")) {
            processApatoFig(reader);

        }*/ else {
            throw new IllegalArgumentException("Unexpected file: " + currentFile.getName());
        }
    }

    public void close() throws SAXException {

        try {
            for (Item pheno : phenos.values()) {
                store(pheno);
            }
        }
        catch (ObjectStoreException e) {
            throw new SAXException(e);
        }
    }

    public void processPheno(Reader reader) throws Exception, SAXException {
        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            String phenosId = line[0];
            String genoxId = line[1];
            String supertermId = line[2];
            String subtermId = line[3];
            String superterm2Id = line [4];
            String subterm2Id = line[5];
            String startStgId = line[6];
            String endStgId = line[7];
            String figId = line[8];
            String tag = line[9];
            String qualityId = line[10];
	    String genoId = line[11];
	    String expId = line[12];
	    String monogenic = line[13];

            Item apato = getPheno(phenosId);
	    
	    if (!StringUtils.isEmpty(genoId)) {

		Item geno = getGeno(genoId);
                apato.setReference("genotype", geno);
	    }

            if (!StringUtils.isEmpty(expId)) {

                Item exp = getEnv(expId);
                apato.setReference("environment", exp);
            }
            if (!StringUtils.isEmpty(supertermId)) {
                Item superTerm = getTerm(supertermId);
                apato.setReference("superTerm", superTerm);
            }
            if (!StringUtils.isEmpty(subtermId)) {
                Item subTerm = getTerm(subtermId);
                apato.setReference("subTerm", subTerm);
            }

            if (!StringUtils.isEmpty(superterm2Id)) {
                Item superTerm2 = getTerm(superterm2Id);
                apato.setReference("superTerm2", superTerm2);
            }
            if (!StringUtils.isEmpty(subterm2Id)) {
                Item subTerm2 = getTerm(subterm2Id);
                apato.setReference("subTerm2", subTerm2);
            }
            if (!StringUtils.isEmpty(figId)) {
                Item fig = getFigure(figId);
                apato.setReference("figure", fig);
            }
            if (!StringUtils.isEmpty(startStgId)) {
                Item startStg = getStage(startStgId);
                apato.setReference("startStage", startStg);
            }
            if (!StringUtils.isEmpty(endStgId)) {
                Item endStage = getStage(endStgId);
                apato.setReference("endStage", endStage);
            }
            if (!StringUtils.isEmpty(qualityId)) {
                Item quality = getQTerm(qualityId);
                apato.setReference("phenotypeTerm", quality);
            }
            if (!StringUtils.isEmpty(tag)) {
                apato.setAttribute("tag", tag);
            }
	    if (!StringUtils.isEmpty(monogenic)){
		System.out.println("found flag:" + monogenic);
		apato.setAttribute("phenotypeIsMonogenic",monogenic);
	    }
        }
    }

    private Item getQTerm(String identifier)
            throws SAXException {
        Item item = terms.get(identifier);
        if (item == null) {
            item = createItem("PATOTerm");
            item.setAttribute("identifier", identifier);
            terms.put(identifier, item);
            try {
                store(item);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }
        return item;
    }

    private Item getFigure(String primaryIdentifier)
            throws SAXException {
        Item item = figures.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Figure");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            figures.put(primaryIdentifier, item);
            try {
                store(item);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }
        return item;
    }

    private Item getStage(String identifier)
            throws SAXException {
        Item item = terms.get(identifier);
        if (item == null) {
            item = createItem("ZFATerm");
            item.setAttribute("identifier", identifier);
            terms.put(identifier, item);
            try {
                store(item);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }
        return item;
    }

    private Item getTerm(String primaryIdentifier) throws SAXException {
        Item item = terms.get(primaryIdentifier);
        if (item == null) {
            String termType = "OntologyTerm";
            String prefix = primaryIdentifier.substring(0, 3);
            if (prefix.equals("ZFA") || prefix.equals("ZFS")) {
                termType = "ZFATerm";
            } else if (prefix.equals("GO:")) {
                termType = "GOTerm";
            } else if (prefix.equals("BPS")){
                termType= "SPATIALTerm";
            }
                
            else {
                LOG.info("ontologyterm created: " + primaryIdentifier);
            }
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

    private Item getGeno(String primaryIdentifier)
            throws SAXException {
        Item genox2 = genotypes.get(primaryIdentifier);
        if (genox2 == null) {
            genox2 = createItem("Genotype");
            genox2.setAttribute("primaryIdentifier", primaryIdentifier);
            genotypes.put(primaryIdentifier, genox2);
            try {
                store(genox2);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }

        }
        return genox2;
    }

    private Item getEnv(String primaryIdentifier)
	throws SAXException {
        Item env = environments.get(primaryIdentifier);
	if (env == null) {
            env = createItem("Environment");
	    env.setAttribute("primaryIdentifier", primaryIdentifier);
            environments.put(primaryIdentifier, env);
            try {
                store(env);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }

        }
        return env;
    }


    private Item getPheno(String primaryIdentifier)
            throws SAXException {
        Item item = phenos.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Phenotype");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            phenos.put(primaryIdentifier, item);
        }
        return item;
    }


}
