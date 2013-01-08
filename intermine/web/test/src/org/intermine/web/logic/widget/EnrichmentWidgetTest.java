package org.intermine.web.logic.widget;

/*
 * Copyright (C) 2002-2013 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.intermine.api.profile.InterMineBag;
import org.intermine.metadata.Model;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.pathquery.PathConstraint;
import org.intermine.pathquery.PathQuery;
import org.intermine.web.logic.widget.config.EnrichmentWidgetConfig;
import org.intermine.web.logic.widget.config.WidgetConfig;
import org.intermine.web.logic.widget.config.WidgetConfigUtil;
import org.intermine.webservice.server.exceptions.ResourceNotFoundException;

public class EnrichmentWidgetTest extends WidgetConfigTestCase
{
    private static Logger LOG = Logger.getLogger(EnrichmentWidgetTest.class);
    
    private EnrichmentWidget widget;
    private String MAX = "1.0";
    private String CORRECTION = "Bonferroni";
    
    private InterMineBag bag;
    private WidgetConfig config;
    private String filter;
    private EnrichmentResults results;

    public void setUp() throws Exception {
        super.setUp();
        config = webConfig.getWidgets().get("contractor_enrichment_with_filter1");
        InterMineBag employeeList = createEmployeeList();
        bag = employeeList;
        widget = new EnrichmentWidget((EnrichmentWidgetConfig) config, bag, null, os, "", MAX, CORRECTION, null);
    }
    
    
    

    public void testValidateBagType() throws Exception {
        InterMineBag companyList = createCompanyList();
        WidgetConfig config = webConfig.getWidgets().get("contractor_enrichment_with_filter1");
        try {
            new EnrichmentWidget((EnrichmentWidgetConfig) config, companyList, null, os, "", MAX, CORRECTION, null);
            fail("Should raise a ResourceNotFoundException");
        } catch (ResourceNotFoundException rnfe){
        }
    }

    public void testProcess() throws Exception {
        EnrichmentWidgetImplLdr ldr 
            = new EnrichmentWidgetImplLdr(bag, null, os, (EnrichmentWidgetConfig) config, filter, false, null);
        EnrichmentInput input = new EnrichmentInputWidgetLdr(os, ldr);
        Double maxValue = Double.parseDouble(MAX);
        results = EnrichmentCalculation.calculate(input, maxValue, CORRECTION, false, null);
        List<List<Object>> exportResults = getResults();

        assertEquals(10, exportResults.size());
    }

    public boolean getHasResults() {
        return results.getPValues().size() > 0;
    }

    public List<List<Object>> getResults() throws Exception {
        List<List<Object>> exportResults = new LinkedList<List<Object>>();
        if (results != null) {
            Map<String, BigDecimal> pValues = results.getPValues();
            Map<String, Integer> counts = results.getCounts();
            Map<String, String> labels = results.getLabels();
            for (String id : pValues.keySet()) {
                List<Object> row = new LinkedList<Object>();
                row.add(id);
                row.add(labels.get(id));
                row.add(pValues.get(id).doubleValue());
                row.add(counts.get(id));
                exportResults.add(row);
            }
        }
        return exportResults;
    }

    /**
     * Returns the pathquery based on the views set in config file and the bag constraint
     * Executed when the user selects any item in the matches column in the enrichment widget.
     * @return the query generated
     */
    public PathQuery getPathQuery() {
        PathQuery q = createPathQueryView();
        // bag constraint
        q.addConstraint(Constraints.in(config.getStartClass(), bag.getName()));
        //constraints for view (bdgp_enrichment)
        List<PathConstraint> pathConstraintsForView =
            ((EnrichmentWidgetConfig) config).getPathConstraintsForView();
        if (pathConstraintsForView != null) {
            for (PathConstraint pc : pathConstraintsForView) {
                q.addConstraint(pc);
            }
        }
        //add type constraints for subclasses
        String enrichIdentifier = ((EnrichmentWidgetConfig) config).getEnrichIdentifier();
        boolean subClassContraint = false;
        String subClassType = "";
        String subClassPath = "";
        if (enrichIdentifier != null && !"".equals(enrichIdentifier)) {
            enrichIdentifier = config.getStartClass() + "."
                + ((EnrichmentWidgetConfig) config).getEnrichIdentifier();
        } else {
            String enrichPath = config.getStartClass() + "."
                + ((EnrichmentWidgetConfig) config).getEnrich();
            if (WidgetConfigUtil.isPathContainingSubClass(os.getModel(), enrichPath)) {
                subClassContraint = true;
                subClassType = enrichPath.substring(enrichPath.indexOf("[") + 1,
                                                    enrichPath.indexOf("]"));
                subClassPath = enrichPath.substring(0, enrichPath.indexOf("["));
                enrichIdentifier = subClassPath + enrichPath.substring(enrichPath.indexOf("]") + 1);
            } else {
                enrichIdentifier = enrichPath;
            }
        }
        if (subClassContraint) {
            q.addConstraint(Constraints.type(subClassPath, subClassType));
        }
        return q;
    }

    /**
     * Returns the pathquery based on the view set in config file in the startClassDisplay
     * and the bag constraint
     * Executed when the user click on the matches column in the enrichment widget.
     * @return the query generated
     */
    public PathQuery getPathQueryForMatches() {
        Model model = os.getModel();
        PathQuery pathQuery = new PathQuery(model);
        String enrichIdentifier;
        boolean subClassContraint = false;
        String subClassType = "";
        String subClassPath = "";
        EnrichmentWidgetConfig ewc = ((EnrichmentWidgetConfig) config);
        if (((EnrichmentWidgetConfig) config).getEnrichIdentifier() != null) {
            enrichIdentifier = config.getStartClass() + "."
                + ((EnrichmentWidgetConfig) config).getEnrichIdentifier();
        } else {
            String enrichPath = config.getStartClass() + "."
                + ((EnrichmentWidgetConfig) config).getEnrich();
            if (WidgetConfigUtil.isPathContainingSubClass(model, enrichPath)) {
                subClassContraint = true;
                subClassType = enrichPath.substring(enrichPath.indexOf("[") + 1,
                                                    enrichPath.indexOf("]"));
                subClassPath = enrichPath.substring(0, enrichPath.indexOf("["));
                enrichIdentifier = subClassPath + enrichPath.substring(enrichPath.indexOf("]") + 1);
            } else {
                enrichIdentifier = enrichPath;
            }
        }

        String startClassDisplayView = config.getStartClass() + "."
            + ((EnrichmentWidgetConfig) config).getStartClassDisplay();
        pathQuery.addView(enrichIdentifier);
        pathQuery.addView(startClassDisplayView);
        pathQuery.addOrderBy(enrichIdentifier, OrderDirection.ASC);
        // bag constraint
        pathQuery.addConstraint(Constraints.in(config.getStartClass(), bag.getName()));
        //subclass constraint
        if (subClassContraint) {
            pathQuery.addConstraint(Constraints.type(subClassPath, subClassType));
        }
        //constraints for view
        List<PathConstraint> pathConstraintsForView =
            ((EnrichmentWidgetConfig) config).getPathConstraintsForView();
        if (pathConstraintsForView != null) {
            for (PathConstraint pc : pathConstraintsForView) {
                pathQuery.addConstraint(pc);
            }
        }
        return pathQuery;
    }

    private PathQuery createPathQueryView() {
        PathQuery pathQuery = new PathQuery(os.getModel());
        pathQuery.addView("Employee.name");
        pathQuery.addView("Employee.age");
        pathQuery.addView("Employee.department.name");
        return pathQuery;
    }

    public void testCreatePathQueryView() {
        PathQuery pathQuery = createPathQueryView();
        assertEquals(pathQuery, widget.createPathQueryView(os,
                webConfig.getWidgets().get(("contractor_enrichment"))));
    }
}
