package org.intermine.bio.dataconversion;

/*
 * Copyright (C) 2002-2012 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.intermine.util.FormattedTextParser;
import org.intermine.util.PropertiesUtil;

/**
 * Create an IdResolver for HGNC previous symbols and aliases to current symbols.
 *
 * @author Richard Smith
 */
public class HgncIdResolverFactory extends IdResolverFactory
{
    protected static final Logger LOG = Logger.getLogger(HgncIdResolverFactory.class);
    private final String propName = "resolver.hgnc.file";
    private final String taxonId = "9606";

    /**
     * Construct without SO term of the feature type.
     * @param soTerm the feature type to resolve
     */
    public HgncIdResolverFactory() {
        this.clsCol = this.defaultClsCol;
    }

    /**
     * Build an IdResolver for HGNC.
     * @return an IdResolver for FlyBase
     */
    @Override
    protected void createIdResolver() {
        if (resolver.hasTaxon(taxonId)) {
            return;
        }
        
        try {
            if (!retrieveFromFile(this.clsCol)) {
                Properties props = PropertiesUtil.getProperties();
                String fileName = props.getProperty(propName);

                if (StringUtils.isBlank(fileName)) {
                    String message = "HGNC resolver has no file name specified, set " + propName
                        + " to the file location.";
                    LOG.warn(message);
                    return;
                }

                try {
                    createFromFile(new BufferedReader(new FileReader(new File(fileName))));
                } catch (FileNotFoundException e) {
                    throw new IllegalArgumentException("Failed to open HGNC identifiers file: "
                            + fileName, e);
                } catch (IOException e) {
                    throw new IllegalArgumentException("Error reading from HGNC identifiers file: "
                            + fileName, e);
                }
                
                try {
                    resolver.writeToFile(new File(ID_RESOLVER_CACHED_FILE_NAME));
                    System.out. println("Written cache file: " + ID_RESOLVER_CACHED_FILE_NAME);
                } catch (IOException e) {
                    throw new IllegalArgumentException("Error writing resolver cache file: "
                            + ID_RESOLVER_CACHED_FILE_NAME, e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createFromFile(BufferedReader reader) throws IOException {
        // HGNC ID | Approved Symbol | Approved Name | Status | Previous Symbols | Aliases
        Iterator<?> lineIter = FormattedTextParser.parseTabDelimitedReader(reader);
        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            String symbol = line[1];

            resolver.addMainIds(taxonId, symbol, Collections.singleton(symbol));
            addSynonyms(resolver, symbol, line[4]);
            addSynonyms(resolver, symbol, line[5]);
        }
    }

    private void addSynonyms(IdResolver resolver, String symbol, String ids) {
        if (!StringUtils.isBlank(ids)) {
            Set<String> synonyms = new HashSet<String>();
            for (String alias : ids.split(",")) {
                synonyms.add(alias.trim());
            }
            resolver.addSynonyms(taxonId, symbol, synonyms);
        }
    }
}
