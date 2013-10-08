
public class infNodo {
	private long id;
	private double lat, lon;

	public infNodo(long id,double lat,double lon){
		this.id=id;
		this.lat=lat;
		this.lon=lon;
	}
	public double getLat(){
		return lat;
	}
	public double getLon(){
		return lon;
	}
	public long getId(){
		return id;
	}
}
