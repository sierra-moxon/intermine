package org.zfin.intermine.dataconversion;

import org.intermine.xml.full.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpecificationSheet {

    // item map of the main entity
    private Map<String, Item> itemMap;
    private Map<String, Map<String, Item>> itemMaps = new HashMap<String, Map<String, Item>>();
    private List<ColumnDefinition> columnDefinitionList = new ArrayList<ColumnDefinition>(10);
    private String fileName;
    private boolean itemReuse;
    private Enum[] entityMapping;
    private int mappingColumn = -1;

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public boolean isItemReuse() {
        return itemReuse;
    }

    public void setItemReuse(boolean itemReuse) {
        this.itemReuse = itemReuse;
    }

    public void setEntityMapping(Enum[] entityMapping) {
        this.entityMapping = entityMapping;
    }

    public Enum[] getEntityMapping() {
        return entityMapping;
    }

    public void setMappingColumn(int mappingColumn) {
        this.mappingColumn = mappingColumn;
    }

    public int getMappingColumn() {
        return mappingColumn;
    }

    public boolean isMappedItemName() {
        return mappingColumn > -1;
    }
}
