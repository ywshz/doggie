package org.yws.doggie.scheduler.models;

/**
 * Created by wangshu.yang on 2015/7/29.
 */
public enum JobRunResult {
    RUNNING, SUCCESS, FAILED;

    public static JobRunResult get(int source) {
        for (JobRunResult e : values()) {
            if (e.ordinal() == source) {
                return e;
            }
        }
        return null;
    }
}
