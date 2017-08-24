package org.ollyice.preferences.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.ollyice.preferences.Preferences;
import org.ollyice.preferences.PreferencesWatcher;

public class MainActivity extends AppCompatActivity {

    DemoPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = new Preferences.Builder(this)
                .build()
                .create(DemoPreferences.class);


        preferences.setUserName("ollyice");

        final EditText editText = (EditText) findViewById(R.id.edittext);
        final TextView textView = (TextView) findViewById(R.id.textview);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editText.getText().toString().trim();
                preferences.setUserName(name);
            }
        });
        preferences.listenUserNameChanged(this).addPreferencesChangedListener(new PreferencesWatcher.OnPreferencesChangedListener<String>() {
            @Override
            public void onChanged(String value) {
                textView.setText("用户名:" + value);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferences.bind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        preferences.unbind(this);
    }
}
