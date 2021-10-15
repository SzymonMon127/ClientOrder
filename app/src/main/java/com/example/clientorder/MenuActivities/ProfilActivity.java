package com.example.clientorder.MenuActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.clientorder.BasketActivity;
import com.example.clientorder.MainActivity;
import com.example.clientorder.ObjectsAndAdapters.Order;

import com.example.clientorder.ObjectsAndAdapters.Profil;
import com.example.clientorder.QuestionsDatabaseHelper;
import com.example.clientorder.R;
import com.example.clientorder.SignInActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ProfilActivity extends AppCompatActivity implements ValueEventListener {

    private FirebaseDatabase firebaseDatabase;
    private String userName;
    private String Order1 = "";


    private int position;
    private int id;
    private String userId;
    int countInt;
    private int[] idTable;
    private  boolean error;


    private String phoneNumberFinal;
    private String addresss1;
    private String address2;
    private String address3;

    private TextView textViewReward;


    private TextView textViewOrder1;

    private TextView textViewOrder2;
    private CardView cardView;

    @SuppressLint("StaticFieldLeak")
    public static ProgressBar progressBar;
    public static ProgressBar progressBar2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        error = false;

        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        progressBar2 = (ProgressBar) findViewById(R.id.ProgressBar1);
        textViewReward = findViewById(R.id.textReward);
        cardView = findViewById(R.id.card_view_reward);

        cardView.setOnClickListener(view -> Snackbar.make(view, getResources().getString(R.string.rewardInfo), 8000).show());

        if (firebaseAuth.getCurrentUser() != null) {

            String firebaseEmail = firebaseAuth.getCurrentUser().getEmail();
            userName = StringUtils.substringBefore(firebaseEmail, "@");
            SaveToHistory();

            getId();
            ReadOrInitFromFirebase();

        }
        ImageButton deleteButton = findViewById(R.id.deleteButton);

        deleteButton.setOnClickListener(v -> showAlertDialogButtonClicked());



        textViewOrder1 = findViewById(R.id.order1);



        textViewOrder2 = findViewById(R.id.order2);






        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;


        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(userName);
    }

    private void getId() {



        String idNumber = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userId = idNumber;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.profil, menu);
        return true;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_phone:
                LoadUi();
                showAlertDialogButtonClickedInfo();
                break;
            case R.id.menu_adress:
                LoadUi();
                ShowAlertDialogAdress(addresss1, address2, address3);
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        firebaseDatabase.getReference("Users").child(userName).child("Orders").removeEventListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        error = false;
        firebaseDatabase.getReference("Users").child(userName).child("Orders").addValueEventListener(this);
        Order1="";

    }

    @Override
    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {


        int size = (int) snapshot.getChildrenCount();


        if (size>0)
        {
            ArrayList<String> cityList1 = new ArrayList<>();
            for (DataSnapshot postSnapshot : snapshot.getChildren()){
                Order order = postSnapshot.getValue(Order.class);
                assert order != null;

                cityList1.add(order.order);
            }

            ArrayList<Double> cityList2 = new ArrayList<>();
            for (DataSnapshot postSnapshot : snapshot.getChildren()){
                Order order = postSnapshot.getValue(Order.class);
                assert order != null;
                cityList2.add(order.cost);
            }




            ArrayList<String> cityList3 = new ArrayList<>();
            for (DataSnapshot postSnapshot : snapshot.getChildren()){
                Order order = postSnapshot.getValue(Order.class);
                assert order != null;
                cityList3.add(order.hour);
            }

            int j =0;
            for (int i =0; i < size; i++)
            {
                j++;
                Order1 = Order1 + "(" +j+")"+ getResources().getString(R.string.order) + "\n" + cityList1.get(i) + "\n\n" + getResources().getString(R.string.price) + cityList2.get(i) + " PLN"
                        + "\n\n" +  getResources().getString(R.string.hourDelivery) +"\n"+ cityList3.get(i) + "\n\n";
            }

        }
        else
        {
            Order1 =getResources().getString(R.string.noOrders);
        }

        progressBar.setVisibility(View.GONE);

        textViewOrder1.setText(Order1);

    }

    @Override
    public void onCancelled(@NonNull @NotNull DatabaseError error) {

    }
    private void SaveToHistory()
    {
        firebaseDatabase.getReference("Users").child(userName).child("OrdersHistory").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                int size = (int) snapshot.getChildrenCount();


                if (size >0)
                {

                    String name;
                    Double price;

                    SQLiteOpenHelper questionsDatabaseHelper = new QuestionsDatabaseHelper(ProfilActivity.this);
                    SQLiteDatabase db = questionsDatabaseHelper.getWritableDatabase();

                    ArrayList<String> cityList = new ArrayList<>();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()){
                        Order order = postSnapshot.getValue(Order.class);
                        assert order != null;
                        cityList.add(order.order);
                    }

                    ArrayList<Double> cityList2 = new ArrayList<>();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()){
                        Order order = postSnapshot.getValue(Order.class);
                        assert order != null;
                        cityList2.add(order.cost);
                    }

                    try {
                        for (int i = 0; i <size; i++)
                        {
                            error = false;
                            name = cityList.get(i);
                            price = cityList2.get(i);
                            ContentValues Info1 = new ContentValues();
                            Info1.put("NAME", name);
                            Info1.put("PRICE",price);


                            db.insert("HISTORY", null, Info1);
                        }
                    }
                    catch (Exception e)
                    {
                        error = true;
                        Toast.makeText(ProfilActivity.this, getResources().getString(R.string.failLoad), Toast.LENGTH_SHORT).show();
                    }
                    finally {
                        if (!error)
                        {
                            firebaseDatabase.getReference("Users").child(userName).child("OrdersHistory").removeValue();
                        }
                    }


                    db.close();
                }
                loadHistory();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("Range")
    private void loadHistory()
    {
            SQLiteOpenHelper questionsDatabaseHelper = new QuestionsDatabaseHelper(this);
            SQLiteDatabase db = questionsDatabaseHelper.getWritableDatabase();

        int tableCount = 0;


            Cursor c = db.query("HISTORY",null,null,null,null,null,null);
        long count1 = DatabaseUtils.queryNumEntries(db, "HISTORY");
        countInt = (int) count1;

        idTable = new int[countInt];

            c.moveToFirst();
            if ( c.moveToFirst())
            {
                int count =1;
                String name;
                double price;

                int idToTable;


                name = c.getString(c.getColumnIndex("NAME"));
                price = c.getDouble(c.getColumnIndex("PRICE"));
                idToTable = c.getInt(c.getColumnIndex("_id"));

                idTable[tableCount] = idToTable;
                tableCount++;
                String order2 = "(" + count + ")" +getResources().getString(R.string.order) + "\n" + name + "\n\n" + getResources().getString(R.string.price) + price + " PLN" + "\n\n";

                while (c.moveToNext()) {
                    count++;
                    name = c.getString(c.getColumnIndex("NAME"));
                    price = c.getDouble(c.getColumnIndex("PRICE"));
                    idToTable = c.getInt(c.getColumnIndex("_id"));

                    idTable[tableCount] = idToTable;
                    order2 = order2 +"(" +count+")"+ getResources().getString(R.string.order) + "\n" +name + "\n\n" + getResources().getString(R.string.price) + price + " PLN" +"\n\n";

                    tableCount++;
                }

                textViewOrder2.setText(order2);
            }

            c.close();
            db.close();


    }


    public void showAlertDialogButtonClicked() {

        final EditText edittext = new EditText(ProfilActivity.this);
        edittext.setInputType(2);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_ClientOrder));
        builder.setTitle(getResources().getString(R.string.deleteHistory));
        builder.setMessage(getResources().getString(R.string.deleteHistoryMEss));
        builder.setView(edittext);

        builder.setPositiveButton(getResources().getString(R.string.deleteRecord), (dialog, which) -> {
            error = false;
            try {
                String YouEditTextValue = edittext.getText().toString();
                position = Integer.parseInt(YouEditTextValue);
            }
            catch (Exception e)
            {
                Toast.makeText(ProfilActivity.this, getResources().getString(R.string.badWord), Toast.LENGTH_SHORT).show();
                error = true;

            }
            finally {
                if (position > 0 && position <=countInt && !error )
                {
                    error = false;
                    try {
                        SQLiteOpenHelper questionsDatabaseHelper = new QuestionsDatabaseHelper(ProfilActivity.this);
                        SQLiteDatabase db = questionsDatabaseHelper.getWritableDatabase();

                        id = idTable[position-1];

                        db.delete("HISTORY", "_id = ?",
                                new String[]{Integer.toString(id)});
                        db.close();

                    }
                    catch (Exception e)
                    {
                        error = true;
                        Toast.makeText(ProfilActivity.this, "Database error", Toast.LENGTH_SHORT).show();

                    }
                    finally {
                        if (!error)
                        {
                            Toast.makeText(ProfilActivity.this, getResources().getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                            loadHistory();
                            if (countInt ==0)
                            {
                                textViewOrder2.setText(R.string.nullHistory);

                            }
                        }
                    }

                }
                else if (!error)
                {
                    Toast.makeText(ProfilActivity.this, getResources().getString(R.string.noOrderOnList), Toast.LENGTH_SHORT).show();

                }
            }




        });

        builder.setNeutralButton(getResources().getString(R.string.deleteAll), (dialog1, which1) -> deleteBasket());
        builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> Toast.makeText(ProfilActivity.this, getResources().getString(R.string.canceled), Toast.LENGTH_SHORT).show());



        AlertDialog dialog = builder.create();
        dialog.setIcon(R.drawable.icon);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.password_background);
        dialog.show();

    }


    private void deleteBasket()
    {
        error = false;
        try {
        SQLiteOpenHelper questionsDatabaseHelper = new QuestionsDatabaseHelper(ProfilActivity.this);
        SQLiteDatabase db = questionsDatabaseHelper.getWritableDatabase();
        db.execSQL("delete from "+ "HISTORY");
        db.close();
    }
    catch (Exception e)
    {
        error = true;
        Toast.makeText(ProfilActivity.this, getResources().getString(R.string.baskedDeletedFail), Toast.LENGTH_SHORT).show();

    }
    finally {
            if (!error)
            {
                Toast.makeText(ProfilActivity.this, getResources().getString(R.string.baskedDeleted), Toast.LENGTH_SHORT).show();
                textViewOrder2.setText(R.string.nullHistory);
            }


    }}

    @SuppressLint("Range")
    private void LoadUi()
    {


        SQLiteOpenHelper questionsDatabaseHelper = new QuestionsDatabaseHelper(this);
        SQLiteDatabase db = questionsDatabaseHelper.getWritableDatabase();


        Cursor c = db.query("ADDRESS",null,null,null,null,null,null);

        c.moveToFirst();
        if ( c.moveToFirst())
        {
            phoneNumberFinal = c.getString(c.getColumnIndex("PHONE"));
            addresss1 = c.getString(c.getColumnIndex("ADRESS1"));
            address2 = c.getString(c.getColumnIndex("ADRESS2"));
            address3 = c.getString(c.getColumnIndex("ADRESS3"));
        }


        c.close();
        db.close();

    }


    public void showAlertDialogButtonClickedInfo() {



        final EditText edittext = new EditText(ProfilActivity.this);


        edittext.setInputType(2);


        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_ClientOrder));
        builder.setTitle(phoneNumberFinal);
        builder.setMessage(getResources().getString(R.string.updateNumber));

        builder.setView(edittext);

        builder.setPositiveButton(getResources().getString(R.string.next), (dialog, which) -> {

            String phoneNumber;
            phoneNumber = String.valueOf(edittext.getText());
            if (phoneNumber.length() >=9)
            {
                phoneNumberFinal = phoneNumber;

                SQLiteOpenHelper questionDatabaseHelper = new QuestionsDatabaseHelper(this);

                SQLiteDatabase db = questionDatabaseHelper.getWritableDatabase();
                Cursor c = db.query("ADDRESS",null,null,null,null,null,null);
                if (c.moveToFirst())
                {
                    boolean fail = false;
                    try {
                        ContentValues values = new ContentValues();
                        values.put("PHONE", phoneNumberFinal);

                        db.update("ADDRESS", values,  "_id=?",
                                new String[]{String.valueOf(1)});
                    }
                                catch (Exception e)
                    {
                        fail = true;
                    }
                    finally {
                    if (!fail)
                    {
                        Toast.makeText(ProfilActivity.this, getResources().getString(R.string.savedSuccess), Toast.LENGTH_SHORT).show();
                    }
                }
                }
                else
                {
                    Toast.makeText(ProfilActivity.this, getResources().getString(R.string.nullDatabase), Toast.LENGTH_SHORT).show();
                }



                db.close();
                c.close();
            }
            else
            {
                showAlertDialogButtonClickedInfo();
                Toast.makeText(ProfilActivity.this,getResources().getString(R.string.noPhone9), Toast.LENGTH_SHORT).show();
            }
        });


        builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> Toast.makeText(ProfilActivity.this, getResources().getString(R.string.canceled), Toast.LENGTH_SHORT).show());



        AlertDialog dialog = builder.create();
        dialog.setIcon(R.drawable.icon);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.password_background);
        dialog.show();

    }

    private void ShowAlertDialogAdress(String string1, String string2, String string3)
    {



        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_ClientOrder));
        LayoutInflater inflater = getLayoutInflater();
        alert.setTitle(getResources().getString(R.string.updateaddress));
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
                Toast.makeText(ProfilActivity.this, getResources().getString(R.string.nullAdress), Toast.LENGTH_LONG).show();
            }
            else
            {
                addresss1 = YouEditTextValue1;
                address2 = YouEditTextValue2;
                address3 = YouEditTextValue3;

                SQLiteOpenHelper questionDatabaseHelper = new QuestionsDatabaseHelper(this);

                SQLiteDatabase db = questionDatabaseHelper.getWritableDatabase();
                Cursor c = db.query("ADDRESS",null,null,null,null,null,null);
                if (c.moveToFirst())
                {
                    boolean fail = false;
                    try {
                        ContentValues values = new ContentValues();
                        values.put("ADRESS1",addresss1);
                        values.put("ADRESS2",address2);
                        values.put("ADRESS3",address3);

                        db.update("ADDRESS", values,  "_id=?",
                                new String[]{String.valueOf(1)});
                    }
                    catch (Exception e)
                    {
                        fail = true;
                    }
                    finally {
                        if (!fail)
                        {
                            Toast.makeText(ProfilActivity.this, getResources().getString(R.string.savedSuccess), Toast.LENGTH_SHORT).show();
                        }
                    }


                }
                else
                {
                    Toast.makeText(ProfilActivity.this, getResources().getString(R.string.nullDatabase), Toast.LENGTH_SHORT).show();
                }



                db.close();
                c.close();

            }

        });
        alert.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> Toast.makeText(ProfilActivity.this, getResources().getString(R.string.canceled),Toast.LENGTH_SHORT).show());


        alert.setIcon(R.drawable.icon);
        alert.setView(view);
        AlertDialog dialog = alert.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.password_background);
        dialog.show();
    }


    private void ReadOrInitFromFirebase()
    {
        try {
            firebaseDatabase.getReference().child("rewardSystem").child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if ((int) dataSnapshot.getChildrenCount() != 0) {
                        Profil newPerson = dataSnapshot.getValue(Profil.class);
                        assert newPerson != null;

                        int rewardPoints = newPerson.points;

                        textViewReward.setText(getResources().getString(R.string.points) +": " + rewardPoints);



                        progressBar2.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(ProfilActivity.this, "Database error.", Toast.LENGTH_SHORT).show();

        }
    }

}