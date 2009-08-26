package org.intermine.api.xml;

/*
 * Copyright (C) 2002-2009 FlyMine
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

import org.intermine.api.profile.SavedQuery;
import org.intermine.pathquery.PathQuery;
import org.intermine.pathquery.PathQueryHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Extension of PathQueryHandler to handle parsing SavedQuerys
 * @author Xavier Watkins
 */
public class SavedQueryHandler extends PathQueryHandler
{
    private Map<String, SavedQuery> queries;
    private Date dateCreated;
    private String queryName;

    /**
     * Constructor
     * @param queries Map from saved query name to SavedQuery
     * @param savedBags Map from bag name to bag
     * @param version the version of the xml, an attribute on the profile manager
     */
    public SavedQueryHandler(Map<String, SavedQuery> queries, Map savedBags, int version) {
        super(new HashMap<String, PathQuery> (), version);
        this.queries = queries;
    }

    /**
     * {@inheritDoc}
     */
    public void startElement(String uri, String localName, String qName, Attributes attrs)
        throws SAXException {
        if (qName.equals("saved-query")) {
            queryName = attrs.getValue("name");
            if (attrs.getValue("date-created") != null) {
                dateCreated = new Date(Long.parseLong(attrs.getValue("date-created")));
            }
        }
        super.startElement(uri, localName, qName, attrs);
    }

    /**
     * {@inheritDoc}
     */
    public void endElement(String uri, String localName, String qName) {
        super.endElement(uri, localName, qName);
        if (qName.equals("saved-query")) {
            queries.put(queryName, new SavedQuery(queryName, dateCreated, query));
            dateCreated = null;
        }
    }
}
