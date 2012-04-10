package es.jcorralejo.android.activities;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import es.jcorralejo.android.R;
import es.jcorralejo.android.bd.LugaresDB.Lugar;
import es.jcorralejo.android.bd.LugaresProvider;
import es.jcorralejo.android.utils.Constantes;

public abstract class LugarAbstractActivity extends Activity{
	
	protected TextView nombreLugar;
	protected TextView descripcionLugar;
	protected TextView direccionLugar;
	protected ImageView imagenLugar;
	
	/**
	 * Identifica el id del lugar que se está mostrando
	 */
	protected long idLugar;
	
	/**
	 * Indica si se debe ignorar los datos que tiene el lugar en BD o los que se editaron en la pantalla
	 */
	protected boolean ignorarDatosBD;
	
	/**
	 * Devuelve el layout a mostrar
	 * @return Layout a mostrar
	 */
	protected abstract int getLayout();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		setTitle(R.string.lugar);
		nombreLugar = (TextView) findViewById(R.id.lugarNombre);
		descripcionLugar = (TextView) findViewById(R.id.lugarDescripcion);
		imagenLugar = (ImageView) findViewById(R.id.lugarImagen);
		direccionLugar = (TextView) findViewById(R.id.lugarDireccion);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		try {
			Bundle extras = getIntent().getExtras();
			if(extras!=null){
				idLugar = extras.getLong(Constantes.PARAMETRO_ID_LUGAR, Constantes.NINGUN_LUGAR);
				if(idLugar != Constantes.NINGUN_LUGAR){
					final String[] columnas = new String[] {Lugar._ID, Lugar.NOMBRE, Lugar.DESCRIPCION, Lugar.FOTO, Lugar.LATITUD, Lugar.LONGITUD};

					Uri uri = Uri.parse(LugaresProvider.CONTENT_URI+"/lugar");
					uri = ContentUris.withAppendedId(uri, idLugar);
					Cursor cursor = managedQuery(uri, columnas, null, null, null);
					cursor.setNotificationUri(getContentResolver(), uri);
					startManagingCursor(cursor);

					// Tomamos los datos del Lugar
					if(cursor.moveToFirst() && !ignorarDatosBD) 
						rellenarDatosLugar(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getFloat(4), cursor.getFloat(5));
				}
			}
			
		} catch (Exception e) {
			//Si se produce algún error al obtener los datos los lugar, avisamos al usuario
			//y cerramos la activity, volcando la traza del error
			Toast.makeText(this, R.string.msg_error_salir, Toast.LENGTH_SHORT).show();
			finish();
			e.printStackTrace();
		}
	}
	
	/**
	 * Rellena los campos con lo datos del lugar a mostrar
	 * @param nombre
	 * @param descripcion
	 * @param imagen
	 * @param latitud
	 * @param longitud
	 */
	private void rellenarDatosLugar(String nombre, String descripcion, String imagen, float latitud, float longitud){
		if(nombre!=null)
			nombreLugar.setText(nombre);
		if(descripcion!=null)
			descripcionLugar.setText(descripcion);
		if(imagen!=null)
			setImagen(Uri.parse(imagen));
		else
			imagenLugar.setImageResource(R.drawable.no_imagen);

		float[] coordenada = {latitud, longitud}; 
		traducirCoordenadas(coordenada);
	}
	
	/**
	 * Rellena el campo imagenLugar con la imagen identificada por el uri pasado por parémtros
	 * @param uriImagen
	 */
	protected void setImagen(Uri uriImagen){
		if(uriImagen!=null){
			long imgID = Long.parseLong(uriImagen.getLastPathSegment());
			Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(), imgID, MediaStore.Images.Thumbnails.MINI_KIND, null);
			imagenLugar.setAdjustViewBounds(true);
			imagenLugar.setScaleType(ScaleType.CENTER_INSIDE);
			imagenLugar.setImageBitmap(bitmap);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			// Abrimos el popUp para confirmar
			case Constantes.DIALOG_PEDIR_CONFIRMACION_SIMPLE:
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.msg_condirmacion_eliminar);
				builder.setPositiveButton(R.string.si,
										  new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												//Eliminamos el lugar
												Uri uri = Uri.parse(LugaresProvider.CONTENT_URI+"/lugar");
												getContentResolver().delete(uri, Lugar._ID+" = "+idLugar, null);
												
												// terminamos la actividad y volvemos al listado de lugares
												finish();
											}
									  	  });
				builder.setNegativeButton(R.string.no, 
										  new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												//No tenemos nada que hacer
											}
										  });
				
				return builder.create();
			default:
				return null;
		}
	}
	
	/**
	 * Intentar traducir a un idioma comprensible las coordenadas pasadas por parámetros
	 * @param coordenada
	 */
	protected void traducirCoordenadas(float[] coordenada){
		try {
			Geocoder gc = new Geocoder(this, Locale.getDefault());
			String dir = "";
			List<Address> addresses = gc.getFromLocation(coordenada[0], coordenada[1], 1);
			if (addresses.size() > 0) {
				Address address = addresses.get(0);
				for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
					dir += address.getAddressLine(i) + "\t";
				dir += address.getCountryName();
			}
			if(!dir.equals("")){
				direccionLugar.setVisibility(View.VISIBLE);
				direccionLugar.setText(dir);
			}else
				direccionLugar.setVisibility(View.GONE);
		} catch (IOException e) {
			//Controlamos este error porque el emulador de la versión 2.2 tiene un  bug
			//Si se produce algún error al "traducir" las coordenadas simplemente logueamos y ocultamos el campo 
			Log.e("Error al traducir coordenadas", e.getMessage());
			direccionLugar.setVisibility(View.GONE);
		}

	}
	
}
