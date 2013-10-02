import java.util.Collection;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;

//TODO devolver tambien distancia y vias al mostrar
public class infEnlace {

	private long id,nodo1, nodo2;
	private boolean wrongway=false; //etiqueta que muestra si la via es transitable en este sentido.
	private Collection<Tag> informacion;
	public infEnlace(long id,long nodo1,long nodo2,Collection<Tag> informacion){
		this.id=id;
		this.nodo1=nodo1;
		this.nodo2=nodo2;
	}
	public infEnlace(long id,long nodo1,long nodo2,Collection<Tag> informacion,boolean oneway){
		this.id=id;
		this.nodo1=nodo1;
		this.nodo2=nodo2;
		this.wrongway=oneway;
	}
	public long getIdNodoOrigen(){
		return nodo1;
	}
	public long getIdNodoDestino(){
		return nodo2;
	}
}
