package kz.gov.pki.samplebundle;

import java.time.OffsetDateTime;
import org.json.JSONObject;
import kz.gov.pki.osgi.layer.annotations.NCALayerClass;
import kz.gov.pki.osgi.layer.annotations.NCALayerMethod;
import kz.gov.pki.osgi.layer.exception.CommonException;
import kz.gov.pki.samplebundle.exception.SampleFailure;

/*
 * CommonInvoker registers classes annotated with @NCALayerClass
 */
@NCALayerClass
public class SampleNLClass {
    
    /*
     * CommonInvoker invokes methods annotated with @NCALayerMethod 
     */
    /* JSON
    {
      "apiVersion": 2, -- optional, default value is 2
      "module": "kz.gov.pki.SampleBundle",
      "class": "SampleNLClass", -- optional, required if there are 2 or more classes
      "method": "salemAit",
      "args": {
        "firstname": "Naruto",
        "lastname": "Uzumaki"       
      }
    }
    */
    @NCALayerMethod
    public String salemAit(String jsonString) {
        JSONObject jsonArgs = new JSONObject(jsonString);
        String firstname = jsonArgs.optString("firstname");
        String lastname = jsonArgs.optString("lastname");
        // throw a detailed exception
        if (firstname.isEmpty()) {
            throw new CommonException(SampleFailure.EMPTY_FIRSTNAME);
        }
        return String.format("Salem, %s %s", firstname, lastname);
    }
    
    /*
     * In addition a method may have an alias,
     * i.e. both names of the method
     * { "method": "obtainCurrentDateTime" }
     * or
     * { "method": "now" }
     * are allowed.
     * 
     */    
    /* JSON
    {
      "module": "kz.gov.pki.SampleBundle",
      "method": "now"
    }
        or
    {
      "module": "kz.gov.pki.SampleBundle",
      "method": "obtainCurrentDateTime"
    }
    */
    @NCALayerMethod(name = "now")
    public OffsetDateTime obtainCurrentDateTime() {
        // CommonInvoker wraps a returning object using JSONObject.wrap(object)  
        return OffsetDateTime.now();
    }
    
}
