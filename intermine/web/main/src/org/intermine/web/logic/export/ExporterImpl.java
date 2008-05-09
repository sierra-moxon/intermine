package org.intermine.web.logic.export;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.intermine.objectstore.flatouterjoins.ReallyFlatIterator;
import org.intermine.web.logic.results.ResultElement;


/**
 * Simple exporter exporting data as tab separated, comma separated 
 * and so. It depends at used row formatter.
 * @author Jakub Kulaviak
 **/
public class ExporterImpl implements Exporter
{

    private PrintWriter out;
    
    private RowFormatter rowFormatter;
    
    private int writtenResultsCount = 0;
    
    /**
     * Constructor.
     * @param out output stream
     * @param rowFormatter used row formatter.
     */
    public ExporterImpl(OutputStream out, RowFormatter rowFormatter) {
        this.out = new PrintWriter(out);
        this.rowFormatter = rowFormatter;
    }

    /**
     * Constructor.
     * @param out output stream
     * @param rowFormatter used row formatter.
     * @param separator line separator
     */
    public ExporterImpl(OutputStream out, RowFormatter rowFormatter, String separator) {
        if (separator.equals(Exporter.WINDOWS_SEPARATOR)) {
            this.out = new CustomPrintWriter(out, Exporter.WINDOWS_SEPARATOR);
        } else {
            this.out = new PrintWriter(out);
        }
        this.rowFormatter = rowFormatter;
    }

    /**
     * Exports results.
     * @param results results to be exported
     */
    public void export(List<List<ResultElement>> results) {
        try {
            ResultElementConverter converter = new ResultElementConverter();
            Iterator<List<ResultElement>> rowIter = new ReallyFlatIterator(results.iterator());
            while (rowIter.hasNext()) {
                List<ResultElement> result = rowIter.next();
                out.println(rowFormatter.format(converter.convert(result)));
                writtenResultsCount++;
            }
            out.flush();
        } catch (RuntimeException e) {
            throw new ExportException("Export failed.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getWrittenResultsCount() {
        return writtenResultsCount;
    }
    
    /**
     * {@inheritDoc}
     * Universal exporter. 
     * @return always true
     */
    public boolean canExport(List<Class> clazzes) {
        return true;
    }
}
