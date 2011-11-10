package org.intermine.web.logic.widget;

/*
 * Copyright (C) 2002-2011 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.Results;
import org.intermine.objectstore.query.ResultsRow;

/**
 * Executes queries and summarises data for a specific EnrichmentWidgetLdr ready for calculation.
 *
 * @author Richard Smith
 */
public class EnrichmentInputWidgetLdr implements EnrichmentInput
{
    private final EnrichmentWidgetLdr ldr;
    private final ObjectStore os;
    private Map<String, Integer> sampleCounts = null;
    private Map<String, Integer> populationCounts = null;
    private Map<String, String> labels = null;
    private static final int BATCH_SIZE = 20000;

    // TODO population counts and sizes are no longer cached

    // TODO make a static cache from populate query to size and populationCounts maps,
    // this should only have a few entries per widget depending on the organism composition
    // of bags and other parameters, e.g. GO namespace.  But log cache size for a bit to make
    // sure.  This should be a CacheMap, old cache was just a standard Map.

    /**
     * Construct with an EnrichmentWidgetLdr that contains queries needed for specific widget tests.
     * This class executes queries and summarises data ready for calculation.
     * @param os the ObjectStore to execute queries in
     * @param ldr queries to fetch data for a specific widget
     */
    public EnrichmentInputWidgetLdr(ObjectStore os, EnrichmentWidgetLdr ldr) {
        this.os = os;
        this.ldr = ldr;
    }

    @Override
    public Map<String, Integer> getAnnotatedCountsInPopulation() {
        if (populationCounts == null) {
            populationCounts = new HashMap<String, Integer>();
            Query query = ldr.getPopulationQuery(false);

            Results results = os.execute(query, BATCH_SIZE, true, true, true);
            Iterator iter = results.iterator();
            while (iter.hasNext()) {
                ResultsRow row =  (ResultsRow) iter.next();

                // an identifier for an attribute value, e.g. a department name
                String identifier = String.valueOf(row.get(0));

                // the number of times the item is applied in the population, e.g. the number of
                // companies that contain a department with this name
                // TODO should check that casting from a long gives correct result
                Integer count = ((Long) row.get(1)).intValue();

                populationCounts.put(identifier, count);

            }
        }
        return populationCounts;
    }

    @Override
    public Map<String, Integer> getAnnotatedCountsInSample() {
        if (sampleCounts == null) {
            sampleCounts = new HashMap<String, Integer>();
            labels = new HashMap<String, String>();

            Query query = ldr.getSampleQuery(false);

            Results results = os.execute(query, BATCH_SIZE, true, true, true);
            Iterator iter = results.iterator();
            while (iter.hasNext()) {
                ResultsRow row =  (ResultsRow) iter.next();

                // an identifier for an attribute value, e.g. a department name
                String identifier = String.valueOf(row.get(0));

                // the number of times the item is applied in the sample, e.g. the number of
                // companies that contain a department with this name
                // TODO should check that casting from a long gives correct result
                Integer count = ((Long) row.get(1)).intValue();

                sampleCounts.put(identifier, count);

                labels.put(identifier, String.valueOf(row.get(2)));
            }
        }
        return sampleCounts;
    }

    @Override
    public Map<String, String> getLabels() {
        if (labels == null) {
            getAnnotatedCountsInSample();
        }
        return labels;
    }

    @Override
    public int getPopulationSize() {
        // TODO this should use os.count() but needs to be backwards compatible with widgets
        Query q = ldr.getPopulationQuery(true);
        return calcTotal(q);
    }

    @Override
    public int getSampleSize() {
        // TODO this should use os.count() but needs to be backwards compatible with widgets
        Query q = ldr.getSampleQuery(true);
        return calcTotal(q);
    }

    @Override
    public int getTestCount() {
        return populationCounts.keySet().size();
    }

    private int calcTotal(Query q) {
        Object[] o = os.executeSingleton(q).toArray();
        if (o.length == 0) {
            // no results
            return  0;
        }
        return  ((java.lang.Long) o[0]).intValue();
    }
}
