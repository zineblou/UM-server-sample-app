package nirvanaExample;

import com.pcbsys.nirvana.client.*;

public class DataGroupClient implements nDataStreamListener {

    nSession mySession;

    public DataGroupClient( String realmURLs) throws nSessionNotConnectedException, nSecurityException, nSessionAlreadyInitialisedException, nRealmUnreachableException, nIllegalArgumentException {
        nSessionAttributes nsa = new nSessionAttributes(realmURLs);
        mySession = nSessionFactory.create(nsa);
        mySession.init(this);
    }

    ////
    // nDataStreamListener Implementation
    ////
    //Callback received when event is available
    public void onMessage(nConsumeEvent event){
        byte[] data = event.getEventData();
        System.out.println("The event data: "+data);
        String message = new String(data);
        System.out.println("The received message "+message);
    }
}