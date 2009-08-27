package org.intermine.web.struts;

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
import java.util.HashMap;

import org.intermine.api.profile.Profile;
import org.intermine.metadata.Model;
import org.intermine.pathquery.Path;
import org.intermine.pathquery.PathQuery;
import org.intermine.web.logic.Constants;

import servletunit.struts.MockStrutsTestCase;

/**
 * Tests for ViewChange.
 *
 * @author Kim Rutherford
 */

public class ViewChangeTest extends MockStrutsTestCase
{
    public ViewChangeTest (String arg) {
        super(arg);
    }

    public void tearDown() throws Exception {
         getActionServlet().destroy();
    }

    public void testRemove() throws Exception {
        Model model = Model.getInstanceByName("testmodel");
        PathQuery query = new PathQuery(model);
        query.getView().add(PathQuery.makePath(model, query, "Employee.name"));
        query.getView().add(PathQuery.makePath(model, query, "Employee.age"));
        getSession().setAttribute(Constants.QUERY, query);

        addRequestParameter("path", "Employee.age");
        addRequestParameter("method", "removeFromView");

        //necessary to work-round struts test case not invoking our SessionListener
        getSession().setAttribute(Constants.PROFILE,
                                  new Profile(null, null, null, null,
                                              new HashMap(), new HashMap(), new HashMap()));

        setRequestPathInfo("/viewChange");

        actionPerform();
        verifyNoActionErrors();
        //verifyForward("query");

        ArrayList<Path> expected = new ArrayList<Path>();
        expected.add(PathQuery.makePath(model, query, "Employee.name"));
        assertEquals(expected, ((PathQuery) getSession().getAttribute(Constants.QUERY)).getView());
    }
}
