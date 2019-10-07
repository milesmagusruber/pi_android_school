package com.milesmagusruber.beerpatrol.db.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_beers")
public class FavoriteBeer {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "beerId")
    private String beerId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "category")
    private String category = null;

    @ColumnInfo(name = "glass")
    private String glass = null;

    @ColumnInfo(name = "abv")
    private String abv = null;

    @ColumnInfo(name = "ibu")
    private String ibu = null;

    @ColumnInfo(name = "description")
    private String description = null;

    @ColumnInfo(name = "icon")
    private String icon = null;

    @ColumnInfo(name = "image")
    private String image = null;

    public FavoriteBeer(@NonNull String beerId, String name, String category, String glass, String abv,
                        String ibu, String description, String icon, String image) {
        this.beerId = beerId;
        this.name = name;
        this.category = category;
        this.glass = glass;
        this.abv = abv;
        this.ibu = ibu;
        this.description = description;
        this.icon = icon;
        this.image = image;
    }

    public void setBeerId(@NonNull String beerId) {
        this.name = beerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setGlass(String glass) {
        this.glass = glass;
    }

    public void setAbv(String abv) {
        this.abv = abv;
    }

    public void setIbu(String ibu) {
        this.ibu = ibu;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBeerId() {
        return beerId;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getCategory() {
        return category;
    }

    public String getGlass() {
        return glass;
    }

    public String getAbv() {
        return abv;
    }

    public String getIbu() {
        return ibu;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

}
