package org.intermine.pathquery;

/*
 * Copyright (C) 2002-2010 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.Reader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.intermine.util.SAXParser;
import org.intermine.util.StringUtil;
import org.intermine.util.Util;
import org.xml.sax.InputSource;

/**
 * Convert PathQueries to and from XML
 *
 * @author Mark Woodbridge
 */
public class PathQueryBinding
{
    /**
     * Convert a PathQuery to XML
     * @param query the PathQuery
     * @param queryName the name of the query
     * @param modelName the model name
     * @param version the version number of the xml format, an attribute of the ProfileManager
     * @return the corresponding XML String
     */
    public static String marshal(PathQuery query, String queryName, String modelName, int version) {
        StringWriter sw = new StringWriter();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();

        try {
            XMLStreamWriter writer = factory.createXMLStreamWriter(sw);
            marshal(query, queryName, modelName, writer, version);
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }

        return sw.toString();
    }

    /**
     * Marshal to an XMLStreamWriter.
     *
     * @param query the PathQuery
     * @param queryName the name of the query
     * @param modelName the model name
     * @param writer the xml stream writer to write to
     * @param version the version number of the xml format, an attribute of the ProfileManager
     */
    public static void marshal(PathQuery query, String queryName, String modelName,
            XMLStreamWriter writer, int version) {
        try {
            writer.writeStartElement("query");
            writer.writeAttribute("name", queryName);
            writer.writeAttribute("model", modelName);
            writer.writeAttribute("view", StringUtil.join(query.getViewStrings(), " "));
            if (query.getDescription() != null) {
                writer.writeAttribute("longDescription", query.getDescription());
            }
            if (query.getSortOrderStrings() != null && !query.getSortOrderStrings().isEmpty()) {
                writer.writeAttribute("sortOrder",
                                      StringUtil.join(query.getSortOrderStrings(), " "));
            }
            if (query.getConstraintLogic() != null) {
                writer.writeAttribute("constraintLogic", query.getConstraintLogic());
            }
            marshalPathQueryDescriptions(query, writer);
            for (Iterator j = query.getNodes().values().iterator(); j.hasNext();) {
                PathNode node = (PathNode) j.next();
                writer.writeStartElement("node");
                writer.writeAttribute("path", node.getPathString());
                if (node.getType() != null) {
                    writer.writeAttribute("type", node.getType());
                }
                for (Iterator k = node.getConstraints().iterator(); k.hasNext();) {
                    Constraint c = (Constraint) k.next();
                    writer.writeStartElement("constraint");
                    writer.writeAttribute("op", "" + c.getOp());
                    // Date is an exception, it is saved as a displayValue, in future all
                    // constraint values will be saved this way
                    Object outputValue;
                    if (c.getValue() instanceof Date) {
                        outputValue = c.getDisplayValue();
                    } else {
                        outputValue = c.getValue();
                    }
                    if ((outputValue instanceof String) && (version < 1)) {
                        outputValue = Util.wildcardUserToSql((String) outputValue);
                    }
                    writer.writeAttribute("value", "" + outputValue);
                    if (c.getDescription() != null) {
                        writer.writeAttribute("description", "" + c.getDescription());
                    } else {
                        writer.writeAttribute("description", "");
                    }
                    if (c.getIdentifier() != null) {
                        writer.writeAttribute("identifier", "" + c.getIdentifier());
                    } else {
                        writer.writeAttribute("identifier", "");
                    }
                    if (c.isEditable()) {
                        writer.writeAttribute("editable", "true");
                    }
                    if (c.getCode() != null) {
                        writer.writeAttribute("code", c.getCode());
                    } else {
                        writer.writeAttribute("code", "");
                    }
                    if (c.getExtraValue() != null) {
                        writer.writeAttribute("extraValue", "" + c.getExtraValue());
                    }
                    writer.writeEndElement();
                }
                writer.writeEndElement();
            }
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create XML for the path descriptions in a PathQuery.
     */
    private static void marshalPathQueryDescriptions(PathQuery query, XMLStreamWriter writer)
        throws XMLStreamException {
        for (Map.Entry<Path, String> entry : query.getPathDescriptions().entrySet()) {
            Path path = entry.getKey();
            String description = entry.getValue();
            writer.writeStartElement("pathDescription");
            writer.writeAttribute("pathString", path.toStringNoConstraints());
            writer.writeAttribute("description", description);
            writer.writeEndElement();
        }
    }

    /**
     * Parse PathQueries from XML
     * @param reader the saved queries
     * @param version the version of the xml, an attribute on the profile manager
     * @return a Map from query name to PathQuery
     */
    public static Map<String, PathQuery> unmarshal(Reader reader, int version) {
        Map<String, PathQuery> queries = new LinkedHashMap<String, PathQuery>();
        try {
            SAXParser.parse(new InputSource(reader), new PathQueryHandler(queries, version));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return queries;
    }

    /**
     * Parses PathQuery from XML.
     * @param reader reader containing XML
     * @param version the version of the xml, an attribute on the profile manager
     * @return PathQuery
     */
    public static PathQuery unmarshalPathQuery(Reader reader, int version) {
        Map<String, PathQuery> map = unmarshal(reader, version);
        if (map.size() != 0) {
            return map.values().iterator().next();
        }
        return null;
    }
}
