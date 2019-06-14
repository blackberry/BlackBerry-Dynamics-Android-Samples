/*
 * (c) 2017 BlackBerry Limited. All rights reserved.
 */
package com.good.automated.general.utils;

/**
 * Storage for durations (timeouts) for common operations.
 * <p>
 * The usage of this enum is the following:
 * {@code Duration.of(Duration.PROVISIONING) }
 * <p>
 * With static import it look much more declarative:
 * {@code Duration.of(PROVISIONING) }
 */
public enum Duration {

    SECONDS_10(10, TimeUnit.SECONDS),

    // Duration of activation of container via access key / auth delegation
    PROVISIONING(1, TimeUnit.MINUTES),

    AUTHORIZE_CALLBACK(SECONDS_10),
    SIMULATION_MODE_PROVISION(20, TimeUnit.SECONDS),

    // Duration until container will be locked
    APPLICATION_LOCKED(1, TimeUnit.MINUTES),


    // Duration after unlock key input
    APPLICATION_UNLOCKED(1, TimeUnit.MINUTES),

    // Duration of flipping application screens
    ICC_FLIP(3, TimeUnit.SECONDS),

    // Duration of auth delegation
    AUTH_DELEGATION(1, TimeUnit.MINUTES),

    // Duration of easy activation
    EASY_ACTIVATION(1, TimeUnit.MINUTES),

    // Delay after click on "Accept" button
    ACCEPTING_PASSWORD(SECONDS_10),

    // Wait duration of UI changes
    UI_WAIT(5000, TimeUnit.MILLISECONDS),

    // Duration of performing UI action like click or swipe
    UI_ACTION(200, TimeUnit.MILLISECONDS),

    SHORT_UI_ACTION(50, TimeUnit.MILLISECONDS),

    // Duration of waiting for screen refresh
    WAIT_FOR_SCREEN(1, TimeUnit.SECONDS),

    // Wait for Policy Update
    POLICY_UPDATE(15, TimeUnit.SECONDS),

    // Wait for 30 seconds
    SECONDS_30(30, TimeUnit.SECONDS),

    // Wait duration of UI changes
    MINUTE_1(1, TimeUnit.MINUTES),

    // Wait duration of UI changes
    MINUTE_2(2, TimeUnit.MINUTES),

    // Wait duration of UI changes
    MINUTE_3(3, TimeUnit.MINUTES),

    MINUTES_20(20, TimeUnit.MINUTES),

    MINUTES_15(15, TimeUnit.MINUTES),

    MINUTES_10(10, TimeUnit.MINUTES),

    // Duration of communication to NOC
    NOC_CONNECTION(80, TimeUnit.SECONDS),

    //Duration of screen rotation
    SCREEN_ROTATION(2, TimeUnit.SECONDS),

    //Duration of receiving PKCS12 UI
    PKCS12_UI(60, TimeUnit.SECONDS),

    KNOWN_WIFI_CONNECTION(SECONDS_10);

    private int timeout;

    Duration(int timeout, TimeUnit unit) {
        this.timeout = unit.toMillis(timeout);
    }

    Duration(Duration duration) {
        this.timeout = duration.timeout;
    }

    public static int of(Duration duration) {
        return duration.timeout;
    }

    private enum TimeUnit {
        MILLISECONDS {
            @Override
            protected int toMillis(int time) {
                return time;
            }
        }, SECONDS {
            @Override
            protected int toMillis(int time) {
                return time * 1000;
            }
        }, MINUTES {
            @Override
            protected int toMillis(int time) {
                return SECONDS.toMillis(time) * 60;
            }
        };

        protected abstract int toMillis(int time);

    }

}
