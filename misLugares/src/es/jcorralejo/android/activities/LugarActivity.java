package es.jcorralejo.android.activities;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import es.jcorralejo.android.R;
import es.jcorralejo.android.bd.LugaresProvider;
import es.jcorralejo.android.bd.LugaresDB.Lugar;
import es.jcorralejo.android.utils.Constantes;

public class LugarActivity extends LugarAbstractActivity {
	
	@Override
	protected int getLayout() {
		return R.layout.lugar;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Button botonEditar = (Button) findViewById(R.id.botonEditar);
		botonEditar.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent();
					i.setClass(getApplicationContext(), EditarLugarActivity.class);
					i.putExtra(Constantes.PARAMETRO_ID_LUGAR, idLugar);
					startActivity(i);
					finish();
				}
			}
		);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_lugar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Al pulsar sobre "Editar" navegamos a la actividad EditarLugarActivity 
			case R.id.lugarEditar:
				Intent i = new Intent();
				i.setClass(getApplicationContext(), EditarLugarActivity.class);
				i.putExtra(Constantes.PARAMETRO_ID_LUGAR, idLugar);
				startActivity(i);
				return true;
				
			// Al pulsar sobre "Eliminar" pedimos confirmación al usuario
			case R.id.lugarEliminar:
				showDialog(Constantes.DIALOG_PEDIR_CONFIRMACION_SIMPLE);
				return true;
				
			// Al pulsar sobre "Ver Ubicación" navegamos a la actividad MapaLugaresActivity para mostrar el lugar en el mapa 
			case R.id.lugarVerUbicacion:
	    		Intent intent = new Intent();
	    		intent.setClass(getApplicationContext(), MapaLugaresActivity.class);
	    		intent.putExtra(Constantes.PARAMETRO_ID_LUGAR, idLugar); 
	    		startActivity(intent);
				return true;
				
			// Al pulsar sobre "Navegar" abrimos Google Navigator para que nos indique el camino desde donde estamos hasta el Lugar 
			case R.id.lugarNavegar:
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
				return super.onOptionsItemSelected(item);
		}
	}
	
}
