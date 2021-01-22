package com.example.tabactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.text.format.DateUtils.MINUTE_IN_MILLIS;

public class SearchActivity extends AppCompatActivity implements NewsAdapter.NewsAdapterOnClickHandler {

    private RecyclerView mRecyclerView;
    private com.example.tabactivity.NewsAdapter mNewsAdapter;
    private ArrayList<News> newsList;
    private ProgressBar mProgressBar;
    private TextView textView;
    boolean isScrolling=false;
    int scrolledItem,totalItem,visibleItem;
    private int n=2;
    private String loadMoreUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        String query = intent.getStringExtra("q_key");

        textView = findViewById(R.id.new_text);
        textView.setText(query);
        mProgressBar = findViewById(R.id.progress_bar2);
        mRecyclerView = findViewById(R.id.recycler_view2);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mNewsAdapter = new NewsAdapter(this);
        mRecyclerView.setAdapter(mNewsAdapter);
        String myUrl ="https://content.guardianapis.com/search?q=\""+query+"\"&api-key=b7a2248e-ad8b-438b-a8d7-152183169a29&show-fields=thumbnail,headline&page-size=10";
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrolledItem=layoutManager.findFirstVisibleItemPosition();
                visibleItem=layoutManager.getChildCount();
                totalItem=layoutManager.getItemCount();
                if(isScrolling && totalItem==scrolledItem+visibleItem){
                    isScrolling=false;
                    loadMoreUrl=myUrl+"&page="+n;
                    n++;
                    loadMore(loadMoreUrl);
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling=true;
                }
            }
        });
        fetchQueryData(myUrl);

    }
    public void fetchQueryData(String mUrl){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, mUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject root=null;
                        newsList = new ArrayList<News>();
                        try {
                            root = response.getJSONObject("response");
                            JSONArray results = root.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject jsonObject = results.getJSONObject(i);
                                String webUrl = jsonObject.getString("webUrl");
                                String section = jsonObject.getString("sectionName");
                                String date = jsonObject.getString("webPublicationDate");

                                JSONObject fields = jsonObject.getJSONObject("fields");
                                String headline = fields.getString("headline");
                                String imgUrl = null;
                                if (fields.has("thumbnail")) imgUrl = fields.getString("thumbnail");

                                String finalTime=formatTime(date);

                                News news = new News(headline, webUrl, imgUrl, section,finalTime);
                                newsList.add(news);
                                Log.d("list-size", String.valueOf(newsList.size()));
                            }

                        } catch (Exception e) {
                            Log.e("requestError", "api call error");
                        }

                        mNewsAdapter.setNewsData(newsList);
                        mProgressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("api error","api call error");
                    }
                });

        mProgressBar.setVisibility(View.VISIBLE);
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }
    void loadMore(String nUrl){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, nUrl, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONObject root=null;
                        try {
                            root = response.getJSONObject("response");
                            JSONArray results = root.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject jsonObject = results.getJSONObject(i);
                                String webUrl = jsonObject.getString("webUrl");
                                String section = jsonObject.getString("sectionName");
                                String date = jsonObject.getString("webPublicationDate");
                                JSONObject fields = jsonObject.getJSONObject("fields");
                                String headline = fields.getString("headline");
                                String imgUrl = null;
                                if (fields.has("thumbnail")) imgUrl = fields.getString("thumbnail");

                                String finalTime=formatTime(date);

                                News news = new News(headline, webUrl, imgUrl, section,finalTime);
                                newsList.add(news);
                            }

                        } catch (Exception e) {
                            Log.e("requestError", "api call error");
                        }

                        mNewsAdapter.setNewsData(newsList);
//                        mProgressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("api error","api call error");
                    }
                });

//        mProgressBar.setVisibility(View.VISIBLE);
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    @Override
    public void onClick(News news) {
        String url = news.url;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(this,R.color.colorPrimary));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url));
    }
    public String formatTime(String givenDate)  {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date fDate;
        long timeInMilliseconds=0;
        try {
            fDate = inputFormat.parse(givenDate);
            timeInMilliseconds = fDate.getTime();
        }catch(Exception e){
            Log.d("formatting_error","problem formatting time");
        }
//        assert fDate != null;
//        String formattedDate = outputFormat.format(fDate);
        String finalTime= (String) DateUtils.getRelativeTimeSpanString(timeInMilliseconds,System.currentTimeMillis(),MINUTE_IN_MILLIS);
        return finalTime;

    }
}