package org.flymine.dataconversion;

/*
 * Copyright (C) 2002-2005 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import org.intermine.metadata.Model;

import org.flymine.io.gff3.GFF3Record;

/**
 * A converter/retriever for the Drosophila tiling path GFF3 files.
 *
 * @author Kim Rutherford
 */

public class TilingPathGFF3RecordHandler extends GFF3RecordHandler
{
    /**
     * Create a new TilingPathGFF3RecordHandler for the given target model.
     * @param tgtModel the model for which items will be created
     */
    public TilingPathGFF3RecordHandler (Model tgtModel) {
        super(tgtModel);
    }

    /**
     * @see GFF3RecordHandler#process()
     */
    public void process(GFF3Record record) {

    }
}
