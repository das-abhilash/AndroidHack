package in.zollet.abhilash.androidhack;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;


public class AndroidHackService extends AccessibilityService {

    static final String TAG = "AndroidHackService";


    private String getEventType(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                return "TYPE_NOTIFICATION_STATE_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                return "TYPE_VIEW_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                return "TYPE_VIEW_FOCUSED";
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                return "TYPE_VIEW_LONG_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                return "TYPE_VIEW_SELECTED";
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                return "TYPE_WINDOW_STATE_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                return "TYPE_VIEW_TEXT_CHANGED";
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                return "WINDOW_CONTENT_CHANGED";

        }
        return "default";
    }

    private String getEventText(AccessibilityEvent event) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : event.getText()) {
            sb.append(s);
        }
        return sb.toString();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (event.getClassName().toString().contentEquals("android.widget.EditText")
                && (getEventType(event).equals("TYPE_VIEW_TEXT_CHANGED") /*|| getEventType(event).equals("WINDOW_CONTENT_CHANGED")*/) ) {

            AccessibilityNodeInfo source = event.getSource();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                    && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {


                String lastWord = getEventText(event).substring(getEventText(event).lastIndexOf(" ") + 1);
                if (lastWord.equals("")) {
                    lastWord = getEventText(event).substring(getEventText(event).lastIndexOf(" ") - 2).trim();
                }

                if (lastWord.toLowerCase().equals("android")) {

                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    Bundle arguments = new Bundle();
                    arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
                            AccessibilityNodeInfo.MOVEMENT_GRANULARITY_WORD);
                    arguments.putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN,
                            true);
                    source.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY,
                            arguments);
                    String replaceText = lastWord.equals("Android") ? "Hacked" : "hacked";
                    ClipData clip = ClipData.newPlainText("replaceText", replaceText);
                    clipboard.setPrimaryClip(clip);
                    source.performAction(AccessibilityNodeInfo.ACTION_PASTE);

                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                if (source.getText().toString().toLowerCase().contains("android")) {

                    Bundle arguments = new Bundle();
                    arguments.putCharSequence(AccessibilityNodeInfo
                            .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, source.getText().toString()
                            .replace("android", "hack").replace("Android", "Hack"));

                    source.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                }
            }

        }

    }


    @Override
    public void onInterrupt() {
        Log.v(TAG, "onInterrupt");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.v(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }

}
