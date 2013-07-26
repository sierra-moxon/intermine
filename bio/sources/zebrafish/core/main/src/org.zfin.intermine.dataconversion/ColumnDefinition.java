package org.zfin.intermine.dataconversion;

import org.intermine.xml.full.Item;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cmpich
 * Date: 7/25/13
 * Time: 12:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ColumnDefinition {

    public static final String PRIMARY_IDENTIFIER = "primaryIdentifier";

    private String itemName;
    private String name;
    private String referenceName;
    private Map<String, Item> itemMap;


    public ColumnDefinition(String itemName, String name) {
        this.name = name;
        this.itemName = itemName;
    }

    public ColumnDefinition(String itemName, String name, String referenceName, Map<String, Item> itemMap) {
        this.itemMap = itemMap;
        this.name = name;
        this.itemName = itemName;
        this.referenceName = referenceName;
    }

    public String getName() {
        return name;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public Map<String, Item> getItemMap() {
        return itemMap;
    }

    public String getItemName() {
        return itemName;
    }

    public boolean isPrimaryColumnDefinition(){
        return name.equals(PRIMARY_IDENTIFIER);
    }
}
