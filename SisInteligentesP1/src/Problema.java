import java.util.ArrayList;
abstract class Problema {
	private nodoBusqueda estado_inicial;
	private long idnodo_inicial, idnodo_final;
	
	public Problema(long nodo_inicio, long nodo_final){
		
	}
	public void Asignar_EstadoInicial(nodoBusqueda estado){
		estado_inicial = estado;
	}
	private boolean Estado_Meta(nodoBusqueda estado){
	return false;	
	}
	private ArrayList<nodoBusqueda> Sucesores(nodoBusqueda estado){
	return null;
	
	}
}
