package com.testtask.pexelsgit 
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.testtask.pexels.R
import com.testtask.pexels.adapters.WallpaperAdapter
import com.testtask.pexels.models.WallpaperModel
import org.json.JSONException
import org.json.JSONObject

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var wallpaperAdapter: WallpaperAdapter
    private lateinit var wallpaperModelList: MutableList<WallpaperModel>
    private lateinit var progressBar: ProgressBar

    private var isScrolling = false
    private var currentItems = 0
    private var totalItems = 0
    private var scrollOutItems = 0

    private var pageNumber = 1
    private val url get() = "https://api.pexels.com/v1/search?query=nature&page=$pageNumber&per_page=80"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progress_bar)

        wallpaperModelList = ArrayList()
        wallpaperAdapter = WallpaperAdapter(requireContext(), wallpaperModelList)

        recyclerView.adapter = wallpaperAdapter
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val gridLayoutManager = recyclerView.layoutManager as GridLayoutManager
                currentItems = gridLayoutManager.childCount
                totalItems = gridLayoutManager.itemCount
                scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition()

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false
                    pageNumber++
                    fetchWallpaper()
                }
            }
        })

        fetchWallpaper()
        return view
    }

    private fun fetchWallpaper() {
        progressBar.visibility = View.VISIBLE

        val request = object : StringRequest(
            Method.GET, url,
            Response.Listener<String> { response ->
                progressBar.visibility = View.GONE
                Log.d("HomeFragment", "Response: $response")

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
                        wallpaperModelList.add(wallpaperModel)
                    }

                    wallpaperAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    Log.e("HomeFragment", "JSON Parsing error", e)
                }
            },
            Response.ErrorListener { error ->
                progressBar.visibility = View.GONE
                Log.e("HomeFragment", "Volley error", error)
                Toast.makeText(context, "Failed to fetch wallpapers", Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "BylC73uP6L7DLisjseaGqtcOq4LMlaWGIq2RGzc6quw7Cr2Op1me8Res"
                return params
            }
        }

        val requestQueue = Volley.newRequestQueue(requireContext())
        requestQueue.add(request)
    }
}
