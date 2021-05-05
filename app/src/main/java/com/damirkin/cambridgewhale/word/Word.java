package com.damirkin.cambridgewhale.word;

import android.net.Uri;
import android.text.TextUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Word {

    private String name;
    private String us_trans;
    private String uk_trans;
    private Uri us_audio;
    private Uri uk_audio;
    private ArrayList<String> variants = new ArrayList<String>();
    private ArrayList<WordVar> examples = new ArrayList<>();
    private int rating;
    private boolean turner = false;


    public ArrayList<WordVar> getExamples() {
        return examples;
    }

    public void setExample(WordVar example) {
        this.examples.add(example);
    }

    public void deteteAll() {
        name = "";
        us_trans = "";
        uk_trans = "";
        us_audio = null;
        uk_audio = null;
        variants.clear();
        examples = new ArrayList<>();
        rating = 0;
        turner = false;
        System.out.println("очистка ворда");

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUs_trans() {
        return us_trans;
    }

    public void setUs_trans(String us_trans) {
        this.us_trans = us_trans;
    }

    public String getUk_trans() {
        return uk_trans;
    }

    public void setUk_trans(String uk_trans) {
        this.uk_trans = uk_trans;
    }

    public Uri getUs_audio() {
        return us_audio;
    }

    public void setUs_audio(Uri us_audio) {
        this.us_audio = us_audio;
    }

    public Uri getUk_audio() {
        return uk_audio;
    }

    public void setUk_audio(Uri uk_audio) {
        this.uk_audio = uk_audio;
    }

    public ArrayList<String> getVariants() {
        return variants;
    }

    public void setVariant(String variant) {
        this.variants.add(variant);
    }


}

