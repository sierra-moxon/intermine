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
public class CompanyConverter extends ZfinDirectoryConverter {
    //
    private static final String DATASET_TITLE = "Company";
    protected String organismRefId;
    private Map<String, Item> companies = new HashMap<String, Item>(900);
    private Map<String, Item> persons = new HashMap<String, Item>(900);

    /**
     * Constructor
     *
     * @param writer the ItemWriter used to handle the resultant items
     * @param model  the Model
     */

    public CompanyConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }

    public void process(File directory) throws Exception {
        try {
            System.out.println("canonical path: " + directory.getCanonicalPath());
            File companyFile = new File(directory.getCanonicalPath() + "/1company.txt");
            processCompany(new FileReader(companyFile));
        } catch (IOException err) {
            throw new RuntimeException("error reading companyFile", err);
        }

        try {
            for (Item company : companies.values()) {
                store(company);
            }
        } catch (ObjectStoreException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            throw new Exception(sw.toString());
        }
    }

    public void processCompany(Reader reader) throws Exception {
        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');
        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 3) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }
            String primaryIdentifier = line[0];
            String name = line[1];
            String contactPerson = line[2];
            Item company;
            if (!StringUtils.isEmpty(primaryIdentifier)) {
                company = getCompany(primaryIdentifier);
                if (!StringUtils.isEmpty(name)) {
                    company.setAttribute("name", name);
                }
                if (!StringUtils.isEmpty(contactPerson)) {
                    company.setAttribute("contactPerson", contactPerson);
                    //lab.setReference("contactPerson", getPerson(contactPerson));
                }
            }
        }
    }

    private Item getCompany(String primaryIdentifier)
            throws SAXException {
        Item item = companies.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Company");
            item.setAttribute("primaryIdentifier", primaryIdentifier);
            companies.put(primaryIdentifier, item);
        }
        return item;

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

