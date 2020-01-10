package ua.zaskarius.keycloak.plugins.radius.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "RADIUS_REALM_PROPERTY", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"KEY"}),
})
public class RadiusParameterModel {
    @Id
    @Column(name = "RADIUS_PARAMETER_ID", length = 36)
    private String id;

    @Column(name = "KEY")
    private String key;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "CONFIG_ID", length = 36)
    private String configId;

    @Column(name = "LAST_MODIFICATION")
    private Date mDate;

    @Column(name = "LAST_MODIFICATION_USER_ID")
    private String mUserId;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
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
}
