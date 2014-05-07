package org.intermine.api.mines;

/*
 * Copyright (C) 2002-2014 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.intermine.util.CacheMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Class to query friendly mines.
 *
 * @author Julie Sullivan
 */
public final class FriendlyMineQueryRunner
{
    private static final Logger LOG = Logger.getLogger(FriendlyMineQueryRunner.class);
    private static final String WEBSERVICE_URL = "/service";
    private static final String QUERY_PATH = "/query/results?format=json&query=";
    private static Map<MultiKey, JSONObject> queryResultsCache
        = new CacheMap<MultiKey, JSONObject>();
    private static final String RELEASE_VERSION_URL = "/version/release";
    private static final boolean DEBUG = false;
    private static final int CONNECT_TIMEOUT = 20000; // 20 seconds

    private FriendlyMineQueryRunner() {
        // don't
    }

    /**
     * Query a mine and recieve map of results.  only processes first two columns set as id and
     * name
     *
     * @param mine mine to query
     * @param xmlQuery query to run
     * @return map of results
     * @throws IOException if something goes wrong
     * @throws JSONException bad JSON
     */
    public static JSONObject runJSONWebServiceQuery(Mine mine, String xmlQuery)
        throws IOException, JSONException {
        MultiKey key = new MultiKey(mine, xmlQuery);
        JSONObject jsonMine = queryResultsCache.get(key);
        if (jsonMine != null) {
            return jsonMine;
        }
        List<Map<String, String>> results = new ArrayList<Map<String, String>>();

        BufferedReader reader = runWebServiceQuery(mine, xmlQuery);
        if (reader == null) {
            LOG.info(String.format("no results found for %s for query \"%s\"",
                    mine.getName(), xmlQuery));
            return null;
        }
        JSONArray queryResults = new JSONArray(new JSONTokener(reader));
        for (int i = 0; i < queryResults.length(); i++) {
            Map<String, String> result = new HashMap<String, String>();
            result.put("id", queryResults.getJSONObject(i).getString("id"));
            result.put("name", queryResults.getJSONObject(i).getString("name"));

            // optional
            String extraValue = queryResults.getJSONObject(i).getString("shortName");
            // used for extra value, eg. organism name
            if (extraValue != null) {
                result.put("ref", extraValue);
            }
            results.add(result);
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("results", results);
        jsonMine = new JSONObject(data);
        queryResultsCache.put(key, jsonMine);
        return jsonMine;
    }

    /**
     * Run a query on a mine using XML query
     * @param mine mine to query
     * @param xmlQuery pathQuery.toXML()
     * @return results
     */
    private static BufferedReader runWebServiceQuery(Mine mine, String xmlQuery) {
        try {
            String urlString = mine.getUrl() + WEBSERVICE_URL + QUERY_PATH
                    + URLEncoder.encode("" + xmlQuery, "UTF-8");
            URL url = new URL(urlString);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            return reader;
        } catch (Exception e) {
            LOG.info("Unable to access " + mine.getName() + " exception: " + e.getMessage());
            return null;
        }
    }

    /**
     * get release version number for each mine.  if release number is different from the one
     * we have locally, run queries to populate maps
     * @param mines list of mines to update
     */
    public static void updateReleaseVersion(Map<String, Mine> mines) {
        boolean clearCache = false;
        for (Mine mine : mines.values()) {
            String currentReleaseVersion = mine.getReleaseVersion();
            String url = mine.getUrl() + WEBSERVICE_URL + RELEASE_VERSION_URL;
            BufferedReader reader = runWebServiceQuery(url);
            final String msg = "Unable to retrieve release version for " + mine.getName();
            String newReleaseVersion = null;

            if (reader != null) {
                try {
                    newReleaseVersion = IOUtils.toString(reader);
                } catch (Exception e) {
                    LOG.warn(msg, e);
                    continue;
                }
            }

            if (StringUtils.isBlank(newReleaseVersion)
                    && StringUtils.isBlank(currentReleaseVersion)) {
                // didn't get a release version this time or last time
                LOG.warn(msg);
                continue;
            }

            // if release version is different
            if (!StringUtils.equals(newReleaseVersion, currentReleaseVersion)
                    || StringUtils.isBlank(currentReleaseVersion)
                    || DEBUG) {

                // update release version
                mine.setReleaseVersion(newReleaseVersion);
                clearCache = true;
            }
        }
        if (clearCache) {
            queryResultsCache = new HashMap<MultiKey, JSONObject>();
        }
    }


    /**
     * Run a query via the web service
     *
     * @param urlString url to query
     * @return reader
     */
    public static BufferedReader runWebServiceQuery(String urlString) {
        if (StringUtils.isEmpty(urlString)) {
            return null;
        }
        BufferedReader reader = null;
        try {
            if (!urlString.contains("?")) {
                // GET
                URL url = new URL(urlString);
                URLConnection conn = url.openConnection();
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                LOG.info("FriendlyMine URL (GET) " + urlString);
            } else {
                // POST
                String[] params = urlString.split("\\?");
                String newUrlString = params[0];
                String queryString = params[1];
                URL url = new URL(newUrlString);
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(queryString);
                wr.flush();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                LOG.info("FriendlyMine URL (POST) " + urlString);
            }
            return reader;
        } catch (Exception e) {
            LOG.info("Unable to access " + urlString + " exception: " + e.getMessage());
            return null;
        }
    }
}

