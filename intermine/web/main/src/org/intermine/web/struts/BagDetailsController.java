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

import java.awt.Font;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.tiles.ComponentContext;
import org.apache.struts.tiles.actions.TilesAction;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStore;
import org.intermine.util.TypeUtil;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.bag.BagQueryConfig;
import org.intermine.web.logic.bag.InterMineBag;
import org.intermine.web.logic.config.BagTableDisplayer;
import org.intermine.web.logic.config.EnrichmentWidgetDisplayer;
import org.intermine.web.logic.config.GraphDisplayer;
import org.intermine.web.logic.config.Type;
import org.intermine.web.logic.config.WebConfig;
import org.intermine.web.logic.profile.Profile;
import org.intermine.web.logic.results.PagedTable;
import org.intermine.web.logic.search.SearchRepository;
import org.intermine.web.logic.search.WebSearchable;
import org.intermine.web.logic.session.SessionMethods;
import org.intermine.web.logic.tagging.TagTypes;
import org.intermine.web.logic.template.TemplateHelper;
import org.intermine.web.logic.widget.BagGraphWidget;
import org.intermine.web.logic.widget.BagTableWidgetLoader;
import org.intermine.web.logic.widget.DataSetLdr;
import org.intermine.web.logic.widget.GraphDataSet;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.urls.CategoryURLGenerator;

/**
 * @author Xavier Watkins
 */
public class BagDetailsController extends TilesAction
{

    /**
     * {@inheritDoc}
     */
    public ActionForward execute(@SuppressWarnings("unused") ComponentContext context,
                                 @SuppressWarnings("unused") ActionMapping mapping,
                                 @SuppressWarnings("unused") ActionForm form,
                                 HttpServletRequest request,
                                 @SuppressWarnings("unused") HttpServletResponse response)
                    throws Exception {

            HttpSession session = request.getSession();
            ServletContext servletContext = session.getServletContext();
            ObjectStore os = (ObjectStore) servletContext.getAttribute(Constants.OBJECTSTORE);

            String bagName = request.getParameter("bagName");
            Boolean myBag = Boolean.FALSE;
            if (bagName == null) {
                bagName = request.getParameter("name");
            }

            InterMineBag imBag = null;
            String scope = request.getParameter("scope");
            if (scope == null) {
                scope = TemplateHelper.ALL_TEMPLATE;
            }

            if (scope.equals(TemplateHelper.USER_TEMPLATE)
                || scope.equals(TemplateHelper.ALL_TEMPLATE)) {
                Profile profile = (Profile) session.getAttribute(Constants.PROFILE);
                imBag = profile.getSavedBags().get(bagName);
                if (imBag != null) {
                    myBag = Boolean.TRUE;
                }
            }

            if (scope.equals(TemplateHelper.GLOBAL_TEMPLATE)
                || scope.equals(TemplateHelper.ALL_TEMPLATE)) {
                // scope == all or global
                SearchRepository searchRepository =
                    SearchRepository.getGlobalSearchRepository(servletContext);
                Map<String, ? extends WebSearchable> publicBagMap =
                    searchRepository.getWebSearchableMap(TagTypes.BAG);
                if (publicBagMap.get(bagName) != null) {
                    imBag = (InterMineBag) publicBagMap.get(bagName);
                }
            }

            /* forward to bag page if this is an invalid bag */
            if (imBag == null) {
                return null;
            }

            Map classKeys = (Map) servletContext.getAttribute(Constants.CLASS_KEYS);
            WebConfig webConfig = (WebConfig) servletContext.getAttribute(Constants.WEBCONFIG);
            Model model = os.getModel();
            Type type = (Type) webConfig.getTypes().get(model.getPackageName()
                                                        + "." + imBag.getType());

            Set graphDisplayers = type.getGraphDisplayers();
            ArrayList<String[]> graphDisplayerArray = new ArrayList<String[]>();
            for (Iterator iter = graphDisplayers.iterator(); iter.hasNext();) {

                try {

                    GraphDisplayer graphDisplayer = (GraphDisplayer) iter.next();
                    String dataSetLoader = graphDisplayer.getDataSetLoader();
                    Class clazz = TypeUtil.instantiate(dataSetLoader);
                    Constructor constr = clazz.getConstructor(new Class[]
                                                                        {
                        InterMineBag.class, ObjectStore.class
                                                                        });

                    DataSetLdr dataSetLdr = (DataSetLdr) constr.newInstance(new Object[]
                                                                                       {
                        imBag, os
                                                                                       });

                    //TODO use caching here
                    if (!dataSetLdr.getDataSets().isEmpty()) {
                        for (Iterator it
                                  = dataSetLdr.getDataSets().keySet().iterator(); it.hasNext();) {
                            String key = (String) it.next();
                            GraphDataSet graphDataSet
                                                = (GraphDataSet) dataSetLdr.getDataSets().get(key);
                            /* stacked bar chart */
                            if (graphDisplayer.getGraphType().equals("StackedBarChart")) {
                                setStackedBarGraph(session, graphDisplayer, graphDataSet,
                                                   graphDisplayerArray, imBag);
                            /* regular bar chart */
                            } else {
                                setBarGraph(os, session, graphDisplayer, graphDataSet,
                                            graphDisplayerArray, imBag, key);
                            }
                        }
                    }
                } catch  (Exception e) {
                    // TODO do something clever
                    //return null;
                    //throw new Exception(e);
                }
            }

            ArrayList<BagTableWidgetLoader> tableDisplayerArray
                                                           = new ArrayList<BagTableWidgetLoader>();
            Set bagTabledisplayers = type.getBagTableDisplayers();
            for (Iterator iter = bagTabledisplayers.iterator(); iter.hasNext();) {
                try {
                    BagTableDisplayer bagTableDisplayer = (BagTableDisplayer) iter.next();
                    String ldrType = bagTableDisplayer.getType();
                    String collectionName = bagTableDisplayer.getCollectionName();
                    String fields = bagTableDisplayer.getFields();
                    String title = bagTableDisplayer.getTitle();
                    String description = bagTableDisplayer.getDescription();
                    String urlGen = bagTableDisplayer.getUrlGen();
                    BagTableWidgetLoader bagWidgLdr =
                        new BagTableWidgetLoader(title, description, ldrType, collectionName,
                                                 imBag, os, webConfig, model,
                                                 classKeys, fields, urlGen);
                    tableDisplayerArray.add(bagWidgLdr);

                } catch  (Exception e) {
                    // TODO do something clever
                    //return null;
                    //throw new Exception();
                }
            }

            ArrayList<EnrichmentWidgetDisplayer> enrichmentWidgetDisplayerArray
            = new ArrayList<EnrichmentWidgetDisplayer>();
            Set enrichmentWidgetDisplayers = type.getEnrichmentWidgetDisplayers();
            for (Iterator iter = enrichmentWidgetDisplayers.iterator(); iter.hasNext();) {
                EnrichmentWidgetDisplayer d = (EnrichmentWidgetDisplayer) iter.next();
                enrichmentWidgetDisplayerArray.add(d);
            }

            
            PagedTable pagedResults = SessionMethods.doQueryGetPagedTable(request, 
                                                    servletContext, imBag);

            // TODO this needs to be removed when InterMineBag can store the initial ids of when the
            // bag was made.
            BagQueryConfig bagQueryConfig =
                (BagQueryConfig) servletContext.getAttribute(Constants.BAG_QUERY_CONFIG);
            Map<String, String []> additionalConverters =
                bagQueryConfig.getAdditionalConverters(imBag.getType());
            if (additionalConverters != null) {
                for (String converterClassName : additionalConverters.keySet()) {
                    String [] paramArray = additionalConverters.get(converterClassName);
                    String [] urlFields = paramArray[0].split(",");
                    for (int i = 0; i < urlFields.length; i++) {
                        if (request.getParameter(urlFields[i]) != null) {
                            request.setAttribute("extrafield", urlFields[i]);
                            request.setAttribute(urlFields[i], request.getParameter(urlFields[i]));
                            request.setAttribute("externalids",
                                                 request.getParameter("externalids"));
                            break;
                        }
                    }
                }
            }

            request.setAttribute("addparameter", request.getParameter("addparameter"));
            request.setAttribute("myBag", myBag);
            request.setAttribute("bag", imBag);
            request.setAttribute("bagSize", new Integer(imBag.size()));
            request.setAttribute("pagedResults", pagedResults);
            request.setAttribute("graphDisplayerArray", graphDisplayerArray);
            request.setAttribute("tableDisplayerArray", tableDisplayerArray);
            request.setAttribute("enrichmentWidgetDisplayerArray", enrichmentWidgetDisplayerArray);

            return null;
    }


    private void setBarGraph(@SuppressWarnings("unused") ObjectStore os,
                             HttpSession session,
                             GraphDisplayer graphDisplayer,
                             GraphDataSet graphDataSet,
                             ArrayList<String[]> graphDisplayerArray,
                             InterMineBag bag,
                             String subtitle) {
        JFreeChart chart = null;
        CategoryPlot plot = null;
        BagGraphWidget bagGraphWidget = null;

        chart = ChartFactory.createBarChart(
                graphDisplayer.getTitle(),          // chart title
                graphDisplayer.getDomainLabel(),    // domain axis label
                graphDisplayer.getRangeLabel(),     // range axis label
                graphDataSet.getDataSet(),            // data
                PlotOrientation.VERTICAL,
                true,
                true,                               // tooltips?
                false                               // URLs?
        );


        if (!subtitle.startsWith("any")) {
            TextTitle subtitleText = new TextTitle(subtitle);
            subtitleText.setFont(new Font("SansSerif", Font.ITALIC, 10));
            chart.addSubtitle(subtitleText);
        }
        plot = chart.getCategoryPlot();

        BarRenderer renderer = new BarRenderer();

        renderer.setItemMargin(0);
        plot.setRenderer(renderer);
        CategoryURLGenerator categoryUrlGen = null;
        // set series 0 to have URLgenerator specified in config file
        // set series 1 to have no URL generator.
        try {
            Class clazz2 = TypeUtil.instantiate(graphDisplayer.getUrlGen());
            Constructor urlGenConstructor = clazz2.getConstructor(new Class[]
                                                                            {
                String.class, String.class
                                                                            });
            categoryUrlGen = (CategoryURLGenerator) urlGenConstructor
            .newInstance(new Object[]
                                    {
                bag.getName(), subtitle
                                    });

        } catch (Exception err) {
            err.printStackTrace();
        }

        //renderer.setItemURLGenerator(null);
        renderer.setSeriesItemURLGenerator(0, categoryUrlGen);
        renderer.setSeriesItemURLGenerator(1, categoryUrlGen);

        // integers only
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        bagGraphWidget = new BagGraphWidget(session,
                         graphDataSet.getCategoryArray(),
                         bag.getName(),
                         graphDisplayer.getToolTipGen(),
                         null,
                         chart,
                         plot,
                         renderer);

        graphDisplayerArray.add(new String[] {
            bagGraphWidget.getHTML(), graphDisplayer.getTitle(), graphDisplayer.getDescription()
        });
    }

    private void setStackedBarGraph(HttpSession session,
                                    GraphDisplayer graphDisplayer,
                                    GraphDataSet graphDataSet,
                                    ArrayList<String[]> graphDisplayerArray,
                                    InterMineBag bag) {

        JFreeChart chart = null;
        CategoryPlot plot = null;
        BagGraphWidget bagGraphWidget = null;

        chart = ChartFactory.createStackedBarChart(
                graphDisplayer.getTitle(),       // chart title
                graphDisplayer.getDomainLabel(), // domain axis label
                graphDisplayer.getRangeLabel(),  // range axis label
                graphDataSet.getDataSet(),         // data
                PlotOrientation.VERTICAL,
                true,
                true,                            // tooltips?
                false                            // URLs?
        );
        plot = chart.getCategoryPlot();
        StackedBarRenderer renderer = (StackedBarRenderer) plot.getRenderer();

        // integers only
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        bagGraphWidget = new BagGraphWidget(session,
                         graphDataSet.getCategoryArray(),
                         bag.getName(),
                         graphDisplayer.getToolTipGen(),
                         graphDisplayer.getUrlGen(),
                         chart,
                         plot,
                         renderer);

        graphDisplayerArray.add(new String[] {
            bagGraphWidget.getHTML(), graphDisplayer.getTitle(), graphDisplayer.getDescription()
        });
    }
}

