package com.volkangurbuz.smsbilgisidemo;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SmsActivity extends AppCompatActivity {

    String SENT = "SMS_SENT";
    String DELIVERED = "SMS_DELIVERED";
    PendingIntent sentPI, deliveredPI;
    BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;

    EditText numara, mesaj;
    Button yolla;
    private static final int SMS_REQUEST = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);
        deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        numara = (EditText) findViewById(R.id.numaraID);
        mesaj = (EditText) findViewById(R.id.mesajID);
        yolla = (Button) findViewById(R.id.button);

        if (!izinVar()) {
            kullaniciIzni();
        }


    }


    public void mesajYolla() {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(numara.getText().toString(), null, mesaj.getText().toString(), sentPI, deliveredPI);

    }


    private void kullaniciIzni() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_REQUEST);

    }


    private boolean izinVar() {

        int resultSms = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if (resultSms == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }



    @Override
    public void onResume() {
        super.onResume();


        smsSentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS yollandı",  Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Genel hata", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "Servis Yok", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Boş PDU değeri", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Sinyal Yok", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

        };


        smsDeliveredReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS iletildi",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS iletilmedi",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };


        registerReceiver(smsSentReceiver, new IntentFilter(SENT));
        registerReceiver(smsDeliveredReceiver, new IntentFilter(DELIVERED));

        yolla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mesajYolla();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
    }

}
