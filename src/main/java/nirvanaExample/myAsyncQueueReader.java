package nirvanaExample;

import com.pcbsys.nirvana.client.*;
public class myAsyncQueueReader implements nEventListener {

    nQueue myQueue = null;

    public myAsyncQueueReader() throws Exception {

        // construct your session and queue objects here
        nSessionAttributes nsa = new nSessionAttributes("nsp://127.0.0.1:9003");
        nSession mySession = nSessionFactory.create(nsa);
        mySession.init();
        // begin consuming events from the queue
        nChannelAttributes cattrib = new nChannelAttributes();
        cattrib.setName("zinebqueue");
        nQueue myQueue=mySession.findQueue(cattrib);
        nQueueReaderContext ctx = new nQueueReaderContext(this, 10);
        nQueueAsyncReader reader = myQueue.createAsyncReader(ctx);

    }

    public void go(nConsumeEvent event) {
        System.out.println("Consumed event "+event.getEventID());
        String message = new String(event.getEventData());
        System.out.println("Message: " +message);
    }

    public static void main(String[] args) {
        try {
            new myAsyncQueueReader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}