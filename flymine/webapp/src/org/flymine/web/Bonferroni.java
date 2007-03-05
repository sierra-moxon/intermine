package org.flymine.web;

/* 
 * Copyright (C) 2002-2005 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.HashMap;
import java.util.Iterator;


/**
 *
 * @author Julie Sullivan
 */
public class Bonferroni
{
    private static HashMap originalMap = new HashMap();
    private static HashMap adjustedMap = new HashMap();
    private static Double significanceValue;
    private static double numberOfTests;
    private static final Double maxValue = new Double("1");

    /**
     * @param originalMap Hashmap of go terms and their pvalues.
     * @param significanceValue significance Value.
     */
    public Bonferroni(HashMap originalMap, String significanceValue) {
      
        this.originalMap = originalMap;
        this.significanceValue = new Double(significanceValue);
        this.numberOfTests = originalMap.size();
    }

    
    public void calculate() {
        
        double bonferroni = significanceValue.doubleValue()/numberOfTests;
        
        for (Iterator iter = originalMap.keySet().iterator(); iter.hasNext();) {

            // get original values
            String goTerm = (String) iter.next();
            Double p = (Double) originalMap.get(goTerm);
            
            // calc new value
            Double adjustedP = new Double(numberOfTests * p.doubleValue());
            //Double adjustedP = new Double(bonferroni + p.doubleValue());
            
            // if the new value is greater than 1, just set to 1.
            // we don't want to confuse people
            if (adjustedP.compareTo(maxValue) > 0) {
                adjustedP = maxValue;
            }
            
            // put new values into map
            adjustedMap.put(goTerm, adjustedP);
            
        }
    }


    public HashMap getAdjustedMap() {
        return adjustedMap;
    }
}
