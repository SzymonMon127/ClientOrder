package com.example.clientorder.MenuActivities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clientorder.R;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;


        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView phoneNumber = findViewById(R.id.phone_number);
        TextView textView = findViewById(R.id.email_adres);
        ImageButton buttonPhone  = findViewById(R.id.phone_button);
        ImageButton buttonEmail = findViewById(R.id.email_button);
        ImageButton buttonAdress = findViewById(R.id.adress_button);

        buttonAdress.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps?q=Skalska+5/18&um=1&ie=UTF-8&sa=X&ved=2ahUKEwiB57uUndfyAhVLsKQKHRdIBZwQ_AUoAXoECAEQAw"));
            startActivity(intent);
        });

        buttonEmail.setOnClickListener(v -> {

            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{String.valueOf(textView.getText())});
            email.putExtra(Intent.EXTRA_SUBJECT, "OrderManagmentApplicationContact");
            email.setType("message/rfc822");

            startActivity(Intent.createChooser(email, getResources().getString(R.string.getEmailProvider)));
        });

        buttonPhone.setOnClickListener(v -> {
            Toast.makeText(ContactActivity.this, getResources().getString(R.string.deletedSucces), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            String parseString = "tel:"+phoneNumber.getText();
            intent.setData(Uri.parse(parseString));
            startActivity(intent);

        });
    }
}