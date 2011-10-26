/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Agent;

import GUI.TravelAgentGUI;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
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
public class TravelAgent extends Agent{
    //refer to http://www.iro.umontreal.ca/~dift6802/jade/src/examples/bookTrading/BookBuyerAgent.java
    //setup -gui ASA:Agent.TravelAgent SIA:Agent.FlightAgent
    
    private TravelAgentGUI travelGUI;
    private DFAgentDescription dfd;
    private ServiceDescription sd;
    
    private msgReqFlightAvailability flight;
    
    // The list of known flight agents
    private Vector flightAgentList = new Vector();
    
    private msgReqFlightAvailability msgRefFlightAva = new msgReqFlightAvailability();
    
    protected void setup() {
        // Printout a welcome message
        System.out.println("Travel-Agent "+ getAID().getName()+" is ready.");
     
        this.flight = new msgReqFlightAvailability();
        
        // Create and show the GUI 
        travelGUI = new TravelAgentGUI(this);
        travelGUI.showGUI();
        
        //add new behaviour
        addBehaviour(new FlightNegotiator());
    }
    
    protected void takeDown() {
        // Dispose the GUI if it is there
        if (travelGUI != null) {
            travelGUI.dispose();
        }

        // Printout a dismissal message
        System.out.println("Buyer-agent "+ getAID().getName()+ "terminated.");
    }
    
    
    public void getFlightDetails(msgReqFlightAvailability input){
        this.flight = new msgReqFlightAvailability(input);
    }
    
    private class FlightNegotiator extends Behaviour {
        private String title;
        private int maxPrice;
        private AID bestSeller; // The seller agent who provides the best offer
        private int bestPrice; // The best offered price
        private int repliesCnt = 0; // The counter of replies from seller agents
        private MessageTemplate mt; // The template to receive replies
        private int step = 0;

        public FlightNegotiator(){
            flight = new msgReqFlightAvailability();
        }
        
        public FlightNegotiator(msgReqFlightAvailability input){
            flight = new msgReqFlightAvailability(input);
        }
        
        public void action() {
          switch (step) {
          //done  
          case 0:
              // Send the cfp to all sellers
              ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
              for (int i = 0; i < flightAgentList.size(); ++i) {
                cfp.addReceiver((AID)flightAgentList.elementAt(i));
              }


              try{
                  //please refer to \jade_example\src\examples\Base64
                  cfp.setContentObject(flight);
                  cfp.setLanguage("JavaSerialization");

                  cfp.setDefaultEnvelope();
                  cfp.getEnvelope().setAclRepresentation(FIPANames.ACLCodec.BITEFFICIENT);
                  send(cfp);
                  System.out.println(getLocalName()+" sent 1st msg with bit-efficient aclCodec "+ cfp);

                  cfp.getEnvelope().setAclRepresentation(FIPANames.ACLCodec.XML); 
                  send(cfp);
                  System.out.println(getLocalName()+" sent 1st msg with xml aclCodec "+ cfp);
              }
              catch(IOException ex){
                  travelGUI.notifyUser(ex.getMessage());
                  return;
              }

              cfp.setConversationId("flight-trade");
              cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
              send(cfp);
              travelGUI.notifyUser("Sent Call for Flight Proposal");

              // Prepare the template to get proposals
              mt = MessageTemplate.and(
              MessageTemplate.MatchConversationId("flight-trade"),
              MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
              step = 1;
              break;
            //done
            case 1:
              // Receive all proposals/refusals from seller agents
              ACLMessage reply = myAgent.receive(mt);
              //make sure there is an reply
              if (reply != null) {
                // Reply received
                if (reply.getPerformative() == ACLMessage.PROPOSE) {
                      try {
                          // list of available flights
                          msgFlightAvailability_Result_List avaFlights = (msgFlightAvailability_Result_List) reply.getContentObject();
                      } catch (UnreadableException ex) {
                         Logger.getLogger(TravelAgent.class.getName()).log(Level.SEVERE, null, ex);
                      }
                      travelGUI.notifyUser("Received available flight listing");
                }
                repliesCnt++;
                if (repliesCnt >= flightAgentList.size()) {
                  // We received all replies
                  step = 2;
                }
              }
              else {
                block();
              }
              break;
            //to do
            case 2:
              if (bestSeller != null && bestPrice <= maxPrice) {
                // Send the purchase order to the seller that provided the best offer
                ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                order.addReceiver(bestSeller);
                order.setContent(title);
                order.setConversationId("book-trade");
                order.setReplyWith("order"+System.currentTimeMillis());
                myAgent.send(order);
                travelGUI.notifyUser("sent Accept Proposal");
                // Prepare the template to get the purchase order reply
                mt = MessageTemplate.and(
                  MessageTemplate.MatchConversationId("book-trade"),
                  MessageTemplate.MatchInReplyTo(order.getReplyWith()));
                step = 3;
              }
              else {
                // If we received no acceptable proposals, terminate
                step = 4;
              }
              break;
            //to do
            case 3:
              
              break;
          } // end of switch
        }

        public boolean done() {
          return step == 4;
        }
      } // End of inner class BookNegotiator
}
