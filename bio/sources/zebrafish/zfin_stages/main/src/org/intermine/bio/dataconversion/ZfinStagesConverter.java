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


public class ZfinStagesConverter extends ZfinDirectoryConverter {

    public static final String DATASET_TITLE = "Developmental Stages";

    private Map<String, Item> stages = new HashMap<String, Item>(50);

    public ZfinStagesConverter(ItemWriter writer, Model model)
            throws ObjectStoreException {
        super(writer, model, "ZFIN", "Developmental Stages");
    }

    public void process(File directory) throws Exception {
        this.directory = directory;
        try {
            processStages("1stages.txt");
        } catch (IOException err) {
            throw new RuntimeException("error reading labFile", err);
        }

        try {
            for (Item stage : stages.values())
                store(stage);
        } catch (ObjectStoreException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            throw new Exception(sw.toString());
        }
    }

    public void processStages(String fileName) throws Exception {
        SpecificationSheet specSheet = new SpecificationSheet();
        String itemName = "ZFATerm";
        specSheet.addColumnDefinition(new ColumnDefinition(itemName));
        specSheet.addColumnDefinition(new ColumnDefinition(itemName, "name"));
        specSheet.addColumnDefinition(new ColumnDefinition(itemName, "stageAbbreviation"));
        specSheet.addColumnDefinition(new ColumnDefinition(itemName, "stageStartHour"));
        specSheet.addColumnDefinition(new ColumnDefinition(itemName, "stageEndHour"));
        specSheet.addColumnDefinition(new ColumnDefinition(itemName, "identifier"));
        specSheet.addItemMap(itemName, stages);
        specSheet.setFileName(fileName);
        processFile(specSheet);
    }

}
