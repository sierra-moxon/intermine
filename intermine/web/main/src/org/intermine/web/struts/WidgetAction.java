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

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;
import org.intermine.metadata.Model;
import org.intermine.model.InterMineObject;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.ResultsRow;
import org.intermine.util.TypeUtil;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.WebUtil;
import org.intermine.web.logic.bag.InterMineBag;
import org.intermine.web.logic.config.Type;
import org.intermine.web.logic.config.WebConfig;
import org.intermine.web.logic.export.ResponseUtil;
import org.intermine.web.logic.export.http.HttpExportUtil;
import org.intermine.web.logic.export.rowformatters.CSVRowFormatter;
import org.intermine.web.logic.export.rowformatters.TabRowFormatter;
import org.intermine.web.logic.export.string.StringTableExporter;
import org.intermine.web.logic.profile.Profile;
import org.intermine.web.logic.query.PathQuery;
import org.intermine.web.logic.query.QueryMonitorTimeout;
import org.intermine.web.logic.session.SessionMethods;
import org.intermine.web.logic.widget.EnrichmentWidgetLdr;
import org.intermine.web.logic.widget.Widget;
import org.intermine.web.logic.widget.WidgetURLQuery;
/**
 * Runs a query based on which record the user clicked on in the widget.  Used by bag table and
 * enrichment widgets.
 * @author Julie Sullivan
 */
public class WidgetAction extends InterMineAction
{
    /**
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     *  an exception
     */
    public ActionForward execute(ActionMapping mapping,
                                 @SuppressWarnings("unused") ActionForm form,
                                 HttpServletRequest request,
                                 @SuppressWarnings("unused") HttpServletResponse response)
    throws Exception {
        WidgetForm widgetForm = (WidgetForm) form;
        String key = request.getParameter("key");
        if ((key != null && key.length() != 0) || (widgetForm.getAction().equals("display"))) {
            return display(mapping, form, request, response);
        } else if (widgetForm.getAction().equals("notAnalysed")) {
            return notAnalysed(mapping, form, request, response);
        } else {
            return export(mapping, form, request, response);
        }
    }


    /**
     * Display selected entries in the results page
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     *  an exception
     */
    public ActionForward notAnalysed(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                @SuppressWarnings("unused")
                                HttpServletResponse response) throws Exception {

        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        ObjectStore os = (ObjectStore) servletContext.getAttribute(Constants.OBJECTSTORE);
        WebConfig webConfig = (WebConfig) servletContext.getAttribute(Constants.WEBCONFIG);
        Model model = os.getModel();

        WidgetForm wf = (WidgetForm) form;
        String bagName = wf.getBagName();
        String ldr = null, urlQuery = null;
        String widgetId = wf.getWidgetid();
        String selectedExtraAttribute = null;

        Type type = (Type) webConfig.getTypes().get(model.getPackageName()
                                                    + "." + wf.getBagType());
        List<Widget> widgets = type.getWidgets();
        for (Widget widget : widgets) {
            if (widget.getId() == (new Integer(widgetId).intValue())) {
                selectedExtraAttribute = widget.getSelectedExtraAttribute();
                ldr = widget.getDataSetLoader();
                urlQuery = widget.getLink();
            }
        }

        Profile currentProfile = (Profile) session.getAttribute(Constants.PROFILE);
        Map<String, InterMineBag> allBags =
            WebUtil.getAllBags(currentProfile.getSavedBags(), servletContext);
        InterMineBag bag = allBags.get(bagName);

        Class<?> clazz = TypeUtil.instantiate(ldr);

        Constructor<?> constr = clazz.getConstructor(new Class[]
                                                               {
            InterMineBag.class, ObjectStore.class, String.class
                                                               });

        EnrichmentWidgetLdr enrichmentWidgetLdr = (EnrichmentWidgetLdr)
        constr.newInstance(new Object[]
                                                         {
            bag, os, selectedExtraAttribute
                                                         });

        Query q = enrichmentWidgetLdr.getQuery("analysed", null);

        // TODO use singleton get list
        Iterator iter = os.execute(q).iterator();
        Collection<InterMineObject> widgetObjects = new ArrayList<InterMineObject>();
        while (iter.hasNext()) {
            ResultsRow resRow = (ResultsRow) iter.next();
            InterMineObject o = os.getObjectById((Integer) resRow.get(0));
            if (!widgetObjects.contains(o)) {
                widgetObjects.add(o);
            }
        }

        clazz = TypeUtil.instantiate(urlQuery);

        constr = clazz.getConstructor(new Class[]
                                                               {
            ObjectStore.class, InterMineBag.class, String.class
                                                               });

        WidgetURLQuery widgetURLQuery = (WidgetURLQuery)
        constr.newInstance(new Object[]
                                                         {
            os, bag, null
                                                         });

        // run query
        PathQuery pathQuery = widgetURLQuery.generatePathQuery(widgetObjects);

        QueryMonitorTimeout clientState
        = new QueryMonitorTimeout(Constants.QUERY_TIMEOUT_SECONDS * 1000);
        MessageResources messages
        = (MessageResources) request.getAttribute(Globals.MESSAGES_KEY);

        SessionMethods.loadQuery(pathQuery, session, response);
        String qid = SessionMethods.startQuery(clientState, session, messages, true, pathQuery);
        Thread.sleep(200); // slight pause in the hope of avoiding holding page
        return new ForwardParameters(mapping.findForward("waiting")).addParameter("trail",
                        "|bag." + bagName).addParameter("qid", qid).forward();
    }

    /**
     * Display selected entries in the results page
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     *  an exception
     */
    public ActionForward display(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                @SuppressWarnings("unused")
                                HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        ObjectStore os = (ObjectStore) servletContext.getAttribute(Constants.OBJECTSTORE);
        String bagName = request.getParameter("bagName");
        String key = request.getParameter("key");
        String link = request.getParameter("link");
        if (key == null || key.equals("")) {
            WidgetForm wf = (WidgetForm) form;
            bagName = wf.getBagName();
            key = wf.getSelectedAsString();
        }

        Profile currentProfile = (Profile) session.getAttribute(Constants.PROFILE);
        Map<String, InterMineBag> allBags =
            WebUtil.getAllBags(currentProfile.getSavedBags(), servletContext);
        InterMineBag bag = allBags.get(bagName);

        Class<?> clazz = TypeUtil.instantiate(link);
        Constructor<?> constr = clazz.getConstructor(new Class[]
                                                               {
            ObjectStore.class, InterMineBag.class, String.class
                                                               });

        WidgetURLQuery urlQuery = (WidgetURLQuery) constr.newInstance(new Object[]
                                                         {
            os, bag, key
                                                         });

        QueryMonitorTimeout clientState
        = new QueryMonitorTimeout(Constants.QUERY_TIMEOUT_SECONDS * 1000);
        MessageResources messages
        = (MessageResources) request.getAttribute(Globals.MESSAGES_KEY);
        PathQuery pathQuery = urlQuery.generatePathQuery(null);
        SessionMethods.loadQuery(pathQuery, session, response);
        String qid = SessionMethods.startQuery(clientState, session, messages, true, pathQuery);
        Thread.sleep(200); // slight pause in the hope of avoiding holding page
        return new ForwardParameters(mapping.findForward("waiting")).addParameter("trail",
                        "|bag." + bagName).addParameter("qid", qid).forward();
    }

    /**
     * Export selected entries
     * @param mapping The ActionMapping used to select this instance
     * @param form The optional ActionForm bean for this request (if any)
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     * @return an ActionForward object defining where control goes next
     * @exception Exception if the application business logic throws
     *  an exception
     */
    public ActionForward export(ActionMapping mapping,
                                ActionForm form,
                                HttpServletRequest request,
                                @SuppressWarnings("unused")
                                HttpServletResponse response) throws Exception {
        WidgetForm widgetForm = (WidgetForm) form;
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        WebConfig webConfig = (WebConfig) servletContext.getAttribute(Constants.WEBCONFIG);
        ObjectStore os = (ObjectStore) servletContext.getAttribute(Constants.OBJECTSTORE);
        Model model = os.getModel();

        String widgetId = widgetForm.getWidgetid();
        Type type = (Type) webConfig.getTypes().get(
                        model.getPackageName() + "." + widgetForm.getBagType());
        List<Widget> widgets = type.getWidgets();
        for (Widget widget : widgets) {
            if (widget.getId() == (new Integer(widgetId).intValue())) {
                StringTableExporter stringExporter;
                PrintWriter writer = HttpExportUtil.
                    getPrintWriterForClient(request, response.getOutputStream());
                if (widgetForm.getExporttype().equals("csv")) {
                    stringExporter = new StringTableExporter(writer, new CSVRowFormatter());
                    ResponseUtil.setCSVHeader(response, "widget" + widgetForm.getWidgetid()
                                                        + ".csv");
                } else {
                    stringExporter = new StringTableExporter(writer, new TabRowFormatter());
                    ResponseUtil.setTabHeader(response, "widget" + widgetForm.getWidgetid()
                                                        + ".tsv");
                }
                stringExporter.export(widget.getExportResults(widgetForm.getSelected()));
            }
        }
        return null;
    }
}
