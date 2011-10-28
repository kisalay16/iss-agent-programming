/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Agent;

import GUI.TravelAgentGUI;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import message.msgFlightAvailability_Result;
import message.msgFlightAvailability_Result_List;
import message.msgReqFlightAvailability;


/**
 *
 * @author henry
 */
public class FlightAgent extends Agent{
    //refer to http://www.iro.umontreal.ca/~dift6802/jade/src/examples/bookTrading/BookBuyerAgent.java
    
    private TravelAgentGUI travelGUI;
    private DFAgentDescription dfd;
    private ServiceDescription sd;
    
    private AID[] flightAgents; //all known flight agents available
    private msgFlightAvailability_Result_List flightAvaList = new msgFlightAvailability_Result_List(); //to keep track of all the available flight list
    
    private msgReqFlightAvailability msgRefFlightAva = new msgReqFlightAvailability();
    
    private Vector requestIDList = new Vector();
    private Integer requestRunningNo = 0;
    
    protected void setup() {
          
          //set up the flightAva List
          msgFlightAvailability_Result a = new msgFlightAvailability_Result("SIA", "SIA001", new Date(2011, 11, 1, 11, 20), new Date(2011, 11, 2, 8, 30), 2000.00, "Singapore", "London", false);
          msgFlightAvailability_Result b = new msgFlightAvailability_Result("SIA", "SIA002", new Date(2011, 11, 10, 11, 20), new Date(2011, 11, 11, 8, 30), 1500.00, "London", "Singapore", false);
          flightAvaList.addFlight(a);
          flightAvaList.addFlight(b);
          
          
          /** Registration with the DF */
          DFAgentDescription dfd = new DFAgentDescription();    
          ServiceDescription sd = new ServiceDescription();
          sd.setType("ObjectReaderAgent"); 
          sd.setName(getName());
          sd.setOwnership("ExampleOfJADE");
          dfd.addServices(sd);
          dfd.setName(getAID());
          try {
            DFService.register(this,dfd);
          } catch (FIPAException e) {
            System.err.println(getLocalName()+" registration with DF unsucceeded. Reason: "+e.getMessage());
            doDelete();
          }
          /** End registration with the DF **/
          System.out.println(getLocalName()+ " succeeded in registration with DF");


        // Add the behaviour serving queries from buyer agents
        addBehaviour(new OfferFlightRequestsServer());
        
        // Add the behaviour serving queries from buyer agents
        addBehaviour(new BookFlightRequestsServer());
    }
    
    // Put agent clean-up operations here
    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Printout a dismissal message
        System.out.println("flight-agent "+getAID().getName()+" terminating.");
    }
    
    private String generateRequestID(){
        Integer temp = requestRunningNo + 1;
        return "Request" + temp.toString();
    }
    
    public void setMsgFlightAva(msgReqFlightAvailability input){
        msgRefFlightAva = new msgReqFlightAvailability(input);
    }
    
    /**
        Inner class OfferFlightRequestsServer.
        This is the behaviour used by flightAgent to serve incoming requests
        for offer from buyer agents.
        If the requested book is in the local catalogue the seller agent replies
        with a PROPOSE message specifying the price. Otherwise a REFUSE message is
        sent back.
        */
    private class OfferFlightRequestsServer extends OneShotBehaviour {
        private int step = 0;
        
        public void action() {
            try {
                System.out.println(getLocalName()+" is waiting for a booking");
                ACLMessage msg = blockingReceive(); 
                System.out.println(getLocalName()+ " rx msg"+msg); 
      
                if(msg.getPerformative() == ACLMessage.CFP){
                    
                      if ("JavaSerialization".equals(msg.getLanguage())) {
                          msgReqFlightAvailability request = (msgReqFlightAvailability)msg.getContentObject();
                          
                          msgFlightAvailability_Result_List flightRequestResult = new msgFlightAvailability_Result_List();
                          
                          try{
                              flightRequestResult = flightAvaList.getFlightsAccordingToSpecs(request);
                              flightRequestResult.setProposalID(generateRequestID());
                          }
                          catch(Exception ex){

                          }
                          
                          //if(flightRequestResult.)
                          if(flightRequestResult.getSize() > 0){
                              try{
                                  ACLMessage reply = new ACLMessage(ACLMessage.PROPOSE);

                                  reply.addReceiver(msg.getSender());

                                  reply.setContentObject(flightRequestResult);
                                  reply.setLanguage("JavaSerialization");
                                  send(reply);
                                  System.out.println(getLocalName()+" sent 1st msg "+msg);

                                  reply.setDefaultEnvelope();
                                  reply.getEnvelope().setAclRepresentation(FIPANames.ACLCodec.BITEFFICIENT);
                                  send(reply);
                                  System.out.println(getLocalName()+" sent 1st msg with bit-efficient aclCodec "+ reply);

                                  reply.getEnvelope().setAclRepresentation(FIPANames.ACLCodec.XML); 
                                  send(reply);
                                  System.out.println(getLocalName()+" sent 1st msg with xml aclCodec "+ reply);
                              }
                              catch(Exception ex){
                                  JOptionPane.showMessageDialog(null, ex.getMessage());
                              }
                          }

                      } else
                          System.out.println(getLocalName()+ " read Java String " + msg.getContent()); 
                }
            } catch(UnreadableException e3){
                  System.err.println(getLocalName()+ " catched exception "+e3.getMessage());
            }
        }
    }
    
    private class BookFlightRequestsServer extends CyclicBehaviour {
        private String sFlightNo;
        private AID targetAgentAID;
        private Integer step = 0;
        private MessageTemplate mt;
        private ArrayList<String> processedRequests = new ArrayList<String>();; //list to keep track of all the processedID
        private Boolean toProceed;
        
        public void action() {
            System.out.println(getLocalName()+" is waiting for a booking");
            ACLMessage msg = blockingReceive(); 
            System.out.println(getLocalName()+ " rx msg"+msg); 

            if(msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL){
                String requestID = msg.getContent();
                toProceed = true;
                
                try{
                    for (String s : processedRequests) {
                        //if previously processed, just inform
                        if(s.compareTo(requestID) == 0){
                            ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
                            reply.addReceiver(msg.getSender());
                            reply.setLanguage("English");
                            reply.setContent("Request already processed");
                            send(reply);
                            toProceed = false;
                            break;
                        }
                    }
                }
                catch(Exception ex){
                    
                }
                        
                if(toProceed == true){
                    //to simulate the searching
                    String flightNo = new String();
                    if(requestID.compareTo("Request1") == 0){
                        flightNo = "SIA001";
                    }
                    if(requestID.compareTo("Request2") == 0){
                        flightNo = "SIA002";
                    }
                    ACLMessage bookingResult = msg.createReply();
                    if(flightNo != null){
                        if(flightAvaList.bookFlight(flightNo) == false){
                            bookingResult.setPerformative(ACLMessage.REFUSE);
                        }
                        else{
                            bookingResult.setPerformative(ACLMessage.CONFIRM);
                        }
                    }
                    processedRequests.add(requestID);
                    send(bookingResult);
                }
            }
            
                    
        }
    }  // End of inner class OfferRequestsServer
}
