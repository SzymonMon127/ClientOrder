package com.example.clientorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.clientorder.MenuActivities.ProfilActivity;
import com.example.clientorder.ObjectsAndAdapters.Order;
import com.example.clientorder.ObjectsAndAdapters.Profil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Objects;
import java.util.Random;

public class BasketActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView priceTextView;
    private TextView positionTextView;
    private TextView priceTotalTextView;
    private int RewardsPoints;
    private int usedRewardPoints;
    private String userId;
    private int[] idTable;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private double rewardCost;

    private int position;
    private int id;

    private double priceTotal;
    int countInt;

    private String userName;
    private String finalyName;
    private String hourDelivery;
    private String AdressDelivery;
    private String PhoneNumber;
    private String paymentDelivery;
    private int hourEditText;
    private int minuteEditText;
    private boolean fail;
    private String city;

    @SuppressLint("StaticFieldLeak")
    public static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_basket);
        fail = false;


        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.ProgressBar);


        nameTextView = findViewById(R.id.nameTextViewBasket);
        priceTextView = findViewById(R.id.priceTextView);
        priceTotalTextView= findViewById(R.id.priceTotalTextView);

        positionTextView = findViewById(R.id.positionTextView);
        ImageButton deleteButton = findViewById(R.id.deleteButton);
        ImageButton addOrderButton = findViewById(R.id.addOrderButton);

        addOrderButton.setOnClickListener(v -> {
            if (priceTotal !=0)
            {
            showAlertDialogButtonClickedInfo();
            }
            else
            {
                Toast.makeText(BasketActivity.this, getResources().getString(R.string.cannotNullOrder), Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(v -> showAlertDialogButtonClicked());


        if (firebaseAuth.getCurrentUser()!=null)
        {
            getId();
            ReadOrInitFromFirebase();
        }
        else
        {
            GoToLogin();
        }

        LoadUi();


    }
    private void GoToLogin() {
        firebaseAuth.signOut();
        Toast.makeText(BasketActivity.this, getResources().getString(R.string.loggedout), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(BasketActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onResume() {
        fail = false;
        super.onResume();
    }

    private void AddOrder() {
        FirebaseDatabase firebaseDatabase;
        firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        Random random = new Random();
        int idInt = random.nextInt();
        String id = "ID " + idInt;
        String timeDelivery = "Nieokreślony";





        if (firebaseAuth.getCurrentUser() != null) {

            String firebaseEmail = firebaseAuth.getCurrentUser().getEmail();
            userName = StringUtils.substringBefore(firebaseEmail, "@");


        }
            try {
                fail = false;

                    Order newOrder1 = new Order(id, hourDelivery, finalyName, AdressDelivery, PhoneNumber, priceTotal,paymentDelivery, userName, usedRewardPoints);
                    Order newOrder2 = new Order(id, timeDelivery, finalyName, AdressDelivery, PhoneNumber, priceTotal,paymentDelivery, userName, usedRewardPoints);
                    int addRewardPoints = (int) (priceTotal);
                    int newRewardsPoints = RewardsPoints - usedRewardPoints + addRewardPoints;
                    Profil newProfil = new Profil(userId,newRewardsPoints);
                     firebaseDatabase.getReference("rewardSystem").child("users").child(userId).setValue(newProfil);

                    firebaseDatabase.getReference("Orders").child(id).setValue(newOrder1);
                    firebaseDatabase.getReference("Users").child(userName).child("Orders").child(id).setValue(newOrder2);


            }
            catch (Exception e)
            {
                fail  = true;
                Toast.makeText(BasketActivity.this, getResources().getString(R.string.failOrder), Toast.LENGTH_LONG).show();

            }
            finally {
                    if (!fail)
                    {
                        Toast.makeText(BasketActivity.this, getResources().getString(R.string.orderSucces), Toast.LENGTH_LONG).show();
                        deleteBasket();
                    }

            }



    }

    @SuppressLint("Range")
    private void LoadUi()
    {


        SQLiteOpenHelper questionsDatabaseHelper = new QuestionsDatabaseHelper(this);
        SQLiteDatabase db = questionsDatabaseHelper.getWritableDatabase();

        int tableCount = 0;
        priceTotal =0;

        Cursor c = db.query("BASKET",null,null,null,null,null,null);
        long count = DatabaseUtils.queryNumEntries(db, "BASKET");

        countInt = (int) count;

        idTable = new int[countInt];

        c.moveToFirst();
        if ( c.moveToFirst())
        {

            String name;
            double price;
            int idToTable;
            int positionIntTextView;


            idToTable = c.getInt(c.getColumnIndex("_id"));
            name = c.getString(c.getColumnIndex("NAME"));
            price = c.getDouble(c.getColumnIndex("PRICE"));
            positionIntTextView =c.getPosition() + 1;

            idTable[tableCount] = idToTable;

            priceTotal = price;
            String nameString = name;
            String priceString = price + " PLN";
            String positionStringTextView = positionIntTextView + ".";

            tableCount++;



            while (c.moveToNext()) {
                idToTable = c.getInt(c.getColumnIndex("_id"));
                name = c.getString(c.getColumnIndex("NAME"));
                price = c.getDouble(c.getColumnIndex("PRICE"));
                positionIntTextView =c.getPosition() + 1;


                idTable[tableCount] = idToTable;
                priceTotal = priceTotal + price;
                nameString = nameString + "\n\n" + name;
                priceString = priceString + "\n\n" + price + " PLN";
                positionStringTextView = positionStringTextView + "\n\n" + positionIntTextView + ".";
                tableCount++;
            }


            String totalPriceStringTextView = getResources().getString(R.string.total) + priceTotal + " PLN";
            priceTotalTextView.setText(totalPriceStringTextView);
            finalyName = "Zamowienie: " + nameString.replace("\n\n", ", ") + ".";


            nameTextView.setText(nameString);
            priceTextView.setText(priceString);
            positionTextView.setText(positionStringTextView);
        }

        c.close();
        db.close();
        progressBar.setVisibility(View.GONE);
    }

    public void showAlertDialogButtonClicked() {

        final EditText edittext = new EditText(BasketActivity.this);
        edittext.setInputType(2);
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_ClientOrder));
        builder.setTitle(getResources().getString(R.string.deleteFromBAsked));
        builder.setMessage(getResources().getString(R.string.deleteBaskedMEss));
        builder.setView(edittext);

        builder.setPositiveButton(getResources().getString(R.string.deleteBaskedMEss), (dialog, which) -> {

           fail = false;


            try {
                String YouEditTextValue = edittext.getText().toString();
                position = Integer.parseInt(YouEditTextValue);
            }
            catch (Exception e)
            {
                Toast.makeText(BasketActivity.this, getResources().getString(R.string.badWord), Toast.LENGTH_SHORT).show();
                fail = true;

            }
            finally {
                if (position > 0 && position <=countInt && !fail)
                {
                    try {
                        SQLiteOpenHelper questionsDatabaseHelper = new QuestionsDatabaseHelper(BasketActivity.this);
                        SQLiteDatabase db = questionsDatabaseHelper.getWritableDatabase();

                        id = idTable[position-1];

                        db.delete("BASKET", "_id = ?",
                                new String[]{Integer.toString(id)});
                        db.close();
                    }
                    catch (Exception e)
                    {
                        fail = true;
                        Toast.makeText(BasketActivity.this, "Database error", Toast.LENGTH_SHORT).show();

                    }
                    finally {
                        if (!fail)
                        {
                            Toast.makeText(BasketActivity.this, getResources().getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                            LoadUi();
                            if (countInt ==0)
                            {
                                nameTextView.setText(R.string.nullBasket);
                                priceTextView.setText(" ");
                                positionTextView.setText(" ");
                                priceTotalTextView.setText(" ");
                            }
                        }

                    }

                }
                else if (!fail)
                {
                    Toast.makeText(BasketActivity.this, getResources().getString(R.string.noOnlList), Toast.LENGTH_SHORT).show();

                }
            }




        });

        builder.setNeutralButton(getResources().getString(R.string.deleteAll), (dialog1, which1) -> deleteBasket());
        builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> Toast.makeText(BasketActivity.this, getResources().getString(R.string.canceled), Toast.LENGTH_SHORT).show());



        AlertDialog dialog = builder.create();
        dialog.setIcon(R.drawable.icon);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.password_background);
        dialog.show();

    }

    private void deleteBasket()
    {
        fail = false;
        try {
        SQLiteOpenHelper questionsDatabaseHelper = new QuestionsDatabaseHelper(BasketActivity.this);
        SQLiteDatabase db = questionsDatabaseHelper.getWritableDatabase();
        db.execSQL("delete from "+ "BASKET");
        db.close();
    }
    catch (Exception e)
    {
        fail = true;
        Toast.makeText(BasketActivity.this, getResources().getString(R.string.baskedDeletedFail), Toast.LENGTH_SHORT).show();

    }
    finally {
            if (!fail)
            {
                Toast.makeText(BasketActivity.this, getResources().getString(R.string.baskedDeleted), Toast.LENGTH_SHORT).show();
                LoadUi();
                nameTextView.setText(R.string.nullBasket);
                priceTextView.setText(" ");
                positionTextView.setText(" ");
                priceTotalTextView.setText(" ");
            }
    }}


    @SuppressLint("Range")
    public void showAlertDialogButtonClickedInfo() {



        final EditText edittext = new EditText(BasketActivity.this);


            edittext.setInputType(2);


        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_ClientOrder));
            builder.setTitle(getResources().getString(R.string.PhoneText));
            builder.setMessage(getResources().getString(R.string.phoneMess));

        builder.setView(edittext);

        SQLiteOpenHelper questionsDatabaseHelper = new QuestionsDatabaseHelper(this);
        SQLiteDatabase db = questionsDatabaseHelper.getWritableDatabase();


        Cursor c = db.query("ADDRESS",null,null,null,null,null,null);

        String database1 = "";


        c.moveToFirst();
        if ( c.moveToFirst())
        {
            database1 = c.getString(c.getColumnIndex("PHONE"));

        }
        c.close();
        db.close();

            edittext.setText(database1);




        builder.setPositiveButton(getResources().getString(R.string.next), (dialog, which) -> {

               String phoneNumber;
                phoneNumber = String.valueOf(edittext.getText());
                if (phoneNumber.length() >=9)
               {
                    PhoneNumber = getResources().getString(R.string.PhoneText)+" " +phoneNumber;
                   ShowAlertDialogHour();

                }
                else
                {
                    showAlertDialogButtonClickedInfo();
                    Toast.makeText(BasketActivity.this,getResources().getString(R.string.noPhone9), Toast.LENGTH_SHORT).show();
               }
        });


        builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> Toast.makeText(BasketActivity.this, getResources().getString(R.string.canceled), Toast.LENGTH_SHORT).show());



        AlertDialog dialog = builder.create();
        dialog.setIcon(R.drawable.icon);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.password_background);
        dialog.show();

    }

    private void  AlertDialogButtons()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_ClientOrder));
        builder.setTitle(getResources().getString(R.string.paymentOptions));



        builder.setItems(new CharSequence[]
                        {getResources().getString(R.string.cash), getResources().getString(R.string.card), getResources().getString(R.string.appPayment)},
                (dialog, which) -> {

                    switch (which) {
                        case 0:
                            paymentDelivery = "PLATNOSC GOTOWKA";
                            AddOrder();
                            break;
                        case 1:
                            paymentDelivery = "PLATNOSC KARTA";
                            AddOrder();
                             break;
                        case 2:
                            fail = false;
                            Intent intent = new Intent(BasketActivity.this, CheckoutActivity.class);
                            try {


                                priceTotal = priceTotal *100;
                                int priceTotalInt;
                                priceTotalInt = (int) priceTotal;
                               String priceTotalString;
                                priceTotalString = String.valueOf(priceTotalInt);

                                intent.putExtra(CheckoutActivity.ammount, priceTotalString);
                                intent.putExtra(CheckoutActivity.phoneNumber, PhoneNumber);
                                intent.putExtra(CheckoutActivity.adress, AdressDelivery);
                                intent.putExtra(CheckoutActivity.hour, hourDelivery);
                                intent.putExtra(CheckoutActivity.order, finalyName);
                                intent.putExtra(CheckoutActivity.reward, usedRewardPoints);
                                intent.putExtra(CheckoutActivity.maxRewardPoints, RewardsPoints);
                                intent.putExtra(CheckoutActivity.userIdString, userId);
                            }
                            catch (Exception e)
                            {
                                fail = true;
                                Toast.makeText(BasketActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                                showAlertDialogButtonClicked();
                            }
                            finally {
                                if (!fail)
                                {
                                    startActivity(intent);

                                }
                            }
                            break;

                    }

                });
        builder.setIcon(R.drawable.icon);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.password_background);
        dialog.show();

    }

    @SuppressLint("Range")
    private void ShowAlertDialogAdress(String string1, String string2, String string3)
    {



        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_ClientOrder));
        LayoutInflater inflater = getLayoutInflater();
        alert.setTitle(getResources().getString(R.string.address));
        View view = inflater.inflate(R.layout.alert_dialog_adress,  findViewById(R.id.root));
        EditText editText1 = view.findViewById(R.id.editTextUl);
        EditText editText2 = view.findViewById(R.id.editTextFirstAdd);
        EditText editText3 = view.findViewById(R.id.editTextSecondAdd);

        SQLiteOpenHelper questionsDatabaseHelper = new QuestionsDatabaseHelper(this);
        SQLiteDatabase db = questionsDatabaseHelper.getWritableDatabase();


        Cursor c = db.query("ADDRESS",null,null,null,null,null,null);

        String database1 = "";
        String database2 = "";
        String database3 = "";

        c.moveToFirst();
        if ( c.moveToFirst())
        {
            database1 = c.getString(c.getColumnIndex("ADRESS1"));
            database2 = c.getString(c.getColumnIndex("ADRESS2"));
            database3 = c.getString(c.getColumnIndex("ADRESS3"));
        }
        c.close();
        db.close();

        if (string1.equals("") && string2.equals("") && string3.equals(""))
        {
            editText1.setText(database1);
            editText2.setText(database2);
            editText3.setText(database3);
        }
        else
            {
            editText1.setText(string1);
            editText2.setText(string2);
            editText3.setText(string3);
        }



        alert.setPositiveButton(getResources().getString(R.string.next), (dialog, which) -> {
            String YouEditTextValue1 = editText1.getText().toString();
            String YouEditTextValue2 = editText2.getText().toString();
            String YouEditTextValue3 = editText3.getText().toString();
            if (YouEditTextValue3.equals("") || YouEditTextValue2.equals("") || YouEditTextValue1.equals(""))
            {
                ShowAlertDialogAdress(YouEditTextValue1, YouEditTextValue2, YouEditTextValue3);
                Toast.makeText(BasketActivity.this, getResources().getString(R.string.nullAdress), Toast.LENGTH_LONG).show();
            }
            else
            {
                AdressDelivery = "Adres: " + YouEditTextValue1 + ", " + YouEditTextValue2 + "/" + YouEditTextValue3 + "\n" + "Kielce";
                ShowRewardAlert();
            }

        });
        alert.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> Toast.makeText(BasketActivity.this, getResources().getString(R.string.canceled),Toast.LENGTH_SHORT).show());


        alert.setIcon(R.drawable.icon);
        alert.setView(view);
        AlertDialog dialog = alert.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.password_background);
        dialog.show();
    }

    private void ShowAlertDialogCity()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_ClientOrder));
        alert.setTitle(getResources().getString(R.string.chooseCity));
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_city,  findViewById(R.id.root));
        RadioGroup radioGroup = view.findViewById(R.id.radio_group);



        alert.setPositiveButton(getResources().getString(R.string.next), (dialog, which) -> {
            int id = radioGroup.getCheckedRadioButtonId();
            if (id ==-1)
            {
                Toast.makeText(BasketActivity.this, getResources().getString(R.string.chooseeCity), Toast.LENGTH_SHORT).show();
                ShowAlertDialogCity();

            }
         else if (id == R.id.city_button_kielce)
        {
            city = "Kielce";
            ShowAlertDialogDeliveryType();

        }

        });
        alert.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> Toast.makeText(BasketActivity.this, getResources().getString(R.string.canceled),Toast.LENGTH_SHORT).show());


        alert.setIcon(R.drawable.icon);
        alert.setView(view);
        AlertDialog dialog = alert.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.password_background);
        dialog.show();
    }

    private void ShowAlertDialogDeliveryType()
    {



        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_ClientOrder));
        alert.setTitle(getResources().getString(R.string.chooseDeliveryTitle));
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_delivery_choose,  findViewById(R.id.root));
        RadioGroup radioGroup = view.findViewById(R.id.radio_group);



        alert.setPositiveButton(getResources().getString(R.string.next), (dialog, which) -> {
            int id = radioGroup.getCheckedRadioButtonId();
            if (id ==-1)
            {
                Toast.makeText(BasketActivity.this, getResources().getString(R.string.chooseDeliveryMessage), Toast.LENGTH_SHORT).show();
                ShowAlertDialogDeliveryType();

            }
            else if (id == R.id.local_delivery)
            {
                AdressDelivery = "Adres" +  ": lokal  -  " +city ;
                ShowRewardAlert();

            }
            else if (id == R.id.house_delivery)
            {
                ShowAlertDialogAdress("", "", "");
            }

        });
        alert.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> Toast.makeText(BasketActivity.this, getResources().getString(R.string.canceled),Toast.LENGTH_SHORT).show());


        alert.setIcon(R.drawable.icon);
        alert.setView(view);
        AlertDialog dialog = alert.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.password_background);
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void ShowRewardAlert()
    {



        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_ClientOrder));
        alert.setTitle(getResources().getString(R.string.alertTitle));
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_reward_system,  findViewById(R.id.root));
        SeekBar seekBar = view.findViewById(R.id.rewardBar);
        seekBar.setMax(RewardsPoints);
        TextView textView = view.findViewById(R.id.infoFromBar);
        usedRewardPoints = seekBar.getProgress();
        textView.setText(seekBar.getProgress()  + " " + getResources().getString(R.string.points));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {

                usedRewardPoints = progress;
                rewardCost = (double)progress /100;
                int checkPrice = (int) (priceTotal/2);
                if (rewardCost > checkPrice)
                {
                    rewardCost = priceTotal;
                    int newProgress = (int) ((rewardCost*100)/2);
                    progress = newProgress;
                    seekBar.setProgress(newProgress);
                    Toast.makeText(BasketActivity.this, getResources().getString(R.string.tobigReward), Toast.LENGTH_SHORT).show();
                }
                textView.setText(progress  +" " + getResources().getString(R.string.points) + " = " + rewardCost + "PLN " );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        alert.setPositiveButton(getResources().getString(R.string.next), (dialog, which) -> {
            priceTotal = priceTotal - rewardCost;
            AlertDialogButtons();

        });
        alert.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> Toast.makeText(BasketActivity.this, getResources().getString(R.string.canceled),Toast.LENGTH_SHORT).show());


        alert.setIcon(R.drawable.icon);
        alert.setView(view);
        AlertDialog dialog = alert.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.password_background);
        dialog.show();
    }

    private void ShowAlertDialogHour()
    {



        AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_ClientOrder));
        LayoutInflater inflater = getLayoutInflater();
        alert.setTitle(getResources().getString(R.string.deliveryhour));
        View view = inflater.inflate(R.layout.alert_dialog_hour,  findViewById(R.id.root));
        NumberPicker numberPickerHour = view.findViewById(R.id.numberPickerHour);
        NumberPicker numberPickerMinutes = view.findViewById(R.id.numberPickerMinutes);


        numberPickerHour.setMaxValue(23);
        numberPickerHour.setMinValue(0);
        numberPickerHour.setValue(0);
        numberPickerMinutes.setMinValue(0);
        numberPickerMinutes.setMaxValue(59);

        numberPickerHour.setOnValueChangedListener((numberPicker, i, i1) -> hourEditText = i1);

        numberPickerMinutes.setOnValueChangedListener((numberPicker, i, i1) -> minuteEditText = i1);


        alert.setPositiveButton(getResources().getString(R.string.next), (dialog, which) -> {

            Calendar rightNow = Calendar.getInstance();
            int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);

            if (currentHourIn24Format >21)
            {
                Toast.makeText(BasketActivity.this, getResources().getString(R.string.appTime4), Toast.LENGTH_LONG).show();
            }
            else if (currentHourIn24Format < 7)
            {
                Toast.makeText(BasketActivity.this, getResources().getString(R.string.appTime3), Toast.LENGTH_LONG).show();

            }
            else {
                    if (hourEditText >22)
                    {
                        Toast.makeText(BasketActivity.this, getResources().getString(R.string.appTime2), Toast.LENGTH_LONG).show();
                        ShowAlertDialogHour();
                    }
                    else if (hourEditText<8)
                    {
                        Toast.makeText(BasketActivity.this, getResources().getString(R.string.appTime1), Toast.LENGTH_LONG).show();
                        ShowAlertDialogHour();
                    }
                    else
                    {
                        String hourEditTextString = String.valueOf(hourEditText);
                        if (hourEditText <10)
                        {
                            hourEditTextString = "0" + hourEditTextString;
                        }
                            if (minuteEditText==0)
                            {

                                ShowAlertDialogCity();
                                hourDelivery = hourEditTextString+":"+minuteEditText+"0";
                            }
                            else if (minuteEditText<10)
                            {
                               ShowAlertDialogCity();
                                hourDelivery = hourEditTextString+":"+"0"+minuteEditText;
                            }
                            else
                            {
                                ShowAlertDialogCity();
                                hourDelivery = hourEditTextString+":"+minuteEditText;
                            }
                            hourDelivery = "Godzina: " + hourDelivery;
                    }



            }




        });
        alert.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> Toast.makeText(BasketActivity.this, getResources().getString(R.string.canceled),Toast.LENGTH_SHORT).show());


        alert.setIcon(R.drawable.icon);
        alert.setView(view);
        AlertDialog dialog = alert.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.password_background);
        dialog.show();
    }
    private void getId() {



        String idNumber = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userId = idNumber;
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

                        RewardsPoints = newPerson.points;






                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(BasketActivity.this, "Database error.", Toast.LENGTH_SHORT).show();

        }
    }

}