package com.milesmagusruber.beerpatrol.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BreweryDBResponse<T> {
    //current page of all result pages
    @SerializedName("currentPage")
    @Expose
    private int currentPage;

    //the number of all pages
    @SerializedName("numberOfPages")
    @Expose
    private int numberOfPages;

    //the number of data results
    @SerializedName("totalResults")
    @Expose
    private int totalResults;

    //list of data objects
    @SerializedName("data")
    @Expose
    private List<T> data;

    //status of request
    @SerializedName("status")
    @Expose
    private String status;

    public int getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getNumberOfPages() {
        return this.numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public int getTotalResults() {
        return this.totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<T> getData() {
        return this.data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
