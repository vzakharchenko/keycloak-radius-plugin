package com.github.vzakharchenko.radius.radius.handlers.otp;

import org.testng.annotations.Test;

import java.util.Calendar;

import static org.testng.Assert.assertTrue;

public class OtpClockTest {
    @Test
    public void test1() {
        OtpClock otpClock = new OtpClock(1);
        assertTrue(otpClock.getCurrentInterval() > 1);
    }

    @Test
    public void test2() {
        OtpClock otpClock = new OtpClock(1);
        otpClock.setCalendar(Calendar.getInstance());
        assertTrue(otpClock.getCurrentInterval() > 1);
    }


}
