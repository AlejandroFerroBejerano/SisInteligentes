// Instalar osmosis e incorporar sus paquetes JAR

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
					//y sus datos basicos ( infnodo y array de ways)
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
					// estructura a añadir: lista de infEnlaces
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
						// 3º crear objeto
						// 4º obtener lista y añadirlo a la lista
						enlaces = map.get(idnodo1).getVias();
						enlaces.add(new infEnlace(entity.getId(), idnodo1,
								idnodo2, entity.getTags()));
						// 5º marcamos el reciproco, indicando si es transitable ( tag oneway )
						enlaces = map.get(idnodo2).getVias();
						enlaces.add(new infEnlace(entity.getId(), idnodo2,
								idnodo1, entity.getTags(), oneway));
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
				//TODO solucionar duplicacion de codigo!!!!
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

	private static void showNode(Node nodo) {
		System.out.println(nodo.getId() + "\t" + nodo.getLatitude() + "\t"
				+ nodo.getLongitude());
	}

	private static void showWay(Way via) {
		Iterator<Tag> tags = via.getTags().iterator();
		Iterator<WayNode> nodos = via.getWayNodes().iterator();
		while (tags.hasNext())
			System.out.print(tags.next().getValue() + "\t");
		System.out.println("");
		// nodos
		while (nodos.hasNext()) {
			System.out.println("\t" + nodos.next().getNodeId());
		}
	}
	
	private static ArrayList<infEnlace> getAdjacents(long idNodo) {
		Iterator<infEnlace> enlaces;
		infEnlace enlaceActual;
		ArrayList<infEnlace> nodosAdyacentes = new ArrayList<>();
		try {
			enlaces = map.get(idNodo).getVias().iterator();
			while (enlaces.hasNext()) {
				enlaceActual = enlaces.next();
				nodosAdyacentes.add(enlaceActual);
			}
		} catch (Exception e) {
		}
		return nodosAdyacentes;
	}

	private static void menu() {
		long idNodo = 0;
		Iterator<infEnlace> enlaces;
		infEnlace enlace;
		Scanner leer = new Scanner(System.in);
		
		System.out
				.println("-----------------------------------------------------------------------");
		System.out
				.println("-- Bienvenido , introduzca en numero de nodo para ver sus adyacentes --");
		System.out
				.println("-----------------------------------------------------------------------");

		System.out
				.println("Una vez introducido el Id del nodo, presione ENTER...");
		// comprobacion de id correcto
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
		System.out.println("\nIDNodo:" + idNodo + "\nAdyacentes:");
		enlaces = getAdjacents(idNodo).iterator();
		if (!enlaces.hasNext()) {
			System.out.println("el nodo no tiene adyacentes o no existe.");
		}else
			while (enlaces.hasNext()){
				enlace=enlaces.next();
				System.out.println("\t" + enlace.getIdNodoDestino() +" "+enlace.getName()+" "+
				distanciaEuclidea(map.get(enlace.getIdNodoOrigen()).getInfNodo().getLon(),
					map.get(enlace.getIdNodoOrigen()).getInfNodo().getLat(),
					map.get(enlace.getIdNodoDestino()).getInfNodo().getLon(),
					map.get(enlace.getIdNodoDestino()).getInfNodo().getLat())+"m"
				);
			}

	}
	//ecuación de Harvesine para la distanca entre dos puntos geograficos dados en coordenadas decimales.
	private static double distanciaEuclidea(double Lo1,double La1,double Lo2,double La2){
		double Distancia1 = 0;
        double r = 6378;

        //Punto inicial
        La1 = La1* Math.PI / 180;
        Lo1 = Lo1 * Math.PI / 180;

        // Punto final
        La2 = La2 * Math.PI / 180;
        Lo2 = Lo2 * Math.PI / 180;        
        Distancia1 = Math.pow(Math.sin((Lo2 - Lo1) / 2), 2);
        Distancia1 = Math.cos(La1) * Math.cos(La2) * Distancia1;
        Distancia1 = Math.pow(Math.sin((La2 - La1) / 2), 2) + Distancia1;
        Distancia1 = Math.sqrt(Distancia1);
        Distancia1 = 2 * r * Math.asin(Distancia1);
        Distancia1*=1000; // multiplicamos para obtener metros
        return Distancia1;
	}
}
