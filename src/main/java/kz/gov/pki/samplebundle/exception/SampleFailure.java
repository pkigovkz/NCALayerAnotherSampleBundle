package kz.gov.pki.samplebundle.exception;

import kz.gov.pki.osgi.layer.exception.Failure;

public enum SampleFailure implements Failure {

    EMPTY_FIRSTNAME("Firstname is empty!");
    
    private String message;
    
    private SampleFailure(String message) {
        this.message = message;
    }
    
    public String getCode() {
        return this.name();
    }
    
    public String getMessage() {
        return message;
    }

}
