package org.yws.doggieweb.models;

/**
 * Created by ywszjut on 15/7/25.
 */
public enum ScheduleStatus {
    ON, OFF;

    public static ScheduleStatus get(int source) {
        for (ScheduleStatus e : values()) {
            if (e.ordinal() == source) {
                return e;
            }
        }
        return null;
    }
}
