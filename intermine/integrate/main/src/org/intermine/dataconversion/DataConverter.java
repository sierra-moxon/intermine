package org.intermine.dataconversion;

/*
 * Copyright (C) 2002-2007 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.xml.full.Item;
import org.intermine.xml.full.ItemFactory;
import org.intermine.xml.full.ItemHelper;
import org.intermine.xml.full.ReferenceList;

/**
 * Abstract parent class of all DataConverters
 * @author Mark Woodbridge
 */
public abstract class DataConverter
{
    private static final Logger LOG = Logger.getLogger(DataConverter.class);

    private ItemWriter writer;
    private Map aliases = new HashMap();
    private int nextClsId = 0;
    private Map ids = new HashMap();
    private Model model;
    private ItemFactory itemFactory;
    
    /**
    * Constructor that should be called by children
    * @param writer an ItemWriter used to handle the resultant Items
    * @param model the data model
    */
    public DataConverter(ItemWriter writer, Model model) {
        this.writer = writer;
        this.model = model;
        this.itemFactory = new ItemFactory(this.model);
    }

    /**
     * Return the ItemWriter that was passed to the constructor.
     * @return the ItemWriter
     */
    public ItemWriter getItemWriter() {
        return writer;
    }
    
    /**
     * Uniquely alias a className
     * @param className the class name
     * @return the alias
     */
    protected String alias(String className) {
        String alias = (String) aliases.get(className);
        if (alias != null) {
            return alias;
        }
        String nextIndex = "" + (nextClsId++);
        aliases.put(className, nextIndex);
        LOG.info("Aliasing className " + className + " to index " + nextIndex);
        return nextIndex;
    }
    
    /**
     * Add an Item to a named collection on another Item. If the collection does not exist
     * if will be created.
     * 
     * @param item item with collection
     * @param collection collection name
     * @param addition item to add to collection
     * @throws ObjectStoreException if something goes wrong
     */
    protected void addToCollection(Item item, String collection, Item addition)
        throws ObjectStoreException {
        ReferenceList coll = item.getCollection(collection);
        if (coll == null) {
            coll = new ReferenceList(collection);
            item.addCollection(coll);
        }
        coll.addRefId(addition.getIdentifier());
    }

    /**
     * Create item for the given class name.  Assign a sequential identifier
     * with an alias set for the class, e.g. ClassA: 1_1, 1_2  ClassB: 2_1
     * @param className unqualified classname to create item for
     * @return a new item with an identifier but not fields
     */
    public Item createItem(String className) {
        return itemFactory.makeItem(alias(className) + "_" + newId(className),
                                    model.getNameSpace() + className, "");
    }
    
    /**
     * Generate an identifier for an item, assign ids sequentially with a
     * different alias per class, e.g. ClassA: 1_1, 1_2  ClassB: 2_1
     * @param className the class of the item
     * @return a new identifier with the next sequential id for the given class
     */
    protected String newId(String className) {
        Integer id = (Integer) ids.get(className);
        if (id == null) {
            id = new Integer(0);
            ids.put(className, id);
        }
        id = new Integer(id.intValue() + 1);
        ids.put(className, id);
        return id.toString();
    }

    /**
     * Store a single XML Item
     * @param item the Item to store
     * @throws ObjectStoreException if an error occurs in storing
     */
    public void store(Item item) throws ObjectStoreException {
        getItemWriter().store(ItemHelper.convert(item));
    }

    /**
     * Store a Collection of XMl Items
     * @param c the Collection to store
     * @throws ObjectStoreException if an error occurs in storing
     */
    public void store(Collection<Item> c) throws ObjectStoreException {
        for (Item item : c) {
            getItemWriter().store(ItemHelper.convert(item));
        }
    }
}
