package mohammad.julfikar.com.offeries;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import mohammad.julfikar.com.offeries.Feed.AppController;
import mohammad.julfikar.com.offeries.Feed.FeedItem;
import mohammad.julfikar.com.offeries.Feed.FeedListAdapter;
import mohammad.julfikar.com.offeries.Helper.Config;
import mohammad.julfikar.com.offeries.Helper.Helper;
import mohammad.julfikar.com.offeries.Helper.SwipeDetector;


public class StoryBoard extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = StoryBoard.class.getSimpleName();
    private ListView listView;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private String URL_FEED = Config.DATA_URL;
    private MaterialProgressBar progressBar;
    private LinearLayout linearLayout;
    private TextView tv_loading_feed;

    private FloatingActionButton fab_search;
    private FloatingActionButton fab_btn;
    private FloatingActionButton fab_logout;
    private FloatingActionButton fab_settings;

    private Boolean isFabOpen = false;

    private FirebaseAuth mAuth;
    private Context context = this;

    private Animation fab_open,fab_close,rotate_backward,rotate_forward;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_board);

        mAuth = FirebaseAuth.getInstance();

        listView = (ListView) this.findViewById(R.id.list_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);

        tv_loading_feed = (TextView) this.findViewById(R.id.loading_feed);

        fab_btn = (FloatingActionButton) findViewById(R.id.fab_btn);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);
        fab_logout = (FloatingActionButton) findViewById(R.id.fab_logout);
        fab_settings = (FloatingActionButton) findViewById(R.id.fab_settings);

        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);

        fab_btn.setOnClickListener(this);
        fab_logout.setOnClickListener(this);
        fab_search.setOnClickListener(this);
        fab_settings.setOnClickListener(this);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        feedItems = new ArrayList<FeedItem>();

        progressBar = (MaterialProgressBar) this.findViewById(R.id.progressbar_story_board);
        linearLayout = (LinearLayout) this.findViewById(R.id.box_story);
        linearLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);

        final SwipeDetector swipeDetector = new SwipeDetector();
        listView.setOnTouchListener(swipeDetector);

        final Animation animation = AnimationUtils.loadAnimation(this,
                R.anim.rotate_forward);

        final Animation animation_end = AnimationUtils.loadAnimation(this,
                R.anim.rotate_backward);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,final View view, int i, long l) {
                if(swipeDetector.swipeDetected()) {
                    if(swipeDetector.getAction() == SwipeDetector.Action.RL) {
                        view.startAnimation(animation);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                view.startAnimation(animation_end);
                            }
                        }, 1000);
                        Toast.makeText(StoryBoard.this,feedItems.get(i).getName(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        refreshContent();
    }

    private void refreshContent(){
        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Entry entry = cache.get(URL_FEED);
        if (entry != null) {
            // fetch the data from cache
            try {
                String data = new String(entry.data, "UTF-8");
                try {
                    parseJsonFeed(new JSONObject(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            // making fresh volley request and getting json
            JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET,
                    URL_FEED, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    VolleyLog.d(TAG, "Response: " + response.toString());
                    if (response != null) {
                        parseJsonFeed(response);
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
            jsonReq.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            // Adding request to volley request queue
            AppController.getInstance().addToRequestQueue(jsonReq);
        }
    }
    /**
     * Parsing json reponse and passing the data to feed view list adapter
     * */
    private void parseJsonFeed(JSONObject response) {
        try {
            JSONArray feedArray = response.getJSONArray("feed");

            for (int i = 0; i < feedArray.length(); i++) {
                JSONObject feedObj = (JSONObject) feedArray.get(i);

                FeedItem item = new FeedItem();
                item.setId(feedObj.getInt("id"));
                item.setName(feedObj.getString("name"));

                // Image might be null sometimes
                String image = feedObj.isNull("image") ? null : feedObj
                        .getString("image");
                item.setImge(image);
                item.setStatus(feedObj.getString("client"));
                item.setProfilePic(feedObj.getString("clientImage"));
                item.setTimeStamp(feedObj.getString("time"));

                // url might be null sometimes
                String feedUrl = feedObj.isNull("url") ? null : feedObj
                        .getString("url");
                item.setUrl(feedUrl);

                feedItems.add(item);
            }

            // notify data changes to list adapater
            listAdapter.notifyDataSetChanged();

            linearLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            tv_loading_feed.setVisibility(View.INVISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    public void animateFAB(){

        if(isFabOpen){

            fab_btn.startAnimation(rotate_backward);
            fab_search.startAnimation(fab_close);
            fab_logout.startAnimation(fab_close);
            fab_settings.startAnimation(fab_close);
            fab_logout.setVisibility(View.INVISIBLE);
            fab_search.setVisibility(View.INVISIBLE);
            fab_settings.setVisibility(View.INVISIBLE);
            fab_search.setClickable(false);
            fab_logout.setClickable(false);
            fab_settings.setClickable(false);
            isFabOpen = false;

        } else {
            fab_btn.startAnimation(rotate_forward);
            fab_search.startAnimation(fab_open);
            fab_logout.startAnimation(fab_open);
            fab_settings.startAnimation(fab_open);
            fab_logout.setVisibility(View.VISIBLE);
            fab_search.setVisibility(View.VISIBLE);
            fab_settings.setVisibility(View.VISIBLE);
            fab_search.setClickable(true);
            fab_logout.setClickable(true);
            fab_settings.setClickable(true);
            isFabOpen = true;

        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_btn:
                animateFAB();
                break;
            case R.id.fab_logout:
                mAuth.signOut();
                Intent intent = new Intent(StoryBoard.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                ((Activity) context).finish();
                ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                finish();
                break;
            case R.id.fab_search:
                break;
            case R.id.fab_settings:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Helper helper = new Helper(StoryBoard.this);
        helper.AlertExit();
    }
}
