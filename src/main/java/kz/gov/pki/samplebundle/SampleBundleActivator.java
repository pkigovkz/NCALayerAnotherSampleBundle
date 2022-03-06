package kz.gov.pki.samplebundle;

import java.util.Hashtable;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import kz.gov.pki.osgi.layer.api.ModuleService;
import static kz.gov.pki.samplebundle.common.BundleLog.LOG;
import static kz.gov.pki.samplebundle.common.BundleProvider.KALKANCRYPT;

/**
 * @author aslan
 * The activator is a main class, which registers your implementation of ModuleService.
 */
public class SampleBundleActivator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		try {
		    // You may leave the next strings, if you need any of these services
		    // obtain the provider from a system bundle
            KALKANCRYPT.discoverProviderService();
            // obtain the logger from a system bundle
            LOG.discoverLogService();

            Hashtable<String, String> props = new Hashtable<>();
            // Your module name might differ from SymbolicName. It's up to you.
            props.put("module", "kz.gov.pki.SampleBundle");
            context.registerService(ModuleService.class.getName(), new SampleModuleService(), props);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new IllegalStateException("Can not start bundle SampleBundle!");
        }
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// not required so far		
	}

}
