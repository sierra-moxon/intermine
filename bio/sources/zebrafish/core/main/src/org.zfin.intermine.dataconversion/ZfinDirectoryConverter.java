package org.zfin.intermine.dataconversion;

import org.apache.commons.lang.StringUtils;
import org.intermine.bio.dataconversion.BioDirectoryConverter;
import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.xml.full.Item;

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

}
