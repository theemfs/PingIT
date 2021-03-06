package edu.gcc.whiletrue.pingit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import static android.support.v4.app.ActivityCompat.finishAffinity;

public class SettingsActivityFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener {

    private Context fragmentContext;
    private SignOutTask signOutTask;
    private AlertDialog confirmSignOutDialog;
    private AlertDialog signOutDialog;

    public SettingsActivityFragment() {
    }

    @Override
    public void onStart() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        // Set the summary of the Name preference to the user's friendly name.
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        String fname = ParseUser.getCurrentUser().get("friendlyName").toString();
        String dispNameKey = getString(R.string.prefs_display_name_key);
        EditTextPreference editTextPref = (EditTextPreference) findPreference(dispNameKey);
        editTextPref.setSummary(fname);
        try{
        // Set the summary of the Notification Sound preference to the tone's friendly name.
            String notKey = getString(R.string.prefs_notification_sound_key);

            String uriPath = sp.getString(notKey, "");
            String name;
            if(uriPath.length()==0) {

                Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(getActivity().getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION);
                Ringtone defaultRingtone = RingtoneManager.getRingtone(getActivity(), defaultRingtoneUri);

                name = defaultRingtone.getTitle(getActivity());
                if (name.trim().equals("")) name = "Blank Name";
            }
            else{
                Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), Uri.parse(uriPath));
                name = ringtone.getTitle(getActivity());
            }

            RingtonePreference ringtonePref = (RingtonePreference)
                    findPreference(notKey);
            ringtonePref.setSummary(name);
        }
        catch(Exception e){
            Toast.makeText(getActivity(), getString(R.string.prefs_ringtone_failed), Toast.LENGTH_SHORT).show();
        }

    }

    private String defaultFName;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout)super.onCreateView(inflater, container, savedInstanceState);

        fragmentContext = inflater.getContext();

        //set displayname to parse name

        EditTextPreference etp = (EditTextPreference)findPreference(getString(R.string.prefs_display_name_key));
        String fname = ParseUser.getCurrentUser().get("friendlyName").toString();
        etp.setText(fname);
        defaultFName = fname;

        //append the footerview below the settings, like the logout button
        FrameLayout footerView = (FrameLayout)inflater.inflate(R.layout.footer_settings, null);
        Button logoutBtn = (Button)footerView.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(this);
        view.addView(footerView);//add footer to the linearlayout hierarchy

        //create the signout dialog to display while the signout thread is running later
        AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
        builder.setTitle(R.string.app_name);
        LinearLayout dialogView = (LinearLayout)inflater.inflate(R.layout.dialog_signin, null);
        //change dialog message for logout...
        TextView logoutText = (TextView)dialogView.findViewById(R.id.signInDialogText);
        logoutText.setText(R.string.signingOutDialogMsg);
        builder.setView(dialogView);//assign the modified view to the alert dialog
        builder.setNegativeButton(R.string.dialogCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signOutTask.cancel(true);//cancel the signout background thread
            }
        });
        signOutDialog = builder.create();//finalize and create the alert dialog for use later

        //create signout confirmation dialog for use later
        builder = new AlertDialog.Builder(inflater.getContext());
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.confirmLogoutMsg);
        builder.setPositiveButton(R.string.dialogYes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //show signout dialog with progress spinner while Parse executes in the background
                ((MainApplication)getActivity().getApplication()).currentPage=0;
                signOutDialog.show();
                signOutTask = new SignOutTask();
                signOutTask.execute();//attempt to signout in the background
            }
        });
        builder.setNegativeButton(R.string.dialogNo, null);
        confirmSignOutDialog = builder.create();

        SwitchPreference notifResendSwitch = (SwitchPreference) findPreference(getString(R.string.prefs_notification_resend_toggle_key));

        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.logoutBtn:
                confirmSignOutDialog.show();
                break;

            default:
                break;
        }
    }

    private class SignOutTask extends AsyncTask<String, Void, Integer>{
        @Override
        protected Integer doInBackground(String... params) {
            try {
                ParseUser.logOut();
            }
            catch(Exception e){
                Log.e(getString(R.string.log_error), getString(R.string.userNotLoggedIn));
                return -1;//error code
            }
            return 0;//log out success
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            //remove persistant login
            SecurePreferences preferences = new SecurePreferences(fragmentContext,getString(R.string.pref_login),SecurePreferences.generateDeviceUUID(fragmentContext),true);
            preferences.clear();

            Intent intent = new Intent(fragmentContext, StartupActivity.class);
            //add an extra to indicate to the startup activity to show the login screen first
            intent.putExtra("startFragment", 1);
            startActivity(intent);//start the login/registration activity
            finishAffinity(getActivity());//finishes all activities in the stack
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //Update a preference's summary as soon as a user changes it
        Preference pref = findPreference(key);


        if(key.equals(getString(R.string.prefs_notification_sound_key))){
            Uri ringtoneUri = Uri.parse(sharedPreferences.getString(key, ""));

            Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), ringtoneUri);
            String name = ringtone.getTitle(getActivity());

            RingtonePreference ringtonePref = (RingtonePreference) findPreference(key);
            ringtonePref.setSummary(name);
        }else if(key.equals(getString(R.string.prefs_notification_resend_toggle_key))){

        }else if(key.equals(getString(R.string.prefs_notification_resend_delay_key))){

        }else if(key.equals(getString(R.string.prefs_display_name_key))){
            String newName = ((EditTextPreference) pref).getText().trim();
            if(newName.equals("")){//override
                ((EditTextPreference) pref).setText(defaultFName);
                Toast.makeText(getActivity(), R.string.str_blank_name_msg, Toast.LENGTH_SHORT).show();
            }
            else {
                defaultFName = newName;
                pref.setSummary(newName);
                ParseUser u = ParseUser.getCurrentUser();
                u.put("friendlyName", newName);
                u.saveInBackground();
            }
        }else if(key.equals(getString(R.string.prefs_clear_pings_key))){
            //will not run
        }
    }
}