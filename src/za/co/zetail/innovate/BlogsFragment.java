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

public class BlogsFragment extends Fragment {
	
	ListView lvBlogs;
	
	ArrayList<String> nid = new ArrayList<String>();
	ArrayList<String> title = new ArrayList<String>();
	ArrayList<String> description = new ArrayList<String>();
	ArrayList<String> username = new ArrayList<String>();
	ArrayList<String> idea_pic = new ArrayList<String>();
	ArrayList<String> user_pic = new ArrayList<String>();
	ArrayList<String> updated = new ArrayList<String>();
	
	public CustomListBlogs adapter;
	
	private Context activityContext, appContext;
	
	private ProgressDialog pDialog;
	
	TextView statusMessage;
	
	public BlogsFragment(Context mainContext) {
		this.appContext = mainContext;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View newIdeasView = inflater.inflate(R.layout.blogs_layout, container, false);
		
		statusMessage = (TextView) newIdeasView.findViewById(R.id.tvStatusMessage);
		
		adapter = new CustomListBlogs(getActivity(), nid, title, description, username, idea_pic, user_pic, updated);
		lvBlogs = (ListView) newIdeasView.findViewById(R.id.lvBlogs);
		lvBlogs.setAdapter(adapter);
		
		activityContext = getActivity();
		
		pDialog = new ProgressDialog(activityContext);
		pDialog.setTitle("Inspiration");
		pDialog.setMessage("Loading...");
		pDialog.setCancelable(true);	
		pDialog.show();
		
		//get list of inspirational articles (blogs) 
		retrieveBlogs();
		
		lvBlogs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {        				
				final String blogNid = nid.get(position);
				String blogPic = idea_pic.get(position);
				String blogDesc = description.get(position);
				String ideaTitle = title.get(position);
				String authorname = username.get(position);
				String authorpic = user_pic.get(position);
				String lastUpdated = updated.get(position);
				
				// custom dialog
				final Dialog blogDialog = new Dialog(activityContext);
				blogDialog.setContentView(R.layout.blog_detail_view);
				blogDialog.setTitle(ideaTitle);
				
				// set the custom dialog components - text, image and button
				ImageView ivIdeaPic = (ImageView) blogDialog.findViewById(R.id.ivBlogImage);				
				Picasso.with(activityContext)
					.load(blogPic)
					.placeholder(R.drawable.photoholder)
					.into(ivIdeaPic);		
								
				TextView tvUsername = (TextView) blogDialog.findViewById(R.id.tvUsername);
				tvUsername.setText("" + authorname);
				
				ImageView ivUserAvatar = (ImageView) blogDialog.findViewById(R.id.ivUserAvatar);				
				Picasso.with(activityContext)
					.load(authorpic)
					.resize(80, 80)
					.placeholder(R.drawable.photoholder)
					.into(ivUserAvatar);		
				
				TextView tvLastUpdated = (TextView) blogDialog.findViewById(R.id.tvLastUpdated);
				tvLastUpdated.setText("Last updated: " + lastUpdated);				
				
				TextView tvDescription = (TextView) blogDialog.findViewById(R.id.tvBlogDescription);
				tvDescription.setText("" + blogDesc);
				
				blogDialog.show();					 				
			}
		});
				
		return newIdeasView;
	}
	
	public void retrieveBlogs() {
		JSONObject jsonParameters = new JSONObject();
		try {
			jsonParameters.put("action", "getBlogs");
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
									idea_pic.add(ideaDetail.getString("blog_pic"));
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