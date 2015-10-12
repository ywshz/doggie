package org.yws.doggieweb.models;

/**
 * Created by wangshu.yang on 2015/7/29.
 */
public enum AllocationType {
    AUTO, ASSIGN;

    public static AllocationType get(String source) {
        for (AllocationType e : values()) {
            if( e.name().equals(source)) {
                return e;
            }
        }
        return null;
    }

    public static AllocationType get(int source) {
        for ( AllocationType e : values() ) {
            if(e.ordinal() == source ) {
                return e;
            }
        }
        return null;
    }

}
