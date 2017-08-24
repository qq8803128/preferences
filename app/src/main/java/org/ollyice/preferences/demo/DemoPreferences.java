package org.ollyice.preferences.demo;

import org.ollyice.preferences.PreferencesWatcher;
import org.ollyice.preferences.PreferencesWrapper;
import org.ollyice.preferences.annotation.CHANGED;
import org.ollyice.preferences.annotation.CONFIG;
import org.ollyice.preferences.annotation.READ;
import org.ollyice.preferences.annotation.WRITE;

/**
 * Created by Administrator on 2017/8/24.
 */
@CONFIG
public interface DemoPreferences extends PreferencesWrapper{
    final String USER_NAME = "userName";

    @READ(USER_NAME)
    String getUserName(String defaultUserName);

    @WRITE(USER_NAME)
    void setUserName(String userName);

    @CHANGED(USER_NAME)
    PreferencesWatcher<String> listenUserNameChanged(Object target);
}
