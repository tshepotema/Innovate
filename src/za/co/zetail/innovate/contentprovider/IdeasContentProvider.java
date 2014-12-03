package za.co.zetail.innovate.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import za.co.zetail.innovate.database.IdeasTable;
import za.co.zetail.innovate.database.InnovateDatabaseHelper;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class IdeasContentProvider extends ContentProvider {

	//the database to be used
	private InnovateDatabaseHelper database;
	
	//used for the URI match
	private static final int IDEAS = 10;
	private static final int IDEA_ID = 20;
	private static final String AUTHORITY = "za.co.zetail.innovate.contentprovider";
	private static final String BASE_PATH = "IDEAS";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/IDEAS";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/image";
	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, IDEAS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", IDEA_ID);
	}

	@Override
	public boolean onCreate() {
		database = new InnovateDatabaseHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// Uisng SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		// Check if the caller has requested a column which does not exists
		checkColumns(projection);
		// Set the table
		queryBuilder.setTables(IdeasTable.TABLE_IDEAS);
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case IDEAS:
			break;
		case IDEA_ID:
			// Adding the ID to the original query
			queryBuilder.appendWhere(IdeasTable.COLUMN_ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// Make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		long id = 0;
		switch (uriType) {
		case IDEAS:
			id = sqlDB.insert(IdeasTable.TABLE_IDEAS, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType) {
		case IDEAS:
			rowsDeleted = sqlDB.delete(IdeasTable.TABLE_IDEAS, selection,
					selectionArgs);
			break;
		case IDEA_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(IdeasTable.TABLE_IDEAS,
						IdeasTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = sqlDB.delete(IdeasTable.TABLE_IDEAS,
						IdeasTable.COLUMN_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted; 
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType) {
		case IDEAS:
			rowsUpdated = sqlDB.update(IdeasTable.TABLE_IDEAS, values, selection,
					selectionArgs);
			break;
		case IDEA_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(IdeasTable.TABLE_IDEAS, values,
						IdeasTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsUpdated = sqlDB.update(IdeasTable.TABLE_IDEAS, values,
						IdeasTable.COLUMN_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	private void checkColumns(String[] projection) {
		String[] available = { IdeasTable.COLUMN_ID, IdeasTable.COLUMN_TITLE, IdeasTable.COLUMN_BODY, 
				IdeasTable.COLUMN_AUTHOR, IdeasTable.COLUMN_DATE, IdeasTable.COLUMN_IMG_PATH};
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(
					Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(
					Arrays.asList(available));
			// Check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException(
						"Unknown columns in projection");
			}
		}
	}
}