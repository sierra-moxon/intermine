package org.intermine.bio.dataconversion;

/*
 * Copyright (C) 2002-2009 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.util.FormattedTextParser;
import org.intermine.xml.full.Item;
import org.xml.sax.SAXException;


/**
 * @author
 */
public class ZfinExperimentsConverter extends BioFileConverter {

    private static final Logger LOG = Logger.getLogger(ZfinExperimentsConverter.class);
    protected String organismRefId;
    private Map<String, Item> envs = new HashMap();
    private Map<String, Item> envConds = new HashMap();
    /**
     * Constructor
     *
     * @param writer the ItemWriter used to handle the resultant items
     * @param model  the Model
     */
    public ZfinExperimentsConverter(ItemWriter writer, Model model)
            throws ObjectStoreException {
        super(writer, model, "ZFIN", "ZFIN Environments Data Set");

        // create and store organism
        Item organism = createItem("Organism");
        organism.setAttribute("taxonId", "7955");
        store(organism);
        organismRefId = organism.getIdentifier();

    }

    public void process(Reader reader) throws Exception {

        processExps(reader);
	


    }

    public void processExps(Reader reader) throws Exception {

        Iterator lineIter = FormattedTextParser.parseDelimitedReader(reader, '|');

        while (lineIter.hasNext()) {
            String[] line = (String[]) lineIter.next();
            if (line.length < 6) {
                throw new RuntimeException("Line does not have enough elements: " + line.length + line[0]);
            }

            String envcondPrimaryIdentifier = line[0];
            String envPrimaryIdentifier = line[1];
            String envConditionName = line[3];
            String envConditionGroup = line[4];

            Item environment = getEnv(envPrimaryIdentifier);
            Item environmentCondition = getEnvCond(envcondPrimaryIdentifier);
	    
	    if(envPrimaryIdentifier.equals("ZDB-EXP-070511-5")) {
		environment.setAttribute("StandardEnvironment","true");
	    }
	    if(envPrimaryIdentifier.equals("ZDB-EXP-041102-1")) {
		environment.setAttribute("StandardEnvironment","true");
            }

            if (!StringUtils.isEmpty(envConditionName)) {
                environmentCondition.setAttribute("conditionName", envConditionName);
            }
            if (!StringUtils.isEmpty(envConditionGroup)) {
                environmentCondition.setAttribute("conditionGroup", envConditionGroup);
            }
            
            environmentCondition.setReference("environment", environment);

            try {
                store(environmentCondition);
            }
            catch (ObjectStoreException e) {
                throw new SAXException(e);
            }
        }
    }

    private Item getEnvCond(String evncondPrimaryIdentifier)
            throws SAXException {
        Item item = envConds.get(evncondPrimaryIdentifier);
        if (item == null) {
            item = createItem("EnvironmentalCondition");
            item.setAttribute("primaryIdentifier", evncondPrimaryIdentifier);
            envConds.put(evncondPrimaryIdentifier, item);

        }
        return item;
    }

    private Item getEnv(String primaryIdentifier)
            throws SAXException {
        Item item = envs.get(primaryIdentifier);
        if (item == null) {
            item = createItem("Environment");
            /*item.setAttribute("name", name);*/
            item.setAttribute("primaryIdentifier", primaryIdentifier);
	    if(primaryIdentifier.equals("ZDB-EXP-070511-5")) {
                item.setAttribute("StandardEnvironment","true");
            }
            if(primaryIdentifier.equals("ZDB-EXP-041102-1")) {
                item.setAttribute("StandardEnvironment","true");
            }
            envs.put(primaryIdentifier, item);
            try {
                store(item);
	     }
            catch (ObjectStoreException e) {
                throw new SAXException(e);
	     }
        }
        return item;
    }

}
