
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
 * DataConverter to load ZFIN marker
 * identifiers from text files
 */
public class zfin_markersConverter extends BioDirectoryConverter {

    private static final Logger LOG = Logger.getLogger(zfin_markersConverter.class);

    private Map<String, Item> links = new HashMap();

    private Map<String, Item> reagents = new HashMap();
    private Map<String, Item> regions = new HashMap();
    private Map<String, Item> snps = new HashMap();
    private Map<String, Item> genes = new HashMap();
    private Map<String, Item> pubs = new HashMap();
    private Map<String, Item> antibodies = new HashMap();
    private Map<String, Item> constructs = new HashMap();
    private Map<String, Item> clones = new HashMap();
    private Map<String, Item> DNAclones = new HashMap();
    private Map<String, Item> RNAclones = new HashMap();
    private Map<String, Item> mrphs = new HashMap();

    private Map<String, Item> sslps = new HashMap();
    private Map<String, Item> rapds = new HashMap();
    private Map<String, Item> stss = new HashMap();
    private Map<String, Item> geneps = new HashMap();
    private Map<String, Item> tscripts = new HashMap();
    private Map<String, Item> efgs = new HashMap();
    private Map<String, Item> probelibs = new HashMap();
    private Map<String, Item> genotypes = new HashMap();
    private Map<String, Item> linkDbs = new HashMap();
    private Map<String, Item> proteins = new HashMap();

    private Map<String, Item> vgeneTscripts =new HashMap();
    private Map<String, Item> antisenseTscripts =new HashMap();
    private Map<String, Item> abberantTscripts    =new HashMap();
    private Map<String, Item> pseudogenicTscripts = new HashMap();
    private Map<String, Item> lincrnaTscripts  = new HashMap();
    private Map<String, Item> disrupteddomainTscripts =new HashMap();
    private Map<String, Item> telementTscripts = new HashMap();
    private Map<String, Item> trnaTscripts   =new HashMap();
    private Map<String, Item> snornaTscripts  =new HashMap();
    private Map<String, Item> snrnaTscripts =new HashMap();
    private Map<String, Item> rnaTscripts   =new HashMap();
    private Map<String, Item> mirnaTscript   =new HashMap();
    private Map<String, Item> pirnaTscripts =new HashMap();
    private Map<String, Item> mrnaTscripts     = new HashMap();
    private Map<String, Item> ncrnaTscripts= new HashMap();
    private Map<String, Item> polyTscripts = new HashMap();

    private Set<String> synonyms = new HashSet();
    /**
     * Constructor
     *
     * @param writer the ItemWriter used to handle the resultant items
     * @param model  the Model
     * @throws ObjectStoreException if an error occurs in storing
     */
    private static final String DATASET_TITLE = "Genes, Markers, and Clones";
    private static final String DATA_SOURCE_NAME = "ZFIN";


    public zfin_markersConverter(ItemWriter writer, Model model){
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);

    }

    public void process(File directory) throws Exception {
        try {
            System.out.println("canonical path: "+ directory.getCanonicalPath());
            File markersFile = new File(directory.getCanonicalPath()+"/1markers.txt");
            processMarkers(new FileReader(markersFile));
        } catch (IOException err) {
            throw new RuntimeException("error reading markersFile", err);
        }
        try {
            File tscriptFile = new File(directory.getCanonicalPath()+"/8transcript.txt");
            processTranscripts(new FileReader(tscriptFile));
        } catch (IOException err) {
            throw new RuntimeException("error reading tscriptFile", err);
        }

        try {
            File mrelsFile = new File(directory.getCanonicalPath()+"/2mrels.txt");
            processMrels(new FileReader(mrelsFile));
        } catch (IOException err) {
            throw new RuntimeException("error reading mrelsFile", err);
        }

        try {
            File aliasFile = new File(directory.getCanonicalPath()+"/3dalias.txt");
            processAliases(new FileReader(aliasFile));
        } catch (IOException err) {
            throw new RuntimeException("error reading aliasFile", err);
        }
        try {
            File replacedDataFile = new File(directory.getCanonicalPath()+"/4replaceddata.txt");
            processMarkerReplacedData(new FileReader(replacedDataFile));
        } catch (IOException err) {
            throw new RuntimeException("error reading replacedDataFile", err);
        }
        try {
            File linkDataFile = new File(directory.getCanonicalPath()+"/5dblinks.txt");
            processLinkData(new FileReader(linkDataFile));
        } catch (IOException err) {
            throw new RuntimeException("error reading linkDataFile", err);
        }

        try {
            File attributeFile = new File(directory.getCanonicalPath()+"/6recattrib.txt");
            processAttribution(new FileReader(attributeFile));
        } catch (IOException err) {
            throw new RuntimeException("error reading attributeFile", err);
        }
        try {
            File cloneFile = new File(directory.getCanonicalPath()+"/9clone.txt");
            processClones(new FileReader(cloneFile));
        } catch (IOException err) {
            throw new RuntimeException("error reading cloneFile", err);
        }
        try {
            File atbFile = new File(directory.getCanonicalPath()+"/7antibody.txt");
            processAntibodies(new FileReader(atbFile));
        } catch (IOException err) {
            throw new RuntimeException("error reading atbFile", err);
        }

        try {

            for (Item probelib : probelibs.values()) {
                store(probelib);
            }
            for (Item gene : genes.values()) {
                store(gene);
            }
            for (Item genep : geneps.values()) {
                store(genep);
            }
            for (Item protein : proteins.values()) {
                store(protein);
            }
            for (Item clone : clones.values()) {
                store(clone);
            }
            for (Item DNAclone : DNAclones.values()) {
                store(DNAclone);
            }
            for (Item RNAclone : RNAclones.values()) {
                store(RNAclone);
            }
            for (Item transcript : tscripts.values()) {
                store(transcript);
            }
            for (Item region : regions.values()) {
                store(region);
            }
            for (Item snp : snps.values()) {
                store(snp);
            }
            for (Item antibody : antibodies.values()) {
                store(antibody);
            }
            for (Item construct : constructs.values()) {
                store(construct);
            }
            for (Item mrph : mrphs.values()) {
                store(mrph);
            }
	    for (Item reagent : reagents.values()){
                store(reagent);
            }

            for (Item sslp : sslps.values()) {
                store(sslp);
            }
            for (Item rapd : rapds.values()) {
                store(rapd);
            }
            for (Item sts : stss.values()) {
                store(sts);
            }
            for (Item efg : efgs.values()) {
                store(efg);
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

    private void processLinkData(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();

            if (line.length < 3) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }

            String linkPrimaryIdentifier = line[0];
            String linkDb = line[1];
            String accNum = line[2];
            String dataPrimaryIdentifier = line[3];
            String linkType = line[4];
            String url = line[5];
            String description = line[6];

            if (!StringUtils.isEmpty(linkPrimaryIdentifier)) {
                Item externalLink = getLink(accNum,getTypedItem(dataPrimaryIdentifier),linkPrimaryIdentifier);

                if (!StringUtils.isEmpty(linkType)) {
                    externalLink.setAttribute("linkType", linkType);
                }
                if (!StringUtils.isEmpty(linkDb)) {
                    externalLink.setReference("source", getLinkDb(linkDb,url,description));

                }
                /*if (!StringUtils.isEmpty(accNum)) {
		    
		    //externalLink.setAttribute("accessionNumber", accNum);
                    if (StringUtils.substring(accNum,0,7).equals("OTTDART")){
                        Item transcript = getTscript(dataPrimaryIdentifier);
                        transcript.setAttribute("VegaId",accNum);
                    }
                    if (StringUtils.substring(accNum,0,7).equals("ENSDART")){
                        Item transcript = getTscript(dataPrimaryIdentifier);
                        transcript.setAttribute("secondaryIdentifier",accNum);
                    }
                    if (StringUtils.substring(accNum,0,7).equals("OTTDARG")){
                         if (dataPrimaryIdentifier.substring(0,12).equals("ZDB-TSCRIPT-")){
                            Item transcript = getTscript(dataPrimaryIdentifier);
                            transcript.setAttribute("VegaId",accNum);
                        }
                        else {
                            Item gene = getGene(dataPrimaryIdentifier);
                            gene.setAttribute("VegaId",accNum);
                        }
                    }
                    if (StringUtils.substring(accNum,0,7).equals("ENSDARG")){
                        if (dataPrimaryIdentifier.substring(0, 10).equals("ZDB-GENEP-")) {
                            Item genep = getGeneP(dataPrimaryIdentifier);
                            genep.setAttribute("secondaryIdentifier",accNum);
                        }
                        else {
                            Item gene = getGene(dataPrimaryIdentifier);
                            gene.setAttribute("secondaryIdentifier",accNum);
                        }
                    }
		    }*/

                try {
                    store(externalLink);
                } catch (ObjectStoreException e) {
                    throw new SAXException(e);
                }

                if (!StringUtils.isEmpty(dataPrimaryIdentifier)) {
                    Item referencedLinkItem2 = getTypedItem(dataPrimaryIdentifier);
                    referencedLinkItem2.addToCollection("crossReferences", externalLink);
                    if (linkDb.equals("UniProtKB")){
                        if (!StringUtils.isEmpty(accNum)){
                            Item protein = getProtein(accNum,dataPrimaryIdentifier);
                            referencedLinkItem2.addToCollection("proteins",protein);
                        }
                    }
                }
            }

        }

    }

    private void processAntibodies(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();

            if (line.length < 5) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }

            String atbPrimaryIdentifier = line[0];
            String antibodyType = line[1];
            String atbheavyChainIsotype = line[2];
            String lightChainIsotype = line[3];
            String immunogenOrganism = line[4];
            String hostOrganism = line[5];

            if (!StringUtils.isEmpty(atbPrimaryIdentifier)) {
                Item atb = getTypedItem(atbPrimaryIdentifier);

                if (!StringUtils.isEmpty(antibodyType)) {
                    atb.setAttribute("type", antibodyType);
                }
                if (!StringUtils.isEmpty(atbheavyChainIsotype)) {
                    atb.setAttribute("heavyChainIsotype", atbheavyChainIsotype);
                }
                if (!StringUtils.isEmpty(lightChainIsotype)) {
                    atb.setAttribute("lightChainIsotype", lightChainIsotype);
                }
                if (!StringUtils.isEmpty(immunogenOrganism)) {
                    atb.setAttribute("immunogenOrganism", immunogenOrganism);
                }
                if (!StringUtils.isEmpty(hostOrganism)) {
                    atb.setAttribute("hostOrganism", hostOrganism);
                }

            }

        }
    }
    private void processTranscripts(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();

            if (line.length < 4) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }

            String tscriptPrimaryIdentifier = line[0];
            String type = line[1];
            String typeDefinition = line[2];
            String typeStatusDefinition = line[3];
            String status = line[4];


            if (!StringUtils.isEmpty(tscriptPrimaryIdentifier)) {
                Item tscript = getTscript(tscriptPrimaryIdentifier);

                if (!StringUtils.isEmpty(type)) {
                    tscript.setAttribute("type", type);
                }
                if (!StringUtils.isEmpty(typeDefinition)) {
                    tscript.setAttribute("typeDefinition",typeDefinition );
                }
                if (!StringUtils.isEmpty(typeStatusDefinition)) {
                    tscript.setAttribute("typeStatusDefinition", typeStatusDefinition);
                }
                if (!StringUtils.isEmpty(status)) {
                    tscript.setAttribute("status",status );
                }

            }

        }
    }


    private void processClones(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();

            if (line.length < 24) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }

            String clonePrimaryIdentifier = line[0];
            String vector = line[2];
            String polymerase = line[3];
            String insertSize = line[4];
            String cloneSite = line[5];
            String digest = line[6];
            String probelibId = line[7];
            String sequenceType = line[8];
            String PCR = line[9];
            String thisseCloneRating = line[10];
            String problemType = line[11];
            String name = line[13];
            String species = line[15];
            String strain = line[17];
            String tissue = line[20];
            String stage = line[22];
            String host = line[23];
            String restrictionSites = line[24];

            if (!StringUtils.isEmpty(clonePrimaryIdentifier)) {
                Item cloneItem = getTypedItem(clonePrimaryIdentifier);
                //System.out.println("item type is: "+cloneItem.getAttribute("type").getValue());


                if (!StringUtils.isEmpty(vector)) {
                    cloneItem.setAttribute("vector", vector);
                }
                if (!StringUtils.isEmpty(polymerase)) {
                    cloneItem.setAttribute("polymerase", polymerase);
                }
                if (!StringUtils.isEmpty(insertSize)) {
                    cloneItem.setAttribute("insertSize", insertSize);
                }
                if (!StringUtils.isEmpty(digest)) {
                    cloneItem.setAttribute("digest", digest);
                }
                if (!StringUtils.isEmpty(sequenceType)) {
                    cloneItem.setAttribute("sequenceType", sequenceType);
                }
                if (!StringUtils.isEmpty(cloneSite)) {
                    cloneItem.setAttribute("cloneSite", cloneSite);
                }
                if (!StringUtils.isEmpty(PCR)) {
                    cloneItem.setAttribute("PCR", PCR);
                }
                if (!StringUtils.isEmpty(thisseCloneRating)) {
                    cloneItem.setAttribute("ThisseCloneRating", thisseCloneRating);
                }
                if (!StringUtils.isEmpty(problemType)) {
                    cloneItem.setAttribute("problemType", problemType);
                }
                if (!StringUtils.isEmpty(probelibId)) {


                    Item probeLibItem = getProbeLibrary(probelibId);

                    // System.out.println("probelibId is: " + probeLibItem.getAttribute("primaryIdentifier").getValue());
                    // System.out.println("cloneid is: " + cloneItem.getAttribute("primaryIdentifier").getValue());

                    if (!StringUtils.isEmpty(name)) {
                        probeLibItem.setAttribute("name", name);
                    }
                    if (!StringUtils.isEmpty(species)) {
                        probeLibItem.setAttribute("species", species);
                    }
		    // if (!StringUtils.isEmpty(strain)) {
                    //    probeLibItem.setReference("strain", getGenotype(strain));
		    // }
                    if (!StringUtils.isEmpty(tissue)) {
                        probeLibItem.setAttribute("tissue", tissue);
                    }
                    if (!StringUtils.isEmpty(stage)) {
                        probeLibItem.setAttribute("stage",stage);
                    }
                    if (!StringUtils.isEmpty(host)) {
                        probeLibItem.setAttribute("host", host);
                    }
                    if (!StringUtils.isEmpty(restrictionSites)) {
                        probeLibItem.setAttribute("restrictionSites", restrictionSites);
                    }


                    cloneItem.setReference("probeLibrary", probeLibItem);
                    //System.out.println("probelib ref is: " + cloneItem.getReference("probeLibrary").getRefId());
                }
            }

        }
    }

    public void processMarkerReplacedData(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();

            if (line.length < 3) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }

            String newPrimaryIdentifier = line[1];
            String oldPrimaryIdentifier = line[0];

            if (!StringUtils.isEmpty(newPrimaryIdentifier)) {
                Item itemReplaced = getTypedItem(newPrimaryIdentifier);
                addSynonym(itemReplaced, "identifier", oldPrimaryIdentifier);
            }
        }
    }

    public void processAttribution(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();

            if (line.length < 3) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }

            String markerPrimaryIdentifier = line[0];
            String pubPrimaryIdentifier = line[1];

            if (!StringUtils.isEmpty(markerPrimaryIdentifier)) {
                Item markerAttrib = getTypedItem(markerPrimaryIdentifier);
                Item pub = getPub(pubPrimaryIdentifier);
                markerAttrib.addToCollection("publications", pub);
            }
        }
    }

    public void processAliases(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 3) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }

            String daliasPrimaryIdentifier = line[0];
            String dataPrimaryIdentifier = line[1];
            String alias = line[2];
            String aliasType = line[3];

            if (!StringUtils.isEmpty(dataPrimaryIdentifier)) {
                Item itemAlias = getTypedItem(dataPrimaryIdentifier);
                if (aliasType.equals("alias")) {
                    aliasType = "name";
                }
                addSynonym(itemAlias, aliasType, alias);
                addSynonym(itemAlias, "accession", daliasPrimaryIdentifier);
            }
        }
    }

    public void processMrels(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();

            //System.out.println(line.length + line[0]);
            if (line.length < 3) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }
            String primaryIdentifier = line[0];
            String mrel1 = line[2];
            String mrel2 = line[3];
            String mrelType = line[1];

            if (!StringUtils.isEmpty(primaryIdentifier)) {
		
                Item item1 = null;
                Item item2 = null;
		
		System.out.println(primaryIdentifier+" "+mrel1+" "+mrel2+" "+mrelType);
		
                if (mrelType.equals("clone contains gene")) {
		    if (!(StringUtils.substring(mrel1,0,7).equals("ZDB-STS"))){
			item1 = getTypedItem(mrel1);	
			if (StringUtils.substring(mrel2,0,9).equals("ZDB-GENEP")){
			    item2 = getGeneP(mrel2);
			    item1.addToCollection("contains", item2);
			}
			if (StringUtils.substring(mrel2,0,9).equals("ZDB-GENE-")){
			    item2 = getGene(mrel2);
			    item1.addToCollection("contains", item2);
			}
		    }
		}
		if (mrelType.equals("clone contains transcript")) {
                    item1 = getTypedItem(mrel1);
                    item2 = getTscript(mrel2);
                    item1.addToCollection("transcripts", item2);
                    //item2.addToCollection("clones",item1);
                }
                //Todo: clone contains cloneoverlap collection?
                if (mrelType.equals("clone overlap")) {
                    item1 = getTypedItem(mrel1);
                    item2 = getTypedItem(mrel2);
                    item1.addToCollection("overlapsWith", item2);
                    //item2.addToCollection("overlapsWith",item1);
                }
		
		if (mrelType.equals("gene contains small segment")) {
                    item1 = getGene(mrel1);
                    item2 = getTypedItem(mrel2);
                    item1.addToCollection("containsSmallSegments", item2);
                    //item2.addToCollection("overlapsWith",item1);                                                                                                     
                }
		
                if (mrelType.equals("gene hybridized by small segment")) {
                    item1 = getTypedItem(mrel1);
                    item2 = getTypedItem(mrel2);
                    item1.addToCollection("hybridizedBySmallSegments", item2);
                }
		
                if (mrelType.equals("coding sequence of")) {
		    if (StringUtils.substring(mrel2,0,8).equals("ZDB-EFG-")){
			item1 = getEFG(mrel2);
		    }
		    if (StringUtils.substring(mrel2,0,8).equals("ZDB-GENE")){
                        item1 = getGene(mrel2);
                    }
		    if (StringUtils.substring(mrel2,0,12).equals("ZDB-TSCRIPT-")){
                        item1 = getTscript(mrel2);
                    }
		    if (StringUtils.substring(mrel2,0,11).equals("ZDB-REGION-")){
                        item1 = getRegion(mrel2);
                    }

		    item2 = getConstruct(mrel1);
		    item2.addToCollection("codingSequences", item1);
                    //item1.addToCollection("codingSequenceOf", item2);
                }
		if (mrelType.equals("contains engineered region")) {
                    item1 = getRegion(mrel2);
                    item2 = getConstruct(mrel1);
                    item2.addToCollection("engineeredRegions", item1);
                    //item1.addToCollection("codingSequenceOf", item2);                                                                 \   
                }

                if (mrelType.equals("gene produces transcript")) {
                    if(mrel1.substring(0,10).equals("ZDB-GENE-P")) {
                        item1 = getGeneP(mrel1);
                        item2 = getTscript(mrel2);
                        item2.setReference("pseudogene", item1);
                    }
                    else {
                        item1 = getGene(mrel1);
                        item2 = getTscript(mrel2);
                        item2.setReference("gene", item1);
                    }
                    //item1.addToCollection("transcripts",item2);
		    
                }
                if (mrelType.equals("gene product recognized by antibody")) {
		    if (mrel1.substring(0,9).equals("ZDB-GENE-")){
			item1 = getGene(mrel1);
			item2 = getAntibody(mrel2);
			item1.addToCollection("antibodies", item2);
		    }
                }
                if (mrelType.equals("gene encodes small segment")) {
                    if(mrel1.substring(0,10).equals("ZDB-GENEP-")) {
                        item1 = getGeneP(mrel1);
                        item2 = getTypedItem(mrel2);
                        item1.addToCollection("encodes", item2);
                    }
                    if (mrel1.substring(0,9).equals("ZDB-GENE-")){
                        item1 = getGene(mrel1);
                        item2 = getTypedItem(mrel2);
                        item1.addToCollection("encodes", item2);
                        //item2.addToCollection("genes",item1);
                    }
                }
                if (mrelType.equals("knockdown reagent targets gene")) {
		    if (mrel1.substring(0,10).equals("ZDB-TALEN-"))  {
			item1 = getReagent(mrel1);
			item2 = getGene(mrel2);
			item1.addToCollection("targets", item2);
			System.out.println("got a talen: " + mrel1);
		    }
		    else if (mrel1.substring(0,11).equals("ZDB-CRISPR-"))  {
			item1 = getReagent(mrel1);
			item2 = getGene(mrel2);
			item1.addToCollection("targets", item2);
			//System.out.println("got a crispr: " + mrel1);
                    }
		    else if (mrel1.substring(0,12).equals("ZDB-MRPHLNO-"))  {
                        item1 = getMrph(mrel1);
                        item2 = getGene(mrel2);
                        item1.addToCollection("targets", item2);
			System.out.println("got a mrph: " + mrel1);
                    }               
                }
                if (mrelType.equals("clone contains small segment")) {
                    item1 = getTypedItem(mrel1);
                    item2 = getTypedItem(mrel2);
                    item1.addToCollection("contains", item2);
                    //item2.addToCollection("clones",item1);
                }
                if (mrelType.equals("contains other features")) {
                    item1 = getConstruct(mrel1);
                    item2 = getRegion(mrel2);
                    item1.addToCollection("contains", item2);
                    //item2.addToCollection("constructs",item1);
                }
                if (mrelType.equals("contains polymorphism")) {
		    if(mrel1.substring(0,9).equals("ZDB-GENE-")){
			item1 = getGene(mrel1);
			item2 = getSNP(mrel2);
			item1.addToCollection("snps", item2);
			//item2.addToCollection("genes",item1);
		    }
                }
                if (mrelType.equals("gene has artifact")) {
                    item1 = getGene(mrel1);
                    item2 = getTypedItem(mrel2);
                    item1.addToCollection("cloneArtifacts", item2);
                    //item2.addToCollection("artifact8s",item1);
                }
                if (mrelType.equals("promoter of")) {
                    if (mrel2.substring(0,8).equals("ZDB-EFG-"))  {
                        item1 = getEFG(mrel2);
                        item2 = getConstruct(mrel1);
                        item1.addToCollection("promotes", item2);
                    }
                    else if (mrel2.substring(0,11).equals("ZDB-REGION-"))  {
                        item1 = getRegion(mrel2);
                        item2 = getConstruct(mrel1);
                        item1.addToCollection("promotes", item2);
                    }
                    else if (mrel2.substring(0,12).equals("ZDB-TSCRIPT-"))  {
                        item1 = getTscript(mrel2);
                        item2 = getConstruct(mrel1);
                        item1.addToCollection("promotes", item2);
                    }
                    else  {
                        item1 = getGene(mrel2);
                        item2 = getConstruct(mrel1);
                        item1.addToCollection("promotes", item2);
                    }
                    //item2.addToCollection("promoters",item1);
                }
		
            }
	    
	}
    }
    
    
    public void processMarkers(Reader reader) throws Exception {
	
        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');
	
        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
	    
            if (line.length < 3) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }
	    
            String primaryIdentifier = line[0];
            String name = line[1];
            String abbrev = line[1];
            String type = line[2];
            String nameR = line[3];

            if (!StringUtils.isEmpty(primaryIdentifier)) {
		Item itemTypedItem = getTypedItem(primaryIdentifier);
		addProperties(itemTypedItem, primaryIdentifier, nameR, type, abbrev);
		addSynonym(itemTypedItem, "identifier", primaryIdentifier);
		addSynonym(itemTypedItem, "accession", primaryIdentifier);
            }
        } // end parsing of file
	
        System.out.println("size of genes" + ":" + genes.size());
        System.out.println("size of antibodies" + ":" + antibodies.size());
        System.out.println("size of clones" + ":" + clones.size());
        System.out.println("size of efgs" + ":" + efgs.size());
        System.out.println("size of tscripts" + ":" + tscripts.size());
        System.out.println("size of mrphs" + ":" + mrphs.size());
        System.out.println("size of snps" + ":" + snps.size());
        System.out.println("size of sslps" + ":" + sslps.size());
        System.out.println("size of rapds" + ":" + rapds.size());
        System.out.println("size of constructs" + ":" + constructs.size());
        System.out.println("size of DNAClones" +":"+DNAclones.size());
        System.out.println("size of RNAClones" +":"+RNAclones.size());
	System.out.println("size of Talens and Crisprs" +":"+reagents.size());
    }

    private void addSynonym(Item item, String type, String value)
            throws SAXException {
        setSynonym(item.getIdentifier(), type, value);
    }

    private void setSynonym(String subjectRefId, String type, String value)
        throws SAXException {
        String key = subjectRefId + type + value;
        if (!synonyms.contains(key)) {
            Item synonym = createItem("Synonym");
            synonym.setAttribute("value", value);
            synonym.setReference("subject", subjectRefId);
            synonyms.add(key);
            try {
                store(synonym);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }
    }

    private void addProperties(Item item, String primaryIdentifier, String name, String type, String abbrev)
            throws SAXException {
        if (!StringUtils.isEmpty(primaryIdentifier)){
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
	}
        if (!StringUtils.isEmpty(name)){
	    item.setAttribute("name", name);
	}
        if (!StringUtils.isEmpty(abbrev)){
	    item.setAttribute("symbol", abbrev);
	}
	/*item.setAttribute("type", type);*/
    }
    
    private Item getTypedTranscript (String type, String primaryIdentifier) 
	throws SAXException{
	Item typedTscript = null;
	if (type.equals("antisense")){
	    typedTscript = getAntisenseTscript(primaryIdentifier);
	}
	else if (type.equals("V-gene")){
	    typedTscript = getVGeneTscript(primaryIdentifier);
	}
	else if (type.equals("aberrant processed transcript")){
	    typedTscript = getAbberantTscript(primaryIdentifier);
	}
	else if (type.equals("pseudogenic transcript")){
	    typedTscript =  getPseudogenicTrscript(primaryIdentifier);
	}
	else if (type.equals("polycistronic transcript")){
	    typedTscript =  getPolycistronicTscript(primaryIdentifier);
	}
	else if (type.equals("ncRNA")){
	    typedTscript = getNcRnaTscript(primaryIdentifier);
	}
	else if (type.equals("mRNA")){
	    typedTscript =  getMRnaTscript(primaryIdentifier);
	}
	else if (type.equals("piRNA")){
	    typedTscript =  getPiRnaTscript(primaryIdentifier);
	}
	else if (type.equals("miRNA")){
	    typedTscript =  getMiRnaTscript(primaryIdentifier);
	}
	else if (type.equals("pre miRNA")){
	    typedTscript =  getPreMiRnaTscript(primaryIdentifier);
	}
	else if (type.equals("rRNA")){
	    typedTscript =  getRRnaTscript(primaryIdentifier);
	}
	else if (type.equals("snRNA")){
	    typedTscript =  getSnRnaTscript(primaryIdentifier);
	}
	else if (type.equals("snoRNA")){
	    typedTscript =  getSnoRnaTscript(primaryIdentifier);
	}
	else if (type.equals("tRNA")){
	    typedTscript =  getTRnaTscript(primaryIdentifier);
	}
	else if (type.equals("transposable element")){
	    typedTscript =  getTElementTscript(primaryIdentifier);
	}
	else if (type.equals("transcript")){
	    typedTscript = getTscript(primaryIdentifier);
	}
	else if (type.equals("disrupted domain")){
	    typedTscript =  getDisruptedDomainTscript(primaryIdentifier);
	}
	else if (type.equals("lincRNA")){
	    typedTscript =  getLincRna(primaryIdentifier);
	}
	else{
	    typedTscript = getTscript(primaryIdentifier);
	}
        return typedTscript;
    }
    

    private Item getPolycistronicTscript(String primaryIdentifier) throws SAXException {

        Item item = polyTscripts.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Polycistronic Transcript");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            polyTscripts.put(primaryIdentifier, item);
        }
        return item;
        }

    private Item getNcRnaTscript(String primaryIdentifier) throws SAXException{
        Item item = ncrnaTscripts.get(primaryIdentifier);
        if (item == null) {
            item = createItem("ncRNA Transcript");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            ncrnaTscripts.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getMRnaTscript(String primaryIdentifier) throws SAXException{
        Item item = mrnaTscripts.get(primaryIdentifier);
        if (item == null) {
            item = createItem("ncRNA Transcript");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            mrnaTscripts.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getPiRnaTscript(String primaryIdentifier) throws SAXException{
        Item item = pirnaTscripts.get(primaryIdentifier);
        if (item == null) {
            item = createItem("ncRNA Transcript");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            pirnaTscripts.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getMiRnaTscript(String primaryIdentifier)throws SAXException {
        Item item = mirnaTscript.get(primaryIdentifier);
        if (item == null) {
            item = createItem("ncRNA Transcript");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            mirnaTscript.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getPreMiRnaTscript(String primaryIdentifier) throws SAXException{
        Item item = ncrnaTscripts.get(primaryIdentifier);
        if (item == null) {
            item = createItem("ncRNA Transcript");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            ncrnaTscripts.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getRRnaTscript(String primaryIdentifier)throws SAXException {
        Item item = rnaTscripts.get(primaryIdentifier);
        if (item == null) {
            item = createItem("ncRNA Transcript");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            rnaTscripts.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getSnRnaTscript(String primaryIdentifier) throws SAXException{
        Item item = snrnaTscripts.get(primaryIdentifier);
        if (item == null) {
            item = createItem("ncRNA Transcript");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            snrnaTscripts.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getSnoRnaTscript(String primaryIdentifier) throws SAXException{
        Item item = snornaTscripts.get(primaryIdentifier);
        if (item == null) {
            item = createItem("ncRNA Transcript");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            snornaTscripts.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getTRnaTscript(String primaryIdentifier) throws SAXException{
        Item item = trnaTscripts.get(primaryIdentifier);
        if (item == null) {
            item = createItem("ncRNA Transcript");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            trnaTscripts.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getTElementTscript(String primaryIdentifier)throws SAXException {
        Item item = telementTscripts.get(primaryIdentifier);
        if (item == null) {
            item = createItem("ncRNA Transcript");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            telementTscripts.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getDisruptedDomainTscript(String primaryIdentifier) throws SAXException{
        Item item = disrupteddomainTscripts.get(primaryIdentifier);
        if (item == null) {
            item = createItem("ncRNA Transcript");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            disrupteddomainTscripts.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getLincRna(String primaryIdentifier) throws SAXException{
        Item item = lincrnaTscripts.get(primaryIdentifier);
        if (item == null) {
            item = createItem("ncRNA Transcript");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            lincrnaTscripts.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getPseudogenicTrscript(String primaryIdentifier) throws SAXException{
        Item item = pseudogenicTscripts.get(primaryIdentifier);
        if (item == null) {
            item = createItem("ncRNA Transcript");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            pseudogenicTscripts.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getAbberantTscript(String primaryIdentifier)throws SAXException {
        Item item = abberantTscripts.get(primaryIdentifier);
        if (item == null) {
            item = createItem("ncRNA Transcript");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            abberantTscripts.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getAntisenseTscript(String primaryIdentifier) throws SAXException{
        Item item = antisenseTscripts.get(primaryIdentifier);
        if (item == null) {
            item = createItem("ncRNA Transcript");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            antisenseTscripts.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getVGeneTscript(String primaryIdentifier) throws SAXException{
        Item item = vgeneTscripts.get(primaryIdentifier);
        if (item == null) {
            item = createItem("ncRNA Transcript");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            vgeneTscripts.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getTypedItem(String primaryIdentifier)
	throws SAXException {

        Item typedItem = null;

        if (primaryIdentifier.substring(0, 9).equals("ZDB-GENE-")) {
            typedItem = getGene(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 10).equals("ZDB-GENEP-")) {
            typedItem = getGeneP(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 8).equals("ZDB-SNP-")) {
            typedItem = getSNP(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 11).equals("ZDB-REGION-")) {
            typedItem = getREGION(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 8).equals("ZDB-ATB-")) {
            typedItem = getAntibody(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 12).equals("ZDB-TSCRIPT-")) {
            typedItem = getTscript(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 12).equals("ZDB-MRPHLNO-")) {
            typedItem = getMrph(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 10).equals("ZDB-TALEN-")){
            typedItem = getReagent(primaryIdentifier);
	}
        else if (primaryIdentifier.substring(0, 11).equals("ZDB-CRISPR-")){
            typedItem = getReagent(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 8).equals("ZDB-EST-")) {
            typedItem = getRNAClone(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 9).equals("ZDB-CDNA-")) {
            typedItem = getRNAClone(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 8).equals("ZDB-BAC-")) {
            typedItem = getDNAClone(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 11).equals("ZDB-FOSMID-")) {
            typedItem = getDNAClone(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 8).equals("ZDB-PAC-")) {
            typedItem = getDNAClone(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 9).equals("ZDB-RAPD-")) {
            typedItem = getRAPD(primaryIdentifier);
        }
	else if (primaryIdentifier.substring(0, 9).equals("ZDB-SSLP-")) {
            typedItem = getSSLP(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 8).equals("ZDB-STS-")) {
            typedItem = getSTS(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 15).equals("ZDB-TGCONSTRCT-")) {
            typedItem = getConstruct(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 15).equals("ZDB-GTCONSTRCT-")) {
            typedItem = getConstruct(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 15).equals("ZDB-PTCONSTRCT-")) {
            typedItem = getConstruct(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 15).equals("ZDB-ETCONSTRCT-")) {
            typedItem = getConstruct(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 8).equals("ZDB-EFG-")) {
            typedItem = getEFG(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 12).equals("ZDB-BAC_END-")) {
            typedItem = getRNAClone(primaryIdentifier);
        }
        else if (primaryIdentifier.substring(0, 12).equals("ZDB-PAC_END-")) {
            typedItem = getRNAClone(primaryIdentifier);
	    
	}else {
		System.out.println("Missing markertype: "+ primaryIdentifier);
	    }
	return typedItem;
    }


    private Item getGene(String primaryIdentifier)
            throws SAXException {
        Item item = genes.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Gene");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            genes.put(primaryIdentifier, item);
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


    private Item getPub(String primaryIdentifier)
            throws SAXException {
        Item item = pubs.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Publication");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            //item.setReference("organism", getOrganism("Zebrafish"));
            pubs.put(primaryIdentifier, item);

            try {
                store(item);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }
        return item;
    }

    private Item getRegion(String primaryIdentifier)
            throws SAXException {
        Item item = regions.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Region");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955") );
            regions.put(primaryIdentifier, item);
        }
        return item;
    }
    private Item getReagent(String primaryIdentifier)
	throws SAXException {
        Item item = reagents.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Reagent");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955") );
            reagents.put(primaryIdentifier, item);
        }
        return item;
    }
    private Item getAntibody(String primaryIdentifier)
            throws SAXException {
        Item item = antibodies.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Antibody");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism",  getOrganism("7955"));
            antibodies.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getGeneP(String primaryIdentifier)
            throws SAXException {
        Item item = geneps.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Pseudogene");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955"));
            geneps.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getConstruct(String primaryIdentifier)
            throws SAXException {
        Item item = constructs.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Construct");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism",  getOrganism("7955"));
	    constructs.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getSTS(String primaryIdentifier)
            throws SAXException {
        Item item = stss.get(primaryIdentifier);
        if (item == null) {
            item = createItem("STS");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism",  getOrganism("7955"));
            stss.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getSNP(String primaryIdentifier)
            throws SAXException {
        Item item = snps.get(primaryIdentifier);
        if (item == null) {
            item = createItem("SNP");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism",  getOrganism("7955"));
            snps.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getREGION(String primaryIdentifier)
            throws SAXException {
        Item item = regions.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Region");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism",  getOrganism("7955"));
            regions.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getSSLP(String primaryIdentifier)
            throws SAXException {
        Item item = sslps.get(primaryIdentifier);
        if (item == null) {
            item = createItem("SimpleSequenceLengthVariation");
            item.setReference("organism", getOrganism("7955") );
	    item.setAttribute("primaryIdentifier",primaryIdentifier);
            sslps.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getRAPD(String primaryIdentifier)
            throws SAXException {
        Item item = rapds.get(primaryIdentifier);
        if (item == null) {
            item = createItem("RAPD");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism",  getOrganism("7955"));
            rapds.put(primaryIdentifier, item);
        }
        return item;
    }

    //Todo: either make EST or Clone or both, not one or the other
    //else marker-relationships won't work.
    private Item getClone(String primaryIdentifier)
            throws SAXException {
        Item item = clones.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Clone");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism",  getOrganism("7955"));
            clones.put(primaryIdentifier, item);
        }
        return item;
    }
    private Item getDNAClone(String primaryIdentifier)
            throws SAXException {
        Item item = DNAclones.get(primaryIdentifier);
        if (item == null) {
            item = createItem("DNAClone");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism",  getOrganism("7955"));
            DNAclones.put(primaryIdentifier, item);
        }
        return item;
    }
    private Item getRNAClone(String primaryIdentifier)
            throws SAXException {
        Item item = RNAclones.get(primaryIdentifier);
        if (item == null) {
            item = createItem("RNAClone");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism", getOrganism("7955") );
            RNAclones.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getMrph(String primaryIdentifier)
            throws SAXException {
        Item item = mrphs.get(primaryIdentifier);
        if (item == null) {
            item = createItem("MorpholinoOligo");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism",  getOrganism("7955"));
	    mrphs.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getTscript(String primaryIdentifier)
            throws SAXException {
        Item item = tscripts.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Transcript");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism",  getOrganism("7955"));
            tscripts.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getEFG(String primaryIdentifier)
            throws SAXException {
        Item item = efgs.get(primaryIdentifier);
        if (item == null) {
            item = createItem("EngineeredForeignGene");
	    item.setAttribute("primaryIdentifier", primaryIdentifier);
            item.setReference("organism",  getOrganism("7955"));
            efgs.put(primaryIdentifier, item);
        }
        return item;
    }


    private Item getLink(String accession, Item typedItem, String primaryIdentifier)
            throws SAXException {
        Item item = links.get(primaryIdentifier);
        if (item == null) {
            item = createItem("CrossReference");
            item.setReference("subject",typedItem);
            item.setAttribute("identifier", accession);
            links.put(primaryIdentifier, item);
        }
        return item;
    }

    private Item getProtein(String accNum, String dataPrimaryIdentifier)
            throws SAXException {
        Item item = proteins.get(accNum);
        if (item == null) {
            item = createItem("Protein");
	    item.setReference("organism",  getOrganism("7955"));
            item.setAttribute("primaryAccession", accNum);
            item.setAttribute("primaryIdentifier", accNum);
            item.addToCollection("genes",getTypedItem(dataPrimaryIdentifier));
            addSynonym(item, "identifier", accNum);
            proteins.put(accNum, item);
        }
        return item;
    }

    private Item getGenotype(String primaryIdentifier)
            throws SAXException {
        Item item = genotypes.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Genotype");
            item.setReference("organism",  getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            genotypes.put(primaryIdentifier, item);
            try {
                store(item);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }
        return item;
    }

    private Item getProbeLibrary(String primaryIdentifier)
            throws SAXException {
        Item item = probelibs.get(primaryIdentifier);
        if (item == null) {
            item = createItem("ProbeLibrary");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
	    item.setReference("organism",  getOrganism("7955"));
            probelibs.put(primaryIdentifier, item);
        }
        return item;
    }

}

