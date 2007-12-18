package org.intermine.bio.dataconversion;

/*
 * Copyright (C) 2002-2007 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Iterator;

import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.objectstore.query.ContainsConstraint;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.QueryClass;
import org.intermine.objectstore.query.QueryObjectReference;
import org.intermine.objectstore.query.Results;
import org.intermine.objectstore.query.ResultsRow;
import org.intermine.objectstore.query.SingletonResults;

import org.intermine.model.InterMineObject;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreWriter;
import org.intermine.objectstore.ObjectStoreWriterFactory;

import org.flymine.model.genomic.FivePrimeUTR;
import org.flymine.model.genomic.LocatedSequenceFeature;
import org.flymine.model.genomic.Location;
import org.flymine.model.genomic.Sequence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

/**
 * Tests for {@link FlyBaseUTRFastaLoaderTask}
 * @author Kim Rutherford
 */
public class FlyBaseUTRFastaLoaderTaskTest extends TestCase {

    private ObjectStoreWriter osw;
    private static final Logger LOG = Logger.getLogger(FlyBaseUTRFastaLoaderTaskTest.class);

    public void setUp() throws Exception {
        osw = ObjectStoreWriterFactory.getObjectStoreWriter("osw.bio-test");
        osw.getObjectStore().flushObjectById();
    }

    public void testFastaLoad() throws Exception {
        FastaLoaderTask flt = new FlyBaseUTRFastaLoaderTask();
        flt.setFastaTaxonId(new Integer(36329));
        flt.setIgnoreDuplicates(true);
        flt.setClassName("org.flymine.model.genomic.FivePrimeUTR");
        flt.setClassAttribute("identifier");
        flt.setIntegrationWriterAlias("integration.bio-test");
        flt.setSourceName("fasta-test");


        File tmpFile = File.createTempFile("FlyBaseUTRFastaLoaderTaskTest", "tmp");
        FileWriter fw = new FileWriter(tmpFile);
        InputStream is =
            getClass().getClassLoader().getResourceAsStream("dmel-all-five_prime_UTR.fasta");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line = null;
        while ((line = br.readLine()) != null) {
            fw.write(line + "\n");
        }

        fw.close();
        tmpFile.deleteOnExit();

        File[] files = new File[1];
        files[0] = tmpFile;
        flt.setFileArray(files);
        flt.execute();
        //Check the results to see if we have some data...
        ObjectStore os = osw.getObjectStore();

        Query q = new Query();
        QueryClass lsfQueryClass = new QueryClass(LocatedSequenceFeature.class);
        QueryClass seqQueryClass = new QueryClass(Sequence.class);
        q.addToSelect(lsfQueryClass);
        q.addToSelect(seqQueryClass);
        q.addFrom(lsfQueryClass);
        q.addFrom(seqQueryClass);

        QueryObjectReference qor = new QueryObjectReference(lsfQueryClass, "sequence");
        ContainsConstraint cc = new ContainsConstraint(qor, ConstraintOp.CONTAINS, seqQueryClass);

        q.setConstraint(cc);

        Results r = os.execute(q);

        boolean seenFBtr0112632 = false;
        boolean seenFBtr0100521 = false;

        for (Object rr: r) {
            FivePrimeUTR utr = (FivePrimeUTR) ((ResultsRow) rr).get(0);
            assertNotNull(utr.getChromosomeLocation());
            if (utr.getIdentifier().equals("FBtr0112632-5-prime-utr")) {
                seenFBtr0112632 = true;
                Location loc = utr.getChromosomeLocation();
                assertEquals(10258903, loc.getStart().intValue());
                assertEquals(10307410, loc.getEnd().intValue());
                assertEquals("3R", loc.getObject().getIdentifier());
                assertEquals("FBtr0112632", utr.getmRNA().getIdentifier());
                assertEquals(36329, utr.getOrganism().getTaxonId().intValue());
            } else {
                if (utr.getIdentifier().equals("FBtr0100521-5-prime-utr")) {
                    seenFBtr0100521 = true;
                    Location loc = utr.getChromosomeLocation();
                    assertEquals(18024494, loc.getStart().intValue());
                    assertEquals(18050424, loc.getEnd().intValue());
                    assertEquals("2R", loc.getObject().getIdentifier());
                    assertEquals("FBtr0100521", utr.getmRNA().getIdentifier());
                    assertEquals(36329, utr.getmRNA().getOrganism().getTaxonId().intValue());
                }
            }
        }

        if (!seenFBtr0100521) {
            throw new RuntimeException("FBtr0100521 5' UTR not seen");
        }
        if (!seenFBtr0112632) {
            throw new RuntimeException("FBtr0112632 5' UTR not seen");
        }
        assertEquals(5, r.size());
    }

    public void tearDown() throws Exception {
        LOG.info("in tear down");
        if (osw.isInTransaction()) {
            osw.abortTransaction();
        }
        Query q = new Query();
        QueryClass qc = new QueryClass(InterMineObject.class);
        q.addFrom(qc);
        q.addToSelect(qc);
        SingletonResults res = osw.getObjectStore().executeSingleton(q);
        LOG.info("created results");
        Iterator resIter = res.iterator();
        osw.beginTransaction();
        while (resIter.hasNext()) {
            InterMineObject o = (InterMineObject) resIter.next();
            LOG.info("deleting: " + o.getId());
            osw.delete(o);
        }
        osw.commitTransaction();
        LOG.info("committed transaction");
        osw.close();
        LOG.info("closed objectstore");
    }

}
