package org.yws.doggie.worker;

public enum TriggerType {
	AUTO, MANUAL;

	public static TriggerType get(int source) {
		for (TriggerType e : values()) {
			if (e.ordinal() == source) {
				return e;
			}
		}
		return null;
	}
}
