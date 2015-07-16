package com.mobmundo.localmob.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mobmundo.localmob.R;
import com.mobmundo.localmob.util.BlurBuilder;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends Activity {
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		final Activity activity = this;
		final View content = activity.findViewById(R.id.linearLogin)
				.getRootView();
		Bitmap praia = BitmapFactory.decodeResource(getResources(),
				R.drawable.praiabkg);
		if (content.getWidth() > 0) {
			Bitmap image = BlurBuilder.blur(content, praia);
			LinearLayout ll = (LinearLayout) findViewById(R.id.linearLogin);
			ll.setBackground(new BitmapDrawable(activity.getResources(), image));
			// window.setBackgroundDrawable(new
			// BitmapDrawable(activity.getResources(), image));
		} else {
			content.getViewTreeObserver().addOnGlobalLayoutListener(
					new OnGlobalLayoutListener() {
						@Override
						public void onGlobalLayout() {
							Bitmap image = BlurBuilder.blur(content,
									BitmapFactory
											.decodeResource(getResources(),
													R.drawable.praiabkg));
							LinearLayout ll = (LinearLayout) findViewById(R.id.linearLogin);
							ll.setBackground(new BitmapDrawable(activity
									.getResources(), image));
						}
					});
		}

		final EditText mUsername = (EditText) findViewById(R.id.username);
		final EditText mPassword = (EditText) findViewById(R.id.password);

		Button signInButton = (Button) findViewById(R.id.sign_in_button);
		signInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				LoginActivity.this.progressDialog = ProgressDialog.show(
						LoginActivity.this, "", "Logging in...", true);
				ParseUser.logInInBackground(mUsername.toString(),
						mPassword.toString(), new LogInCallback() {

							@Override
							public void done(ParseUser user, ParseException e) {
								// TODO Auto-generated method stub
								if (e != null) {
									// Show the error message
									progressDialog.dismiss();
									Toast.makeText(LoginActivity.this,
											e.getMessage(), Toast.LENGTH_LONG)
											.show();
								} else {
									progressDialog.dismiss();
									Toast.makeText(LoginActivity.this,
											"Conectado", Toast.LENGTH_LONG)
											.show();
									// Start an intent for the dispatch activity
									abrirTela(MasterAppActivity.class);
								}
							}
						});
			}
		});

		Button btnRegister = (Button) findViewById(R.id.button_register);
		btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				abrirTela(RegisterActivity.class);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void abrirTela(Class<?> activity) {
		Intent myIntent = new Intent(this, activity);
		startActivity(myIntent);
	}
}
