package org.zfin.intermine.dataconversion;

import org.intermine.xml.full.Item;

import java.util.Map;

/**
 * Column definition of a specification sheet
 */
public class ColumnDefinition {

    public static final String PRIMARY_IDENTIFIER = "primaryIdentifier";
    public static final String MAPPED_ITEM_NAME = "mappedItemName";

    private String itemName;
    private String name;
    private String referenceName;
    private boolean collection;
    private Map<String, Item> itemMap;
    private ZdbPkId[] enumMap;
    boolean enumMapping = false;

    public ColumnDefinition(ZdbPkId[] enums) {
        this.enumMap = enums;
        enumMapping = true;
    }

    public ColumnDefinition(String itemName, String name) {
        this.name = name;
        this.itemName = itemName;
    }

    public ColumnDefinition(String itemName) {
        this.name = PRIMARY_IDENTIFIER;
        this.itemName = itemName;
    }

    public ColumnDefinition(String name, Map<String, Item> itemMap) {
        this.name = name;
        this.itemMap = itemMap;
    }

    public ColumnDefinition(String itemName, String name, String referenceName) {
        this.name = name;
        this.itemName = itemName;
        this.referenceName = referenceName;
    }

    public ColumnDefinition(String itemName, String name, boolean isCollection, String referenceName) {
        this.name = name;
        this.itemName = itemName;
        this.collection = isCollection;
        this.referenceName = referenceName;
    }

    public ColumnDefinition(Map<String, Item> itemMap, String name) {
        if (name.equals(PRIMARY_IDENTIFIER))
            this.name = name;
        else
            this.referenceName = name;
        this.itemMap = itemMap;
    }

    public String getName() {
        return name;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemName(String pkID) {
        if (enumMapping) {
            for (ZdbPkId enumeration : enumMap) {
                if (enumeration.toString().equals(pkID))
                    return enumeration.getValue();
            }
        }
        throw new RuntimeException("No PK found for " + pkID);
    }

    public boolean isPrimaryColumnDefinition() {
        return name.equals(PRIMARY_IDENTIFIER);
    }

    public boolean isAttribute() {
        return referenceName == null;
    }

    public boolean isReference() {
        return referenceName != null && !collection;
    }

    public boolean isCollection() {
        return collection;
    }

    public Map<String, Item> getItemMap() {
        return itemMap;
    }

    public void setItemMap(Map<String, Item> itemMap) {
        this.itemMap = itemMap;
    }

    public boolean isEnumMapping() {
        return enumMapping;
    }

}
