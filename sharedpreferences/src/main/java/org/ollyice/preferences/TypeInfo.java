package org.ollyice.preferences;

/**
 * Created by Administrator on 2017/8/24.
 */

class TypeInfo {
    String key;
    int type;
    boolean sync;

    public TypeInfo(String key,int type,boolean sync) {
        super();
        this.type = type;
        this.sync = sync;
        this.key = key;
    }
}
