package com.github.vzakharchenko.radius.dm.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "RADIUS_DM_CLIENT_END_SESSION")
public class DMClientEndModel {

    @Id
    @Column(name = "SESSION_ID", length = 128)
    @Access(AccessType.PROPERTY)
    private String id;

    @Column(name = "END_CAUSE")
    private String endCause;

    @Column(name = "MODIFY_DATE")
    @Version
    private Date modifyDate;

    @Column(name = "END_DATE")
    private Date endDate;

    public Date getEndDate() {
        return endDate == null ? null : (Date) endDate.clone();
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate == null ? null : (Date) endDate.clone();
    }

    public Date getModifyDate() {
        return modifyDate == null ? null : (Date) modifyDate.clone();
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate == null ? null : (Date) modifyDate.clone();
    }

    public String getEndCause() {
        return endCause;
    }

    public void setEndCause(String endCause) {
        this.endCause = endCause;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
