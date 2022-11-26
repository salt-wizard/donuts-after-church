package io.salt.wizard.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Wrapper for the HikariDataSource object.
 */
public class HikariCPProvider {
    private static final Logger _logger = LoggerFactory.getLogger(HikariCPProvider.class);
    private static HikariDataSource _hikariDataSource;

    private HikariCPProvider() {}
    
    public static void init(Properties _properties){
    	_logger.trace("*********************************************");
    	_logger.trace("*** Entering ---> init() ***");
        _logger.info("Creating HikariConfig for HikariCPProvider...");
        
        // Convert json config to properties for HikariCP
        HikariConfig _hikariConfig = new HikariConfig(_properties);
        _hikariDataSource = new HikariDataSource(_hikariConfig);
        
        _logger.trace("*** Exiting ---> init() ***");
        _logger.trace("*********************************************");
    }

    public static Connection getConnection() throws SQLException {
        return _hikariDataSource.getConnection();
    }
    
    public static void shutdown() {
    	_logger.trace("*********************************************");
    	_logger.trace("*** Entering ---> init() ***");
    	_hikariDataSource.close();
    	if(_hikariDataSource.isClosed()) {
    		_logger.info("Closed connection to datasource.");
    	} else {
    		_logger.error("Unable to close datasource connection.");
    	}
    	_logger.trace("*** Exiting ---> init() ***");
    	_logger.trace("*********************************************");
    }
}

