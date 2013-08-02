package org.intermine.bio.dataconversion;

import org.intermine.xml.full.Item;
import org.zfin.intermine.dataconversion.SpecificationSheet;
import org.zfin.intermine.dataconversion.ZfinDirectoryConverter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * DataConverter to load ZFIN feature identifiers from text files
 */
public class SourceFeaturePrefixConverter {

    private Map<String, Item> featurePrefix = new HashMap<String, Item>(300);
    private ZfinDirectoryConverter converter;

    public SourceFeaturePrefixConverter(ZfinDirectoryConverter converter) {
        this.converter = converter;
    }

    public void process(File directory) throws Exception {
        converter.directory = directory;
        processFeatures("feature-prefix.txt");


    }

    public void processFeatures(String file) throws Exception {

        SpecificationSheet specSheet = new SpecificationSheet();
        specSheet.setItemMap(featurePrefix);
        specSheet.setFileName(file);
        converter.processFile(specSheet);
    }

    public Map<String, Item> getFeaturePrefix() {
        return featurePrefix;
    }
}
