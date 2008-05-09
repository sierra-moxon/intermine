package org.intermine.bio.web;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

/**
 * Export protein interaction information as SIF file suitable for use with
 * CytoScape if there are ProteinInteraction objects on the select list of
 * the query.
 *
 * @author Florian Reisinger
 * @author Richard Smith
 */
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.flymine.model.genomic.ProteinInteraction;
import org.intermine.bio.networkview.FlyNetworkCreator;
import org.intermine.bio.networkview.network.FlyNetwork;
import org.intermine.model.InterMineObject;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.util.StringUtil;
import org.intermine.web.logic.export.ExportException;
import org.intermine.web.logic.export.ExportHelper;
import org.intermine.web.logic.export.http.HttpExportUtil;
import org.intermine.web.logic.export.http.TableHttpExporter;
import org.intermine.web.logic.results.PagedTable;
import org.intermine.web.logic.results.ResultElement;
import org.intermine.web.logic.results.WebTable;

/**
 * An implementation of TableHttpExporter that exports protein interactions
 * in cytoscape SIF format
 *
 * @author Florian Reisinger
 */
public class ProteinInteractionExporter implements TableHttpExporter
{
    static final boolean DEBUG = false;

    /**
     * Method called to export a PagedTable object using the BioJava
     * sequence and feature writers.
     * @param pt exported PagedTable
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     */
    public void export(PagedTable pt, HttpServletRequest request, HttpServletResponse response) {

        response.setContentType("text/plain");
        response.setHeader("Content-Disposition ", "attachment; filename=interaction"
                + StringUtil.uniqueString() + ".sif"); //flo

        int realFeatureIndex = ExportHelper.getFirstColumnForClass(pt, ProteinInteraction.class);

        int writtenInteractionsCount = 0; //flo
        Set exported = new HashSet();
        PrintWriter printWriter = null;

        try {
            WebTable rowList = pt.getAllRows(); // get all the data in rows

            // loop over the rows
            for (int rowIndex = 0; rowIndex < rowList.size()
                    && rowIndex <= pt.getMaxRetrievableIndex(); rowIndex++) {
                List<ResultElement> row;
                try {
                    row = rowList.getResultElements(rowIndex); // einzelne reihe des resultset
                } catch (RuntimeException e) {
                    // re-throw as a more specific exception
                    if (e.getCause() instanceof ObjectStoreException) {
                        throw (ObjectStoreException) e.getCause();
                    } else {
                        throw e;
                    }
                }
                // das objekt aus der gueltigen spalte der aktuellen reihe holen
                // get object of interest - ProteinInteraction
                InterMineObject object =
                    (InterMineObject) row.get(realFeatureIndex).getInterMineObject();

                if (!exported.contains(object.getId())) {
                    // cast to ProteinInteraction
                    ProteinInteraction feature = (ProteinInteraction) object;

                    // try to avoid opening the OutputStream until we know that the query is
                    // going to work - this avoids some problems that occur when
                    // getOutputStream() is called twice (once by this method and again to
                    // write the error)
                    printWriter = HttpExportUtil.getPrintWriterForClient(request, response.getOutputStream());

                    Set<ProteinInteraction> interactions =
                        (Set<ProteinInteraction>) Collections.singleton(feature);
                    printWriter.write(getSifLines(interactions));
                    printWriter.flush();

                    writtenInteractionsCount++; //flo
                    exported.add(object.getId());
                }
            }

            if (printWriter != null) {
                printWriter.flush();
                printWriter.close();
            }

            if (writtenInteractionsCount == 0) {
                throw new ExportException("Nothing was found for export.");
            }
        } catch (Exception e) {
            throw new ExportException("Export failed", e);
        }
    }

    /**
     * {@inheritDoc}
     * @param pt the PagedTable containing the results
     * @return true if exportable results were found
     * @see org.intermine.web.logic.export.TableHttpExporter#canExport
     * @see org.intermine.bio.web.PIUtil#canExport
     */
    public boolean canExport(PagedTable pt) {
        return ExportHelper.canExport(pt, ProteinInteraction.class);
    }

    /**
     * Create lines in sif format
     * @param interactions the interactions
     * @return String respresenting the network in sif format
     */
    public static String getSifLines(Collection<ProteinInteraction> interactions) {
        FlyNetwork fn = FlyNetworkCreator.createFlyNetwork(interactions);
        return fn.toSIF();
    }
}
