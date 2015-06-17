package com.dsr_company.max.wifilocator;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.List;

/**
 * Created by max on 11.06.2015.
 */
public class WiFiScanInfo {
    String BSSID;
    int count;
    int minDbm;
    int maxDbm;
    double avgDbm;
    double disperisonDbm;

    public WiFiScanInfo(String BSSID, List<Integer> signals){
        this.BSSID = BSSID;
        if (signals != null && signals.size() > 0) {
            minDbm = maxDbm = signals.get(0);
        } else {
            throw new InvalidParameterException("Signals list can't be empty");
        }
        this.count = signals.size();
        int sum = 0;

        for (int sig:signals){
            sum += sig;
            if (sig > maxDbm) {
                maxDbm = sig;
            } else if (sig < minDbm) {
                minDbm = sig;
            }
        }
        avgDbm = sum/count;

        float tmp = 0;
        for (int sig:signals){
            tmp += Math.pow(avgDbm - sig, 2);
        }
        disperisonDbm = Math.sqrt(tmp/count);
    }

    @Override
    public String toString() {
        return String.format("BSSID: %s, min: %d, max: %d, avg: %f, disperison: %f", BSSID, minDbm, maxDbm, avgDbm, disperisonDbm);
    }
}
