package org.intermine.web;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.intermine.model.userprofile.Tag;
import org.intermine.objectstore.ObjectStoreWriter;
import org.intermine.util.SAXParser;
import org.intermine.web.bag.PkQueryIdUpgrader;
import org.intermine.web.logic.bag.IdUpgrader;
import org.intermine.web.logic.profile.Profile;
import org.intermine.web.logic.profile.ProfileManager;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Code for reading and writing ProfileManager objects as XML
 *
 * @author Kim Rutherford
 */

public class ProfileManagerBinding
{
    private static final Logger LOG = Logger.getLogger(ProfileManagerBinding.class);

    /**
     * Convert the contents of a ProfileManager to XML and write the XML to the given writer.
     * @param profileManager the ProfileManager
     * @param writer the XMLStreamWriter to write to
     */
    public static void marshal(ProfileManager profileManager, XMLStreamWriter writer) {
        try {
            writer.writeStartElement("userprofiles");
            List usernames = profileManager.getProfileUserNames();

            Iterator iter = usernames.iterator();

            while (iter.hasNext()) {
                Profile profile = profileManager.getProfile((String) iter.next());
                LOG.info("Writing profile: " + profile.getUsername());
                long startTime = System.currentTimeMillis();

                ProfileBinding.marshal(profile, profileManager.getObjectStore(), writer);

                long totalTime = System.currentTimeMillis() - startTime;
                LOG.info("Finished writing profile: " + profile.getUsername()
                         + " took " + totalTime + "ms.");
            }
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Read a ProfileManager from an XML stream Reader
     * @param reader contains the ProfileManager XML
     * @param profileManager the ProfileManager to store the unmarshalled Profiles to
     * @param osw ObjectStoreWriter used to resolve object ids and write bags
     * @param idUpgrader the IdUpgrader to use to find objects in the new ObjectStore that
     * correspond to object in old bags.
     * @param servletContext global ServletContext object
     * @param abortOnError if true, throw an exception if there is a problem.  If false, log the
     * problem and continue if possible (used by read-userprofile-xml).
     */
    public static void unmarshal(Reader reader, ProfileManager profileManager,
                                 ObjectStoreWriter osw, PkQueryIdUpgrader idUpgrader,
                                 ServletContext servletContext, boolean abortOnError) {
        try {
            ProfileManagerHandler profileManagerHandler =
                new ProfileManagerHandler(profileManager, idUpgrader, servletContext, osw,
                                          abortOnError);
            SAXParser.parse(new InputSource(reader), profileManagerHandler);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Read a ProfileManager from an XML stream Reader.  If there is a problem, throw an
     * exception.
     * @param reader contains the ProfileManager XML
     * @param profileManager the ProfileManager to store the unmarshalled Profiles to
     * @param osw ObjectStoreWriter used to resolve object ids and write bags
     * @param idUpgrader the IdUpgrader to use to find objects in the new ObjectStore that
     * correspond to object in old bags.
     * @param servletContext global ServletContext object
     */
    public static void unmarshal(Reader reader, ProfileManager profileManager,
                                 ObjectStoreWriter osw, PkQueryIdUpgrader idUpgrader,
                                 ServletContext servletContext) {
        unmarshal(reader, profileManager, osw, idUpgrader, servletContext, true);
    }
}

/**
 * Extension of DefaultHandler to handle parsing ProfileManagers
 * @author Kim Rutherford
 */
class ProfileManagerHandler extends DefaultHandler
{
    private ProfileHandler profileHandler = null;
    private ProfileManager profileManager = null;
    private IdUpgrader idUpgrader;
    private ObjectStoreWriter osw;
    private final ServletContext servletContext;
    private boolean abortOnError;
    private long startTime = 0;
    private static final Logger LOG = Logger.getLogger(ProfileManagerBinding.class);

    /**
     * Create a new ProfileManagerHandler
     * @param profileManager the ProfileManager to store the unmarshalled Profile to
     * @param idUpgrader the IdUpgrader to use to find objects in the new ObjectStore that
     * correspond to object in old bags.
     * @param servletContext global ServletContext object
     * @param osw an ObjectStoreWriter to the production database, to write bags
     * @param abortOnError if true, throw an exception if there is a problem.  If false, log the
     * problem and continue if possible (used by read-userprofile-xml).
     */
    public ProfileManagerHandler(ProfileManager profileManager, IdUpgrader idUpgrader,
                                 ServletContext servletContext, ObjectStoreWriter osw,
                                 boolean abortOnError) {
        super();
        this.profileManager = profileManager;
        this.idUpgrader = idUpgrader;
        this.servletContext = servletContext;
        this.osw = osw;
        this.abortOnError = abortOnError;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attrs)
        throws SAXException {
        if (qName.equals("userprofile")) {
            startTime = System.currentTimeMillis();
            profileHandler = new ProfileHandler(profileManager, idUpgrader, servletContext, osw,
                                                abortOnError);
        }
        if (profileHandler != null) {
            profileHandler.startElement(uri, localName, qName, attrs);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (qName.equals("userprofile")) {
            Profile profile = profileHandler.getProfile();
            profileManager.createProfile(profile);
            Iterator tagIter = profileHandler.getTags().iterator();
            while (tagIter.hasNext()) {
                Tag tag = (Tag) tagIter.next();
                profileManager.addTag(tag.getTagName(), tag.getObjectIdentifier(), tag.getType(),
                                      profile.getUsername(), abortOnError);

            }
            profileHandler = null;
            long totalTime = System.currentTimeMillis() - startTime;
            LOG.info("Finished profile: " + profile.getUsername() + " took " + totalTime + "ms.");
        }
        if (profileHandler != null) {
            profileHandler.endElement(uri, localName, qName);
        }
    }
}
