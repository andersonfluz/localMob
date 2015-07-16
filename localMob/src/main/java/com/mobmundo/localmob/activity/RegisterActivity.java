package com.mobmundo.localmob.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Build.VERSION;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobmundo.localmob.DAO.PersonDAO;
import com.mobmundo.localmob.entity.Person;
import com.mobmundo.localmob.util.Alertas;
import com.mobmundo.localmob.util.Conexao;
import com.mobmundo.localmob.util.EmailValidator;
import com.mobmundo.localmob.R;
import com.mobmundo.localmob.util.BlurBuilder;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends Activity implements
		LoaderCallbacks<Cursor> {
	
	private UserRegisterTask mAuthTask = null;
	private EditText mName;
	private EditText mUsername;
	private AutoCompleteTextView mEmailView;
	private EditText mPasswordView;
	private EditText mConfirmPasswordView;
	private EditText mPhone;
	private ImageView imvIconCam;
	private EditText etSelFoto;
	private ImageView imvFoto;
	private Button mEmailSignInButton;
	private ProgressDialog dlg;
	ParseUser user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		final Activity activity = this;
		final View content = activity.findViewById(R.id.linearRegister)
				.getRootView();
		Bitmap praia = BitmapFactory.decodeResource(getResources(),
				R.drawable.praiabkg);
		if (content.getWidth() > 0) {
			Bitmap image = BlurBuilder.blur(content, praia);
			LinearLayout ll = (LinearLayout) findViewById(R.id.linearRegister);
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
							LinearLayout ll = (LinearLayout) findViewById(R.id.linearRegister);
							ll.setBackground(new BitmapDrawable(activity
									.getResources(), image));
						}
					});
		}
		mName = (EditText) findViewById(R.id.cadastro_nome);
		mUsername = (EditText) findViewById(R.id.cadastro_usuario);
		mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
		mPasswordView = (EditText) findViewById(R.id.password);
		mConfirmPasswordView = (EditText) findViewById(R.id.passwordConfirm);
		mPhone = (EditText) findViewById(R.id.phone);
		imvIconCam = (ImageView) findViewById(R.id.imvIcoFoto);
		etSelFoto = (EditText) findViewById(R.id.etImagemSelec);
		imvFoto = (ImageView) findViewById(R.id.imvFoto);
		dlg = new ProgressDialog(RegisterActivity.this);

		populateAutoComplete();

		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
		mEmailSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

		imvIconCam.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectImage();
			}
		});

		etSelFoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectImage();
			}
		});
	}

	private void selectImage() {
		final CharSequence[] options = { "Tirar Foto", "Suas Imagens",
				"Cancelar" };
		AlertDialog.Builder builder = new AlertDialog.Builder(
				RegisterActivity.this);
		builder.setTitle("Selecione uma Foto");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (options[item].equals("Tirar Foto")) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(android.os.Environment
							.getExternalStorageDirectory(), "temp.jpg");
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
					startActivityForResult(intent, 1);
				} else if (options[item].equals("Suas Imagens")) {
					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 2);

				} else if (options[item].equals("Cancelar")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				File f = new File(Environment.getExternalStorageDirectory()
						.toString());
				for (File temp : f.listFiles()) {
					if (temp.getName().equals("temp.jpg")) {
						f = temp;
						break;
					}
				}
				try {
					Bitmap bitmap;
					BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

					bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
							bitmapOptions);

					imvFoto.setImageBitmap(bitmap);
					imvFoto.setVisibility(View.VISIBLE);

					String path = android.os.Environment
							.getExternalStorageDirectory()
							+ File.separator
							+ "Phoenix" + File.separator + "default";
					f.delete();
					OutputStream outFile = null;
					File file = new File(path, String.valueOf(System
							.currentTimeMillis()) + ".jpg");
					try {
						outFile = new FileOutputStream(file);
						bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
						outFile.flush();
						outFile.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (requestCode == 2) {

				Uri selectedImage = data.getData();
				String[] filePath = { MediaStore.Images.Media.DATA };
				Cursor c = getContentResolver().query(selectedImage, filePath,
						null, null, null);
				c.moveToFirst();
				int columnIndex = c.getColumnIndex(filePath[0]);
				String picturePath = c.getString(columnIndex);
				c.close();
				Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
				Log.w("path of image from gallery......******************.........",
						picturePath + "");
				imvFoto.setImageBitmap(thumbnail);
				imvFoto.setVisibility(View.VISIBLE);

			}
		}
	}

	private ParseFile convertImageViewToFile(ImageView imgView) {
		imgView.buildDrawingCache();
		Bitmap bm = imgView.getDrawingCache();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return new ParseFile(stream.toByteArray());
	}

	private void populateAutoComplete() {
		if (VERSION.SDK_INT >= 14) {
			// Use ContactsContract.Profile (API 14+)
			getLoaderManager().initLoader(0, null, this);
		} else if (VERSION.SDK_INT >= 8) {
			// Use AccountManager (API 8+)
			new SetupEmailAutoCompleteTask().execute(null, null);
		}
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (!new Conexao(getApplicationContext()).verificaConexao()) {
			AlertDialog alerta = new Alertas().gerarAlerta(this,
					this.getApplicationContext(), "Acesso Internet",
					"Verifique a conexao com a internet");
			alerta.show();
			return;
		}
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		String name = mName.getText().toString();
		String username = mUsername.getText().toString();
		String email = mEmailView.getText().toString();
		String password = mPasswordView.getText().toString();
		String confirmPassword = mConfirmPasswordView.getText().toString();
		String phone = mPhone.getText().toString();
		ParseFile photo = this.convertImageViewToFile(imvFoto);

		boolean cancel = false;
		View focusView = null;

		// Check for a exist name.
		if (TextUtils.isEmpty(name.trim())) {
			mName.setError(getString(R.string.error_field_required));
			focusView = mName;
			cancel = true;
		}

		// Check for a exist username.
		if (TextUtils.isEmpty(username.trim())) {
			mUsername.setError(getString(R.string.error_field_required));
			focusView = mUsername;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(email.trim())) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!isEmailValid(email)) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		// Check for a valid password, if the user entered one.
		if ((TextUtils.isEmpty(password.trim()) || !isPasswordValid(password))) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		} else {
			if ((TextUtils.isEmpty(confirmPassword.trim()) || !isPasswordValid(confirmPassword
					.trim()))) {
				mConfirmPasswordView
						.setError(getString(R.string.error_invalid_password));
				focusView = mConfirmPasswordView;
				cancel = true;
			} else {
				if (!password.equals(confirmPassword.trim())) {
					mConfirmPasswordView
							.setError(getString(R.string.error_no_math_password));
					mPasswordView
							.setError(getString(R.string.error_no_math_password));
					focusView = mPasswordView;
					cancel = true;
				}
			}
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress();
			mAuthTask = new UserRegisterTask(username, name, email, password,
					phone, photo);
			mAuthTask.execute((Void) null);
		}

	}

	private boolean isEmailValid(String email) {
		// TODO: Replace this with your own logic
		return new EmailValidator().validate(email);
	}

	private boolean isPasswordValid(String password) {
		return password.length() > 4;
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	public void showProgress() {
		dlg.setTitle("Por favor aguarde.");
		dlg.setMessage("Estamos registrando seu usuario.");
		dlg.show();
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	public void dispenseProgress() {
		dlg.dismiss();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(this,
				// Retrieve data rows for the device user's 'profile' contact.
				Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
						ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
				ProfileQuery.PROJECTION,

				// Select only email addresses.
				ContactsContract.Contacts.Data.MIMETYPE + " = ?",
				new String[] { ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE },

				// Show primary email addresses first. Note that there won't be
				// a primary email address if the user hasn't specified one.
				ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		List<String> emails = new ArrayList<String>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			emails.add(cursor.getString(ProfileQuery.ADDRESS));
			cursor.moveToNext();
		}
		addEmailsToAutoComplete(emails);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {

	}

	private interface ProfileQuery {
		String[] PROJECTION = { ContactsContract.CommonDataKinds.Email.ADDRESS,
				ContactsContract.CommonDataKinds.Email.IS_PRIMARY, };

		int ADDRESS = 0;
		// int IS_PRIMARY = 1;
	}

	/**
	 * Use an AsyncTask to fetch the user's email addresses on a background
	 * thread, and update the email text field with results on the main UI
	 * thread.
	 */
	class SetupEmailAutoCompleteTask extends
			AsyncTask<Void, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Void... voids) {
			ArrayList<String> emailAddressCollection = new ArrayList<String>();

			// Get all emails from the user's contacts and copy them to a list.
			ContentResolver cr = getContentResolver();
			Cursor emailCur = cr.query(
					ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
					null, null, null);
			while (emailCur.moveToNext()) {
				String email = emailCur
						.getString(emailCur
								.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
				emailAddressCollection.add(email);
			}
			emailCur.close();

			return emailAddressCollection;
		}

		@Override
		protected void onPostExecute(List<String> emailAddressCollection) {
			addEmailsToAutoComplete(emailAddressCollection);
		}
	}

	private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
		// Create adapter to tell the AutoCompleteTextView what to show in its
		// dropdown list.
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				RegisterActivity.this,
				android.R.layout.simple_dropdown_item_1line,
				emailAddressCollection);

		mEmailView.setAdapter(adapter);
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {
		private final String mUserName;
		private final String mName;
		private final String mEmail;
		private final String mPassword;
		private final String mPhone;
		private final ParseFile mPhoto;

		UserRegisterTask(String userName, String name, String email,
				String password, String phone, ParseFile photo) {
			mUserName = userName;
			mName = name;
			mEmail = email;
			mPassword = password;
			mPhone = phone;
			mPhoto = photo;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			user = new ParseUser();
			user.setUsername(mUserName);
			user.setPassword(mPassword);
			user.setEmail(mEmail);
			// Call the Parse signup method
			user.signUpInBackground(new SignUpCallback() {
				@Override
				public void done(ParseException e) {
					if (e != null) {
						// Show the error message
						dispenseProgress();
						Toast.makeText(RegisterActivity.this, e.getMessage(),
								Toast.LENGTH_LONG).show();
					} else {
						dispenseProgress();
						Toast.makeText(RegisterActivity.this,
								"Usuario Registrado.", Toast.LENGTH_LONG)
								.show();
						Person person = new Person(mName, mPhoto, mPhone, user);

						PersonDAO pDAO = new PersonDAO();
						pDAO.savePerson(person);
						Toast.makeText(RegisterActivity.this,
								"Pessoa Registrada.", Toast.LENGTH_LONG).show();
						ParseUser.logInInBackground(mUsername.toString(),
								mPassword.toString(), new LogInCallback() {

									@Override
									public void done(ParseUser user, ParseException e) {
										// TODO Auto-generated method stub
										if (e != null) {
											// Show the error message
											Toast.makeText(RegisterActivity.this,
													e.getMessage(), Toast.LENGTH_LONG)
													.show();
										} else {
											Toast.makeText(RegisterActivity.this,
													"Conectado", Toast.LENGTH_LONG)
													.show();
											// Start an intent for the dispatch activity
											abrirTela(MasterAppActivity.class);
										}
									}
								});
					}
				}
			});
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if (success) {
				// Intent intent = new Intent(SignUpActivity.this,
				// DispatchActivity.class);
				// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
				// Intent.FLAG_ACTIVITY_NEW_TASK);
				// startActivity(intent);
			} else {
				mUsername.setError(getString(R.string.error_user_exist));
				mUsername.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			// mAuthTask = null;
			showProgress();
		}
	}
	
	public void abrirTela(Class<?> activity) {
		Intent myIntent = new Intent(this, activity);
		startActivity(myIntent);
	}

}
