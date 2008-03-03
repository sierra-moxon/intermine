package org.intermine.web.logic.bag;

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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.intermine.objectstore.query.BagConstraint;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.objectstore.query.ConstraintSet;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.QueryClass;
import org.intermine.objectstore.query.QueryExpression;
import org.intermine.objectstore.query.QueryField;

import org.intermine.metadata.FieldDescriptor;
import org.intermine.metadata.Model;
import org.intermine.util.SAXParser;
import org.intermine.web.logic.ClassKeyHelper;

import java.io.InputStream;

import org.xml.sax.InputSource;

/**
 * @author Richard Smith
 *
 */
public class BagQueryHelper
{

    /**
     * Message associated with default bag query.
     */
    public static final String DEFAULT_MESSAGE = "searching key fields";

    /**
     * Create a BagQuery that constrains the class key fields of the given type.
     * @param type the class to query for
     * @param bagQueryConfig The BagQueryConfig object used to get the extra class and field to
     * constrain when making the query (eg. constrain Bioentiry.organism.name = "something")
     * @param classKeys the class keys map
     * @param input the input strings/identifiers
     * @param model the Model to pass to the BagQuery constructor
     * @return a BagQuery that queries for objects of class given of type where any of the class
     * key fields match any of the input identifiers
     * @throws ClassNotFoundException if the type isn't in the model
     */
    public static BagQuery createDefaultBagQuery(String type, BagQueryConfig bagQueryConfig,
                                                 Model model, Map classKeys, Collection input)
        throws ClassNotFoundException {

        Class cls = Class.forName(type);
        if (!ClassKeyHelper.hasKeyFields(classKeys, type)) {
            throw new IllegalArgumentException("Internal error - no key fields found for type: "
                                               + type + ".");
        }

        List lowerCaseInput = new ArrayList();
        for (Object o : input) {
            if (o instanceof String) {
                o = ((String) o).toLowerCase();
                try {
                    lowerCaseInput.add(new Integer((String) o));
                } catch (NumberFormatException e) {
                    // Not a number
                }
            }
            lowerCaseInput.add(o);
        }

        Query q = new Query();
        QueryClass qc = new QueryClass(cls);
        q.addFrom(qc);
        q.addToSelect(new QueryField(qc, "id"));

        ConstraintSet cs = new ConstraintSet(ConstraintOp.OR);
        q.setConstraint(cs);

        Collection<FieldDescriptor> keyFields = ClassKeyHelper.getKeyFields(classKeys, type);

        Iterator keyFieldIter = keyFields.iterator();
        while (keyFieldIter.hasNext()) {
            FieldDescriptor fld = (FieldDescriptor) keyFieldIter.next();

            if (!fld.isAttribute()) {
                continue;
            }

            QueryField qf = new QueryField(qc, fld.getName());
            if (qf.getType().equals(String.class)) {
                QueryExpression qe = new QueryExpression(QueryExpression.LOWER, qf);
                // constrain field to be in a bag
                cs.addConstraint(new BagConstraint(qe, ConstraintOp.IN, lowerCaseInput));
            } else {
                cs.addConstraint(new BagConstraint(qf, ConstraintOp.IN, lowerCaseInput));
            }

            q.addToSelect(qf);
        }

        if (cs.getConstraints().size() == 0) {
            String message =
                "Internal error - could not find any usable key fields for type: " + type + ".";
            throw new IllegalArgumentException(message);
        }
        BagQuery bq = new BagQuery(bagQueryConfig, model, q, DEFAULT_MESSAGE, false);
        return bq;
    }

    /**
     * Read the bag query configuration from the given stream.
     * @param model the Model to use to check the bag types
     * @param is the InputStream
     * @return the BagQueryConfig object for this webapp
     * @throws Exception if there is a problem parsing the bag-queries.xml
     */
    public static BagQueryConfig readBagQueryConfig(Model model, InputStream is) throws Exception {
        BagQueryHandler handler = new BagQueryHandler(model);
        SAXParser.parse(new InputSource(is), handler);
        return handler.getBagQueryConfig();
    }
}
