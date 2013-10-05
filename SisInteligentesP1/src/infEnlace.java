import java.util.Collection;
import java.util.Iterator;

import org.openstreetmap.osmosis.core.domain.v0_6.Tag;


public class infEnlace {

	private long id,nodo1, nodo2;
	private boolean wrongway=false; //etiqueta que muestra si la via es transitable en este sentido.
	private Collection<Tag> informacion;
	public infEnlace(long id,long nodo1,long nodo2,Collection<Tag> informacion){
		this.id=id;
		this.nodo1=nodo1;
		this.nodo2=nodo2;
		this.informacion=informacion;
	}
	public infEnlace(long id,long nodo1,long nodo2,Collection<Tag> informacion,boolean oneway){
		this.id=id;
		this.nodo1=nodo1;
		this.nodo2=nodo2;
		this.informacion=informacion;
		this.wrongway=oneway;
	}
	public long getIdNodoOrigen(){
		return nodo1;
	}
	public long getIdNodoDestino(){
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
}
