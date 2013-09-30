// Instalar osmosis e incorporar sus paquetes JAR

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.task.v0_6.RunnableSource;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlDownloader;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;

public class main {
	
	private static ArrayList<Node> nodos = new ArrayList<Node>();
	private static ArrayList<Way> vias = new ArrayList<Way>();
	
	static public void main(String[] args) {
		//File file = new File("data/map.osm");// Para trabajar offile
		Sink sinkImplementation = new Sink() {
			public void initialize(Map<String, Object> metaData) {
			};

			public void process(EntityContainer entityContainer) {
				Entity entity = entityContainer.getEntity();
				if (entity instanceof Node) {
//TODO
					nodos.add(new Node(entity.getId(), entity.getVersion(), entity.getTimestamp(), entity.getUser(),entity.getChangesetId(), ((Node) entity).getLatitude(), ((Node) entity).getLongitude()));
				} else if (entity instanceof Way) {
//TODO
					vias.add(new Way(entity.getId(), entity.getVersion(), entity.getTimestamp(), entity.getUser(), entity.getChangesetId()));
				//buscar nodos y añadirlos
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
		//TODO
		RunnableSource reader = new XmlDownloader(-3.9426,-3.9101,38.9978,38.9685, null);
		
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
		
		System.out.println("Fin de carga:");
		System.out.print(nodos.size());
		System.out.println(" nodos");
		System.out.print(vias.size());
		System.out.println(" vias");
	}
}
