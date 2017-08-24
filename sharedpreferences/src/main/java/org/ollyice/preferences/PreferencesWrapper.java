package org.ollyice.preferences;

/**
 * Created by Administrator on 2017/8/24.
 */

public interface PreferencesWrapper {
    void bind(Object target);
    void unbind(Object target);
    void clear();
}
