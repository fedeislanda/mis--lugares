package es.jcorralejo.android.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import es.jcorralejo.android.R;
import es.jcorralejo.android.bd.LugaresDB.Lugar;
import es.jcorralejo.android.bd.LugaresProvider;
import es.jcorralejo.android.utils.Constantes;

public class ListaLugaresActivity extends ListActivity{
	
	private SimpleCursorAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lugares);
		setTitle(R.string.nombre_lugares);
		
		//Configuramos el adapter
		configurarAdapter();
		
		// Registramos la lista de lugares para definir su menú contextual
		ListView listaLugares = (ListView) findViewById(android.R.id.list);
		registerForContextMenu(listaLugares);
	}
	
	public void configurarAdapter() {
		// Sólo necesitamos id, nombre y descripción de cada Lugar
		final String[] columnas = new String[] {Lugar._ID, Lugar.NOMBRE, Lugar.DESCRIPCION};
		// Nos traemos la información de todos los Lugares
		Uri uri = Uri.parse(LugaresProvider.CONTENT_URI+"/lugar");
		Cursor cursor = managedQuery(uri, columnas, null, null, Lugar.NOMBRE); //Ordeamos por Nombre
		// Queremos enterarnos si cambian los datos para recargar el cursor
		cursor.setNotificationUri(getContentResolver(), uri);
		// Para que la actividad se encarge de manejar el cursor según sus ciclos de vida
		startManagingCursor(cursor);
		
		// Mapeamos las querys SQL a los campos de las vistas
		String[] camposDb = new String[] {Lugar.NOMBRE, Lugar.DESCRIPCION};
		int[] camposView = new int[] {R.id.lugarNombre, R.id.lugarDescripcion};
		
		// Creamos el adapter
		adapter = new SimpleCursorAdapter(this, R.layout.lugares_item, cursor, camposDb, camposView);
		setListAdapter(adapter);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_lista, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Al pulsar sobre "Agregar" navegamos a la actividad MapaLugaresActivity con un Toast que indica
			// que se debe pulsar un punto para guardar el nuevo Lugar 
			case R.id.listaAgregar:
				Toast.makeText(this, R.string.msg_info_agregar_lugar, Toast.LENGTH_LONG).show();
				
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), MapaLugaresActivity.class);
				startActivity(intent);
				
				return true;
				
			// Al pulsar sobre "Eliminar" mostramos los checks para que el usuario marque los lugares
			// que quiere eliminar, además del botón "Eliminar" para confirmar la acción
			case R.id.listaEliminar:
				if(adapter.getCount()==0){
					Toast.makeText(this, R.string.control_eliminar_lugares, Toast.LENGTH_LONG).show();
					return true;
				}else
					return accionMenuEliminar();
				
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * Ejecutado al pulsar la opción de menú "Eliminar", muestra los checks para que el usuario marque los lugares
	 * que quiere eliminar, además del botón "Eliminar" para confirmar la acción
	 * @return
	 */
	private boolean accionMenuEliminar(){
		// Creamos el botón "Eliminar"
		Button botonEliminar = new Button(this);
		botonEliminar.setText(R.string.eliminar);
		botonEliminar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
		botonEliminar.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					showDialog(Constantes.DIALOG_PEDIR_CONFIRMACION_MULTIPLE);
				}
			}
		);
		
		// Creamos el botón "Cancelar"
		Button botonCancelar = new Button(this);
		botonCancelar.setText(R.string.cancelar);
		botonCancelar.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
		botonCancelar.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					configurarListenerCancelarEliminacion();
				}
			}
		);
		
		// Agrupamos ambos botones en un LinearLayout 
		LinearLayout linearLayoutBotones = new LinearLayout(this);
		linearLayoutBotones.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1); //El alto debe ser 0 para que se reparta bien el espacio con el ListView
		layoutParams.setMargins(0, 15, 0, 0);
		linearLayoutBotones.setLayoutParams(layoutParams);
		linearLayoutBotones.setGravity(Gravity.CENTER_HORIZONTAL);
		linearLayoutBotones.addView(botonEliminar,0);
		linearLayoutBotones.addView(botonCancelar,1);
		
		// Añadimos el LinearLayout al final de la pantalla
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutLugares);
		linearLayout.addView(linearLayoutBotones, 1);
		
		// Mostramos los checks 
		ListView lista = (ListView) findViewById(android.R.id.list);
		for(int i=0; i<lista.getChildCount(); i++){
			LinearLayout lugar = (LinearLayout) lista.getChildAt(i);
			LinearLayout check_nombre = (LinearLayout) lugar.getChildAt(0);
			CheckBox checkBox = (CheckBox)check_nombre.getChildAt(0);
			checkBox.setVisibility(View.VISIBLE);
		}
		
		return true;
	}
	
	/**
	 * Cancela la acción de eliminar lugares, es decir, carga de nuevo el adapter y elimina los botones "Eliminar" y "Cancelar"
	 */
	private void configurarListenerCancelarEliminacion(){
		// Cargamos de nuevo el adapter...
		configurarAdapter();
		// ..y eliminamos los botones "Eliminar" y "Cancelar"
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayoutLugares);
		linearLayout.removeViewAt(1);
	}
	
	/**
	 * Elimina los lugares seleccionados
	 */
	private void configurarListenerEliminar(){
		ListView lista = (ListView) findViewById(android.R.id.list);
		String idsEliminar = "";
		for(int i=0; i<lista.getChildCount(); i++){
			LinearLayout lugar = (LinearLayout) lista.getChildAt(i);
			LinearLayout check_nombre = (LinearLayout) lugar.getChildAt(0);
			CheckBox checkBox = (CheckBox)check_nombre.getChildAt(0);
			// Si está marcado, lo añadimos para eliminar el registro posteriormente
			if(checkBox.isChecked())
				idsEliminar += adapter.getItemId(i)+",";
		}
		
		// Eliminamos todos los registro a la vez, siempre que se haya seleccionado alguno
		if(!"".equals(idsEliminar)){
			//Añadimos los paréntesos y quitamos la última coma ","
			idsEliminar = "("+idsEliminar;
			idsEliminar = idsEliminar.substring(0, idsEliminar.length()-1)+")";
			
			Uri uri = Uri.parse(LugaresProvider.CONTENT_URI+"/lugar");
			getContentResolver().delete(uri, Lugar._ID+" in "+idsEliminar, null);
		}
		
		configurarListenerCancelarEliminacion();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent();
		i.setClass(getApplicationContext(), LugarActivity.class);
		i.putExtra(Constantes.PARAMETRO_ID_LUGAR, id);
		startActivity(i);
	}
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
			// Abrimos el popUp para pedir confirmación 
			case Constantes.DIALOG_PEDIR_CONFIRMACION_MULTIPLE:
				builder.setMessage(R.string.msg_condirmacion_eliminar_varios);
				builder.setPositiveButton(R.string.si,
										  new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												configurarListenerEliminar();
											}
									  	  });
				builder.setNegativeButton(R.string.no, 
										  new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												configurarListenerCancelarEliminacion();
											}
										  });
				
				return builder.create();
			case Constantes.DIALOG_PEDIR_CONFIRMACION_SIMPLE:
				final long idLugar = args.getLong(Constantes.PARAMETRO_ID_LUGAR);
				builder.setMessage(R.string.msg_condirmacion_eliminar);
				builder.setPositiveButton(R.string.si,
										  new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												//Eliminamos el lugar
												
												Uri uri = Uri.parse(LugaresProvider.CONTENT_URI+"/lugar");
												getContentResolver().delete(uri, Lugar._ID+" = "+idLugar, null);
												
												//Recargamos la pantalla
												configurarAdapter();
											}
									  	  });
				builder.setNegativeButton(R.string.no, 
										  new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												//No hay que hacer nada
											}
										  });
				
				return builder.create();

			default:
				return null;
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		int id = (int)((AdapterContextMenuInfo)menuInfo).id;
		menu.add(id, Constantes.MENU_DETALLES, Menu.NONE, R.string.detalles);
		menu.add(id, Constantes.MENU_EDITAR, Menu.NONE, R.string.editar);
		menu.add(id, Constantes.MENU_VER_UBICACION, Menu.NONE, R.string.ver_ubicacion);
		menu.add(id, Constantes.MENU_NAVEGAR, Menu.NONE, R.string.navegar);
		menu.add(id, Constantes.MENU_ELIMINAR, Menu.NONE, R.string.eliminar);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		Intent i = new Intent();
	    long idLugar = (long)item.getGroupId();
		switch(item.getItemId()) {
	    	// Vemos los detalles del lugar
	        case Constantes.MENU_DETALLES:
	    		i.setClass(getApplicationContext(), LugarActivity.class);
	    		i.putExtra(Constantes.PARAMETRO_ID_LUGAR, idLugar);
	    		startActivity(i);
	            return true;
            // Navegamos a editar el lugar
	        case Constantes.MENU_EDITAR:
	    		i.setClass(getApplicationContext(), EditarLugarActivity.class);
	    		i.putExtra(Constantes.PARAMETRO_ID_LUGAR, idLugar);
	    		startActivity(i);
	        	return true;
	        // Preguntamos si eliminar el lugar
	        case Constantes.MENU_ELIMINAR:
	        	Bundle b = new Bundle();
	        	b.putLong(Constantes.PARAMETRO_ID_LUGAR, idLugar);
	        	showDialog(Constantes.DIALOG_PEDIR_CONFIRMACION_SIMPLE, b);
	        	return true;
        	// Mostramos el lugar en el mapa
	        case Constantes.MENU_VER_UBICACION:
	    		i.setClass(getApplicationContext(), MapaLugaresActivity.class);
	    		i.putExtra(Constantes.PARAMETRO_ID_LUGAR, idLugar); 
	    		startActivity(i);
	        	return true;
	        case Constantes.MENU_NAVEGAR:
				Uri uri = Uri.parse(LugaresProvider.CONTENT_URI+"/lugar");
				uri = ContentUris.withAppendedId(uri, idLugar);
				Cursor cursor = managedQuery(uri, new String[] {Lugar.LATITUD, Lugar.LONGITUD}, null, null, null);
				cursor.setNotificationUri(getContentResolver(), uri);
				startManagingCursor(cursor);

				if(cursor.moveToFirst()){ 
					String location = cursor.getFloat(0)+","+cursor.getFloat(1);
					i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+location));
					startActivity(i);
				}
	        	return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
}
