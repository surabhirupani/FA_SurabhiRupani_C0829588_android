package com.example.fa_surabhirupani_c0829588_android.Database;



import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.fa_surabhirupani_c0829588_android.Model.Place;

import java.util.List;

@Dao
public interface DAO {
    //Insert Querys
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlace(Place place);

    //Delete Querys
    @Query("DELETE FROM place WHERE placeId = :placeId")
    void deletePlace(Long placeId);

    @Query("SELECT * FROM place")
    List<Place> getPlaces();
}
