package org.intermine.web.struts;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.config.WebConfig;
import org.intermine.web.logic.export.ExportException;
import org.intermine.web.logic.export.http.TableExporterFactory;
import org.intermine.web.logic.export.http.TableHttpExporter;
import org.intermine.web.logic.results.PagedTable;
import org.intermine.web.logic.results.WebResults;
import org.intermine.web.logic.session.SessionMethods;

/*
 * TODO   
 * - rewrite  web service for new exporters
 * - what to do if more columns with LocatedSequenceFeature ?
 */

/**
 * Implementation of <strong>Action</strong> that allows the user to export a 
 * PagedTable to a file.
 *
 * @author Kim Rutherford
 * @author Jakub Kulaviak 
 */
public class TableExportAction extends InterMineAction
{
    protected static final Logger LOG = Logger.getLogger(TableExportAction.class);

    private static final String ERROR_MSG = "Export failed. Please contact support.";
    
    // timeout for export is 1 day
    private static final int TIMEOUT = 24 * 60 * 60;

    /**
     * Method called to export a PagedTable object.  Uses the type request parameter to choose the
     * export method.
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     *  an exception
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
        throws Exception {
        request.getSession().setMaxInactiveInterval(TIMEOUT);
        String type = request.getParameter("type");
        PagedTable pt = null;
        try {
            TableExporterFactory factory = new TableExporterFactory(getWebConfig(request));
            TableHttpExporter exporter = factory.getExporter(type);
            pt = getPagedTable(request, request.getSession());

            if (pt == null) {
                throw new ExportException("Export failed.");
            }
            
            if (pt.getExactSize() > pt.getMaxRetrievableIndex()) {
                throw new ExportException("Result is too big for export. "
                        + "Table for export can have at the most "
                        + pt.getMaxRetrievableIndex() + " rows.");
            }
            
            
            if (pt.getWebTable() instanceof WebResults) {
                ((WebResults) pt.getWebTable()).goFaster();
            }
            
            exporter.export(pt, request, response);
            // If null is returned then no forwarding is performed and 
            // to the output is not flushed any jsp output, so user
            // will get only required export data
            return null;
        } catch (RuntimeException e) {
            return processException(mapping, request, response, e);            
        } finally {
            if (pt != null && pt.getWebTable() instanceof WebResults) {
                ((WebResults) pt.getWebTable()).releaseGoFaster();
            }
        }
    }

    private WebConfig getWebConfig(HttpServletRequest request) {
        WebConfig wc = (WebConfig) request.getSession().getServletContext().
            getAttribute(Constants.WEBCONFIG);
        if (wc == null) {
            throw new RuntimeException("WebConfig not present in web session.");
        }
        return wc;
    }

    private ActionForward processException(ActionMapping mapping,
            HttpServletRequest request, HttpServletResponse response,
            RuntimeException e) throws IOException {
        LOG.error("Export failed.", e);
        String msg = null;
        if (e instanceof ExportException) {
            msg = e.getMessage();
            if (msg == null || msg.length() == 0) {
                msg = ERROR_MSG;
            }            
        } else {
            msg = ERROR_MSG;
        }        
        // If response wasn't commited then we can display error, else 
        // there is only possibility to append error message to the end.
        if (!response.isCommitted()) {
          PrintWriter writer = null;
          response.reset();
          try {
              // Tricky. This is called to verify, that if the error will be forwarded to jsp 
              // page that it will be displayed. If getWriter() method throws exception there it 
              // means that getOutputStream() method already was called and displaying error message
              // in proper jsp error page would fail.
              response.getWriter();
              recordError(new ActionMessage("errors.export.displayonlyparameters", msg), request);
              return mapping.findForward("error");
          } catch (IllegalStateException ex) {
              OutputStream out = response.getOutputStream();
              writer = new PrintWriter(out);
              writer.println(msg);
              writer.flush();
          }
        } else {
            // Attempt to writer error to output stream where data was already sent.
            // If there are textual data user will see the error else it will
            // makes binary file  probably unreadable and so the user knows
            // that something is wrong.
            // At this moment only excel format exports binary output and 
            // it is flushed at the end - that's why an error can only happen before 
            // response is  commited and user will see the error and won't get the 
            // excel file.
            try {
                PrintWriter writer = response.getWriter();
                writer.println(msg);
                writer.flush();
            } catch (IllegalStateException ex) {
                OutputStream out = response.getOutputStream();
                PrintWriter writer = new PrintWriter(out);
                writer.println(msg);
                writer.flush();
            }
        }
        return null;
    }
    
    /**
     * @param request request
     * @param session session
     * @return PagedTable from session
     */
    protected PagedTable getPagedTable(HttpServletRequest request, HttpSession session) {
        PagedTable pt;
        String tableType = request.getParameter("tableType");
        if (tableType.equals("bag")) {
            pt = SessionMethods.getResultsTable(session, "bag." 
                    + request.getParameter("table"));
        } else {
            pt = SessionMethods.getResultsTable(session, request.getParameter("table"));
        }
        return pt;
    }    
}
