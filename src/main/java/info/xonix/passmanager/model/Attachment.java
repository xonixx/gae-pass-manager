package info.xonix.passmanager.model;

import java.util.Date;

/**
 * User: xonix
 * Date: 26.03.16
 * Time: 22:33
 */
public class Attachment {
    public final String data;
    public final Date uploaded;

    public Attachment(String data, Date uploaded) {
        this.data = data;
        this.uploaded = uploaded;
    }
}
