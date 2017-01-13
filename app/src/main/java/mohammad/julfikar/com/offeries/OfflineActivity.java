package mohammad.julfikar.com.offeries;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mohammad.julfikar.com.offeries.Helper.Helper;
import mohammad.julfikar.com.offeries.Helper.SwipeDetector;
import mohammad.julfikar.com.offeries.OfflineDB.DBConstructors;
import mohammad.julfikar.com.offeries.OfflineDB.DBHandler;
import mohammad.julfikar.com.offeries.OfflineDB.OfflineBaseAdapter;

public class OfflineActivity extends AppCompatActivity {

    private ArrayList<DBConstructors>constructorsArrayList = new ArrayList<DBConstructors>();
    private OfflineBaseAdapter baseAdapter;
    private ListView listView;
    private FloatingActionButton fab_back;
    private Context context = this;
    private CoordinatorLayout coordinatorLayout;
    private ImageView iv_empty;
    private LinearLayout box_offline;
    private TextView tv_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);

        coordinatorLayout = (CoordinatorLayout) this.findViewById(R.id.activity_offline);
        fab_back = (FloatingActionButton) this.findViewById(R.id.fab_back);
        iv_empty = (ImageView) this.findViewById(R.id.iv_no_item);
        tv_empty = (TextView) this.findViewById(R.id.tv_empty);
        box_offline = (LinearLayout)this.findViewById(R.id.box_offline);

        Helper helper = new Helper(OfflineActivity.this);
        if(!helper.IsNetworkAvailable()){
            fab_back.setVisibility(View.INVISIBLE);
        }

        final DBHandler handler = new DBHandler(this);

        final List<DBConstructors>constructor = handler.getAllRecord();

        for(DBConstructors con:constructor){
            constructorsArrayList.add(con);
        }
        baseAdapter = new OfflineBaseAdapter(this,R.layout.offline_item,constructorsArrayList);
        listView = (ListView) this.findViewById(R.id.offline_list_view);
        listView.setAdapter(baseAdapter);

        if(constructor.size() == 0){
            box_offline.setVisibility(View.INVISIBLE);
            iv_empty.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.VISIBLE);
        }

        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, StoryBoard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                ((Activity) context).finish();
                ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
            }
        });

        final SwipeDetector swipeDetector = new SwipeDetector();
        listView.setOnTouchListener(swipeDetector);

        final Animation animation_delete = AnimationUtils.loadAnimation(this,
                R.anim.rotate_forward);

        final Animation animation_end_delete = AnimationUtils.loadAnimation(this,
                R.anim.rotate_backward);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,final View view, int i, long l) {
                if(swipeDetector.swipeDetected()) {
                    if(swipeDetector.getAction() == SwipeDetector.Action.RL){
                        final int id = constructorsArrayList.get(i).getId();
                        final String name = constructorsArrayList.get(i).getName();
                        final String desc = constructorsArrayList.get(i).getDesc();
                        final String link = constructorsArrayList.get(i).getLink();
                        final byte[] image = constructorsArrayList.get(i).getImage();
                        try{
                            boolean isDeleted = handler.deleteRecord(new DBConstructors(id,name,desc,link,image));
                            if(isDeleted == true){
                                view.startAnimation(animation_delete);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        view.startAnimation(animation_end_delete);
                                    }
                                }, 500);

                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "Post is deleted from your drawer", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }catch (Exception e){

                        }
                    }else{
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Swipe Right-Left to delete item from drawer", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Helper helper = new Helper(OfflineActivity.this);
        helper.AlertExit();
    }
}
