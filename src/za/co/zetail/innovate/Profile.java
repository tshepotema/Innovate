package za.co.zetail.innovate;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import za.co.zetail.innovate.volley.Const;
import za.co.zetail.innovate.volley.VolleySingleton;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class Profile extends Activity {
	
	TextView tvUsername, tvEmail, tvEarned, tvRedeemed, tvAvailable;
	ImageView ivProfilePhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_layout);
		
		initializeLayout();
		
		getProfileDetails();
	}

	private void initializeLayout() {
		ivProfilePhoto = (ImageView) findViewById(R.id.ivProfilePhoto);
		
		tvUsername = (TextView) findViewById(R.id.tvUsername);
		tvEmail = (TextView) findViewById(R.id.tvEmail);
		tvEarned = (TextView) findViewById(R.id.tvPointsEarned);
		tvRedeemed = (TextView) findViewById(R.id.tvPointsRedeemed);
		tvAvailable = (TextView) findViewById(R.id.tvPointsAvailable);				
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

	private void getProfileDetails() {
		JSONObject loginDetails = new JSONObject();
		try {
			loginDetails.put("uid", 49);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		JSONArray reqData = new JSONArray();
		reqData.put(loginDetails); 
		
		JSONObject jsonParameters = new JSONObject();
		try {
			jsonParameters.put("action", "getProfile");
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
						String responseStatus = "failed", email = "", username = "", picture = "";
						JSONObject userDetails;
						int pEarned = 0, pRedeemed = 0, pAvailable = 0;
						try {
							responseStatus = responseObject.getString("status");
							userDetails = responseObject.getJSONObject("data");
							Log.d("profile", userDetails.toString());
							if (responseStatus.equals("success")) {
								//for (int i = 0; i < userDetails.length(); i++) {
									//JSONObject userDetail = userDetails.getJSONObject(i);
									username = userDetails.getString("name");
									email = userDetails.getString("email");
									picture = userDetails.getString("picture");
									pEarned = userDetails.getInt("points_earned");
									pRedeemed = userDetails.getInt("points_redeemed");
									pAvailable = pEarned - pRedeemed;
								//}
								tvUsername.setText("" + username);
								tvEmail.setText("Email: " + email);
								tvEarned.setText("Points Earned: " + pEarned);
								tvRedeemed.setText("Points Redeemed: " + pRedeemed);
								tvAvailable.setText("Points Available: " + pAvailable);
								
								Picasso.with(getApplication())
								.load(picture)
								.placeholder(R.drawable.photoholder)
								.into(ivProfilePhoto);
								
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						//tvStatusMessage.setText("Error: " + error.getMessage());
						tvUsername.setText("Connection to system temporarily unavailable");
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
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}
	
}