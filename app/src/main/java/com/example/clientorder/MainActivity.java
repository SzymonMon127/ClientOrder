package com.example.clientorder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.clientorder.MenuActivities.ContactActivity;
import com.example.clientorder.MenuActivities.HomeActivity;
import com.example.clientorder.MenuActivities.PrivaciPolicyActivity;
import com.example.clientorder.MenuActivities.ProfilActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.clientorder.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;






public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private String phoneNumberFinal;
    private String addresss1;
    private String address2;
    private String address3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        firebaseAuth = FirebaseAuth.getInstance();


        if (firebaseAuth.getCurrentUser() != null)
        {
               Toast.makeText(MainActivity.this,getResources().getString(R.string.welcome),  Toast.LENGTH_SHORT).show();
            LoadUi();
        }
        else
        {
          GoToLogin();
        }



        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;


        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setDisplayHomeAsUpEnabled(true);
        firebaseAuth = FirebaseAuth.getInstance();

        com.example.clientorder.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());




        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_pizza, R.id.navigation_dinner, R.id.navigation_salad, R.id.navigation_souces,R.id.navigation_drink)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_logout:
               GoToLogin();
                break;
            case R.id.menu_basket:
                Intent intent1 = new Intent(MainActivity.this, BasketActivity.class);
                startActivity(intent1);
                break;
            case R.id.menu_profil:
                Intent intent2 = new Intent(MainActivity.this, ProfilActivity.class);
                startActivity(intent2);
                break;
            case R.id.menu_private_policy:
                Intent intent3 = new Intent(MainActivity.this, PrivaciPolicyActivity.class);
                startActivity(intent3);
                break;
            case R.id.menu_home:
                Intent intent4 = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent4);
                break;
            case R.id.menu_contact:
                Intent intent5 = new Intent(MainActivity.this, ContactActivity.class);
                startActivity(intent5);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void GoToLogin() {
        firebaseAuth.signOut();
        Toast.makeText(MainActivity.this, getResources().getString(R.string.loggedout), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }


    private void LoadUi()
    {


        SQLiteOpenHelper questionsDatabaseHelper = new QuestionsDatabaseHelper(this);
        SQLiteDatabase db = questionsDatabaseHelper.getWritableDatabase();


        Cursor c = db.query("ADDRESS",null,null,null,null,null,null);

        c.moveToFirst();
        if ( c.moveToFirst())
        {

        }
        else
        {
            showAlertDialogButtonClickedInfo();
        }


        c.close();
        db.close();

    }

    public void showAlertDialogButtonClickedInfo() {



        final EditText edittext = new EditText(MainActivity.this);


        edittext.setInputType(2);


        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_ClientOrder));
        builder.setTitle(getResources().getString(R.string.PhoneText));
        builder.setMessage(getResources().getString(R.string.phoneMess));

        builder.setView(edittext);

        builder.setPositiveButton(getResources().getString(R.string.next), (dialog, which) -> {

            String phoneNumber;
            phoneNumber = String.valueOf(edittext.getText());
            if (phoneNumber.length() >=9)
            {
                phoneNumberFinal = phoneNumber;
                ShowAlertDialogAdress("", "", "");



            }
            else
            {
                showAlertDialogButtonClickedInfo();
                Toast.makeText(MainActivity.this,getResources().getString(R.string.noPhone9), Toast.LENGTH_SHORT).show();
            }
        });


        builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> Toast.makeText(MainActivity.this, getResources().getString(R.string.canceled), Toast.LENGTH_SHORT).show());



        AlertDialog dialog = builder.create();
        dialog.setIcon(R.drawable.icon);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.password_background);
        dialog.show();

    }

    private void ShowAlertDialogAdress(String string1, String string2, String string3)
    {



        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_ClientOrder));
        LayoutInflater inflater = getLayoutInflater();
        alert.setTitle(getResources().getString(R.string.address));
        View view = inflater.inflate(R.layout.alert_dialog_adress,  findViewById(R.id.root));
        EditText editText1 = view.findViewById(R.id.editTextUl);
        EditText editText2 = view.findViewById(R.id.editTextFirstAdd);
        EditText editText3 = view.findViewById(R.id.editTextSecondAdd);

        editText1.setText(string1);
        editText2.setText(string2);
        editText3.setText(string3);


        alert.setPositiveButton(getResources().getString(R.string.next), (dialog, which) -> {
            String YouEditTextValue1 = editText1.getText().toString();
            String YouEditTextValue2 = editText2.getText().toString();
            String YouEditTextValue3 = editText3.getText().toString();
            if (YouEditTextValue3.equals("") || YouEditTextValue2.equals("") || YouEditTextValue1.equals(""))
            {
                ShowAlertDialogAdress(YouEditTextValue1,YouEditTextValue2, YouEditTextValue3 );
                Toast.makeText(MainActivity.this, getResources().getString(R.string.nullAdress), Toast.LENGTH_LONG).show();
            }
            else
            {
                addresss1 = YouEditTextValue1;
                address2 = YouEditTextValue2;
                address3 = YouEditTextValue3;
                boolean fail = false;
                try {
                    SQLiteOpenHelper questionsDatabaseHelper = new QuestionsDatabaseHelper(MainActivity.this);
                    SQLiteDatabase db = questionsDatabaseHelper.getWritableDatabase();

                        ContentValues Info1 = new ContentValues();
                        Info1.put("PHONE", phoneNumberFinal);
                        Info1.put("ADRESS1",addresss1);
                        Info1.put("ADRESS2",address2);
                         Info1.put("ADRESS3",address3);

                        db.insert("ADDRESS", null, Info1);


                        db.close();
                }
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                    fail = true;
                }
                finally {
                    if (!fail)
                    {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.savedSuccess), Toast.LENGTH_SHORT).show();
                    }

                }
            }

        });
        alert.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> Toast.makeText(MainActivity.this, getResources().getString(R.string.canceled),Toast.LENGTH_SHORT).show());


        alert.setIcon(R.drawable.icon);
        alert.setView(view);
        AlertDialog dialog = alert.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.password_background);
        dialog.show();
    }
}