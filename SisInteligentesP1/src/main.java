// Instalar osmosis e incorporar sus paquetes JAR

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
	
	private static Map<Long,Entity> map = new HashMap <>();
	static public void main(String[] args) {
		
		//File file = new File("data/map.osm");// Para trabajar offile
		Sink sinkImplementation = new Sink() {
			public void initialize(Map<String, Object> metaData) {
			};

			public void process(EntityContainer entityContainer) {
				Entity entity = entityContainer.getEntity();
				if (entity instanceof Node) {
					map.put(entity.getId(), entity);
				}else if (entity instanceof Way) {
//TODO;
					
				}
			}
			
			public void release() {
			}

			public void complete() {
			}

		};
		CompressionMethod compression = CompressionMethod.None;
		//RunnableSource reader = new XmlReader(file, false, compression);
		// Para usar el fichero XML descargado.

		// Para acceder directamente desde la BD de OSM online
		// Utilizar XmlDownloader
		System.out.println("Cargando");		
		//cojemos de argumentos las coordenadas
		RunnableSource reader = new XmlDownloader(Double.parseDouble(args[0]),Double.parseDouble(args[1]),
				Double.parseDouble(args[2]),Double.parseDouble(args[3]), null);
		
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
		
		System.out.println("Fin :)");

	}
	
	private static void showNode(Node nodo){
		System.out.println(nodo.getId()+"\t"+nodo.getLatitude()+"\t"+nodo.getLongitude());
	}
	private static void showWay(Way via){
		Iterator <Tag>tags=via.getTags().iterator();
		Iterator<WayNode>nodos=via.getWayNodes().iterator();
		while(tags.hasNext())
			System.out.print(tags.next().getValue()+"\t");
		System.out.println("");
		//nodos
		while(nodos.hasNext()){
			System.out.println("\t"+nodos.next().getNodeId());
		}
		
		
	}
}
