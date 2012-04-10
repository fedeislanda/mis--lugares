package es.jcorralejo.android.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import es.jcorralejo.android.R;
import es.jcorralejo.android.bd.LugaresDB.Lugar;
import es.jcorralejo.android.bd.LugaresProvider;
import es.jcorralejo.android.utils.Constantes;


public class EditarLugarActivity extends LugarAbstractActivity{

	/** Uri de la imaegn seleccionada de la galería */
	private Uri uriNuevaImagen;
	
	/** Indica si estamos agregando un luegar nuevo o editando uno existente */
	private boolean agregando = false;
	
	/** Indica si estamos editando las coordenadas de un lugar existente */
	private boolean editarCoordenada = false;
	
	/** Guarda la información de las coordenada a la hora de crear un nuevo lugar */
	float[] coordenada = null;
	
	@Override
	protected int getLayout() {
		return R.layout.editar_lugar;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Al pulsar sobre la imagen vamos a seleccionar una de la galería del teléfono
		imagenLugar.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					navegarEditarImagen();
				}
			}
		);
		
		Bundle extras = getIntent().getExtras();
		if(extras!=null){
			coordenada = (float[]) extras.get(Constantes.PARAMETRO_PUNTO_MAPA_SELECCIONADO);
			if(extras.containsKey(Constantes.EDITAR_COORDENADA_LUGAR))
				editarCoordenada = extras.getBoolean(Constantes.EDITAR_COORDENADA_LUGAR);
			agregando = coordenada!=null && !editarCoordenada;
		}
		
		// Botón editar/agregar
		Button botonEditar = (Button) findViewById(R.id.botonEditar);
		botonEditar.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					Uri uri = Uri.parse(LugaresProvider.CONTENT_URI+"/lugar");
					ContentValues contentValues = new ContentValues();
					if(agregando){
						if(nombreYDescripcionIncorrectos())
							Toast.makeText(getBaseContext(), R.string.control_agregar_lugar, Toast.LENGTH_SHORT).show();
						else{
							contentValues.put(Lugar.FOTO, uriNuevaImagen!=null ? uriNuevaImagen.toString() : null);
							contentValues.put(Lugar.DESCRIPCION, descripcionLugar.getText().toString());
							contentValues.put(Lugar.NOMBRE, nombreLugar.getText().toString());
							contentValues.put(Lugar.LATITUD, coordenada[0]);
							contentValues.put(Lugar.LONGITUD, coordenada[1]);
							getContentResolver().insert(uri, contentValues);
							// Volvemos a la pantalla anterior
							finish();
						}
					}else{
						//Modificamos el lugar con los nuevos datos
						if(ignorarDatosBD)
							contentValues.put(Lugar.FOTO, uriNuevaImagen!=null ? uriNuevaImagen.toString() : null);
						contentValues.put(Lugar.DESCRIPCION, descripcionLugar.getText().toString());
						contentValues.put(Lugar.NOMBRE, nombreLugar.getText().toString());
						if(editarCoordenada){
							contentValues.put(Lugar.LATITUD, coordenada[0]);
							contentValues.put(Lugar.LONGITUD, coordenada[1]);
						}
						getContentResolver().update(uri, contentValues, Lugar._ID+" = "+idLugar, null);
						// Volvemos a la pantalla anterior
						finish();
					}
				}
			}
		);
		
		// Botón eliminar/cancelar
		Button botonEliminar = (Button) findViewById(R.id.botonEliminar);
		botonEliminar.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(agregando){
							finish();
						}else
							//Levantamos el popUp de confirmación
							showDialog(Constantes.DIALOG_PEDIR_CONFIRMACION_SIMPLE);
					}
				}
		);
		
		//Si en vez de estar editando estamos agregando modificamos la descripción de los botones
		//y el texto que aparece en los input
		if(agregando){
			botonEditar.setText(R.string.agregar);
			botonEliminar.setText(R.string.cancelar);
			
			nombreLugar.setText(R.string.msg_agregar_nombre);
			descripcionLugar.setText(R.string.msg_agregar_descripcion);
			imagenLugar.setImageResource(R.drawable.no_imagen);

			traducirCoordenadas(coordenada);
		}else{
			direccionLugar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(getBaseContext(), R.string.msg_info_editar_coordenadas_lugar, Toast.LENGTH_LONG).show();
					
					Intent i = new Intent();
					i.setClass(getApplicationContext(), MapaLugaresActivity.class);
		    		i.putExtra(Constantes.PARAMETRO_ID_LUGAR, idLugar); 
		    		i.putExtra(Constantes.EDITAR_COORDENADA_LUGAR, true); 
		    		startActivityForResult(i, Constantes.RESULT_EDITAR_COORDENADA);
				}
			});
		}
	}
	
	/**
	 * Comprueba que el nombre y la descripción estén rellenos
	 * @return
	 */
	private boolean nombreYDescripcionIncorrectos(){
		String msgAgregarDescripcion = getResources().getString(R.string.msg_agregar_descripcion);
		String msgAgregarNombre = getResources().getString(R.string.msg_agregar_nombre);
		
		return "".equals(nombreLugar.getText().toString()) || "".equals(descripcionLugar.getText().toString())
			|| msgAgregarDescripcion.equals(descripcionLugar.getText().toString())
			|| msgAgregarNombre.equals(nombreLugar.getText().toString());
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == Constantes.RESULT_FOTO){
			if(resultCode == RESULT_OK){
				if(data!=null){
					// Procesamos la imagen de vuelta de la galería
					Uri selectedImage = data.getData();
					setImagen(selectedImage);
					uriNuevaImagen = selectedImage;
					ignorarDatosBD = true;
				}
			}
		}
		
		else if (requestCode == Constantes.RESULT_EDITAR_COORDENADA){
			if(resultCode == RESULT_OK){
				if(data!=null && data.getExtras()!=null){
					Bundle extras = data.getExtras();
					coordenada = (float[]) extras.get(Constantes.PARAMETRO_PUNTO_MAPA_SELECCIONADO);
					if(extras.containsKey(Constantes.EDITAR_COORDENADA_LUGAR))
						editarCoordenada = extras.getBoolean(Constantes.EDITAR_COORDENADA_LUGAR);
					agregando = coordenada!=null && !editarCoordenada;
					ignorarDatosBD = true;
				}
				
				traducirCoordenadas(coordenada);
			}
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, Constantes.MENU_EDITAR, 0, R.string.editar);
		menu.add(0, Constantes.MENU_ELIMINAR, 0, R.string.eliminar);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    switch(item.getItemId()) {
	    	// Navegamos a la galería para seleccionar otra imagen
	        case Constantes.MENU_EDITAR:
	        	navegarEditarImagen();
	            return true;
	        // Eliminamos la imagen actual del lugar
	        case Constantes.MENU_ELIMINAR:
	        	imagenLugar.setImageResource(R.drawable.no_imagen);
	        	ignorarDatosBD = true;
	        	uriNuevaImagen = null;
	        	return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	/**
	 * Navega a la galería para seleccionar una nueva imagen para el lugar
	 */
	private void navegarEditarImagen(){
		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		startActivityForResult(intent, Constantes.RESULT_FOTO);	
	}
	
	
}
