package com.example.clientorder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;

import android.view.View;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import com.example.clientorder.ObjectsAndAdapters.Order;
import com.example.clientorder.ObjectsAndAdapters.Profil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;

import org.apache.commons.lang3.StringUtils;

import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;

import cz.msebera.android.httpclient.Header;


public class CheckoutActivity extends AppCompatActivity {

    public String paymentIntentClientSecret;
    private Stripe stripe;
    private  boolean fail;


    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;

    private String userName;


    public static final String ammount = "ammount";
    public static final String hour = "hour";
    public static final String adress = "adress";
    public static final String phoneNumber = "phoneNumber";
    public static final String order = "order";
    public static final String paymentMethod = "Oplacone w aplikacji";
    public static final String reward = "reward";
    public static final String maxRewardPoints = "rewardPointsMax";
    public static final String userIdString = "userIdString";

    private String userId;


    private String Currency;
    private String hourString;
    private String adressString;
    private String phoneNumberString;
    private String orderString;
    double CurrencyPLN;
    private int rewardsPoints;
    private int maxRewardsPointsINt;

    @SuppressLint("StaticFieldLeak")
    public static ProgressBar progressBar;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        fail = false;

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;


        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        progressBar = (ProgressBar) findViewById(R.id.ProgressBar);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {

            getNameAndId();

        }
        else
        {
            goToLogin();

        }



        Currency = (String) getIntent().getExtras().get(ammount);
        hourString = (String) getIntent().getExtras().get(hour);
        adressString = (String) getIntent().getExtras().get(adress);
        phoneNumberString = (String) getIntent().getExtras().get(phoneNumber);
        orderString = (String) getIntent().getExtras().get(order);
        rewardsPoints = (int) getIntent().getExtras().get(reward);
        maxRewardsPointsINt = (int) getIntent().getExtras().get(maxRewardPoints);
        userId = (String) getIntent().getExtras().get(userIdString);

         CurrencyPLN = Double.parseDouble(Currency);
         CurrencyPLN = CurrencyPLN/100;

        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51JUrcDHGlTVxEHGGykLaraPZC8D4eas5geUDAeGz394ZPIabd8zd4zACgL4EQc008rRrapP4vqoWswOmtFRcdhcC00OVIOP5GE"
        );

        if (firebaseDatabase== null)
        {
            Toast.makeText(CheckoutActivity.this, "Database connection fail", Toast.LENGTH_SHORT).show();
        }
        else
        {
            startCheckout();
        }


        ProdcutInfo();

    }


    private void startCheckout() {


        Button payButton = findViewById(R.id.payButton);
        CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);


        try {
            AsyncHttpClient client = new AsyncHttpClient();

            client.post("https://thawing-cliffs-89288.herokuapp.com/create-payment-intent?amount="+Currency+"&currency=pln", new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    paymentIntentClientSecret = new String(responseBody, StandardCharsets.UTF_8);
                    Log.d("dd", "Response SP Status. " + statusCode + paymentIntentClientSecret);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    displayAlert( getResources().getString(R.string.paymentFail));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        payButton.setOnClickListener((View view) -> {
            progressBar.setVisibility(View.VISIBLE);
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
            if (params != null) {
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                final Context context = getApplicationContext();
                stripe = new Stripe(
                        context,
                        PaymentConfiguration.getInstance(context).getPublishableKey()
                );
                stripe.confirmPayment(this, confirmParams);
            }
        });
    }


    @Override
    protected void onResume() {
        fail = false;
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }



    private static final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        @NonNull
        private final WeakReference<CheckoutActivity> activityRef;

        PaymentResultCallback(@NonNull CheckoutActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final CheckoutActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                progressBar.setVisibility(View.GONE);
                // Payment completed successfully
                activityRef.get().displayAlert(activity.getResources().getString(R.string.paymentSucces));
                activity.AddOrder();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Log.v(
                        "Payment completed",
                        gson.toJson(paymentIntent)
                );



            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                progressBar.setVisibility(View.GONE);
                activityRef.get().displayAlert( activity.getResources().getString(R.string.paymentFail));
                // Payment failed
                Log.v(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
                );
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final CheckoutActivity activity = activityRef.get();
            if (activity == null) {
                progressBar.setVisibility(View.GONE);
                activityRef.get().displayAlert(activity.getResources().getString(R.string.paymentFail));
                return;
            }

            // Payment request failed – allow retrying using the same payment method
            Log.v("Error", e.toString());
        }
    }



    private void getNameAndId()
    {
        String firebaseEmail = firebaseAuth.getCurrentUser().getEmail();
            userName = StringUtils.substringBefore(firebaseEmail, "@");
    }



    private void goToLogin() {

        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }


    private void ProdcutInfo()
    {
        TextView infoText = findViewById(R.id.PriceInfo);
        String infoString;
        infoString = orderString + "\n\n" + hourString + "\n\n" + phoneNumberString + "\n\n"
                +  adressString + "\n\n" + getResources().getString(R.string.price)  + CurrencyPLN+ " PLN" + "\n\n" + rewardsPoints + " " + getResources().getString(R.string.points);

        infoText.setText(infoString);
    }

    private void displayAlert(
                              @Nullable String message) {
        Activity activity = this;
        runOnUiThread(() -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                    .setTitle(getResources().getString(R.string.payment))
                    .setMessage(message);

                builder.setPositiveButton("Ok", (dialogInterface, i) -> {
                    Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                            startActivity(intent);
                });

            builder.create().show();
        });
    }


    private void AddOrder() {
        Random random = new Random();
        int idInt = random.nextInt();
        String id = "ID " + idInt;
        String timeDelivery = "Nieokreślony";

        fail = false;
        try {

            Order newOrder1 = new Order(id, hourString, orderString, adressString, phoneNumberString, CurrencyPLN,paymentMethod, userName, rewardsPoints);
            Order newOrder2 = new Order(id, timeDelivery, orderString, adressString, phoneNumberString, CurrencyPLN,paymentMethod, userName, rewardsPoints);



            int addRewardPoints = (int) (CurrencyPLN);
            int newRewardsPoints = maxRewardsPointsINt - rewardsPoints + addRewardPoints;
            Profil newProfil = new Profil(userId,newRewardsPoints);
            firebaseDatabase.getReference("rewardSystem").child("users").child(userId).setValue(newProfil);

            firebaseDatabase.getReference("Orders").child(id).setValue(newOrder1);
            firebaseDatabase.getReference("Users").child(userName).child("Orders").child(id).setValue(newOrder2);


        }
        catch (Exception e)
        {
            fail = true;
            Toast.makeText(CheckoutActivity.this, getResources().getString(R.string.failOrder), Toast.LENGTH_LONG).show();

        }
        finally {
            if(!fail)
            {
                Toast.makeText(CheckoutActivity.this, getResources().getString(R.string.orderSucces), Toast.LENGTH_LONG).show();
                deleteBasket();
            }
        }



    }

    private void deleteBasket() {
        try {
            SQLiteOpenHelper questionsDatabaseHelper = new QuestionsDatabaseHelper(CheckoutActivity.this);
            SQLiteDatabase db = questionsDatabaseHelper.getWritableDatabase();
            db.execSQL("delete from " + "BASKET");
            db.close();
        } catch (Exception e) {
            Toast.makeText(CheckoutActivity.this, getResources().getString(R.string.baskedDeletedFail), Toast.LENGTH_SHORT).show();
        }
    }


}