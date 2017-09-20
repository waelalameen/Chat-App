package com.alameen.wael.hp.chatapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private AutoCompleteTextView email;
    private TextInputEditText password;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private GoogleApiClient apiClient;
    private final static int RC_SIGN_IN = 100;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private static final String webClientId = "164610072002-mdfomj3pbaig65mjgkp0ic3trsn6lflt.apps.googleusercontent.com";
    private static final String HOST_URL = "http://labsne.com/Chat/tokens.php";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "Jannal.ttf");

        email = (AutoCompleteTextView) findViewById(R.id.email);
        password = (TextInputEditText) findViewById(R.id.password);
        email.setTypeface(typeface);
        password.setTypeface(typeface);
        //password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ((TextView) findViewById(R.id.title)).setTypeface(typeface);
        ((TextView) findViewById(R.id.or)).setTypeface(typeface);
        ((TextView) findViewById(R.id.or)).setText(Html.fromHtml("Don't you have an account ? <font color='#0099ff'>Sign Up here</font>"));

        Button normal = (Button) findViewById(R.id.normal_sign_in);
        normal.setTypeface(typeface);
        normal.setOnClickListener(this);

        SignInButton googleSignInButton = (SignInButton) findViewById(R.id.google_sign_in);
        googleSignInButton.setOnClickListener(this);

        GoogleSignInOptions googleSignIn = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(webClientId).requestEmail().build();
        apiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignIn).build();
        firebaseAuth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                setUpFirebaseUser(user, "Google");
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.normal_sign_in:
                signInNormal(view);
                break;
            case R.id.google_sign_in:
                signInWithGoogle();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        apiClient.connect();
        firebaseAuth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authListener != null) {
            firebaseAuth.removeAuthStateListener(authListener);
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignIn(result);
        }
    }

    private void handleGoogleSignIn(GoogleSignInResult result) {
        Log.d("GoogleSignInResult", " : "+result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount googleAccount = result.getSignInAccount();

            if (googleAccount != null) {
                Log.d("user", googleAccount.getDisplayName());
                Log.d("photo_url", " : " + googleAccount.getPhotoUrl());
                firebaseAuthWithGoogle(googleAccount);
            }
        } else {
            Log.d("failed", result.getStatus().getStatusMessage());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount googleAccount) {
        Log.d("user_id", googleAccount.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(googleAccount.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("google successful", " : "+task.isSuccessful());
                } else {
                    Log.d("google error", " : "+task.getException());
                }
            }
        });
    }

    private void signInNormal(View view) {
        final String mEmail = email.getText().toString();
        final String mPass = password.getText().toString();

        if (TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPass) || (TextUtils.isEmpty(mEmail) && TextUtils.isEmpty(mPass))) {
            Snackbar.make(view, "Enter Email and Password Please", Snackbar.LENGTH_SHORT).show();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(mEmail, mPass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    proceedNormalSignIn(mEmail, mPass);
                }
            });
        }
    }

    private void proceedNormalSignIn(String mEmail, String mPass) {
        firebaseAuth.signInWithEmailAndPassword(mEmail, mPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    setUpFirebaseUser(user, "Normal");
                }
            }
        });
    }

    private void setUpFirebaseUser(FirebaseUser user, String FLAG) {
        String userPhoto = null;

        if (user != null) {
            if (FLAG.equals("Google")) {
                try {
                    userPhoto = user.getPhotoUrl().toString();
                    Log.d("userPhoto", userPhoto);
                } catch (Exception e) {e.fillInStackTrace();}
            } else {
                userPhoto = getResources().getResourceName(R.drawable.user);
                Log.d("userPhoto", userPhoto);
            }

            Map<String, Object> map = new HashMap<>();
            map.put(user.getDisplayName(), "");

            SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("userName", user.getDisplayName());
            editor.putString("userImage", user.getPhotoUrl().toString());
            editor.putString("email", user.getEmail());
            editor.apply();

            root.updateChildren(map);
            root = FirebaseDatabase.getInstance().getReference().child(user.getDisplayName());

            SharedPreferences sharedPreferences = getSharedPreferences("token", MODE_PRIVATE);
            String myToken = sharedPreferences.getString("token", "");

            SharedPreferences sign = getSharedPreferences("sign", MODE_PRIVATE);
            boolean isSingedIn = sign.getBoolean("isSignedIn", false);

            Map<String, Object> map2 = new HashMap<>();
            map2.put("userName", user.getDisplayName());
            map2.put("userPhoto", userPhoto);
            map2.put("token", myToken);

            try {
                insertToken(myToken);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("JSONException", e.getMessage());
            }

            if (!isSingedIn) {
                root.updateChildren(map2);
                sign = getSharedPreferences("sign", MODE_PRIVATE);
                SharedPreferences.Editor signEditor = sign.edit();
                signEditor.putBoolean("isSignedIn", true);
                signEditor.apply();
            } else {
                root.updateChildren(map2);
            }

            startActivity(new Intent(Login.this, MainActivity.class));
        } else {
            Log.d("user_id", "user signed out");
            FirebaseAuth.getInstance().signOut();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Error message : "+connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
    }

    private void insertToken(String token) throws JSONException {
        new Async().execute(token);
    }

    class Async extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String token = params[0];

            try {
                URL url = new URL(HOST_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                String data = URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStreamWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                inputStream.close();
                httpURLConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
