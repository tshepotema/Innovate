package za.co.zetail.innovate;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;

import za.co.zetail.innovate.volley.Const;
import za.co.zetail.innovate.volley.VolleySingleton;

public class Login extends Activity implements AnimationListener {
	
	Animation animSequential;
	ImageView ivLogo;
	TextView tvStatusMessage;
	EditText etUsername, etPassword;
	Button bLogin;
	ProgressDialog pDialog;
	
	private static String TAG = Login.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
				
		initializeLoginLayout();
		
		// load the animation
		animSequential = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.sequential);

		// set animation listener
		animSequential.setAnimationListener(this);

		// start the animation
		ivLogo.startAnimation(animSequential);		
	}
		
	private void initializeLoginLayout() {
		ivLogo = (ImageView) findViewById(R.id.ivLogo);
		tvStatusMessage = (TextView) findViewById(R.id.tvStatusMessage);
		etUsername = (EditText) findViewById(R.id.etUsername);
		etPassword = (EditText) findViewById(R.id.etPassword);
		bLogin = (Button) findViewById(R.id.bLogin);
		
		bLogin.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				//validate logins
				pDialog.show();
				attemptLogin();
			}
		});	
		
		pDialog = new ProgressDialog(this);
		pDialog.setMessage("Signin in...");
		pDialog.setCancelable(false);
	}
	
	/*
	 * send a volley request to the server to validate login details
	 */
	private void attemptLogin() {
		String username = etUsername.getText().toString();
		String password = etPassword.getText().toString();
		
		JSONObject loginDetails = new JSONObject();
		try {
			loginDetails.put("username", username);
			loginDetails.put("password", password);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		JSONArray reqData = new JSONArray();
		reqData.put(loginDetails); 
		
		JSONObject jsonParameters = new JSONObject();
		try {
			jsonParameters.put("action", "login");
			jsonParameters.put("data", reqData);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Request a json object response from the provided URL.
		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST, Const.WS_URL, jsonParameters,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject responseObject) {
						//parse the response
						String responseStatus = "failed", responseAction = "unknown", email = "";
						int userID = 0;
						JSONObject userDetails;
						try {
							responseStatus = responseObject.getString("status");
							responseAction = responseObject.getString("action");
							userDetails = responseObject.getJSONObject("data");
							if (responseStatus.equals("success")) {
								email = userDetails.getString("email");
								userID = userDetails.getInt("uid");
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
														
						if (responseStatus.equals("success") && responseAction.equals("login")) {
							Intent openMain = new Intent(Login.this, MainActivity.class);
							startActivity(openMain);													
						} else {																		
							//tvStatusMessage.setText("action = " + responseAction + " status = " + responseStatus + " :: userID = " + userID + " :: email = " + email);
							tvStatusMessage.setText("Invalid login details");
							etPassword.setText("");
						}
						if (pDialog.isShowing()) pDialog.dismiss();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						//tvStatusMessage.setText("Error: " + error.getMessage());
						tvStatusMessage.setText("Connection to system temporarily unavailable");
						if (pDialog.isShowing()) pDialog.dismiss();
					}
				}) {

			/**
			 * Passing some request headers
			 * */
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();
				headers.put("Content-Type", "application/json; charset=utf-8");
				return headers;
			}

			@Override
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("User name", Const.SECURITY_USER);
				params.put("Password",  Const.SECURITY_PASS);

				return params;
			}

		};
						
		// Access the RequestQueue through my singleton class.
		VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);				
		
		//Intent openMain = new Intent(Login.this, MainActivity.class);
		//startActivity(openMain);		
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub		
	}

}