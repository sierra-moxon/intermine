package org.intermine.bio.dataconversion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.intermine.util.PropertiesUtil;

public class EntrezGeneIdResolverFactory {
    protected static final Logger LOG = Logger.getLogger(HgncIdResolverFactory.class);
    private final String clsName = "gene";
    private final String propName = "resolver.entrez.file";
    private final String taxonId = "9606";

    /**
     * Build an IdResolver from Entrez Gene gene_info file
     * @return an IdResolver for Entrez Gene
     */
    protected IdResolver createIdResolver() {
        Properties props = PropertiesUtil.getProperties();
        String fileName = props.getProperty(propName);

        if (StringUtils.isBlank(fileName)) {
            String message = "Entrez gene resolver has no file name specified, set " + propName
                + " to the location of the gene_info file.";
            throw new IllegalArgumentException(message);
        }

        IdResolver resolver;
        BufferedReader reader;
        try {
            FileReader fr = new FileReader(new File(fileName));
            reader = new BufferedReader(fr);
            resolver = createFromFile(reader);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Failed to open gene_info file: "
                    + fileName, e);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error reading from gene_info file: "
                    + fileName, e);
        }

        return resolver;
    }

    private IdResolver createFromFile(BufferedReader reader) throws IOException {
        IdResolver resolver = new IdResolver(clsName);

        NcbiGeneInfoParser parser = new NcbiGeneInfoParser(reader);
        Map<String, Set<GeneInfoRecord>> records = parser.getGeneInfoRecords();
        if (records == null) {
            throw new IllegalArgumentException("Failed to read any records from gene_info file.");
        }
        if (!records.containsKey(taxonId)) {
            throw new IllegalArgumentException("No records in gene_info file for taxon: " 
                   + taxonId);
        }
        for (GeneInfoRecord record : records.get(taxonId)) {
            resolver.addMainIds(taxonId, record.entrez, record.getMainIds());
            resolver.addSynonyms(taxonId, record.entrez, record.ensemblIds);
            resolver.addSynonyms(taxonId, record.entrez, record.synonyms);
        }
        return resolver;
    }
}
