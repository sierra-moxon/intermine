package org.intermine.web.logic;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreFactory;
import org.intermine.objectstore.ObjectStoreWriter;
import org.intermine.objectstore.ObjectStoreWriterFactory;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.intermine.ObjectStoreInterMineImpl;
import org.intermine.objectstore.intermine.SqlGenerator;
import org.intermine.util.SynchronisedIterator;
import org.intermine.web.logic.bag.BagQueryConfig;
import org.intermine.web.logic.bag.BagQueryHelper;
import org.intermine.web.logic.profile.Profile;
import org.intermine.web.logic.profile.ProfileManager;
import org.intermine.web.logic.query.MainHelper;
import org.intermine.web.logic.tagging.TagNames;
import org.intermine.web.logic.tagging.TagTypes;
import org.intermine.web.logic.template.TemplateQuery;

/**
 * Class to run a performance test on a production database by loading a set of template queries
 * from a userprofile database and running them in varying numbers of threads.
 *
 * @author Matthew Wakeling
 */
public class PerformanceTester
{
    public static final String SUPERUSER = "rachel@flymine.org";

    public static void main(String args[]) throws Exception {
        ObjectStore productionOs = ObjectStoreFactory.getObjectStore("os.production");
        ObjectStoreFactory.getObjectStore("os.userprofile-production");
        ObjectStoreWriter userProfileOs = ObjectStoreWriterFactory
            .getObjectStoreWriter("osw.userprofile-production");
        Properties classKeyProps = new Properties();
        classKeyProps.load(PerformanceTester.class.getClassLoader()
                .getResourceAsStream("class_keys.properties"));
        BagQueryConfig bagQueryConfig = BagQueryHelper.readBagQueryConfig(productionOs.getModel(),
                PerformanceTester.class.getClassLoader()
                .getResourceAsStream("webapp/WEB-INF/bag-queries.xml"));
        Map classKeys = ClassKeyHelper.readKeys(productionOs.getModel(), classKeyProps);
        ProfileManager pm = new ProfileManager(productionOs, userProfileOs, null);
        Profile p = pm.getProfile(SUPERUSER);
        Map<String, TemplateQuery> templates = p.getSavedTemplates();
        templates = pm.filterByTags(templates, Collections.singletonList(TagNames.IM_PUBLIC),
                TagTypes.TEMPLATE, SUPERUSER);
        templates.remove("ChromLocation_RegionOverlappingTFBindingsite");
        templates.remove("gene_adjacentgenes_allflyatlas");
        templates.remove("FlyFish_Genes");
        templates.remove("Gene_FlyFish");
        templates.remove("Chromosome_RegionOverlappingInsertion");
        templates.remove("All_Genes_In_Organism_To_Publications");
        templates.remove("ProteinInteractionOrganism_ProteinInteractionOrganism");
        templates.remove("Stage_FlyFish");

        int i = Integer.parseInt(args[0]);
        System .out.println("Running with " + i + " threads:");
        doRun(productionOs, classKeys, bagQueryConfig, templates, i);
    }

    private static void doRun(ObjectStore productionOs, Map classKeys,
            BagQueryConfig bagQueryConfig, Map<String, TemplateQuery> templates, int threadCount) {
        long startTime = System.currentTimeMillis();
        Iterator<Map.Entry<String, TemplateQuery>> iter = new SynchronisedIterator(templates
                .entrySet().iterator());
        Set<Integer> threads = new HashSet();

        synchronized (threads) {
            for (int i = 1; i < threadCount; i++) {
                Thread worker = new Thread(new Worker(productionOs, classKeys, bagQueryConfig,
                            threads, iter, i));
                threads.add(new Integer(i));
                worker.start();
            }
        }

        try {
            while (iter.hasNext()) {
                Map.Entry<String, TemplateQuery> entry = iter.next();
                doQuery(productionOs, classKeys, bagQueryConfig, entry.getKey(), entry.getValue(),
                        0);
            }
        } catch (NoSuchElementException e) {
            // This is fine - just a consequence of concurrent access to the iterator. It means the
            // end of the iterator has been reached, so there is no more work to do.
        }
        //System .out.println("Thread 0 finished");
        synchronized (threads) {
            while (threads.size() != 0) {
                //System .out.println(threads.size() + " threads left");
                try {
                    threads.wait();
                } catch (InterruptedException e) {
                    // Do nothing
                }
            }
        }
        System .out.println("Whole run took " + (System.currentTimeMillis() - startTime) + " ms");
    }


    private static void doQuery(ObjectStore productionOs, Map classKeys,
            BagQueryConfig bagQueryConfig, String templateName, TemplateQuery templateQuery,
            int threadNo) {
        try {
            //Query q = TemplateHelper.getPrecomputeQuery(entry.getValue(), new ArrayList(), null);
            long queryStartTime = System.currentTimeMillis();
            Query q = MainHelper.makeQuery(templateQuery, new HashMap(), new HashMap(), null,
                    null, false, productionOs, classKeys, bagQueryConfig);
            String sqlString = SqlGenerator.generate(q, 0, Integer.MAX_VALUE,
                    ((ObjectStoreInterMineImpl) productionOs).getSchema(),
                    ((ObjectStoreInterMineImpl) productionOs).getDatabase(), (Map) null);
            System .out.println("Thread " + threadNo + ": executing template " + templateName
                    + " with query " + q + ", SQL: " + sqlString);
            List results = productionOs.execute(q, 0, 1000, false, false,
                    ObjectStore.SEQUENCE_IGNORE);
            System .out.println("Thread " + threadNo + ": template " + templateName + " took "
                    + (System.currentTimeMillis() - queryStartTime) + " ms");
            if (results.isEmpty()) {
                System .out.println("Template " + templateName + " does not return any rows");
            }
        } catch (Exception e) {
            System .err.println("Thread " + threadNo + ": template " + templateName
                    + " could not be run.");
            e.printStackTrace(System.err);
        }
    }

    private static class Worker implements Runnable
    {
        private ObjectStore productionOs;
        private Map classKeys;
        private BagQueryConfig bagQueryConfig;
        private Set threads;
        private Iterator<Map.Entry<String, TemplateQuery>> iter;
        private int threadNo;

        public Worker(ObjectStore productionOs, Map classKeys, BagQueryConfig bagQueryConfig,
                Set threads, Iterator<Map.Entry<String, TemplateQuery>> iter, int threadNo) {
            this.productionOs = productionOs;
            this.classKeys = classKeys;
            this.bagQueryConfig = bagQueryConfig;
            this.threads = threads;
            this.iter = iter;
            this.threadNo = threadNo;
        }

        public void run() {
            try {
                while (iter.hasNext()) {
                    Map.Entry<String, TemplateQuery> entry = iter.next();
                    doQuery(productionOs, classKeys, bagQueryConfig, entry.getKey(),
                            entry.getValue(), threadNo);
                }
            } catch (NoSuchElementException e) {
                // Empty
            } finally {
                //System .out.println("Thread " + threadNo + " finished");
                synchronized (threads) {
                    threads.remove(new Integer(threadNo));
                    threads.notify();
                }
            }
        }
    }
}
