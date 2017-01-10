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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import mohammad.julfikar.com.offeries.Helper.Helper;


public class Login extends AppCompatActivity {

    private EditText et_email;
    private EditText et_password;
    private Button btn_login;
    private Button btn_new_register;
    private Button btn_forgot_password;
    private ImageView iv_login;
    private LinearLayout linearLayout;
    private MaterialProgressBar progressBar;
    private CoordinatorLayout coordinatorLayout;

    private FirebaseAuth mAuth;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        linearLayout = (LinearLayout) this.findViewById(R.id.box);
        coordinatorLayout = (CoordinatorLayout) this.findViewById(R.id.activity_login);
        progressBar = (MaterialProgressBar) this.findViewById(R.id.progressbar);

        et_email = (EditText) this.findViewById(R.id.input_email);
        et_password = (EditText) this.findViewById(R.id.input_password);
        iv_login = (ImageView) this.findViewById(R.id.iv_login);
        btn_login = (Button) this.findViewById(R.id.btn_login);
        btn_forgot_password = (Button) this.findViewById(R.id.btn_reset);
        btn_new_register = (Button) this.findViewById(R.id.btn_new_signup);

        final Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.clockwise);
        iv_login.startAnimation(animation);

        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.INVISIBLE);

                final String email = et_email.getText().toString();
                final String password = et_password.getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Do Not Leave Any Section Blank", Snackbar.LENGTH_LONG);
                    snackbar.show();

                    progressBar.setVisibility(View.INVISIBLE);
                    linearLayout.setVisibility(View.VISIBLE);

                    return;
                }else{
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    //if successful do something
                                    if(task.isSuccessful()){
                                        Snackbar snackbar_su = Snackbar
                                                .make(coordinatorLayout, "Login Successful", Snackbar.LENGTH_LONG);
                                        snackbar_su.show();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent = new Intent(context, StoryBoard.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                context.startActivity(intent);
                                                ((Activity) context).finish();
                                                ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                                                finish();
                                            }
                                        }, 2000);
                                    }

                                    if (!task.isSuccessful()) {
                                        Snackbar snackbar = Snackbar
                                                .make(coordinatorLayout, "Login Failed. Please Try Again", Snackbar.LENGTH_LONG);
                                        snackbar.show();
                                        progressBar.setVisibility(View.INVISIBLE);
                                        linearLayout.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                }
            }
        });
        btn_new_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Registration.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                ((Activity) context).finish();
                ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                finish();
            }
        });
        btn_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, ForgotPassword.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                ((Activity) context).finish();
                ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Helper helper = new Helper(Login.this);
        helper.AlertExit();
    }
}
