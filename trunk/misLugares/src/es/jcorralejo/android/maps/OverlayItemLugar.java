package es.jcorralejo.android.maps;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class OverlayItemLugar extends OverlayItem{

	private long idLugar;
	
	public OverlayItemLugar(GeoPoint point, String title, String snippet) {
		super(point, title, snippet);
	}
	
	public OverlayItemLugar(GeoPoint point, String title, String snippet, long idLugar) {
		super(point, title, snippet);
		this.idLugar = idLugar;
	}

	public long getIdLugar() {
		return idLugar;
	}

	public void setIdLugar(long idLugar) {
		this.idLugar = idLugar;
	}

}
