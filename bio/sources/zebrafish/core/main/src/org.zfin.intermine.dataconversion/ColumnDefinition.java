package org.zfin.intermine.dataconversion;

/**
 * Column definition of a specification sheet
 */
public class ColumnDefinition {

    public static final String PRIMARY_IDENTIFIER = "primaryIdentifier";

    private String itemName;
    private String name;
    private String referenceName;
    private boolean collection;

    public ColumnDefinition(String itemName, String name) {
        this.name = name;
        this.itemName = itemName;
    }

    public ColumnDefinition(String itemName) {
        this.name = PRIMARY_IDENTIFIER;
        this.itemName = itemName;
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

    public String getName() {
        return name;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public String getItemName() {
        return itemName;
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
}
