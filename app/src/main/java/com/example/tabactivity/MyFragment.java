package com.example.tabactivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.text.format.DateUtils.MINUTE_IN_MILLIS;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MyFragment extends Fragment implements NewsAdapter.NewsAdapterOnClickHandler {

    private Context mContext;
    private String mUrl;
    private String HOME = "https://content.guardianapis.com/search?api-key=b7a2248e-ad8b-438b-a8d7-152183169a29&show-fields=thumbnail,headline&page-size=10";
    private String TECH = "https://content.guardianapis.com/technology?api-key=b7a2248e-ad8b-438b-a8d7-152183169a29&show-fields=thumbnail,headline&page-size=10";
    private String sports = "https://content.guardianapis.com/sport?api-key=b7a2248e-ad8b-438b-a8d7-152183169a29&show-fields=thumbnail,headline&page-size=10";
    private String BUSINESS = "https://content.guardianapis.com/business?api-key=b7a2248e-ad8b-438b-a8d7-152183169a29&show-fields=thumbnail,headline&page-size=10";
    private String environment = "https://content.guardianapis.com/environment?api-key=b7a2248e-ad8b-438b-a8d7-152183169a29&show-fields=thumbnail,headline&page-size=10";
    private String lifeandstyle = "https://content.guardianapis.com/lifeandstyle?api-key=b7a2248e-ad8b-438b-a8d7-152183169a29&show-fields=thumbnail,headline&page-size=10";
    private String science = "https://content.guardianapis.com/science?api-key=b7a2248e-ad8b-438b-a8d7-152183169a29&show-fields=thumbnail,headline&page-size=10";
    private RecyclerView mRecyclerView;
    private com.example.tabactivity.NewsAdapter mNewsAdapter;
    private ArrayList<News> newsList;
    private ProgressBar mProgressBar;
    boolean isScrolling=false;
    int scrolledItem,totalItem,visibleItem;
    private int n=2;
    String loadMoreUrl;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext=context;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragTab1.
     */
    // TODO: Rename and change types and number of parameters
    public static MyFragment newInstance(String param1, String param2) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putSerializable("saved_key",newsList);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mNewsAdapter = new NewsAdapter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mNewsAdapter);
        switch (mParam1) {
            case "1":
                mUrl = HOME;
                break;
            case "2":
                mUrl = TECH;
                break;
            case "3":
                mUrl = BUSINESS;
                break;
            case "4":
                mUrl = sports;
                break;
            case "5":
                mUrl = science;
                break;
            case "6":
                mUrl = environment;
                break;
            default:
                mUrl = lifeandstyle;
                break;
        }
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrolledItem=layoutManager.findFirstVisibleItemPosition();
                visibleItem=layoutManager.getChildCount();
                totalItem=layoutManager.getItemCount();
                if(isScrolling && totalItem==scrolledItem+visibleItem){
                    isScrolling=false;
                    loadMoreUrl=mUrl+"&page="+n;
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

        if (internet_connection()) fetchData();
        else Toast.makeText(mContext,R.string.try_again,Toast.LENGTH_LONG).show();

        return view;
    }

    @Override
    public void onClick(News news) {

        String url = news.url;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(mContext, Uri.parse(url));
    }
    public void fetchData(){

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
        MySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);

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
        MySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);

    }

    public boolean internet_connection(){
        //Check if connected to internet, output accordingly
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
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