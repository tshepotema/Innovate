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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NewIdeasFragment extends Fragment {
	
	ListView lvNewIdeas;
	
	ArrayList<String> nid = new ArrayList<String>();
	ArrayList<String> title = new ArrayList<String>();
	ArrayList<String> description = new ArrayList<String>();
	ArrayList<String> username = new ArrayList<String>();
	ArrayList<String> idea_pic = new ArrayList<String>();
	ArrayList<String> user_pic = new ArrayList<String>();
	ArrayList<String> updated = new ArrayList<String>();
	
	public CustomList adapter;
	
	private Context ideasContext, appContext;
	
	private ProgressDialog pDialog;
	
	TextView statusMessage;
	
	public NewIdeasFragment(Context mainContext) {
		this.appContext = mainContext;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View newIdeasView = inflater.inflate(R.layout.ideas_layout, container, false);
		
		statusMessage = (TextView) newIdeasView.findViewById(R.id.tvStatusMessage);
		
		adapter = new CustomList(getActivity(), nid, title, description, username, idea_pic, user_pic, updated);
		lvNewIdeas = (ListView) newIdeasView.findViewById(R.id.lvNewIdeas);
		lvNewIdeas.setAdapter(adapter);
		
		ideasContext = getActivity();
		
		pDialog = new ProgressDialog(ideasContext);
		pDialog.setTitle("New Ideas");
		pDialog.setMessage("Loading...");
		pDialog.setCancelable(true);	
		pDialog.show();
		
		//get the freshes ideas
		retrieveNewIdeas();
		
		lvNewIdeas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        				
				final String ideaNid = nid.get(position);
				String ideaPic = idea_pic.get(position);
				String ideaDesc = description.get(position);
				String ideaTitle = title.get(position);
				String authorname = username.get(position);
				String authorpic = user_pic.get(position);
				String lastUpdated = updated.get(position);
				
				// custom dialog
				final Dialog ideaDialog = new Dialog(ideasContext);
				ideaDialog.setContentView(R.layout.idea_detail_view);
				ideaDialog.setTitle(ideaTitle);
				
				// set the custom dialog components - text, image and button
				ImageView ivIdeaPic = (ImageView) ideaDialog.findViewById(R.id.ivIdeaImage);				
				Picasso.with(ideasContext)
					.load(ideaPic)
					.placeholder(R.drawable.photoholder)
					.into(ivIdeaPic);		
								
				TextView tvUsername = (TextView) ideaDialog.findViewById(R.id.tvUsername);
				tvUsername.setText("" + authorname);
				
				ImageView ivUserAvatar = (ImageView) ideaDialog.findViewById(R.id.ivUserAvatar);				
				Picasso.with(ideasContext)
					.load(authorpic)
					.resize(80, 80)
					.placeholder(R.drawable.photoholder)
					.into(ivUserAvatar);		
				
				TextView tvLastUpdated = (TextView) ideaDialog.findViewById(R.id.tvLastUpdated);
				tvLastUpdated.setText("Last updated: " + lastUpdated);				
				
				TextView tvDescription = (TextView) ideaDialog.findViewById(R.id.tvIdeaDescription);
				tvDescription.setText("" + ideaDesc);
				
				ideaDialog.show();					 				
			}
		});
				
		return newIdeasView;
	}
	
	public void retrieveNewIdeas() {
		JSONObject jsonParameters = new JSONObject();
		try {
			jsonParameters.put("action", "getNewIdeas");
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
									username.add(ideaDetail.getString("username"));
									idea_pic.add(ideaDetail.getString("idea_pic"));
									user_pic.add(ideaDetail.getString("user_pic"));
									updated.add(ideaDetail.getString("updated"));
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