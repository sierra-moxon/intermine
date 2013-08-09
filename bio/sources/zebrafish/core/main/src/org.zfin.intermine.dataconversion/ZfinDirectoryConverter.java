package org.zfin.intermine.dataconversion;

import org.apache.commons.lang.StringUtils;
import org.intermine.bio.dataconversion.BioDirectoryConverter;
import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.util.FormattedTextParser;
import org.intermine.xml.full.Item;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Directory converter
 */
public abstract class ZfinDirectoryConverter extends BioDirectoryConverter {

    protected static final String DATA_SOURCE_NAME = "ZFIN";
    public File directory;

    public ZfinDirectoryConverter(ItemWriter writer, Model model, String dataSourceName, String dataSetTitle) {
        super(writer, model, dataSourceName, dataSetTitle);
    }

    protected void setAttribute(Item item, String key, String value) {
        if (!StringUtils.isEmpty(value)) {
            item.setAttribute(key, value);
        }
    }

    protected Item getItem(String pkID, String itemName) throws SAXException {
        return getItem(pkID, itemName, null);
    }


    protected Item getItem(String pkID, String itemName, Map<String, Item> itemMap) throws SAXException {
        if (itemMap == null)
            return createItem(pkID, itemName);
        Item item = itemMap.get(pkID);
        if (item == null) {
            item = createItem(pkID, itemName);
            itemMap.put(pkID, item);
        }
        return item;

    }

    protected void storeAll(Map<String, Item> items, String... itemNames) throws ObjectStoreException {
        if (itemNames == null)
            return;
        for (String itemName : itemNames) {
            Iterator<Map.Entry<String,Item>> iterator = items.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String,Item> entry = iterator.next();
                Item value = entry.getValue();
                if (value.getClassName().equals(itemName)) {
                    store(value);
                    iterator.remove();
                }
            }
        }
        // store the remaining objects
        for (Item feature : items.values())
            store(feature);

    }


    private Item createItem(String primaryID, String itemName) {
        Item item = super.createItem(itemName);
        item.setAttribute(ColumnDefinition.PRIMARY_IDENTIFIER, primaryID);
        return item;
    }

    protected Iterator<String[]> getLineIterator(FileReader reader) throws IOException {
        return FormattedTextParser.parseDelimitedReader(reader, '|');
    }

    public void processFile(SpecificationSheet specSheet) throws Exception {
        FileReader reader = new FileReader(directory.getCanonicalPath() + System.getProperty("file.separator") + specSheet.getFileName());
        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');
        int lineIndex = 0;
        while (lineIter.hasNext()) {
            lineIndex++;
            String[] line = (String[]) lineIter.next();
            if (line.length < specSheet.getNumberOfColumns()) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }
            // handle primaryIdentifier
            int primaryIdentifierColumn = specSheet.getPrimaryIdentifierColumn();
            String pkID = line[primaryIdentifierColumn];
            if (StringUtils.isEmpty(pkID)) {
                throw new RuntimeException("primaryIdentifier is null in line " + lineIndex);
            }
            ColumnDefinition columnDefinition = specSheet.getColumnDefinition(primaryIdentifierColumn);
            // main item to work with (populate)
            Item item;
            if (specSheet.isMappedItemName()) {
                String mappedItemName = getMappedItemName(line[specSheet.getMappingColumn()]);
                item = getItem(pkID,
                        mappedItemName, specSheet.getItemMap(ColumnDefinition.MAPPED_ITEM_NAME));
            } else {
                item = getItem(pkID,
                        columnDefinition.getItemName(),
                        specSheet.getItemMap());
            }
            int columnIndex = 0;
            for (String colEntry : line) {
                if (columnIndex > specSheet.getNumberOfColumns() - 1 || specSheet.isPKColumn(columnIndex) || specSheet.getMappingColumn() == columnIndex) {
                    columnIndex++;
                    continue;
                }
                if (StringUtils.isNotEmpty(colEntry)) {
                    ColumnDefinition colDefinition = specSheet.getColumnDefinition(columnIndex);
                    if (colDefinition.isAttribute())
                        item.setAttribute(colDefinition.getName(), line[columnIndex]);
                    if (colDefinition.isReference()) {
                        Item reference = getItem(colEntry, colDefinition.getReferenceName(), specSheet.getItemMap());
                        item.setReference(colDefinition.getName(), reference);
                    }
                    if (colDefinition.isCollection()) {
                        Item reference = getItem(colEntry, colDefinition.getReferenceName(), specSheet.getItemMap());
                        item.addToCollection(colDefinition.getName(), reference);
                    }
                }
                columnIndex++;
            }
        }
    }

    /**
     * Override if you need to map an id to an item name such as features.
     *
     * @param featureType
     * @return
     */
    public String getMappedItemName(String featureType) {
        return null;
    }
}