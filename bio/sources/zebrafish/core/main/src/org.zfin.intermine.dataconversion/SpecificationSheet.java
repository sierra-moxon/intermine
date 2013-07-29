package org.zfin.intermine.dataconversion;

import org.intermine.xml.full.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cmpich
 * Date: 7/25/13
 * Time: 12:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpecificationSheet {

    // item map of the main entity
    private Map<String, Item> itemMap;
    private Map<String, Map<String, Item>> itemMaps = new HashMap<String, Map<String, Item>>();
    private List<ColumnDefinition> columnDefinitionList = new ArrayList<ColumnDefinition>(10);

    public void addColumnDefinition(ColumnDefinition definition) {
        columnDefinitionList.add(definition);
    }

    public List<ColumnDefinition> getColumnDefinitionList() {
        return columnDefinitionList;
    }

    public int getNumberOfColumns() {
        return columnDefinitionList.size();
    }

    public int getPrimaryIdentifierColumn() {
        int index = 0;
        for (ColumnDefinition definition : columnDefinitionList) {
            if (definition.isPrimaryColumnDefinition())
                return index;
            index++;
        }
        return -1;
    }

    public ColumnDefinition getColumnDefinition(int columnIndex) {
        return columnDefinitionList.get(columnIndex);
    }

    public boolean isPKColumn(int columnIndex) {
        return columnDefinitionList.get(columnIndex).isPrimaryColumnDefinition();
    }

    public void setItemMap(Map<String, Item> itemMap) {
        this.itemMap = itemMap;
    }

    public Map<String, Item> getItemMap() {
        return itemMap;
    }

    public void addItemMap(String name, Map<String, Item> itemMap){
        itemMaps.put(name, itemMap);
    }

    public Map<String, Item> getItemMap(String key){
        return itemMaps.get(key);
    }
}
