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
            processLabs(new FileReader(labFile));
        } catch (IOException err) {
            throw new RuntimeException("error reading labFile", err);
        }

        try {
            for (Item lab : labs.values()) {
                store(lab);
            }
        } catch (ObjectStoreException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            throw new Exception(sw.toString());
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
            String contactPerson = line[2];
            System.out.println("Lab ID: " + primaryIdentifier);
            System.out.println("Lab name: " + name);
            System.out.println("Lab: contactPerson" + contactPerson);
            Item lab;
            if (!StringUtils.isEmpty(primaryIdentifier)) {
                lab = getLab(primaryIdentifier);
                if (!StringUtils.isEmpty(name)) {
                    lab.setAttribute("name", name);
                }
                if (!StringUtils.isEmpty(contactPerson)) {
                    lab.setAttribute("contactPerson", contactPerson);
                }
            }
        }
    }

    private Item getLab(String primaryIdentifier)
            throws SAXException {
        Item item = labs.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Lab");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            labs.put(primaryIdentifier, item);
        }
        return item;
    }
}

