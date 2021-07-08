package SamplePublisherSubscriber;

import com.pcbsys.nirvana.client.nDataStreamListener;
import com.pcbsys.nirvana.client.nIllegalArgumentException;
import com.pcbsys.nirvana.client.nSessionAttributes;
import com.pcbsys.nirvana.client.*;


public class Publisher {
    public static void main(String[] args) {
        try {
            nSessionAttributes nsa = new nSessionAttributes("nsp://LAPTOP-6H70OF8U:9003");
            nSession mySession = nSessionFactory.create(nsa);
            mySession.init();
            /**
             * Create a channel and a queue

             nChannelAttributes cattrib = new nChannelAttributes();
             cattrib.setChannelMode(nChannelAttributes.QUEUE_MODE);
             cattrib.setMaxEvents(0);
             cattrib.setTTL(0);
             cattrib.setType(nChannelAttributes.PERSISTENT_TYPE);
             cattrib.setName("testQueue");
             nQueue myQueue=mySession.createQueue(cattrib);
             nQueue myQueue = mySession.createQueue(cattrib);
             **/

            // Getting the queue by name
            nChannelAttributes cattrib = new nChannelAttributes();
            cattrib.setName("Queue");
            nQueue myQueue=mySession.findQueue(cattrib);
            String message = "Hello from zineb";
            System.out.println("Pushed message: "+message);
            myQueue.push(new nConsumeEvent("TAG", message.getBytes()));

        } catch (nRealmUnreachableException|nIllegalArgumentException|nSecurityException|
                nSessionNotConnectedException|nSessionAlreadyInitialisedException|
                nUnknownRemoteRealmException|nSessionPausedException|nUnexpectedResponseException|nRequestTimedOutException e) {
            e.printStackTrace();
        } catch (nMaxBufferSizeExceededException e) {
            e.printStackTrace();
        } catch (nChannelNotFoundException e) {
            e.printStackTrace();
        } catch (com.pcbsys.nirvana.client.nIllegalChannelMode nIllegalChannelMode) {
            nIllegalChannelMode.printStackTrace();
        }
    }
}
