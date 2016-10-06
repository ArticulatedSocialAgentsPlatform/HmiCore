package nl.utwente.hmi.middleware.worker;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.fasterxml.jackson.databind.JsonNode;


/**
 * A simple abstract implementation of a worker thread. This thread has an internal queue which is filled with Data by a middleware listener. 
 * Each Data is then processed by the processData method, which can be implemented by subclasses to fit their specific needs
 * @author davisond
 *
 */
public abstract class AbstractWorker implements Worker {

	protected BlockingQueue<JsonNode> queue = null;

	private boolean running = true;
	
	/**
	 * Create a new AbstractWorker object. This provides the basics required for recieving data into the processing queue
	 */
	public AbstractWorker(){
		this.queue = new LinkedBlockingQueue<JsonNode>();
	}
	
	/**
	 * Adds a certain data to the processing queue of this worker
	 */
	@Override
	public void addDataToQueue(JsonNode jn) {
		queue.add(jn);
	}

	/**
	 * This method is constantly reading the processing queue. 
	 * If a new data comes in, it calls the processData() method, which can do the actual processing as required for the specific worker.
	 */
	@Override
	public void run() {
		while(running){
			try {
				JsonNode jn = queue.take();
				processData(jn);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Does the required processing on the data 
	 * Each worker is responsible for implementing this method as required.
	 * @param d the incoming data
	 */
	abstract public void processData(JsonNode jn);

}
