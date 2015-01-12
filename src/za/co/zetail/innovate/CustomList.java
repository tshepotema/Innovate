package za.co.zetail.innovate;

import java.util.ArrayList;

import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomList extends ArrayAdapter<String> {
	
	private final Activity context;
	
	private final ArrayList<String> nid;
	private final ArrayList<String> title;
	private final ArrayList<String> description;
	private final ArrayList<String> username;
	private final ArrayList<String> idea_pic;
	private final ArrayList<String> user_pic;
	private final ArrayList<String> updated;
	
	public CustomList(Activity context, ArrayList<String> nid2, ArrayList<String> title2, ArrayList<String> description2
			, ArrayList<String> username2, ArrayList<String> idea_pic2, ArrayList<String> user_pic2, ArrayList<String> updated2) {
		super(context, R.layout.idea_list_row, title2);
		this.context = context;
		this.nid = nid2;
		this.title = title2;
		this.description = description2;
		this.username = username2;
		this.idea_pic = idea_pic2;
		this.user_pic = user_pic2;
		this.updated = updated2;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		
		View rowView = inflater.inflate(R.layout.idea_list_row, null, true);
		
		TextView txtUsername = (TextView) rowView.findViewById(R.id.tvUsername);		
		TextView txtUpdated = (TextView) rowView.findViewById(R.id.tvUpdated);
		TextView txtIdeaTitle = (TextView) rowView.findViewById(R.id.tvIdeaTitle);
		TextView txtPhotoDescription = (TextView) rowView.findViewById(R.id.tvDescription);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.list_image);
				
		txtUsername.setText("" + username.get(position));
		txtUpdated.setText("" + updated.get(position));
		txtIdeaTitle.setText("" + title.get(position));
		txtPhotoDescription.setText("" + description.get(position));
		
		String imageURL = idea_pic.get(position);
		
		Picasso.with(context)
			.load(imageURL)
			.placeholder(R.drawable.photoholder)
			.into(imageView);		
				
		return rowView;
	}
				
}