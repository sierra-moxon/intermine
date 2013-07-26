package org.zfin.intermine.dataconversion;

import org.apache.commons.lang.StringUtils;
import org.intermine.bio.dataconversion.BioDirectoryConverter;
import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.util.FormattedTextParser;
import org.intermine.xml.full.Item;
import org.xml.sax.SAXException;

import java.io.Reader;
import java.util.Iterator;
import java.util.Map;

/**
 * Directory converter
 */
public abstract class ZfinDirectoryConverter extends BioDirectoryConverter {

    protected static final String DATA_SOURCE_NAME = "ZFIN";

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

    private Item createItem(String primaryID, String itemName) {
        Item item = super.createItem(itemName);
        item.setAttribute(ColumnDefinition.PRIMARY_IDENTIFIER, primaryID);
        return item;
    }

    public void processFile(Reader reader, SpecificationSheet specSheet) throws Exception {

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
            Item item = getItem(pkID,
                    columnDefinition.getItemName(),
                    specSheet.getItemMap());

            int columnIndex = 0;
            for (String colEntry : line) {
                if (columnIndex > specSheet.getNumberOfColumns() - 1 || specSheet.isPKColumn(columnIndex)) {
                    columnIndex++;
                    continue;
                }
                if (StringUtils.isNotEmpty(colEntry)) {
                    item.setAttribute(specSheet.getColumnDefinition(columnIndex).getName(), line[columnIndex]);
                }
                columnIndex++;
            }
        }
    }
}