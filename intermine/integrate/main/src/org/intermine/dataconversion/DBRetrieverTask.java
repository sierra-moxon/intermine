package org.intermine.dataconversion;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStoreWriter;
import org.intermine.objectstore.ObjectStoreWriterFactory;
import org.intermine.sql.Database;
import org.intermine.sql.DatabaseFactory;
import org.intermine.task.ConverterTask;

import org.apache.tools.ant.BuildException;

import org.apache.log4j.Logger;

/**
 * Initiates retrieval and conversion of data from a source database
 *
 * @author Andrew Varley
 * @author Mark Woodbridge
 * @author Matthew Wakeling
 */
public class DBRetrieverTask extends ConverterTask
{
    private static final Logger LOG = Logger.getLogger(DBRetrieverTask.class);

    protected String database;

    /**
     * Set the database name
     * @param database the database name
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * Run the task
     * @throws BuildException if a problem occurs
     */
    public void execute() throws BuildException {
        if (database == null) {
            throw new BuildException("database attribute is not set");
        }
        if (getOsName() == null) {
            throw new BuildException("osName attribute is not set");
        }

        ObjectStoreWriter osw = null;
        ItemWriter writer = null;
        try {
            Database db = DatabaseFactory.getDatabase(database);
            Model m = Model.getInstanceByName(getModelName());
            osw = ObjectStoreWriterFactory.getObjectStoreWriter(getOsName());
            writer = new ObjectStoreItemWriter(osw);
            DBReader reader = new ReadAheadDBReader(db, m);
            System.err .println("Processing data from DB " + db.getURL());
            new DBRetriever(m, db, reader, writer, getExcludeList()).process();
            reader.close();
        } catch (Exception e) {
            LOG.error("problem retrieving data: ", e);
            throw new BuildException("failed to read from " + database, e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (osw != null) {
                    osw.close();
                }
            } catch (Exception e) {
                throw new BuildException(e);
            }
        }

        try {
            doSQL(osw.getObjectStore());
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }
}
