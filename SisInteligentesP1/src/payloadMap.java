import java.util.ArrayList;


public class payloadMap {
	private infNodo nodo;
	private ArrayList<infEnlace> vias;
public payloadMap(infNodo nodo, ArrayList<infEnlace> vias){
	this.nodo=nodo;
	this.vias=vias;
}
public ArrayList<infEnlace> getVias(){
	return vias;
}
public infNodo getInfNodo(){
	return nodo;
}
}
