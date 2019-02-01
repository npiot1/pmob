package com.example.piotn.pmob_td;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class Film {

    private String nom;
    private String date;
    private String real;
    private Bitmap image = null;

    public Film(String nom, String date, String real, Bitmap image) {
        this.nom = nom;
        this.date = date;
        this.real = real;
        this.image = image;

    }

    public Film(String nom, String date, String real) {
        this.nom = nom;
        this.date = date;
        this.real = real;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReal() {
        return real;
    }

    public void setReal(String real) {
        this.real = real;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
