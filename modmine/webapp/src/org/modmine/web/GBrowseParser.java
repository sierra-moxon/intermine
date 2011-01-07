package org.modmine.web;

/*
 * Copyright (C) 2002-2011 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.intermine.util.PropertiesUtil;

/**
 * @author contrino
 *
 */
public class GBrowseParser
{
    private static final Logger LOG = Logger.getLogger(GBrowseParser.class);

    private static final String GBROWSE_BASE_URL = getGBrowsePrefix();
    @SuppressWarnings("unused")
    private static final String GBROWSE_URL_END = "/?show_tracks=1";
    private static final String GBROWSE_ST_URL_END = "/?action=scan";
    private static final String GBROWSE_DEFAULT_URL =
        "http://modencode.oicr.on.ca/cgi-bin/gb2/gbrowse/";

    /**
     * A GBrowse track, identified by
     * the organism, the track name, and eventually the subtrack name.
     *
     * subtracks are linked to modENCODE submissions
     *
     */
    public static class GBrowseTrack
    {
        private String organism; // {fly,worm}
        private String track;    // e.g. Snyder_PHA4_GFP_COMB
        private String subTrack; // e.g. PHA4_L2_GFP
        private Integer DCCid;

        /**
         * Instantiates a GBrowseTrack only to track level.
         *
         * @param organismName e.g. fly, worm
         * @param trackName    e.g. Snyder_PHA4_GFP_COMB
         */

        public GBrowseTrack(String organismName, String trackName) {
            this.organism  = organismName;
            this.track = trackName;
        }

        /**
         * Instantiates a GBrowse track fully.
         *
         * @param organism     e.g. fly, worm
         * @param track        e.g. Snyder_PHA4_GFP_COMB
         * @param subTrack     e.g. PHA4_L2_GFP
         * @param DCCid
         */
        public GBrowseTrack(String organism, String track, String subTrack, Integer DCCid) {
            this.organism  = organism;
            this.track = track;
            this.subTrack = subTrack;
            this.DCCid = DCCid;
        }

        /**
         * @return the organism
         */
        public String getOrganism() {
            return organism;
        }

        /**
         * @return the track name
         */
        public String getTrack() {
            return track;
        }

        /**
         * @return the subTrack
         */
        public String getSubTrack() {
            return subTrack;
        }

        /**
         * @return the DCCid
         */
        public Integer getDCCid() {
            return DCCid;
        }
    }

    /**
     * Method to read the list of GBrowse tracks for a given organism
     *
     * @param organism (i.e. fly or worm)
     * @return submissionTracksCache
     */
    public static Map<Integer, List<GBrowseTrack>> readTracks(String organism) {
        Map<Integer, List<GBrowseTrack>> submissionsToTracks =
            new HashMap<Integer, List<GBrowseTrack>>();
        try {
            URL url = new URL(GBROWSE_BASE_URL + organism + GBROWSE_ST_URL_END);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;

            final String SEPARATOR = ";";

            // examples of lines:
            //
            // [Henikoff_Salt_H3_WIG]
            // key      = H3.3 Chromatin fractions extracted with NaCl
            // select   = 80mM fraction;2534 350mM fraction;2535 600mM fraction;2536
            // citation = <h1> H3.3 NaCl Salt Extracted Chromatin ....
            //
            // [LIEB_WIG_CHROMNUC_ENV]
            // key      = Chromosome-Nuclear Envelope Interaction
            // select   = SDQ3891_LEM2_N2_MXEMB_1;2729 SDQ3897_NPP13_N2_MXEMB_1;2738
            // citation = <h1> Chromosome-Nuclear Envelope Interaction proteins...
            //
            // note: subtracks have also names with spaces

            StringBuffer trackName = new StringBuffer();
            StringBuffer toAppend = new StringBuffer();

            while ((line = reader.readLine()) != null) {
                LOG.debug("SUBTRACK LINE: " + line);
                if (line.startsWith("[")) {
                    // this is a track
                    trackName.setLength(0);
                    trackName.append(line.substring(1, line.indexOf(']')));
                }
                if (line.startsWith("select")) {
                    // here subtracks are listed
                    String data = line.replace("select   = ", "");
                    String[] result = data.split("\\s");
                    for (String token : result) {
                        if (token.indexOf(SEPARATOR) < 0) {
                            // we are dealing with a bit of name
                            toAppend.append(token + " ");
                        } else {
                            // this is a token with subId
                            String subTrack = toAppend.toString()
                                + token.substring(0, token.indexOf(SEPARATOR));
                            String dcc = token.substring(
                                    token.indexOf(SEPARATOR) + 1, token.length());
                            if (!StringUtils.isNumeric(dcc)) {
                                LOG.error("[SUBTRACKS] " + subTrack + " has a non numeric dccId: "
                                        + dcc);
                                continue;
                            }
                            Integer dccId = Integer.parseInt(dcc);
                            LOG.debug("SUBTRACK: " + subTrack + "|" + dccId);
                            toAppend.setLength(0); // empty buffer
                            GBrowseTrack newTrack =
                                new GBrowseTrack(organism, trackName.toString(), subTrack, dccId);
                            addToGBMap(submissionsToTracks, dccId, newTrack);
                        }
                    }
                }
            }
            reader.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
        return submissionsToTracks;
    }


    /**
     * This method adds a GBrowse track to a map with
     * key = dccId
     * value = list of associated GBrowse tracks
     */
    private static void addToGBMap(
            Map<Integer, List<GBrowseTrack>> m,
            Integer key, GBrowseTrack value) {
        //
        List<GBrowseTrack> gbs = new ArrayList<GBrowseTrack>();

        if (m.containsKey(key)) {
            gbs = m.get(key);
        }
        if (!gbs.contains(value)) {
            gbs.add(value);
            m.put(key, gbs);
        }
    }


    /**
     * This method get the GBrowse base URL from the properties
     * or default to one
     * @return the base URL
     */
    public static String getGBrowsePrefix() {
        Properties props = PropertiesUtil.getProperties();
        String gbURL = props.getProperty("gbrowse.prefix") + "/";
        if (gbURL == null || gbURL.length() < 5) {
            return GBROWSE_DEFAULT_URL;
        }
        return gbURL;
    }

}
