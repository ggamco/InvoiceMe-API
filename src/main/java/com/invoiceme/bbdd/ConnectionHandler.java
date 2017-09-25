package com.invoiceme.bbdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.invoiceme.exceptions.BackendDAOException;
import com.invoiceme.utiles.PropertyUtil;
import com.jolbox.bonecp.BoneCPDataSource;

public class ConnectionHandler {
	
    private static final int MAX_CONNS = 10;
    private static final int STEP_CONNECTIONS = 5;
    private static final int WAITING_INTERVAL = 10;
    private static final long MAX_CON_AGE = 30;

    private static Logger logger = Logger.getLogger(ConnectionHandler.class);

    public static String DB_SCHEME = PropertyUtil.getProperty("scheme.mandarina");

    /** Datasource creado por el pool de conexiones. */
    private static DataSource cpdsMandarina = null;

    /** Precarga de conexiones */
    public static void init() {
        getDS(DB_SCHEME);
    }

    /** Evento de apagado. Cierra todas las conexiones */
    public static void shutdown() {
        destroyDatasource((BoneCPDataSource) cpdsMandarina);
    }

    /**
     * Cierra los objetos query de base de datos.
     *
     * @param rs ResultSet.
     * @param ps PreparedStatement.
     * @throws BackendDAOException .
     */
    public static void closeDatabaseObjects(ResultSet rs, PreparedStatement ps) throws BackendDAOException {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException e) {
            logger.error("An error ocurred while tried to close the database query objects: " + e.getMessage(), e);
            throw new BackendDAOException(e.getMessage(), e);
        }
    }

    /**
     * Cierra la conexion con la base de datos.
     *
     * @param connection Connection object.
     */
    public static void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        } catch (Exception e) {
            logger.error("An error ocurred while tried to close connection: " + e.getMessage(), e);
        }
    }

    /**
     * Rollback operations.
     *
     * @param connection Connection object.
     */
    public static void rollback(Connection connection) {
        try {
            if (connection != null) {
                connection.rollback();
            }
        } catch (SQLException e) {
            logger.error("An error ocurred while tried to rollback connection: " + e.getMessage(), e);
        }
    }

    /**
     * Commit operations.
     *
     * @param connection Connection object.
     * @throws BackendDAOException .
     */
    public static void commit(Connection connection) throws BackendDAOException {
        try {
            if (connection != null) {
                connection.commit();
            }
        } catch (SQLException e) {
            logger.error("An error ocurred while tried to rollback connection: " + e.getMessage(), e);
            throw new BackendDAOException(e.getMessage(), e);
        }
    }

    /**
     * Get number of leased connections.
     *
     * @return number of leased connections, -1 if connections pool is not created yet.
     */
    public static int size() {
        int result = -1;
        if (cpdsMandarina != null) {
            try {
                result = ((BoneCPDataSource) cpdsMandarina).getTotalLeased();
            } catch (NullPointerException e) {
                result = 0;
            }
        }
        return result;
    }

    /**
     * Gives access to datasource.
     *
     * @return datasource.
     */
    private static synchronized DataSource getDS(String scheme) {
        if (DB_SCHEME.equals(scheme)) {
            if (cpdsMandarina == null) {
                String dbUrl = PropertyUtil.getProperty("database.url");
                cpdsMandarina = getDataSource(dbUrl);
            }
            return cpdsMandarina;
        }
        return null;
    }

    private static synchronized DataSource getDataSource(String dbUrl) {
        BoneCPDataSource cpds = null;
        try {
            logger.debug("[ConnectionManager.getDS]. Creating datasource for " + dbUrl);
            String user = PropertyUtil.getProperty("database.user");
            String password = PropertyUtil.getProperty("database.password");
            String driver = PropertyUtil.getProperty("database.driver");
            String maxConnections = PropertyUtil.getProperty("database.maxConnections");
            int maxConns;
            try {
                maxConns = Integer.parseInt(maxConnections);
            } catch (NumberFormatException e) {
                maxConns = MAX_CONNS;
            }

            if (driver != null) {
                Class.forName(driver); // load the DB driver
                cpds = new BoneCPDataSource();

                cpds.setJdbcUrl(dbUrl);
                cpds.setUsername(user);
                cpds.setPassword(password);

                cpds.setMinConnectionsPerPartition(STEP_CONNECTIONS);
                cpds.setMaxConnectionsPerPartition(maxConns);
                cpds.setAcquireIncrement(STEP_CONNECTIONS);
                cpds.setMaxConnectionAgeInSeconds(MAX_CON_AGE);
            }
        } catch (ClassNotFoundException ex) {
            logger.error("", ex);
        }
        return cpds;
    }

    /** Close datasource and its connections. */
    private static void destroyDatasource(BoneCPDataSource dataSource) {
        if (dataSource != null) {
            logger.debug("[ConnectionManager.destroyDatasource]. Close datasource");
            dataSource.close();
            dataSource = null;
        }
    }

    /**
     * Opens the connection if closed.
     *
     * @return Connection object.
     * @throws BackendDAOException .
     */
    public Connection getConnection(String scheme) throws BackendDAOException {
        logger.debug("Begin getConnection()");
        Connection connection = null;
        try {
            DataSource ds = getDS(scheme);
            if (ds != null) {
                while (connection == null) {
                    connection = ds.getConnection();
                    if (connection != null) {
                        break;
                    }
                    logger.debug("[ConnectionManager.getConnection]. Waiting for a connection. " + size() + " open.");
                    try {
                        Thread.sleep(WAITING_INTERVAL);
                    } catch (InterruptedException ex) {
                        logger.error("[ConnectionManager.getConnection]", ex);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new BackendDAOException(e.getMessage(), e);
        }

        logger.debug("End getConnection()");
        return connection;
    }
}
