package org.linphone.compatibility;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

public class Compatibility
{
    private Compatibility()
    {

    }
    public static String getDeviceName(Context context)
    {
        String name = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            name = Settings.Global.getString(context.getContentResolver(), Settings.Global.DEVICE_NAME);
        }
        if (name == null)
        {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter != null)
            {
                name = adapter.getName();
            }
        }
        if (name == null)
        {
            name = Settings.Secure.getString(context.getContentResolver(), "bluetooth_name");
        }
        if (name == null)
        {
            name = Build.MANUFACTURER + " " + Build.MODEL;
        }
        return name;
    }
}
