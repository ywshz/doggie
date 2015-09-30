package org.yws.doggieweb.models;

/**
 * Created by ywszjut on 15/7/25.
 */
public enum ScheduleType {
    CRON, DEPENDENCY;

    public static ScheduleType get(int source) {
        for (ScheduleType e : values()) {
            if (e.ordinal() == source) {
                return e;
            }
        }
        return null;
    }
}
