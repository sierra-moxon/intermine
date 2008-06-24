package org.intermine.web;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.intermine.metadata.Model;
import org.intermine.pathquery.PathQuery;
import org.intermine.web.logic.ClassKeyHelper;
import org.intermine.web.logic.Constants;
import org.intermine.web.logic.query.SavedQuery;
import org.intermine.web.logic.query.SavedQueryBinding;
import org.intermine.web.logic.session.SessionMethods;

import java.io.StringReader;

import javax.servlet.ServletContext;

import junit.framework.TestCase;
import servletunit.ServletContextSimulator;

public class SavedQueryBindingTest extends TestCase
{
    private Model model;
    private Date created = new Date(1124276877010L);
    private Map classKeys;

    protected void setUp() throws Exception {
        super.setUp();
        model = Model.getInstanceByName("testmodel");
        Properties classKeyProps = new Properties();
        classKeyProps.load(getClass().getClassLoader()
                           .getResourceAsStream("class_keys.properties"));
        classKeys = ClassKeyHelper.readKeys(model, classKeyProps);
    }

    public void testMarshalSavedQuery() throws Exception {
        PathQuery query = new PathQuery(model);
        SavedQuery sq = new SavedQuery("hello", created, query);

        String xml = SavedQueryBinding.marshal(sq);
        ServletContext servletContext = new ServletContextSimulator();
        servletContext.setAttribute(Constants.CLASS_KEYS, classKeys);
        SavedQuery sq2 = (SavedQuery) SavedQueryBinding.unmarshal(new StringReader(xml),
                                                                  new HashMap(),
                                                                  SessionMethods.getClassKeys(servletContext)).values().iterator().next();

        assertEquals(sq, sq2);
    }
}
