package org.intermine.web.logic.widget.config;

/*
 * Copyright (C) 2002-2009 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.intermine.metadata.FieldDescriptor;
import org.intermine.objectstore.ObjectStore;
import org.intermine.web.logic.bag.InterMineBag;
import org.intermine.web.logic.config.WebConfig;
import org.intermine.web.logic.widget.Widget;

/*
 * Copyright (C) 2002-2009 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

/**
 * @author Xavier Watkins
 *
 */
public class ExternalWidgetConfig extends WidgetConfig
{

    private String displayFields, exportField;
    private WebConfig webConfig;
    private Map<String, List<FieldDescriptor>> classKeys;
    private String pathStrings, externalLink, externalLinkLabel;
    private String columnTitle = null;
    private String widgetName, organismShortName = null;

    /**
     * @return the widgetName
     */
    public String getWidgetName() {
        return widgetName;
    }

    /**
     * @param widgetName the widgetName to set
     */
    public void setWidgetName(String widgetName) {
        this.widgetName = widgetName;
    }

    /**
     * @return the organismShortName
     */
    public String getOrganismShortName() {
        return organismShortName;
    }

    /**
     * @param organismShortName the organismShortName to set
     */
    public void setOrganismShortName(String organismShortName) {
        this.organismShortName = organismShortName;
    }

    /**
     * @return the fields
     */
    public String getDisplayFields() {
        return displayFields;
    }

    /**
     * @param fields the fields to set
     */
    public void setDisplayFields(String fields) {
        this.displayFields = fields;
    }



    /**
     * @return the title for the count column
     */
    public String getColumnTitle() {
        return columnTitle;
    }

    /**
     * @param columnTitle set title for count column
     */
    public void setColumnTitle(String columnTitle) {
        this.columnTitle = columnTitle;
    }

    /**
     * Do-nothing implementation of superclass method
     * @param imBag a bag
     * @param os the objectstore
     * @return null
     */
    public Map<String, Collection<String>> getExtraAttributes(InterMineBag imBag,
                                                      ObjectStore os) {
        return null;
    }

    /**
     * @return the webConfig
     */
    public WebConfig getWebConfig() {
        return webConfig;
    }

    /**
     * @param webConfig the webConfig to set
     */
    public void setWebConfig(WebConfig webConfig) {
        this.webConfig = webConfig;
    }

    /**
     * @param classKeys the classKeys to set
     */
    public void setClassKeys(Map<String, List<FieldDescriptor>> classKeys) {
        this.classKeys = classKeys;
    }

    /**
     * Get the classKeys
     * @return the class keys
     */
    public Map<String, List<FieldDescriptor>> getClassKeys() {
        return classKeys;
    }


    /**
     * Comma separated list of path strings to appear in the widget, ie Employee.firstName,
     * Employee.lastName
     * @return the pathStrings
     */
    public String getPathStrings() {
        return pathStrings;
    }

    /**
     * @param pathStrings the pathString to set
     */
    public void setPathStrings(String pathStrings) {
        this.pathStrings = pathStrings;
    }

    /**
     * {@inheritDoc}
     */
    public String getExternalLink() {
        return externalLink;
    }

    /**
     * {@inheritDoc}
     */
    public void setExternalLink(String externalLink) {
        this.externalLink = externalLink;
    }

    /**
     * {@inheritDoc}
     */
    public String getExternalLinkLabel() {
        return externalLinkLabel;
    }

    /**
    * {@inheritDoc}
     */
    public void setExternalLinkLabel(String externalLinkLabel) {
        this.externalLinkLabel = externalLinkLabel;
    }

    /**
     * @return the exportField
     */
    public String getExportField() {
        return exportField;
    }

    /**
     * @param exportField the exportField to set
     */
    public void setExportField(String exportField) {
        this.exportField = exportField;
    }

    /**
     * {@inheritDoc}
     */
    public Widget getWidget(InterMineBag imBag, ObjectStore os,
                                 List<String> selectedExtraAttribute) {
        // TODO fix this
        //return new Widget(this, imBag, os, selectedExtraAttribute.get(0));
        return null;
    }

}
