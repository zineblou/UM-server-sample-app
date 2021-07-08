package SamplePublisherSubscriber;

import com.pcbsys.nirvana.client.*;
public class Consumer implements nEventListener {

    nQueue myQueue = null;

    public Consumer() throws Exception {
        // construct the session
        nSessionAttributes nsa = new nSessionAttributes("nsp://127.0.0.1:9003");
        nSession mySession = nSessionFactory.create(nsa);
        mySession.init();

        // begin consuming events from the queue
        nChannelAttributes cattrib = new nChannelAttributes();
        cattrib.setName("Queue");
        nQueue myQueue=mySession.findQueue(cattrib);
        nQueueReaderContext ctx = new nQueueReaderContext(this, 10);
        nQueueAsyncReader reader = myQueue.createAsyncReader(ctx);
        String readerId = reader.getReaderId();
        System.out.println("Reader id "+readerId);
    }

    public void go(nConsumeEvent event) {
        System.out.println("Consumed event "+event.getEventID());
        String message = new String(event.getEventData());
        System.out.println("Popped message: "+message);
    }

    public static void main(String[] args) {
        try {
            new Consumer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}