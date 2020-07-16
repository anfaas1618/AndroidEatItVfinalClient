package com.NirmalBakeryClient.androideatitv2client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class contactUs extends AppCompatActivity {
    Button call1,call2;
     static  int REQUEST_PHONE_CALL=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        call1=findViewById(R.id.callone);
        call2=findViewById(R.id.calltwo);
        call1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "8827913770"));// Initiates the Intent
                    startActivity(intent);
                }catch (Exception e)
                {
                    Toast.makeText(contactUs.this, "[call error ] "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                if (ContextCompat.checkSelfPermission(contactUs.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(contactUs.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                }
                else
                {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "8827913770"));// Initiates the Intent
                    startActivity(intent);
                }
            }
        });
        call2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "9406548464"));// Initiates the Intent
                    startActivity(intent);
                }catch (Exception e)
                {
                    Toast.makeText(contactUs.this, "[call error ] "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                if (ContextCompat.checkSelfPermission(contactUs.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(contactUs.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                }
                else
                {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "9406548464"));// Initiates the Intent
                    startActivity(intent);
                }
            }
        });
    }

}