package org.yws.doggieweb.models;

/**
 * Created by ywszjut on 15/7/25.
 */
public enum TriggerType {
    AUTO,MANUAL;

    public static TriggerType get(int source) {
        for (TriggerType e : values()) {
            if (e.ordinal() == source) {
                return e;
            }
        }
        return null;
    }
}
