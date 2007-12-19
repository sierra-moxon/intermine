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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.biojava.bio.seq.Sequence;

/**
 * A loader that works for FASTA files with an NCBI formatted header:
 * http://www.ncbi.nlm.nih.gov/blast/fasta.shtml
 * http://en.wikipedia.org/wiki/Fasta_format
 * @author Kim Rutherford
 */
public class NCBIFastaLoaderTask extends FastaLoaderTask
{
    private static final Pattern NCBI_DB_PATTERN =
        Pattern.compile("^gi\\|([^\\|]*)\\|(gb|emb|dbj|ref)\\|([^\\|]*?)(\\.\\d+)?\\|.*");
    // private static final Pattern PROT_DB_PATTERN =
    //    Pattern.compile("^(ref|pir|prf)\\|([^\\|]*)\\|([^\\|]*).*");
    private static final Pattern UNIPROT_PATTERN =
        Pattern.compile("^([^\\|]+)\\|([^\\|\\s]*).*");

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getIdentifier(Sequence bioJavaSequence) {
        String seqIdentifier = bioJavaSequence.getName();

        Matcher ncbiMatcher = NCBI_DB_PATTERN.matcher(seqIdentifier);
        if (ncbiMatcher.matches()) {
            return ncbiMatcher.group(3);
        } else {
            Matcher uniprotMatcher = UNIPROT_PATTERN.matcher(seqIdentifier);
            if (uniprotMatcher.matches()) {
                return uniprotMatcher.group(2);
            } else {
                throw new RuntimeException("can't parse FASTA identifier: " + seqIdentifier);
            }
        }
    }
}
