package org.intermine.api.results;

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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.intermine.api.bag.BagQueryRunner;
import org.intermine.api.query.MainHelper;
import org.intermine.model.FastPathObject;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.objectstore.intermine.ObjectStoreInterMineImpl;
import org.intermine.objectstore.query.PathExpressionField;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.QueryCollectionPathExpression;
import org.intermine.objectstore.query.QueryObjectPathExpression;
import org.intermine.objectstore.query.QuerySelectable;
import org.intermine.objectstore.query.Results;
import org.intermine.pathquery.Path;
import org.intermine.pathquery.PathQuery;

/**
 * An Iterator that produces data in a format suitable for exporting. The data is flattened, so if
 * there are outer joined collections, there may be more rows than in the original results.
 *
 * @author Matthew Wakeling
 */
public class ExportResultsIterator implements Iterator<List<ResultElement>>
{
    private static final Logger LOG = Logger.getLogger(ExportResultsIterator.class);
    
    private Iterator<List> osIter;
    private Iterator<List<ResultElement>> subIter;
    // This object contains a description of the collections in the input.
    private List columns;
    private int columnCount;
    private Results results;

    /**
     * Constructor for ExportResultsIterator. This creates a new instance from the given
     * ObjectStore, PathQuery, and other necessary objects.
     *
     * @param os an ObjectStore that the query will be run on
     * @param pq a PathQuery to run
     * @param savedBags a Map of the bags that the query may have used
     * @param bagQueryRunner a BagQueryRunner for any LOOKUP constraints
     * @throws ObjectStoreException if something goes wrong executing the query
     */
    public ExportResultsIterator(ObjectStore os, PathQuery pq, Map savedBags,
            BagQueryRunner bagQueryRunner) throws ObjectStoreException {
        init(os, pq, savedBags, bagQueryRunner, 0);
    }

    /**
     * Constructor for ExportResultsIterator. This creates a new instance from the given
     * ObjectStore, PathQuery, and other necessary objects.
     *
     * @param os an ObjectStore that the query will be run on
     * @param pq a PathQuery to run
     * @param savedBags a Map of the bags that the query may have used
     * @param bagQueryRunner a BagQueryRunner for any LOOKUP constraints
     * @param batchSize the batch size for the results
     * @throws ObjectStoreException if something goes wrong executing the query
     */
    public ExportResultsIterator(ObjectStore os, PathQuery pq, Map savedBags,
            BagQueryRunner bagQueryRunner, int batchSize) throws ObjectStoreException {
        init(os, pq, savedBags, bagQueryRunner, batchSize);
    }
    
    private void init(ObjectStore os, PathQuery pq, Map savedBags,
            BagQueryRunner bagQueryRunner, int batchSize)
            throws ObjectStoreException {
        Map<String, QuerySelectable> pathToQueryNode = new HashMap<String, QuerySelectable>();
        Map returnBagQueryResults = new HashMap();
        Query q = MainHelper.makeQuery(pq, savedBags, pathToQueryNode, bagQueryRunner,
                returnBagQueryResults, false);
        results = os.execute(q, batchSize, true, true, true);
        osIter = results.iterator();
        List<List<ResultElement>> empty = Collections.emptyList();
        subIter = empty.iterator();
        columns = convertColumnTypes(q.getSelect(), pq, pathToQueryNode);
        columnCount = pq.getView().size();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
        while ((!subIter.hasNext()) && osIter.hasNext()) {
            subIter = decodeRow(osIter.next()).iterator();
        }
        return subIter.hasNext();
    }

    /**
     * {@inheritDoc}
     */
    public List<ResultElement> next() {
        while ((!subIter.hasNext()) && osIter.hasNext()) {
            subIter = decodeRow(osIter.next()).iterator();
        }
        return subIter.next();
    }

    /**
     * This method is not supported.
     * {@inheritDoc}
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Switches on the goFaster mode in the objectstore for this query.
     *
     * @throws RuntimeException if something goes wrong
     */
    public void goFaster()  {
        try {
            ((ObjectStoreInterMineImpl) results.getObjectStore()).goFaster(results.getQuery());    
        } catch (ObjectStoreException ex) {
            LOG.error("Error happened during executing goFaster method.", ex);
        }
    }

    /**
     * Switches off the goFaster mode in the objectstore for this query.
     *
     * @throws RuntimeException if something goes wrong
     */
    public void releaseGoFaster() {
        try {
            ((ObjectStoreInterMineImpl) results.getObjectStore()).releaseGoFaster(results
                    .getQuery());    
        } catch (ObjectStoreException ex) {
            LOG.error("Error happened during executing releaseGoFaster method.", ex);
        }
    }

    private List convertColumnTypes(List<? extends QuerySelectable> select, PathQuery pq,
            Map<String, QuerySelectable> pathToQueryNode) {
        List retval = new ArrayList();
        for (QuerySelectable qs : select) {
            boolean notFinished = true;
            while (notFinished) {
                if (qs instanceof QueryObjectPathExpression) {
                    QueryObjectPathExpression qope = (QueryObjectPathExpression) qs;
                    List<QuerySelectable> subSelect = qope.getSelect();
                    if (!subSelect.isEmpty()) {
                        qs = subSelect.get(0);
                        if (qs.equals(qope.getDefaultClass())) {
                            qs = qope;
                            notFinished = false;
                        }
                    } else {
                        notFinished = false;
                    }
                } else if (qs instanceof PathExpressionField) {
                    PathExpressionField pef = (PathExpressionField) qs;
                    QueryObjectPathExpression qope = pef.getQope();
                    qs = qope.getSelect().get(pef.getFieldNumber());
                    if (qs.equals(qope.getDefaultClass())) {
                        qs = qope;
                        notFinished = false;
                    }
                } else {
                    notFinished = false;
                }
            }
            if (qs instanceof QueryCollectionPathExpression) {
                QueryCollectionPathExpression qc = (QueryCollectionPathExpression) qs;
                List<QuerySelectable> subSelect = qc.getSelect();
                if (subSelect.isEmpty()) {
                    retval.add(convertColumnTypes(Collections.singletonList(qc.getDefaultClass()),
                                pq, pathToQueryNode));
                } else {
                    retval.add(convertColumnTypes(subSelect, pq, pathToQueryNode));
                }
            } else {
                Map<Path, Integer> fieldToColumnNumber = new HashMap<Path, Integer>();
                int columnNo = 0;
                for (Path path : pq.getView()) {
                    Path parent = path.getPrefix();
                    QuerySelectable selectableForPath = pathToQueryNode.get(
                            parent.toStringNoConstraints());
                    if (selectableForPath instanceof QueryCollectionPathExpression) {
                        selectableForPath = ((QueryCollectionPathExpression) selectableForPath)
                            .getDefaultClass();
                    }
                    if (qs.equals(selectableForPath)) {
                        fieldToColumnNumber.put(path, new Integer(columnNo));
                    } else {
                    }
                    columnNo++;
                }
                retval.add(fieldToColumnNumber);
            }
        }
        return retval;
    }

    /**
     * Allows test to access column info.
     *
     * @return columns
     */
    protected List getColumns() {
        return columns;
    }

    private List<List<ResultElement>> decodeRow(List row) {
        List<ResultElement> template = new ArrayList<ResultElement>();
        for (int i = 0; i < columnCount; i++) {
            template.add(null);
        }
        List<List<ResultElement>> retval = new ArrayList<List<ResultElement>>();
        expandCollections(row, retval, template, columns);
        return retval;
    }

    private void expandCollections(List row, List<List<ResultElement>> retval,
            List<ResultElement> template, List columns) {
        if (row.size() != columns.size()) {
            throw new IllegalArgumentException("Column description (size " + columns.size()
                    + ") does not match input data (size " + row.size() + ")");
        }
        template = new ArrayList(template);
        int columnNo = 0;
        boolean multiRow = false;
        for (Object column : columns) {
            if (column instanceof Map) {
                Map<Path, Integer> desc = (Map<Path, Integer>) column;
                for (Map.Entry<Path, Integer> descEntry : desc.entrySet()) {
                    template.set(descEntry.getValue().intValue(),
                            new ResultElement((FastPathObject) row.get(columnNo),
                                descEntry.getKey(), false));
                }
            } else if (!multiRow) {
                // Check the collection size, to see if we can get away with a single row.
                List<List> collection = (List<List>) row.get(columnNo);
                if (collection.size() > 1) {
                    multiRow = true;
                } else if (collection.size() == 1) {
                    multiRow = isCollectionMultiRow(collection.iterator().next(), (List) column);
                }
            }
            columnNo++;
        }
        boolean hasCollections = false;
        columnNo = 0;
        for (Object column : columns) {
            if (column instanceof List) {
                List<List> collection = (List<List>) row.get(columnNo);
                for (List subRow : collection) {
                    if (multiRow) {
                        hasCollections = true;
                        expandCollections(subRow, retval, template, (List) column);
                    } else {
                        expandCollectionsJustOneRow(subRow, retval, template, (List) column);
                    }
                }
            }
            columnNo++;
        }
        if (!hasCollections) {
            retval.add(template);
        }
    }

    private void expandCollectionsJustOneRow(List row, List<List<ResultElement>> retval,
            List<ResultElement> template, List columns) {
        if (row.size() != columns.size()) {
            throw new IllegalArgumentException("Column description (size " + columns.size()
                    + ") does not match input data (size " + row.size() + ")");
        }
        int columnNo = 0;
        for (Object column : columns) {
            if (column instanceof Map) {
                Map<Path, Integer> desc = (Map<Path, Integer>) column;
                for (Map.Entry<Path, Integer> descEntry : desc.entrySet()) {
                    template.set(descEntry.getValue().intValue(),
                            new ResultElement((FastPathObject) row.get(columnNo),
                                descEntry.getKey(), false));
                }
            } else {
                List<List> collection = (List<List>) row.get(columnNo);
                for (List subRow : collection) {
                    expandCollectionsJustOneRow(subRow, retval, template, (List) column);
                }
            }
            columnNo++;
        }
    }

    private boolean isCollectionMultiRow(List row, List columns) {
        boolean multiRow = false;
        int columnNo = 0;
        for (Object column : columns) {
            if ((column instanceof List) && (!multiRow)) {
                List<List> collection = (List<List>) row.get(columnNo);
                if (collection.size() > 1) {
                    multiRow = true;
                } else if (collection.size() == 1) {
                    multiRow = isCollectionMultiRow(collection.iterator().next(), (List) column);
                }
            }
            columnNo++;
        }
        return multiRow;
    }
}
