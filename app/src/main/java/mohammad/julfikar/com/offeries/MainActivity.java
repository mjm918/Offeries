package mohammad.julfikar.com.offeries;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;

import mohammad.julfikar.com.offeries.Helper.Helper;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView tv_slow_connection;
    private TextView tv_auth;
    private Context context = this;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseCrash.report(new Exception("Non-fatal error. App Started"));

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Helper helper = new Helper(MainActivity.this);

        tv_auth = (TextView) this.findViewById(R.id.auth);
        tv_slow_connection = (TextView) this.findViewById(R.id.slow_connection);

        if (!helper.IsNetworkAvailable()){
            tv_slow_connection.setVisibility(View.VISIBLE);
            tv_auth.setVisibility(View.INVISIBLE);
        }

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    String email = user.getEmail();
                    String uid = user.getUid();

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, uid);
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, email);
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
                    mFirebaseAnalytics.setMinimumSessionDuration(20000);
                    mFirebaseAnalytics.setSessionTimeoutDuration(500);

                    Intent intent = new Intent(MainActivity.this, StoryBoard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                    ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                    finish();
                }else{
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                    ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                    finish();
                    System.out.println("onAuthStateChanged:signed_out");
                }
            }
        };
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public Context getActivity() {
        return MainActivity.this;
    }
}
