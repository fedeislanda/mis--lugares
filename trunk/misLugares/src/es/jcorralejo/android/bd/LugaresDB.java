package es.jcorralejo.android.bd;

import android.provider.BaseColumns;

public class LugaresDB {
	
	// Nombre de la base de datos
	public static final String DB_NAME = "lugares.db";
	// Version de la base de datos
	public static final int DB_VERSION = 1;
	
	// Esta clase no debe ser instanciada
	private LugaresDB() {}
	
	// Definicion de la tabla "lugares"
	public static final class Lugar implements BaseColumns {
		// Esta clase no debe ser instanciada
		private Lugar() {}
		
		// Orden por defecto
		public static final String DEFAULT_SORT_ORDER = "_ID DESC";
		
		// Abstraccion de los nombres de campos y tablas
		public static final String NOMBRE_TABLA = "lugar";
		public static final String _ID = "_id";
		public static final String NOMBRE = "nombre";
		public static final String DESCRIPCION = "descripcion";
		public static final String LATITUD = "latitud";
		public static final String LONGITUD = "longitud";
		public static final String FOTO = "foto";
		public static final String _COUNT = "6";
	}

}
