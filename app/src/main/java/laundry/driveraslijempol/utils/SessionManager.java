package laundry.driveraslijempol.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Bagus on 24/07/2018.
 */
public class SessionManager {
    private static final String TAG = SessionManager.class.getSimpleName();

    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LaundrySession";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private final static String ACCESS_TOKEN = "access_token";
    private final static String FULLNAME = "fullname";
    private final static String NOHP = "nohp";
    private final static String OS_PLAYER_ID = "os_player_id";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setIsLogin(boolean isLoggedIn){
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.commit();

        Log.d(TAG, "User login session modified");
    }

    public boolean isLoggedIn(){
        return  pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void setAccessToken(String token){
        editor.putString(ACCESS_TOKEN, token);
        editor.commit();

        Log.d(TAG, "User new token "+token);
    }

    public String getAccessToken(){
        return pref.getString(ACCESS_TOKEN, null);
    }

    public void setFullname(String fullname){
        editor.putString(FULLNAME, fullname);
        editor.commit();

        Log.d(TAG, "User fullname set to "+fullname);
    }

    public String getFullname(){
        return pref.getString(FULLNAME, "Your Name Here");
    }

    public void setNohp(String nohp){
        editor.putString(NOHP, nohp);
        editor.commit();

        Log.d(TAG, "Nohp set to "+nohp);
    }

    public String getNohp(){
        return pref.getString(NOHP, "Your NoHP Here");
    }

    public void setOsPlayerId(String playerId){
        editor.putString(OS_PLAYER_ID, playerId);
        editor.commit();

        Log.d(TAG, "OS Player ID "+playerId);
    }

    public String getOsPlayerId(){
        return pref.getString(OS_PLAYER_ID, null);
    }
}
