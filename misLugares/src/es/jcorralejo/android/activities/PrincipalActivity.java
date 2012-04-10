package es.jcorralejo.android.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import es.jcorralejo.android.R;
import es.jcorralejo.android.utils.Constantes;

public class PrincipalActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.principal);
		
		Button botonLista = (Button) findViewById(R.id.botonLista);
		Button botonMapa = (Button) findViewById(R.id.botonMapa);
		
		botonLista.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					navegarListaLugaresActivity();
				}
			}
		);
		botonMapa.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					navegarMapaLugaresActivity();
				}
			}
		);
	}
	
	/**
	 * Abre un {@link Intent} para navegar a la {@link Activity} de {@link ListaLugaresActivity}
	 */
	private void navegarListaLugaresActivity(){
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), ListaLugaresActivity.class);
		startActivity(intent);
	}
	
	private void navegarMapaLugaresActivity(){
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), MapaLugaresActivity.class);
		intent.putExtra(Constantes.PARAMETRO_ID_LUGAR, Constantes.TODOS_LUGARES); 
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_principal, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			// Al pulsar sobre "Lista" navegamos a la actividad ListaLugaresActivity
			case R.id.menuLista:
				navegarListaLugaresActivity();
				return true;
			// Al pulsar sobre "Mapa" navegamos a la actividad MapaLugaresActivity
			case R.id.menuMapa:
				navegarMapaLugaresActivity();
				return true;
			// Al pulsar sobre "Configuración" navegamos a la actividad ConfiguracionActivity
			case R.id.menuConfiguracion:
				Toast.makeText(this, R.string.opcion_configuracion, Toast.LENGTH_LONG).show();
				return true;
			// Al pulsar sobre "Acerca de" levantamos el popUp con la información sobre la aplicación
			case R.id.menuAcercaDe:
				showDialog(Constantes.DIALOG_ACERCA_DE);
				return true;
			// Al pulsar sobre "Salir" salimos de la aplicación
			case R.id.menuQuit:
				finish();
				return true;
			//Acción por defecto
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			// Abrimos el popUp "Acerca de..." 
			case Constantes.DIALOG_ACERCA_DE:
				LayoutInflater li = LayoutInflater.from(this);
				View view = li.inflate(R.layout.acercade, null);
				final AlertDialog.Builder builder = new AlertDialog.Builder(this).setIcon(R.drawable.icon)
																			     .setTitle(getString(R.string.app_name))
																			     .setPositiveButton("Ok", null)
																			     .setView(view);
				return builder.create();
			default:
				return null;
		}
	}

}
