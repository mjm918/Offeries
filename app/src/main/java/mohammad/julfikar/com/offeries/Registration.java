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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;


public class Registration extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    private Context context = this;
    private EditText et_email;
    private EditText et_password;
    private EditText et_re_password;
    private Button btn_register;
    private Button btn_re_login;
    private SignInButton btn_google;
    private FirebaseAuth mAuth;
    private CoordinatorLayout coordinatorLayout;
    private MaterialProgressBar progressBar;
    private LinearLayout linearLayout;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        coordinatorLayout = (CoordinatorLayout) this.findViewById(R.id.activity_registration);
        linearLayout = (LinearLayout) this.findViewById(R.id.box_reg);
        et_email = (EditText) this.findViewById(R.id.reg_email);
        et_password = (EditText) this.findViewById(R.id.reg_password);
        et_re_password = (EditText) this.findViewById(R.id.reg_re_password);
        btn_register = (Button) this.findViewById(R.id.btn_reg);
        btn_re_login = (Button) this.findViewById(R.id.btn_re_login);
        btn_google = (SignInButton) this.findViewById(R.id.btn_google);
        btn_google.setSize(SignInButton.SIZE_STANDARD);
        progressBar = (MaterialProgressBar) this.findViewById(R.id.progressbar_reg);

        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        btn_re_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Registration.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                ((Activity) context).finish();
                ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
                finish();
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.INVISIBLE);

                final String email = et_email.getText().toString();
                final String password = et_password.getText().toString();
                final String rePassword = et_re_password.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(rePassword)){
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Do Not Leave Any Section Blank", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    progressBar.setVisibility(View.INVISIBLE);
                    linearLayout.setVisibility(View.VISIBLE);
                    return;
                }else{

                    if (!password.equals(rePassword)){
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Password Doesn't Match"+" "+password+" "+ rePassword, Snackbar.LENGTH_LONG);
                        snackbar.show();
                        progressBar.setVisibility(View.INVISIBLE);
                        linearLayout.setVisibility(View.VISIBLE);
                        return;
                    }
                    if(password.length() < 6){
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Password Must Be 6 Digits", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        progressBar.setVisibility(View.INVISIBLE);
                        linearLayout.setVisibility(View.VISIBLE);
                        return;
                    }
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Snackbar snackbar_su = Snackbar
                                            .make(coordinatorLayout, "Registration Successful", Snackbar.LENGTH_LONG);
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
                                    if (!task.isSuccessful()) {
                                        Snackbar snackbar = Snackbar
                                                .make(coordinatorLayout, "Registration Failed. Please Try Again", Snackbar.LENGTH_LONG);
                                        snackbar.show();
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
        Intent intent = new Intent(Registration.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        ((Activity) context).finish();
        ((Activity) context).overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
        finish();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Google Sign In", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            Snackbar snackbar_su = Snackbar
                    .make(coordinatorLayout, "Registration Successful "+acct.getDisplayName(), Snackbar.LENGTH_LONG);
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
        } else {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Registration Failed. Please Try Again", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
