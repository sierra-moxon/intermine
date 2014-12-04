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
 * @author
 */
public class ZfinFeatureMarkerRelationshipsConverter extends BioFileConverter {

    private static final Logger LOG = Logger.getLogger(ZfinFeatureMarkerRelationshipsConverter.class);
    protected String organismRefId;

    private Map<String, Item> features = new HashMap();
    private Map<String, Item> genes = new HashMap();
    private Map<String, Item> geneps = new HashMap();
    private Map<String, Item> constructs = new HashMap();
    private Set<String> synonyms = new HashSet();
    private Map<String, Item> DNAClones = new HashMap();
    private Map<String, Item> RNAClones = new HashMap();
    private Map<String, Item> stss = new HashMap();
    private Map<String, Item> sslps = new HashMap();
    private Map<String, Item> rapds = new HashMap();
    private Map<String, Item> markers = new HashMap();
    private Map<String, Item> terms = new HashMap();
    private Map<String, Item> reagents = new HashMap();

    public ZfinFeatureMarkerRelationshipsConverter(ItemWriter writer, Model model)
            throws ObjectStoreException {
        super(writer, model, "ZFIN", "ZFIN Gene and Allele Data Set");

    }

    public void process(Reader reader) throws Exception {

        processFmrels(reader);

        try {
           for (Item feature : features.values()) {
                //System.out.println(feature.getClassName());
                    store(feature);
               //System.out.println(feature.getAttribute("primaryIdentifier").toString());
            }
            
            for (Item construct : constructs.values()) {
                //System.out.println(feature.getClassName());
                    store(construct);
               //System.out.println(feature.getAttribute("primaryIdentifier").toString());
            }
	    for (Item reagent : reagents.values()) {
                System.out.println(reagent.getClassName());                                                                      
		store(reagent);
		//System.out.println(feature.getAttribute("primaryIdentifier").toString());                                         
            }
            for (Item dnaclone : DNAClones.values()) {
                //System.out.println(feature.getClassName());
                    store(dnaclone);
               //System.out.println(feature.getAttribute("primaryIdentifier").toString());
            }
            for (Item rnaclone : RNAClones.values()) {
                //System.out.println(feature.getClassName());
                    store(rnaclone);
               //System.out.println(feature.getAttribute("primaryIdentifier").toString());
            }
            for (Item sts : stss.values()) {
                //System.out.println(feature.getClassName());
                    store(sts);
               //System.out.println(feature.getAttribute("primaryIdentifier").toString());
            }
            for (Item sslp : sslps.values()) {
                //System.out.println(feature.getClassName());
                    store(sslp);
               //System.out.println(feature.getAttribute("primaryIdentifier").toString());
            }
            for (Item rapd : rapds.values()) {
                //System.out.println(feature.getClassName());
                    store(rapd);
               //System.out.println(feature.getAttribute("primaryIdentifier").toString());
            }
        }
        catch (ObjectStoreException e) {
            throw new SAXException(e);
        }
    }

    public void processFmrels(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 4) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }


            String relType = line[1];
            String featurePrimaryIdentifier = line[2];
            String markerPrimaryIdentifier = line[3];
            String type = line[4];

            if (!StringUtils.isEmpty(featurePrimaryIdentifier)) {
                if (!StringUtils.isEmpty(markerPrimaryIdentifier)) {
		    
                    Item feature = getTypedFeature(featurePrimaryIdentifier, type);
		    
                    Item marker = getTypedItem(markerPrimaryIdentifier);
		    
                    if (relType.equals("is allele of")) {
			feature.addToCollection("genes", marker);
		    }     
		    
		    else if (relType.equals("contains innocuous sequence feature")) {
			//System.out.println("innocuous!" + marker.getAttribute("primaryIdentifier").toString());
			marker.setAttribute("innocuouslyInserted", "true");
			marker.setAttribute("phenotypicallyInserted", "false");
			feature.addToCollection("constructs",marker);
		    } else if (relType.equals("created by")) {
			System.out.println("created by found");
			marker.setReference("creates", feature);
			feature.setReference("createdBy", marker);
		    } else if (relType.equals("contains phenotypic sequence feature")) {
			marker.setAttribute("phenotypicallyInserted", "true");
			marker.setAttribute("innocuouslyInserted", "false");
			feature.addToCollection("constructs",marker);
			
		    } else if (relType.equals("markers present")) {
			feature.addToCollection("markersPresent", marker);
			
		    } else if (relType.equals("markers missing")) {
			feature.addToCollection("markersMissing", marker);
			
		    } else if (relType.equals("markers moved")) {
			feature.addToCollection("markersMoved", marker);
			
		    } else {
			System.out.println("relType not found! " + relType);
			if (marker == null){
			    throw new SAXException();
			    
			}
		    }
		    if (feature == null){
			throw new SAXException();
			
		    }
		}
		//TODO: set synonym relationships
	    }
	    
	    
	}
    }

    private Item getTypedItem(String primaryIdentifier)
            throws SAXException {

        Item typedItem = null;
       //Item typedItem = getMarker(primaryIdentifier);

        if (primaryIdentifier.substring(0, 15).equals("ZDB-TGCONSTRCT-")) {
            typedItem = getConstruct(primaryIdentifier);
            
        } else if (primaryIdentifier.substring(0, 15).equals("ZDB-GTCONSTRCT-")) {
            typedItem = getConstruct(primaryIdentifier);

        } else if (primaryIdentifier.substring(0, 15).equals("ZDB-ETCONSTRCT-")) {
            typedItem = getConstruct(primaryIdentifier);

        } else if (primaryIdentifier.substring(0, 15).equals("ZDB-PTCONSTRCT-")) {
            typedItem = getConstruct(primaryIdentifier);

        } else if (primaryIdentifier.substring(0, 9).equals("ZDB-GENE-")) {
            typedItem = getGene(primaryIdentifier);
        } else if (primaryIdentifier.substring(0, 10).equals("ZDB-GENEP-")) {
            typedItem = getGeneP(primaryIdentifier);
        } else if (primaryIdentifier.substring(0, 8).equals("ZDB-EST-")) {
            typedItem = getRNAClone(primaryIdentifier);
        } else if (primaryIdentifier.substring(0, 8).equals("ZDB-PAC-")) {
            typedItem = getDNAClone(primaryIdentifier);
        } else if (primaryIdentifier.substring(0, 8).equals("ZDB-STS-")) {
            typedItem = getSTS(primaryIdentifier);
        } else if (primaryIdentifier.substring(0, 9).equals("ZDB-RAPD-")) {
            typedItem = getRAPD(primaryIdentifier);
        } else if (primaryIdentifier.substring(0, 9).equals("ZDB-SSLP-")) {
            typedItem = getSSLP(primaryIdentifier);
	} else if (primaryIdentifier.substring(0, 10).equals("ZDB-TALEN-")) {
            typedItem = getReagent(primaryIdentifier);
	} else if (primaryIdentifier.substring(0, 11).equals("ZDB-CRISPR-")) {
            typedItem = getReagent(primaryIdentifier);
        } else {
            System.out.println("markerType not found! " + primaryIdentifier);

        }

        return typedItem;
    }

    private Item getConstruct(String primaryIdentifier)
            throws SAXException {
        Item item = constructs.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Construct");
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            constructs.put(primaryIdentifier, item);
           /* try {
                store(item);
            }
            catch (ObjectStoreException e) {
                throw new SAXException(e);
            }    */
        }
        
        return item;
    }

    private Item getRNAClone(String primaryIdentifier)
            throws SAXException {
        Item item = RNAClones.get(primaryIdentifier);
        if (item == null) {
            item = createItem("RNAClone");
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            RNAClones.put(primaryIdentifier, item);
            /*try {
                store(item);
            }
            catch (ObjectStoreException e) {
                throw new SAXException(e);
            }     */
        }
        return item;                                  
    }

    private Item getReagent(String primaryIdentifier)
	throws SAXException {
        Item item = reagents.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Reagent");
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            reagents.put(primaryIdentifier, item);
            /*try {                                                                                                                
                store(item);                                                                                                       
            }                                                                                                                      
            catch (ObjectStoreException e) {                                                                                       
                throw new SAXException(e);                                                                                         
		}     */
        }
        return item;
    }

    private Item getDNAClone(String primaryIdentifier)
            throws SAXException {
        Item item = DNAClones.get(primaryIdentifier);
        if (item == null) {
            item = createItem("DNAClone");
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            DNAClones.put(primaryIdentifier, item);
            /*try {
                store(item);
            }
            catch (ObjectStoreException e) {
                throw new SAXException(e);
            }   */
        }
        return item;
    }

    private Item getSTS(String primaryIdentifier)
            throws SAXException {
        Item item = stss.get(primaryIdentifier);
        if (item == null) {
            item = createItem("STS");
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            stss.put(primaryIdentifier, item);
            /*try {
                store(item);
            }
            catch (ObjectStoreException e) {
                throw new SAXException(e);
            }    */
        }
        return item;
    }

    private Item getSSLP(String primaryIdentifier)
            throws SAXException {
        Item item = sslps.get(primaryIdentifier);
        if (item == null) {
            item = createItem("SimpleSequenceLengthVariation");
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            sslps.put(primaryIdentifier, item);
            /*try {
                store(item);
            }
            catch (ObjectStoreException e) {
                throw new SAXException(e);
            }   */
        }
        return item;
    }

    private Item getRAPD(String primaryIdentifier)
            throws SAXException {
        Item item = rapds.get(primaryIdentifier);
        if (item == null) {
            item = createItem("RAPD");
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            rapds.put(primaryIdentifier, item);
            /*try {
                store(item);
            }
            catch (ObjectStoreException e) {
                throw new SAXException(e);
            }  */
        }
        return item;
    }

    private Item getTypedFeature(String primaryIdentifier, String type) throws SAXException {
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
        } else if (type.equals("INDEL")) {
            typedItem = getFeature(primaryIdentifier, "Indel");
        }
	else {
            System.out.println("Type not found " + type);
        }
        return typedItem;
    }

    private Item getFeature(String primaryIdentifier, String soTermName) {
        Item item = features.get(primaryIdentifier);
        if (item == null) {
            item = createItem(soTermName);
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            features.put(primaryIdentifier, item);
        } 
        return item;
    }


    private Item getGene(String primaryIdentifier)
            throws SAXException {
        Item item = genes.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Gene");
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            genes.put(primaryIdentifier, item);
             try {
                store(item);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }
        return item;
    }


    private Item getGeneP(String primaryIdentifier)
	throws SAXException {
        Item item = geneps.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Pseudogene");
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            geneps.put(primaryIdentifier, item);
	    try {
                store(item);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }
        return item;
    }


}
