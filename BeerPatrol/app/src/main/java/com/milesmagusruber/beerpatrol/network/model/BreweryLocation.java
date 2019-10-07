package com.milesmagusruber.beerpatrol.network.model;

import com.google.gson.annotations.SerializedName;

public class BreweryLocation {
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("streetAddress")
    public String streetAddress;

    @SerializedName("locality")
    public String locality;

    @SerializedName("region")
    public String region;

    @SerializedName("postalCode")
    public String postalCode;

    @SerializedName("latitude")
    public double latitude;

    @SerializedName("longitude")
    public double longitude;

    @SerializedName("distance")
    public double distance;

    @SerializedName("brewery")
    public Brewery brewery;

    public class Brewery {
        @SerializedName("id")
        public String id;

        @SerializedName("name")
        public String name;

        @SerializedName("nameShortDisplay")
        public String nameShortDisplay;

        @SerializedName("description")
        public String description;

        @SerializedName("website")
        public String website;

        @SerializedName("established")
        public String established;

        @SerializedName("images")
        public Images images;

        public class Images {

            @SerializedName("icon")
            public String icon;

            @SerializedName("medium")
            public String medium;

            @SerializedName("large")
            public String large;

            @SerializedName("squareMedium")
            public String squareMedium;

            @SerializedName("squareLarge")
            public String squareLarge;
        }

    }

}
