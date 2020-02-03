package com.github.vzakharchenko.radius.radius.handlers.otp;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class OtpClock {
    private final int interval;
    private Calendar calendar;

    public OtpClock(int interval) {
        this.interval = interval;
    }

    public long getCurrentInterval() {
        Calendar currentCalendar = this.calendar;

        if (currentCalendar == null) {
            currentCalendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        }

        return (currentCalendar.getTimeInMillis() / 1000) / this.interval;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}
