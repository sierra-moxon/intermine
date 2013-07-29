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

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.util.FormattedTextParser;
import org.intermine.xml.full.Item;
import org.xml.sax.SAXException;
import org.zfin.intermine.dataconversion.ColumnDefinition;
import org.zfin.intermine.dataconversion.SpecificationSheet;
import org.zfin.intermine.dataconversion.ZfinDirectoryConverter;


/**
 * @author Sierra
 * @author Christian
 */
public class LabsConverter extends ZfinDirectoryConverter {
    //
    private static final String DATASET_TITLE = "Lab";
    protected String organismRefId;
    private Map<String, Item> labs = new HashMap<String, Item>(900);
    private Map<String, Item> prefixes = new HashMap<String, Item>(900);
    private Map<String, Item> persons = new HashMap<String, Item>(900);

    /**
     * Constructor
     *
     * @param writer the ItemWriter used to handle the resultant items
     * @param model  the Model
     */

    public LabsConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }

    public void process(File directory) throws Exception {
        try {
            System.out.println("canonical path: " + directory.getCanonicalPath());
            File labFile = new File(directory.getCanonicalPath() + "/1lab.txt");
            File sourceFeatureFile = new File(directory.getCanonicalPath() + "/feature-prefix-source.txt");
            processLabs(new FileReader(labFile));
            processSourceFeatures(new FileReader(sourceFeatureFile));
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

    public void processSourceFeatures(Reader reader) throws Exception {
        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 2) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }
            String prefixID = line[0];
            String labID = line[1];
            Item lab = getItem(labID, "Lab", labs);
            Item prefix = getItem(prefixID, "FeaturePrefix", prefixes);
            lab.setReference("prefix", prefix);
        }
    }


    public void processLabs(Reader reader) throws Exception {
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

        processFile(reader, specSheet);
    }

}

