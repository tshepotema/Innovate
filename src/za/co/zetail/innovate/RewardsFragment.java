package za.co.zetail.innovate;

import java.util.ArrayList;
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

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RewardsFragment extends Fragment {
	
	ListView lvRewards;
	
	ArrayList<String> nid = new ArrayList<String>();
	ArrayList<String> title = new ArrayList<String>();
	ArrayList<String> description = new ArrayList<String>();
	ArrayList<String> reward_pic = new ArrayList<String>();
	ArrayList<String> points = new ArrayList<String>();
	
	public CustomListRewards adapter;
	
	private Context activityContext, appContext;
	
	private ProgressDialog pDialog;
	
	TextView statusMessage;
	
	public RewardsFragment(Context mainContext) {
		this.appContext = mainContext;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View newIdeasView = inflater.inflate(R.layout.rewards_layout, container, false);
		
		statusMessage = (TextView) newIdeasView.findViewById(R.id.tvStatusMessage);
		
		adapter = new CustomListRewards(getActivity(), nid, title, description, reward_pic, points);
		lvRewards = (ListView) newIdeasView.findViewById(R.id.lvRewards);
		lvRewards.setAdapter(adapter);
		
		activityContext = getActivity();
		
		pDialog = new ProgressDialog(activityContext);
		pDialog.setTitle("Rewards");
		pDialog.setMessage("Loading...");
		pDialog.setCancelable(true);	
		pDialog.show();
		
		//get the list of rewards
		retrieveRewards();
		
		lvRewards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        				
				final String rewardNid = nid.get(position);
				String rewardPic = reward_pic.get(position);
				String rewardDesc = description.get(position);
				String rewardTitle = title.get(position);
				String rewardPoints = points.get(position);
				
				// custom dialog
				final Dialog rewardDialog = new Dialog(activityContext);
				rewardDialog.setContentView(R.layout.rewards_detail_view);
				rewardDialog.setTitle(rewardTitle);
				
				// set the custom dialog components - text, image and button
				ImageView ivIdeaPic = (ImageView) rewardDialog.findViewById(R.id.ivRewardPhoto);				
				Picasso.with(activityContext)
					.load(rewardPic)
					.placeholder(R.drawable.photoholder)
					.into(ivIdeaPic);
								
				TextView tvPoints = (TextView) rewardDialog.findViewById(R.id.tvPointsReq);
				tvPoints.setText("Points : " + rewardPoints);
								
				TextView tvDescription = (TextView) rewardDialog.findViewById(R.id.tvDescription);
				tvDescription.setText("" + rewardDesc);
				
				Button btClose = (Button) rewardDialog.findViewById(R.id.btBack);
				btClose.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						rewardDialog.dismiss();
					}
				});
				
				rewardDialog.show();
			}
		});
				
		return newIdeasView;
	}
	
	public void retrieveRewards() {
		JSONObject jsonParameters = new JSONObject();
		try {
			jsonParameters.put("action", "listRewards");
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		
		// Request a json object response from the provided URL.		
		JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST, Const.WS_URL, jsonParameters,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject responseObject) {
						//parse the response
						String responseStatus = "failed";
						JSONArray ideasDetails;
						try {
							responseStatus = responseObject.getString("status");
							ideasDetails = responseObject.getJSONArray("data");
							if (responseStatus.equals("success")) {
								for (int i = 0; i < ideasDetails.length(); i++) {
									JSONObject ideaDetail = ideasDetails.getJSONObject(i);
									nid.add(ideaDetail.getString("nid"));
									title.add(ideaDetail.getString("title"));
									description.add(ideaDetail.getString("description"));
									points.add(ideaDetail.getString("points"));
									reward_pic.add(ideaDetail.getString("reward_pic"));
								}
							}
							//statusMessage.setText(ideasDetails.toString());
							//statusMessage.setVisibility(View.VISIBLE);

						} catch (JSONException e) {
							e.printStackTrace();
						}
														
						if (responseStatus.equals("failed")) {
							statusMessage.setText("No response from systems : " + responseObject.toString());
							statusMessage.setVisibility(View.VISIBLE);
						}
						if (pDialog.isShowing()) pDialog.dismiss();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						//tvStatusMessage.setText("Error: " + error.getMessage());
						statusMessage.setText("Connection to system temporarily unavailable");
						statusMessage.setVisibility(View.VISIBLE);
						if (pDialog.isShowing()) pDialog.dismiss();
					}
				}) {

			/**
			 * Passing some request headers
			 **/
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
						
		//Access the RequestQueue through my singleton class.
		VolleySingleton.getInstance(appContext).addToRequestQueue(jsonObjReq);				
		
	}
	
}