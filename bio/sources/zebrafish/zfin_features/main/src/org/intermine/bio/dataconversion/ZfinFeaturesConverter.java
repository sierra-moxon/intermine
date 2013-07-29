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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.util.FormattedTextParser;
import org.intermine.xml.full.Item;
import org.xml.sax.SAXException;
import org.zfin.intermine.dataconversion.ZfinDirectoryConverter;

import java.io.*;
import java.util.*;


/**
 * DataConverter to load ZFIN feature identifiers from text files
 */
public class ZfinFeaturesConverter extends ZfinDirectoryConverter {

    private static final Logger LOG = Logger.getLogger(ZfinFeaturesConverter.class);
    protected String organismRefId;
    private Map<String, Item> features = new HashMap();
    private Map<String, Item> prefixes = new HashMap();
    private Map<String, Item> labs = new HashMap();


    private Model model;
    private ItemWriter writer;

    /**
     * Constructor
     *
     * @param writer the ItemWriter used to handle the resultant items
     * @param model  the Model
     * @throws ObjectStoreException if an error occurs in storing
     */
    public ZfinFeaturesConverter(ItemWriter writer, Model model)
            throws ObjectStoreException {
        super(writer, model, "ZFIN", "Alleles and Transgenics");
        this.writer = writer;
        this.model = model;
    }

    @Override
    public void process(File directory) throws Exception {
        File featureFile = new File(directory.getCanonicalPath() + "/1features.txt");

        FeaturePrefixConverter featurePrefixConverter = new FeaturePrefixConverter(this);
        featurePrefixConverter.process(directory);
        prefixes = featurePrefixConverter.getFeaturePrefix();
        processFeatures(new FileReader(featureFile));
        File sourceFeatureFile = new File(directory.getCanonicalPath() + "/feature-prefix-source.txt");
        processSourceFeatures(new FileReader(sourceFeatureFile));

        try {
            for (Item lab : labs.values())
                store(lab);
            for (Item featurePrefix : prefixes.values())
                store(featurePrefix);
            for (Item feature : features.values())
                store(feature);
        } catch (ObjectStoreException e) {
            throw new Exception(e);
        }
    }

    private void processSourceFeatures(FileReader reader) throws IOException, SAXException {
        Iterator lineIter = getLineIterator(reader);

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 2) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }
            String prefixID = line[0];
            String labID = line[1];
            Item lab = getItem(labID, "Lab", labs);
            Item prefix = getItem(prefixID, "FeaturePrefix", prefixes);
            prefix.addToCollection("labs", lab);
        }
    }

    public void processFeatures(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] columns = (String[]) lineIter.next();
            if (columns.length < 5) {
                throw new RuntimeException("Line does not have enough elements: " + columns.length + columns[0]);
            }
            String primaryIdentifier = columns[0];

            String name = columns[1];
            String abbrev = columns[2];
            String type = columns[3];
            String lineDesignation = columns[4];

            Item feature;

            if (!StringUtils.isEmpty(primaryIdentifier)) {
                feature = getTypedItem(primaryIdentifier, type);

                if (!StringUtils.isEmpty(name)) {
                    feature.setAttribute("name", name);
                }
                if (!StringUtils.isEmpty(abbrev)) {
                    feature.setAttribute("symbol", abbrev);
                }
                if (!StringUtils.isEmpty(primaryIdentifier)) {
                    feature.setAttribute("featureId", primaryIdentifier);
                }
                if (!StringUtils.isEmpty(lineDesignation)) {
                    Item prefix = getItem(lineDesignation, "FeaturePrefix", prefixes);
                    feature.setReference("featurePrefix", prefix);
                }
            }

        }
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

}
