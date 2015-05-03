package com.eightbitforest.supertext;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final int CONTACT_PICKER_RESULT = 1001;
    public static final String TAG = "debugMessage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onContactClick(View view)
    {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    public void onSendClick(View view)
    {
        TextView phoneNumber = (TextView) findViewById(R.id.phoneNumber);
        TextView messageText = (TextView) findViewById(R.id.message);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber.getText().toString(), null, messageText.getText().toString(), null, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    Cursor cursor = null;
                    String phone = "";
                    try {
                        Uri result = data.getData();

                        String id = result.getLastPathSegment();

                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[] { id },
                                null);

                        int phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);

                        if (cursor.moveToFirst()) {
                            phone = cursor.getString(phoneIdx);
                        } else {
                            Log.w(TAG, "No results");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to get phone data", e);
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }

                        EditText phoneEntry = (EditText) findViewById(R.id.phoneNumber);
                        phoneEntry.setText(phone);

                        if (phone.length() == 0) {
                            Toast.makeText(this, "No phone number found for contact.",
                                    Toast.LENGTH_LONG).show();
                        }

                    }

                    break;
            }

        }
        else
            Log.w(TAG, "Warning: contact activity result not ok");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
