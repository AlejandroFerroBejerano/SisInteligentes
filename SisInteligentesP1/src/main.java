// Instalar osmosis e incorporar sus paquetes JAR

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.RunnableSource;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlDownloader;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;

public class main {

	private static Map<Long, payloadMap> map = new HashMap<>();

	public static void main(String[] args) {
		double coordenadaIzq, coordenadaDer, coordenadaSup, coordenadaInf;
		File file = new File("data/map.osm");// Para trabajar offile
		Sink sinkImplementation = new Sink() {
			public void initialize(Map<String, Object> metaData) {
			};

			public void process(EntityContainer entityContainer) {
				Entity entity = entityContainer.getEntity();
				if (entity instanceof Node) {
					// setup de la key del hashmap e inclusion de la estructura contenedor payload
					map.put(entity.getId(),
							new payloadMap(new infNodo(entity.getId(),
									((Node) entity).getLatitude(),
									((Node) entity).getLongitude()),
									new ArrayList<infEnlace>()));
				} else if (entity instanceof Way) {
					boolean oneway = false;
					Iterator<Tag> tags = entity.getTags().iterator();
					Iterator<WayNode> nodos = ((Way) entity).getWayNodes()
							.iterator();
					ArrayList<infEnlace> enlaces;
					long idnodo1, idnodo2;
					Tag tag;
					// unimos nodos contiguos unos con otros
					// 1º ver si es oneway
					while (tags.hasNext()) {
						tag = tags.next();
						if (tag.getKey().equals("oneway")) {
							oneway = tag.getValue().equalsIgnoreCase("yes");
							break;
						}
					}
					// 2º obtener nodo origen y nodo destino (solo da la id)
					idnodo1 = nodos.next().getNodeId();
					while (nodos.hasNext()) { // se repite esta tarea con los
												// sucesivos nodos
						idnodo2 = nodos.next().getNodeId();
						// 3º crear objeto 4º obtener lista y añadirlo a la lista
						enlaces = map.get(idnodo1).getVias();
						enlaces.add(new infEnlace(entity.getId(), map.get(idnodo1).getInfNodo(),
								map.get(idnodo2).getInfNodo(), entity.getTags()));
						// 5º marcamos el reciproco, indicando si es transitable ( tag oneway )
						enlaces = map.get(idnodo2).getVias();
						enlaces.add(new infEnlace(entity.getId(), map.get(idnodo2).getInfNodo(),
								map.get(idnodo1).getInfNodo(), entity.getTags(), oneway));
						// 6º el nodo origen ahora es destino, swap!
						idnodo1 = idnodo2;
					}
				}
			}

			public void release() {
			}

			public void complete() {
			}

		};
		RunnableSource reader;
		// comprobando coordenadas
		if (args.length != 4) {
			System.out
					.println("Error en las coordenadas del mapa, se usaran coordenadas por defecto.");
			CompressionMethod compression = CompressionMethod.None;
			reader = new XmlReader(file, false, compression);
		} else {
			try {
				// ordenamos 1º con 3º y 2º con 4º
				coordenadaIzq = Math.min(Double.parseDouble(args[0]),
						Double.parseDouble(args[2]));
				coordenadaDer = Math.max(Double.parseDouble(args[0]),
						Double.parseDouble(args[2]));
				coordenadaSup = Math.max(Double.parseDouble(args[1]),
						Double.parseDouble(args[3]));
				coordenadaInf = Math.min(Double.parseDouble(args[1]),
						Double.parseDouble(args[3]));
				// Para usar el fichero XML descargado. cojemos de argumentos
				// las coordenadas -
				reader = new XmlDownloader(coordenadaIzq, coordenadaDer,
						coordenadaSup, coordenadaInf, null);
			} catch (Exception e) {
				System.out
						.println("Error descargando el mapa, se usaran el mapa por defecto.");
				CompressionMethod compression = CompressionMethod.None;
				reader = new XmlReader(file, false, compression);
			}
		}
		System.out.println("Cargando");
		reader.setSink(sinkImplementation);

		Thread readerThread = new Thread(reader);
		readerThread.start();
		System.out.println("Comenzamos");
		while (readerThread.isAlive()) {
			try {
				readerThread.join();
			} catch (InterruptedException e) {
				/* No hacer nada */
			}
		}
		menu();
		System.out.println("Fin :)");

	}

	private static void showNode(infNodo nodo) {
		System.out.println(nodo.getId() + "\t" + nodo.getLat()+ "\t"
				+ nodo.getLon());
	}

	private static void showWay(infEnlace via) {
		Iterator<Tag> tags = via.getTags().iterator();
		while (tags.hasNext())
			System.out.print(tags.next().getValue() + "\t");
		System.out.println("");
		// nodos
		System.out.println("nodo origen:"+via.getIdNodoOrigen()+" nodo destino:"+via.getIdNodoDestino());

	}
	
	private static ArrayList<infEnlace> getAdjacents(infNodo idNodo) {
		Iterator<infEnlace> enlaces;
		infEnlace enlaceActual;
		ArrayList<infEnlace> nodosAdyacentes = new ArrayList<>();
		try {
			enlaces = map.get(idNodo.getId()).getVias().iterator();
			while (enlaces.hasNext()) {
				enlaceActual = enlaces.next();
				nodosAdyacentes.add(enlaceActual);
			}
		} catch (Exception e) {
		}
		return nodosAdyacentes;
	}
	
	private static PriorityQueue<nodoBusqueda> sucesores(nodoBusqueda nodoorigen){
		Iterator<infEnlace> viasSucesores;
		infEnlace via;
		PriorityQueue<nodoBusqueda> frontera= new PriorityQueue<>();
		viasSucesores=getAdjacents(nodoorigen.getNodo()).iterator();
		while(viasSucesores.hasNext()){
			via = viasSucesores.next();
			// para no añadir un nodo como sucesor que ya hayamos pasado por el ( es decir el padre) o si es el primer caso.
			// si estamos en la raiz ( todos) o si no estamos cogiendo el nodo padre anterior como sucesor
			if(nodoorigen.getPadre()==null||!via.getIdNodoOrigen().equals(nodoorigen.getPadre().getNodo()))
				frontera.add(new nodoBusqueda(via.getIdNodoDestino(),via,via.getDistancia(),nodoorigen.getProfundidad()+1,nodoorigen));
		}
		return frontera;
	}

	private static boolean esFinal(infNodo origen,infNodo fin){
		boolean esFinal=origen.equals(fin);
		return esFinal;
	}
	
	private static ArrayList<nodoBusqueda> stressFrontera(long id){
		ArrayList<nodoBusqueda> adyacentes = new ArrayList<>();
		double tiempo=System.currentTimeMillis();
		try{
			while(true){
				adyacentes.add(sucesores(new nodoBusqueda(map.get(id).getInfNodo(),null,0,0,null)).iterator().next());
				id=adyacentes.get(adyacentes.size()-1).getNodo().getId();
				if(adyacentes.size()%1000000==0){
					//test running
					System.out.println(".::Elemento "+adyacentes.size()+" añadido::.");
					System.out.println(".::Tiempo/nodo "+ (System.currentTimeMillis()-tiempo)/adyacentes.size() +" ms::.");
				}
			}
		}catch (OutOfMemoryError e){
			System.out.println(".::Numero maximo elementos: "+adyacentes.size()+" añadidos::.");
			System.out.println(".::Tiempo/nodo: "+ (System.currentTimeMillis()-tiempo)/adyacentes.size() +" ms::.");
			System.out.println(".::Tiempo total: "+(System.currentTimeMillis()-tiempo)+" ms::.");
		} 
		return adyacentes;
	}

	private static void menu() {
		long idNodo = 0;
		ArrayList<nodoBusqueda> enlaces;
		long enlace;
		Scanner leer = new Scanner(System.in);
		
		System.out
				.println("-----------------------------------------------------------------------");
		System.out
				.println("-- Bienvenido , introduzca en numero de nodo para ver sus sucesores --");
		System.out
				.println("-----------------------------------------------------------------------");

		System.out
				.println("Una vez introducido el Id del nodo, presione ENTER...");
		// comprobacion de id correcto
		while (idNodo == 0) {
			try {
				idNodo = Long.parseLong(leer.next());
			} catch (Exception e) {
				leer.reset();// borramos buffer
				System.out
						.println("Id del nodo mal introducido, recuerde que el id solo se compone de numeros.");
			}
		}
		
		enlaces = stressFrontera(idNodo);
		System.out.println("IDNodo:" + idNodo + "\nIntroduzca nodo final:");
		//pedir final
		idNodo=0;
		while (idNodo == 0) {
			try {
				idNodo = Long.parseLong(leer.next());
				leer.close();
			} catch (Exception e) {
				leer.reset();// borramos buffer
				System.out
						.println("Id del nodo mal introducido, recuerde que el id solo se compone de numeros.");
			}
		}
		
		if (enlaces.size()==0) {
			System.out.println("el nodo no tiene adyacentes o no existe.");
		}
		/*else
			while (enlaces.hasNext()){
				enlace=enlaces.next();
				System.out.print("\t" + enlace.getNodo().getId() +" "+enlace.getMovimiento().getName()+" "+
				enlace.getCoste()+"m Es final?:"
				);
				if(esFinal(enlace.getNodo(),map.get(idNodo).getInfNodo()))
					System.out.println("si");
				else
					System.out.println("no");		
			}
		 */
	}
}
