package es.jcorralejo.android.bd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LugaresSQLHelper extends SQLiteOpenHelper{
	
	public LugaresSQLHelper(Context context) {
		super(context, LugaresDB.DB_NAME, null, LugaresDB.DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		if (db.isReadOnly()) 
			db = getWritableDatabase(); 
		
		db.execSQL("CREATE TABLE " +
					LugaresDB.Lugar.NOMBRE_TABLA + " (" +
					LugaresDB.Lugar._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
					LugaresDB.Lugar.NOMBRE + " TEXT," +
					LugaresDB.Lugar.DESCRIPCION + " TEXT," +
					LugaresDB.Lugar.LATITUD + " FLOAT," +
					LugaresDB.Lugar.LONGITUD + " FLOAT," +
					LugaresDB.Lugar.FOTO + " TEXT" + ")");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// No tiene nada que hacer
	}

}
