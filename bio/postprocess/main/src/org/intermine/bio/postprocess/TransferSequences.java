package org.intermine.bio.postprocess;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.objectstore.query.ConstraintSet;
import org.intermine.objectstore.query.ContainsConstraint;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.QueryClass;
import org.intermine.objectstore.query.QueryCollectionReference;
import org.intermine.objectstore.query.QueryField;
import org.intermine.objectstore.query.QueryNode;
import org.intermine.objectstore.query.QueryObjectReference;
import org.intermine.objectstore.query.Results;
import org.intermine.objectstore.query.ResultsRow;

import org.intermine.bio.util.Constants;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.objectstore.ObjectStoreWriter;
import org.intermine.objectstore.intermine.ObjectStoreInterMineImpl;
import org.intermine.objectstore.proxy.ProxyReference;
import org.intermine.util.DynamicUtil;

import org.flymine.model.genomic.Assembly;
import org.flymine.model.genomic.Chromosome;
import org.flymine.model.genomic.ChromosomeBand;
import org.flymine.model.genomic.Exon;
import org.flymine.model.genomic.Gene;
import org.flymine.model.genomic.LocatedSequenceFeature;
import org.flymine.model.genomic.Location;
import org.flymine.model.genomic.Sequence;
import org.flymine.model.genomic.Supercontig;
import org.flymine.model.genomic.Transcript;
import org.flymine.model.genomic.CDS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.symbol.IllegalAlphabetException;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SymbolList;

/**
 * Transfer sequences from the Assembly objects to the other objects that are located on the
 * Assemblys and to the objects that the Assemblys are located on (eg. Chromosomes).
 *
 * @author Kim Rutherford
 */

public class TransferSequences
{
    protected ObjectStoreWriter osw;
    private static final Logger LOG = Logger.getLogger(TransferSequences.class);

    /**
     * Create a new TransferSequences object from the given ObjectStoreWriter
     * @param osw writer on genomic ObjectStore
     */
    public TransferSequences (ObjectStoreWriter osw) {
        this.osw = osw;
    }

    /**
     * Copy the Assembly sequences to the appropriate place in the chromosome sequences and store a
     * Sequence object for each Chromosome.  Uses the ObjectStoreWriter that was passed to the
     * constructor
     * @throws Exception if there are problems with the transfer
     */
    public void transferToChromosome()
        throws Exception {
        ObjectStore os = osw.getObjectStore();

        long startTime = System.currentTimeMillis();

        Results results =
            PostProcessUtil.findLocationAndObjects(os, Chromosome.class, Assembly.class,
                                                   false, false);
        // could try reducing further if still OutOfMemeory problems
        results.setBatchSize(20);
        results.setNoPrefetch();

        Map chromosomeTempFiles = new HashMap();

        Iterator resIter = results.iterator();

        Chromosome currentChr = null;
        RandomAccessFile currentChrBases = null;

        while (resIter.hasNext()) {
            ResultsRow rr = (ResultsRow) resIter.next();
            Integer chrId = (Integer) rr.get(0);
            Chromosome chr = (Chromosome) os.getObjectById(chrId);
            Assembly assembly = (Assembly) rr.get(1);
            Location assemblyOnChrLocation = (Location) rr.get(2);

            if (assembly instanceof Supercontig) {
                continue;
            }

            if (currentChr == null || !chr.equals(currentChr)) {
                if (currentChr != null) {
                    currentChrBases.close();
                    LOG.info("finished writing temp file for Chromosome: "
                             + currentChr.getSecondaryIdentifier());
                }

                File tempFile = getTempFile(chr);
                currentChrBases = getChromosomeTempSeqFile(chr, tempFile);
                chromosomeTempFiles.put(chr, tempFile);

                currentChr = chr;
            }

            copySeqArray(currentChrBases, assembly.getSequence().getResidues(),
                         assemblyOnChrLocation.getStart().intValue(),
                         assemblyOnChrLocation.getStrand());
        }
        if (currentChrBases == null) {
            LOG.error("in transferToChromosome(): no Assembly sequences found");
        } else {
            currentChrBases.close();
            storeTempSequences(chromosomeTempFiles);
            LOG.info("finished writing temp file for Chromosome: "
                     + currentChr.getSecondaryIdentifier());
        }

        LOG.info("Finished transferring sequences to chromosomes - took "
                 + (System.currentTimeMillis() - startTime) + " ms.");
    }

    private File getTempFile(Chromosome chr) throws IOException {
        String prefix = "transfer_sequences_temp_" + chr.getId() + "_"
            + chr.getSecondaryIdentifier();
        return File.createTempFile(prefix, null, new File ("/tmp/"));
    }

    /**
     * Initialise the given File by setting it's length to the length of the Chromosome and
     * initialising it with "." characters.
     * @throws IOException
     */
    private RandomAccessFile getChromosomeTempSeqFile(Chromosome chr, File tempFile)
                                                      throws IOException {
        RandomAccessFile raf = new RandomAccessFile(tempFile, "rw");

        byte[] bytes;
        try {
            bytes = ("......................................................................."
                            + ".............................................").getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("unexpected exception", e);
        }

        int writeCount = chr.getLength().intValue() / bytes.length + 1;

        // fill with '.' so we can see the parts of the Chromosome sequence that haven't
        // been set
        for (int i = 0; i < writeCount; i++) {
            raf.write(bytes);
        }

        raf.setLength(chr.getLength().longValue());

        return raf;
    }

    private void storeTempSequences(Map chromosomeTempFiles)
        throws ObjectStoreException, IOException {
        Iterator chromosomeTempFilesIter = chromosomeTempFiles.keySet().iterator();

        while (chromosomeTempFilesIter.hasNext()) {
            Chromosome chr = (Chromosome) chromosomeTempFilesIter.next();
            File file = (File) chromosomeTempFiles.get(chr);
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String sequenceString = bufferedReader.readLine();
            osw.beginTransaction();
            LOG.info("Storing sequence for chromosome: " + chr.getSecondaryIdentifier());
            storeNewSequence(chr, sequenceString);
            osw.commitTransaction();
        }
    }

    private void storeNewSequence(LocatedSequenceFeature feature, String sequenceString)
        throws ObjectStoreException {
        Sequence sequence =
            (Sequence) DynamicUtil.createObject(Collections.singleton(Sequence.class));
        sequence.setResidues(sequenceString);
        sequence.setLength(sequenceString.length());
        osw.store(sequence);
        feature.proxySequence(new ProxyReference(osw.getObjectStore(),
                                                 sequence.getId(), Sequence.class));
        feature.setLength(new Integer(sequenceString.length()));
        osw.store(feature);
    }

    /**
     * Use the Location relations to copy the sequence from the Chromosomes to every
     * LocatedSequenceFeature that is located on a Chromosome and which doesn't already have a
     * sequence (ie. don't copy to Assembly).  Uses the ObjectStoreWriter that was passed to the
     * constructor
     * @throws Exception if there are problems with the transfer
     */
    public void transferToLocatedSequenceFeatures()
        throws Exception {
        long startTime = System.currentTimeMillis();
        ObjectStore os = osw.getObjectStore();
        osw.beginTransaction();
        Query q = new Query();
        q.setDistinct(false);
        QueryClass qcObj = new QueryClass(Chromosome.class);
        QueryField qfObj = new QueryField(qcObj, "id");
        q.addFrom(qcObj);
        q.addToSelect(qfObj);

        QueryClass qcSub = new QueryClass(LocatedSequenceFeature.class);
        q.addFrom(qcSub);
        q.addToSelect(qcSub);

        q.addToOrderBy(qcSub);

        QueryClass qcLoc = new QueryClass(Location.class);
        q.addFrom(qcLoc);
        q.addToSelect(qcLoc);
        ConstraintSet cs = new ConstraintSet(ConstraintOp.AND);
        QueryObjectReference ref1 = new QueryObjectReference(qcLoc, "object");
        ContainsConstraint cc1 = new ContainsConstraint(ref1, ConstraintOp.CONTAINS, qcObj);
        cs.addConstraint(cc1);
        QueryObjectReference ref2 = new QueryObjectReference(qcLoc, "subject");
        ContainsConstraint cc2 = new ContainsConstraint(ref2, ConstraintOp.CONTAINS, qcSub);
        cs.addConstraint(cc2);

        QueryObjectReference lsfSeqRef = new QueryObjectReference(qcSub, "sequence");
        ContainsConstraint lsfSeqRefNull = new ContainsConstraint(lsfSeqRef, ConstraintOp.IS_NULL);

        cs.addConstraint(lsfSeqRefNull);

        q.setConstraint(cs);

        Set<QueryNode> indexesToCreate = new HashSet<QueryNode>();
        indexesToCreate.add(qfObj);
        indexesToCreate.add(qcLoc);
        indexesToCreate.add(qcSub);
        ((ObjectStoreInterMineImpl) os).precompute(q, indexesToCreate,
            Constants.PRECOMPUTE_CATEGORY);
        Results results = os.execute(q);

        results.setBatchSize(1000);

        Iterator<ResultsRow> resIter = results.iterator();

        long start = System.currentTimeMillis();
        int i = 0;
        while (resIter.hasNext()) {
            ResultsRow rr = resIter.next();

            Integer chrId = (Integer) rr.get(0);
            LocatedSequenceFeature feature = (LocatedSequenceFeature) rr.get(1);
            Location locationOnChr = (Location) rr.get(2);

            try {
                if (feature instanceof Assembly) {
                    continue;
                }

                if (feature instanceof ChromosomeBand) {
                    continue;
                }

                if (feature instanceof Transcript) {
                    continue;
                }

                if (feature instanceof CDS) {
                    continue;
                }

                if (feature instanceof Gene) {
                    Gene gene = (Gene) feature;
                    if (gene.getLength() != null && gene.getLength().intValue() > 2000000) {
                        LOG.error("gene too long in transferToLocatedSequenceFeatures() ignoring: "
                                  + gene);
                        continue;
                    }
                }

                Chromosome chr = (Chromosome) os.getObjectById(chrId);
                Sequence chromosomeSequence = chr.getSequence();

                if (chromosomeSequence == null) {
                    LOG.warn("no sequence found for: " + chr.getSecondaryIdentifier() + "  id: "
                            + chr.getId());
                    continue;
                }

                String featureSeq = getSubSequence(chromosomeSequence, locationOnChr);

                if (featureSeq == null) {
                    // probably the locationOnChr is out of range
                    continue;
                }

                Sequence sequence =
                    (Sequence) DynamicUtil.createObject(Collections.singleton(Sequence.class));
                sequence.setResidues(featureSeq);
                sequence.setLength(featureSeq.length());
                osw.store(sequence);
                LocatedSequenceFeature cloneLsf =
                    (LocatedSequenceFeature) PostProcessUtil.cloneInterMineObject(feature);
                cloneLsf.setSequence(sequence);
                cloneLsf.setLength(new Integer(featureSeq.length()));
                osw.store(cloneLsf);
                i++;
                if (i % 1000 == 0) {
                    long now = System.currentTimeMillis();
                    LOG.info("Set sequences for " + i + " features"
                             + " (avg = " + ((60000L * i) / (now - start)) + " per minute)");
                }
            } catch (Exception e) {
                Exception e2 = new Exception("Exception while processing LocatedSequenceFeature "
                        + feature);
                e2.initCause(e);
                throw e2;
            }
        }

        osw.commitTransaction();

        LOG.info("Finished setting " + i + " feature sequences - took "
                 + (System.currentTimeMillis() - startTime) + " ms.");
    }

    private String getSubSequence(Sequence chromosomeSequence, Location locationOnChr)
        throws IllegalSymbolException, IllegalAlphabetException {
        int charsToCopy =
            locationOnChr.getEnd().intValue() - locationOnChr.getStart().intValue() + 1;
        String chromosomeSequenceString = chromosomeSequence.getResidues();

        if (charsToCopy > chromosomeSequenceString.length()) {
            LOG.error("LocatedSequenceFeature too long, ignoring - Location: "
                      + locationOnChr.getId() + "  LSF id: " + locationOnChr.getObject());
            return null;
        }

        int startPos = locationOnChr.getStart().intValue() - 1;
        int endPos = startPos + charsToCopy;

        if (startPos < 0 || endPos < 0) {
            LOG.error("LocatedSequenceFeature has negative coordinate, ignoring Location: "
                      + locationOnChr.getId() + "  LSF id: " + locationOnChr.getObject());
            return null;

            // Do we really want an exception?  Not much we can do about it.  (rns 22/10/06)
// TODO XXX FIXME - uncomment this
//             throw new RuntimeException("in TransferSequences.getSubSequence(): locationOnChr "
//                                        + locationOnChr
//                                        + "\n  startPos: " + startPos + " endPos " + endPos
//                                        + "\n chromosomeSequence.substr(0,1000) " +
//                                        chromosomeSequenceString.substring(0,1000)
//                                        + "\n location.getObject() "
//                                        + locationOnChr.getObject().toString()
//                                        + " location.getSubject() " +
//                                        locationOnChr.getSubject().toString() + " "
//                                        + "\n location.getSubject().getId() " +
//                                        locationOnChr.getSubject().getId() +
//                                        "\n location.getObject().getId() ");
        }

        if (endPos > chromosomeSequenceString.length()) {
            LOG.error("LocatedSequenceFeature has end coordinate greater than chromsome length."
                      + "ignoring Location: "
                      + locationOnChr.getId() + "  LSF id: " + locationOnChr.getObject());
            return null;
        }

        String subSeqString;

        if (startPos < endPos) {
            subSeqString = new String(chromosomeSequenceString.substring(startPos, endPos));
        } else {
            subSeqString = new String(chromosomeSequenceString.substring(endPos, startPos));
        }

        if (locationOnChr.getStrand().equals("-1")) {
            SymbolList symbolList = DNATools.createDNA(subSeqString);

            symbolList = DNATools.reverseComplement(symbolList);

            subSeqString = symbolList.seqString();
        }

        return subSeqString;
    }

    private void copySeqArray(RandomAccessFile raf, String sourceSequence,
                              int start, String strand)
        throws IllegalSymbolException, IllegalAlphabetException, IOException {

        byte[] byteArray = new byte[sourceSequence.length()];

        if (strand.equals("-1")) {
            SymbolList symbolList = DNATools.createDNA(sourceSequence);

            symbolList = DNATools.reverseComplement(symbolList);

            byteArray = symbolList.seqString().getBytes("US-ASCII");
        } else {
            byteArray = sourceSequence.toLowerCase().getBytes("US-ASCII");
        }

        raf.seek(start - 1);
        raf.write(byteArray);
    }

    /**
     * For each Transcript, join and transfer the sequences from the child Exons to a new Sequence
     * object for the Transcript.  Uses the ObjectStoreWriter that was passed to the constructor
     * @throws Exception if there are problems with the transfer
     */
    public void transferToTranscripts()
        throws Exception {

        long startTime = System.currentTimeMillis();

        osw.beginTransaction();

        ObjectStore os = osw.getObjectStore();
        Query q = new Query();
        q.setDistinct(false);

        QueryClass qcTranscript = new QueryClass(Transcript.class);
        q.addFrom(qcTranscript);
        q.addToSelect(qcTranscript);
        q.addToOrderBy(qcTranscript);

        QueryClass qcExon = new QueryClass(Exon.class);
        q.addFrom(qcExon);
        q.addToSelect(qcExon);

        QueryClass qcExonSequence = new QueryClass(Sequence.class);
        q.addFrom(qcExonSequence);
        q.addToSelect(qcExonSequence);

        QueryClass qcExonLocation = new QueryClass(Location.class);
        q.addFrom(qcExonLocation);
        q.addToSelect(qcExonLocation);

        QueryField qfExonStart = new QueryField(qcExonLocation, "start");
        q.addToOrderBy(qfExonStart);

        ConstraintSet cs = new ConstraintSet(ConstraintOp.AND);

        QueryCollectionReference exonsRef =
            new QueryCollectionReference(qcTranscript, "exons");
        ContainsConstraint cc1 =
            new ContainsConstraint(exonsRef, ConstraintOp.CONTAINS, qcExon);
        cs.addConstraint(cc1);

        QueryObjectReference locRef =
            new QueryObjectReference(qcExon, "chromosomeLocation");
        ContainsConstraint cc2 =
            new ContainsConstraint(locRef, ConstraintOp.CONTAINS, qcExonLocation);
        cs.addConstraint(cc2);

        QueryObjectReference sequenceRef = new QueryObjectReference(qcExon, "sequence");
        ContainsConstraint cc3 =
            new ContainsConstraint(sequenceRef, ConstraintOp.CONTAINS, qcExonSequence);
        cs.addConstraint(cc3);

        QueryObjectReference transcriptSeqRef = new QueryObjectReference(qcTranscript, "sequence");
        ContainsConstraint lsfSeqRefNull =
            new ContainsConstraint(transcriptSeqRef, ConstraintOp.IS_NULL);

        cs.addConstraint(lsfSeqRefNull);

        q.setConstraint(cs);

        ((ObjectStoreInterMineImpl) os).precompute(q, Constants
                                                   .PRECOMPUTE_CATEGORY);
        Results res = os.execute(q);
        res.setBatchSize(1000);

        Iterator resIter = res.iterator();

        Transcript currentTranscript = null;
        StringBuffer currentTranscriptBases = new StringBuffer();

        long start = System.currentTimeMillis();
        int i = 0;
        while (resIter.hasNext()) {
           ResultsRow rr = (ResultsRow) resIter.next();
           Transcript transcript =  (Transcript) rr.get(0);

           if (currentTranscript == null || !transcript.equals(currentTranscript)) {
               if (currentTranscript != null) {
                   storeNewSequence(currentTranscript,
                                    currentTranscriptBases.toString());
                   i++;
                   if (i % 100 == 0) {
                       long now = System.currentTimeMillis();
                       LOG.info("Set sequences for " + i + " Transcripts"
                                + " (avg = " + ((60000L * i) / (now - start)) + " per minute)");
                   }
               }
               currentTranscriptBases = new StringBuffer();
               currentTranscript = transcript;
           }

           Sequence exonSequence = (Sequence) rr.get(2);
           Location  location = (Location) rr.get(3);

           if (location.getStrand() != null && location.getStrand().equals("-1")) {
               currentTranscriptBases.insert(0, exonSequence.getResidues());
           } else {
               currentTranscriptBases.append(exonSequence.getResidues());
           }
        }
        if (currentTranscript == null) {
            LOG.error("in transferToTranscripts(): no Transcripts found");
        } else {
            storeNewSequence(currentTranscript, currentTranscriptBases.toString());
        }

        LOG.info("Finished setting " + i + " Trascript sequences - took "
                 + (System.currentTimeMillis() - startTime) + " ms.");

        osw.commitTransaction();
    }
}
