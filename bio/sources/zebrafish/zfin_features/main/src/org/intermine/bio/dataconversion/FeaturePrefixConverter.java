package org.intermine.bio.dataconversion;

import org.intermine.xml.full.Item;
import org.zfin.intermine.dataconversion.ColumnDefinition;
import org.zfin.intermine.dataconversion.SpecificationSheet;
import org.zfin.intermine.dataconversion.ZfinDirectoryConverter;

import java.util.HashMap;
import java.util.Map;


/**
 * DataConverter to load ZFIN feature identifiers from text files
 */
public class FeaturePrefixConverter  {

    private Map<String, Item> featurePrefix = new HashMap<String, Item>(350);
    public static final String ITEM_NAME = "FeaturePrefix";
    private ZfinDirectoryConverter converter;

    public FeaturePrefixConverter(ZfinDirectoryConverter converter) {
        this.converter = converter;
    }

    public void process() throws Exception {
        processFeatures("feature-prefix.txt");
    }

    public void processFeatures(String fileName) throws Exception {

        SpecificationSheet specSheet = new SpecificationSheet();
        specSheet.addColumnDefinition(new ColumnDefinition(ITEM_NAME, ColumnDefinition.PRIMARY_IDENTIFIER));
        specSheet.addColumnDefinition(new ColumnDefinition(ITEM_NAME, "name"));
        specSheet.addColumnDefinition(new ColumnDefinition(ITEM_NAME, "instituteName"));
        specSheet.addItemMap(ITEM_NAME, featurePrefix);
        specSheet.setFileName(fileName);
        converter.processFile(specSheet);
    }

    public Map<String, Item> getFeaturePrefix() {
        return featurePrefix;
    }
}
