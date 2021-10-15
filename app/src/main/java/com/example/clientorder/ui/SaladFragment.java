package com.example.clientorder.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.clientorder.MenuActivities.CaptionedImagesAdapter;
import com.example.clientorder.ObjectsAndAdapters.Products;
import com.example.clientorder.QuestionsDatabaseHelper;
import com.example.clientorder.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import java.util.ArrayList;


public class SaladFragment extends Fragment implements ValueEventListener {



    private RecyclerView mainActivityRecyclerView;
    FirebaseDatabase firebaseDatabase;
    boolean[] availabilityTable;
    int[] idTable;
    String[] nameTable;
    Double[] priceTable;
    String[] descriptionTable;
    String[] typeTable;
    String[] nameENGTable;
    String[] desENGTable;

    boolean[] availabilityTable1;
    int[] idTable1;
    String[] nameTable1;
    Double[] priceTable1;
    String[] descriptionTable1;
    String[] typeTable1;
    private boolean availabilityBolean;
    private String name;
    private double price;
   int idProduct;
    String description;
    String type;
    String[] nameENGTable1;
    String[] desENGTable1;

    @SuppressLint("StaticFieldLeak")
    public static ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_salad, container, false);

        mainActivityRecyclerView = layout.findViewById(R.id.Fragment_salad_recycler);


        firebaseDatabase = FirebaseDatabase.getInstance();

        progressBar = layout.findViewById(R.id.ProgressBar);






        return layout;


    }

    @Override
    public void onPause() {
        super.onPause();
        firebaseDatabase.getReference().child("Products").orderByChild("numberOfList").removeEventListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseDatabase.getReference().child("Products").orderByChild("numberOfList").addValueEventListener(this);

    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        int count = 0;

        int size = (int) dataSnapshot.getChildrenCount();

        ArrayList<Boolean> cityList = new ArrayList<>();
        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
            Products products = postSnapshot.getValue(Products.class);
            assert products != null;
            cityList.add(products.availability);
        }

        ArrayList<Integer> cityList2 = new ArrayList<>();
        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
            Products products = postSnapshot.getValue(Products.class);
            assert products != null;
            cityList2.add(products.id);
        }
        ArrayList<String> cityList3 = new ArrayList<>();
        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
            Products products = postSnapshot.getValue(Products.class);
            assert products != null;
            cityList3.add(products.name);
        }

        ArrayList<Double> cityList4 = new ArrayList<>();
        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
            Products products = postSnapshot.getValue(Products.class);
            assert products != null;
            cityList4.add(products.price);
        }

        ArrayList<String> cityList5 = new ArrayList<>();
        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
            Products products = postSnapshot.getValue(Products.class);
            assert products != null;
            cityList5.add(products.description);
        }

        ArrayList<String> cityList6 = new ArrayList<>();
        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
            Products products = postSnapshot.getValue(Products.class);
            assert products != null;
            cityList6.add(products.type);
        }

        ArrayList<String> cityList7 = new ArrayList<>();
        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
            Products products = postSnapshot.getValue(Products.class);
            assert products != null;
            cityList7.add(products.nameENG);
        }

        ArrayList<String> cityList8 = new ArrayList<>();
        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
            Products products = postSnapshot.getValue(Products.class);
            assert products != null;
            cityList8.add(products.descriptionENG);
        }
        nameENGTable = new String[size];
        for (int i = 0; i < size; i++)
        {
            nameENGTable[i] = cityList7.get(i);
        }
        desENGTable = new String[size];
        for (int i = 0; i < size; i++)
        {
            desENGTable[i] = cityList8.get(i);
        }

        availabilityTable = new boolean[size];
        for (int i = 0; i < size; i++)
        {
            availabilityTable[i] = cityList.get(i);
        }

        idTable = new int[size];
        for (int i = 0; i < size; i++)
        {
            idTable[i] = cityList2.get(i);
        }

        nameTable = new String[size];
        for (int i = 0; i < size; i++)
        {
            nameTable[i] = cityList3.get(i);
        }

        priceTable = new Double[size];
        for (int i = 0; i < size; i++)
        {
            priceTable[i] = cityList4.get(i);
        }

        descriptionTable = new String[size];
        for (int i = 0; i < size; i++)
        {
            descriptionTable[i] = cityList5.get(i);
        }

        typeTable = new String[size];
        for (int i = 0; i < size; i++)
        {
            typeTable[i] = cityList6.get(i);
        }

        for (int i =0; i < size; i++)
        {
            String type;
            type = typeTable[i];
            if (type.equals("salad"))
            {
                count++;
            }
        }

        availabilityTable1 = new boolean[count];
        idTable1 = new int[count];
        nameTable1 = new String[count];
        priceTable1 = new Double[count];
        descriptionTable1 = new String[count];
        typeTable1 = new String[count];
        nameENGTable1 = new String[count];
        desENGTable1 = new String[count];

        int coun1 = 0;
        for (int i =0; i < size; i++)
        {
            String type;
            type = typeTable[i];
            if (type.equals("salad"))
            {
                availabilityTable1[coun1] = availabilityTable[i];
                nameTable1[coun1] = nameTable[i];
                priceTable1[coun1] = priceTable[i];
                descriptionTable1[coun1] = descriptionTable[i];
                availabilityTable1[coun1] = availabilityTable[i];
                typeTable1[coun1] = typeTable[i];
                nameENGTable1[coun1] = nameENGTable[i];
                desENGTable1[coun1] = desENGTable[i];
                coun1++;
            }
        }

        CaptionedImagesAdapter adapter;
        if (getResources().getString(R.string.language).equals("PL"))
        {
            adapter = new CaptionedImagesAdapter(nameTable1, priceTable1, descriptionTable1);

        }
        else
        {
            adapter = new CaptionedImagesAdapter(nameENGTable1, priceTable1, desENGTable1);

        }
        mainActivityRecyclerView.setAdapter(adapter);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mainActivityRecyclerView.setLayoutManager(layoutManager);
        adapter.setListener(position -> {


            availabilityBolean =availabilityTable1[position];
            price = priceTable1[position];
            idProduct = idTable1[position];
            type = typeTable1[position];
            if (getResources().getString(R.string.language).equals("PL"))
            {
                name = nameTable1[position];
                description = descriptionTable1[position];
            }
            else
            {
                name = nameENGTable1[position];
                description = desENGTable1[position];
            }
showAlertDialogButtonClicked();


        });


        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
    public void showAlertDialogButtonClicked() {

        final EditText edittext = new EditText(getContext());
        edittext.setInputType(2);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Theme_ClientOrder);
        builder.setTitle(getResources().getString(R.string.addTitle));
        builder.setMessage(getResources().getString(R.string.addMessage));
        builder.setView(edittext);

        builder.setPositiveButton(getResources().getString(R.string.add), (dialog, which) -> {
            int doubleOrder;
            boolean fail = false;
            try {
                String YouEditTextValue = edittext.getText().toString();
                doubleOrder= Integer.parseInt(YouEditTextValue);
            }
            catch (Exception e)
            {
                doubleOrder = 1;
            }



            if (availabilityBolean)
            {
                try {
                    SQLiteOpenHelper questionsDatabaseHelper = new QuestionsDatabaseHelper(getContext());
                    SQLiteDatabase db = questionsDatabaseHelper.getWritableDatabase();


                    if (doubleOrder == 0)
                    {
                        fail = true;
                        Toast.makeText(getContext(), getResources().getString(R.string.cannotBeEmpty), Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        fail = false;
                        if (doubleOrder >1)
                        {
                            name = name + " x" + doubleOrder;
                            price = price*doubleOrder;
                        }
                        ContentValues Info1 = new ContentValues();
                        Info1.put("NAME", name);
                        Info1.put("PRICe",price);

                        db.insert("BASKET", null, Info1);


                        db.close();
                    }

                }
                catch (Exception e)
                {
                    Toast.makeText(getContext(), "Database error", Toast.LENGTH_SHORT).show();

                }
                finally {
                    if (!fail)
                    {
                        Toast.makeText(getContext(), getResources().getString(R.string.toBAskedSucces), Toast.LENGTH_SHORT).show();
                    }

                }
            }
            else {
                Toast.makeText(getContext(), getResources().getString(R.string.noMagazine), Toast.LENGTH_SHORT).show();
            }



        });


        builder.setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> Toast.makeText(getContext(), getResources().getString(R.string.canceled), Toast.LENGTH_SHORT).show());



        AlertDialog dialog = builder.create();
        dialog.setIcon(R.drawable.icon);
        dialog.getWindow().setLayout(1000, 600);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.password_background);
        dialog.show();

    }

}