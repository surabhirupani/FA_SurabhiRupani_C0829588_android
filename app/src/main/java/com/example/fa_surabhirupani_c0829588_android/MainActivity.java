package com.example.fa_surabhirupani_c0829588_android;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fa_surabhirupani_c0829588_android.Adapter.PlaceAdapter;
import com.example.fa_surabhirupani_c0829588_android.Database.AppDatabase;
import com.example.fa_surabhirupani_c0829588_android.Database.DAO;
import com.example.fa_surabhirupani_c0829588_android.Model.Place;
import com.example.fa_surabhirupani_c0829588_android.helper.SwipeHelper;
import com.example.fa_surabhirupani_c0829588_android.helper.SwipeUnderlayButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DAO dao;
    private View llEmptyBox;
    private List<Place> placeList, searchedLists;
    private PlaceAdapter placeAdapter;
    Toolbar toolbar;
    SwipeHelper swipeHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dao = AppDatabase.getDb(this).getDAO();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Favourite Places");
        setSupportActionBar(toolbar);

        llEmptyBox = findViewById(R.id.llEmptyBox);

        FloatingActionButton floatingActionButton = findViewById(R.id.fabNewListItem);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        placeList = new ArrayList<>();
        searchedLists = new ArrayList<>();
        insertPlaces();
        placeAdapter = new PlaceAdapter(searchedLists);

        RecyclerView recyclerViewTodoList = findViewById(R.id.recyclerViewPlaces);
        recyclerViewTodoList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTodoList.setHasFixedSize(true);
        recyclerViewTodoList.setAdapter(placeAdapter);

        getPlaceItems();

        // using SwipeHelper class
        swipeHelper = new SwipeHelper(this, 300, recyclerViewTodoList) {
            @Override
            protected void instantiateSwipeButton(RecyclerView.ViewHolder viewHolder, List<SwipeUnderlayButton> buffer) {
                buffer.add(new SwipeUnderlayButton(MainActivity.this,
                        "Delete",
                        R.drawable.ic_delete_white,
                        30,
                        50,
                        Color.parseColor("#ff3c30"),
                        SwipeDirection.LEFT,
                        new SwipeUnderlayButtonClickListener() {
                            @Override
                            public void onClick(int position) {
                                final Place place = placeList.get(position);
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Delete Place");
                                builder.setMessage("Are you sure you want to delete place?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface di, int i) {
                                        di.dismiss();
                                        dao.deletePlace(place.getPlaceId());
                                        getPlaceItems();
                                        Toast.makeText(getApplicationContext(), "Place deleted!", Toast.LENGTH_LONG).show();
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        }));
                buffer.add(new SwipeUnderlayButton(MainActivity.this,
                        "Visited",
                        R.drawable.ic_baseline_edit_location_alt_24,
                        30,
                        50,
                        Color.parseColor("#FF851299"),
                        SwipeDirection.LEFT,
                        new SwipeUnderlayButtonClickListener() {
                            @Override
                            public void onClick(int position) {
                                final Place place = placeList.get(position);
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Visited Place");
                                builder.setMessage("Have you visited this place?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface di, int i) {
                                        di.dismiss();
                                        place.setVisited(true);
                                        dao.insertPlace(place);
                                        getPlaceItems();
                                        Toast.makeText(getApplicationContext(), "Place Visited!", Toast.LENGTH_LONG).show();
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        }));
                buffer.add(new SwipeUnderlayButton(MainActivity.this,
                        "Update",
                        R.drawable.ic_update_white,
                        30,
                        50,
                        Color.parseColor("#ff9502"),
                        SwipeDirection.LEFT,
                        new SwipeUnderlayButtonClickListener() {
                            @Override
                            public void onClick(int position) {
                                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                                intent.putExtra("place", placeList.get(position));
                                startActivity(intent);
                            }
                        }));
            }
        };
    }

    private void insertPlaces() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            // <---- run your one time code here
            Place place1 = new Place();
            place1.setPlaceName("India");
            place1.setPlaceLat(42.546245);
            place1.setPlaceLong(1.601554);
            place1.setVisited(true);
            placeList.add(place1);

            Place place2 = new Place();
            place2.setPlaceName("Australia");
            place2.setPlaceLat(23.424076);
            place2.setPlaceLong(53.847818);
            place2.setVisited(true);
            placeList.add(place2);

            Place place3 = new Place();
            place3.setPlaceName("Paris");
            place3.setPlaceLat(33.93911);
            place3.setPlaceLong(67.709953);
            place3.setVisited(false);
            placeList.add(place3);

            Place place4 = new Place();
            place4.setPlaceName("France");
            place4.setPlaceLat(56.130366);
            place4.setPlaceLong(-106.346771);
            place4.setVisited(false);
            placeList.add(place4);

            Place place5 = new Place();
            place5.setPlaceName("Canada");
            place5.setPlaceLat(28.033886);
            place5.setPlaceLong(1.659626);
            place5.setVisited(false);
            placeList.add(place5);

            Place place6 = new Place();
            place6.setPlaceName("USA");
            place6.setPlaceLat(26.820553);
            place6.setPlaceLong(30.802498);
            place6.setVisited(true);
            placeList.add(place6);

            Place place7 = new Place();
            place7.setPlaceName("London");
            place7.setPlaceLat(55.378051);
            place7.setPlaceLong(-3.435973);
            place7.setVisited(true);
            placeList.add(place7);

            Place place8 = new Place();
            place8.setPlaceName("Pakistan");
            place8.setPlaceLat(49.465691);
            place8.setPlaceLong(-2.585278);
            place8.setVisited(false);
            placeList.add(place8);

            Place place9 = new Place();
            place9.setPlaceName("New Zealand");
            place9.setPlaceLat(20.593684);
            place9.setPlaceLong(78.96288);
            place9.setVisited(true);
            placeList.add(place9);

            Place place10 = new Place();
            place10.setPlaceName("Melbourne");
            place10.setPlaceLat(37.09024);
            place10.setPlaceLong(-95.712891);
            place10.setVisited(false);
            placeList.add(place10);

            for (int i=0;i<placeList.size();i++) {
                dao.insertPlace(placeList.get(i));
            }
            // mark first time has ran.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }
    }

    private void getPlaceItems() {
        placeList.clear();
        searchedLists.clear();
        List<Place> placeList1 = dao.getPlaces();
        if (placeList1.isEmpty())
            llEmptyBox.setVisibility(View.VISIBLE);
        else {
            llEmptyBox.setVisibility(View.GONE);
            placeList.addAll(placeList1);
            searchedLists.addAll(placeList1);
            placeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getPlaceItems();
    }
}