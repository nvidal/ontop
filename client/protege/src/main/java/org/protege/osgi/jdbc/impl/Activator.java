package org.protege.osgi.jdbc.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.protege.osgi.jdbc.JdbcRegistry;
import org.protege.osgi.jdbc.OSGiJdbcDriver;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Hashtable;

public class Activator implements BundleActivator {
    private OSGiJdbcDriver driver;

	@Override
    public void start(BundleContext context) throws SQLException {
		try {
            JdbcRegistry registry = new JdbcRegistryImpl();
			context.registerService(JdbcRegistry.class.getName(), registry, new Hashtable<String, String>());
			driver = new OSGiJdbcDriver(context, registry);
			DriverManager.registerDriver(driver);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
	}

    @Override
	public void stop(BundleContext context) throws Exception {
		DriverManager.deregisterDriver(driver);
		driver = null;
	}


}
