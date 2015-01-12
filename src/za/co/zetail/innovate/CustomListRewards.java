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

public class CustomListRewards extends ArrayAdapter<String> {
	
	private final Activity context;
	
	private final ArrayList<String> nid;
	private final ArrayList<String> title;
	private final ArrayList<String> description;
	private final ArrayList<String> reward_pic;
	private final ArrayList<String> points;
	
	public CustomListRewards(Activity context, ArrayList<String> nid2, ArrayList<String> title2, ArrayList<String> description2
			, ArrayList<String> reward_pic2, ArrayList<String> points2) {
		super(context, R.layout.rewards_list_row, title2);
		this.context = context;
		this.nid = nid2;
		this.title = title2;
		this.description = description2;
		this.reward_pic = reward_pic2;
		this.points = points2;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		
		View rowView = inflater.inflate(R.layout.rewards_list_row, null, true);
		
		TextView txtPoints = (TextView) rowView.findViewById(R.id.tvPoints);		
		TextView txtRewardTitle = (TextView) rowView.findViewById(R.id.tvRewardTitle);
		TextView txtDescription = (TextView) rowView.findViewById(R.id.tvDescription);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.list_image);
				
		txtPoints.setText("" + points.get(position));
		txtRewardTitle.setText("" + title.get(position));
		txtDescription.setText(description.get(position));
		
		String imageURL = reward_pic.get(position);		
		Picasso.with(context)
			.load(imageURL)
			.placeholder(R.drawable.photoholder)
			.into(imageView);		
				
		return rowView;
	}				
}