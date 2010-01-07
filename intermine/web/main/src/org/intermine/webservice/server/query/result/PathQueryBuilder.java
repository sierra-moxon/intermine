package org.intermine.webservice.server.query.result;

/*
 * Copyright (C) 2002-2010 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.List;
import java.util.Map;

import org.intermine.api.profile.InterMineBag;
import org.intermine.pathquery.PathQuery;
import org.intermine.pathquery.PathQueryUtil;
import org.intermine.web.logic.ServletMethods;
import org.intermine.webservice.server.exceptions.BadRequestException;


/**
 * PathQueryBuilder builds PathQuery object from xml and validates it.
 *
 * @author Jakub Kulaviak
 **/
public class PathQueryBuilder
{

    private PathQuery pathQuery;

    /**
     * PathQueryBuilder constructor.
     * @param xml xml string from which will be PathQuery constructed
     * @param schemaUrl url of XML Schema file, validation is performed according this file
     * @param savedBags previously saved bags
     */
    public PathQueryBuilder(String xml, String schemaUrl, Map<String, InterMineBag> savedBags) {
        buildQuery(xml, schemaUrl, savedBags);
    }

    private void buildQuery(String xml, String schemaUrl,
            Map<String, InterMineBag> savedBags) {
        XMLValidator validator = new XMLValidator();
        validator.validate(xml, schemaUrl);
        if (validator.getErrorsAndWarnings().size() == 0) {
            try {
                pathQuery = ServletMethods.fromXml(xml, savedBags,
                        PathQuery.USERPROFILE_VERSION);
            } catch (Exception ex) {
                String msg = "XML is well formatted but contains invalid model data. "
                        + "Check that your constraints are correct "
                        + "and query corresponds to model. Cause:" + ex.getMessage();
                throw new BadRequestException(msg, ex);
            }
            if (!pathQuery.isValid()) {
                throw new BadRequestException(PathQueryUtil.getProblemsSummary(pathQuery.
                        getProblems()));
            }
        } else {
            throw new BadRequestException(formatMessage(validator.getErrorsAndWarnings()));
        }
    }

    private String formatMessage(List<String> msgs) {
        StringBuilder sb = new StringBuilder();
        for (String msg : msgs) {
            sb.append(msg);
            if (!msg.endsWith(".")) {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    /**
     * Returns parsed PathQuery.
     * @return parsed PathQuery
     */
    public PathQuery getQuery() {
        return pathQuery;
    }
}
