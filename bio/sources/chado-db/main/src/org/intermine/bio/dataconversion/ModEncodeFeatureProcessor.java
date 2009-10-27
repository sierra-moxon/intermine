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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.log4j.Logger;
import org.intermine.bio.chado.config.ConfigAction;
import org.intermine.bio.chado.config.SetFieldConfigAction;
import org.intermine.bio.util.OrganismData;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.util.StringUtil;
import org.intermine.util.TypeUtil;
import org.intermine.xml.full.Attribute;
import org.intermine.xml.full.Item;

/**
 * A processor that loads feature referred to by the modENCODE metadata.  This class is designed
 * to be used by ModEncodeMetaDataProcessor and will be called once for each submission that has
 * metadata.
 * @author Kim Rutherford
 */
public class ModEncodeFeatureProcessor extends SequenceProcessor
{
    private static final Logger LOG = Logger.getLogger(ModEncodeFeatureProcessor.class);

    private final String dataSetIdentifier;
    private final String dataSourceIdentifier;
    private final List<Integer> dataList;
    private Set<String> chromosomeInterMineTypes = new HashSet<String>();

    private static final String SUBFEATUREID_TEMP_TABLE_NAME = "modmine_subfeatureid_temp";


    // feature type to query from the feature table
    private static final List<String> FEATURES = Arrays.asList(
         "gene", "mRNA", "transcript",
         "CDS", "intron", "exon", "EST",
         "five_prime_untranslated_region",
         "five_prime_UTR", "three_prime_untranslated_region",
         "three_prime_UTR", "origin_of_replication",
         "binding_site", "protein_binding_site", "TF_binding_site",
         "transcript_region", "histone_binding_site", "copy_number_variation",
         "natural_transposable_element", "start_codon", "stop_codon"
         , "cDNA"
         , "three_prime_RACE_clone", "three_prime_RST", "three_prime_UST"
         , "polyA_site", "overlapping_EST_set", "exon_region"
         , "experimental_feature", "SL1_acceptor_site", "SL2_acceptor_site"
         , "transcription_end_site", "TSS"
    );



    // the configuration for this processor, set when getConfig() is called the first time
    private final Map<Integer, MultiKeyMap> config = new HashMap();

    private Map<Integer, FeatureData> commonFeaturesMap = new HashMap<Integer, FeatureData>();

    /**
     * Create a new ModEncodeFeatureProcessor.
     * @param chadoDBConverter     the parent converter
     * @param dataSetIdentifier    the item identifier of the DataSet,
     *                             i.e. the submissionItemIdentifier
     * @param dataSourceIdentifier the item identifier of the DataSource,
     *                             i.e. the labItemIdentifier
     * @param dataList             the list of data ids to be used in the subquery
     */

    public ModEncodeFeatureProcessor(ChadoDBConverter chadoDBConverter,
            String dataSetIdentifier, String dataSourceIdentifier,
            List <Integer> dataList) {
        super(chadoDBConverter);
        this.dataSetIdentifier = dataSetIdentifier;
        this.dataSourceIdentifier = dataSourceIdentifier;
        this.dataList = dataList;
        for (String chromosomeType : getChromosomeFeatureTypes()) {
            chromosomeInterMineTypes.add(TypeUtil.javaiseClassName(fixFeatureType(chromosomeType)));
        }
        chromosomeInterMineTypes.add("Gene");
        chromosomeInterMineTypes.add("MRNA");
    }

    /**
     * Get a list of the chado/so types of the LocatedSequenceFeatures we wish to load.  The list
     * will not include chromosome-like features.
     * @return the list of features
     */
    @Override
    protected List<String> getFeatures() {
        return FEATURES;
    }


    /**
     * Get a map of features that are expected to be common between submissions.  This map can be
     * used to initialise this processor for a subsequent run.  The feature types added to this map
     * are governed by the addToFeatureMap method in this class.
     * @return a map of chado feature id to FeatureData objects
     */
    protected Map<Integer, FeatureData> getCommonFeaturesMap() {
        return commonFeaturesMap;
    }


    /**
     * Initialise SequenceProcessor with features that have already been processed and put the same
     * features data into a commonFeaturesMap that tracks features (e.g. Chromosomes) that appear
     * in multiple submissions but should only be processed once.
     * @param initialMap map of chado feature id to FeatureData objects
     */
    protected void initialiseCommonFeatures(Map<Integer, FeatureData> initialMap) {
        super.initialiseFeatureMap(initialMap);
        commonFeaturesMap.putAll(initialMap);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected String getExtraFeatureConstraint() {
        /*
         * tried also other queries (using union, without join), this seems better
         */

        return "(cvterm.name = 'chromosome' OR cvterm.name = 'chromosome_arm') AND "
        + " feature_id IN ( SELECT featureloc.srcfeature_id "
        + " FROM featureloc, " + SUBFEATUREID_TEMP_TABLE_NAME
        + " WHERE featureloc.feature_id = " + SUBFEATUREID_TEMP_TABLE_NAME + ".feature_id) "
        + " OR feature_id IN ( SELECT feature_id "
        + " FROM " + SUBFEATUREID_TEMP_TABLE_NAME + " ) ";

/*        return "(cvterm.name = 'chromosome' OR cvterm.name = 'chromosome_arm') AND "
        + " feature_id IN ( SELECT featureloc.srcfeature_id "
        + " FROM featureloc "
        + " WHERE featureloc.feature_id IN ( SELECT feature_id "
        + " FROM " + SUBFEATUREID_TEMP_TABLE_NAME + " )) "
        + " OR feature_id IN ( SELECT feature_id "
        + " FROM " + SUBFEATUREID_TEMP_TABLE_NAME + " ) ";
*/

    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void extraProcessing(Connection connection, Map<Integer, FeatureData> featureDataMap)
                    throws ObjectStoreException, SQLException {

        // process indirect locations via match features and featureloc feature<->match<->feature
        ResultSet matchLocRes = getMatchLocResultSet(connection);
        processLocationTable(connection, matchLocRes);

        ResultSet matchESTLocRes = getESTMatchLocResultSet(connection);
        processLocationTable(connection, matchESTLocRes);

        // for piano (sub 515)
        // Note: UST gives no results (?)
        // TODO: check, and run only if necessary
        // removed for build (none present)
//        ResultSet matchUSTLocRes = getUSTMatchLocResultSet(connection);
//        processLocationTable(connection, matchUSTLocRes);
//
//        ResultSet matchRSTLocRes = getRSTMatchLocResultSet(connection);
//        processLocationTable(connection, matchRSTLocRes);


        processFeatureScores(connection);
    }

    /**
     * Override method that adds completed features to featureMap.  Also put features that will
     * appear in multiple submissions in a map made available at the end of processing.
     * @param featureId the chado feature id
     * @param fdat feature information
     */
    protected void addToFeatureMap(Integer featureId, FeatureData fdat) {
        super.addToFeatureMap(featureId, fdat);
        // We know chromosomes will be common between submissions so add them here
        if (chromosomeInterMineTypes.contains(fdat.getInterMineType())
                && !commonFeaturesMap.containsKey(featureId)) {
            commonFeaturesMap.put(featureId, fdat);
        }
    }


    //
    // TODO: make a function getMatchLocResultSet(connection, EST, EST_match)
    //

    /**
     * Return the interesting EST matches from the featureloc and feature tables.
     * feature<->featureloc<->match_feature<->featureloc<->feature
     * This is a protected method so that it can be overriden for testing
     * @param connection the db connection
     * @return the SQL result set
     * @throws SQLException if a database problem occurs
     */
    protected ResultSet getESTMatchLocResultSet(Connection connection) throws SQLException {
        String query =
            "SELECT -1 AS featureloc_id, est.feature_id, chrloc.fmin, "
            + " chrloc.srcfeature_id AS srcfeature_id, chrloc.fmax, FALSE AS is_fmin_partial, "
            + " estloc.strand "
            + " FROM feature est, featureloc estloc, cvterm estcv, feature mf, "
            + " cvterm mfcv, featureloc chrloc, feature chr, cvterm chrcv "
            + " WHERE est.type_id = estcv.cvterm_id "
            + " AND estcv.name = 'EST' "
            + " AND est.feature_id = estloc.srcfeature_id "
            + " AND estloc.feature_id = mf.feature_id "
            + " AND mf.feature_id = chrloc.feature_id "
            + " AND chrloc.srcfeature_id = chr.feature_id "
            + " AND chr.type_id = chrcv.cvterm_id "
            + " AND chrcv.name = 'chromosome' "
            + " AND mf.type_id = mfcv.cvterm_id "
            + " AND mfcv.name = 'EST_match' "
            + " AND est.feature_id IN "
            + " (select feature_id from " + SUBFEATUREID_TEMP_TABLE_NAME + " ) ";
        LOG.info("executing: " + query);
        long bT = System.currentTimeMillis();
        Statement stmt = connection.createStatement();
        ResultSet res = stmt.executeQuery(query);
        LOG.info("TIME QUERYING ESTMATCH " + ":" + (System.currentTimeMillis() - bT));
        return res;
    }

    /**
     * Return the interesting UST matches from the featureloc and feature tables.
     * feature<->featureloc<->match_feature<->featureloc<->feature
     * This is a protected method so that it can be overriden for testing
     * @param connection the db connection
     * @return the SQL result set
     * @throws SQLException if a database problem occurs
     */
    protected ResultSet getUSTMatchLocResultSet(Connection connection) throws SQLException {
        String query =
            "SELECT -1 AS featureloc_id, ust.feature_id, chrloc.fmin, "
            + " chrloc.srcfeature_id AS srcfeature_id, chrloc.fmax, FALSE AS is_fmin_partial, "
            + " ustloc.strand "
            + " FROM feature ust, featureloc ustloc, cvterm ustcv, feature mf, "
            + " cvterm mfcv, featureloc chrloc, feature chr, cvterm chrcv "
            + " WHERE ust.type_id = ustcv.cvterm_id "
            + " AND ustcv.name = 'three_prime_UST' "
            + " AND ust.feature_id = ustloc.srcfeature_id "
            + " AND ustloc.feature_id = mf.feature_id "
            + " AND mf.feature_id = chrloc.feature_id "
            + " AND chrloc.srcfeature_id = chr.feature_id "
            + " AND chr.type_id = chrcv.cvterm_id "
            + " AND chrcv.name = 'chromosome' "
            + " AND mf.type_id = mfcv.cvterm_id "
            + " AND mfcv.name = 'UST_match' "
            + " AND ust.feature_id IN "
            + " (select feature_id from " + SUBFEATUREID_TEMP_TABLE_NAME + " ) ";
        LOG.info("executing: " + query);
        long bT = System.currentTimeMillis();
        Statement stmt = connection.createStatement();
        ResultSet res = stmt.executeQuery(query);
        LOG.info("TIME QUERYING USTMATCH " + ":" + (System.currentTimeMillis() - bT));
        return res;
    }

    /**
     * Return the interesting RST matches from the featureloc and feature tables.
     * feature<->featureloc<->match_feature<->featureloc<->feature
     * This is a protected method so that it can be overriden for testing
     * @param connection the db connection
     * @return the SQL result set
     * @throws SQLException if a database problem occurs
     */
    protected ResultSet getRSTMatchLocResultSet(Connection connection) throws SQLException {
        String query =
            "SELECT -1 AS featureloc_id, rst.feature_id, chrloc.fmin, "
            + " chrloc.srcfeature_id AS srcfeature_id, chrloc.fmax, FALSE AS is_fmin_partial, "
            + " rstloc.strand "
            + " FROM feature rst, featureloc rstloc, cvterm rstcv, feature mf, "
            + " cvterm mfcv, featureloc chrloc, feature chr, cvterm chrcv "
            + " WHERE rst.type_id = rstcv.cvterm_id "
            + " AND rstcv.name = 'three_prime_RST' "
            + " AND rst.feature_id = rstloc.srcfeature_id "
            + " AND rstloc.feature_id = mf.feature_id "
            + " AND mf.feature_id = chrloc.feature_id "
            + " AND chrloc.srcfeature_id = chr.feature_id "
            + " AND chr.type_id = chrcv.cvterm_id "
            + " AND chrcv.name = 'chromosome' "
            + " AND mf.type_id = mfcv.cvterm_id "
            + " AND mfcv.name = 'RST_match' "
            + " AND rst.feature_id IN "
            + " (select feature_id from " + SUBFEATUREID_TEMP_TABLE_NAME + " ) ";
        LOG.info("executing: " + query);
        long bT = System.currentTimeMillis();
        Statement stmt = connection.createStatement();
        ResultSet res = stmt.executeQuery(query);
        LOG.info("TIME QUERYING RSTMATCH " + ":" + (System.currentTimeMillis() - bT));
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Integer store(Item feature, int taxonId) throws ObjectStoreException {
        processItem(feature, taxonId);
        Integer itemId = super.store(feature, taxonId);
        return itemId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Item makeLocation(int start, int end, int strand, FeatureData srcFeatureData,
                              FeatureData featureData, int taxonId) throws ObjectStoreException {
        Item location =
            super.makeLocation(start, end, strand, srcFeatureData, featureData, taxonId);
        processItem(location, taxonId);
        return location;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Item createSynonym(FeatureData fdat, String type, String identifier,
                                 boolean isPrimary, List<Item> otherEvidence)
        throws ObjectStoreException {
        // Don't create synonyms for main identifiers of modENCODE features.  There are too many and
        // not useful to quick search.
        if (isPrimary) {
            return null;
        }
        Item synonym = super.createSynonym(fdat, type, identifier, isPrimary, otherEvidence);
        OrganismData od = fdat.getOrganismData();
        processItem(synonym, od.getTaxonId());
        return synonym;
    }

    /**
     * Method to add dataSets and DataSources to items before storing
     */
    private void processItem(Item item, Integer taxonId) {
        if (item.getClassName().equals("DataSource")
            || item.getClassName().equals("DataSet")
            || item.getClassName().equals("Organism")
            || item.getClassName().equals("Sequence")) {
            return;
        }

        if (taxonId == null) {
            ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
            ClassLoader classLoader = getClass().getClassLoader();
            Thread.currentThread().setContextClassLoader(classLoader);
            try {
                throw new RuntimeException("getCurrentTaxonId() returned null while processing "
                                           + item);
            } finally {
                Thread.currentThread().setContextClassLoader(currentClassLoader);
            }
        } else {
            DataSetStoreHook.setDataSets(getModel(), item, dataSetIdentifier, dataSourceIdentifier);
        }
    }



    /**
     * {@inheritDoc}
     *
     * see FlyBaseProcessor for many more examples of configuration
     */
    @Override
    protected Map<MultiKey, List<ConfigAction>> getConfig(int taxonId) {
        MultiKeyMap map = config.get(new Integer(taxonId));
        if (map == null) {
            map = new MultiKeyMap();
            config.put(new Integer(taxonId), map);

            // TODO: check possible conflicts with our sql matching
            // map.put(new MultiKey("relationship", "ESTMatch", "evidence_for_feature", "Intron"),
            //        Arrays.asList(new SetFieldConfigAction("intron")));


            // for sub 515
            map.put(new MultiKey("relationship", "ThreePrimeUTR", "adjacent_to", "CDS"),
                    Arrays.asList(new SetFieldConfigAction("CDS")));

            map.put(new MultiKey("relationship", "PolyASite",
                    "derives_from", "ThreePrimeRACEClone"),
                    Arrays.asList(new SetFieldConfigAction("threePrimeRACEClone")));

            map.put(new MultiKey("relationship", "ThreePrimeRST",
                    "derives_from", "ThreePrimeRACEClone"),
                    Arrays.asList(new SetFieldConfigAction("threePrimeRACEClone")));

            map.put(new MultiKey("relationship", "ThreePrimeUST",
                    "complete_evidence_for_feature", "ThreePrimeUTR"),
                    Arrays.asList(new SetFieldConfigAction("threePrimeUTR")));

            // for sub 35
            map.put(new MultiKey("relationship", "OverlappingESTSet",
                    "full_evidence_for_feature", "Gene"),
                    Arrays.asList(new SetFieldConfigAction("gene")));

            map.put(new MultiKey("relationship", "OverlappingESTSet",
                    "full_evidence_for_feature", "MRNA"),
                    Arrays.asList(new SetFieldConfigAction("mRNA")));

            map.put(new MultiKey("relationship", "OverlappingESTSet",
                    "partial_evidence_for_feature", "MRNA"),
                    Arrays.asList(new SetFieldConfigAction("mRNA")));

            // 433
            map.put(new MultiKey("relationship", "OverlappingESTSet",
                    "full_evidence_for_feature", "Gene"),
                    Arrays.asList(new SetFieldConfigAction("gene")));

            map.put(new MultiKey("relationship", "OverlappingESTSet",
                    "complete_evidence_for_feature", "Intron"),
                    Arrays.asList(new SetFieldConfigAction("intron")));

            map.put(new MultiKey("relationship", "OverlappingESTSet",
                    "complete_evidence_for_feature", "PolyASite"),
                    Arrays.asList(new SetFieldConfigAction("polyASite")));

            map.put(new MultiKey("relationship", "OverlappingESTSet",
                    "complete_evidence_for_feature", "SL1AcceptorSite"),
                    Arrays.asList(new SetFieldConfigAction("SL1AcceptorSite")));

            map.put(new MultiKey("relationship", "OverlappingESTSet",
                    "complete_evidence_for_feature", "SL2AcceptorSite"),
                    Arrays.asList(new SetFieldConfigAction("SL2AcceptorSite")));

            map.put(new MultiKey("relationship", "OverlappingESTSet",
                    "complete_evidence_for_feature", "TranscriptionEndSite"),
                    Arrays.asList(new SetFieldConfigAction("transcriptionEndSite")));

            map.put(new MultiKey("relationship", "OverlappingESTSet",
                    "evidence_for_feature", "TranscriptRegion"),
                    Arrays.asList(new SetFieldConfigAction("transcriptRegion")));

            map.put(new MultiKey("relationship", "OverlappingESTSet",
                    "complete_evidence_for_feature", "TSS"),
                    Arrays.asList(new SetFieldConfigAction("TSS")));

            map.put(new MultiKey("relationship", "ExperimentalFeature",
                    "evidence_for_feature", "Transcript"),
                    Arrays.asList(new SetFieldConfigAction("transcript")));

            map.put(new MultiKey("relationship", "ExperimentalFeature",
                    "evidence_for_feature", "Exon"),
                    Arrays.asList(new SetFieldConfigAction("exon")));

        }
        return map;
    }




    /**
     * copied from FlyBaseProcessor
     * {@inheritDoc}
     */
    @Override
    protected Item makeFeature(Integer featureId, String chadoFeatureType, String interMineType,
                               String name, String uniqueName,
                               int seqlen, int taxonId) {
        String realInterMineType = interMineType;

        if (chadoFeatureType.equals("chromosome_arm")
                || chadoFeatureType.equals("ultra_scaffold")) {
                realInterMineType = "Chromosome";
            }

        Item feature = getChadoDBConverter().createItem(realInterMineType);

        return feature;
    }



    /**
     * method to transform dataList (list of integers)
     * in the string for a IN clause in SQL (comma separated)
     * @return String
     */
    protected String forINclause() {

        StringBuffer ql = new StringBuffer();

        Iterator<Integer> i = dataList.iterator();
        Integer index = 0;
        while (i.hasNext()) {
          index++;
          if (index > 1) {
              ql = ql.append(", ");
          }
            ql = ql.append(i.next());
        }
        return ql.toString();
    }

    /**
     * Create a temporary table of all feature_ids for a given submission.
     * @param connection the connection
     * @throws SQLException if there is a database problem
     */
    protected void createSubFeatureIdTempTable(Connection connection) throws SQLException {
        String queryList = forINclause();

        String query =
            " CREATE TEMPORARY TABLE " + SUBFEATUREID_TEMP_TABLE_NAME
            + " AS SELECT data_feature.feature_id "
            + " FROM data_feature "
            + " WHERE data_id IN (" + queryList + ")";

        Statement stmt = connection.createStatement();
        LOG.info("executing: " + query);
        long bT = System.currentTimeMillis();
        stmt.execute(query);
        LOG.info("TIME CREATING TEMP FEAT TABLE " + ":" + (System.currentTimeMillis() - bT));
        String idIndexQuery = "CREATE INDEX " + SUBFEATUREID_TEMP_TABLE_NAME + "_feature_index ON "
            + SUBFEATUREID_TEMP_TABLE_NAME + "(feature_id)";
        LOG.info("executing: " + idIndexQuery);
        long bT1 = System.currentTimeMillis();
        stmt.execute(idIndexQuery);
        LOG.info("TIME CREATING INDEX " + ":" + (System.currentTimeMillis() - bT1));
        String analyze = "ANALYZE " + SUBFEATUREID_TEMP_TABLE_NAME;
        LOG.info("executing: " + analyze);
        long bT2 = System.currentTimeMillis();
        stmt.execute(analyze);
        LOG.info("TIME ANALYZING " + ":" + (System.currentTimeMillis() - bT2));
    }


    /**
     * {@inheritDoc}
     */
    @Override
   protected void earlyExtraProcessing(Connection connection) throws  SQLException {
        // to limit the process to the current submission
        createSubFeatureIdTempTable(connection);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void finishedProcessing(Connection connection,
            Map<Integer, FeatureData> featureDataMap)
        throws SQLException {
        // override in subclasses as necessary
        String query =
            " DROP TABLE " + SUBFEATUREID_TEMP_TABLE_NAME;

        Statement stmt = connection.createStatement();
        LOG.info("executing: " + query);
        stmt.execute(query);

    }

    /**
     * Process the identifier and return a "cleaned" version.  Implement in sub-classes to fix
     * data problem.
     * @param type the InterMine type of the feature that this identifier came from
     * @param identifier the identifier
     * @return a cleaned identifier
     */
    /**
     * {@inheritDoc}
     */
    @Override
    protected String fixIdentifier(FeatureData fdat, String identifier) {

        //do
        String uniqueName = fdat.getChadoFeatureUniqueName();
        String name = fdat.getChadoFeatureName();
        // String type = fdat.getInterMineType();
        // LOG.info("IDI TYPE " + type);

        if (StringUtil.isEmpty(identifier)) {
            if (StringUtil.isEmpty(name)) {
                String fixedName = uniqueName.substring(uniqueName.lastIndexOf('.') + 1);
                return fixedName;
            } else {
                return name;
            }
        } else if (identifier == name) {
            return identifier;
        } else {
            return identifier;
        }
    }

    private void processFeatureScores(Connection connection) throws SQLException,
    ObjectStoreException {
        ResultSet res = getFeatureScores(connection);
        while (res.next()) {

            Integer featureId = res.getInt("feature_id");
            Double score = res.getDouble("score");
            String program = res.getString("program");

            if (featureMap.containsKey(featureId)) {
                FeatureData fData = featureMap.get(featureId);
                Integer storedFeatureId = fData.getIntermineObjectId();

                Attribute scoreAttribute = new Attribute("score", score.toString());
                getChadoDBConverter().store(scoreAttribute, storedFeatureId);

                Attribute scoreTypeAttribute = new Attribute("scoreType", program);
                getChadoDBConverter().store(scoreTypeAttribute, storedFeatureId);
            }
        }
        res.close();
    }


    private ResultSet getFeatureScores(Connection connection) throws SQLException {
        String query =
            "SELECT f.feature_id as feature_id, af.rawscore as score, a.program as program"
            + " FROM feature f, analysisfeature af, analysis a "
            + " WHERE f.feature_id = af.feature_id "
            + " AND af.analysis_id = a.analysis_id "
            + " AND f.feature_id IN "
            + " (select feature_id from " + SUBFEATUREID_TEMP_TABLE_NAME + " ) ";
        LOG.info("executing: " + query);
        long bT = System.currentTimeMillis();
        Statement stmt = connection.createStatement();
        ResultSet res = stmt.executeQuery(query);
        LOG.info("TIME QUERYING FEATURE SCORES " + ":" + (System.currentTimeMillis() - bT));
        return res;
    }
}
