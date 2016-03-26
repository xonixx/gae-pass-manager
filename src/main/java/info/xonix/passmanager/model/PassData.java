package info.xonix.passmanager.model;

import java.util.Date;

/**
 * User: xonix
 * Date: 26.03.16
 * Time: 22:33
 */
public class PassData {
    public final String data;
    public final Date lastUpdated;

    public PassData(String data, Date lastUpdated) {
        this.data = data;
        this.lastUpdated = lastUpdated;
    }
}
