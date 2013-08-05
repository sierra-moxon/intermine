package org.intermine.bio.dataconversion;

import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.xml.full.Item;
import org.zfin.intermine.dataconversion.ColumnDefinition;
import org.zfin.intermine.dataconversion.SpecificationSheet;
import org.zfin.intermine.dataconversion.ZfinDirectoryConverter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Sierra
 * @author Christian
 */
public class LabConverter extends ZfinDirectoryConverter {
    //
    private static final String DATASET_TITLE = "Lab";
    protected String organismRefId;
    private Map<String, Item> labs = new HashMap<String, Item>(1000);
    private Map<String, Item> prefixes = new HashMap<String, Item>(400);
    private Map<String, Item> persons = new HashMap<String, Item>(900);

    /**
     * Constructor
     *
     * @param writer the ItemWriter used to handle the resultant items
     * @param model  the Model
     */

    public LabConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }

    public void process(File directory) throws Exception {
        this.directory = directory;
        try {
            processLabs("1lab.txt");
            processSourceFeatures("feature-prefix-source.txt");
        } catch (IOException err) {
            throw new RuntimeException("error reading labFile", err);
        }

        try {
            for (Item person : persons.values())
                store(person);
            for (Item prefix : prefixes.values())
                store(prefix);
            for (Item lab : labs.values())
                store(lab);
        } catch (ObjectStoreException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            throw new Exception(sw.toString());
        }
    }

    public void processSourceFeatures(String file) throws Exception {
        SpecificationSheet specSheet = new SpecificationSheet();
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE, "prefix", "FeaturePrefix"));
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE));
        specSheet.addItemMap(DATASET_TITLE, labs);
        specSheet.addItemMap("FeaturePrefix", prefixes);
        specSheet.setFileName(file);
        processFile(specSheet);
    }


    public void processLabs(String file) throws Exception {
        SpecificationSheet specSheet = new SpecificationSheet();
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE));
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE, "name"));
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE, "contactPerson", "Person"));
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE, "url"));
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE, "email"));
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE, "fax"));
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE, "phone"));
        specSheet.addItemMap(DATASET_TITLE, labs);
        specSheet.addItemMap("Person", persons);
        specSheet.setFileName(file);
        processFile(specSheet);
    }

}

