package com.milesmagusruber.beerpatrol.network.model;

import com.google.gson.annotations.SerializedName;

public class Beer {
    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("nameDisplay")
    public String nameDisplay;

    @SerializedName("description")
    public String description;

    @SerializedName("styleId")
    public Integer styleId;

    @SerializedName("glasswareId")
    public Integer glasswareId;

    @SerializedName("abv")
    public String abv;

    @SerializedName("ibu")
    public String ibu;

    @SerializedName("labels")
    public Labels labels;

    @SerializedName("style")
    public Style style;

    @SerializedName("glass")
    public Glass glass;

    public class Labels {

        @SerializedName("icon")
        public String icon;

        @SerializedName("medium")
        public String medium;

        @SerializedName("large")
        public String large;

    }

    public class Style {

        @SerializedName("id")
        public Integer id;

        @SerializedName("categoryId")
        public Integer categoryId;

        @SerializedName("category")
        public Category category;

        @SerializedName("name")
        public String name;

        @SerializedName("shortName")
        public String shortName;

        @SerializedName("description")
        public String description;

        public class Category {

            @SerializedName("id")
            public Integer id;

            @SerializedName("name")
            public String name;

            @SerializedName("createDate")
            public String createDate;

        }
    }

    public class Glass {
        @SerializedName("id")
        public Integer id;

        @SerializedName("name")
        public String name;

        @SerializedName("createDate")
        public String createDate;
    }

}
