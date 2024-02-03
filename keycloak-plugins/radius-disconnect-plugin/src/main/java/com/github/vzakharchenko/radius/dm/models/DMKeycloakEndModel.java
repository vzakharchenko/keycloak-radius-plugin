package com.github.vzakharchenko.radius.dm.models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "RADIUS_DM_KEYCLOAK_END_SESSION")
public class DMKeycloakEndModel {

    @Id
    @Column(name = "SESSION_ID", length = 128)
    @Access(AccessType.PROPERTY)
    private String id;

    @Column(name = "END_STATUS")
    private String endStatus;

    @Column(name = "END_MESSAGE")
    private String endMessage;

    @Column(name = "END_ATTEMPTS")
    private Integer attempts;

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

    public String getEndStatus() {
        return endStatus;
    }

    public void setEndStatus(String endStatus) {
        this.endStatus = endStatus;
    }

    public String getEndMessage() {
        return endMessage;
    }

    public void setEndMessage(String endMessage) {
        this.endMessage = endMessage;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
