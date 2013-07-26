package org.intermine.bio.dataconversion;

/*
 * Copyright (C) 2002-2009 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import org.intermine.xml.full.Item;
import org.zfin.intermine.dataconversion.ColumnDefinition;
import org.zfin.intermine.dataconversion.SpecificationSheet;
import org.zfin.intermine.dataconversion.ZfinDirectoryConverter;

import java.io.*;
import java.util.*;


/**
 * DataConverter to load ZFIN feature identifiers from text files
 */
public class SourceFeaturePrefixConverter {

    private Map<String, Item> featurePrefix = new HashMap();
    public static final String ITEM_NAME = "FeaturePrefix";
    private ZfinDirectoryConverter converter;

    public SourceFeaturePrefixConverter(ZfinDirectoryConverter converter) {
        this.converter = converter;
    }

    public void process(File directory) throws Exception {
        File featurePrefixes = new File(directory.getCanonicalPath() + "/feature-prefix.txt");
        System.out.println("canonical path: " + directory.getCanonicalPath());
        processFeatures(new FileReader(featurePrefixes));


    }

    public void processFeatures(Reader reader) throws Exception {

        SpecificationSheet specSheet = new SpecificationSheet();
        specSheet.addColumnDefinition(new ColumnDefinition(ITEM_NAME, ColumnDefinition.PRIMARY_IDENTIFIER));
        specSheet.addColumnDefinition(new ColumnDefinition(ITEM_NAME, "name"));
        specSheet.addColumnDefinition(new ColumnDefinition(ITEM_NAME, "instituteName"));
        specSheet.setItemMap(featurePrefix);
        converter.processFile(reader, specSheet);
    }

    public Map<String, Item> getFeaturePrefix() {
        return featurePrefix;
    }
}
