package civicloop.model;

import java.io.Serializable;
import java.util.UUID;


public class Service implements Creditable, Serializable {
    private String serviceId;
    private String serviceType;  
    private String providerId;
    private boolean isAvailable;  


    public Service(String serviceType, String providerId) {
        this.serviceId = UUID.randomUUID().toString().substring(0, 8);
        this.serviceType = serviceType;
        this.providerId = providerId;
        this.isAvailable = true;
    }
// ---------- Creditable interface ----------
    @Override
    public double getCreditRate() {
        return 1.0;   // 1 hour of active work = 1 TimeCredit
    }

    @Override
    public String getOfferType() {
        return "Service";
    }

// ---------- Getters & business methods ----------

    public String getServiceId() { return serviceId; }
    public String getServiceType() { return serviceType; }
    public String getProviderId() { return providerId; }
    public boolean isAvailable() { return isAvailable; }

    public void markBusy() { this.isAvailable = false; }
    public void markAvailable() { this.isAvailable = true; }
}
// done

