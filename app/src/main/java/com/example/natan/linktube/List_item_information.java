package com.example.natan.linktube;

import com.google.api.services.youtube.model.SearchResult;

import java.util.List;

/**
 * Created by natan on 7/11/2016.
 */
public class List_item_information {
    int position;
    List<SearchResult> results;

    public List_item_information(List<SearchResult> results){
        this.results = results;
        position = 0;
    }
}
