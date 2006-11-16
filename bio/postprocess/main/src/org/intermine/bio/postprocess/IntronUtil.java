package org.intermine.bio.postprocess;

/*
 * Copyright (C) 2002-2005 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.BitSet;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.*;
import org.intermine.objectstore.query.QueryClass;
import org.intermine.objectstore.query.Results;
import org.intermine.objectstore.query.ResultsRow;
import org.intermine.objectstore.query.QueryCollectionReference;
import org.intermine.objectstore.query.QueryObjectReference;
import org.intermine.objectstore.query.ConstraintSet;
import org.intermine.model.InterMineObject;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.objectstore.ObjectStoreWriter;
import org.intermine.util.DynamicUtil;
import org.intermine.util.TypeUtil;
import org.intermine.objectstore.intermine.ObjectStoreInterMineImpl;

import org.flymine.model.genomic.DataSet;
import org.flymine.model.genomic.DataSource;
import org.flymine.model.genomic.Chromosome;
import org.flymine.model.genomic.Transcript;
import org.flymine.model.genomic.Exon;
import org.flymine.model.genomic.Intron;
import org.flymine.model.genomic.Location;
import org.flymine.model.genomic.Synonym;

/**
 * Methods for creating feature for introns.
 * @author Wenyan Ji
 */
public class IntronUtil
{
    private static final Logger LOG = Logger.getLogger(IntronUtil.class);

    private ObjectStoreWriter osw = null;
    private ObjectStore os;
    private DataSet dataSet;
    private DataSource dataSource;

    protected Map intronMap = new HashMap();

    /**
     * Create a new IntronUtil object that will operate on the given ObjectStoreWriter.
     * NOTE - needs to be run after LocatedSequenceFeature.chromosomeLocation has been set.
     * @param osw the ObjectStoreWriter to use when creating/changing objects
     */
    public IntronUtil(ObjectStoreWriter osw) {
        this.osw = osw;
        this.os = osw.getObjectStore();
        dataSource = (DataSource) DynamicUtil.createObject(Collections.singleton(DataSource.class));
        dataSource.setName("FlyMine");
        try {
            dataSource = (DataSource) os.getObjectByExample(dataSource,
                                                            Collections.singleton("name"));
        } catch (ObjectStoreException e) {
            throw new RuntimeException("unable to fetch FlyMine DataSource object", e);
        }
    }

    /**
     * Create Intron objects
     * @throws ObjectStoreException if there is an ObjectStore problem
     * @throws IllegalAccessException if there is an ObjectStore problem
     */
    public void createIntronFeatures()
        throws ObjectStoreException, IllegalAccessException {

        dataSet = (DataSet) DynamicUtil.createObject(Collections.singleton(DataSet.class));
        dataSet.setTitle("FlyMine introns");
        dataSet.setDescription("Introns calculated by FlyMine");
        dataSet.setVersion("" + new Date()); // current time and date
        dataSet.setUrl("http://www.flymine.org");
        dataSet.setDataSource(dataSource);

        int exonCounts;
        Query q = new Query();
        ConstraintSet cs = new ConstraintSet(ConstraintOp.AND);
        QueryClass qcTran = new QueryClass(Transcript.class);
        q.addFrom(qcTran);
        q.addToSelect(qcTran);

        QueryClass qcTranLoc = new QueryClass(Location.class);
        q.addFrom(qcTranLoc);
        q.addToSelect(qcTranLoc);
        QueryObjectReference qorTranLoc = new QueryObjectReference(qcTran, "chromosomeLocation");
        ContainsConstraint ccTranLoc = new ContainsConstraint(qorTranLoc, ConstraintOp.CONTAINS, qcTranLoc);
        cs.addConstraint(ccTranLoc);

        QueryClass qcExon = new QueryClass(Exon.class);
        q.addFrom(qcExon);
        QueryCollectionReference qcrExons = new QueryCollectionReference(qcTran, "exons");
        ContainsConstraint ccTranExons = new ContainsConstraint(qcrExons, ConstraintOp.CONTAINS, qcExon);
        cs.addConstraint(ccTranExons);

        QueryClass qcExonLoc = new QueryClass(Location.class);
        q.addFrom(qcExonLoc);
        q.addToSelect(qcExonLoc);
        QueryObjectReference qorExonLoc = new QueryObjectReference(qcExon, "chromosomeLocation");
        ContainsConstraint ccExonLoc = new ContainsConstraint(qorExonLoc, ConstraintOp.CONTAINS, qcExonLoc);
        cs.addConstraint(ccExonLoc);

        q.setConstraint(cs);
        q.addToOrderBy(qcTran);

        ((ObjectStoreInterMineImpl) os).precompute(q, PostProcessTask.PRECOMPUTE_CATEGORY);
        Results results = new Results(q, os, os.getSequence());
        results.setBatchSize(500);
        Iterator resultsIter = results.iterator();

        Set locationSet = new HashSet();
        Transcript lastTran = null;
        Location lastTranLoc = null;
        int tranCount = 0, exonCount = 0;
        while (resultsIter.hasNext()) {
            ResultsRow rr = (ResultsRow) resultsIter.next();
            Transcript thisTran = (Transcript) rr.get(0);

            if (lastTran == null) {
                lastTran = thisTran;
                lastTranLoc = (Location) rr.get(1);
            }

            if (!thisTran.getId().equals(lastTran.getId())) {
                tranCount++;
                Set intronSet = createIntronFeatures(locationSet, lastTran, lastTranLoc);
                exonCount += locationSet.size();
                locationSet = new HashSet();
                lastTran = thisTran;
                lastTranLoc = (Location) rr.get(1);
            }
            locationSet.add(rr.get(2));
        }

        if (lastTran != null) {
            Set intronSet = createIntronFeatures(locationSet, lastTran, lastTranLoc);
            tranCount++;
            exonCount += locationSet.size();
        }

        LOG.info("Read " + tranCount + " transcripts with " + exonCount + " exons.");

        osw.beginTransaction();
        for (Iterator i = intronMap.keySet().iterator(); i.hasNext();) {
            String identifier = (String) i.next();
            Intron intron = (Intron) intronMap.get(identifier);
            osw.store(intron);
            osw.store(intron.getChromosomeLocation());
            osw.store((InterMineObject) intron.getSynonyms().iterator().next());
        }

        if (intronMap.size() > 1) {
            osw.store(dataSet);
        }
        osw.commitTransaction();
        //osw.abortTransaction();
    }


    /**
     * Return a set of Intron objects that don't overlap the Locations
     * in the locationSet argument.  The caller must call ObjectStoreWriter.store() on the
     * Intron, its chromosomeLocation and the synonym in the synonyms collection.
     * @param locationSet a set of Locations for the exonss on a particular transcript
     * @param transcriptId the ID of the Transcript that the Locations refer to
     * @return a set of Intron objects
     * @throws ObjectStoreException if there is an ObjectStore problem
     */
    protected Set createIntronFeatures(Set locationSet, Transcript transcript, Location tranLoc)
        throws ObjectStoreException {
        //final BitSet bs = new BitSet(transcript.getLength().intValue() + 1);
        final BitSet bs = new BitSet(transcript.getLength().intValue());

        if (locationSet.size() == 1) {
            return null;
        }
        Chromosome chr = transcript.getChromosome();

        Iterator locationIter = locationSet.iterator();
        int tranStart = tranLoc.getStart().intValue();

        while (locationIter.hasNext()) {
            Location location = (Location) locationIter.next();
            bs.set(location.getStart().intValue() - tranStart, (location.getEnd().intValue() - tranStart) + 1);
        }

        int prevEndPos = 0;

        int intronCount = 0;
        while (prevEndPos != -1) {
            intronCount++;
            int nextIntronStart = bs.nextClearBit(prevEndPos + 1);
            int intronEnd;
            int nextSetBit = bs.nextSetBit(nextIntronStart);

            if (nextSetBit == -1) {
                intronEnd = transcript.getLength().intValue();
            } else {
                intronEnd = nextSetBit - 1;
            }

            if (nextSetBit == -1
                || intronCount == (locationSet.size() - 1)) {
                prevEndPos = -1;
            } else {
                prevEndPos = intronEnd;
            }

            int newLocStart = nextIntronStart + tranStart;
            int newLocEnd = intronEnd + tranStart;

            String identifier = "intron_chr" + chr.getIdentifier()
                + "_" + Integer.toString(newLocStart) + ".." + Integer.toString(newLocEnd);

            if (intronMap.get(identifier) == null) {
                Intron intron = (Intron)
                    DynamicUtil.createObject(Collections.singleton(Intron.class));
                Location location =
                    (Location) DynamicUtil.createObject(Collections.singleton(Location.class));
                Synonym synonym =
                    (Synonym) DynamicUtil.createObject(Collections.singleton(Synonym.class));

                intron.setChromosome(chr);
                intron.setOrganism(chr.getOrganism());
                intron.addEvidence(dataSet);
                intron.setIdentifier(identifier);

                location.setStart(new Integer(newLocStart));
                location.setEnd(new Integer(newLocEnd));
                location.setStrand(new Integer(1));
                location.setPhase(new Integer(0));
                location.setStartIsPartial(Boolean.FALSE);
                location.setEndIsPartial(Boolean.FALSE);
                location.setSubject(intron);
                location.setObject(transcript);
                location.addEvidence(dataSet);

                synonym.addEvidence(dataSet);
                synonym.setSource(dataSource);
                synonym.setSubject(intron);
                synonym.setType("identifier");
                synonym.setValue(intron.getIdentifier());

                intron.setChromosomeLocation(location);
                intron.addSynonyms(synonym);
                int length = location.getEnd().intValue() - location.getStart().intValue() + 1;
                intron.setLength(new Integer(length));
                intron.addTranscripts(transcript);

                intronMap.put(identifier, intron);
            } else {
                Intron intron = (Intron) intronMap.get(identifier);
                intron.addTranscripts(transcript);
                intronMap.put(identifier, intron);
            }
        }

        Set intronSet = new HashSet();
        for (Iterator i = intronMap.keySet().iterator(); i.hasNext(); ) {
            intronSet.add(intronMap.get(i.next()));
        }
        return intronSet;
    }

    /**
     * @param os objectStore
     * @param transcriptId Integer
     * @return all the exons locationSet for the particular transcriptId
     */
    private Set getLocationSet(ObjectStore os, Integer transcriptId) {
        Set locationSet = new HashSet();

        ConstraintSet cs = new ConstraintSet(ConstraintOp.AND);

        Query q = new Query();
        QueryClass qct = new QueryClass(Transcript.class);
        QueryField qf = new QueryField(qct, "id");
        SimpleConstraint sc1 = new SimpleConstraint(qf, ConstraintOp.EQUALS,
                                            new QueryValue(transcriptId));

        q.addFrom(qct);
        q.addToSelect(qf);
        q.addToOrderBy(qf);
        cs.addConstraint(sc1);

        QueryCollectionReference ref1 = new QueryCollectionReference(qct, "exons");
        QueryClass qce = new QueryClass(Exon.class);
        q.addFrom(qce);
        q.addToSelect(qce);
        ContainsConstraint cc1 = new ContainsConstraint(ref1, ConstraintOp.CONTAINS, qce);
        cs.addConstraint(cc1);

        QueryClass qcl = new QueryClass(Location.class);
        q.addFrom(qcl);
        q.addToSelect(qcl);
        QueryObjectReference ref2 = new QueryObjectReference(qcl, "subject");
        ContainsConstraint cc2 = new ContainsConstraint(ref2, ConstraintOp.CONTAINS, qce);
        cs.addConstraint(cc2);

        q.setConstraint(cs);

        Results res = new Results(q, os, os.getSequence());
        Iterator iter = res.iterator();
        while (iter.hasNext()) {
            ResultsRow rr = (ResultsRow) iter.next();
            Integer id = (Integer) rr.get(0);
            Location loc = (Location) rr.get(2);
            locationSet.add(loc);
        }
        return locationSet;
    }
}
