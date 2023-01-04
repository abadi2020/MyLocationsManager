package com.example.abasad.mylocationsmanager;

import com.google.android.gms.maps.model.LatLng;

public class Place {
    public String comment;
    public double latitude;
    public double longitude;
    public boolean haveBeen;

    public Place(double lat, double longit ){latitude = lat; longitude = longit;}
    public Place(String Comment, double lat, double longit ){latitude = lat; longitude = longit; comment = Comment; haveBeen = false;}
    public Place(String Comment, double lat, double longit, boolean haveBeen ){latitude = lat; longitude = longit; comment = Comment; this.haveBeen = haveBeen; }
}
