package com.testtask.pexels;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment; // Используем androidx.fragment.app.Fragment

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.testtask.pexels.adapters.WallpaperAdapter;
import com.testtask.pexels.models.WallpaperModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeJavaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView.Adapter<?> adapter;
    private WallpaperAdapter wallpaperAdapter;
    private List<WallpaperModel> wallpaperModelList;
    private ProgressBar progressBar;
    private LinearLayout contentView;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private RecyclerView topMostRecyclerView;
    private EditText searchEt;
    private ImageView searchIv;

    private boolean isScrolling = false;
    private int currentItems = 0;
    private int totalItems = 0;
    private int scrollOutItems = 0;

    private int pageNumber = 1;
    private static final String BASE_URL = "https://api.pexels.com/v1/curated";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_java);

        if (savedInstanceState == null) {
            loadFragment(new HomeJavaFragment());
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.home) {
                    loadFragment(new HomeJavaFragment());
                    return true;
                } else if (id == R.id.favorites) {
                    loadFragment(new BookmarksJavaFragment());
                    return true;
                }
                return false;
            }
        });

        wallpaperModelList = new ArrayList<>();
        wallpaperAdapter = new WallpaperAdapter(this, wallpaperModelList);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(wallpaperAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentItems = gridLayoutManager.getChildCount();
                totalItems = gridLayoutManager.getItemCount();
                scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    fetchWallpaper();
                }
            }
        });

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        contentView = findViewById(R.id.content_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        searchEt = findViewById(R.id.searchEv);
        searchIv = findViewById(R.id.search_image);

        searchIv.setOnClickListener(v ->
                Toast.makeText(HomeJavaActivity.this, "Search Button Clicked", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void fetchWallpaper() {
        String url = BASE_URL + "?page=" + pageNumber + "&per_page=80";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    progressBar.setVisibility(View.GONE);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("photos");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsObject = jsonArray.getJSONObject(i);
                            int id = jsObject.getInt("id");
                            String photographerName = jsObject.getString("photographer");

                            JSONObject objectImage = jsObject.getJSONObject("src");
                            String originalUrl = objectImage.getString("original");
                            String mediumUrl = objectImage.getString("medium");

                            WallpaperModel wallpaperModel = new WallpaperModel(id, originalUrl, mediumUrl, photographerName);
                            wallpaperModelList.add(wallpaperModel);
                        }

                        wallpaperAdapter.notifyDataSetChanged();
                        pageNumber++;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(HomeJavaActivity.this, "Failed to fetch wallpapers", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "BylC73uP6L7DLisjseaGqtcOq4LMlaWGIq2RGzc6quw7Cr2Op1me8Res");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    // Implementing onItemClick in case it's needed
    public void onItemClick(int position) {
        // Handle item click event here
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
