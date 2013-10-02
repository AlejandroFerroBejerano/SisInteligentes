
public class nodoBusqueda {
	private infNodo estado;
	private infEnlace movimiento;
	private double costo;
	int profundidad;
	private nodoBusqueda padre;
	
	public nodoBusqueda(infNodo estado,infEnlace movimiento,double costo,int profundidad,nodoBusqueda padre){
		this.estado=estado;
		this.movimiento=movimiento;
		this.costo=costo;
		this.profundidad=profundidad;
		this.padre=padre;
	}

}
