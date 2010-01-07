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

import static org.intermine.pathquery.Constraint.ISO_DATE_FORMAT;

import java.util.Date;

import org.intermine.objectstore.query.ConstraintOp;

import org.intermine.util.TypeUtil;

import java.text.ParseException;

/**
 * Parser for parsing constraint value.
 * @author Jakub Kulaviak
 **/
public class ConstraintValueParser
{

    /**
     * @param value parsed value
     * @param type Java type, it is type of returned object
     * @param constraintOp operation connected with this value
     * @return converted object
     * @throws ParseValueException if value can not be converted to required type
     */
    public static Object parse(String value, Class type, ConstraintOp constraintOp)
        throws ParseValueException {
        Object parsedValue = null;

        if (value == null || value.length() == 0) {
            throw new ParseValueException("No input given, please supply a valid expression");
        }

        if (Date.class.equals(type)) {
            try {
                parsedValue = ISO_DATE_FORMAT.parse(value);
            } catch (ParseException e) {
                throw new ParseValueException(value + " is not a valid date - example: "
                        + ISO_DATE_FORMAT.format(new Date()));
            }
        } else if (String.class.equals(type)) {
            if (value.length() == 0) {
                // Is the expression valid? We need a non-zero length string at least
                throw new ParseValueException("Please supply a valid expression.");
            } else {
                parsedValue = value.trim();
            }
        } else {
            try {
                parsedValue = TypeUtil.stringToObject(type, value);
                if (parsedValue instanceof String) {
                    parsedValue = ((String) parsedValue).trim();
                }
            } catch (NumberFormatException e) {
                throw new ParseValueException(value + " is not a valid number.");
            }
        }
        return parsedValue;
    }
}
