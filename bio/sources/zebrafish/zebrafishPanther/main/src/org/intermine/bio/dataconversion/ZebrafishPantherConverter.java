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
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
public class ZebrafishPantherConverter extends BioFileConverter
{
    //
    private static final String DATASET_TITLE = "Panther Orthologs For Zebrafish Genes";
    private static final String DATA_SOURCE_NAME = "Panther";
    private Map<String, Item> linkDbs = new HashMap();
    private Map<String, Item> links = new HashMap();
    private Map<String, Item> externalGenes = new HashMap();
    private Map<String, Item> genes = new HashMap();
    private Map<String, Item> proteins = new HashMap();
    private Map<String, Item> orthos = new HashMap();
    private Map<String, Item> codes = new HashMap();
    private Map<String, Item> orthoEvs = new HashMap();
    private static final Logger LOG = Logger.getLogger(ZebrafishPantherConverter.class);
    //protected String organismRefId;
    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public ZebrafishPantherConverter(ItemWriter writer, Model model)
        throws ObjectStoreException{
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
        Item organism = createItem("Organism");
	//        organism.setAttribute("taxonId", "7955");
        //store(organism);
        //organismRefId = organism.getIdentifier();
    }

    public void process(Reader reader) throws Exception {

        processOrthos(reader);

	try {
            for (Item gene : genes.values()) {
                store(gene);
            }
        }
        catch (ObjectStoreException e) {
            throw new SAXException(e);
        }


        try {
            for (Item orth : orthos.values()) {
                store(orth);
            }
        }
        catch (ObjectStoreException e) {
            throw new SAXException(e);
        }

    }

    private void processOrthos(Reader reader) throws Exception {
        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 9) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0] + line[1] +line[2] +line[3] +line[4]);
            }

            String organism1 = line[0];
            String geneid1 = line[1];
            String uniprotId1 = line[2];
            String organism2 = line[3];
            String geneid2 = line[4];
            String uniprotId2 = line[5];
            String orthoType = line[6];
            String pantherId = line[8];
            Item ortho = null;

            if (StringUtils.equals(organism1,"DANRE")){
		String id = geneid2+geneid1;
                Item gene = getGene(geneid1,organism1);
                ortho = getOrtho(id,"IEA","PantherHomologue");
                Item externalGene = getExternalGene(geneid2, geneid2, organism2, geneid2);
                ortho.setReference("homologue", externalGene);
                ortho.setReference("gene",gene);
                Item orthoProtein = getProtein(uniprotId2);
                Item zebrafishProtein = getProtein(uniprotId1);
                Item crossReference = getExternalLink(pantherId,pantherId,"Other","Panther");
                ortho.setReference("protein",orthoProtein);
                ortho.addToCollection("crossReferences",crossReference);
                gene.addToCollection("proteins",zebrafishProtein);
                externalGene.addToCollection("proteins",orthoProtein);
               
                if (StringUtils.equals(orthoType,"LDO")) {
		    orthoType = "Least Diverged Orthologue";
                }
               else if (StringUtils.equals(orthoType,"O")) {
		   orthoType = "Orthologue";
                }
               else { 
                     System.out.println("warning: new ortho type");
                }
		ortho.setAttribute("type",orthoType);
                String orthoId = ortho.getIdentifier();
		if (geneid1.equalsIgnoreCase("ZDB-GENE-040426-1729")) {
                    System.out.println ("here is the record we're looking for: ZDB-GENE-040426-1729 ortho identifier"+orthoId);
                }

            }
            else if (StringUtils.equals(organism2,"DANRE")) {
                String id =geneid1+geneid2;
		Item gene = getGene(geneid2,organism2);
                ortho = getOrtho(id,"IEA","PantherHomologue");
                Item externalGene = getExternalGene(geneid1, geneid1, organism1, geneid1);
                ortho.setReference("homologue", externalGene);
                ortho.setReference("gene",gene);
                Item orthoProtein = getProtein(uniprotId2);
                Item zebrafishProtein = getProtein(uniprotId2);
                Item crossReference = getExternalLink(pantherId,pantherId,"Other","Panther");
                ortho.setReference("protein",orthoProtein);
                ortho.addToCollection("crossReferences",crossReference);
                gene.addToCollection("proteins",zebrafishProtein);
                externalGene.addToCollection("proteins",orthoProtein);
                ortho.setAttribute("type",orthoType);
            }
            else {
                throw new RuntimeException("Line does not have a DANRE member"+ line.length + line[0] + line[1] +line[2] +line[3] +line[4]);

            }

         
        }
    }
    private Item getProtein(String primaryIdentifier)
            throws SAXException {
        Item item = proteins.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Protein");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            proteins.put(primaryIdentifier, item);
            try {
                store(item);
            }
            catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }
        return item;
    }
    private Item getGene(String primaryIdentifier, String species)
            throws SAXException {
            Item item = genes.get(primaryIdentifier);
            if (item == null) {
                item = createItem("Gene");
		item.setReference("organism", getOrganism(getTaxon(species)));
                item.setAttribute("primaryIdentifier", primaryIdentifier);
                genes.put(primaryIdentifier, item);
                /*try {
		    if (primaryIdentifier.equalsIgnoreCase("ZDB-GENE-040426-1729")){
			    System.out.println("here is the store of the record we're looking for:ZDB-GENE-040426-1729");
			}
                    store(item);
                }
                catch (ObjectStoreException e) {
                    throw new SAXException(e);
		    }*/
            }
            return item;
    }

    private Item getOrtho(String orthoPrimaryIdentifier, String codeAbbrev, String orthoPub)
            throws SAXException {
        Item item = orthos.get(orthoPrimaryIdentifier);
        if (item == null) {
            item = createItem("Homologue");
            item.setAttribute("primaryIdentifier", orthoPrimaryIdentifier);
            orthos.put(orthoPrimaryIdentifier, item);
            Item orthoEv = getOrthoEv(orthoPrimaryIdentifier, codeAbbrev, orthoPub);
            item.addToCollection("evidence", orthoEv);
        }
        return item;
    }

    private Item getOrthoEv(String primaryIdentifier, String codeAbbrev, String orthoPub)
            throws SAXException {
        String orthoEvPK = primaryIdentifier.concat(codeAbbrev);
        Item item = orthoEvs.get(orthoEvPK);
        if (item == null) {
            item = createItem("OrthologueEvidence");
            //item.addToCollection("publications", getPub(orthoPub));
            orthoEvs.put(orthoEvPK, item);
            Item code = getCode(codeAbbrev);
            item.setReference("evidenceCode", code);
            try {
                store(item);
            }
            catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }
        return item;
    }

    private Item getCode(String abbrev)
            throws SAXException {
        Item item = codes.get(abbrev);
        if (item == null) {
            item = createItem("OrthologueEvidenceCode");
            item.setAttribute("abbreviation", abbrev);
            codes.put(abbrev, item);
            try {
                store(item);
            }
            catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }
        return item;
    }
    private Item getExternalLink(String primaryIdentifier, String accessionNumber, String orthoFdbType, String orthoForeignDBName)
            throws SAXException {
        Item item = links.get(primaryIdentifier);
        if (item == null) {
            item = createItem("CrossReference");
            item.setAttribute("identifier", accessionNumber);
            item.setAttribute("linkType", orthoFdbType);
            //item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("source", getLinkDb(orthoForeignDBName));
            links.put(primaryIdentifier, item);
            try {
                store(item);
            }
            catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }
        return item;
    }

    private Item getLinkDb(String name)
            throws SAXException {
        Item item = linkDbs.get(name);
        if (item == null) {
            item = createItem("DataSource");
            item.setAttribute("name", name);
            linkDbs.put(name, item);
            try {
                store(item);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }
        return item;
    }

    private Item getExternalGene(String orthoAbbrev, String orthoName, String orthoSpecies, String orthoPrimaryIdentifier)
            throws SAXException {
        Item item = externalGenes.get(orthoName);
        if (item == null) {
            item = createItem("Gene");
            if (!StringUtils.isEmpty(orthoName)) {
                item.setAttribute("name", orthoName);
            } else  if (!StringUtils.isEmpty(orthoAbbrev)){
		item.setAttribute("name", orthoAbbrev);
	    }
	    else {
		System.out.println(orthoAbbrev);
	    }
            item.setAttribute("symbol", orthoAbbrev);
            item.setAttribute("primaryIdentifier", orthoPrimaryIdentifier);
            item.setReference("organism", getOrganism(getTaxon(orthoSpecies)));
            externalGenes.put(orthoName, item);
            try {
                store(item);
            }
            catch (ObjectStoreException e) {
                throw new SAXException(e);
            }

        }
        return item;
    }
    private String getTaxon(String species)
            throws SAXException {
        String taxon = "";
        if (species.equals("DANRE")) {
            taxon = "7955";
        } else if (species.equals("HUMAN")) {
            taxon = "9606";
        } else if (species.equals("MOUSE")) {
            taxon = "10090";
        } else if (species.equals("DROME")) {
            taxon = "7227";
        } else if (species.equals("Yeast")) {
            taxon = "4932";
        } else {
            System.out.println(species + "species is not in group");
        }

        return taxon;
    }


}
