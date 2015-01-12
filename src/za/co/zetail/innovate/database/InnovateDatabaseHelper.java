package za.co.zetail.innovate.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class InnovateDatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "innovate.db";
	private static final int DATABASE_VERSION = 1;

	public InnovateDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	//called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase db) {
		IdeasTable.onCreate(db);
		MyIdeasTable.onCreate(db);
		UsersTable.onCreate(db);
	}

	//called during the upgrade of the database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		IdeasTable.onUpgrade(db, oldVersion, newVersion);
		MyIdeasTable.onUpgrade(db, oldVersion, newVersion);
		UsersTable.onUpgrade(db, oldVersion, newVersion);
	}

}