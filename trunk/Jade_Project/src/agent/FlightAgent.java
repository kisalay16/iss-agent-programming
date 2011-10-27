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
import java.util.Date;
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
        
        public BookFlightRequestsServer(String flightNo, AID agentAID){
            sFlightNo = flightNo;
            targetAgentAID = agentAID;
        }
        
        public void action() {
              switch(step){
                  case 0:   
                      if (sFlightNo != null) {
                        ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                        order.addReceiver(targetAgentAID);
                        order.setContent(sFlightNo);
                        order.setConversationId("flight-trade");
                        order.setReplyWith("order"+System.currentTimeMillis());
                        myAgent.send(order);
                        // Prepare the template to get the purchase order reply
                        mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"), MessageTemplate.MatchInReplyTo(order.getReplyWith()));
                        step = 3;
                        }
                    else {
                        // If we received no acceptable proposals, terminate
                        step = 1;
                    }
                    break;
                    
                  case 1:
                      
                  break;
                    
            }
        }
    }  // End of inner class OfferRequestsServer
}
