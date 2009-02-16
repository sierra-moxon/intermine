package org.intermine.objectstore.intermine;

/*
 * Copyright (C) 2002-2009 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;

import org.intermine.metadata.ClassDescriptor;
import org.intermine.metadata.MetaDataException;
import org.intermine.metadata.Model;
import org.intermine.model.InterMineObject;
import org.intermine.modelproduction.MetadataManager;
import org.intermine.objectstore.DataChangedException;
import org.intermine.objectstore.ObjectStoreAbstractImpl;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.objectstore.ObjectStoreQueryDurationException;
import org.intermine.objectstore.ObjectStoreWriter;
import org.intermine.objectstore.query.BagConstraint;
import org.intermine.objectstore.query.Constraint;
import org.intermine.objectstore.query.ConstraintHelper;
import org.intermine.objectstore.query.ConstraintOp;
import org.intermine.objectstore.query.ConstraintSet;
import org.intermine.objectstore.query.ConstraintTraverseAction;
import org.intermine.objectstore.query.ObjectStoreBag;
import org.intermine.objectstore.query.OrderDescending;
import org.intermine.objectstore.query.Query;
import org.intermine.objectstore.query.QueryClass;
import org.intermine.objectstore.query.QueryCollectionPathExpression;
import org.intermine.objectstore.query.QueryField;
import org.intermine.objectstore.query.QueryNode;
import org.intermine.objectstore.query.QueryObjectPathExpression;
import org.intermine.objectstore.query.QueryObjectReference;
import org.intermine.objectstore.query.QueryOrderable;
import org.intermine.objectstore.query.QuerySelectable;
import org.intermine.objectstore.query.Results;
import org.intermine.objectstore.query.ResultsInfo;
import org.intermine.objectstore.query.ResultsRow;
import org.intermine.objectstore.query.SingletonResults;
import org.intermine.objectstore.query.iql.IqlQuery;
import org.intermine.sql.Database;
import org.intermine.sql.DatabaseFactory;
import org.intermine.sql.DatabaseUtil;
import org.intermine.sql.precompute.BestQuery;
import org.intermine.sql.precompute.BestQueryExplainer;
import org.intermine.sql.precompute.OptimiserCache;
import org.intermine.sql.precompute.PrecomputedTable;
import org.intermine.sql.precompute.PrecomputedTableManager;
import org.intermine.sql.precompute.QueryOptimiser;
import org.intermine.sql.precompute.QueryOptimiserContext;
import org.intermine.sql.query.ExplainResult;
import org.intermine.sql.query.PostgresExplainResult;
import org.intermine.sql.writebatch.Batch;
import org.intermine.sql.writebatch.BatchWriterPostgresCopyImpl;
import org.intermine.util.CacheMap;
import org.intermine.util.ShutdownHook;
import org.intermine.util.Shutdownable;
import org.intermine.util.TypeUtil;

/**
 * An SQL-backed implementation of the ObjectStore interface. The schema is oriented towards data
 * retrieval and multiple inheritance, rather than efficient data storage.
 *
 * @author Matthew Wakeling
 * @author Andrew Varley
 */
public class ObjectStoreInterMineImpl extends ObjectStoreAbstractImpl implements Shutdownable
{
    private static final Logger LOG = Logger.getLogger(ObjectStoreInterMineImpl.class);
    
    private static final Logger SQLLOGGER = Logger.getLogger("sqllogger");

    protected static final int CACHE_LARGEST_OBJECT = 5000000;
    protected static Map instances = new HashMap();
    protected Database db;
    protected Set writers = new HashSet();
    protected Writer log = null;
    protected DatabaseSchema schema;
    protected Connection logTableConnection = null;
    protected Batch logTableBatch = null;
    protected String logTableName = null;
    protected boolean logEverything = false;
    protected long statsBagTableTime = 0;
    protected long statsGenTime = 0;
    protected long statsOptTime = 0;
    protected long statsNulTime = 0;
    protected long statsEstTime = 0;
    protected long statsExeTime = 0;
    protected long statsConTime = 0;
    protected QueryOptimiserContext limitedContext;
    protected boolean verboseQueryLog = false;
    protected boolean logBeforeExecute = false;
    protected int sequenceBase = 0;
    protected int sequenceOffset = SEQUENCE_MULTIPLE;
    protected static final int SEQUENCE_MULTIPLE = 1000000;
    protected boolean logExplains = false;
    private int formatVersion = -1;
    protected boolean disableResultsCache = false;

    // don't use a table to represent bags if the bag is smaller than this value
    protected int minBagTableSize = -1;
    protected Map bagConstraintTables = Collections.synchronizedMap(new WeakHashMap());
    protected Set bagTablesInDatabase = Collections.synchronizedSet(new HashSet());
    protected Map<Query, PrecomputedTable> goFasterMap = Collections.synchronizedMap(
            new IdentityHashMap<Query, PrecomputedTable>());
    protected Map<Query, OptimiserCache> goFasterCacheMap = Collections.synchronizedMap(
            new IdentityHashMap<Query, OptimiserCache>());
    protected Map<Query, Integer> goFasterCountMap = new IdentityHashMap<Query, Integer>();
    protected ReferenceQueue bagTablesToRemove = new ReferenceQueue();
    private String description;
    protected Map<String, Results> resultsCache = new CacheMap<String, Results>();
    protected Map<String, SingletonResults> singletonResultsCache
        = new CacheMap<String, SingletonResults>();

    private static final String[] LOG_TABLE_COLUMNS = new String[] {"timestamp", "optimise",
        "estimated", "execute", "permitted", "convert", "iql", "sql"};

    /**
     * The name of the SEQUENCE in the database to use when generating unique integers in
     * getUniqueInteger().
     */
    public static final String UNIQUE_INTEGER_SEQUENCE_NAME = "objectstore_unique_integer";
    /** The name of the table that holds the integer ObjectStoreBag elements. */
    public static final String INT_BAG_TABLE_NAME = "osbag_int";
    /** The name of the bagid column in the osbag table. */
    public static final String BAGID_COLUMN = "bagid";
    /** The name of the value column in the osbag table. */
    public static final String BAGVAL_COLUMN = "value";
    /** The name of the table that stores Clobs. */
    public static final String CLOB_TABLE_NAME = "clob";
    /** The name of the clobid column in the clob table. */
    public static final String CLOBID_COLUMN = "clobid";
    /** The name of the page number column in the clob table. */
    public static final String CLOBPAGE_COLUMN = "clobpage";
    /** The name of the value column in the clob table. */
    public static final String CLOBVAL_COLUMN = "value";

    /**
     * Constructs an ObjectStoreInterMineImpl.
     *
     * @param db the database in which the model resides
     * @param model the model
     * @throws NullPointerException if db or model are null
     * @throws IllegalArgumentException if db or model are invalid
     */
    protected ObjectStoreInterMineImpl(Database db, Model model) {
        super(model);
        this.db = db;
        initFormatVersion();
        schema = new DatabaseSchema(model, Collections.EMPTY_LIST, false, Collections.EMPTY_SET);
        ShutdownHook.registerObject(new WeakReference(this));
        limitedContext = new QueryOptimiserContext();
        limitedContext.setTimeLimit(getMaxTime() / 10);
        description = "ObjectStoreInterMineImpl(" + db + ")";
    }

    /**
     * Constructs an ObjectStoreInterMineImpl, with a schema.
     *
     * @param db the database in which the model resides
     * @param schema the schema
     * @throws NullPointerException if db or model are null
     * @throws IllegalArgumentException if db or model are invalid
     */
    protected ObjectStoreInterMineImpl(Database db, DatabaseSchema schema) {
        super(schema.getModel());
        this.db = db;
        this.schema = schema;
        initFormatVersion();
        ShutdownHook.registerObject(new WeakReference(this));
        limitedContext = new QueryOptimiserContext();
        limitedContext.setTimeLimit(getMaxTime() / 10);
        description = "ObjectStoreInterMineImpl(" + db + ")";
    }

    /**
     * Initialises the format version number of the objectstore from the database. Will throw
     * an exception if the database format is newer than this code can cope with.
     */
    private void initFormatVersion() {
        if (db != null) {
            String versionString = null;
            try {
                versionString = MetadataManager.retrieve(db, MetadataManager.OS_FORMAT_VERSION);
            } catch (SQLException e) {
                LOG.error("Error retrieving database format version number", e);
            }
            if (versionString == null) {
                formatVersion = 0;
            } else {
                try {
                    formatVersion = Integer.parseInt(versionString);
                } catch (NumberFormatException e) {
                    NumberFormatException e2 = new NumberFormatException("Cannot parse database"
                            + " format version \"" + versionString + "\"");
                    e2.initCause(e);
                    throw e2;
                }
            }
            if (formatVersion > 0) {
                throw new IllegalArgumentException("Database version is too new for this code. "
                        + "Please update to a newer version of InterMine. Database version: "
                        + formatVersion + ", latest supported version: 0");
            }
        }
    }

    /**
     * Returns the DatabaseSchema used by this ObjectStore.
     *
     * @return a DatabaseSchema
     */
    public DatabaseSchema getSchema() {
        return schema;
    }

    /**
     * Returns the Database used by this ObjectStore
     *
     * @return the db
     */
    public Database getDatabase() {
        return db;
    }

    /**
     * Returns whether optimisation should be permitted. For the ObjectStore, this will always be
     * true, but for the ObjectStoreWriter, it may be false if there is written data that has not
     * been committed yet.
     *
     * @return a boolean
     */
    public boolean everOptimise() {
        return true;
    }

    /**
     * Returns a Connection. Please put them back.
     *
     * @return a java.sql.Connection
     * @throws SQLException if there is a problem with that
     */
    public Connection getConnection() throws SQLException {
        Connection retval = db.getConnection();
        if (!retval.getAutoCommit()) {
            retval.setAutoCommit(true);
        }
        return retval;
    }

    /**
     * Allows one to put a connection back.
     *
     * @param c a Connection
     */
    public void releaseConnection(Connection c) {
        if (c != null) {
            try {
                if (!c.getAutoCommit()) {
                    Exception e = new Exception();
                    e.fillInStackTrace();
                    LOG.error("releaseConnection called while in transaction - rolling back."
                              + System.getProperty("line.separator"), e);
                    c.rollback();
                    c.setAutoCommit(true);
                }
                c.close();
            } catch (SQLException e) {
                LOG.error("Could not release SQL connection " + c, e);
            }
        }
    }

    /**
     * Gets a ObjectStoreInterMineImpl instance for the given underlying properties
     *
     * @param osAlias the alias of this objectstore
     * @param props The properties used to configure a InterMine-based objectstore
     * @return the ObjectStoreInterMineImpl for this repository
     * @throws IllegalArgumentException if props or model are invalid
     * @throws ObjectStoreException if there is any problem with the instance
     */
    public static ObjectStoreInterMineImpl getInstance(String osAlias, Properties props)
        throws ObjectStoreException {
        String dbAlias = props.getProperty("db");
        if (dbAlias == null) {
            throw new ObjectStoreException("No 'db' property specified for InterMine"
                                           + " objectstore (" + osAlias + ")."
                                           + "Check properties file");
        }

        String missingTablesString = props.getProperty("missingTables");
        String logfile = props.getProperty("logfile");
        String truncatedClassesString = props.getProperty("truncatedClasses");
        String logTable = props.getProperty("logTable");
        String minBagTableSizeString = props.getProperty("minBagTableSize");
        String noNotXmlString = props.getProperty("noNotXml");
        String logEverythingString = props.getProperty("logEverything");
        String verboseQueryLogString = props.getProperty("verboseQueryLog");
        String logExplainsString = props.getProperty("logExplains");
        String logBeforeExecuteString = props.getProperty("logBeforeExecute");
        String disableResultsCacheString = props.getProperty("disableResultsCache");

        synchronized (instances) {
            ObjectStoreInterMineImpl os = (ObjectStoreInterMineImpl) instances.get(osAlias);
            if (os == null) {
                Database database;
                try {
                    database = DatabaseFactory.getDatabase(dbAlias);
                } catch (Exception e) {
                    throw new ObjectStoreException("Unable to get database for InterMine"
                            + " ObjectStore", e);
                }
                Model osModel;
                try {
                    osModel = getModelFromClasspath(osAlias, props);
                } catch (MetaDataException e) {
                    throw new ObjectStoreException("Cannot load model", e);
                }
                List truncatedClasses = new ArrayList();
                if (truncatedClassesString != null) {
                    String classes[] = truncatedClassesString.split(",");
                    for (int i = 0; i < classes.length; i++) {
                        ClassDescriptor truncatedClassDescriptor =
                            osModel.getClassDescriptorByName(classes[i]);
                        if (truncatedClassDescriptor == null) {
                            throw new ObjectStoreException("Truncated class " + classes[i]
                                                           + " does not exist in the model");
                        }
                        truncatedClasses.add(truncatedClassDescriptor);
                    }
                }
                boolean noNotXml = false;
                if ("true".equals(noNotXmlString) || (noNotXmlString == null)) {
                    noNotXml = true;
                } else if ("false".equals(noNotXmlString)) {
                    noNotXml = false;
                } else {
                    throw new ObjectStoreException("Invalid value for property noNotXml: "
                            + noNotXmlString);
                }
                HashSet missingTables = new HashSet();
                if (missingTablesString != null) {
                    String tables[] = missingTablesString.split(",");
                    for (int i = 0; i < tables.length; i++) {
                        missingTables.add(tables[i].toLowerCase());
                    }
                }
                DatabaseSchema databaseSchema = new DatabaseSchema(osModel, truncatedClasses,
                        noNotXml, missingTables);
                os = new ObjectStoreInterMineImpl(database, databaseSchema);
                os.description = osAlias;

                if (logfile != null) {
                    try {
                        FileWriter fw = new FileWriter(logfile, true);
                        BufferedWriter logWriter = new BufferedWriter(fw);
                        ShutdownHook.registerObject(logWriter);
                        os.setLog(logWriter);
                    } catch (IOException e) {
                        LOG.error("Error setting up execute log in file " + logfile + ": " + e);
                    }
                }
                if (logTable != null) {
                    try {
                        os.setLogTableName(logTable);
                    } catch (SQLException e) {
                        LOG.error("Error setting up execute log in database table " + logTable + ":"
                                + e);
                    }
                }
                if (minBagTableSizeString != null) {
                    try {
                        int minBagTableSizeInt = Integer.parseInt(minBagTableSizeString);
                        os.setMinBagTableSize(minBagTableSizeInt);
                    } catch (NumberFormatException e) {
                        LOG.error("Error setting minBagTableSize: " + e);
                    }
                }
                if ("true".equals(logEverythingString)) {
                    os.setLogEverything(true);
                }
                if ("true".equals(verboseQueryLogString)) {
                    os.setVerboseQueryLog(true);
                }
                if ("true".equals(logExplainsString)) {
                    os.setLogExplains(true);
                }
                if ("true".equals(logBeforeExecuteString)) {
                    os.setLogBeforeExecute(true);
                }
                if ("true".equals(disableResultsCacheString)) {
                    os.setDisableResultsCache(true);
                }
                instances.put(osAlias, os);
            }
            return os;
        }
    }

    /**
     * Returns the log used by this objectstore.
     *
     * @return the log
     */
    public synchronized Writer getLog() {
        return log;
    }

    /**
     * Allows the log to be set in this objectstore.
     *
     * @param log the log
     */
    public synchronized void setLog(Writer log) {
        LOG.info("Setting log to " + log);
        this.log = log;
    }

    /**
     * Allows the log table name to be set in this objectstore.
     *
     * @param tableName the table name
     * @throws SQLException if something goes wrong
     */
    public synchronized void setLogTableName(String tableName) throws SQLException {
        try {
            if (logTableName != null) {
                logTableBatch.close(logTableConnection);
                releaseConnection(logTableConnection);
                logTableConnection = null;
                logTableBatch = null;
                logTableName = null;
            }
            if (tableName != null) {
                logTableConnection = getConnection();
                if (!DatabaseUtil.tableExists(logTableConnection, tableName)) {
                    logTableConnection.createStatement().execute("CREATE TABLE " + tableName
                        + "(timestamp bigint, optimise bigint, estimated bigint, "
                        + "execute bigint, permitted bigint, convert bigint, iql text, sql text)");
                }
                logTableBatch = new Batch(new BatchWriterPostgresCopyImpl());
                logTableName = tableName;
            }
        } catch (SQLException e) {
            logTableConnection = null;
            logTableBatch = null;
            logTableName = null;
            throw e;
        }
    }

    /**
     * Sets the logEverything configuration option.
     *
     * @param logEverything a boolean
     */
    public void setLogEverything(boolean logEverything) {
        this.logEverything = logEverything;
    }

    /**
     * Gets the logEverything configuration option.
     *
     * @return a boolean
     */
    public boolean getLogEverything() {
        return logEverything;
    }

    /**
     * Sets the verboseQueryLog configuration option.
     *
     * @param verboseQueryLog a boolean
     */
    public void setVerboseQueryLog(boolean verboseQueryLog) {
        this.verboseQueryLog = verboseQueryLog;
    }

    /**
     * Gets the verboseQueryLog configuration option.
     *
     * @return a boolean
     */
    public boolean getVerboseQueryLog() {
        return verboseQueryLog;
    }

    /**
      * Sets the logExplains configuration option.
      *
      * @param logExplains a boolean
      */
    public void setLogExplains(boolean logExplains) {
        this.logExplains = logExplains;
    }

    /**
     * Gets the logExplains configuration option.
     *
     * @return a boolean
     */
    public boolean getLogExplains() {
        return logExplains;
    }

    /**
     * Sets the logBeforeExecute configuration option.
     *
     * @param logBeforeExecute a boolean
     */
    public void setLogBeforeExecute(boolean logBeforeExecute) {
        this.logBeforeExecute = logBeforeExecute;
    }

    /**
     * Gets the logBeforeExecute configuration option.
     *
     * @return a boolean
     */
    public boolean getLogBeforeExecute() {
        return logBeforeExecute;
    }

    /**
     * Sets the disableResultsCache configuration option.
     *
     * @param disableResultsCache a boolean
     */
    public void setDisableResultsCache(boolean disableResultsCache) {
        this.disableResultsCache = disableResultsCache;
    }

    /**
     * Gets the disableResultsCache configuration option.
     *
     * @return a boolean
     */
    public boolean getDisableResultsCache() {
        return disableResultsCache;
    }

    /**
     * Allows the log table to be flushed, guaranteeing that all log entries are committed to the
     * database.
     */
    public synchronized void flushLogTable() {
        if (logTableName != null) {
            try {
                logTableBatch.flush(logTableConnection);
            } catch (SQLException e) {
                LOG.error("Failed to flush log entries to log table: " + e);
            }
        }
    }

    /**
     * Produce an entry in the DB log.
     *
     * @param optimise the number of milliseconds used to optimise the query
     * @param estimated the estimated number of milliseconds required to run the query
     * @param execute the number of milliseconds spent executing the query
     * @param permitted an acceptable number of milliseconds for the query to take
     * @param convert the number of milliseconds spent converting the results
     * @param q the Query run
     * @param sql the SQL string executed
     */
    protected synchronized void dbLog(long optimise, long estimated, long execute, long permitted,
            long convert, Query q, String sql) {
        if (logTableName != null) {
            try {
                logTableBatch.addRow(logTableConnection, logTableName, null, LOG_TABLE_COLUMNS,
                        new Object[] {new Long(System.currentTimeMillis()), new Long(optimise),
                            new Long(estimated), new Long(execute),
                            new Long(permitted), new Long(convert), q.toString(), sql});
            } catch (SQLException e) {
                LOG.error("Failed to write to log table: " + e);
            }
        }
    }

    /**
     * Set the cutoff value used to decide if a bag should be put in a table.
     *
     * @param minBagTableSize don't use a table to represent bags if the bag is smaller than this
     * value
     */
    public void setMinBagTableSize(int minBagTableSize) {
        this.minBagTableSize = minBagTableSize;
    }

    /**
     * Returns the cutoff value used to decide if a bag should be put in a table.
     *
     * @return an int
     */
    public int getMinBagTableSize() {
        return minBagTableSize;
    }

    /**
     * {@inheritDoc}
     */
    public Results execute(Query q) {
        return execute(q, 0, true, true, true);
    }

    /**
     * {@inheritDoc}
     */
    public Results execute(Query q, int batchSize, boolean optimise, boolean explain,
            boolean prefetch) {
        String cacheKey = "Batchsize: " + batchSize + ", optimise: " + optimise + ", explain: "
            + explain + ", prefetch: " + prefetch + ", query: " + q;
        synchronized (resultsCache) {
            Results retval = resultsCache.get(cacheKey);
            if (retval != null) {
                try {
                    checkSequence(retval.getSequence(), null, null);
                } catch (DataChangedException e) {
                    retval = null;
                }
            }
            if (retval == null) {
                retval = super.execute(q, batchSize, optimise, explain, prefetch);
                resultsCache.put(cacheKey, retval);
                //LOG.error("Results cache miss for " + q);
            //} else {
                //LOG.error("Results cache hit for " + q);
            }
            return retval;
        }
    }

    /**
     * {@inheritDoc}
     */
    public SingletonResults executeSingleton(Query q) {
        return executeSingleton(q, 0, true, true, true);
    }

    /**
     * {@inheritDoc}
     */
    public SingletonResults executeSingleton(Query q, int batchSize, boolean optimise,
            boolean explain, boolean prefetch) {
        String cacheKey = "Batchsize: " + batchSize + ", optimise: " + optimise + ", explain: "
            + explain + ", prefetch: " + prefetch + ", query: " + q;
        synchronized (singletonResultsCache) {
            SingletonResults retval = singletonResultsCache.get(cacheKey);
            if (retval != null) {
                try {
                    checkSequence(retval.getSequence(), null, null);
                } catch (DataChangedException e) {
                    retval = null;
                }
            }
            if (retval == null) {
                retval = super.executeSingleton(q, batchSize, optimise, explain, prefetch);
                singletonResultsCache.put(cacheKey, retval);
                //LOG.error("Results cache miss for " + q);
            //} else {
                //LOG.error("Results cache hit for " + q);
            }
            return retval;
        }
    }

    /*
     * Now, we need some query cancellation mechanism. So, here is how it will work:
     * 1. A thread calls registerRequest(Object requestId), which creates an entry in a lookup
     *     that matches that request ID with that thread. Now, all activity performed by that
     *     thread is associated with that request ID. Only one thread can have a particular request
     *     ID at a time, and only one request ID can have a particular thread at a time.
     * 2. That thread performs some activity. The Statement that the thread uses is entered into
     *     a lookup against the request ID.
     * 3. Another thread calls the cancelRequest(Object requestId) method, which looks up the
     *     Statement and calls Statement.cancel(), and records the request ID in a Set.
     * 4. The requesting thread receives an SQLException, and re-throws it as an
     *     ObjectStoreException.
     * 5. If another request comes in on the same request ID, the presence of the ID in the Set
     *     causes an exception to be thrown before the database is even consulted.
     *
     * Some of these things will require synchronised actions. Notably:
     *
     * 1. The action of registering a Statement with a request ID, which should throw an exception
     *     immediately if that request has been cancelled.
     * 2. The action of registering a request ID to cancel, which should call Statement.cancel() on
     *     any Statement already registered for that request ID.
     * 3. The action of deregistering a Statement for your request ID, which will remove all
     *     records.
     *
     * We don't want to do this registration etc. every single time anything happens - only if we
     * are supplied with a request id. Therefore requests without IDs cannot be cancelled.
     */

    private ThreadLocal requestId = new ThreadLocal();

    /**
     * This method registers a Thread with a request ID.
     *
     * @param id the request ID
     * @throws ObjectStoreException if this Thread is already registered
     */
    public void registerRequestId(Object id) throws ObjectStoreException {
        if (requestId.get() != null) {
            throw new ObjectStoreException("This Thread is already registered with a request ID");
        }
        requestId.set(id);
    }

    /**
     * This method deregisters a Thread from a request ID.
     *
     * @param id the request ID
     * @throws ObjectStoreException if the Thread is not registered with this ID
     */
    public void deregisterRequestId(Object id) throws ObjectStoreException {
        if (!id.equals(requestId.get())) {
            throw new ObjectStoreException("This Thread is not registered with ID " + id);
        }
        requestId.set(null);
    }

    private WeakHashMap cancelRegistry = new WeakHashMap();
    private static final String BLACKLISTED = "Blacklisted";

    /**
     * This method registers a Statement with the current Thread's request ID, or throws an
     * exception if that request is black-listed, or does nothing if no request ID is present
     * for this Thread.
     *
     * @param s a Statement
     * @throws ObjectStoreException if the request is black-listed
     */
    protected void registerStatement(Statement s) throws ObjectStoreException {
        Object id = requestId.get();
        if (id != null) {
            synchronized (cancelRegistry) {
                Object statement = cancelRegistry.get(id);
                if (statement == BLACKLISTED) {
                    throw new ObjectStoreException("Request id " + id + " is cancelled");
                } else if (statement != null) {
                    throw new ObjectStoreException("Request id " + id + " is currently being"
                            + " serviced in another thread. Don't share request IDs over multiple"
                            + " threads!");
                }
                cancelRegistry.put(id, s);
            }
        }
    }

    /**
     * This method cancels any Statement running in a given request ID, and blacklists that ID.
     *
     * @param id the request ID
     * @throws ObjectStoreException if the cancel fails
     */
    public void cancelRequest(Object id) throws ObjectStoreException {
        synchronized (cancelRegistry) {
            try {
                Object statement = cancelRegistry.get(id);
                if (statement instanceof Statement) {
                    ((Statement) statement).cancel();
                }
            } catch (SQLException e) {
                throw new ObjectStoreException("Statement cancel failed", e);
            } finally {
                cancelRegistry.put(id, BLACKLISTED);
            }
        }
    }

    /**
     * This method deregisters a Statement for the request ID of the current thread.
     *
     * @param s a Statement
     * @throws ObjectStoreException if this Thread does not have this Statement registered
     */
    protected void deregisterStatement(Statement s) throws ObjectStoreException {
        Object id = requestId.get();
        if (id != null) {
            synchronized (cancelRegistry) {
                Object statement = cancelRegistry.get(id);
                if ((statement != BLACKLISTED) && (statement != s)) {
                    throw new ObjectStoreException("The current thread does not have this statement"
                            + " registered");
                } else if (statement == s) {
                    cancelRegistry.remove(id);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<ResultsRow> execute(Query q, int start, int limit, boolean optimise,
            boolean explain, Map<Object, Integer> sequence) throws ObjectStoreException {
        Constraint where = q.getConstraint();
        if (where instanceof ConstraintSet) {
            ConstraintSet where2 = (ConstraintSet) where;
            if (where2.getConstraints().isEmpty()
                    && (ConstraintOp.NAND.equals(where2.getOp())
                        || ConstraintOp.OR.equals(where2.getOp()))) {
                return Collections.emptyList();
            }
        }
        Connection c = null;
        try {
            c = getConnection();
            return executeWithConnection(c, q, start, limit, optimise, explain, sequence);
        } catch (SQLException e) {
            throw new ObjectStoreException("Could not get connection to database", e);
        } finally {
            releaseConnection(c);
        }
    }

    /**
     * Overrides Object.finalize - release the DB log connection.
     */
    protected synchronized void finalize() {
        LOG.error("Garbage collecting ObjectStoreInterMineImpl with sequence = " + sequenceNumber
                + " and Database " + getDatabase().getURL());
        try {
            close();
        } catch (ObjectStoreException e) {
            LOG.error("Exception while garbage-collecting ObjectStoreInterMineImpl: "
                    + e);
        }
    }

    /**
     * Closes this ObjectStore's DB log connection.
     *
     * @throws ObjectStoreException in subclasses
     */
    public synchronized void close() throws ObjectStoreException {
        LOG.info("Close called on ObjectStoreInterMineImpl with sequence = " + sequenceNumber
                + ", time spent: Bag Tables: " + statsBagTableTime + ", SQL Gen: " + statsGenTime
                + ", SQL Optimise: " + statsOptTime + ", Nulls: " + statsNulTime + ", Estimate: "
                + statsEstTime + ", Execute: " + statsExeTime + ", Results Convert: "
                + statsConTime);
        flushLogTable();
        Connection c = null;
        try {
            c = getConnection();
            LOG.info("Temporary tables to drop: " + bagTablesInDatabase);
            Iterator iter = bagTablesInDatabase.iterator();
            while (iter.hasNext()) {
                BagTableToRemove bttr = (BagTableToRemove) iter.next();
                try {
                    c.createStatement().execute(bttr.getDropSql());
                    LOG.info("Closing objectstore - dropped temporary table: " + bttr.getDropSql());
                } catch (SQLException e) {
                    LOG.error("Failed to drop temporary bag table: " + bttr.getDropSql()
                            + ", continuing");
                }
                iter.remove();
            }
            flushOldTempBagTables(c);
        } catch (SQLException e) {
            LOG.error("Failed to drop temporary bag tables: " + e);
        } finally {
            if (c != null) {
                releaseConnection(c);
            }
        }
    }

    /**
     * Called by the ShutdownHook on shutdown.
     */
    public synchronized void shutdown() {
        LOG.info("Shutting down open ObjectStoreInterMineImpl with sequence = " + sequenceNumber
                + " and Database " + getDatabase().getURL());
        try {
            close();
        } catch (ObjectStoreException e) {
            LOG.error("Exception caught while shutting down ObjectStoreInterMineImpl: "
                    + e);
        }
    }

    /**
     * Performs the actual execute, given a Connection.
     *
     * @param c the Connection
     * @param q the Query
     * @param start the start row number (inclusive, from zero)
     * @param limit maximum number of rows to return
     * @param optimise boolean
     * @param explain boolean
     * @param sequence object representing database state
     * @return a List of ResultRow objects
     * @throws ObjectStoreException sometimes
     */
    protected List<ResultsRow> executeWithConnection(Connection c, Query q, int start, int limit,
            boolean optimise, boolean explain, Map<Object, Integer> sequence)
    throws ObjectStoreException {

        if (explain) {
            checkStartLimit(start, limit, q);
        }
        checkSequence(sequence, q, "Execute (START " + start + " LIMIT " + limit + ") ");

        long preBagTableTime = System.currentTimeMillis();
        if (getMinBagTableSize() != -1) {
            createTempBagTables(c, q);
            flushOldTempBagTables(c);
        }
        long preGenTime = System.currentTimeMillis();
        String sql;
        try {
            sql = SqlGenerator.generate(q, start, limit, schema, db, bagConstraintTables);
        } catch (CompletelyFalseException e) {
            return Collections.emptyList();
        }
        String generatedSql = sql;
        try {
            long estimatedTime = 0;
            long startOptimiseTime = System.currentTimeMillis();
            ExplainResult explainResult = null;
            if (optimise && everOptimise()) {
                PrecomputedTable pt = goFasterMap.get(q);
                BestQuery bestQuery;
                if (pt != null) {
                    OptimiserCache oCache = goFasterCacheMap.get(q);
                    bestQuery = QueryOptimiser.optimiseWith(sql, null, db, c,
                            QueryOptimiserContext.DEFAULT, Collections.singleton(pt), oCache);
                    if (sql.equals(bestQuery.getBestQueryString())) {
                        LOG.warn("Query with goFaster failed to optimise: original = "
                                + sql + ", pt = " + pt.getSQLString());
                    }
                } else {
                    bestQuery = QueryOptimiser.optimise(sql, null, db, c,
                            (explain ? limitedContext : QueryOptimiserContext.DEFAULT));
                }
                sql = bestQuery.getBestQueryString();
                if (bestQuery instanceof BestQueryExplainer) {
                    explainResult = ((BestQueryExplainer) bestQuery).getBestExplainResult();
                }
            }
            long endOptimiseTime = System.currentTimeMillis();
            sql = sql.replaceAll(" ([^ )]*) IS NULL", " ($1 IS NULL) = true");
            sql = sql.replaceAll(" ([^ )]*) IS NOT NULL", " ($1 IS NOT NULL) = true");
            long postNullStuff = System.currentTimeMillis();
            if (explain) {
                //System//.out.println(getModel().getName() + ": Executing SQL: EXPLAIN " + sql);
                //long time = (new Date()).getTime();
                if (explainResult == null) {
                    explainResult = ExplainResult.getInstance(sql, c);
                }
                //long now = (new Date()).getTime();
                //if (now - time > 10) {
                //    LOG.debug(getModel().getName() + ": Executed SQL (time = "
                //            + (now - time) + "): EXPLAIN " + sql);
                //}

                //System .out.println("Explain result for " + sql + "\n"
                //        + ((PostgresExplainResult) explainResult).getExplainText());

                estimatedTime = explainResult.getTime();
                if (explainResult.getTime() > getMaxTime()) {
                    throw (new ObjectStoreQueryDurationException("Estimated time to run query("
                                + explainResult.getTime() + ") greater than permitted maximum ("
                                + getMaxTime() + "): IQL query: " + q + ", SQL query: " + sql));
                }
            }
            if (getLogBeforeExecute()) {
                SQLLOGGER.info("(BEFORE EXECUTE) iql: " + q + "\n"
                        + "generated sql: " + generatedSql + "\n"
                        + "optimised sql: " + sql);
            }
            long preExecute = System.currentTimeMillis();
            Statement s = c.createStatement();
            registerStatement(s);
            ResultSet sqlResults;
            try {
                sqlResults = s.executeQuery(sql);
            } finally {
                deregisterStatement(s);
            }
            long postExecute = System.currentTimeMillis();
            List<ResultsRow>  objResults = ResultsConverter.convert(sqlResults, q, this, c,
                    sequence, optimise);
            long postConvert = System.currentTimeMillis();
            long permittedTime = (objResults.size() * 2) + start + (150 * q.getFrom().size())
                    + (sql.length() / 20) - (q.getFrom().size() == 0 ? 0 : 100);
            boolean doneExplainLog = false;
            if (postExecute - preExecute > permittedTime) {
                LOG.debug(getModel().getName() + ": Executed SQL (time = "
                        + (postExecute - preExecute) + " > " + permittedTime + ", rows = "
                        + objResults.size() + "): " + sql);
                if (getLogExplains()) {
                    doneExplainLog = true;
                    if (explainResult == null) {
                        explainResult = ExplainResult.getInstance(sql, c);
                    }
                    if (explainResult instanceof PostgresExplainResult) {
                        LOG.debug("EXPLAIN result: " + ((PostgresExplainResult) explainResult)
                                .getExplainText());
                    }
                }
            }
            if ((estimatedTime > 0) || getLogEverything()) {
                Writer executeLog = getLog();
                if (executeLog != null) {
                    try {
                        executeLog.write("EXECUTE\toptimise: " + (endOptimiseTime
                                    - startOptimiseTime) + "\testimated: " + estimatedTime
                                + "\texecute: " + (postExecute - preExecute) + "\tpermitted: "
                                + permittedTime + "\tconvert: " + (postConvert - postExecute) + "\t"
                                + q + "\t" + sql + "\n");
                    } catch (IOException e) {
                        LOG.error("Error writing to execute log " + e);
                    }
                }
                dbLog(endOptimiseTime - startOptimiseTime, estimatedTime, postExecute - preExecute,
                        permittedTime, postConvert - postExecute, q, sql);
            }
            long bagTableTime = preGenTime - preBagTableTime;
            statsBagTableTime += bagTableTime;
            long genTime = startOptimiseTime - preGenTime;
            statsGenTime += genTime;
            long optTime = endOptimiseTime - startOptimiseTime;
            statsOptTime += optTime;
            long nulTime = postNullStuff - endOptimiseTime;
            statsNulTime += nulTime;
            long estTime = preExecute - postNullStuff;
            statsEstTime += estTime;
            long exeTime = postExecute - preExecute;
            statsExeTime += exeTime;
            long conTime = postConvert - postExecute;
            statsConTime += conTime;
            if (getVerboseQueryLog()) {
                SQLLOGGER.info("(VERBOSE) iql: " + q + "\n"
                        + "generated sql: " + generatedSql + "\n"
                        + "optimised sql: " + sql + "\n"
                        + "bag tables: " + bagTableTime + " ms, generate: " + genTime
                        + " ms, optimise: " + optTime + " ms, " + "replace nulls: " + nulTime
                        + " ms,  estimate: " + estTime + " ms, " + "execute: " + exeTime
                        + " ms, convert results: " + conTime + " ms, total: "
                        + (postConvert - preBagTableTime) + " ms" + ", rows: "
                        + objResults.size());
                if (getLogExplains() && (!doneExplainLog)) {
                    if (explainResult == null) {
                        explainResult = ExplainResult.getInstance(sql, c);
                    }
                    if (explainResult instanceof PostgresExplainResult) {
                        SQLLOGGER.info("EXPLAIN result: " + ((PostgresExplainResult) explainResult)
                                .getExplainText());
                    }
                }
            }
            Object firstOrderByObject = q.getEffectiveOrderBy().iterator().next();
            if ((firstOrderByObject instanceof QueryOrderable)
                    && (!(firstOrderByObject instanceof QueryObjectReference))) {
                QueryOrderable firstOrderBy = (QueryOrderable) firstOrderByObject;
                if (firstOrderBy instanceof OrderDescending) {
                    firstOrderBy = ((OrderDescending) firstOrderBy).getQueryOrderable();
                }
                if (q.getSelect().contains(firstOrderBy) && (objResults.size() > 1)) {
                    int colNo = q.getSelect().indexOf(firstOrderBy);
                    int rowNo = objResults.size() - 1;
                    Object lastObj = ((List) objResults.get(rowNo)).get(colNo);
                    rowNo--;
                    boolean done = false;
                    while ((!done) && (rowNo >= 0)) {
                        Object thisObj = ((List) objResults.get(rowNo)).get(colNo);
                        if ((lastObj != null) && (thisObj != null) && !lastObj.equals(thisObj)) {
                            done = true;
                            Object value = (thisObj instanceof InterMineObject
                                            ? ((InterMineObject) thisObj).getId() : thisObj);
                            SqlGenerator.registerOffset(q, start + rowNo + 1, schema, db,
                                                        value, bagConstraintTables);
                        }
                        rowNo--;
                    }
                }
            }
            return objResults;
        } catch (SQLException e) {
            throw new ObjectStoreException("Problem running SQL statement \"" + sql
                    + "\" while executing query \"" + q + "\"", e);
        } catch (RuntimeException e) {
            throw new ObjectStoreException("Problem executing query \"" + q + "\"", e);
        }
    }

    /**
     * Generate sql from a Query
     *
     * @param q the Query
     * @return an SQL String
     * @throws ObjectStoreException if something goes wrong
     */
    public String generateSql(Query q) throws ObjectStoreException {
        Connection c = null;
        try {
            c = getConnection();
            return generateSql(c, q, 0, Integer.MAX_VALUE);
        } catch (SQLException e) {
            throw new ObjectStoreException("Failed to get connection", e);
        } finally {
            releaseConnection(c);
        }
    }

    /**
     * Create temporary tables for the bag in the BagConstraints of the given Query, then call
     * SqlGenerator.generate().  Entries are placed in the bagConstraintTables Map, which is a
     * WeakHashMap from BagConstraint -&gt; table name. When the BagConstraint is garbage-
     * collected, or when the JVM exits, the table associated with the table name is dropped from
     * the database.
     *
     * @param c a Connection to use
     * @param q the Query
     * @param start the start row number (inclusive, from zero)
     * @param limit maximum number of rows to return
     * @return the SQL for the Query
     * @throws ObjectStoreException if an error occurs
     */
    protected String generateSql(Connection c, Query q, int start, int limit)
        throws ObjectStoreException {

        if (getMinBagTableSize() != -1) {
            // We have a strong reference to the Query, and therefore all the BagConstraints. We can
            // count on the bagConstraintTables Map to be sane.

            createTempBagTables(c, q);
            flushOldTempBagTables(c);
        }

        return SqlGenerator.generate(q, start, limit, schema, db, bagConstraintTables);
    }

    /**
     * Create temporary tables for use with Query that use bags.  Each BagConstraint in the Query is
     * examined and a temporary table containing values of the appropriate type from the bag is
     * created. The new table names will be the values of the bagConstraintTables Map and the
     * BagConstraint references will be the keys.
     *
     * @param c a Connection to use
     * @param q the Query
     * @throws ObjectStoreException if there is a error in the ObjectStore
     */
    protected void createTempBagTables(Connection c, Query q)
        throws ObjectStoreException {

        final List bagConstraints = new ArrayList();

        ConstraintHelper.traverseConstraints(q.getConstraint(), new ConstraintTraverseAction() {
            public void apply(Constraint constraint) {
                if (constraint instanceof BagConstraint) {
                    BagConstraint bagConstraint = (BagConstraint) constraint;
                    if (bagConstraint.getBag() != null) {
                        bagConstraints.add(bagConstraint);
                    }
                }
            }
        });

        Iterator bagConstraintIterator = bagConstraints.iterator();

        boolean wasNotInTransaction = false;

        try {
            wasNotInTransaction = c.getAutoCommit();
            if (wasNotInTransaction) {
                c.setAutoCommit(false);
            }

            while (bagConstraintIterator.hasNext()) {
                BagConstraint bagConstraint = (BagConstraint) bagConstraintIterator.next();
                if (!bagConstraintTables.containsKey(bagConstraint)) {
                    Collection bag = bagConstraint.getBag();

                    if (bag.size() >= getMinBagTableSize()) {
                        createTempBagTable(c, bagConstraint, true,
                                new IqlQuery(q).getQueryString());
                    }
                }
            }
            if (wasNotInTransaction) {
                c.commit();
            }
        } catch (SQLException e) {
            throw new ObjectStoreException("database error while creating temporary "
                                           + "table for bag", e);
        } finally {
            try {
                if (wasNotInTransaction) {
                    c.setAutoCommit(true);
                }
            } catch (SQLException e) {
                throw new ObjectStoreException("database error while creating temporary "
                                               + "table for bag", e);
            }
        }
    }

    /**
     * Creates a temporary bag table for the given BagConstraint.
     *
     * @param bagConstraint a BagConstraint
     * @throws ObjectStoreException if an error occurs
     */
    public void createTempBagTable(BagConstraint bagConstraint) throws ObjectStoreException {
        Connection c = null;
        try {
            c = getConnection();
            createTempBagTable(c, bagConstraint, false, null);
        } catch (SQLException e) {
            throw new ObjectStoreException("Could not get connection to database", e);
        } finally {
            releaseConnection(c);
        }
    }

    /**
     * Creates a temporary bag table for the given BagConstraint.
     *
     * @param c a Connection
     * @param bagConstraint a BagConstraint
     * @param log true to log this action
     * @param text extra data to place in the log
     * @return a BagTableToRemove object
     * @throws SQLException if an error occurs
     */
    protected BagTableToRemove createTempBagTable(Connection c, BagConstraint bagConstraint,
            boolean log, String text) throws SQLException {
        Class type = bagConstraint.getQueryNode().getType();
        String tableName =
            TypeUtil.unqualifiedName(type.getName()) + "_bag_" + getUniqueInteger(c);
        if (log) {
            LOG.info("Creating temporary table " + tableName + " of size "
                    + bagConstraint.getBag().size() + " for " + text);
        }
        Exception e = new Exception();
        e.fillInStackTrace();
        DatabaseUtil.createBagTable(db, c, tableName, bagConstraint.getBag(), type);
        bagConstraintTables.put(bagConstraint, tableName);
        BagTableToRemove bagTableToRemove = new BagTableToRemove(tableName,
                bagTablesToRemove);
        bagTablesInDatabase.add(bagTableToRemove);
        return bagTableToRemove;
    }

    /**
     * Removes any temporary bag tables that are no longer reachable.
     *
     * @param c the Connection to use
     */
    public synchronized void flushOldTempBagTables(Connection c) {
        BagTableToRemove bttr = (BagTableToRemove) bagTablesToRemove.poll();
        while (bttr != null) {
            if (bagTablesInDatabase.contains(bttr)) {
                removeTempBagTable(c, bttr);
                LOG.info("Dropped unreachable temporary table: " + bttr.getDropSql());
            }
            bttr = (BagTableToRemove) bagTablesToRemove.poll();
        }
    }

    /**
     * Removes a temporary bag table, given a BagTableToRemove object.
     *
     * @param c the Connection to use
     * @param bttr the BagTableToRemove object
     */
    protected synchronized void removeTempBagTable(Connection c, BagTableToRemove bttr) {
        if (bagTablesInDatabase.contains(bttr)) {
            try {
                c.createStatement().execute(bttr.getDropSql());
            } catch (SQLException e) {
                LOG.error("Failed to drop temporary bag table: " + bttr.getDropSql()
                        + ", continuing");
            }
            bagTablesInDatabase.remove(bttr);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ResultsInfo estimate(Query q) throws ObjectStoreException {
        Connection c = null;
        try {
            c = getConnection();
            return estimateWithConnection(c, q);
        } catch (SQLException e) {
            throw new ObjectStoreException("Could not get connection to database", e);
        } finally {
            releaseConnection(c);
        }
    }

    /**
     * Runs an EXPLAIN for the given query.
     *
     * @param c the Connection
     * @param q the Query to explain
     * @return parsed results of EXPLAIN
     * @throws ObjectStoreException if an error occurs explaining the query
     */
    protected ResultsInfo estimateWithConnection(Connection c,
            Query q) throws ObjectStoreException {
        String sql;
        try {
            sql = generateSql(c, q, 0, Integer.MAX_VALUE);
        } catch (CompletelyFalseException e) {
            return new ResultsInfo(0, 0, 0, 0, 0);
        }
        try {
            if (everOptimise()) {
                sql = QueryOptimiser.optimise(sql, db);
            }
            //long time = (new Date()).getTime();
            ExplainResult explain = ExplainResult.getInstance(sql, c);
            //long now = (new Date()).getTime();
            //if (now - time > 10) {
            //    LOG.debug(getModel().getName() + ": Executed SQL (time = "
            //            + (now - time) + "): EXPLAIN " + sql);
            //}
            return new ResultsInfo(explain.getStart(), explain.getComplete(),
                    (int) explain.getEstimatedRows());
        } catch (SQLException e) {
            throw new ObjectStoreException("Problem explaining SQL statement \"" + sql + "\"", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public int count(Query q, Map<Object, Integer> sequence) throws ObjectStoreException {
        Connection c = null;
        try {
            c = getConnection();
            return countWithConnection(c, q, sequence);
        } catch (SQLException e) {
            throw new ObjectStoreException("Could not get connection to database", e);
        } finally {
            releaseConnection(c);
        }
    }

    /**
     * Counts the results in a query, given a Connection.
     *
     * @param c the Connection
     * @param q the Query
     * @param sequence int
     * @return an int
     * @throws ObjectStoreException sometimes
     */
    protected int countWithConnection(Connection c, Query q,
            Map<Object, Integer> sequence) throws ObjectStoreException {
        checkSequence(sequence, q, "COUNT ");

        String sql = null;
        try {
            sql = generateSql(c, q, 0, Integer.MAX_VALUE);
            if (everOptimise()) {
                sql = QueryOptimiser.optimise(sql, db);
            }
            sql = "SELECT COUNT(*) FROM (" + sql + ") as fake_table";
            //long time = (new Date()).getTime();
            ResultSet sqlResults;
            Statement s = c.createStatement();
            registerStatement(s);
            try {
                sqlResults = s.executeQuery(sql);
            } finally {
                deregisterStatement(s);
            }
            //long now = (new Date()).getTime();
            //if (now - time > 10) {
            //    LOG.debug(getModel().getName() + ": Executed SQL (time = "
            //            + (now - time) + "): " + sql);
            //}
            sqlResults.next();
            return sqlResults.getInt(1);
        } catch (CompletelyFalseException e) {
            return 0;
        } catch (SQLException e) {
            throw new ObjectStoreException("Problem counting SQL statement \"" + sql + "\"", e);
        }
    }

    /**
     * Internal method called by the ObjectStoreWriter, to notify the ObjectStore that some of the
     * data in the database has changed.
     *
     * @param tablesAltered a Set of table names that may have been altered
     */
    public void databaseAltered(Set tablesAltered) {
        if (tablesAltered.size() > 0) {
            changeSequence(tablesAltered);
            for (Iterator iter = tablesAltered.iterator(); iter.hasNext();) {
                Object o = iter.next();
                if (o instanceof ObjectStoreBag) {
                    iter.remove();
                }
            }
            // We have just removed the ObjectStoreBags from the Set of altered things. This means
            // that although the DataChangedException stuff is ObjectStoreBag-specific, the dropping
            // precomputed tables bit is not. Changing any ObjectStoreBag will result in all
            // Precomputed tables that have an ObjectStoreBag being dropped.
            if ((tablesAltered.size() > 1) || (!tablesAltered.contains(INT_BAG_TABLE_NAME))) {
                flushObjectById();
            }
            try {
                PrecomputedTableManager ptm = PrecomputedTableManager.getInstance(db);
                ptm.dropAffected(tablesAltered);
            } catch (SQLException e) {
                throw new Error("Problem with precomputed tables", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void flushObjectById() {
        super.flushObjectById();
        Iterator writerIter = writers.iterator();
        while (writerIter.hasNext()) {
            ObjectStoreWriter writer = (ObjectStoreWriter) writerIter.next();
            if (writer != this) {
                writer.flushObjectById();
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * This method is overridden in order to improve the performance of the operation - this
     * implementation does not bother with the EXPLAIN call to the underlying SQL database.
     */
    protected InterMineObject internalGetObjectById(Integer id,
            Class clazz) throws ObjectStoreException {
        if (schema.isFlatMode(clazz)) {
            return super.internalGetObjectById(id, clazz);
        }
        Connection c = null;
        try {
            c = getConnection();
            return internalGetObjectByIdWithConnection(c, id, clazz);
        } catch (SQLException e) {
            throw new ObjectStoreException("Could not get connection to database", e);
        } finally {
            releaseConnection(c);
        }
    }

    /**
     * Gets an object by id given a Connection.
     *
     * @param c the Connection
     * @param id the id
     * @param clazz a Class of the object
     * @return the object
     * @throws ObjectStoreException if an error occurs
     */
    protected InterMineObject internalGetObjectByIdWithConnection(Connection c,
            Integer id, Class clazz) throws ObjectStoreException {
        String sql = SqlGenerator.generateQueryForId(id, clazz, schema);
        String currentColumn = null;
        try {
            //System//.out.println(getModel().getName() + ": Executing SQL: " + sql);
            //long time = (new Date()).getTime();
            ResultSet sqlResults;
            Statement s = c.createStatement();
            registerStatement(s);
            try {
                sqlResults = s.executeQuery(sql);
            } finally {
                deregisterStatement(s);
            }
            //long now = (new Date()).getTime();
            //if (now - time > 10) {
            //    System//.out.println(getModel().getName() + ": Executed SQL (time = "
            //            + (now - time) + "): " + sql);
            //}
            if (sqlResults.next()) {
                currentColumn = sqlResults.getString("a1_");
                if (sqlResults.next()) {
                    throw new ObjectStoreException("More than one object in the database has this"
                            + " primary key");
                }
                InterMineObject retval = NotXmlParser.parse(currentColumn, this);
                //if (currentColumn.length() < CACHE_LARGEST_OBJECT) {
                    cacheObjectById(retval.getId(), retval);
                //} else {
                //    LOG.debug("Not cacheing large object " + retval.getId() + " on getObjectById"
                //            + " (size = " + (currentColumn.length() / 512) + " kB)");
                //}
                return retval;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new ObjectStoreException("Problem running SQL statement \"" + sql + "\"", e);
        } catch (ClassNotFoundException e) {
            throw new ObjectStoreException("Unknown class mentioned in database OBJECT field"
                    + " while converting results: " + currentColumn, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMultiConnection() {
        return true;
    }

    /**
     * Creates precomputed tables for the given query.
     *
     * @param q the Query for which to create the precomputed tables
     * @param category a String describing the category of the precomputed tables
     * @return the names of the new precomputed tables
     * @throws ObjectStoreException if anything goes wrong
     */
    public List<String> precompute(Query q, String category) throws ObjectStoreException {
        return precompute(q, null, false, category);
    }

    /**
     * Creates precomputed tables for the given query.
     *
     * @param q the Query for which to create the precomputed tables
     * @param allFields true if all fields of QueryClasses in the SELECT list should be included in
     * the precomputed table's SELECT list.
     * @param category a String describing the category of the precomputed tables
     * @return the names of the new precomputed tables
     * @throws ObjectStoreException if anything goes wrong
     */
    public List<String> precompute(Query q, boolean allFields,
            String category) throws ObjectStoreException {
        return precompute(q, null, allFields, category);
    }

    /**
     * Creates precomputed tables for the given query.
     *
     * @param q the Query for which to create the precomputed tables
     * @param indexes a Collection of QueryOrderables for which to create indexes
     * @return the names of the new precomputed tables
     * @param category a String describing the category of the precomputed tables
     * @throws ObjectStoreException if anything goes wrong
     */
    public List<String> precompute(Query q, Collection indexes,
            String category) throws ObjectStoreException {
        return precompute(q, indexes, false, category);
    }

    /**
     * Creates precomputed tables for the given query.
     *
     * @param q the Query for which to create the precomputed tables
     * @param indexes a Collection of QueryOrderables for which to create indexes
     * @param allFields true if all fields of QueryClasses in the SELECT list should be included in
     * the precomputed table's SELECT list.
     * @param category a String describing the category of the precomputed tables
     * @return the names of the new precomputed tables
     * @throws ObjectStoreException if anything goes wrong
     */
    public List<String> precompute(Query q, Collection indexes,
            boolean allFields, String category) throws ObjectStoreException {
        Connection c = null;
        try {
            c = getConnection();
            return precomputeWithConnection(c, q, indexes, allFields, category);
        } catch (SQLException e) {
            throw new ObjectStoreException("Could not get connection to database", e);
        } finally {
            releaseConnection(c);
        }
    }

    /**
     * Creates precomputed tables with the given query and connection.
     *
     * @param c the Connection
     * @param q the Query
     * @param indexes a Collection of QueryNodes for which to create indexes - they must all exist
     * in the SELECT list of the query
     * @param allFields true if all fields of QueryClasses in the SELECT list should be included in
     * the precomputed table's SELECT list.
     * @param category a String describing the category of the precomputed tables
     * @return the names of the new precomputed tables
     * @throws ObjectStoreException if anything goes wrong
     */
    public List<String> precomputeWithConnection(Connection c, Query q, Collection indexes,
            boolean allFields, String category) throws ObjectStoreException {
        QueryNode qn = null;
        String sql = null;
        try {
            int tableNumber = getUniqueInteger(c);
            if (getMinBagTableSize() != -1) {
                createTempBagTables(c, q);
                flushOldTempBagTables(c);
            }
            if (allFields) {
                sql = SqlGenerator.generate(q, schema, db, null, SqlGenerator.QUERY_FOR_PRECOMP,
                        Collections.EMPTY_MAP);
            } else {
                sql = SqlGenerator.generate(q, schema, db, null, SqlGenerator.QUERY_FOR_GOFASTER,
                        Collections.EMPTY_MAP);
            }
            PrecomputedTable pt = new PrecomputedTable(new org.intermine.sql.query.Query(sql),
                    sql, "precomputed_table_" + tableNumber, category, c);
            Set stringIndexes = new HashSet();
            if (indexes != null && !indexes.isEmpty()) {
                Map aliases = q.getAliases();
                stringIndexes = new HashSet();
                String all = null;
                Iterator indexIter = indexes.iterator();
                try {
                    while (indexIter.hasNext()) {
                        qn = (QueryNode) indexIter.next();
                        String alias = DatabaseUtil.generateSqlCompatibleName((String) aliases
                                .get(qn));
                        if (qn instanceof QueryClass) {
                            alias += "id";
                        } else if (qn instanceof QueryField) {
                            if (String.class.equals(((QueryField) qn).getType())) {
                                alias = "lower(" + alias + ")";
                            }
                        }
                        if (all == null) {
                            all = alias;
                        } else {
                            stringIndexes.add(alias);
                            all += ", " + alias;
                        }
                    }
                } catch (NullPointerException e) {
                    throw new ObjectStoreException("QueryNode " + qn + " (to be indexed) is not"
                            + " present in the SELECT list of query " + q, e);
                }
                stringIndexes.add(all);
                LOG.info("Creating precomputed table for query " + q + " with indexes "
                        + stringIndexes);
            }
            PrecomputedTableManager ptm = PrecomputedTableManager.getInstance(db);
            List<String> retval = new ArrayList<String>();
            try {
                ptm.add(pt, stringIndexes);
                retval.add(pt.getName());
            } catch (IllegalArgumentException e) {
                LOG.info("Precomputed table for " + sql + " already exists");
            }
            for (QuerySelectable qs : q.getSelect()) {
                if (qs instanceof QueryCollectionPathExpression) {
                    Query subQ = ((QueryCollectionPathExpression) qs).getQuery(null);
                    retval.addAll(precomputeWithConnection(c, subQ,
                                Collections.singleton(subQ.getSelect().get(0)),
                                allFields, category));
                } else if (qs instanceof QueryObjectPathExpression) {
                    Query subQ = ((QueryObjectPathExpression) qs).getQuery(null,
                            getSchema().isMissingNotXml());
                    retval.addAll(precomputeWithConnection(c, subQ,
                                Collections.singleton(subQ.getSelect().get(0)),
                                allFields, category));
                }
            }
            return retval;
        } catch (SQLException e) {
            throw new ObjectStoreException(e);
        } catch (RuntimeException e) {
            LOG.error("Error", e);
            throw new ObjectStoreException("Query SQL cannot be parsed, so cannot be precomputed: "
                    + sql + ", IQL: " + q, e);
        }
    }

    /**
     * Checks if a query is precomputed or not for the given type
     *
     * @param query the query
     * @param type the type
     * @return true if and only if the given query is (already) precomputed
     * @throws ObjectStoreException if the is a database problem
     */
    public boolean isPrecomputed(Query query, String type) throws ObjectStoreException {
        Connection c = null;
        try {
            c = getConnection();
            return isPrecomputedWithConnection(c, query, type);
        } catch (SQLException e) {
            throw new ObjectStoreException("Could not get connection to database", e);
        } finally {
            releaseConnection(c);
        }
    }

    /**
     * Checks if a query is precomputed or not for the given type and connection
     *
     * @param c the connection
     * @param query the query
     * @param type the type
     * @return true if and only if the given query is (already) precomputed
     * @throws ObjectStoreException if there is a database problem
     * @throws SQLException if there is a database problem
     */
    public boolean isPrecomputedWithConnection(Connection c, Query query, String type)
            throws ObjectStoreException, SQLException {
        PrecomputedTableManager ptm = PrecomputedTableManager.getInstance(db);
        String sqlQuery = generateSql(c, query, 0, Integer.MAX_VALUE);
        return (ptm.lookupSql(type, sqlQuery) != null);
    }

    /**
     * Makes a certain Query go faster, using extra resources. The user should release
     * the resources later by calling releaseGoFaster on the same Query. Failure to release
     * resources may result in an overall degradation in performance.
     *
     * @param q the Query to speed up
     * @throws ObjectStoreException if something is wrong
     */
    public void goFaster(Query q) throws ObjectStoreException {
        Connection c = null;
        try {
            c = getConnection();
            goFasterWithConnection(q, c);
        } catch (SQLException e) {
            throw new ObjectStoreException("Could not get connection to database", e);
        } finally {
            releaseConnection(c);
        }
    }

    /**
     * Makes a certain Query go faster, using extra resources. The user should release
     * the resources later by calling releaseGoFaster on the same Query. Failure to release
     * resources may result in an overall degradation in performance.
     *
     * @param q the Query to speed up
     * @param c the Connection to use
     * @throws ObjectStoreException if something is wrong
     */
    public void goFasterWithConnection(Query q, Connection c) throws ObjectStoreException {
        synchronized (goFasterMap) {
            if (goFasterMap.containsKey(q)) {
                int goFasterCount = goFasterCountMap.get(q).intValue();
                goFasterCount++;
                goFasterCountMap.put(q, new Integer(goFasterCount));
            }
            try {
                if (getMinBagTableSize() != -1) {
                    createTempBagTables(c, q);
                    flushOldTempBagTables(c);
                }
                String sql = SqlGenerator.generate(q, schema, db, null,
                        SqlGenerator.QUERY_FOR_GOFASTER, Collections.EMPTY_MAP);
                PrecomputedTable pt = new PrecomputedTable(new org.intermine.sql.query.Query(sql),
                        sql, "temporary_precomp_" + getUniqueInteger(c), "goFaster", c);
                PrecomputedTableManager ptm = PrecomputedTableManager.getInstance(db);
                ptm.addTableToDatabase(pt, new HashSet(), false);
                goFasterMap.put(q, pt);
                goFasterCacheMap.put(q, new OptimiserCache());
                goFasterCountMap.put(q, new Integer(1));
            } catch (SQLException e) {
                throw new ObjectStoreException(e);
            }
        }
    }

    /**
     * Releases the resources used by goFaster().
     *
     * @param q the Query for which to release resources
     * @throws ObjectStoreException if something goes wrong
     */
    public void releaseGoFaster(Query q) throws ObjectStoreException {
        try {
            synchronized (goFasterMap) {
                if (goFasterMap.containsKey(q)) {
                    int goFasterCount = goFasterCountMap.get(q).intValue();
                    goFasterCount--;
                    if (goFasterCount == 0) {
                        PrecomputedTable pt = goFasterMap.remove(q);
                        goFasterCacheMap.remove(q);
                        goFasterCountMap.remove(q);
                        if (pt != null) {
                            PrecomputedTableManager ptm = PrecomputedTableManager.getInstance(db);
                            ptm.deleteTableFromDatabase(pt.getName());
                        }
                    } else {
                        goFasterCountMap.put(q, new Integer(goFasterCount));
                    }
                }
            }
        } catch (SQLException e) {
            throw new ObjectStoreException(e);
        }
    }

    /**
     * Return a unique integer from a SEQUENCE in the database.
     *
     * @param c a Connection to use
     * @return an integer that is unique in this database
     * @throws SQLException if something goes wrong
     */
    public int getUniqueInteger(Connection c) throws SQLException {
        Statement s = c.createStatement();
        ResultSet r = s.executeQuery("SELECT nextval('" + UNIQUE_INTEGER_SEQUENCE_NAME + "')");
        if (!r.next()) {
            throw new RuntimeException("No result while attempting to get a unique"
                                       + " integer from " + UNIQUE_INTEGER_SEQUENCE_NAME);
        }
        return r.getInt(1);
    }

    /**
     * Class describing a temporary bag table, which can be removed. A bag table can be forcibly
     * dropped by passing one of these objects to the removeTempBagTable method. Alternatively,
     * the table will be automatically dropped after the table name is garbage collected.
     *
     * @author Matthew Wakeling
     */
    protected class BagTableToRemove extends WeakReference
    {
        String dropSql;

        private BagTableToRemove(String tableName, ReferenceQueue refQueue) {
            super(tableName, refQueue);
            dropSql = "DROP TABLE " + tableName;
        }

        private String getDropSql() {
            return dropSql;
        }

        /**
         * Returns the SQL statement that will drop the table.
         *
         * @return a String
         */
        public String toString() {
            return dropSql;
        }
    }

    /**
     * Gets an ID number which is unique in the database.
     *
     * @return an Integer
     * @throws ObjectStoreException if a problem occurs
     */
    public Integer getSerial() throws ObjectStoreException {
        try {
            Connection c = null;
            try {
                c = getConnection();
                return getSerialWithConnection(c);
            } finally {
                releaseConnection(c);
            }
        } catch (SQLException e) {
            throw new ObjectStoreException("Error generating serial number", e);
        }
    }

    /**
     * Gets an ID number which is unique in the database, given a Connection.
     *
     * @param c the Connection
     * @return an Integer
     * @throws SQLException if a problem occurs
     */
    protected Integer getSerialWithConnection(Connection c) throws SQLException {
        if (sequenceOffset >= SEQUENCE_MULTIPLE) {
            long start = System.currentTimeMillis();
            sequenceOffset = 0;
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery("SELECT nextval('serial');");
            //System//.out.println(getModel().getName()
            //        + ": Executed SQL: SELECT nextval('serial');");
            if (!r.next()) {
                throw new SQLException("No result while attempting to get a unique id");
            }
            long nextSequence = r.getLong(1);
            sequenceBase = (int) (nextSequence * SEQUENCE_MULTIPLE);
            long end = System.currentTimeMillis();
            LOG.info("Got new set of serial numbers - took " + (end - start) + " ms");
        }
        return new Integer(sequenceBase + (sequenceOffset++));
    }

    /**
     * {@inheritDoc}
     */
    public Set<Object> getComponentsForQuery(Query q) {
        try {
            Set<Object> retval = SqlGenerator.findTableNames(q, getSchema(), true);
            return retval;
        } catch (ObjectStoreException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return description;
    }
}
