package org.intermine.bio.web.widget;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.flymine.model.genomic.Chromosome;
import org.flymine.model.genomic.LocatedSequenceFeature;
import org.flymine.model.genomic.Organism;
import org.intermine.bio.web.logic.BioUtil;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.query.BagConstraint;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.objectstore.query.ConstraintSet;
import org.intermine.objectstore.query.ContainsConstraint;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.QueryClass;
import org.intermine.objectstore.query.QueryExpression;
import org.intermine.objectstore.query.QueryField;
import org.intermine.objectstore.query.QueryFunction;
import org.intermine.objectstore.query.QueryObjectReference;
import org.intermine.objectstore.query.QueryValue;
import org.intermine.objectstore.query.Results;
import org.intermine.objectstore.query.ResultsRow;
import org.intermine.objectstore.query.SimpleConstraint;
import org.intermine.web.logic.bag.InterMineBag;
import org.intermine.web.logic.widget.DataSetLdr;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * 
 *
 * @author Julie Sullivan
 */
public class ChromosomeDistributionDataSetLdr implements DataSetLdr
{
    private DefaultCategoryDataset dataSet;
    private ObjectStore os;
    private Model model;
    private String bagType;
    private Collection<String> chromosomeList;
        
    /**
     * Creates a ChromosomeDistributionDataSetLdr used to retrieve, organise
     * and structure the data to create a graph
     * @param bag the bag
     * @param os the ObjectStore
     * @param organismName the organism name
     * @throws Exception if getting the list of organisms fails
     */
    public ChromosomeDistributionDataSetLdr(InterMineBag bag, ObjectStore os, String organismName)
        throws Exception {
        super();
        this.os = os;
        model = os.getModel();
        bagType = bag.getType();
        
        /* chromosome --> count of genes */
        LinkedHashMap<String, int[]> resultsTable = new LinkedHashMap<String, int[]>();

        /* get all chromosomes for this organism */
        // Collection<String> chromosomeList = organismToChromosomes.get(organismName);
        chromosomeList = BioUtil.getChromosomes(os, Arrays.asList(organismName), false);

        /* initialise results list - so all chromosomes are displayed */
        for (Iterator<String> chrIter = chromosomeList.iterator(); chrIter.hasNext();) {
            String chromosomeName = chrIter.next();
            int[] count = new int[3];
            count[0] = 0;   // actual
            count[1] = 0;   // expected
            count[2] = 0;   // total
            resultsTable.put(chromosomeName, count);
        }

        /* run query to get gene count per chromsome */
        Query q = createQuery(organismName, "actual", bag);
        Results results = os.execute(q);
        results.setBatchSize(50000);

        // find out how many genes in the bag have a chromosome location, use this
        // to work out the expected number for each chromosome. This is a hack to
        // deal with the proportion of genes not assigned to a chromosome, it would
        // be easier of they were located on an 'unknown' chromosome.
        int totalInBagWithLocation = 0;

        // put results in maps
        Iterator iter = results.iterator();
        while (iter.hasNext()) {
            ResultsRow resRow = (ResultsRow) iter.next();
            String chromosome = (String) resRow.get(0);
            Long geneCount = (java.lang.Long) resRow.get(1);
            (resultsTable.get(chromosome))[0] = geneCount.intValue();
            totalInBagWithLocation += geneCount.intValue();
        }
        
        int grandTotal = addExpected(resultsTable, organismName);
        
        for (String chromosome : resultsTable.keySet()) {
            
            double expectedValue = 0;
            double proportion = 0.0000000000;
            double totalWithChromosome = (resultsTable.get(chromosome))[2];
            
            if (totalWithChromosome > 0) {
                proportion = totalWithChromosome / grandTotal;
            }
            expectedValue = totalInBagWithLocation * proportion;
            if (resultsTable.get(chromosome) != null) {
                (resultsTable.get(chromosome))[1] = (int) Math.round(expectedValue);
            }
        }
        
        /* add data to dataset for display on graph */
        dataSet = new DefaultCategoryDataset();
        for (Iterator<String> iterator = resultsTable.keySet().iterator(); iterator.hasNext();) {
            String chromosome = iterator.next();
            dataSet.addValue((resultsTable.get(chromosome))[0], "Actual", chromosome);
            dataSet.addValue((resultsTable.get(chromosome))[1], "Expected", chromosome);
        }
    }

    /**
     * {@inheritDoc}
     */
    public CategoryDataset getDataSet() {
        return dataSet;
    }

    private int addExpected(HashMap<String, int[]> resultsTable, String organismName)
        throws ClassNotFoundException {

        // get counts of gene in database for gene
        Query q = createQuery(organismName, "expected", null);
        Results results = os.execute(q);
        Iterator iter = results.iterator();
        int grandTotal = 0;
        
        while (iter.hasNext()) {
            ResultsRow resRow = (ResultsRow) iter.next();

            String chromosome = (String) resRow.get(0);         // chromosome
            Long geneCount = (java.lang.Long) resRow.get(1);    // genecount
            
            (resultsTable.get(chromosome))[2] = geneCount.intValue();
            grandTotal += geneCount.intValue();
        }
        
        return grandTotal;
    }
    
    private Query createQuery(String organism, String resultsType, InterMineBag bag)
        throws ClassNotFoundException {

        Query q = new Query();

        QueryClass chromosomeQC = new QueryClass(Chromosome.class);
        Class<?> bagCls = Class.forName(model.getPackageName() + "." + bagType);
        QueryClass featureQC;

        // query LocatedSequenceFeature if possible for better chance of using precompute
        if (LocatedSequenceFeature.class.isAssignableFrom(bagCls)) {
            featureQC = new QueryClass(LocatedSequenceFeature.class);
        } else {
            featureQC = new QueryClass(bagCls);
        }
        QueryClass organismQC = new QueryClass(Organism.class);

        QueryField chromoQF = new QueryField(chromosomeQC, "primaryIdentifier");
        QueryFunction countQF = new QueryFunction();
        QueryField organismNameQF = new QueryField(organismQC, "name");
        
        if (!resultsType.equals("total")) {            
            q.addToSelect(chromoQF);
            q.addToSelect(countQF);
        } else {
            q.addToSelect(organismNameQF);
            q.addToSelect(countQF);
        }

        q.addFrom(chromosomeQC);
        q.addFrom(featureQC);
        q.addFrom(organismQC);

        ConstraintSet cs = new ConstraintSet(ConstraintOp.AND);

        QueryObjectReference r = new QueryObjectReference(featureQC, "chromosome");
        cs.addConstraint(new ContainsConstraint(r, ConstraintOp.CONTAINS, chromosomeQC));

        Collection<String> chrs = new ArrayList<String>();
        // make chromos lowercase for query
        for (Iterator<String> iter = chromosomeList.iterator(); 
        iter.hasNext();) {
            String chromosomeName = (iter.next()).toLowerCase();
            chrs.add(chromosomeName);
        }
        
        // TODO if there are no chromosomes, we don't want to render this graph
        if (chrs != null && !chrs.isEmpty()) {
            QueryField qfChrId = new QueryField(chromosomeQC, "primaryIdentifier");
            QueryExpression qf = new QueryExpression(QueryExpression.LOWER, qfChrId);
            BagConstraint bagChr = new BagConstraint(qf, ConstraintOp.IN, chrs);
            cs.addConstraint(bagChr);
        }
               
        QueryObjectReference r2 = new QueryObjectReference(featureQC, "organism");        
        cs.addConstraint(new ContainsConstraint(r2, ConstraintOp.CONTAINS, organismQC));

        QueryExpression qf = new QueryExpression(QueryExpression.LOWER, organismNameQF);        
        SimpleConstraint sc = new SimpleConstraint(qf, ConstraintOp.EQUALS, 
                                                   new QueryValue(organism));
        cs.addConstraint(sc);

        if (resultsType.equals("actual")) {
            QueryField qf2 = new QueryField(featureQC, "id");
            BagConstraint bagC = new BagConstraint(qf2, ConstraintOp.IN, bag.getOsb());
            cs.addConstraint(bagC);
        }

        q.setConstraint(cs);

        if (!resultsType.equals("total")) {
            q.addToGroupBy(chromoQF);
            q.addToOrderBy(chromoQF);
        } else {
            q.addToGroupBy(organismNameQF);
        }
        return q;
    }
}
