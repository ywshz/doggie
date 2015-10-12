package org.yws.doggie.scheduler.models;

/**
 * Created by ywszjut on 15/7/25.
 */
public enum JobType {
    SHELL, HIVE, PYTHON;

    public static JobType get(int source) {
        for (JobType e : values()) {
            if (e.ordinal() == source) {
                return e;
            }
        }
        return null;
    }

}
