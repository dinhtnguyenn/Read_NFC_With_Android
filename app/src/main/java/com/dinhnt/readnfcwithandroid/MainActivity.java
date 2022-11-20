package com.dinhnt.readnfcwithandroid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    private static Class targetActivity = null;
    private NfcAdapter nfcAdapter = null;
    private IntentFilter[] intentFiltersArray = null;
    private String[][] techListsArray = null;
    private PendingIntent pendingIntent = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("Tap Tag window ready ...");

        NfcManager nfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
//        nfcAdapter = nfcManager.getDefaultAdapter();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcManager != null) {
            System.out.println("NFC Manager ready ...");
        }

        if (nfcAdapter != null) {
            System.out.println("NFC Adapter ready ...");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(
                    this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
        }else {
            pendingIntent = PendingIntent.getActivity(
                    this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        }
        intentFiltersArray = new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)};
        techListsArray = new String[][]{new String[]{NfcA.class.getName()}, new String[]{NfcB.class.getName()}, new String[]{IsoDep.class.getName()}};
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            PendingIntent nfcPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        }else {
            PendingIntent nfcPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        }
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (nfcAdapter != null) {
            try {
                nfcAdapter.disableForegroundDispatch(this);
            } catch (IllegalStateException ex) {
                Log.e("ATHTAG", "Error disabling NFC foreground dispatch", ex);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        System.out.println("Doing onNewIntent() ...");

        // Use NFC to read tag
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        if (tag != null) {
            Toast.makeText(this, "Đã đọc được NFC", Toast.LENGTH_SHORT).show();
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.dinhnt.com"));
            startActivity(browserIntent);
        } else {
            System.out.println("No new tag found !!!");
        }
    }

    public static void setTargetRedirectIntent(Class activity) {
        targetActivity = activity;
    }

}