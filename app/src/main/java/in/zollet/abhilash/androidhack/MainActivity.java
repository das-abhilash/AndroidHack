package in.zollet.abhilash.androidhack;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    Button go_to_accessebility_service;
    TextView instruction;
    static final String LOGTAG = "AndroidHackService";
     String service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        service = getPackageName() + "/" + AndroidHackService.class.getCanonicalName();
        go_to_accessebility_service = (Button) findViewById(R.id.go_to_accessebility_service);
        instruction = (TextView) findViewById(R.id.instruction);

        go_to_accessebility_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent settingsIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(settingsIntent);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isAccessibilityEnabled())
            instruction.setText("Accessibility Service is Enabled. \nYou are good to go \n(Evernote will not work)\n type \"Android\" and it'll be changed to \"Hacked\" ");
        else
            instruction.setText("Go to Assecibility Service and enable the \"Android Hack\" Service.");
    }

    public boolean isAccessibilityEnabled() {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + AndroidHackService.class.getCanonicalName();
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.d(LOGTAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {

            String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();

                    if (accessabilityService.equalsIgnoreCase(service)) {
                        Log.d(LOGTAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }

        }
        return accessibilityFound;
    }
}
