package org.intermine.web.logic.bag;

/*
 * Copyright (C) 2002-2007 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import servletunit.struts.MockStrutsTestCase;

import org.intermine.model.testmodel.Address;
import org.intermine.model.testmodel.Employee;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.query.BagConstraint;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.objectstore.query.ConstraintSet;
import org.intermine.objectstore.query.ContainsConstraint;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.QueryClass;
import org.intermine.objectstore.query.QueryField;
import org.intermine.objectstore.query.QueryObjectReference;
import org.intermine.objectstore.query.Results;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.bag.TypeConverter;

/**
 * @author Matthew Wakeling
 */
public class TypeConverterTest extends MockStrutsTestCase
{
    public TypeConverterTest(String arg1) {
        super(arg1);
    }

    public void tearDown() throws Exception {
         getActionServlet().destroy();
    }

    public void test1() throws Exception {
        ServletContext context = getActionServlet().getServletContext();

        ObjectStore os = (ObjectStore) context.getAttribute(Constants.OBJECTSTORE);
        List names = Arrays.asList(new String[] {"EmployeeA2", "EmployeeB2"});
        Query q = new Query();
        QueryClass qc1 = new QueryClass(Employee.class);
        QueryClass qc2 = new QueryClass(Address.class);
        q.addFrom(qc1);
        q.addToSelect(qc1);
        q.addFrom(qc2);
        q.addToSelect(qc2);
        ConstraintSet cs = new ConstraintSet(ConstraintOp.AND);
        q.setConstraint(cs);
        cs.addConstraint(new BagConstraint(new QueryField(qc1, "name"), ConstraintOp.IN, names));
        cs.addConstraint(new ContainsConstraint(new QueryObjectReference(qc1, "address"),
                    ConstraintOp.CONTAINS, qc2));
        Results r = os.execute(q);
        assertEquals("Results: " + r, 2, r.size());
        List employees = new ArrayList();
        employees.add(((List) r.get(0)).get(0));
        employees.add(((List) r.get(1)).get(0));
        Map expected = new HashMap();
        expected.put(((List) r.get(0)).get(0), Collections.singletonList(((List) r.get(0)).get(1)));
        expected.put(((List) r.get(1)).get(0), Collections.singletonList(((List) r.get(1)).get(1)));

        Map got = TypeConverter.convertObjects(context, Employee.class, Address.class, employees);

        assertEquals(expected, got);
    }
}
