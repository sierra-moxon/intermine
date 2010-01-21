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

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.intermine.bio.util.OrganismRepository;
import org.intermine.sql.Database;
import org.intermine.sql.DatabaseFactory;

/**
 * Create an IdResolver for Worm genes by querying tables in a WormBase
 * chado database.
 * @author Richard Smith
 *
 */
public class WormBaseChadoIdResolverFactory extends IdResolverFactory
{
    protected static final Logger LOG = Logger.getLogger(WormBaseChadoIdResolverFactory.class);
    private Database db;
    private String soTerm = null, taxonId = null;


    /**
     * Construct with SO term of the feature type to read from chado database.
     * @param soTerm the feature type to resolve
     */
    public WormBaseChadoIdResolverFactory(String soTerm) {
        this.soTerm = soTerm;
    }

    
    /**
     * Construct with SO term of the feature type to read from chado database and restrict to
     * data from only one organism.
     * @param soTerm the feature type to resolve
     * @param taxonId taxon id of the organism to resolve identifiers for
     */
    public WormBaseChadoIdResolverFactory(String soTerm, String taxonId) {
        this.soTerm = soTerm;
        this.taxonId = taxonId;
    }
    
    
    /**
     * Build an IdResolver for WormBase by accessing a WormBase chado database.
     * @return an IdResolver for WormBase
     */
    protected IdResolver createIdResolver() {
        IdResolver resolver = new IdResolver(soTerm);
        
        try {
            // TODO maybe this shouldn't be hard coded here?
            db = DatabaseFactory.getDatabase("db.wormbase");

            String cacheFileName = "build/" + db.getName() + "." + soTerm 
                + ((taxonId != null) ? "." + taxonId : "");
            File f = new File(cacheFileName);
            if (f.exists()) {
                System.out .println("WormBaseIdResolver reading from cache file: " + cacheFileName);
                resolver = createFromFile(soTerm, f);
            } else {
                resolver = createFromDb(db);
                resolver.writeToFile(f);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return resolver;
    }
    
    
    private IdResolver createFromDb(Database db) {
        IdResolver resolver = new IdResolver(soTerm);
        Connection conn = null;
        OrganismRepository or = OrganismRepository.getOrganismRepository();
        
        
        try {
            conn = db.getConnection();
            String query = "select c.cvterm_id"
                + " from cvterm c, cv"
                + " where c.cv_id = cv.cv_id"
                + " and cv.name = \'sequence\'"
                + " and c.name =\'" + soTerm + "\'";
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
                String taxonId = "" + or.getOrganismDataByAbbreviation(organism).getTaxonId();
                resolver.addMainIds(taxonId, uniquename, Collections.singleton(name));
                i++;
            }
            LOG.info("feature query returned " + i + " rows.");
            stmt.close();
            
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
        
        return resolver;
    }
}
