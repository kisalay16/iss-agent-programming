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
    
    // The list of known flight agents
    private Vector flightAgentList = new Vector();
    
    private msgReqFlightAvailability msgRefFlightAva = new msgReqFlightAvailability();
    
    protected void setup() {
        // Printout a welcome message
        System.out.println("Travel-Agent "+ getAID().getName()+" is ready.");
        // Get names of seller agents as arguments
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; ++i) {
                AID seller = new AID((String) args[i], AID.ISLOCALNAME);
                flightAgentList.addElement(seller);
            }
        }
        
        //to show the travelAgent UI
        travelGUI = new TravelAgentGUI(this);
        travelGUI.showGUI();
        
        // Update the list of seller agents every 10 sec
        addBehaviour(new TickerBehaviour(this, 5000) {
          protected void onTick() {
            // Update the list of seller agents
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("flight-selling");
            template.addServices(sd);
            try {
              DFAgentDescription[] result = DFService.search(myAgent, template);
              flightAgentList.clear();
              for (int i = 0; i < result.length; ++i) {
                flightAgentList.addElement(result[i].getName());
              }
            }
            catch (FIPAException fe) {
              fe.printStackTrace();
            }
          }
        } );
    }
    
    public void determineAction(int iInput){
        dfd = new DFAgentDescription();
        dfd.setName(getAID());
        sd = new ServiceDescription();
            
        if(iInput == 1){    
            // Register the book-selling service in the yellow pages
            sd.setType("flight-selling");
            sd.setName("JADE-flight-booking");
        }
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
    
      protected void takeDown() {
        // Dispose the GUI if it is there
        if (travelGUI != null) {
          travelGUI.dispose();
        }

        // Printout a dismissal message
        System.out.println("Buyer-agent "+getAID().getName()+"terminated.");
      }
    
    public void setMsgFlightAva(msgReqFlightAvailability input){
        msgRefFlightAva = new msgReqFlightAvailability(input);
    }
    
    private class PurchaseFlightManager extends TickerBehaviour {
        private String title;
        private msgReqFlightAvailability flight;
        private int maxPrice, startPrice;
        private long deadline, initTime, deltaT;

        private PurchaseFlightManager(Agent a, String t, int mp, Date d) {
          super(a, 5000); // tick every 5 sec
          title = t;
          maxPrice = mp;
          deadline = d.getTime();
          initTime = System.currentTimeMillis();
          deltaT = deadline - initTime;
        }
        
        private PurchaseFlightManager(Agent a, msgReqFlightAvailability f, Date d) {
          super(a, 5000); // tick every 5 sec
          flight = new msgReqFlightAvailability(f);
          deadline = d.getTime();
          initTime = System.currentTimeMillis();
          deltaT = deadline - initTime;
        }

        public void onTick() {
          long currentTime = System.currentTimeMillis();
          if (currentTime > deadline) {
            // Deadline expired
            travelGUI.notifyUser("Cannot book flight!");
            stop();
          }
          else {
            // Compute the currently acceptable price and start a negotiation
            long elapsedTime = currentTime - initTime;
            int acceptablePrice = (int)Math.round(1.0 * flight.getBudget() * (1.0 * elapsedTime / deltaT));
            myAgent.addBehaviour(new FlightNegotiator(title, acceptablePrice, this));
          }
        }
    }
    
    private class FlightNegotiator extends Behaviour {
        private String title;
        private int maxPrice;
        private PurchaseFlightManager flightPurchase_Manager;
        private AID bestSeller; // The seller agent who provides the best offer
        private int bestPrice; // The best offered price
        private int repliesCnt = 0; // The counter of replies from seller agents
        private MessageTemplate mt; // The template to receive replies
        private int step = 0;
        
        private msgReqFlightAvailability flight;

        public FlightNegotiator(String t, int p, PurchaseFlightManager m) {
          super(null);
          title = t;
          maxPrice = p;
          flightPurchase_Manager = m;
        }
        
        public FlightNegotiator(msgReqFlightAvailability f, PurchaseFlightManager m) {
          super(null);
          flight = new msgReqFlightAvailability(f);
          flightPurchase_Manager = m;
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
                      msgFlightAvailability_Result avaFlights = (msgFlightAvailability_Result) reply.getContentObject();
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
          // Receive the purchase order reply
          reply = myAgent.receive(mt);
          if (reply != null) {
            // Purchase order reply received
            if (reply.getPerformative() == ACLMessage.INFORM) {
              // Purchase successful. We can terminate
              travelGUI.notifyUser("Book "+title+" successfully purchased. Price = " + bestPrice);
              flightPurchase_Manager.stop();
            }
            step = 4;
          }
          else {
            block();
          }
          break;
      } // end of switch
    }

    public boolean done() {
      return step == 4;
    }
  } // End of inner class BookNegotiator
}
