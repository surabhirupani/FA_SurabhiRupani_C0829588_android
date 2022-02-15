package com.example.fa_surabhirupani_c0829588_android.Model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "place")
public class Place implements Serializable {
    @PrimaryKey
    private Long placeId;

    @ColumnInfo
    private String placeName;

    @ColumnInfo
    private String createDate;

    @ColumnInfo
    private Boolean isVisited;

    @ColumnInfo
    private Double placeLat;

    @ColumnInfo
    private Double placeLong;

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Boolean getVisited() {
        return isVisited;
    }

    public void setVisited(Boolean visited) {
        isVisited = visited;
    }

    public Double getPlaceLat() {
        return placeLat;
    }

    public void setPlaceLat(Double placeLat) {
        this.placeLat = placeLat;
    }

    public Double getPlaceLong() {
        return placeLong;
    }

    public void setPlaceLong(Double placeLong) {
        this.placeLong = placeLong;
    }
}
