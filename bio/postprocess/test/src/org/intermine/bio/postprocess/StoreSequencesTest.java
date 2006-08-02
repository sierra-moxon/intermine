package org.intermine.bio.postprocess;

import junit.framework.TestCase;
import junit.framework.Assert;

import java.sql.*;
import java.util.Iterator;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreWriter;
import org.intermine.objectstore.ObjectStoreWriterFactory;
import org.intermine.objectstore.query.SingletonResults;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.QueryClass;
import org.intermine.objectstore.query.QueryField;
import org.intermine.objectstore.query.QueryValue;
import org.intermine.objectstore.query.SimpleConstraint;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.util.DynamicUtil;
import org.intermine.sql.Database;
import org.intermine.model.InterMineObject;

import org.flymine.model.genomic.*;
import org.apache.log4j.Logger;

public class StoreSequencesTest extends TestCase {

    private ObjectStoreWriter osw;
    private String ensemblDb = "db.ensembl-human";
    private Database db = null;

    private static final Logger LOG = Logger.getLogger(StoreSequencesTest.class);

    public void setUp() throws Exception {
        osw = ObjectStoreWriterFactory.getObjectStoreWriter("osw.bio-test");
        storeContigs();
    }

    public void tearDown() throws Exception {
        if (osw.isInTransaction()) {
            osw.abortTransaction();
        }
        Query q = new Query();
        QueryClass qc = new QueryClass(InterMineObject.class);
        q.addFrom(qc);
        q.addToSelect(qc);
        ObjectStore os = osw.getObjectStore();
        SingletonResults res = new SingletonResults(q, os, os.getSequence());
        Iterator resIter = res.iterator();
        //osw.beginTransaction();
        while (resIter.hasNext()) {
            InterMineObject o = (InterMineObject) resIter.next();
            osw.delete(o);
        }
        //osw.commitTransaction();
        osw.close();
    }

    // Changed test to use MockStoreSequences test to avoid requiring remote
    // database.  Uncomment this test and set objectstore properties for db.ensembl-human
    // to check getSequence().
//     public void testGetSequence() throws Exception{
//         Database db = DatabaseFactory.getDatabase(ensemblDb);
//         StoreSequences ss = new StoreSequences(osw, db);
//         String seq = ss.getSequence("CR381709.1.2001.2054");
//         String expectedSequence = "TTCCTAGGAGGTTCTAATCAATGCAACTATAGGTATTTTCTGCCAAGGTCTAGC";
//         assertEquals(expectedSequence, seq);
//     }

    public void testStoreContigSequences() throws Exception {
        storeContigs();
        StoreSequences ss = new MockStoreSequences(osw, db);
        ss.storeContigSequences();

        Query q1 = new Query();
        QueryClass qc = new QueryClass(Contig.class);
        q1.addToSelect(qc);
        q1.addFrom(qc);
        QueryField qf = new QueryField(qc, "identifier");
        SimpleConstraint sc1 = new SimpleConstraint(qf, ConstraintOp.EQUALS,
                               new QueryValue("CR381709.1.2001.2054"));
        q1.setConstraint(sc1);
        ObjectStore os = osw.getObjectStore();
        SingletonResults res1 = new SingletonResults(q1, os, os.getSequence());
        Contig con1 = (Contig) res1.get(0);
        String seq1 =  con1.getSequence().getResidues();
        String expectedSequence = "TTCCTAGGAGGTTCTAATCAATGCAACTATAGGTATTTTCTGCCAAGGTCTAGC";
        Assert.assertEquals(expectedSequence, seq1);

        Query q2 = new Query();
        q2.addToSelect(qc);
        q2.addFrom(qc);
        SimpleConstraint sc2 = new SimpleConstraint(qf, ConstraintOp.EQUALS,
                               new QueryValue("AADD01209098.1.15791.15883"));
        q2.setConstraint(sc2);
        SingletonResults res2 = new SingletonResults(q2, os, os.getSequence());
        Contig con2 = (Contig) res2.get(0);
        String seq2 =  con2.getSequence().getResidues();
        expectedSequence = "TAAGTCTCTCAAAAACCCCTGGAAGACTGTATCAAGGGGTTGTTGTTGGTGGCACTGGTGTGATAATGGATCTGATATTCATTGTGATAGCAG";
        Assert.assertEquals(expectedSequence, seq2);
    }

    private void storeContigs() throws Exception{
        Contig contig1 = (Contig) DynamicUtil.createObject(Collections.singleton(Contig.class));
        Contig contig2 = (Contig) DynamicUtil.createObject(Collections.singleton(Contig.class));

        contig1.setIdentifier("AADD01209098.1.15791.15883");
        contig2.setIdentifier("CR381709.1.2001.2054");
        osw.beginTransaction();
        osw.store(contig1);
        osw.store(contig2);
        osw.commitTransaction();
    }

    // MockStoreSequences subclasses store sequences and overrides getSequence()
    // method to return desired sequence for given contig id
    private class MockStoreSequences extends StoreSequences {
        Map seqs = new HashMap();

        MockStoreSequences(ObjectStoreWriter osw, Database db) throws SQLException,
        ClassNotFoundException {
            super(osw, db);
            seqs.put("CR381709.1.2001.2054", "TTCCTAGGAGGTTCTAATCAATGCAACTATAGGTATTTTCTGCCAAGGTCTAGC");
            seqs.put("AADD01209098.1.15791.15883", "TAAGTCTCTCAAAAACCCCTGGAAGACTGTATCAAGGGGTTGTTGTTGGTGGCACTGGTGTGATAATGGATCTGATATTCATTGTGATAGCAG");
        }

        protected String getSequence(String contigId) throws SQLException {
            return (String) seqs.get(contigId);
        }
    }



}

