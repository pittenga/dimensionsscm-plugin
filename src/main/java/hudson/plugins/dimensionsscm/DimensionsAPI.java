/*
 * ===========================================================================
 *  Copyright (c) 2007, 2014 Serena Software. All rights reserved.
 *
 *  Use of the Sample Code provided by Serena is governed by the following
 *  terms and conditions. By using the Sample Code, you agree to be bound by
 *  the terms contained herein. If you do not agree to the terms herein, do
 *  not install, copy, or use the Sample Code.
 *
 *  1.  GRANT OF LICENSE.  Subject to the terms and conditions herein, you
 *  shall have the nonexclusive, nontransferable right to use the Sample Code
 *  for the sole purpose of developing applications for use solely with the
 *  Serena software product(s) that you have licensed separately from Serena.
 *  Such applications shall be for your internal use only.  You further agree
 *  that you will not: (a) sell, market, or distribute any copies of the
 *  Sample Code or any derivatives or components thereof; (b) use the Sample
 *  Code or any derivatives thereof for any commercial purpose; or (c) assign
 *  or transfer rights to the Sample Code or any derivatives thereof.
 *
 *  2.  DISCLAIMER OF WARRANTIES.  TO THE MAXIMUM EXTENT PERMITTED BY
 *  APPLICABLE LAW, SERENA PROVIDES THE SAMPLE CODE AS IS AND WITH ALL
 *  FAULTS, AND HEREBY DISCLAIMS ALL WARRANTIES AND CONDITIONS, EITHER
 *  EXPRESSED, IMPLIED OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY
 *  IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY, OF FITNESS FOR A
 *  PARTICULAR PURPOSE, OF LACK OF VIRUSES, OF RESULTS, AND OF LACK OF
 *  NEGLIGENCE OR LACK OF WORKMANLIKE EFFORT, CONDITION OF TITLE, QUIET
 *  ENJOYMENT, OR NON-INFRINGEMENT.  THE ENTIRE RISK AS TO THE QUALITY OF
 *  OR ARISING OUT OF USE OR PERFORMANCE OF THE SAMPLE CODE, IF ANY,
 *  REMAINS WITH YOU.
 *
 *  3.  EXCLUSION OF DAMAGES.  TO THE MAXIMUM EXTENT PERMITTED BY APPLICABLE
 *  LAW, YOU AGREE THAT IN CONSIDERATION FOR RECEIVING THE SAMPLE CODE AT NO
 *  CHARGE TO YOU, SERENA SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER,
 *  INCLUDING BUT NOT LIMITED TO DIRECT, SPECIAL, INCIDENTAL, INDIRECT, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, DAMAGES FOR LOSS OF
 *  PROFITS OR CONFIDENTIAL OR OTHER INFORMATION, FOR BUSINESS INTERRUPTION,
 *  FOR PERSONAL INJURY, FOR LOSS OF PRIVACY, FOR NEGLIGENCE, AND FOR ANY
 *  OTHER LOSS WHATSOEVER) ARISING OUT OF OR IN ANY WAY RELATED TO THE USE
 *  OF OR INABILITY TO USE THE SAMPLE CODE, EVEN IN THE EVENT OF THE FAULT,
 *  TORT (INCLUDING NEGLIGENCE), STRICT LIABILITY, OR BREACH OF CONTRACT,
 *  EVEN IF SERENA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.  THE
 *  FOREGOING LIMITATIONS, EXCLUSIONS AND DISCLAIMERS SHALL APPLY TO THE
 *  MAXIMUM EXTENT PERMITTED BY APPLICABLE LAW.  NOTWITHSTANDING THE ABOVE,
 *  IN NO EVENT SHALL SERENA'S LIABILITY UNDER THIS AGREEMENT OR WITH RESPECT
 *  TO YOUR USE OF THE SAMPLE CODE AND DERIVATIVES THEREOF EXCEED US$10.00.
 *
 *  4.  INDEMNIFICATION. You hereby agree to defend, indemnify and hold
 *  harmless Serena from and against any and all liability, loss or claim
 *  arising from this agreement or from (i) your license of, use of or
 *  reliance upon the Sample Code or any related documentation or materials,
 *  or (ii) your development, use or reliance upon any application or
 *  derivative work created from the Sample Code.
 *
 *  5.  TERMINATION OF THE LICENSE.  This agreement and the underlying
 *  license granted hereby shall terminate if and when your license to the
 *  applicable Serena software product terminates or if you breach any terms
 *  and conditions of this agreement.
 *
 *  6.  CONFIDENTIALITY.  The Sample Code and all information relating to the
 *  Sample Code (collectively "Confidential Information") are the
 *  confidential information of Serena.  You agree to maintain the
 *  Confidential Information in strict confidence for Serena.  You agree not
 *  to disclose or duplicate, nor allow to be disclosed or duplicated, any
 *  Confidential Information, in whole or in part, except as permitted in
 *  this Agreement.  You shall take all reasonable steps necessary to ensure
 *  that the Confidential Information is not made available or disclosed by
 *  you or by your employees to any other person, firm, or corporation.  You
 *  agree that all authorized persons having access to the Confidential
 *  Information shall observe and perform under this nondisclosure covenant.
 *  You agree to immediately notify Serena of any unauthorized access to or
 *  possession of the Confidential Information.
 *
 *  7.  AFFILIATES.  Serena as used herein shall refer to Serena Software,
 *  Inc. and its affiliates.  An entity shall be considered to be an
 *  affiliate of Serena if it is an entity that controls, is controlled by,
 *  or is under common control with Serena.
 *
 *  8.  GENERAL.  Title and full ownership rights to the Sample Code,
 *  including any derivative works shall remain with Serena.  If a court of
 *  competent jurisdiction holds any provision of this agreement illegal or
 *  otherwise unenforceable, that provision shall be severed and the
 *  remainder of the agreement shall remain in full force and effect.
 * ===========================================================================
 */
package hudson.plugins.dimensionsscm;

import com.serena.dmclient.api.Baseline;
import com.serena.dmclient.api.BulkOperator;
import com.serena.dmclient.api.DimensionsConnection;
import com.serena.dmclient.api.DimensionsConnectionDetails;
import com.serena.dmclient.api.DimensionsConnectionManager;
import com.serena.dmclient.api.DimensionsNetworkException;
import com.serena.dmclient.api.DimensionsObjectFactory;
import com.serena.dmclient.api.DimensionsRelatedObject;
import com.serena.dmclient.api.DimensionsResult;
import com.serena.dmclient.api.DimensionsRuntimeException;
import com.serena.dmclient.api.Filter;
import com.serena.dmclient.api.ItemRevision;
import com.serena.dmclient.api.Project;
import com.serena.dmclient.api.Request;
import com.serena.dmclient.api.SystemAttributes;
import com.serena.dmclient.api.SystemRelationship;
import com.serena.dmclient.objects.DimensionsObject;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Node;
import hudson.model.Run;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Dimensions API facade.
 */
public class DimensionsAPI implements Serializable {
    private static final String MISSING_SOURCE_PATH = "The nested element needs a valid 'srcpath' attribute"; //$NON-NLS-1$
    private static final String MISSING_PROJECT = "The nested element needs a valid project to work on"; //$NON-NLS-1$
    private static final String MISSING_BASELINE = "The nested element needs a valid baseline to work on"; //$NON-NLS-1$
    private static final String MISSING_REQUEST = "The nested element needs a valid request to work on"; //$NON-NLS-1$
    private static final String BAD_BASE_DATABASE_SPEC = "The <dimensions> task needs a valid 'database' attribute, in the format 'dbname@dbconn'"; //$NON-NLS-1$
    private static final String NO_COMMAND_LINE = "The <run> nested element need a valid 'cmd' attribute"; //$NON-NLS-1$
    private static final String SRCITEM_SRCPATH_CONFLICT = "The <getcopy> nested element needs exactly one of the 'srcpath' or 'srcitem' attributes"; //$NON-NLS-1$

    // Thread safe key (sequence) generator.
    private static final AtomicLong sequence = new AtomicLong(1);

    // Dimensions server details.
    private String dmServer;
    private String dmDb;

    private String dbName;
    private String dbConn;

    // Dimensions user details.
    private String dmUser;
    private String dmPasswd;

    // Dimensions project details.
    private String dmProject;
    private String dmDirectory;
    private String dmRequest;
    private String projectPath;

    private static final String DATE_TYPE = "edit";
    private boolean allRevisions;
    private int version = -1;
    private final ConcurrentMap<Long, DimensionsConnection> conns = new ConcurrentHashMap<Long, DimensionsConnection>();
    private PrintStream listener;
    private PathMatcher pathMatcher;

    /**
     * Gets the logger.
     *
     * @return the task logger
     */
    public final PrintStream getLogger() {
        return this.listener;
    }

    /**
     * Set the logger.
     */
    public final void setLogger(PrintStream logger) {
        this.listener = logger;
    }

    /**
     * Get matcher for paths by pattern.
     */
    final PathMatcher getPathMatcher() {
        return pathMatcher;
    }

    /**
     * Set matcher for paths by pattern.
     */
    final void setPathMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    /**
     * Gets the user ID for the connection.
     *
     * @return the user ID of the user as whom to connect
     */
    public final String getSCMUserID() {
        return this.dmUser;
    }

    /**
     * Gets the Dimensions version if set.
     *
     * @return version
     */
    public final int getDmVersion() {
        if (version > 0) {
            return this.version;
        } else {
            return 0;
        }
    }

    /**
     * Gets the base database for the connection (as "NAME@CONNECTION").
     *
     * @return the name of the base database to connect to
     */
    public final String getSCMDatabase() {
        return this.dmDb;
    }

    /**
     * Gets the base database for the connection.
     *
     * @return the name of the base database only
     */
    public final String getSCMBaseDb() {
        return this.dbName;
    }

    /**
     * Gets the database DNS for the connection.
     *
     * @return the name of the DSN only
     */
    public final String getSCMDsn() {
        return this.dbConn;
    }

    /**
     * Gets the server for the connection.
     *
     * @return the name of the server to connect to
     */
    public final String getSCMServer() {
        return this.dmServer;
    }

    /**
     * Gets the project ID for the connection.
     *
     * @return the project ID
     */
    public final String getSCMProject() {
        return this.dmProject;
    }

    /**
     * Gets the project path.
     *
     * @return the project path
     */
    public final String getSCMPath() {
        return this.projectPath;
    }

    /**
     * Gets the repository connection class
     *
     * @return the SCM repository connection
     */
    public final DimensionsConnection getCon(long key) {
        Logger.debug("Looking for key " + key);
        DimensionsConnection con = conns.get(key);
        if (con != null) {
            try {
                DimensionsConnectionManager.unregisterThreadConnection();
            } catch (Exception e) {
            }
            DimensionsConnectionManager.registerThreadConnection(con);
            Logger.debug("Found database");
            return con;
        }
        Logger.debug("Could not find database");
        return null;
    }

    /**
     * Creates a Dimensions session using the supplied login credentials and server details.
     *
     * @param userID Dimensions user ID
     * @param password Dimensions password
     * @param database Base database name
     * @param server Hostname of the remote Dimensions server
     * @return A long key for the connection
     * @throws DimensionsRuntimeException, IllegalArgumentException
     */
    public final long login(String userID, String password, String database, String server)
            throws IllegalArgumentException, DimensionsRuntimeException {
        long key = sequence.getAndIncrement();

        dmServer = server;
        dmDb = database;
        dmUser = userID;
        dmPasswd = password;

        Logger.debug("Checking Dimensions login parameters...");

        if (dmServer == null || dmServer.length() == 0 || dmDb == null || dmDb.length() == 0
                || dmUser == null || dmUser.length() == 0 || dmPasswd == null || dmPasswd.length() == 0) {
            throw new IllegalArgumentException("Invalid or not parameters have been specified");
        }
        try {
            // check if we need to pre-process the login details
            String[] dbCompts = parseDatabaseString(dmDb);
            dbName = dbCompts[0];
            dbConn = dbCompts[1];
            Logger.debug("Logging into Dimensions: " + dmUser + " " + dmServer + " " + dmDb);

            DimensionsConnectionDetails details = new DimensionsConnectionDetails();
            details.setUsername(dmUser);
            details.setPassword(dmPasswd);
            details.setDbName(dbName);
            details.setDbConn(dbConn);
            details.setServer(dmServer);
            Logger.debug("Getting Dimensions connection...");
            DimensionsConnection connection = DimensionsConnectionManager.getConnection(details);
            if (connection != null) {
                Logger.debug("Connection map key is " + key);
                Logger.debug("Connection map size before putIfAbsent is " + conns.size());
                if (conns.putIfAbsent(key, connection) != null) {
                    Logger.debug("Connection map already contains key " + key);
                }
                Logger.debug("Connection map size after putIfAbsent is " + conns.size());
                if (version < 0) {
                    version = 2009;
                    // Get the server version.
                    List<String> inf = connection.getObjectFactory().getServerVersion(2);
                    if (inf == null) {
                        Logger.debug("Detection of server information failed");
                    }
                    if (inf != null) {
                        Logger.debug("Server information detected -" + inf.size());
                        for (int i = 0; i < inf.size(); ++i) {
                            String prop = inf.get(i);
                            Logger.debug(i + " - " + prop);
                        }

                        // Try and locate the server version.
                        // If not found, then get the schema version and use that.
                        String serverx = inf.get(2);
                        if (serverx == null) {
                            serverx = inf.get(0);
                        }
                        if (serverx != null) {
                            Logger.debug("Detected server version: " + serverx);
                            String[] tokens = serverx.split(" ");
                            serverx = tokens[0];
                            if (serverx.startsWith("10.")) {
                                version = 10;
                            } else if (serverx.startsWith("2009")) {
                                version = 2009;
                            } else if (serverx.startsWith("201")) {
                                version = 2010;
                            } else if (serverx.startsWith("12.1")) {
                                version = 2010;
                            } else if (serverx.startsWith("12.2")) {
                                version = 2010;
                            } else {
                                version = 2009;
                            }
                            Logger.debug("Version to process set to " + version);
                        } else {
                            Logger.debug("No server information found");
                        }
                    }
                }
            } else {
                Logger.debug("Dimensions connection was null");
            }
        } catch (Exception e) {
            throw (DimensionsRuntimeException) new DimensionsRuntimeException(Values.exceptionMessage("Login to Dimensions failed",
                    e, "no message")).initCause(e);
        }
        if (conns.containsKey(key)) {
            return key;
        }
        return -1L;
    }

    /**
     * Creates a Dimensions session using the supplied login credentials and server details. With additional tracing
     * from supplied build details.
     *
     * @param userID Dimensions user ID
     * @param password Dimensions password
     * @param database base database name
     * @param server hostname of the remote Dimensions server
     * @param build details of the invoking build run
     * @return a long
     * @throws DimensionsRuntimeException, IllegalArgumentException
     */
    public final long login(String userID, String password, String database, String server, Run build)
            throws IllegalArgumentException, DimensionsRuntimeException {
        Logger.debug("DimensionsAPI.login - build number: \"" + build.getNumber() + "\", project: \""
                + build.getParent().getName() + "\"");
        if (build instanceof AbstractBuild) {
            Node node = ((AbstractBuild) build).getBuiltOn();
            String nodeName = node != null ? node.getNodeName() : null;
            Logger.debug("  build getBuiltOn().getNodeName(): " + (nodeName != null ? ("\"" + nodeName + "\"") : null));
        }
        final long key = login(userID, password, database, server);
        Logger.debug("  key: \"" + key + "\"");
        return key;
    }

    /**
     * Disconnects from the Dimensions repository
     */
    public final void logout(long key) {
        DimensionsConnection connection = getCon(key);
        if (connection == null) {
            Logger.debug("Failed to close connection for key \"" + key + "\" - connection not found!");
        } else {
            try {
                Logger.debug("Closing connection to Dimensions for key \"" + key + "\"...");
                connection.close();
            } catch (DimensionsNetworkException dne) {
                Logger.debug("Exception thrown: DimensionsNetworkException", dne);
            } catch (DimensionsRuntimeException dre) {
                Logger.debug("Exception thrown: DimensionsRuntimeException", dre);
            }
            conns.remove(key);
            Logger.debug("Now have " + conns.size() + " connections in use...");
        }
    }

    /**
     * Disconnects from the Dimensions repository, with additional tracing from supplied build details.
     */
    public final void logout(long key, Run build) {
        Logger.debug("DimensionsAPI.logout - build number: \"" + build.getNumber() + "\", project: \""
                + build.getParent().getName() + "\"");
        logout(key);
    }

    /**
     * Parses a base database specification.
     * <p>
     * Valid patterns are dbName/dbPassword@dbConn or dbName@dbConn. Anything else will cause a java.text.ParseException
     * to be thrown. Returns an array of either [dbName, dbConn, dbPassword] or [dbName, dbConn].
     *
     * @param database a base database specification
     * @return an array of base database specification components
     * @throws ParseException if the supplied String does not conform to the above rules
     */
    private static String[] parseDatabaseString(String database) throws ParseException {
        String[] dbCompts;
        int endName = database.indexOf('/');
        int startConn = database.indexOf('@');
        if (startConn < 1 || startConn == database.length() - 1) {
            throw new ParseException(BAD_BASE_DATABASE_SPEC, startConn);
        }
        if (endName < 0 || startConn <= endName) {
            // no '/' or '@' is before '/':
            String dbName = database.substring(0, startConn);
            String dbConn = database.substring(startConn + 1);
            dbCompts = new String[2];
            dbCompts[0] = dbName;
            dbCompts[1] = dbConn;
        } else if (endName == 0 || startConn == endName + 1) {
            // '/' at start or '/' immediately followed by '@':
            throw new ParseException(BAD_BASE_DATABASE_SPEC, endName);
        } else {
            String dbName = database.substring(0, endName);
            String dbPassword = database.substring(endName + 1, startConn);
            String dbConn = database.substring(startConn + 1);
            dbCompts = new String[3];
            dbCompts[0] = dbName;
            dbCompts[1] = dbConn;
            dbCompts[2] = dbPassword;
        }
        return dbCompts;
    }

    /**
     * Has the repository had any changes made during a certain time?
     */
    public boolean hasRepositoryBeenUpdated(final long key, final String projectName, final FilePath workspace,
            final Calendar fromDate, final Calendar toDate, final TimeZone tz) throws IOException, InterruptedException {
        DimensionsConnection connection = getCon(key);
        if (fromDate == null) {
            return true;
        }
        if (connection == null) {
            throw new IOException("Not connected to an SCM repository");
        }
        try {
            @SuppressWarnings("unchecked")
            List<ItemRevision> items = calcRepositoryDiffs(key, projectName, null, null, workspace, fromDate, toDate, tz);
            if (items != null) {
                PathMatcher pathMatcher = getPathMatcher();
                for (ItemRevision itemRevision : items) {
                    String fullPathName = (String) itemRevision.getAttribute(SystemAttributes.FULL_PATH_NAME);
                    // Match when fullPathName is not ignored, false otherwise.
                    if (pathMatcher.match(fullPathName)) {
                        Logger.debug("Found " + items.size() + " changed item(s), "
                                + "and at least one ('" + fullPathName + "') passed the " + pathMatcher);
                        return true;
                    }
                }
            }
            Logger.debug("Found " + (items == null ? "nil" : items.size()) + " changed item(s), "
                    + ((items == null || items.isEmpty()) ? "so" : "but") + " none passed the " + getPathMatcher());
        } catch (Exception e) {
            String message = Values.exceptionMessage("Unable to run hasRepositoryBeenUpdated", e, "no message");
            Logger.debug(message, e);
            throw (IOException) new IOException(message).initCause(e);
        }
        return false;
    }

    /**
     * Get a copy of the code.
     */
    public boolean checkout(final long key, final String projectName, final FilePath projectDir,
            final FilePath workspaceName, StringBuffer cmdOutput, final String baseline, final String requests,
            final boolean doRevert, final boolean doExpand, final boolean doNoMetadata, final boolean doNoTouch,
            final String permissions, final String eol) throws IOException, InterruptedException {
        boolean bRet = false;
        DimensionsConnection connection = getCon(key);
        if (connection == null) {
            throw new IOException("Not connected to an SCM repository");
        }
        try {
            String coCmd = "UPDATE /BRIEF ";
            if (version == 10) {
                coCmd = "DOWNLOAD ";
                if (requests != null) {
                    coCmd = "FCDI ";
                }
            }

            {
                // Disable the new refactoring UPDATE options
                if (version == 2010) {
                    coCmd += " /LEGACY_MODE ";
                }

                String cmd = coCmd;
                String projDir = (projectDir != null) ? projectDir.getRemote() : null;

                if (requests != null && version == 10) {
                    cmd += requests;
                }

                if (projDir != null && !projDir.equals("\\") && !projDir.equals("/") && requests == null) {
                    cmd += "/DIR=\"" + projDir + "\"";
                }

                if (requests != null && version != 10) {
                    if (requests.indexOf(',') == -1) {
                        cmd += "/CHANGE_DOC_IDS=(\"" + requests + "\") ";
                    } else {
                        cmd += "/CHANGE_DOC_IDS=(" + requests + ") ";
                    }
                    cmd += "/WORKSET=\"" + projectName + "\" ";
                } else if (baseline != null) {
                    cmd += "/BASELINE=\"" + baseline + "\"";
                } else {
                    cmd += "/WORKSET=\"" + projectName + "\" ";
                }

                if (permissions != null && permissions.length() > 0) {
                    if (!permissions.equals("DEFAULT")) {
                        cmd += "/PERMS=" + permissions;
                    }
                }

                if (eol != null && eol.length() > 0) {
                    if (!eol.equals("DEFAULT")) {
                        cmd += "/EOL=" + eol;
                    }
                }

                cmd += "/USER_DIR=\"" + workspaceName.getRemote() + "\" ";

                if (doRevert) {
                    cmd += " /OVERWRITE";
                }
                if (doExpand) {
                    cmd += " /EXPAND";
                }
                if (doNoMetadata) {
                    cmd += " /NOMETADATA";
                }
                if (doNoTouch) {
                    cmd += " /NOTOUCH";
                }

                if (requests == null) {
                    getLogger().println("[DIMENSIONS] Checking out directory '" + (projDir != null ? projDir : "/") + "'...");
                    getLogger().flush();
                }

                DimensionsResult res = run(connection, cmd);
                if (res != null) {
                    cmdOutput = cmdOutput.append(res.getMessage());
                    String outputStr = cmdOutput.toString();
                    Logger.debug(outputStr);
                    bRet = true;

                    // Check if any conflicts were identified.
                    int confl = outputStr.indexOf("C\t");
                    if (confl > 0) {
                        bRet = false;
                    }
                }
            }
        } catch (Exception e) {
            throw (IOException) new IOException(Values.exceptionMessage("Exception during checkout", e, "no message")).initCause(e);
        }
        return bRet;
    }

    /**
     * Generate changelog file.
     */
    public boolean createChangeSetLogs(final long key, final String projectName, final FilePath projectDir,
            final Calendar fromDate, final Calendar toDate, final File changelogFile, final TimeZone tz,
            final String url, final String baseline, final String requests) throws IOException, InterruptedException {
        DimensionsConnection connection = getCon(key);
        if (connection == null) {
            throw new IOException("Not connected to an SCM repository");
        }
        try {
            List items = calcRepositoryDiffs(key, projectName, baseline, requests, projectDir, fromDate, toDate, tz);

            Logger.debug("CM Url : " + (url != null ? url : "(null)"));
            if (requests != null) {
                getLogger().println("[DIMENSIONS] Calculating changes for request(s) '" + requests + "'...");
            } else {
                getLogger().println("[DIMENSIONS] Calculating changes for directory '"
                        + (projectDir != null ? projectDir.getRemote() : "/") + "'...");
            }
            getLogger().flush();

            if (items != null) {
                // Write the list of changes into a changelog file.
                List changes = createChangeList(items, tz, url);
                Logger.debug("Writing " + changes.size() + " changes to changelog file '" + changelogFile.getPath() + "'");
                DimensionsChangeLogWriter.writeLog(changes, changelogFile);
            } else {
                // No changes, so create an empty changelog file.
                Logger.debug("Writing null changes to changelog file '" + changelogFile.getPath() + "'");
                DimensionsChangeLogWriter.writeLog(null, changelogFile);
            }
        } catch (Exception e) {
            throw (IOException) new IOException(Values.exceptionMessage("Exception calculating changes", e,
                    "no message")).initCause(e);
        }
        return true;
    }

    /**
     * Calculate any repository changes made during a certain time.
     */
    private List calcRepositoryDiffs(final long key, final String projectName, final String baselineName,
            final String requests, final FilePath workspace, final Calendar fromDate, final Calendar toDate,
            final TimeZone tz) throws IOException, InterruptedException {
        DimensionsConnection connection = getCon(key);
        if (connection == null) {
            throw new IOException("Not connected to an SCM repository");
        }
        if (fromDate == null && baselineName == null && requests == null) {
            return null;
        }
        try {
            // Get the dates for the last build
            int[] attrs = getItemFileAttributes(true);
            String dateAfter = (fromDate != null) ? formatDatabaseDate(fromDate.getTime(), tz) : "01-JAN-1970 00:00:00";
            String dateBefore = (toDate != null) ? formatDatabaseDate(toDate.getTime(), tz) : formatDatabaseDate(Calendar.getInstance().getTime(), tz);

            Filter filter = new Filter();
            if (baselineName != null || !isStream(connection, projectName)) {
                filter.criteria().add(new Filter.Criterion(SystemAttributes.LAST_UPDATED_DATE, dateAfter, Filter.Criterion.GREATER_EQUAL));
                filter.criteria().add(new Filter.Criterion(SystemAttributes.LAST_UPDATED_DATE, dateBefore, Filter.Criterion.LESS_EQUAL));
            } else {
                filter.criteria().add(new Filter.Criterion(SystemAttributes.CREATION_DATE, dateAfter, Filter.Criterion.GREATER_EQUAL));
                filter.criteria().add(new Filter.Criterion(SystemAttributes.CREATION_DATE, dateBefore, Filter.Criterion.LESS_EQUAL));
            }
            filter.criteria().add(new Filter.Criterion(SystemAttributes.IS_EXTRACTED, "Y", Filter.Criterion.NOT)); //$NON-NLS-1$
            filter.orders().add(new Filter.Order(SystemAttributes.REVISION_COMMENT, Filter.ORDER_ASCENDING));
            filter.orders().add(new Filter.Order(SystemAttributes.ITEMFILE_DIR, Filter.ORDER_ASCENDING));
            filter.orders().add(new Filter.Order(SystemAttributes.ITEMFILE_FILENAME, Filter.ORDER_ASCENDING));

            Logger.debug("Looking between " + dateAfter + " -> " + dateBefore);
            String projName;

            if (baselineName != null && requests == null) {
                projName = baselineName.toUpperCase(Values.ROOT_LOCALE);
            } else {
                projName = projectName.toUpperCase(Values.ROOT_LOCALE);
            }

            List items = null;

            if (requests != null) {
                //
                // Use existing routine to get list of items related to requests
                //
                try {
                    items = getItemsInRequests(connection, projName, requests, dateAfter, dateBefore);
                } catch (Exception e) {
                    throw (IOException) new IOException(Values.exceptionMessage("Exception getting items in requests", e,
                            "no message")).initCause(e);
                }
            } else if (baselineName != null) {
                // setup filter for baseline Name
                Filter baselineFilter = new Filter();
                baselineFilter.criteria().add(new Filter.Criterion(SystemAttributes.OBJECT_SPEC,
                        baselineName.toUpperCase(Values.ROOT_LOCALE), Filter.Criterion.EQUALS));

                List<Baseline> baselineObjects = connection.getObjectFactory().getBaselines(baselineFilter);
                Logger.debug("Baseline query for \"" + baselineName + "\" returned " + baselineObjects.size() + " baselines");
                for (int i = 0; i < baselineObjects.size(); i++) {
                    Logger.debug("Baseline " + i + " is \"" + baselineObjects.get(i).getName() + "\"");
                }

                if (baselineObjects.size() == 0) {
                    throw new IOException("Could not find baseline \"" + baselineName + "\" in repository");
                }
                if (baselineObjects.size() > 1) {
                    throw new IOException("Found more than one baseline named \"" + baselineName + "\" in repository");
                }

                items = queryItems(connection, baselineObjects.get(0), workspace.getRemote(), filter, attrs, true, !allRevisions);
            } else {
                Project projectObj = connection.getObjectFactory().getProject(projName);
                items = queryItems(connection, projectObj, workspace.getRemote(), filter, attrs, true, !allRevisions);
            }
            return items;
        } catch (Exception e) {
            throw (IOException) new IOException(Values.exceptionMessage("Unable to run calcRepositoryDiffs", e,
                    "no message")).initCause(e);
        }
    }

    /**
     * Lock a project.
     */
    public DimensionsResult lockProject(long key, String projectId) throws DimensionsRuntimeException {
        DimensionsConnection connection = getCon(key);
        if (connection == null) {
            throw new DimensionsRuntimeException("Not connected to an SCM repository");
        }
        try {
            String cmd = "LCK WORKSET ";
            if (projectId != null) {
                cmd += "\"" + projectId + "\"";
                DimensionsResult res = run(connection, cmd);
                if (res != null) {
                    Logger.debug("Locking project - " + res.getMessage());
                    return res;
                }
            }
            return null;
        } catch (Exception e) {
            throw (DimensionsRuntimeException) new DimensionsRuntimeException(Values.exceptionMessage("Lock project", e,
                    "no message")).initCause(e);
        }
    }

    /**
     * Unlock a project.
     */
    public DimensionsResult unlockProject(long key, String projectId) throws DimensionsRuntimeException {
        DimensionsConnection connection = getCon(key);
        if (connection == null) {
            throw new DimensionsRuntimeException("Not connected to an SCM repository");
        }
        try {
            String cmd = "ULCK WORKSET ";
            if (projectId != null) {
                cmd += "\"" + projectId + "\"";
                DimensionsResult res = run(connection, cmd);
                if (res != null) {
                    Logger.debug("Unlocking project - " + res.getMessage());
                    return res;
                }
            }
            return null;
        } catch (Exception e) {
            throw (DimensionsRuntimeException) new DimensionsRuntimeException(Values.exceptionMessage("Unlock project", e,
                    "no message")).initCause(e);
        }
    }

    /**
     * Build a baseline.
     */
    public DimensionsResult buildBaseline(long key, String area, String projectId, boolean batch, boolean buildClean,
            String buildConfig, String options, boolean capture, String requests, String targets, Run build,
            String blnName) throws DimensionsRuntimeException {
        DimensionsConnection connection = getCon(key);
        if (connection == null) {
            throw new DimensionsRuntimeException("Not connected to an SCM repository");
        }
        try {
            String cmd = "BLDB ";
            if (projectId != null && build != null) {
                Integer buildNo = build.getNumber();
                if (blnName != null && blnName.length() > 0) {
                    cmd += blnName;
                } else {
                    cmd += "\"" + projectId + "_" + build.getParent().getName() + "_" + buildNo + "\"";
                }
                if (area != null && area.length() > 0) {
                    cmd += " /AREA=\"" + area + "\"";
                }
                if (batch) {
                    cmd += " /NOWAIT";
                } else {
                    cmd += " /WAIT";
                }
                if (capture) {
                    cmd += " /CAPTURE";
                } else {
                    cmd += " /NOCAPTURE";
                }
                if (buildClean) {
                    cmd += " /BUILD_CLEAN";
                }
                if (buildConfig != null && buildConfig.length() > 0) {
                    cmd += " /BUILD_CONFIG=\"" + buildConfig + "\"";
                }
                if (options != null && options.length() > 0) {
                    if (options.indexOf(',') == -1) {
                        cmd += "/BUILD_OPTIONS=(\"" + options + "\") ";
                    } else {
                        cmd += "/BUILD_OPTIONS=(" + options + ") ";
                    }
                }
                if (requests != null && requests.length() > 0) {
                    if (requests.indexOf(',') == -1) {
                        cmd += "/CHANGE_DOC_IDS=(\"" + requests + "\") ";
                    } else {
                        cmd += "/CHANGE_DOC_IDS=(" + requests + ") ";
                    }
                }
                if (targets != null && targets.length() > 0) {
                    if (targets.indexOf(',') == -1) {
                        cmd += "/TARGETS=(\"" + targets + "\") ";
                    } else {
                        cmd += "/TARGETS=(" + targets + ") ";
                    }
                }
                DimensionsResult res = run(connection, cmd);
                if (res != null) {
                    Logger.debug("Building baseline - " + res.getMessage());
                    return res;
                }
            }
            return null;
        } catch (Exception e) {
            throw (DimensionsRuntimeException) new DimensionsRuntimeException(Values.exceptionMessage("Build baseline", e,
                    "no message")).initCause(e);
        }
    }

    /**
     * Build a project.
     */
    public DimensionsResult buildProject(long key, String area, String projectId, boolean batch, boolean buildClean,
            String buildConfig, String options, boolean capture, String requests, String targets, String stage,
            String type, boolean audit, boolean populate, boolean touch, Run build)
            throws DimensionsRuntimeException {
        DimensionsConnection connection = getCon(key);
        if (connection == null) {
            throw new DimensionsRuntimeException("Not connected to an SCM repository");
        }
        try {
            String cmd = "BLD ";
            if (projectId != null && build != null) {
                cmd += "\"" + projectId + "\"";
                if (area != null && area.length() > 0) {
                    cmd += " /AREA=\"" + area + "\"";
                }
                if (type != null && type.length() > 0) {
                    cmd += " /TYPE=\"" + type + "\"";
                }
                if (stage != null && stage.length() > 0) {
                    cmd += " /STAGE=\"" + stage + "\"";
                }
                if (touch) {
                    cmd += " /TOUCH";
                } else {
                    cmd += " /NOTOUCH";
                }
                if (populate) {
                    cmd += " /POPULATE";
                } else {
                    cmd += " /NOPOPULATE";
                }
                if (audit) {
                    cmd += " /AUDIT";
                } else {
                    cmd += " /NOAUDIT";
                }
                if (batch) {
                    cmd += " /NOWAIT";
                } else {
                    cmd += " /WAIT";
                }
                if (capture) {
                    cmd += " /CAPTURE";
                } else {
                    cmd += " /NOCAPTURE";
                }
                if (buildClean) {
                    cmd += " /BUILD_CLEAN";
                }
                if (buildConfig != null && buildConfig.length() > 0) {
                    cmd += " /BUILD_CONFIG=\"" + buildConfig + "\"";
                }
                if (options != null && options.length() > 0) {
                    if (options.indexOf(',') == -1) {
                        cmd += "/BUILD_OPTIONS=(\"" + options + "\") ";
                    } else {
                        cmd += "/BUILD_OPTIONS=(" + options + ") ";
                    }
                }
                if (requests != null && requests.length() > 0) {
                    if (requests.indexOf(',') == -1) {
                        cmd += "/CHANGE_DOC_IDS=(\"" + requests + "\") ";
                    } else {
                        cmd += "/CHANGE_DOC_IDS=(" + requests + ") ";
                    }
                }
                if (targets != null && targets.length() > 0) {
                    if (targets.indexOf(',') == -1) {
                        cmd += "/TARGETS=(\"" + targets + "\") ";
                    } else {
                        cmd += "/TARGETS=(" + targets + ") ";
                    }
                }
                DimensionsResult res = run(connection, cmd);
                if (res != null) {
                    Logger.debug("Building project - " + res.getMessage());
                    return res;
                }
            }
            return null;
        } catch (Exception e) {
            throw (DimensionsRuntimeException) new DimensionsRuntimeException(Values.exceptionMessage("Build project", e,
                    "no message")).initCause(e);
        }
    }

    /**
     * Upload files.
     */
    public DimensionsResult UploadFiles(long key, FilePath rootDir, String projectId, File cmdFile, String projectName,
            int buildNo, String requests, boolean forceCheckIn, boolean forceTip, String owningPart)
            throws DimensionsRuntimeException {
        DimensionsConnection connection = getCon(key);
        if (connection == null) {
            throw new DimensionsRuntimeException("Not connected to an SCM repository");
        }
        try {
            boolean isStream = false;

            if (version != 10) {
                isStream = isStream(connection, projectId);
            }

            String ciCmd = "DELIVER /BRIEF /ADD /UPDATE /DELETE ";
            if (version == 10 || !isStream) {
                ciCmd = "UPLOAD ";
            }
            if (projectId != null) {
                ciCmd += " /USER_FILELIST=\"" + cmdFile.getAbsolutePath() + "\"";
                ciCmd += " /WORKSET=\"" + projectId + "\"";
                ciCmd += " /COMMENT=\"Build artifacts delivered by Jenkins for job '" + projectName + "' - build " + buildNo + "\"";
                ciCmd += " /USER_DIRECTORY=\"" + rootDir.getRemote() + "\"";
                if (requests != null && requests.length() > 0) {
                    if (requests.indexOf(',') == -1) {
                        ciCmd += "/CHANGE_DOC_IDS=(\"" + requests + "\") ";
                    } else {
                        ciCmd += "/CHANGE_DOC_IDS=(" + requests + ") ";
                    }
                }
                if (owningPart != null && owningPart.length() > 0) {
                    ciCmd += "/PART=\"" + owningPart + "\"";
                }
                if (!isStream) {
                    if (forceCheckIn) {
                        ciCmd += "/FORCE_CHECKIN ";
                    }
                    if (forceTip) {
                        ciCmd += "/FORCE_TIP ";
                    }
                }
                DimensionsResult res = run(connection, ciCmd);
                if (res != null) {
                    Logger.debug("Saving artifacts - " + res.getMessage());
                    return res;
                }
            }
            return null;
        } catch (Exception e) {
            throw (DimensionsRuntimeException) new DimensionsRuntimeException(Values.exceptionMessage("Upload files", e,
                    "no message")).initCause(e);
        }
    }

    /**
     * Create a project tag.
     */
    public DimensionsResult createBaseline(long key, String projectVersion, Run build, String blnScope,
            String blnTemplate, String blnOwningPart, String blnType, String requestId, String blnId, String blnName,
            StringBuffer cblId) throws DimensionsRuntimeException {
        DimensionsConnection connection = getCon(key);
        if (connection == null) {
            throw new DimensionsRuntimeException("Not connected to an SCM repository");
        }
        try {
            String cmd = "CBL ";

            if (projectVersion != null && build != null) {
                boolean wsBln = true;
                boolean revisedBln = false;
                Integer buildNo = build.getNumber();

                if (blnScope != null && blnScope.length() > 0) {
                    if (blnScope.equals("REVISED")) {
                        revisedBln = true;
                        cmd = "CRB ";
                    }
                }

                // Split "PRODUCT:PROJECT;VERSION".
                int colon = projectVersion.indexOf(':');
                int semicolon = projectVersion.lastIndexOf(';');
                if (semicolon <= colon) {
                    semicolon = -1;
                }
                String productName = projectVersion.substring(0, colon).trim();
                String projectName = (semicolon >= 0) ? projectVersion.substring(colon + 1, semicolon).trim()
                        : projectVersion.substring(colon + 1).trim();

                if (blnName != null && blnName.length() > 0) {
                    String cId = blnName;

                    cId = cId.replace("[PROJECTID]", projectName);
                    cId = cId.replace("[HUDSON_PROJECT]", build.getParent().getName().trim());
                    cId = cId.replace("[BUILDNO]", buildNo.toString());
                    cId = cId.replace("[CURRENT_DATE]", DateUtils.getNowStrDateVerbose().trim());
                    if (blnId != null && blnId.length() > 0) {
                        cId = cId.replace("[DM_BASELINE]", blnId.substring(blnId.indexOf(':') + 1).trim());
                    }
                    cblId.append('"').append(productName).append(':').append(cId).append('"');
                } else {
                    cblId.append('"').append(productName).append(':').append(projectName).append('_')
                            .append(build.getParent().getName()).append('_').append(buildNo).append('"');
                }

                cmd += cblId.toString() + " /WORKSET=\"" + projectVersion + "\"";
                if (!revisedBln) {
                    if (blnScope == null || blnScope.length() == 0) {
                        cmd += " /SCOPE=WORKSET ";
                    } else {
                        wsBln = false;
                        cmd += " /SCOPE=" + blnScope;
                        if (blnScope.equals("WORKSET")) {
                            wsBln = true;
                        }
                    }

                    if (!wsBln) {
                        if (blnTemplate != null && blnTemplate.length() > 0) {
                            cmd += " /TEMPLATE_ID=\"" + blnTemplate + "\"";
                        }
                        if (blnOwningPart != null && blnOwningPart.length() > 0) {
                            cmd += " /PART=\"" + blnOwningPart + "\"";
                        }
                    }
                } else {
                    if (requestId != null && requestId.length() > 0) {
                        cmd += " /UPDATE_CHANGE_DOC_IDS=(" + requestId + ") /REMOVE_CHANGE_DOC_IDS=(" + requestId + ")";
                    }
                    if (blnId != null && blnId.length() > 0) {
                        cmd += " /BASELINE1=\"" + blnId + "\"";
                    }
                }

                if (blnType != null && blnType.length() > 0) {
                    cmd += " /TYPE=\"" + blnType + "\"";
                }

                if (!revisedBln) {
                    cmd += " /DESCRIPTION=\"Baseline created by Hudson/Jenkins for job '"
                            + build.getParent().getName() + "' - build " + build.getNumber() + "\"";
                }

                DimensionsResult res = run(connection, cmd);
                if (res != null) {
                    Logger.debug("Tagging project - " + res.getMessage());
                    return res;
                }
            }
            return null;
        } catch (Exception e) {
            throw (DimensionsRuntimeException) new DimensionsRuntimeException(Values.exceptionMessage("Create baseline", e,
                    "no message")).initCause(e);
        }
    }

    /**
     * Deploy a baseline.
     */
    public DimensionsResult deployBaseline(long key, String projectId, Run build, String state,
            String blnName) throws DimensionsRuntimeException {
        DimensionsConnection connection = getCon(key);
        if (connection == null) {
            throw new DimensionsRuntimeException("Not connected to an SCM repository");
        }
        try {
            String cmd = "DPB ";
            if (projectId != null && build != null) {
                Integer buildNo = build.getNumber();
                if (blnName != null && blnName.length() > 0) {
                    cmd += blnName;
                } else {
                    cmd += "\"" + projectId + "_" + build.getParent().getName() + "_" + buildNo + "\"";
                }
                cmd += " /WORKSET=\"" + projectId + "\"";
                if (state != null && state.length() > 0) {
                    cmd += " /STAGE=\"" + state + "\"";
                }
                cmd += " /COMMENT=\"Project Baseline deployed by Hudson/Jenkins for job '"
                        + build.getParent().getName() + "' - build " + build.getNumber() + "\"";
                DimensionsResult res = run(connection, cmd);
                if (res != null) {
                    Logger.debug("Deploying baseline - " + res.getMessage());
                    return res;
                }
            }
            return null;
        } catch (Exception e) {
            throw (DimensionsRuntimeException) new DimensionsRuntimeException(Values.exceptionMessage("Deploy baseline", e,
                    "no message")).initCause(e);
        }
    }

    /**
     * Action a baseline.
     */
    public DimensionsResult actionBaseline(long key, String projectId, Run build, String state,
            String blnName) throws DimensionsRuntimeException {
        DimensionsConnection connection = getCon(key);
        if (connection == null) {
            throw new DimensionsRuntimeException("Not connected to an SCM repository");
        }
        try {
            String cmd = "ABL ";
            if (projectId != null && build != null) {
                Integer buildNo = build.getNumber();
                if (blnName != null && blnName.length() > 0) {
                    cmd += blnName;
                } else {
                    cmd += "\"" + projectId + "_" + build.getParent().getName() + "_" + buildNo + "\"";
                }
                cmd += " /WORKSET=\"" + projectId + "\"";
                if (state != null && state.length() > 0) {
                    cmd += " /STATUS=\"" + state + "\"";
                }
                cmd += " /COMMENT=\"Project Baseline action by Hudson/Jenkins for job '"
                        + build.getParent().getName() + "' - build " + build.getNumber() + "\"";
                DimensionsResult res = run(connection, cmd);
                if (res != null) {
                    Logger.debug("Actioning baseline - " + res.getMessage());
                    return res;
                }
            }
            return null;
        } catch (Exception e) {
            throw (DimensionsRuntimeException) new DimensionsRuntimeException(Values.exceptionMessage("Action baseline", e,
                    "no message")).initCause(e);
        }
    }

    /**
     * Construct the change list.
     */
    private List createChangeList(List items, TimeZone tz, String url) throws DimensionsRuntimeException {
        items = getSortedItemList(items);
        List changeSet = new ArrayList(items.size());
        String key = null;
        DimensionsChangeSet cs = null;

        // Internal
        //int SBM_ID   = 49;
        //int SBM_LINK = 17;

        for (int i = 0; i < items.size(); ++i) {
            Logger.debug("Processing change " + i + "/" + items.size());
            ItemRevision item = (ItemRevision) items.get(i);
            int x = 0;

            if (item.getAttribute(SystemAttributes.FULL_PATH_NAME) == null) {
                // Came from another project or something - not in here.
                continue;
            }

            Integer fileVersion = (Integer) item.getAttribute(SystemAttributes.FILE_VERSION);
            String operation;
            if (fileVersion != null) {
                x = fileVersion;
            }
            if (x < 2) {
                operation = "add";
            } else {
                operation = "edit";
            }
            String spec = (String) item.getAttribute(SystemAttributes.OBJECT_SPEC);
            String revision = (String) item.getAttribute(SystemAttributes.REVISION);
            String fileName = ((String) item.getAttribute(SystemAttributes.FULL_PATH_NAME)) + ";" + revision;
            String author = (String) item.getAttribute(SystemAttributes.LAST_UPDATED_USER);
            String comment = (String) item.getAttribute(SystemAttributes.REVISION_COMMENT);
            String date = (String) item.getAttribute(getDateTypeAttribute(operation));

            if (date == null) {
                date = (String) item.getAttribute(getDateTypeAttribute("edit"));
            }
            String urlString = constructURL(spec, url, getSCMDsn(), getSCMBaseDb());
            if (urlString == null) {
                urlString = "";
            }
            if (comment == null) {
                comment = "(None)";
            }
            Logger.debug("Change details -" + comment + " " + revision + " " + fileName + " " + author
                    + " " + spec + " " + date + " " + operation + " (" + x + ") " + urlString);

            Calendar opDate = Calendar.getInstance();
            opDate.setTime(DateUtils.parse(date, tz));

            if (key == null) {
                cs = new DimensionsChangeSet(fileName, author, operation, revision, comment, urlString, opDate);
                key = comment + author;
                changeSet.add(cs);
            } else {
                String key1 = comment + author;
                if (key.equals(key1)) {
                    cs.add(fileName, operation, urlString);
                } else {
                    cs = new DimensionsChangeSet(fileName, author, operation, revision, comment, urlString, opDate);
                    key = comment + author;
                    changeSet.add(cs);
                }
            }

            // at this point we have a valid DimensionsChangeSet (cs) that has already been added
            // to the list (changeSet).  So now we will add all requests to the DimensionsChangeSet.
            List itemRequests = item.getChildRequests(null);

            for (int j = 0; j < itemRequests.size(); ++j) {
                DimensionsRelatedObject obj = (DimensionsRelatedObject) itemRequests.get(j);
                DimensionsObject relType = obj.getRelationship();
                if (SystemRelationship.IN_RESPONSE.equals(relType)) {
                    Request req = (Request) obj.getObject();

                    // Which attributes do I want.
                    req.queryAttribute(new int[] { SystemAttributes.TITLE, SystemAttributes.DESCRIPTION,
                        SystemAttributes.OBJECT_SPEC /* , SBM_ID, SBM_LINK */ });

                    //String requestUrl = (String) req.getAttribute(SBM_LINK);
                    //String objectId = (String) req.getAttribute(SBM_ID);

                    String objectId = (String) req.getAttribute(SystemAttributes.OBJECT_SPEC);
                    String requestUrl = constructRequestURL(objectId, url, getSCMDsn(), getSCMBaseDb());
                    String titlex = (String) req.getAttribute(SystemAttributes.TITLE);

                    cs.addRequest(objectId, requestUrl, titlex);
                    Logger.debug("Child Request Details IRT -" + objectId + " " + requestUrl + " " + titlex);
                } else {
                    Logger.debug("Child Request Details Ignored");
                }
            }
        }
        return changeSet;
    }

    /**
     * Sort the item list.
     */
    private static List getSortedItemList(List items) throws DimensionsRuntimeException {
        Collections.sort(items, new Comparator() {
            @Override
            public int compare(Object oa1, Object oa2) {
                int result = 0;
                try {
                    ItemRevision o1 = (ItemRevision) oa1;
                    ItemRevision o2 = (ItemRevision) oa2;

                    String a1 = (String) o1.getAttribute(SystemAttributes.REVISION_COMMENT);
                    String a2 = (String) o2.getAttribute(SystemAttributes.REVISION_COMMENT);

                    a1 += (String) o1.getAttribute(SystemAttributes.LAST_UPDATED_USER);
                    a2 += (String) o2.getAttribute(SystemAttributes.LAST_UPDATED_USER);

                    result = a1.compareTo(a2);
                } catch (Exception e) {
                    throw (DimensionsRuntimeException) new DimensionsRuntimeException(Values.exceptionMessage(
                            "Unable to sort item list", e, "no message")).initCause(e);
                }
                return result;
            }
        });
        return items;
    }

    /**
     * Sets the current project for the current user, which is deduced from the current thread.
     *
     * @param connection the connection for which to set the current project.
     * @param projectName the project to switch to, in the form PRODUCT NAME:PROJECT NAME.
     * @throws DimensionsRuntimeException
     */
    private static void setCurrentProject(DimensionsConnection connection, String projectName) {
        connection.getObjectFactory().setCurrentProject(projectName, false, "", "", null, true);
    }

    private static Project getCurrentProject(DimensionsConnection connection) {
        return connection.getObjectFactory().getCurrentUser().getCurrentProject();
    }

    static int[] getItemFileAttributes(boolean isDirectory) {
        if (isDirectory) {
            final int[] attrs = { SystemAttributes.OBJECT_SPEC, SystemAttributes.PRODUCT_NAME,
                SystemAttributes.OBJECT_ID, SystemAttributes.VARIANT, SystemAttributes.TYPE_NAME,
                SystemAttributes.REVISION, SystemAttributes.FULL_PATH_NAME, SystemAttributes.ITEMFILE_FILENAME,
                SystemAttributes.LAST_UPDATED_USER, SystemAttributes.FILE_VERSION,
                SystemAttributes.REVISION_COMMENT, SystemAttributes.LAST_UPDATED_DATE,
                SystemAttributes.CREATION_DATE };
            return attrs;
        }
        final int[] attrs = { SystemAttributes.PRODUCT_NAME, SystemAttributes.OBJECT_ID, SystemAttributes.VARIANT,
            SystemAttributes.TYPE_NAME, SystemAttributes.REVISION, SystemAttributes.ITEMFILE_FILENAME,
            SystemAttributes.LAST_UPDATED_USER, SystemAttributes.FILE_VERSION, SystemAttributes.LAST_UPDATED_DATE,
            SystemAttributes.CREATION_DATE };
        return attrs;
    }

    private static String preProcessSrcPath(String srcPath) {
        String path = srcPath.equals("/") ? "" : srcPath;
        if (!path.endsWith("/") && !path.equals("")) {
            path += "/";
        }
        // Note "\\/" because previous line adds "/" to "\\" (yuk!)
        if (path.equals("\\/") || path.equals("/")) {
            path = "";
        }
        if (path.indexOf('\\') != 0) {
            path = path.replace('\\', '/');
        }
        return path;
    }

    // URL encode a webclient path + spec for opening
    private static String constructURL(String spec, String url, String dsn, String db) {
        String urlString = "";
        if (spec != null && spec.length() > 0 && url != null && url.length() > 0) {
            String host = url;
            if (host.endsWith("/")) {
                host = host.substring(0, host.length() - 1);
            }
            if (host.startsWith("http:")) {
                host = host.substring(7, host.length());
            } else if (host.startsWith("https:")) {
                host = host.substring(8, host.length());
            }
            String page = "/dimensions/";
            String urlQuery = "jsp=api&command=openi&object_id=";
            urlQuery += spec;
            urlQuery += "&DB_CONN=";
            urlQuery += dsn;
            urlQuery += "&DB_NAME=";
            urlQuery += db;
            try {
                Logger.debug("Host URL - " + host + " " + page + " " + urlQuery);
                String urlStr = encodeUrl(host, page, urlQuery);
                Logger.debug("Change URL - " + urlStr);
                urlString = urlStr;
            } catch (Exception e) {
                Logger.debug("Malformed URL", e);
                return null;
            }
        }
        return urlString;
    }

    // URL encode a webclient path + spec for opening
    private static String constructRequestURL(String spec, String url, String dsn, String db) {
        String urlString = "";
        if (spec != null && spec.length() > 0 && url != null && url.length() > 0) {
            String host = url;
            if (host.endsWith("/")) {
                host = host.substring(0, host.length() - 1);
            }
            if (host.startsWith("http:")) {
                host = host.substring(7, host.length());
            } else if (host.startsWith("https:")) {
                host = host.substring(8, host.length());
            }
            String page = "/dimensions/";
            String urlQuery = "jsp=api&command=opencd&object_id=";
            urlQuery += spec;
            urlQuery += "&DB_CONN=";
            urlQuery += dsn;
            urlQuery += "&DB_NAME=";
            urlQuery += db;
            try {
                Logger.debug("Request Host URL - " + host + " " + page + " " + urlQuery);
                String urlStr = encodeUrl(host, page, urlQuery);
                Logger.debug("Request Change URL - " + urlStr);
                urlString = urlStr;
            } catch (Exception e) {
                Logger.debug("Malformed URL", e);
                return null;
            }
        }
        return urlString;
    }

    /**
     * Encode a URL correctly - handles spaces as %20.
     */
    private static String encodeUrl(String host, String page, String query)
            throws MalformedURLException, URISyntaxException {
        String urlStr = "";
        if (page != null && page.length() > 0 && host != null && host.length() > 0
                && query != null && query.length() > 0) {
            URI uri = new URI("http", host, page, query, null);
            urlStr = uri.toASCIIString();
        }
        return urlStr;
    }

    /**
     * Find items given a directory spec.
     */
    static List queryItems(DimensionsConnection connection, Project srcProject, String srcPath, Filter filter,
            int[] attrs, boolean isRecursive, boolean isLatest) {
        // Check srcPath validity check srcPath trailing slash do query.
        if (srcPath == null) {
            throw new IllegalArgumentException(MISSING_SOURCE_PATH);
        }
        if (srcProject == null) {
            throw new IllegalArgumentException(MISSING_PROJECT);
        }

        String path = preProcessSrcPath(srcPath);
        if (!(isRecursive && path.equals(""))) {
            filter.criteria().add(new Filter.Criterion(SystemAttributes.ITEMFILE_DIR,
                    (isRecursive ? path + '%' : path), 0));
        }

        if (isLatest) {
            filter.criteria().add(new Filter.Criterion(SystemAttributes.IS_LATEST_REV,
                    Boolean.TRUE, 0));
        }

        // Catch any exceptions that may be thrown by the Java API and for now return no changes.
        // Going forward it would be good to trap all the possible exception types and do something about them.
        try {
            long time0 = System.currentTimeMillis();
            List rels = srcProject.getChildItems(filter);
            long time1 = System.currentTimeMillis();
            if (Logger.isDebugEnabled()) {
                Logger.debug("queryItems() - Project(" + srcProject.getName() + ").getChildItems("
                        + Values.toString(filter) + ") found " + rels.size() + " rel(s) in " + (time1 - time0) + " ms");
            }
            if (rels.size() == 0) {
                return Collections.emptyList();
            }
            List items = new ArrayList(rels.size());
            for (int i = 0; i < rels.size(); ++i) {
                DimensionsRelatedObject rel = (DimensionsRelatedObject) rels.get(i);
                items.add(rel.getObject());
            }
            BulkOperator bo = connection.getObjectFactory().getBulkOperator(items);
            bo.queryAttribute(attrs);
            return items;
        } catch (Exception e) {
            Logger.debug("Caught exception", e);
            return Collections.emptyList();
        }
    }

    /**
     * Find items given a baseline/directory spec.
     */
    static List queryItems(DimensionsConnection connection, Baseline srcBaseline, String srcPath, Filter filter,
            int[] attrs, boolean isRecursive, boolean isLatest) {
        // Check srcPath validity check srcPath trailing slash do query.
        if (srcPath == null) {
            throw new IllegalArgumentException(MISSING_SOURCE_PATH);
        }
        if (srcBaseline == null) {
            throw new IllegalArgumentException(MISSING_BASELINE);
        }

        String path = preProcessSrcPath(srcPath);
        if (!(isRecursive && path.equals(""))) {
            filter.criteria().add(new Filter.Criterion(SystemAttributes.ITEMFILE_DIR,
                    (isRecursive ? path + '%' : path), 0));
        }

        if (isLatest) {
            filter.criteria().add(new Filter.Criterion(SystemAttributes.IS_LATEST_REV,
                    Boolean.TRUE, 0));
        }

        // Catch any exceptions that may be thrown by the Java API and for now return no changes.
        // Going forward it would be good to trap all the possible exception types and do something about them.
        try {
            Logger.debug("Looking for changed files in '" + path + "' in project: " + srcBaseline.getName());
            List rels = srcBaseline.getChildItems(filter);
            Logger.debug("Found " + rels.size());
            if (rels.size() == 0) {
                return null;
            }
            List items = new ArrayList(rels.size());
            for (int i = 0; i < rels.size(); ++i) {
                DimensionsRelatedObject rel = (DimensionsRelatedObject) rels.get(i);
                items.add(rel.getObject());
            }
            BulkOperator bo = connection.getObjectFactory().getBulkOperator(items);
            bo.queryAttribute(attrs);
            return items;
        } catch (Exception e) {
            String message = Values.exceptionMessage("Exception from the Java API querying items", e, "no message");
            Logger.debug(message, e);
            return null;
        }
    }

    /**
     * Find items given a request/directory spec.
     */
    static boolean queryItems(DimensionsConnection connection, Request request, String srcPath, List items,
            Filter filter, Project srcProject, boolean isRecursive, boolean isLatest) {
        // Check srcPath validity check srcPath trailing slash do query.
        if (srcPath == null) {
            throw new IllegalArgumentException(MISSING_SOURCE_PATH);
        }
        if (request == null) {
            throw new IllegalArgumentException(MISSING_REQUEST);
        }

        Logger.debug("Looking for items against request " + request.getName());

        String path = preProcessSrcPath((srcPath.equals("") ? "/" : srcPath));
        if (!(isRecursive && path.equals(""))) {
            filter.criteria().add(new Filter.Criterion(SystemAttributes.ITEMFILE_DIR,
                    (isRecursive ? path + '%' : path), 0));
        }

        if (isLatest) {
            filter.criteria().add(new Filter.Criterion(SystemAttributes.IS_LATEST_REV,
                    Boolean.TRUE, 0));
        }

        // Catch any exceptions that may be thrown by the Java API and for now return no changes.
        // Going forward it would be good to trap all the possible exception types and do something about them.
        try {
            Logger.debug("Looking for changed files in '" + path + "' in request: " + request.getName());
            request.queryChildItems(filter, srcProject);
            List rels = request.getChildItems(filter);
            Logger.debug("Found " + rels.size());
            if (rels.size() == 0) {
                return true;
            }
            for (int i = 0; i < rels.size(); ++i) {
                Logger.debug("Processing " + i + "/" + rels.size());
                DimensionsRelatedObject child = (DimensionsRelatedObject) rels.get(i);
                if (child != null && child.getObject() instanceof ItemRevision) {
                    Logger.debug("Found an item");
                    DimensionsObject relType = child.getRelationship();
                    if (SystemRelationship.IN_RESPONSE.equals(relType)) {
                        items.add(child.getObject());
                    }
                }
            }
            return true;
        } catch (Exception e) {
            String message = Values.exceptionMessage("Exception from the Java API querying items", e, "no message");
            Logger.debug(message, e);
            return false;
        }
    }

    /**
     * Flatten the list of related requests.
     */
    private boolean getDmChildRequests(Request request, List requestList) throws DimensionsRuntimeException {
        try {
            request.flushRelatedObjects(Request.class, true);
            request.queryChildRequests(null);
            List rels = request.getChildRequests(null);
            Logger.debug("Found " + rels.size());
            if (rels.size() == 0) {
                return true;
            }
            for (int i = 0; i < rels.size(); i++) {
                Logger.debug("Processing " + i + "/" + rels.size());
                DimensionsRelatedObject child = (DimensionsRelatedObject) rels.get(i);
                if (child != null && child.getObject() instanceof Request) {
                    Logger.debug("Found a request");
                    DimensionsObject relType = child.getRelationship();
                    if (SystemRelationship.DEPENDENT.equals(relType)) {
                        Logger.debug("Found a dependent request");
                        requestList.add(child.getObject());
                        if (!getDmChildRequests((Request) child.getObject(), requestList)) {
                            return false;
                        }
                    }
                } else {
                    Logger.debug("Related object was null or not a request " + (child != null));
                }
            }
            return true;
        } catch (Exception e) {
            String message = Values.exceptionMessage("Exception from the Java API querying child requests", e, "no message");
            Logger.debug(message);
            throw (DimensionsRuntimeException) new DimensionsRuntimeException(message).initCause(e);
        }
    }

    /**
     * Runs a Dimensions command.
     *
     * @param connection the connection for which to run the command
     * @param cmd the command line to run
     * @throws Exception if the command failed
     * @throws IllegalArgumentException if the command string was null or an empty string
     */
    static DimensionsResult run(DimensionsConnection connection, String cmd)
            throws IllegalArgumentException, DimensionsRuntimeException {
        if (cmd == null || cmd.equals("")) {
            throw new IllegalArgumentException(NO_COMMAND_LINE);
        }
        Logger.debug("Running the command '" + cmd + "'...");
        try {
            DimensionsObjectFactory dof = connection.getObjectFactory();
            DimensionsResult res = dof.runCommand(cmd);
            return res;
        } catch (Exception e) {
            String message = Values.exceptionMessage("Dimensions command '" + cmd + "' failed", e, "no message");
            Logger.debug(message);
            throw (DimensionsRuntimeException) new DimensionsRuntimeException(message).initCause(e);
        }
    }

    /**
     * Convert the human-readable <code>dateType</code> into a DMClient attribute name.
     * <p>
     * Defaults to {@link com.serena.dmclient.api.SystemAttributes#CREATION_DATE} if it is not recognized.
     *
     * @param dateType created, updated, revised or actioned.
     * @return the corresponding field value from {@link com.serena.dmclient.api.SystemAttributes}
     */
    static int getDateTypeAttribute(String dateType) {
        int ret = SystemAttributes.CREATION_DATE;
        if (dateType != null) {
            if (dateType.equalsIgnoreCase("edit")) { //$NON-NLS-1$
                ret = SystemAttributes.LAST_UPDATED_DATE;
            } else if (dateType.equalsIgnoreCase("actioned")) { //$NON-NLS-1$
                ret = SystemAttributes.LAST_ACTIONED_DATE;
            } else if (dateType.equalsIgnoreCase("revised")) { //$NON-NLS-1$
                ret = SystemAttributes.UTC_MODIFIED_DATE;
            } else if (dateType.equalsIgnoreCase("add")) { //$NON-NLS-1$
                ret = SystemAttributes.CREATION_DATE;
            }
        }
        return ret;
    }

    /**
     * Database times are in Oracle format, in a specified timezone.
     */
    static String formatDatabaseDate(Date date, TimeZone timeZone) {
        return (timeZone == null) ? DateUtils.format(date) : DateUtils.format(date, timeZone);
    }

    /**
     * Database times are in Oracle format, in a specified timezone.
     */
    static Date parseDatabaseDate(String date, TimeZone timeZone) {
        return (timeZone == null) ? DateUtils.parse(date) : DateUtils.parse(date, timeZone);
    }

    public boolean isStream(long key, String projectId) throws DimensionsRuntimeException {
        DimensionsConnection connection = getCon(key);
        if (connection == null) {
            throw new DimensionsRuntimeException("Not connected to an SCM repository");
        }
        return isStream(connection, projectId);
    }

    /**
     * Query what type of object a project is.
     *
     * @param connection Dimensions connection
     * @param projectName Name of the project
     * @return boolean
     */
    private boolean isStream(DimensionsConnection connection, final String projectId) {
        if (connection != null) {
            DimensionsObjectFactory fc = connection.getObjectFactory();
            Project proj = fc.getProject(projectId.toUpperCase(Values.ROOT_LOCALE));
            if (proj != null) {
                proj.queryAttribute(SystemAttributes.WSET_IS_STREAM);
                Boolean isStream = (Boolean) proj.getAttribute(SystemAttributes.WSET_IS_STREAM);
                if (isStream != null) {
                    return isStream;
                }
            }
        }
        return false;
    }

    /**
     * Populate list with all the items related to a set of requests.
     *
     * @param connection Dimensions connection
     * @param projectName Name of the project
     * @param requests List of requests
     * @param dateAfter Date filter
     * @param dateBefore Date filter
     * @return List
     * @throws DimensionsRuntimeException
     */
    public List getItemsInRequests(DimensionsConnection connection, final String projName, final String requests,
            final String dateAfter, final String dateBefore) throws DimensionsRuntimeException {
        List items = null;
        int[] attrs = getItemFileAttributes(true);

        if (requests != null && connection != null) {
            String[] reqStr;
            if (requests.indexOf(',') != -1) {
                reqStr = requests.split(",");
                Logger.debug("User specified " + reqStr.length + " requests");
            } else {
                reqStr = new String[1];
                reqStr[0] = requests;
            }

            // Set up filter for requests Name.
            List requestList = new ArrayList(1);
            items = new ArrayList(1);

            Filter filter = new Filter();

            filter.criteria().add(new Filter.Criterion(SystemAttributes.IS_EXTRACTED, "Y", Filter.Criterion.NOT)); //$NON-NLS-1$
            filter.orders().add(new Filter.Order(SystemAttributes.REVISION_COMMENT, Filter.ORDER_ASCENDING));
            filter.orders().add(new Filter.Order(SystemAttributes.ITEMFILE_DIR, Filter.ORDER_ASCENDING));
            filter.orders().add(new Filter.Order(SystemAttributes.ITEMFILE_FILENAME, Filter.ORDER_ASCENDING));

            if (dateAfter != null) {
                if (!isStream(connection, projName)) {
                    filter.criteria().add(new Filter.Criterion(SystemAttributes.LAST_UPDATED_DATE, dateAfter, Filter.Criterion.GREATER_EQUAL));
                } else {
                    filter.criteria().add(new Filter.Criterion(SystemAttributes.CREATION_DATE, dateAfter, Filter.Criterion.GREATER_EQUAL));
                }
            }

            if (dateBefore != null) {
                if (!isStream(connection, projName)) {
                    filter.criteria().add(new Filter.Criterion(SystemAttributes.LAST_UPDATED_DATE, dateBefore, Filter.Criterion.LESS_EQUAL));
                } else {
                    filter.criteria().add(new Filter.Criterion(SystemAttributes.CREATION_DATE, dateBefore, Filter.Criterion.LESS_EQUAL));
                }
            }

            for (String xStr : reqStr) {
                xStr = xStr.trim();
                Logger.debug("Request to process is \"" + xStr + "\"");
                Request requestObj = connection.getObjectFactory().findRequest(xStr.toUpperCase(Values.ROOT_LOCALE));

                if (requestObj != null) {
                    Logger.debug("Request to process is \"" + requestObj.getName() + "\"");
                    requestList.add(requestObj);
                    // Get all the children for this request
                    if (!getDmChildRequests(requestObj, requestList)) {
                        throw new DimensionsRuntimeException("Could not process request \"" + requestObj.getName()
                                + "\" children in repository");
                    }
                    Logger.debug("Request has " + requestList.size() + " elements to process");
                    Project projectObj = connection.getObjectFactory().getProject(projName);
                    for (int i = 0; i < requestList.size(); i++) {
                        Request req = (Request) requestList.get(i);
                        Logger.debug("Request " + i + " is \"" + req.getName() + "\"");
                        if (!queryItems(connection, req, "/", items, filter, projectObj, true, allRevisions)) {
                            throw new DimensionsRuntimeException("Could not process items for request \""
                                    + req.getName() + "\"");
                        }
                    }

                    Logger.debug("Request has " + items.size() + " items to process");
                    BulkOperator bo = connection.getObjectFactory().getBulkOperator(items);
                    bo.queryAttribute(attrs);
                }
            }
        }
        return items;
    }
}
