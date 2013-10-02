import java.util.Collection;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;


public class infEnlace {

	private long id,nodo1, nodo2;
	Collection<Tag> informacion;
	public infEnlace(long id,long nodo1,long nodo2){
		this.id=id;
		this.nodo1=nodo1;
		this.nodo2=nodo2;
	}
public void	setInfo(Collection<Tag> informacion){
		this.informacion=informacion;
	}
}
