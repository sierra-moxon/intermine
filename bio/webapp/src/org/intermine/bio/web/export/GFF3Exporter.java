package org.intermine.bio.web.export;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flymine.model.genomic.LocatedSequenceFeature;
import org.intermine.bio.io.gff3.GFF3Record;
import org.intermine.bio.util.GFF3Util;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.util.IntPresentSet;
import org.intermine.util.TypeUtil;
import org.intermine.util.TypeUtil.FieldInfo;
import org.intermine.web.logic.export.ExportException;
import org.intermine.web.logic.export.ExportHelper;
import org.intermine.web.logic.export.Exporter;
import org.intermine.web.logic.results.ResultElement;

/**
 * Exports LocatedSequenceFeature objects in GFF3 format. 
 * @author Kim Rutherford
 * @author Jakub Kulaviak
 */
public class GFF3Exporter implements Exporter
{

    PrintWriter out;
    private int featureIndex;
    private Map soClassNames;
    private int writtenResultsCount = 0;
    private boolean headerPrinted = false;
    private IntPresentSet exportedIds = new IntPresentSet();

    /**
     * Constructor.
     * @param out output stream
     * @param featureIndex index of column with exported sequence
     * @param soClassNames mapping
     */
    public GFF3Exporter(PrintWriter out, int featureIndex, Map soClassNames) {
        this.out = out;
        this.featureIndex = featureIndex;
        this.soClassNames = soClassNames;
    }

    /**
     * {@inheritDoc}
     */
    public void export(List<List<ResultElement>> results) {
        try {
            for (int i = 0; i < results.size(); i++) {
                List<ResultElement> row = results.get(i);
                exportRow(row);
            }
            out.flush();
        } catch (Exception ex) {
            throw new ExportException("Export failed.", ex);
        }
    }

    private void exportRow(List<ResultElement> row) throws ObjectStoreException, 
        IllegalAccessException {
        LocatedSequenceFeature lsf = (LocatedSequenceFeature) row.get(
                featureIndex).getInterMineObject();
        
        if (exportedIds.contains(lsf.getId())) {
            return;
        } 
        
        Map<String, List<String>> extraAttributes = new HashMap<String, List<String>>();

        // add some fields as extra attributes if the object has them

        List<String> extraFields = Arrays.asList(new String[] {"symbol",
                "primaryIdentifier", "name" });
        for (String fieldName : extraFields) {
            FieldInfo field = TypeUtil.getFieldInfo(lsf.getClass(), fieldName);
            if (field != null
                    && (TypeUtil.getFieldValue(lsf, fieldName) != null)) {
                List<String> values = new ArrayList<String>();
                values.add((String) TypeUtil.getFieldValue(lsf, fieldName));
                extraAttributes.put(fieldName, values);
            }
        }

        GFF3Record gff3Record = GFF3Util.makeGFF3Record(lsf, soClassNames,
                extraAttributes);

        if (gff3Record == null) {
            // no chromsome ref or no chromosomeLocation ref
            return;
        }

        if (!headerPrinted) {
            out.println("##gff-version 3");
            headerPrinted = true;
        }
        
        out.println(gff3Record.toGFF3());
        exportedIds.add(lsf.getId());
        writtenResultsCount++;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canExport(List<Class> clazzes) {
        return canExportStatic(clazzes);
    }
    
    /* Method must have different name than canExport because canExport() method 
     * is  inherited from Exporter interface */
    /**
     * @param clazzes classes of result row
     * @return true if this exporter can export result composed of specified classes 
     */    
    public static boolean canExportStatic(List<Class> clazzes) {
        return ExportHelper.getClassIndex(clazzes, LocatedSequenceFeature.class) >= 0;
    }

    /**
     * {@inheritDoc}
     */
    public int getWrittenResultsCount() {
        return writtenResultsCount;
    }    
}
