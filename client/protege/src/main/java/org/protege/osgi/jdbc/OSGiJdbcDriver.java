package org.protege.osgi.jdbc;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class OSGiJdbcDriver implements Driver {
	private int majorVersion;
	private int minorVersion;
	private JdbcRegistry registry;
	
	public OSGiJdbcDriver(BundleContext context, JdbcRegistry registry) {
		String versionString = (String) context.getBundle().getHeaders().get(Constants.BUNDLE_VERSION);
		Version version = new Version(versionString);
		majorVersion = version.getMajor();
		minorVersion = version.getMinor();
		this.registry = registry;
	}

	public boolean acceptsURL(String url) throws SQLException {
        for (Driver delegate : registry.getJdbcDrivers()) {
			if (delegate.acceptsURL(url)) {
				return true;
			}
		}
		return false;
	}

	public Connection connect(String url, Properties info) throws SQLException {
		for (Driver delegate : registry.getJdbcDrivers()) {
			if (delegate.acceptsURL(url)) {
				return delegate.connect(url, info);
			}
		}
		return null;
	}

	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
			throws SQLException {
		for (Driver delegate : registry.getJdbcDrivers()) {
			if (delegate.acceptsURL(url)) {
				return delegate.getPropertyInfo(url, info);
			}
		}
		return null;
	}

    @Override
	public boolean jdbcCompliant() {
        return registry.getJdbcDrivers().stream().allMatch(Driver::jdbcCompliant);
	}

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    public int getMajorVersion() {
		return majorVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

}
