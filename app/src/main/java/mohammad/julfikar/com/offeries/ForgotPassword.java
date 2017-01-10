package mohammad.julfikar.com.offeries;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class ForgotPassword extends AppCompatActivity {

    private Context context = this;
    private ImageView iv_forgot;
    private EditText forgot_email;
    private CoordinatorLayout coordinatorLayout;
    private LinearLayout linearLayout;
    private MaterialProgressBar progressBar;
    private Button btn_forgot;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        coordinatorLayout = (CoordinatorLayout) this.findViewById(R.id.activity_forgot_password);
        iv_forgot = (ImageView) this.findViewById(R.id.iv_forgot);
        forgot_email = (EditText) this.findViewById(R.id.forgot_email);
        linearLayout = (LinearLayout) this.findViewById(R.id.box_forgot);
        progressBar = (MaterialProgressBar) this.findViewById(R.id.forgot_progressbar);
        btn_forgot = (Button) this.findViewById(R.id.btn_forgot);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        iv_forgot.startAnimation(animation);

        btn_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.INVISIBLE);

                final String et_email = forgot_email.getText().toString();

                if(TextUtils.isEmpty(et_email)){
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Do Not Leave Any Section Blank", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    progressBar.setVisibility(View.INVISIBLE);
                    linearLayout.setVisibility(View.VISIBLE);
                    return;
                }else{
                    mAuth.sendPasswordResetEmail(et_email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Snackbar snackbar = Snackbar
                                                .make(coordinatorLayout, "Password reset instruction has been sent to your email", Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent = new Intent(context, Login.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                context.startActivity(intent);
                                                ((Activity) context).finish();
                                                ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                                                finish();
                                            }
                                        }, 2000);
                                    } else {
                                        Snackbar snackbar_fail = Snackbar
                                                .make(coordinatorLayout, "Email is not recognized", Snackbar.LENGTH_LONG);
                                        snackbar_fail.show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                        linearLayout.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ForgotPassword.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        ((Activity) context).finish();
        ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
        finish();
    }
}
