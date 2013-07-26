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
import org.zfin.intermine.dataconversion.ZfinDirectoryConverter;


/**
 * @author Sierra
 * @author Christian
 */
public class LabsConverter extends ZfinDirectoryConverter {
    //
    private static final String DATASET_TITLE = "Labs";
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
            prefix.addToCollection("labs", lab);
        }
    }


    public void processLabs(Reader reader) throws Exception {
        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');
        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 3) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }
            String primaryIdentifier = line[0];
            String name = line[1];
            String contactPersonID = line[2];
            Item lab;
            if (!StringUtils.isEmpty(primaryIdentifier)) {
                lab = getItem(primaryIdentifier, "Lab", labs);
                if (!StringUtils.isEmpty(name)) {
                    lab.setAttribute("name", name);
                }
                if (!StringUtils.isEmpty(contactPersonID)) {
                    lab.setReference("contactPerson", getPerson(contactPersonID));
                }
            }
        }
    }

    private Item getPerson(String primaryIdentifier)
            throws SAXException {
        Item item = persons.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Person");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            persons.put(primaryIdentifier, item);
            try {
                store(item);
            } catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }
        return item;

    }
}

