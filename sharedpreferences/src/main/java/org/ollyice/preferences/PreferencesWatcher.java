package org.ollyice.preferences;

/**
 * Created by Administrator on 2017/8/24.
 */

public class PreferencesWatcher<T> {
    OnPreferencesChangedListener<T> listener;
    String key;
    public void addPreferencesChangedListener(OnPreferencesChangedListener<T> listener){
        this.listener = listener;
    }

    public interface OnPreferencesChangedListener<R>{
        void onChanged(R value);
    }
}
