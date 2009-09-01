package org.intermine.web.logic;

/*
 * Copyright (C) 2002-2009 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.intermine.api.bag.InterMineBag;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.Results;
import org.intermine.objectstore.query.ResultsRow;
import org.intermine.web.logic.widget.BenjaminiHochberg;
import org.intermine.web.logic.widget.Bonferroni;
import org.intermine.web.logic.widget.EnrichmentWidgetLdr;
import org.intermine.web.logic.widget.ErrorCorrection;
import org.intermine.web.logic.widget.Hypergeometric;

/**
 * Utility methods for the web package.
 *
 * @author Kim Rutherford
 * @author Julie Sullivan
 */

public abstract class WebUtil
{
    protected static final Logger LOG = Logger.getLogger(WebUtil.class);

    /**
     * Lookup an Integer property from the SessionContext and return it.
     * @param session the current session
     * @param propertyName the property to find
     * @param defaultValue the value to return if the property isn't present
     * @return the int value of the property
     */
    public static int getIntSessionProperty(HttpSession session, String propertyName,
                                            int defaultValue) {
        Map webProperties =
            (Map) session.getServletContext().getAttribute(Constants.WEB_PROPERTIES);
        String n = (String) webProperties.get(propertyName);

        int intVal = defaultValue;

        try {
            intVal = Integer.parseInt(n);
        } catch (NumberFormatException e) {
            LOG.warn("Failed to parse " + propertyName + " property: " + n);
        }

        return intVal;
    }


    /**
     * Verifies names (bags, queries, etc) only contain A-Z, a-z, 0-9, underscores and
     * dashes.  And spaces.  And dots.
     * @param name Name of bag/query/template to be validated
     * @return isValid Returns true if this name is correct, false if this name contains a bad char
     */
    public static boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        Pattern p = Pattern.compile("[^\\w\\s\\.\\-:]");
        Matcher m = p.matcher(name);
        return !m.find();
    }

    /**
     * Returns the word value of special characters (ie returns _AMPERSAND_ for &, etc).  Used for
     * the forced renaming of queries/templates in the query/template import.
     * @param specialCharacter The special character, ie &
     * @return wordEquivalent The special character's name, ie AMPERSAND
     */
    public static String getSpecCharToText(String specialCharacter) {

        HashMap specCharToText = mapChars();
        String wordEquivalent = (String) specCharToText.get(specialCharacter);
        wordEquivalent = "_" + wordEquivalent + "_";
        return wordEquivalent;

    }


    /**
     * Takes a string and replaces special characters with the text value, e.g. it would change
     * "a&b" to "a_AMPERSAND_b".  This is used in the query/template imports to handle special
     * characters.
     * @param name Name of query/template
     * @return rebuiltName Name of query/template with the special characters removed
     */
    public static String replaceSpecialChars(String name) {
        String tmp = name;
        String rebuiltName = "";

        for (int i = 0; i < tmp.length(); i++) {
            char c = tmp.charAt(i);
            String str = String.valueOf(c);

            if (!WebUtil.isValidName(str)) {
                rebuiltName += WebUtil.getSpecCharToText(str);
            } else {
                rebuiltName += str;
            }
        }
        return rebuiltName;
    }


    private static HashMap<String, String> mapChars() {

        HashMap<String, String> specCharToText = new HashMap<String, String> ();

        specCharToText.put("‘", new String("QUOTE"));
        specCharToText.put("’", new String("QUOTE"));
        specCharToText.put("“", new String("QUOTE"));
        specCharToText.put("”", new String("QUOTE"));
        specCharToText.put("‹", new String("LESS_THAN_SIGN"));
        specCharToText.put("›", new String("GREATER_THAN_SIGN"));
        specCharToText.put("!", new String("EXCLAMATION_POINT"));
        specCharToText.put("£", new String("POUND_SIGN"));
        specCharToText.put("$", new String("DOLLAR_SIGN"));
        specCharToText.put("%", new String("PERCENT_SIGN"));

        specCharToText.put("^", new String("CARET"));
        specCharToText.put("&", new String("AMPERSAND"));
        specCharToText.put("(", new String("LEFT_PARENTHESIS"));
        specCharToText.put(")", new String("RIGHT_PARENTHESIS"));
        specCharToText.put("+", new String("PLUS_SIGN"));
        specCharToText.put("=", new String("EQUALS_SIGN"));
        specCharToText.put("{", new String("LEFT_BRACKET"));
        specCharToText.put("}", new String("RIGHT_BRACKET"));
        specCharToText.put("[", new String("LEFT_BRACKET"));
        specCharToText.put("]", new String("RIGHT_BRACKET"));
        specCharToText.put(":", new String("COLON"));

        specCharToText.put(";", new String("SEMICOLON"));
        specCharToText.put("@", new String("AT_SIGN"));
        specCharToText.put(",", new String("COMMA"));
        specCharToText.put("?", new String("QUESTION_MARK"));
        specCharToText.put("~", new String("TILDE"));
        specCharToText.put("#", new String("HASH"));
        specCharToText.put("<", new String("LESS_THAN"));
        specCharToText.put(">", new String("GREATER_THAN"));
        specCharToText.put("'", new String("APOSTROPHE"));
        specCharToText.put("/", new String("FORWARD_SLASH"));
        specCharToText.put("\\", new String("BACK_SLASH"));
        specCharToText.put("*", new String("STAR"));

        return specCharToText;
    }

    /**
     * takes a map and puts it in random order
     * also shortens the list to be map.size() = max
     * @param map The map to be randomised - the Map will be unchanged after the call
     * @param max the number of items to be in the final list
     * @param <V> the value type
     * @return the newly randomised, shortened map
     */
    public static <V> Map<String, V> shuffle(Map<String, V> map, int max) {
        List<String> keys = new ArrayList<String>(map.keySet());

        Collections.shuffle(keys);

        if (keys.size() > max) {
            keys = keys.subList(0, max);
        }

        Map<String, V> returnMap = new HashMap<String, V>();

        for (String key: keys) {
            returnMap.put(key, map.get(key));
        }
        return returnMap;
    }


    /**
     * Return the contents of the page given by prefixURLString + '/' + path as a String.  Any
     * relative links in the page will be modified to go via showStatic.do
     * @param prefixURLString the prefix (including "http://...") of the web site to read from.
     *    eg. http://www.flymine.org/doc/help
     * @param path the page to retrieve eg. manualFlyMineHome.shtml
     * @return the contents of the page
     * @throws IOException if there is a problem while reading
     */
    public static String getStaticPage(String prefixURLString, String path)
        throws IOException {
        StringBuffer buf = new StringBuffer();

        URL url = new URL(prefixURLString + '/' + path);
        URLConnection connection = url.openConnection();
        InputStream is = connection.getInputStream();
        Reader reader = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(reader);
        String line;
        while ((line = br.readLine()) != null) {
            // replace relative urls ie. href="manualExportfasta.shtml"
            line = line.replaceAll("href=\"([^\"]+)\"",
                                   "href=\"showStatic.do?path=$1\"");
            buf.append(line + "\n");
        }
        return buf.toString();
    }

    private static Map<String, List> statsCalcCache = new HashMap<String, List>();

    /**
     * Runs both queries and compares the results.
     * @param os the object store
     * @param ldr the loader
     * @param bag the bag we are analysing
     * @param maxValue maximum value to return - for display purposes only
     * @param errorCorrection which error correction algorithm to use, Bonferroni
     * or Benjamini Hochberg or none
     * @return array of three results maps
     */
    public static ArrayList statsCalc(ObjectStore os,
                                      EnrichmentWidgetLdr ldr,
                                      InterMineBag bag,
                                      Double maxValue,
                                      String errorCorrection) {

        ArrayList<Map> maps = new ArrayList<Map>();

        int populationTotal = calcTotal(os, ldr, true); // objects annotated in database
        int sampleTotal = calcTotal(os, ldr, false);    // objects annotated in bag

        // sample query
        Query q = ldr.getSampleQuery(false);

        Results r = null;

        HashMap<String, Long> countMap = new HashMap();
        HashMap<String, String> idMap = new HashMap();
        HashMap<String, BigDecimal> resultsMap = new HashMap();
        Map dummy = new HashMap();
        SortableMap sortedMap = new SortableMap();

        // if the model has changed, the query might not be valid
        if (q != null) {
            r = os.execute(q, 20000, true, true, true);

            Iterator iter = r.iterator();

            while (iter.hasNext()) {

                // extract results
                ResultsRow rr =  (ResultsRow) iter.next();

                // id of item
                String id = (String) rr.get(0);

                // count of item
                Long count = (Long) rr.get(1);

                // id & count
                countMap.put(id, count);

                // id & label
                idMap.put(id, (String) rr.get(2));

            }

            // run population query
            List rAll = statsCalcCache.get(ldr.getPopulationQuery(false).toString());
            if (rAll == null) {
                rAll = os.execute(ldr.getPopulationQuery(false), 20000, true, true, true);
                rAll = new ArrayList(rAll);
                statsCalcCache.put(ldr.getPopulationQuery(false).toString(), rAll);
            }

            Iterator itAll = rAll.iterator();

            // loop through results again to calculate p-values
            while (itAll.hasNext()) {

                ResultsRow rrAll =  (ResultsRow) itAll.next();

                String id = (String) rrAll.get(0);

                if (countMap.containsKey(id)) {

                    Long countBag = countMap.get(id);
                    Long countAll = (java.lang.Long) rrAll.get(1);

                    // (k,n,M,N)
                    double p = Hypergeometric.calculateP(countBag.intValue(), sampleTotal,
                                                         countAll.intValue(), populationTotal);

                    try {
                        resultsMap.put(id, new BigDecimal(p));
                    } catch (Exception e) {
                        String msg = p + " isn't a double.  calculated for " + id + " using "
                        + " k: "  + countBag
                        + ", n: " + sampleTotal
                        + ", M: " + countAll
                        + ", N: " + populationTotal
                        + ".  k query: "
                        + ldr.getSampleQuery(false).toString()
                        + ".  n query: "
                        + ldr.getSampleQuery(true).toString()
                        + ".  M query: "
                        + ldr.getPopulationQuery(false).toString()
                        + ".  N query: "
                        + ldr.getPopulationQuery(true).toString();

                        throw new RuntimeException(msg, e);
                    }
                }
            }

            Map<String, BigDecimal> adjustedResultsMap = new HashMap<String, BigDecimal>();

            if (!errorCorrection.equals("None")) {
                adjustedResultsMap = calcErrorCorrection(errorCorrection, maxValue, resultsMap);
            } else {
                // TODO move this to the ErrorCorrection class
                BigDecimal max = new BigDecimal(maxValue.doubleValue());
                for (String id : resultsMap.keySet()) {
                    BigDecimal pvalue = resultsMap.get(id);
                    if (pvalue.compareTo(max) <= 0) {
                        adjustedResultsMap.put(id, pvalue);
                    }
                }
            }
            sortedMap = new SortableMap(adjustedResultsMap);
            sortedMap.sortValues();
            dummy.put("widgetTotal", new Integer(sampleTotal));
        } else {
            // no results
            dummy.put("widgetTotal", new Integer(0));
        }

        maps.add(0, sortedMap);
        maps.add(1, countMap);
        maps.add(2, idMap);
        maps.add(3, dummy);

        return maps;
    }

    /**
     * See online help docs for detailed description of what error correction is and why we need it.
     * Briefly, in all experiments certain things happen that look interesting but really just
     * happened by chance.  We need to account for this phenomenon to ensure our numbers are
     * interesting behaviour and not just random happenstance.
     *
     * To do this we take all of our p-values and adjust them.  Here we are using on of our two
     * methods available - which one we use is determined by the user.
     * @param errorCorrection which multiple hypothesis test correction to use - Bonferroni or
     * BenjaminiHochberg
     * @param maxValue maximum value we're interested in - used for display purposes only
     * @param resultsMap map containing unadjusted p-values
     * @return map of all the adjusted p-values
     */
    protected static Map<String, BigDecimal> calcErrorCorrection(String errorCorrection,
                                                 Double maxValue,
                                                 HashMap<String, BigDecimal> resultsMap) {

        ErrorCorrection e = null;

        if (errorCorrection != null && errorCorrection.equals("Bonferroni")) {
            e = new Bonferroni(resultsMap);
        } else {
            e = new BenjaminiHochberg(resultsMap);
        }
        e.calculate(maxValue);
        return e.getAdjustedMap();
    }

    private static int calcTotal(ObjectStore os, EnrichmentWidgetLdr ldr, boolean calcTotal) {
        Query q = new Query();
        if (calcTotal) {
            q = ldr.getPopulationQuery(true);
        } else {
            q = ldr.getSampleQuery(true);
        }
        if (q == null) {
            // bad query, model probably changed.  no results
            return 0;
        }
        Object[] o = os.executeSingleton(q).toArray();
        if (o.length == 0) {
            // no results
            return  0;
        }
        return  ((java.lang.Long) o[0]).intValue();
    }


    /**
     * Formats column name. Replaces " > " with "&nbsp;&gt; ".
     * @param original original column name
     * @return modified string
     */
    public static String formatColumnName(String original) {
        // replaces all dots and colons but not dots with following space - they are probably
        // part of name, e.g. 'D. melanogaster'
        return original.replaceAll("&", "&amp;").replaceAll(" > ", "&nbsp;&gt; ")
            .replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
}
