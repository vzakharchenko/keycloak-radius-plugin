package com.github.vzakharchenko.radius.radius.handlers.session;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PasswordData {
    private String password;
    private boolean session;

    private PasswordData(String password) {
        this.password = password;
    }

    private PasswordData(String password, boolean session) {
        this.password = password;
        this.session = session;
    }

    public String getPassword() {
        return password;
    }

    public boolean isSessionPassword() {
        return session;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PasswordData that = (PasswordData) o;

        return new EqualsBuilder().append(session, that.session).append(password, that.password).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(password).append(session).toHashCode();
    }

    public static PasswordData create(String password){
        return new PasswordData(password);
    }
    public static PasswordData create(String password, boolean session){
        return new PasswordData(password, session);
    }


}
