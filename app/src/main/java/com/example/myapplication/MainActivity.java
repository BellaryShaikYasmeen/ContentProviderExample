package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends Activity {
    TextView tv1;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1 = findViewById(R.id.textView);
        tv1.setOnClickListener(v -> requestContactPermission());

    }

    private void getContacts() {
        //TODO get contacts code here
        Toast.makeText(this, "Get contacts ....", Toast.LENGTH_LONG).show();
        ContentResolver cr = getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = cr.query(uri, null, null, null);
        Log.i("Contacts_provider", "Total no of Contacts" + cursor.getCount());
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String contactNo = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.i("Contacts_provider", "\n ContactName \n" + contactName + "\n Phone Number\n" + contactNo);
            }

        }


    }

    public void requestContactPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 0);
        getContacts();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts();
            } else {
                Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
            }
        }
    }
}