package es.jcorralejo.android.maps;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;

public class ItemizedOverlayLugar extends ItemizedOverlay<OverlayItemLugar> {

	private ArrayList<OverlayItemLugar> mOverlays = new ArrayList<OverlayItemLugar>();
	private Context context;
	
	/** Indica si este ItemizedOverlay indica la posición que marca el GPS*/
	private boolean posicionGPS = false;
	/** Indica el lugar que se ha pulsado*/
	private OverlayItemLugar lugarPulsado = null;
	
	public ItemizedOverlayLugar(Context context, Drawable defaultMarker, boolean posicionGPS) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
	}
	
	/**
	 * Añade un nuevo Item con los datos indicados
	 * @param lat
	 * @param lon
	 * @param etiqueta
	 * @param descripcion
	 * @param idLugar
	 */
	public void add(double lat, double lon, String etiqueta, String descripcion, long idLugar) {
		int lt = (int) (lat * 1E6);
		int ln = (int) (lon * 1E6);
		GeoPoint punto = new GeoPoint(lt, ln);
		OverlayItemLugar item = new OverlayItemLugar(punto, etiqueta, descripcion, idLugar);
		mOverlays.add(item);
		populate();
	}
	
	public void clear() {
		mOverlays.clear();
		populate();
	}
	
	@Override
	protected OverlayItemLugar createItem(int i) {
		return mOverlays.get(i);
	}
	
	@Override
	public int size() {
		return mOverlays.size();
	}
	
	@Override
	protected boolean onTap(int index) {
		boolean result = super.onTap(index);
		//En el caso de la posición el GPS no tiene sentido controlar las pulsaciones
		lugarPulsado = posicionGPS ? null : mOverlays.get(index);
		return result;
	}
	
	public OverlayItemLugar getLugarPulsado() {
		return lugarPulsado;
	}

	public void setLugarPulsado(OverlayItemLugar lugarPulsado) {
		this.lugarPulsado = lugarPulsado;
	}

}

