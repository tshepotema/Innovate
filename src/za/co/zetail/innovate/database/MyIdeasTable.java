package za.co.zetail.innovate.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MyIdeasTable {
	//Database Table
	public static final String DATABASE_FUNCAM = "innovate.db";
	public static final String TABLE_MYIDEAS = "myideas";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_BODY = "body";
	public static final String COLUMN_AUTHOR = "author";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_IMG_PATH = "path";
	
	//Logging tag
	private static final String TAG = MyIdeasTable.class.getSimpleName();
	
	//Database Creation SQL
	public static final String DATABASE_CREATE = "" +
			"CREATE TABLE " + TABLE_MYIDEAS + " (" +
			COLUMN_ID + " INTEGER PRIMARY KEY, " +
			COLUMN_TITLE + " TEXT NOT NULL, " + 
			COLUMN_BODY + " TEXT NOT NULL, " + 
			COLUMN_AUTHOR + " TEXT NOT NULL, " + 
			COLUMN_DATE + " TEXT NOT NULL, " + 
			COLUMN_IMG_PATH + " TEXT NULL" + ")";
	
	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		Log.d(TAG, TABLE_MYIDEAS + " table has been created");
	}
	
	public static void onUpgrade(SQLiteDatabase database, int oldVer, int newVer) {
		Log.d(TAG, "Upgrading database from ver " + oldVer + " to ver " + newVer);
		database.execSQL("DROP IF EXISTS TABLE " + TABLE_MYIDEAS);
		onCreate(database);
	}
}