package kz.gov.pki.samplebundle.common;

import kz.gov.pki.osgi.layer.api.NCALayerService;
import org.osgi.framework.*;
import org.osgi.util.tracker.ServiceTracker;
import java.security.Provider;

/**
 * @author aslan
 * Provides Kalkancrypt provider. You may exclude this class,
 * if you prefer the standard way to add the provider.
 */
public enum BundleProvider {
    KALKANCRYPT;

    private static final BundleContext CONTEXT = FrameworkUtil.getBundle(BundleProvider.class).getBundleContext();
    private NCALayerService ncaLayerService;
    private Provider provider;

    public void discoverProviderService() throws InvalidSyntaxException {
        String serviceFilter = "(objectClass=" + NCALayerService.class.getName() + ")";
        ServiceTracker<NCALayerService, NCALayerService> serviceTracker =
                new ServiceTracker(CONTEXT, CONTEXT.createFilter(serviceFilter), null);
        serviceTracker.open();
        ncaLayerService = serviceTracker.getService();
        if (ncaLayerService == null) {
            CONTEXT.addServiceListener((e) -> {
                if (e.getType() == ServiceEvent.REGISTERED) {
                    ncaLayerService = serviceTracker.getService((ServiceReference<NCALayerService>) e.getServiceReference());
                    provider = ncaLayerService.getProvider();
                }
            }, serviceFilter);
        } else {
            provider = ncaLayerService.getProvider();
        }
    }

    public Provider getProvider(){
        return provider;
    }
}
