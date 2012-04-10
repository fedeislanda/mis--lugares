package es.jcorralejo.android.utils;

public class Constantes {
	
	/**
	 * Constante para identificar el parámetro id de un lugar concreto 
	 */
	public static final String PARAMETRO_ID_LUGAR = "idLugar";
	
	/**
	 * Constante para identificar el parámetro punto seleccionado en el mapa 
	 */
	public static final String PARAMETRO_PUNTO_MAPA_SELECCIONADO = "puntoMapaSeleccionado";
	
	/**
	 * Constante para identificar el popUp para perdir confirmación al usuario de un Lugar
	 */
	public static final int DIALOG_PEDIR_CONFIRMACION_MULTIPLE = 0;
	
	/**
	 * Constante para identificar el popUp para perdir confirmación al usuario de un Lugar
	 */
	public static final int DIALOG_PEDIR_CONFIRMACION_SIMPLE = 1;
	
	/**
	 * Constante para identificar cada foto seleccionada de la Galería
	 */
	public static final int RESULT_FOTO = 1;

	/**
	 * Constante para identificar la nueva coordenada cuando la estamos editando en un lugar
	 */
	public static final int RESULT_EDITAR_COORDENADA = 2;

	/**
	 * Constante para identificar la opción de menú contextual "DETALLES"
	 */
	public static final int MENU_DETALLES = 0;

	/**
	 * Constante para identificar la opción de menú contextual "EDITAR"
	 */
	public static final int MENU_EDITAR = 1;

	/**
	 * Constante para identificar la opción de menú contextual "ELIMINAR"
	 */
	public static final int MENU_ELIMINAR = 4;

	/**
	 * Constante para identificar la opción de menú contextual "VER UBICACIÓN"
	 */
	public static final int MENU_VER_UBICACION = 2;
	
	/**
	 * Constante para identificar la opción de menú contextual "VER UBICACIÓN"
	 */
	public static final int MENU_NAVEGAR = 3;
	
	/**
	 * Constante para indicar TODOS los lugares almacenados en BD
	 */
	public static final long TODOS_LUGARES = Long.MAX_VALUE;

	/**
	 * Constante para indicar NINGUN lugar almacenad en BD
	 */
	public static final long NINGUN_LUGAR = Long.MIN_VALUE;

	/**
	 * Constante para identificar el popUp "Acerca de..."
	 */
	public static final int DIALOG_ACERCA_DE = 0;

	/**
	 * Constante para identificar el popUp "Opciones sobre Punto en el Mapa"
	 */
	public static final int DIALOG_OPCIONES_MAPA = 0;

	/**
	 * Constante para identificar el índice de las coordenadas que identifica a la latitud 
	 */
	public static final int INDICE_COORDENADA_LATITUD = 0;

	/**
	 * Constante para identificar el índice de las coordenadas que identifica a la longitud
	 */
	public static final int INDICE_COORDENADA_LONGITUD = 1;
	
	/**
	 * Constante para indicar si estamos editando las coordenadas de un lugar
	 */
	public static final String EDITAR_COORDENADA_LUGAR = "EDITAR_COORDENADA_LUGAR";
	
	/**
	 * Constante para indicar los caracteres a mostrar en el resumen de la descripción de un Lugar
	 */
	public static final int NUM_CARACTERES_RESUMEN_DESCRIPCION = 40;
	
	
}
