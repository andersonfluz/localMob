package com.mobmundo.localmob.activity;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.mobmundo.localmob.R;
import com.mobmundo.localmob.DAO.PersonDAO;
import com.mobmundo.localmob.entity.Person;
import com.mobmundo.localmob.util.Conexao;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.twitter.Twitter;

public class FirstActivity extends Activity implements OnClickListener, GoogleApiClient.ConnectionCallbacks, OnConnectionFailedListener {

	ProgressDialog progressDialog;
	Boolean googlePlus = false;
	ImageView imgFacebook;
    GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;
    private static final int RC_SIGN_IN = 0;
    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		imgFacebook = (ImageView) findViewById(R.id.imvConectFacebook);
		ImageView imgGooglePlus = (ImageView) findViewById(R.id.imvConectGoogle);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
        if (supportsGooglePlayServices()) {
			// Set a listener to connect the user when the G+ button is clicked.
			imgGooglePlus.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if (new Conexao(getApplicationContext()).verificaConexao()) {
						googlePlus = true;
                        signInWithGplus();
					} else {
						Toast.makeText(FirstActivity.this,
								"Verifique sua conexão", Toast.LENGTH_LONG)
								.show();
					}
				}
			});
		} else {
			// Don't offer G+ sign in if the app's version is too low to support
			// Google Play
			// Services.
			imgGooglePlus.setVisibility(View.GONE);
			// return;
		}
		
		// Acesso por email - Activity de cadastro ou acesso
		ImageView imgEmail = (ImageView) findViewById(R.id.imvConectEmail);
		imgEmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				abrirTela(LoginActivity.class);
			}
        });
	}

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Sign-in into google
     * */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            resolveSignInError();
        }
    }

    /**
     * Method to resolve any signin errors
     * */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

	private boolean supportsGooglePlayServices() {
		return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!googlePlus) {
			super.onActivityResult(requestCode, resultCode, data);
			ParseFacebookUtils.onActivityResult(requestCode, resultCode,
					data);
		}else{
            if (requestCode == RC_SIGN_IN) {
                if (resultCode != RESULT_OK) {
                    //mSignInClicked = false;
                }

                mIntentInProgress = false;

                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
            }
        }
	}

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        //updateUI(false);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (googlePlus) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }


    public void loginFacebookUser(View v) {
		if (!new Conexao(getApplicationContext()).verificaConexao()) {
			Toast.makeText(FirstActivity.this, "Verifique sua conexao",
					Toast.LENGTH_LONG).show();
		} else {
			FirstActivity.this.progressDialog = ProgressDialog.show(
					FirstActivity.this, "", "Logging in...", true);
			List<String> permissions = Arrays.asList("basic_info",
					"user_about_me", "user_relationships", "user_birthday",
					"user_location");
			ParseFacebookUtils.logInWithReadPermissionsInBackground(permissions, this, new LogInCallback() {
				@Override
				public void done(ParseUser user, ParseException err) {
					FirstActivity.this.progressDialog.dismiss();
					if (user == null) {
					}
					else if (user.isNew()) {
						Session session = ParseFacebookUtils.();
						if (session != null && session.isOpened()) {
							makeMeRequestFacebook();
						}
						abrirTela(MasterAppActivity.class);
					} else {
						abrirTela(MasterAppActivity.class);
					}
				}
			});
		}
	}

	public void loginTwitterUser(View v) {
		if (!new Conexao(getApplicationContext()).verificaConexao()) {
			Toast.makeText(FirstActivity.this, "Verifique sua conexão",
					Toast.LENGTH_LONG).show();
		} else {
			ParseTwitterUtils.logIn(this, new LogInCallback() {
				@Override
				public void done(ParseUser user, ParseException err) {
					if (user == null) {
						// Log.d("MyApp",
						// "Uh oh. The user cancelled the Twitter login.");
					} else if (user.isNew()) {
						if (ParseTwitterUtils.getTwitter() != null) {
							Person person = makeMeRequestTwitter();
							person.setUser(user);
							PersonDAO pDAO = new PersonDAO();
							pDAO.savePerson(person);
						}
						abrirTela(MasterAppActivity.class);
					} else {
						// Log.d("MyApp", "User logged in through Twitter!");
						abrirTela(MasterAppActivity.class);
					}
				}
			});
		}
	}

	public void abrirTela(Class<?> activity) {
		Intent myIntent = new Intent(this, activity);
		startActivity(myIntent);
	}

	private void makeMeRequestFacebook() {
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserCallback() {

					@Override
					public void onCompleted(GraphUser user, Response response) {
						if (user != null) {
							// Create a JSON object to hold the profile info
							JSONObject userProfile = new JSONObject();
							try {
								// Populate the JSON object
								userProfile.put("name", user.getName());

								// Save the user profile info in a user property
								ParseUser currentUser = ParseUser
										.getCurrentUser();
								currentUser.put("profile", userProfile);
								currentUser.saveInBackground();

								// Save Person of User
								savePersonUser();
							} catch (JSONException e) {
								Toast.makeText(FirstActivity.this,
										"Erro: " + e.getMessage(),
										Toast.LENGTH_LONG).show();
							}
						}
					}
				});
		request.executeAsync();
	}

	private void savePersonUser() {
		ParseUser currentUser = ParseUser.getCurrentUser();
		Person person = new Person("", "");
		if (currentUser.has("profile")) {
			JSONObject userProfile = currentUser.getJSONObject("profile");
			try {
				if (userProfile.has("name")) {
					person.setName(userProfile.getString("name"));
				}
				PersonDAO pDAO = new PersonDAO();
				pDAO.savePerson(person);
			} catch (JSONException e) {
				Toast.makeText(FirstActivity.this, "Erro: " + e.getMessage(),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	private Person makeMeRequestTwitter() {
		Twitter twitter = ParseTwitterUtils.getTwitter();
		twitter.getScreenName();
		Person person = new Person(twitter.getScreenName(), "");
		return person;
	}

    @Override
    public void onConnected(Bundle bundle) {
        //mSignInClicked = false;
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        // Get user's information
        //getProfileInformation();

        // Update the UI after signin
        //updateUI(true);
    }


    private interface ProfileQuery {
		String[] PROJECTION = { ContactsContract.CommonDataKinds.Email.ADDRESS,
				ContactsContract.CommonDataKinds.Email.IS_PRIMARY, };

		// int ADDRESS = 0;
		// int IS_PRIMARY = 1;
	}


	public void loginCadastroParse() {

	}

	public ParseFile convertImageViewToFile(ImageView imgView) {
		imgView.buildDrawingCache();
		Bitmap bm = imgView.getDrawingCache();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return new ParseFile(stream.toByteArray());
	}
}
