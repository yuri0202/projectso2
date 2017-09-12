package yuria.shApping.resources;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import yuria.shApping.R;


public class CustomToast {
    public static final int ERROR=0, SUCCESS=1, WARNING=2;

    public static void create_custom_toast(Context applicationContext, LayoutInflater inflater, ViewGroup myViewGroup, int layout_id, String message) {
        View layout = inflater.inflate(layout_id, myViewGroup);

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(applicationContext);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}