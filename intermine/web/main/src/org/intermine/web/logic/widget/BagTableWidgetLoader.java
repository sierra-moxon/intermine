package org.intermine.web.logic.widget;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.intermine.metadata.ClassDescriptor;
import org.intermine.metadata.Model;
import org.intermine.model.InterMineObject;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.objectstore.query.BagConstraint;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.objectstore.query.ConstraintSet;
import org.intermine.objectstore.query.ContainsConstraint;
import org.intermine.objectstore.query.OrderDescending;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.QueryClass;
import org.intermine.objectstore.query.QueryCollectionReference;
import org.intermine.objectstore.query.QueryField;
import org.intermine.objectstore.query.QueryFunction;
import org.intermine.objectstore.query.QueryObjectReference;
import org.intermine.objectstore.query.QueryReference;
import org.intermine.objectstore.query.ResultsRow;
import org.intermine.path.Path;
import org.intermine.util.TypeUtil;
import org.intermine.web.logic.ClassKeyHelper;
import org.intermine.web.logic.bag.InterMineBag;
import org.intermine.web.logic.config.FieldConfig;
import org.intermine.web.logic.config.FieldConfigHelper;
import org.intermine.web.logic.config.WebConfig;
import org.intermine.web.logic.query.MainHelper;
import org.intermine.web.logic.results.ResultElement;

/**
 * @author Xavier Watkins
 *
 */
public class BagTableWidgetLoader
{
    private List columns;
    private List flattenedResults;
    private String title, description;

    /**
     * This class loads and formats the data for the count
     * table widgets in the bag details page
     *
     * @param title title of widget
     * @param description description of widget
     * @param type The type to do the count on
     * @param collectionName the name of the collection corresponding to the
     * bag type
     * @param bag the bag
     * @param os the objectstore
     * @param webConfig the webConfig
     * @param model the model
     * @param classKeys the classKeys
     * @param fields fields involved in widget
     * @param urlGen the class that generates the pathquery used in the links from the widget
     */
    public BagTableWidgetLoader(String title, String description, String type, String
            collectionName, InterMineBag bag, ObjectStore os, WebConfig webConfig, Model model,
            Map classKeys, String fields, String urlGen) {
        this.title = title;
        this.description = description;
        Query q = new Query();

        Class clazzA = null;
        Class clazzB = null;
        try {
            clazzA = Class.forName(model.getPackageName() + "." + type);
            clazzB = Class.forName(model.getPackageName() + "." + bag.getType());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        QueryClass qClassA = new QueryClass(clazzA);
        QueryClass qClassB = new QueryClass(clazzB);

        q.addFrom(qClassA);
        q.addFrom(qClassB);

        QueryFunction count = new QueryFunction();

        q.addToSelect(qClassA);
        q.addToSelect(count);


        ConstraintSet cstSet = new ConstraintSet(ConstraintOp.AND);
        QueryReference qr;
        try {
            qr = new QueryCollectionReference(qClassB, collectionName);
        } catch (Exception e) {
            qr = new QueryObjectReference(qClassB, collectionName);
        }
        ContainsConstraint cstr = new ContainsConstraint(qr, ConstraintOp.CONTAINS, qClassA);
        cstSet.addConstraint(cstr);

        QueryField qf = new QueryField(qClassB, "id");
        BagConstraint bagCstr = new BagConstraint(qf, ConstraintOp.IN, bag.getOsb());
        cstSet.addConstraint(bagCstr);

        q.setConstraint(cstSet);

        q.addToGroupBy(qClassA);
        q.addToOrderBy(new OrderDescending(count));

        List results;
        try {
            results = os.execute(q, 0, 10, true, true, ObjectStore.SEQUENCE_IGNORE);
        } catch (ObjectStoreException e) {
            throw new RuntimeException(e);
        }
        ClassDescriptor cld;
        try {
            cld = MainHelper.getClassDescriptor(type, model);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("unexpected exception", e);
        }
        columns = new ArrayList();

        if ((fields != null) && (fields.length() != 0)) {
            String[] fieldArray = fields.split(",");
            for (int i = 0; i < fieldArray.length; i++) {
                String field = fieldArray[i];
                String newColumnName = type + "." + field;
                columns.add(newColumnName);
            }
        } else {
            List cldFieldConfigs = FieldConfigHelper.getClassFieldConfigs(webConfig, cld);
            for (Iterator iter = cldFieldConfigs.iterator(); iter.hasNext();) {
                FieldConfig fc = (FieldConfig) iter.next();
                if (!fc.getShowInResults()) {
                    continue;
                }
                String fieldExpr = fc.getFieldExpr();
                String newColumnName = type + "." + fieldExpr;
                columns.add(newColumnName);
            }
        }
        flattenedResults = new ArrayList<ArrayList>();
        for (Iterator iter = results.iterator(); iter.hasNext();) {
            ArrayList<String[]> flattenedRow = new ArrayList<String[]>();
            ResultsRow resRow = (ResultsRow) iter.next();
            //Integer lastObjectId = null;
            String key = "";
            for (Iterator iterator = resRow.iterator(); iterator.hasNext();) {
                Object element = iterator.next();
                if (element instanceof InterMineObject) {
                    InterMineObject o = (InterMineObject) element;
                    for (Iterator iterator3 = columns.iterator(); iterator3.hasNext();) {
                        String columnName = (String) iterator3.next();
                        Path path = new Path(model, columnName);
                        Object fieldValue = path.resolve(o);
                        Class thisType = path.getStartClassDescriptor().getType();
                        String fieldName = path.getEndFieldDescriptor().getName();
                        boolean isKeyField = ClassKeyHelper.isKeyField(classKeys,
                                TypeUtil.unqualifiedName(thisType.getName()), fieldName);
                        String link = null;
                        if (isKeyField) {
                            key = fieldValue.toString();
                            link = "objectDetails.do?id=" + o.getId() + "&amp;trail=|bag."
                                   + bag.getName() + "|" + o.getId();
                        }
                        flattenedRow.add(new String[]
                            {
                                (String) fieldValue, link
                            });
                    }
                } else if (element instanceof Long) {
                    flattenedRow.add(new String[]
                            {
                                String.valueOf((Long) element),
                                "widgetAction.do?bagName=" + bag.getName() + "&link=" + urlGen
                                                + "&key=" + key
                            });
                }

            }
            flattenedResults.add(flattenedRow);
        }
        // Add the count column
        columns.add(bag.getType() + "s");
    }

    /**
     * get the flattened results
     * @return the flattened results
     */
    public List getFlattenedResults() {
        return flattenedResults;
    }

    /**
     * Get the columnNames
     * @return the columnNames
     */
    public List getColumns() {
        return columns;
    }

    /**
     * Get the title
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the description
     * @return the description
     */
    public String getDescription() {
        return description;
    }

}
