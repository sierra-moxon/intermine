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
public class CompanyConverter extends ZfinDirectoryConverter {
    //
    private static final String DATASET_TITLE = "Company";
    protected String organismRefId;
    private Map<String, Item> items = new HashMap<String, Item>(2700);

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
        this.directory = directory;
        try {
            processCompany("1company.txt");
            processPrefixCompany("company-feature-prefix-source.txt");
        } catch (IOException err) {
            throw new RuntimeException("error reading companyFile", err);
        }

        try {
            for (Item prefix : items.values())
                store(prefix);
        } catch (ObjectStoreException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            throw new Exception(sw.toString());
        }
    }

    private void processPrefixCompany(String fileName) throws Exception {
        SpecificationSheet specSheet = new SpecificationSheet();
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE, "prefix", "FeaturePrefix"));
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE));
        specSheet.setItemMap(items);
        specSheet.setFileName(fileName);
        processFile(specSheet);
    }

    public void processCompany(String fileName) throws Exception {
        SpecificationSheet specSheet = new SpecificationSheet();
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE));
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE, "name"));
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE, "contactPerson", "Person"));
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE, "url"));
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE, "email"));
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE, "fax"));
        specSheet.addColumnDefinition(new ColumnDefinition(DATASET_TITLE, "phone"));
        specSheet.setItemMap(items);
        specSheet.setFileName(fileName);
        processFile(specSheet);
    }

}

