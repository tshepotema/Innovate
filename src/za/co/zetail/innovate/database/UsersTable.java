package za.co.zetail.innovate.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UsersTable {
	//Database Table
	public static final String DATABASE_INNOVATE = "innovate.db";
	public static final String TABLE_USERS = "users";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_EMAIL = "email";
	public static final String COLUMN_PICTURE = "picture";
	public static final String COLUMN_FOLLOWING = "following";
	public static final String COLUMN_FOLLOWER = "follower";
	
	//Logging tag
	private static final String TAG = IdeasTable.class.getSimpleName();
	
	//Database Creation SQL
	public static final String DATABASE_CREATE = "" +
			"CREATE TABLE " + TABLE_USERS + " (" +
			COLUMN_ID + " INTEGER PRIMARY KEY, " +
			COLUMN_NAME + " TEXT NOT NULL, " + 
			COLUMN_EMAIL + " TEXT NOT NULL, " + 
			COLUMN_PICTURE + " TEXT, " + 
			COLUMN_FOLLOWING + " INTEGER DEFAULT 0, " + 
			COLUMN_FOLLOWER + " INTEGER DEFAULT 0" + ")";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		Log.d(TAG, TABLE_USERS + " table has been created");
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVer, int newVer) {
		Log.d(TAG, "Upgrading database from ver " + oldVer + " to ver " + newVer);
		database.execSQL("DROP IF EXISTS TABLE " + TABLE_USERS);
		onCreate(database);
	}

}
