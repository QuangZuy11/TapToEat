package vn.edu.fpt.taptoeat.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "TapToEatSession";
    private static final String KEY_TABLE_NUMBER = "tableNumber";
    private static final String KEY_SESSION_ID = "sessionId";
    private static final String KEY_TABLE_ID = "tableId";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(int tableNumber, String sessionId, String tableId) {
        editor.putInt(KEY_TABLE_NUMBER, tableNumber);
        editor.putString(KEY_SESSION_ID, sessionId);
        editor.putString(KEY_TABLE_ID, tableId);
        editor.apply();
    }

    public int getTableNumber() {
        return prefs.getInt(KEY_TABLE_NUMBER, -1);
    }

    public String getSessionId() {
        return prefs.getString(KEY_SESSION_ID, null);
    }

    public String getTableId() {
        return prefs.getString(KEY_TABLE_ID, null);
    }

    public boolean hasActiveSession() {
        return getSessionId() != null && getTableNumber() != -1;
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
