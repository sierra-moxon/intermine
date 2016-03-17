package org.intermine.bio.dataconversion;

import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.xml.full.Item;
import org.zfin.intermine.dataconversion.ColumnDefinition;
import org.zfin.intermine.dataconversion.FeatureType;
import org.zfin.intermine.dataconversion.SpecificationSheet;
import org.zfin.intermine.dataconversion.ZfinDirectoryConverter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class FeatureLabConverter extends ZfinDirectoryConverter {

    private Map<String, Item> items = new HashMap<String, Item>(40000);


    public FeatureLabConverter(ItemWriter writer, Model model)
            throws ObjectStoreException {
        super(writer, model, "ZFIN", "Alleles and Transgenics");
    }

    @Override
    public void process(File dataDir) throws Exception {
        this.directory = dataDir;
        processFeatureLab("labOfOrigin.txt");
        try {
            storeAll(items, "lab");
        } catch (ObjectStoreException e) {
            throw new Exception(e);
        }
    }

    public void processFeatureLab(String fileName) throws Exception {
        SpecificationSheet specSheet = new SpecificationSheet();
        specSheet.addColumnDefinition(new ColumnDefinition(ColumnDefinition.MAPPED_ITEM_NAME));
        specSheet.addColumnDefinition(new ColumnDefinition(ColumnDefinition.MAPPED_ITEM_NAME, "labOfOrigin", true, "Lab"));
        specSheet.addColumnDefinition(new ColumnDefinition(ColumnDefinition.MAPPED_ITEM_NAME, "featureType"));
        specSheet.setItemMap(items);
        // feature item name is mapped into feature type mapped item name
        specSheet.setMappingColumn(2);
        specSheet.setFileName(fileName);
        processFile(specSheet);

    }

    public String getMappedItemName(String featureType) {
        return FeatureType.getFeatureValue(featureType);
    }
}
