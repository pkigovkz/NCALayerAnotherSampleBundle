package kz.gov.pki.samplebundle.common;

import org.osgi.framework.*;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author aslan
 * LoggingService logs to ncalayer.log
 */
public enum BundleLog {
	LOG;

	private static final BundleContext CONTEXT = FrameworkUtil.getBundle(BundleLog.class).getBundleContext();
	private LogService logService;

	public void discoverLogService() throws InvalidSyntaxException {
		String logServiceFilter = "(objectClass=" + LogService.class.getName() + ")";
		ServiceTracker<LogService, LogService> logServiceTracker =
				new ServiceTracker(CONTEXT, CONTEXT.createFilter(logServiceFilter), null);
		logServiceTracker.open();
		logService = logServiceTracker.getService();
		if (logService == null) {
			CONTEXT.addServiceListener((e) -> {
				if (e.getType() == ServiceEvent.REGISTERED) {
					logService = logServiceTracker.getService((ServiceReference<LogService>) e.getServiceReference());
					info("Found LogService while listening");
				}
			}, logServiceFilter);
		} else {
			info("Found LogService on load");
		}
	}

	public void info(String msg) {
		if (logService != null) {
			log(LogService.LOG_INFO, msg);
		} else {
			System.out.println(msg);
		}
	}

	public void debug(String msg) {
		if (logService != null) {
			log(LogService.LOG_DEBUG, msg);
		} else {
			System.out.println(msg);
		}
	}

	public void error(String msg, Throwable e) {
		if (logService != null) {
			log(LogService.LOG_ERROR, msg, e);
		} else {
			System.err.println(msg);
			e.printStackTrace();
		}
	}

	public void log(int level, String msg) {
		if (logService != null) {
			logService.log(level, msg);
		} else {
			System.out.println(msg);
		}
	}

	public void log(int level, String msg, Throwable e) {
		if (logService != null) {
			logService.log(level, msg, e);
		} else {
			System.err.println(msg);
			e.printStackTrace();
		}
	}
}
