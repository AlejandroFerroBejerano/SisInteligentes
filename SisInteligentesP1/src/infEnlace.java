import java.util.Collection;
import java.util.Iterator;

import org.openstreetmap.osmosis.core.domain.v0_6.Tag;


public class infEnlace{

	private long id;
	private infNodo nodo1, nodo2;
	private boolean wrongway=false; //etiqueta que muestra si la via es transitable en este sentido.
	private Collection<Tag> informacion;
	public infEnlace(long id,infNodo nodo1,infNodo nodo2,Collection<Tag> informacion){
		this.id=id;
		this.nodo1=nodo1;
		this.nodo2=nodo2;
		this.informacion=informacion;
	}
	public infEnlace(long id,infNodo nodo1,infNodo nodo2,Collection<Tag> informacion,boolean oneway){
		this.id=id;
		this.nodo1=nodo1;
		this.nodo2=nodo2;
		this.informacion=informacion;
		this.wrongway=oneway;
	}
	public infNodo getIdNodoOrigen(){
		return nodo1;
	}
	public infNodo getIdNodoDestino(){
		return nodo2;
	}
	public String getName() {
		Tag tag;
		Iterator <Tag> camposInfo;
		camposInfo=informacion.iterator();
		do{
			tag=camposInfo.next();
		}while(!tag.getKey().equals("name")||!camposInfo.hasNext());
		return tag.getValue();
	}

	public double getDistancia() {
		double Lo1, La1, Lo2, La2, Distancia1 = 0,r = 6378;
	        //Punto inicial
        La1 = nodo1.getLat()* Math.PI / 180;
        Lo1 = nodo1.getLon()* Math.PI / 180;
	        // Punto final
        La2 = nodo2.getLat() * Math.PI / 180;
        Lo2 = nodo2.getLon() * Math.PI / 180;        
        Distancia1 = Math.pow(Math.sin((Lo2 - Lo1) / 2), 2);
        Distancia1 = Math.cos(La1) * Math.cos(La2) * Distancia1;
        Distancia1 = Math.pow(Math.sin((La2 - La1) / 2), 2) + Distancia1;
        Distancia1 = Math.sqrt(Distancia1);
        Distancia1 = 2 * r * Math.asin(Distancia1);
        Distancia1*=1000; // multiplicamos para obtener metros
        return Distancia1;
	}

}
