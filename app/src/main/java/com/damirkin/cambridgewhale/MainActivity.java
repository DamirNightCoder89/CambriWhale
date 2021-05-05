package com.damirkin.cambridgewhale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {  // implements MainFragment.OnFragmentSendDataListener

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

//    @Override
//    public void onSendData(String selectedItem) {
//        DetailFragment fragment = (DetailFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.detailFragment);
//        if (fragment != null)
//            fragment.setSelectedItem(selectedItem);
//    }

}