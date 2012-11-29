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

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.intermine.bio.util.OrganismRepository;
import org.intermine.sql.Database;
import org.intermine.sql.DatabaseFactory;

/**
 * Create an IdResolver for Worm genes by querying tables in a WormBase
 * chado database.
 *
 * @author Richard Smith
 */
public class WormBaseChadoIdResolverFactory extends IdResolverFactory
{
    protected static final Logger LOG = Logger.getLogger(WormBaseChadoIdResolverFactory.class);

    private Database db;
    private final String propName = "db.wormbase";
    private final String taxonId = "6239";

    public WormBaseChadoIdResolverFactory() {
        this.clsCol = this.defaultClsCol;
    }
    
    /**
     * Construct with SO term of the feature type to read from chado database.
     * @param clsName the feature type to resolve
     */
    public WormBaseChadoIdResolverFactory(String clsName) {
        this.clsCol = new HashSet<String>(Arrays.asList(new String[] {clsName}));
    }

    /**
     * Build an IdResolver for WormBase by accessing a WormBase chado database.
     * @return an IdResolver for WormBase
     */
    @Override
    protected void createIdResolver() {
        if (resolver.hasTaxon(taxonId)) {
            return;
        }

        try {
            if (!restoreFromFile(this.clsCol)) {
                db = DatabaseFactory.getDatabase(propName);
                System.out .println("WormBaseIdResolver reading from database: " + db.getName());
                createFromDb(db);
                resolver.writeToFile(new File(ID_RESOLVER_CACHED_FILE_NAME));
                System.out .println("OntologyIdResolver caching in file: " 
                        + ID_RESOLVER_CACHED_FILE_NAME);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createFromDb(Database database) {
        Connection conn = null;
        OrganismRepository or = OrganismRepository.getOrganismRepository();
        try {
            conn = database.getConnection();
            String query = "select c.cvterm_id"
                + " from cvterm c, cv"
                + " where c.cv_id = cv.cv_id"
                + " and cv.name = \'sequence\'"
                + " and c.name =\'" + this.clsCol.iterator().next() + "\'";
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);
            String soTermId = null;
            res.next();
            soTermId = res.getString("cvterm_id");

            String orgConstraint = "";
            if (taxonId != null) {
                String abbrev = or.getOrganismDataByTaxon(new Integer(taxonId)).getAbbreviation();
                query = "select organism_id"
                    + " from organism"
                    + " where abbreviation = \'" + abbrev + "\'";
                LOG.info("QUERY: " + query);
                stmt = conn.createStatement();
                res = stmt.executeQuery(query);
                String organismId = null;
                res.next();
                organismId = res.getString("organism_id");
                stmt.close();
                orgConstraint = " and o.organism_id = " + organismId;
            }

            // fetch feature name for located genes
            query = "select distinct o.abbreviation, f.uniquename, f.name"
                + " from feature f, featureloc l, organism o"
                + " where f.organism_id = o.organism_id"
                + " and f.is_obsolete = false"
                + " and f.type_id = " + soTermId
                + " and l.feature_id = f.feature_id"
                + orgConstraint;
            LOG.info("QUERY: " + query);
            stmt = conn.createStatement();
            res = stmt.executeQuery(query);
            int i = 0;
            while (res.next()) {
                String uniquename = res.getString("uniquename");
                String name = res.getString("name");
                String organism = res.getString("abbreviation");
                String taxId = "" + or.getOrganismDataByAbbreviation(organism).getTaxonId();
                resolver.addMainIds(taxId, uniquename, Collections.singleton(name));
                i++;
            }
            LOG.info("feature query returned " + i + " rows.");
            stmt.close();

            // fetch gene synonyms
            query = "select distinct o.abbreviation, f.uniquename, s.name, "
                + " fs.is_current, c.name as type"
                + " from feature f, feature_synonym fs, synonym s,"
                + " organism o, cvterm c"
                + " where f.organism_id = o.organism_id"
                + " and f.is_obsolete = false"
                + " and f.type_id = " + soTermId
                + " and fs.feature_id = f.feature_id "
                + " and fs.synonym_id = s.synonym_id"
                + " and s.type_id = c.cvterm_id"
                + orgConstraint;
            LOG.info("QUERY: " + query);
            stmt = conn.createStatement();
            res = stmt.executeQuery(query);
            i = 0;
            while (res.next()) {
                String uniquename = res.getString("uniquename");
                String synonym = res.getString("name");
                String organism = res.getString("abbreviation");
                String taxId = "" + or.getOrganismDataByAbbreviation(organism).getTaxonId();
                boolean isCurrent = res.getBoolean("is_current");
                String type = res.getString("type");
                if (isCurrent && "symbol".equals(type)) {
                    resolver.addMainIds(taxId, uniquename, Collections.singleton(synonym));
                } else {
                    resolver.addSynonyms(taxId, uniquename, Collections.singleton(synonym));
                }
            }
            stmt.close();
            LOG.info("synonym query returned " + i + " rows.");

        } catch (Exception e) {
            LOG.error(e);
            throw new RuntimeException(e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
