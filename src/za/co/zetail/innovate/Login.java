package za.co.zetail.innovate;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;

import za.co.zetail.innovate.volley.Const;
import za.co.zetail.innovate.volley.VolleySingleton;

public class Login extends Activity implements AnimationListener {
	
	Animation animSequential;
	ImageView ivLogo;
	EditText etUsername, etPassword;
	Button bLogin;
	
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
		etUsername = (EditText) findViewById(R.id.etUsername);
		etPassword = (EditText) findViewById(R.id.etPassword);
		bLogin = (Button) findViewById(R.id.bLogin);
		
		bLogin.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				//validate logins
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

				// Request a string response from the provided URL.
				JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST, Const.WS_URL, jsonParameters,
						new Response.Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								etUsername.setText(response.toString());
							}
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								VolleyLog.d(TAG, "Error: " + error.getMessage());
								etUsername.setText("Error: " + error.getMessage());
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
						params.put("name", "TshepoDeveloper");
						params.put("email", "tshepo@myemail.com");
						params.put("pass", "Password444");

						return params;
					}

				};
								
				// Access the RequestQueue through my singleton class.
				VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);				
				
				//onSuccessful login
				//Intent openMain = new Intent(Login.this, MainActivity.class);
				//startActivity(openMain);				
			}
		});		
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