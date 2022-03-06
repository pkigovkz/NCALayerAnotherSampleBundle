package kz.gov.pki.samplebundle;

import org.json.JSONObject;
import kz.gov.pki.osgi.layer.api.ModuleService;
import kz.gov.pki.osgi.layer.nlcommons.CommonInvoker;

/**
 * @author aslan
 * It is just an example. You can implement everything in your own way!
 * Please, check Import-Package in osgi.properties. If you need specific
 * packages add ones. Remove unused packages.
 * If you'd like to expose your packages to other bundles,
 * use Export-Package property.
 */
public class SampleModuleService implements ModuleService {
    
    private CommonInvoker invoker;
    
    public SampleModuleService() {
        // Register a class annotated with @NCALayerClass 
        this.invoker = new CommonInvoker(new SampleNLClass());
        // or a list of classes
        // this.invoker = new CommonInvoker(Arrays.asList(new SampleNLClass(), new AnotherSampleNLClass()));
        // In this case class-name per method is required, e.g. 
        // { "class": "AnotherSampleNLClass" }
    }

    @Override
    public String process(String jsonString, String jsonAddInfo) {
        // in order to log to ncalayer.log
        // import static kz.gov.pki.samplebundle.common.BundleLog.LOG;
        // LOG.info("something");
        String ret;
        
        JSONObject jsonAddInfoObj = new JSONObject(jsonAddInfo);
        // You can refuse a connection, if an origin is not your domain
        String jsonOrigin = jsonAddInfoObj.getString("origin");
        if (!"https://nca.pki.gov.kz".equalsIgnoreCase(jsonOrigin)) {
           ret = invoker.buildJSONErrorResponse(new IllegalAccessException("An access to the bundle is restricted!"));
        } else {
           ret = invoker.invoke(jsonString);
        }
        return ret;
    }

}
