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

import java.util.HashMap;
import java.util.Map;

import org.intermine.api.profile.InterMineBag;
import org.intermine.api.template.TemplateQuery;
import org.intermine.pathquery.PathQuery;
import org.intermine.pathquery.PathQueryHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Extension of PathQueryHandler to handle parsing TemplateQueries
 * @author Xavier Watkins
 */
public class TemplateQueryHandler extends PathQueryHandler
{
    Map<String, TemplateQuery> templates;
    String templateName;
    String templateDesc;
    String templateTitle;
    String templateComment;
    boolean important;

    /**
     * Constructor
     * @param templates Map from template name to TemplateQuery
     * @param savedBags Map from bag name to bag
     * @param version the version of the XML, an attribute on the profile manager
     */
    public TemplateQueryHandler(Map<String, TemplateQuery> templates,
            Map<String, InterMineBag> savedBags, int version) {
        super(new HashMap<String, PathQuery>(), version);
        this.templates = templates;
        reset();
    }

    /**
     * {@inheritDoc}
     */
    public void startElement(String uri, String localName, String qName, Attributes attrs)
        throws SAXException {
        if (qName.equals("template")) {
            templateName = attrs.getValue("name");
            templateTitle = attrs.getValue("title");
            templateDesc = attrs.getValue("longDescription");
            if (attrs.getValue("description") != null && templateTitle == null) {
                // support old serialisation format: description -> title
                templateTitle = attrs.getValue("description");
            }
            templateComment = attrs.getValue("comment");
            important = Boolean.valueOf(attrs.getValue("important")).booleanValue();
        }
        super.startElement(uri, localName, qName, attrs);
    }

    /**
     * {@inheritDoc}
     */
    public void endElement(String uri, String localName, String qName) {
        super.endElement(uri, localName, qName);
        if (qName.equals("template")) {
            templates.put(templateName, new TemplateQuery(templateName,
                                                          templateTitle,
                                                          templateDesc,
                                                          templateComment,
                                                          query));
            reset();
        }
    }

    private void reset() {
        templateName = "";
        templateDesc = "";
    }
}
