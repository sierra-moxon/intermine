package org.intermine.bio.dataconversion;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.util.FormattedTextParser;
import org.intermine.xml.full.Item;
import org.xml.sax.SAXException;
import org.zfin.intermine.dataconversion.ColumnDefinition;
import org.zfin.intermine.dataconversion.SpecificationSheet;
import org.zfin.intermine.dataconversion.ZfinDirectoryConverter;

import java.io.*;
import java.util.*;


/**
 * DataConverter to load ZFIN feature identifiers from text files
 */
public class ZfinFeaturesConverter extends ZfinDirectoryConverter {

    private static final Logger LOG = Logger.getLogger(ZfinFeaturesConverter.class);
    protected String organismRefId;
    private Map<String, Item> items = new HashMap<String, Item>(25550);

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
        this.directory = directory;
        File featureFile = new File(directory.getCanonicalPath() + "/1features.txt");
        
        processFeatures(new FileReader(featureFile));
        processSourceFeatures("feature-prefix-source.txt");

        try {
            storeAll(items, "Lab");
        } catch (ObjectStoreException e) {
            throw new Exception(e);
        }
    }

    private void processSourceFeatures(String file) throws Exception {

        SpecificationSheet specSheet = new SpecificationSheet();
        specSheet.addColumnDefinition(new ColumnDefinition("FeaturePrefix"));
        specSheet.addColumnDefinition(new ColumnDefinition("FeaturePrefix", "labs", true, "Lab"));
        specSheet.setItemMap(items);
        specSheet.setFileName(file);
        processFile(specSheet);
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
	    String mutagen = columns[5];
	    String mutagee = columns[6];

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
		if (!StringUtils.isEmpty(mutagen)) {
                    feature.setAttribute("mutagen", mutagen);
                }
		if (!StringUtils.isEmpty(mutagee)) {
                    feature.setAttribute("mutagee", mutagee);
                }
                if (!StringUtils.isEmpty(lineDesignation)) {
                    Item prefix = getItem(lineDesignation, "FeaturePrefix", items);
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
        } else if (type.equals("INDEL")) {
            typedItem = getFeature(primaryIdentifier, "Indel");
        }

        return typedItem;
    }

    private Item getFeature(String primaryIdentifier, String soTermName) {
        Item item = items.get(primaryIdentifier);
        if (item == null) {
            item = createItem(soTermName);
            item.setReference("organism", getOrganism("7955"));
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            items.put(primaryIdentifier, item);
        }

        return item;
    }


}

