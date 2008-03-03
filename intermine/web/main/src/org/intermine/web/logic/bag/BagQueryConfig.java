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

import java.util.Map;

/**
 * Configuration for BagQuery objects.
 * @author Kim Rutherford
 */
public class BagQueryConfig
{
    private String connectField;
    private String extraConstraintClassName;
    private String constrainField;
    private final Map bagQueries;
    private Map<String, Map> additionalConverters;

    /**
     * Create a new BagQueryConfig object.
     * @param bagQueries a Map from class name to bag query
     * @param additionalConverters extra converters configured
     */
    public BagQueryConfig(Map bagQueries, Map additionalConverters) {
        this.bagQueries = bagQueries;
        this.additionalConverters = additionalConverters;
    }

    /**
     * Return the class name that was passed to the constructor.  This (and connectField and
     * constrainField) is used to configure the addition of an extra constraint to the bag queries.
     * (eg. constraining the Organism).
     *
     * @return the extra class name
     */
    public String getExtraConstraintClassName() {
        return extraConstraintClassName;
    }

    /**
     * Set the class name of extra constraint to use in BagQuery objects using this config object.
     *
     * @param extraConstraintClassName the class name
     */
    public void setExtraConstraintClassName(String extraConstraintClassName) {
        this.extraConstraintClassName = extraConstraintClassName;
    }

    /**
     * Return the connecting field.
     *
     * @return the connecting field
     */
    public String getConnectField() {
        return connectField;
    }

    /**
     * Set the connecting field for adding an extra constraint to bag queries.
     *
     * @param connectField the field name
     */
    public void setConnectField(String connectField) {
        this.connectField = connectField;
    }

    /**
     * Return the constrain field.
     *
     * @return the constrain field
     */
    public String getConstrainField() {
        return constrainField;
    }

    /**
     * Set the field to constrain when making an extra constraint.
     *
     * @param constrainField the constraint field
     */
    public void setConstrainField(String constrainField) {
        this.constrainField = constrainField;
    }

    /**
     * Return a Map from type name to a List of BagQuerys to run for that type
     * @return the BagQuerys Map
     */
    public Map getBagQueries() {
        return bagQueries;
    }

    /**
     * Return a Map from converter Class name to field name to use in the url to get that field
     * @param type get converters for this type or a subtype of it
     * @return the additionalConverters
     */
    public Map<String, String[]> getAdditionalConverters(String type) {
        return additionalConverters.get(type);
    }
}
