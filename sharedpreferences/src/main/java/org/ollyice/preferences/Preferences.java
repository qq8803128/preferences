package org.ollyice.preferences;


import android.content.Context;
import android.content.SharedPreferences;

import org.ollyice.preferences.annotation.CONFIG;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Preferences {
    private Context mContext;

    private Preferences(Context context) {
        mContext = context;
    }

    public <T> T create(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new PreferencesInvocationHandler(mContext, clazz));
    }

    public static class Builder {
        private Context mContext;

        public Builder(Context context) {
            mContext = context;
        }

        public Preferences build() {
            return new Preferences(mContext);
        }
    }

    static class PreferencesInvocationHandler implements InvocationHandler, TypeWrapper {
        private SharedPreferences mPreferences;
        private Map<String, TypeInfo> types;
        private Map<String, String> reads;
        private Map<String, String> writes;
        private Map<String, String> listeners;
        private Map<String, String> keyListeners;
        private List<String> wrapper;

        private Map<Object, Listener> sharedPreferencesListener = new HashMap<>();
        private Map<Object, Map<String, PreferencesWatcher>> watchers = new HashMap<>();

        public PreferencesInvocationHandler(Context context, Class<?> clazz) {
            super();
            CONFIG config = clazz.getAnnotation(CONFIG.class);
            if (config != null) {
                mPreferences = context.getSharedPreferences(config.name(), config.mode());
                Method[] methods = clazz.getMethods();
                types = Utils.getWriteProxyMethodsReturnType(methods);
                reads = Utils.getReadProxyMethods(methods);
                writes = Utils.getWriteProxyMethods(methods);
                listeners = Utils.getOnValueChangeMethods(methods);
                keyListeners = Utils.getOnValueKeyChangeMethods(methods);
                wrapper = Utils.getWrapperMethods(PreferencesWrapper.class.getMethods());
            } else {
                throw new PreferencesException("未配置CONFIG");
            }
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            String name = method.getName();
            if (wrapper.contains(name)) {
                final Object obj = objects[0];
                if (name.contains("unbind")) {
                    if (sharedPreferencesListener.containsKey(obj)) {
                        mPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener.get(obj).release());
                        sharedPreferencesListener.get(obj).release();
                        sharedPreferencesListener.remove(obj);
                    }
                } else if (name.contains("bind")) {
                    if (!sharedPreferencesListener.containsKey(obj)) {
                        Listener listener = new Listener(obj) {
                            @Override
                            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                                if (listeners.containsValue(s) && keyListeners.containsKey(s)) {
                                    String k = keyListeners.get(s);
                                    listenValue(obj, types.get(listeners.get(k)));
                                }
                            }
                        };
                        mPreferences.registerOnSharedPreferenceChangeListener(listener);
                        sharedPreferencesListener.put(obj, listener);
                    }
                }else if (name.contains("clear")){
                    mPreferences.edit().clear().apply();
                }
            } else if (writes.containsKey(name)) {
                TypeInfo type = types.get(writes.get(name));
                return setValue(type, objects);
            } else if (reads.containsKey(name)) {
                TypeInfo type = types.get(reads.get(name));
                return getValue(type, objects);
            } else if (listeners.containsKey(name)) {
                TypeInfo type = types.get(listeners.get(name));
                if (type != null) {
                    if (watchers.get(objects[0]) == null) {
                        watchers.put(objects[0], new HashMap<String, PreferencesWatcher>());
                    }
                    Map<String, PreferencesWatcher> map = watchers.get(objects[0]);
                    if (!map.containsKey(type.key)) {
                        map.put(type.key, new PreferencesWatcher());
                    }
                    return map.get(type.key);
                }
            }
            return null;
        }

        Object getValue(TypeInfo type, Object[] objects) {
            Object retn = null;
            if (type != null) {
                switch (type.type) {
                    case TYPE_STRING:
                        retn = mPreferences.getString(type.key, (String) objects[0]);
                        break;
                    case TYPE_SET:
                        retn = mPreferences.getStringSet(type.key, (Set<String>) objects[0]);
                        break;
                    case TYPE_INT:
                        retn = mPreferences.getInt(type.key, (Integer) objects[0]);
                        break;
                    case TYPE_LONG:
                        retn = mPreferences.getLong(type.key, (Long) objects[0]);
                        break;
                    case TYPE_FLOAT:
                        retn = mPreferences.getFloat(type.key, (Float) objects[0]);
                        break;
                    case TYPE_BOOLEAN:
                        retn = mPreferences.getBoolean(type.key, (Boolean) objects[0]);
                        break;
                }
            }
            return retn;
        }

        Object setValue(TypeInfo type, Object[] objects) {
            Object retn = null;
            SharedPreferences.Editor editor = mPreferences.edit();
            if (type != null) {
                switch (type.type) {
                    case TYPE_STRING:
                        editor = editor.putString(type.key, (String) objects[0]);
                        if (type.sync) {
                            retn = editor.commit();
                        } else {
                            editor.apply();
                        }
                        break;
                    case TYPE_SET:
                        editor = editor.putStringSet(type.key, (Set<String>) objects[0]);
                        if (type.sync) {
                            retn = editor.commit();
                        } else {
                            editor.apply();
                        }
                        break;
                    case TYPE_INT:
                        editor = editor.putInt(type.key, (Integer) objects[0]);
                        if (type.sync) {
                            retn = editor.commit();
                        } else {
                            editor.apply();
                        }
                        break;
                    case TYPE_LONG:
                        editor = editor.putLong(type.key, (Long) objects[0]);
                        if (type.sync) {
                            retn = editor.commit();
                        } else {
                            editor.apply();
                        }
                        break;
                    case TYPE_FLOAT:
                        editor = editor.putFloat(type.key, (Float) objects[0]);
                        if (type.sync) {
                            retn = editor.commit();
                        } else {
                            editor.apply();
                        }
                        break;
                    case TYPE_BOOLEAN:
                        editor = editor.putBoolean(type.key, (Boolean) objects[0]);
                        if (type.sync) {
                            retn = editor.commit();
                        } else {
                            editor.apply();
                        }
                        break;
                }
            }
            return retn;
        }

        void listenValue(Object target, TypeInfo type) {
            if (watchers.containsKey(target) && watchers.get(target).containsKey(type.key)) {
                watchers.get(target).get(type.key).listener.onChanged(getChangedValue(type));
            }
        }

        Object getChangedValue(TypeInfo type) {
            Object retn = null;
            if (type != null) {
                switch (type.type) {
                    case TYPE_STRING:
                        retn = mPreferences.getString(type.key, "");
                        break;
                    case TYPE_SET:
                        retn = mPreferences.getStringSet(type.key, null);
                        break;
                    case TYPE_INT:
                        retn = mPreferences.getInt(type.key, 0);
                        break;
                    case TYPE_LONG:
                        retn = mPreferences.getLong(type.key, 0);
                        break;
                    case TYPE_FLOAT:
                        retn = mPreferences.getFloat(type.key, 0);
                        break;
                    case TYPE_BOOLEAN:
                        retn = mPreferences.getBoolean(type.key, false);
                        break;
                }
            }
            return retn;
        }
    }

    static class Listener implements SharedPreferences.OnSharedPreferenceChangeListener {
        Object target;

        Listener(Object target) {
            this.target = target;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        }

        Listener release() {
            target = null;
            return this;
        }
    }
}
