package es.gmbdesign.invoiceme.bbdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.jolbox.bonecp.BoneCPDataSource;

import es.gmbdesign.invoiceme.exceptions.BackendDAOException;
import es.gmbdesign.invoiceme.utiles.PropertyUtil;

public class ConnectionHandler {
	
    private static final int MAX_CONNS = 10;
    private static final int STEP_CONNECTIONS = 5;
    private static final int WAITING_INTERVAL = 10;
    private static final long MAX_CON_AGE = 30;

    private static Logger logger = Logger.getLogger(ConnectionHandler.class);

    public static String DB_SCHEME = PropertyUtil.getProperty("scheme");

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
            logger.error("Ha ocurrido un error mientras se intentaba cerarr los objetos query: " + e.getMessage(), e);
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
            logger.error("Ha ocurrido un error mientras es intentaba cerrar la conexión: " + e.getMessage(), e);
        }
    }

    /**
     * Rollback.
     *
     * @param connection Connection object.
     */
    public static void rollback(Connection connection) {
        try {
            if (connection != null) {
                connection.rollback();
            }
        } catch (SQLException e) {
            logger.error("Ha ocurrido un error mientras se intentaba un rollback: " + e.getMessage(), e);
        }
    }

    /**
     * Commit.
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
            logger.error("Ha ocurrido un error mientras se intentaba un commit: " + e.getMessage(), e);
            throw new BackendDAOException(e.getMessage(), e);
        }
    }

    /**
     * Recupera el numero de conexiones creadas.
     *
     * @return numero de conexiones, -1 si el pool no se ha creado aún.
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
     * Da acceso al DataSource.
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
            logger.debug("[ConnectionManager.getDS]. Creando dataSource para " + dbUrl);
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
                Class.forName(driver);
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

    /** Cierra DataSource y sus conexiones. */
    private static void destroyDatasource(BoneCPDataSource dataSource) {
        if (dataSource != null) {
            logger.debug("[ConnectionManager.destroyDatasource]. Cerrando DataSource");
            dataSource.close();
            dataSource = null;
        }
    }

    /**
     * Abra la conexión si está cerrada.
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
                    logger.debug("[ConnectionManager.getConnection]. Esperando por una conexión. " + size() + " open.");
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
