package mohammad.julfikar.com.offeries;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import mohammad.julfikar.com.offeries.Helper.Helper;

public class SplashActivity extends AppCompatActivity {

    private ImageView splash;
    private int SPLASH_TIME_OUT = 3000;
    private Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splash = (ImageView) this.findViewById(R.id.logo);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        splash.startAnimation(animation);

        Helper helper = new Helper(SplashActivity.this);
        if(!helper.IsNetworkAvailable()){
            helper.Alert();
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                    ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
