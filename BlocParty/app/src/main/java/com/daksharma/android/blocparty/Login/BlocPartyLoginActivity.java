package com.daksharma.android.blocparty.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.daksharma.android.blocparty.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

/**
 * Created by Daksh on 2/16/16.
 */
public class BlocPartyLoginActivity extends AppCompatActivity implements OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = BlocPartyLoginActivity.class.getSimpleName().toUpperCase();

    private GoogleApiClient     mGoogleApiClient;
    private GoogleSignInOptions mGoogleSignInOption;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout_activity);

        // Google Sign In Options
        mGoogleSignInOption = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Google Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOption)
                .build();

        // Sign in Button
        SignInButton googleSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);
        googleSignInButton.setScopes(mGoogleSignInOption.getScopeArray());
        findViewById(R.id.sign_in_button).setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();

        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    Log.e(TAG, "Google SignIn Result: " + googleSignInResult.isSuccess());
                }
            });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.e(TAG, "Google Account SignIn: " + result.isSuccess());
            if (result.isSuccess()) {
                GoogleSignInAccount gAccount = result.getSignInAccount();
                Log.e(TAG, "Name: " + gAccount.getDisplayName());
                Log.e(TAG, "Account ID: " + gAccount.getId());
                Log.e(TAG, "Email: " + gAccount.getEmail());
                Log.e(TAG, "ID Token: " + gAccount.getIdToken());
                Log.e(TAG, "Server Auth Code: " + gAccount.getServerAuthCode());
            }
        }
    }

    @Override
    public void onConnectionFailed (ConnectionResult connectionResult) {
        Log.e(TAG, "Connection : " + connectionResult.getErrorMessage());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
