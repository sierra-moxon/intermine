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
 * @author Prita
 */
public class PersonConverter extends ZfinDirectoryConverter {
    //
    private static final String DATASET_TITLE = "Person";
    protected String organismRefId;
    private Map<String, Item> persons = new HashMap<String, Item>(900);
    private Map<String, Item> labs = new HashMap<String, Item>(900);
    private Map<String, Item> companies = new HashMap<String, Item>(100);
    private Map<String, Item> pubs = new HashMap<String, Item>(900);

    /**
     * Constructor
     *
     * @param writer the ItemWriter used to handle the resultant items
     * @param model  the Model
     */

    public PersonConverter(ItemWriter writer, Model model) {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
    }

    public void process(File directory) throws Exception {
        this.directory = directory;
        try {
            processPerson("1person.txt");
            File personAssociationFile = new File(directory.getCanonicalPath() + "/person_associations.txt");
            processPersonAssociation(new FileReader(personAssociationFile));
        } catch (IOException err) {
            throw new RuntimeException("error reading personFile", err);
        }

        try {
            for (Item lab : labs.values())
                store(lab);
            for (Item publication : pubs.values())
                store(publication);
            for (Item company : companies.values())
                store(company);
            for (Item person : persons.values())
                store(person);
        } catch (ObjectStoreException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            throw new Exception(sw.toString());
        }
    }

    private void processPersonAssociation(FileReader reader) throws Exception {
        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');
        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            String pkID = line[0];
            if (line.length < 2) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + pkID);
            }
            Item person = getItem(pkID, "Person", persons);
            String sourceID = line[1];
            if (sourceID.startsWith("ZDB-LAB")) {
                Item lab = getItem(sourceID, "Lab", labs);
                person.addToCollection("labs", lab);
            }
            if (sourceID.startsWith("ZDB-PUB")) {
                Item publication = getItem(sourceID, "Publication", pubs);
                person.addToCollection("publications", publication);
            }
            if (sourceID.startsWith("ZDB-COMPANY")) {
                Item company = getItem(sourceID, "Company", companies);
                person.addToCollection("companies", company);
            }
        }
    }

    public void processPerson(String fileName) throws Exception {
        SpecificationSheet specSheet = new SpecificationSheet();
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE));
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE, "firstName"));
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE, "lastName"));
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE, "fullName"));
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE, "email"));
        specSheet.addItemMap(DATASET_TITLE, persons);
        specSheet.setFileName(fileName);
        processFile(specSheet);
    }

}

