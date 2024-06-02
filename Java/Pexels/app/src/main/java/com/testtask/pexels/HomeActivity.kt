package com.testtask.pexels

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.testtask.pexels.adapters.WallpaperAdapter
import com.testtask.pexels.interfaces.RecyclerViewClickLictener
import com.testtask.pexels.models.WallpaperModel
import org.json.JSONException
import org.json.JSONObject

class HomeActivity : AppCompatActivity(), RecyclerViewClickLictener {
    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var wallpaperAdapter: WallpaperAdapter
    private lateinit var wallpaperModelList: List<WallpaperModel>
    private lateinit var progressBar: ProgressBar
    private lateinit var contentView: LinearLayout
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var topMostRecyclerView: RecyclerView
    private lateinit var searchEt: EditText
    private lateinit var searchIv: ImageView

    private var isScrolling = false
    private var currentItems = 0
    private var totalItems = 0
    private var scrollOutItems = 0

    var pageNumber = 1
    val url = "https://api.pexels.com/v1/curated?page=$pageNumber&per_page=80"

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.favorites -> {
                    loadFragment(BookmarksFragment())
                    true
                }
                else -> false
            }
        }

        wallpaperModelList = ArrayList()
        wallpaperAdapter = WallpaperAdapter(this, wallpaperModelList)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = wallpaperAdapter


        val gridLayoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = gridLayoutManager

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                currentItems = gridLayoutManager.childCount
                totalItems = gridLayoutManager.itemCount
                scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition()

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false
                    fetchWallpaper()
                }
            }
        })

        progressBar = findViewById(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE

        contentView = findViewById(R.id.content_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        searchEt = findViewById(R.id.searchEv)
        searchIv = findViewById(R.id.search_image)

        searchIv.setOnClickListener {
            Toast.makeText(this@HomeActivity, "Search Button Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment).commit()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun fetchWallpaper() {
        val request = object : StringRequest(Method.GET, url,
            Response.Listener<String> { response ->
                progressBar.visibility = View.GONE

                try {
                    val jsonObject = JSONObject(response)
                    val jsonArray = jsonObject.getJSONArray("photos")

                    for (i in 0 until jsonArray.length()) {
                        val jsObject = jsonArray.getJSONObject(i)
                        val id = jsObject.getInt("id")
                        val photographerName = jsObject.getString("photographer")

                        val objectImage = jsObject.getJSONObject("src")
                        val originalUrl = objectImage.getString("original")
                        val mediumUrl = objectImage.getString("medium")

                        val wallpaperModel = WallpaperModel(id, originalUrl, mediumUrl, photographerName)
                        wallpaperModelList += wallpaperModel

                    }

                    wallpaperAdapter.notifyDataSetChanged()
                    pageNumber++
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                progressBar.visibility = View.GONE
                Toast.makeText(this@HomeActivity, "Failed to fetch wallpapers", Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "BylC73uP6L7DLisjseaGqtcOq4LMlaWGIq2RGzc6quw7Cr2Op1me8Res"
                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(request)
    }


    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }
}


