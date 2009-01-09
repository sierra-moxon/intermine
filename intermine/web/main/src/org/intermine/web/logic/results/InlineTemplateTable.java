package org.intermine.web.logic.results;

/*
 * Copyright (C) 2002-2009 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.intermine.objectstore.query.ResultsRow;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.results.flatouterjoins.MultiRow;
import org.intermine.web.logic.results.flatouterjoins.MultiRowValue;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * An inline table created by running a template.
 * @author Kim Rutherford
 */
public class InlineTemplateTable implements Serializable
{
    private int inlineSize = -1;
    private List<MultiRow<ResultsRow<MultiRowValue<ResultElement>>>> inlineResults;
    private static final Logger LOG = Logger.getLogger(InlineTemplateTable.class);
    private final List<String> columnNames;
    private int resultsSize = -1;
    private int maxInlineTableSize = 10;
    private PagedTable pagedTable;

    /**
     * Construct a new InlineTemplateTable
     * @param pagedResults the Results of running the template query
     * @param webProperties the web properties from the session
     */
    public InlineTemplateTable(PagedTable pagedResults, Map webProperties) {
        this.columnNames = pagedResults.getColumnNames();
        this.pagedTable = pagedResults;
        String maxInlineTableSizeString =
            (String) webProperties.get(Constants.INLINE_TABLE_SIZE);

        try {
            maxInlineTableSize = Integer.parseInt(maxInlineTableSizeString);
        } catch (NumberFormatException e) {
            LOG.warn("Failed to parse " + Constants.INLINE_TABLE_SIZE + " property: "
                     + maxInlineTableSizeString);
        }

        inlineSize = maxInlineTableSize;

        inlineResults
            = new ArrayList<MultiRow<ResultsRow<MultiRowValue<ResultElement>>>>(inlineSize);

        for (int i = 0; i < inlineSize; i++) {
            try {
                inlineResults.add(pagedResults.getRows().get(i));
            } catch (IndexOutOfBoundsException e) {
                // getExactSize() will now be fast because the Results object knows the size
                break;
            }
        }

        // often fast because we have retreived the first batch
        resultsSize = pagedResults.getExactSize();

        if (resultsSize < inlineSize) {
            inlineSize = resultsSize;
        }
    }

    /**
     * Return headings for the columns
     * @return the column names
     */
    public List<String> getColumnNames() {
        return columnNames;
    }

    /**
     * Return the part of the Results that should be display inline in the object details pages.
     * This is used only in JSP files currently.
     * @return the first getSize() rows from the Results object that was passed to the constructor
     */
    public List<MultiRow<ResultsRow<MultiRowValue<ResultElement>>>> getInlineResults() {
        return inlineResults;
    }

    /**
     * Return the number of rows in the Results object that was passed to the constructor.
     * @return the row count
     */
    public int getResultsSize() {
        return resultsSize;
    }

    /**
     * Get the corresponding PagedTable
     * @return the pagedTable
     */
    public PagedTable getPagedTable() {
        return pagedTable;
    }

    /**
     * Set the PagedTable
     * @param pagedTable the pagedTable to set
     */
    public void setPagedTable(PagedTable pagedTable) {
        this.pagedTable = pagedTable;
    }

}
