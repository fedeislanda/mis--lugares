package es.jcorralejo.android.bd;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class LugaresProvider extends ContentProvider {
	
	public static final Uri CONTENT_URI = Uri.parse("content://es.jcorralejo.lugares");
	private static final int LUGAR = 1;
	private static final int LUGAR_ID = 2;
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("es.jcorralejo.lugares", "lugar", LUGAR);
		uriMatcher.addURI("es.jcorralejo.lugares", "lugar/#", LUGAR_ID);
	}
	
	private SQLiteDatabase lugaresDB;
	
	@Override
	public boolean onCreate() {
		Context context = getContext();
		LugaresSQLHelper dbHelper = new LugaresSQLHelper(context);
		lugaresDB = dbHelper.getWritableDatabase();
		return (lugaresDB == null) ? false : true;
	}


	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
			case LUGAR:
				// Para conjunto de lugares
				return "vnd.android.cursor.dir/vnd.jcorralejo.lugar";
			case LUGAR_ID:
				// Para un solo lugar
				return "vnd.android.cursor.item/vnd.jcorralejo.lugar";
			default:
				throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowID = lugaresDB.replace(LugaresDB.Lugar.NOMBRE_TABLA, "", values);
		// Si todo ha ido ok devolvemos su Uri
		if (rowID > 0) {
			Uri baseUri = Uri.parse("content://es.jcorralejo.lugares/lugar");
			Uri _uri = ContentUris.withAppendedId(baseUri, rowID);
			getContext().getContentResolver().notifyChange(_uri, null);
			return _uri;
		}
		
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereargs) {
		int count = 0;
		switch (uriMatcher.match(uri)) {
			case LUGAR:
				count = lugaresDB.delete(LugaresDB.Lugar.NOMBRE_TABLA, where, whereargs);
				break;
			case LUGAR_ID:
				String id = uri.getPathSegments().get(1);
				count = lugaresDB.delete(LugaresDB.Lugar.NOMBRE_TABLA, LugaresDB.Lugar._ID + " = " + id + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereargs);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int count = 0;
		
		switch (uriMatcher.match(uri)) {
			case LUGAR:
				count = lugaresDB.update(LugaresDB.Lugar.NOMBRE_TABLA, values, selection, selectionArgs);
				break;
			case LUGAR_ID:
				count = lugaresDB.update(LugaresDB.Lugar.NOMBRE_TABLA, 
										 values, 
										 LugaresDB.Lugar._ID + " = " + uri.getPathSegments().get(1) + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables(LugaresDB.Lugar.NOMBRE_TABLA);
		
		if (uriMatcher.match(uri) == LUGAR_ID)
			sqlBuilder.appendWhere(LugaresDB.Lugar._ID + " = " + uri.getPathSegments().get(1));
		
		if (sortOrder == null || sortOrder == "")
			sortOrder = LugaresDB.Lugar.DEFAULT_SORT_ORDER;

		Cursor c = sqlBuilder.query(lugaresDB, projection, selection, selectionArgs, null, null, sortOrder);
		
		// Registramos los cambios para que se enteren nuestros observers
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

}
