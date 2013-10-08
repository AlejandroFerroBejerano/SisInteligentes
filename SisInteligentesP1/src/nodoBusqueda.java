import java.util.Comparator;


public class nodoBusqueda  implements Comparable<nodoBusqueda> {
	private infNodo estado;
	private infEnlace movimiento;
	private double costo;
	private int profundidad;
	private nodoBusqueda padre;
	
	public nodoBusqueda(infNodo estado,infEnlace movimiento,double costo,int profundidad,nodoBusqueda padre){
		this.estado=estado;
		this.movimiento=movimiento;
		this.costo=costo;
		this.profundidad=profundidad;
		this.padre=padre;
	}
	public infNodo getNodo(){
		return estado;
	}
	public nodoBusqueda getPadre(){
		return padre;
	}
	public int getProfundidad() {
		return profundidad;
	}
	public infEnlace getMovimiento(){
		return movimiento;
	}

	@Override
	public int compareTo(nodoBusqueda arg0) {
		if (getMovimiento().getDistancia() < arg0.getMovimiento().getDistancia())
        {
            return -1;
        }else if (getMovimiento().getDistancia() > arg0.getMovimiento().getDistancia())
        {
            return 1;
        }
        return 0;
	}

}
