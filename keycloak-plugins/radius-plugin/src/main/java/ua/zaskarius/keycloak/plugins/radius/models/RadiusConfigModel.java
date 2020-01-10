package ua.zaskarius.keycloak.plugins.radius.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "RADIUS_CONFIGURATION", uniqueConstraints = {
})
public class RadiusConfigModel {
    @Id
    @Column(name = "RADIUS_ID", length = 36)
    private String id;

    @Column(name = "AUTO_START")
    private boolean start;

    @Column(name = "AUTH_PORT")
    private int authPort;


    @Column(name = "ACCOUNT_PORT")
    private int accountPort;

    @Column(name = "LAST_MODIFICATION")
    private Date mDate;

    @Column(name = "LAST_MODIFICATION_USER_ID")
    private String mUserId;

    @Column(name = "RADIUS_SHARED")
    private String radiusShared;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public int getAuthPort() {
        return authPort;
    }

    public void setAuthPort(int authPort) {
        this.authPort = authPort;
    }

    public int getAccountPort() {
        return accountPort;
    }

    public void setAccountPort(int accountPort) {
        this.accountPort = accountPort;
    }

    public Date getmDate() {
        return mDate != null ? (Date) mDate.clone() : null;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate != null ? (Date) mDate.clone() : null;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getRadiusShared() {
        return radiusShared;
    }

    public void setRadiusShared(String radiusShared) {
        this.radiusShared = radiusShared;
    }
}

