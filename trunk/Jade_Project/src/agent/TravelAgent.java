/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Agent;

import GUI.TravelAgentGUI;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
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
    private msgFlightAvailability_Result_List flightAvaResult;
    
    // The list of known flight agents
    private Vector flightAgentList = new Vector();
    AID reader = new AID();
    
    
    private msgReqFlightAvailability msgRefFlightAva = new msgReqFlightAvailability();
    
    protected void setup() {
          travelGUI = new TravelAgentGUI(this);
          travelGUI.showGUI();
          /** Search with the DF for the name of the ObjectReaderAgent **/
          
          DFAgentDescription dfd = new DFAgentDescription();  
          ServiceDescription sd = new ServiceDescription();
          sd.setType("ObjectReaderAgent"); 
          dfd.addServices(sd);
          try {
            while (true) {
              System.out.println(getLocalName()+ " waiting for an ObjectReaderAgent registering with the DF");
              SearchConstraints c = new SearchConstraints();
              c.setMaxDepth(new Long(3));
              DFAgentDescription[] result = DFService.search(this,dfd,c);
              if ((result != null) && (result.length > 0)) {
                dfd = result[0]; 
                reader = dfd.getName();
                break;
              }
              Thread.sleep(10000);
            }
          } catch (Exception fe) {
              fe.printStackTrace();
              System.err.println(getLocalName()+" search with DF is not succeeded because of " + fe.getMessage());
              doDelete();
          }

          System.out.println(getLocalName()+" agent sends ACLMessages whose content is a Java object");

        
          this.flight = new msgReqFlightAvailability();
        
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
        //add new behaviour
        try{
            addBehaviour(new RequestFlightDetails(input));
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, reader);
        }
    }
    
    private class RequestFlightDetails extends CyclicBehaviour {
        private MessageTemplate mt; // The template to receive replies
        int step = 0;
        
        public RequestFlightDetails(msgReqFlightAvailability input){
            flight = new msgReqFlightAvailability(input);
        }
        
        public void action() {
            switch(step){
                case 0: 
                      // Send the cfp to all sellers
                      ACLMessage cfp = new ACLMessage(ACLMessage.CFP);

                      cfp.addReceiver(reader);

                      try{
                          //please refer to \jade_example\src\examples\Base64
                          MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                          ACLMessage msg = myAgent.receive(mt);
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

                      // Prepare the template to get proposals
                      mt = MessageTemplate.and(
                        MessageTemplate.MatchConversationId("flight-trade"),
                        MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                        step = 1; //waiting for reply
                      break;
                case 1:
                    ACLMessage msgAvaResult = blockingReceive(); 
                    
                    if(msgAvaResult.getPerformative() == ACLMessage.PROPOSE){
                    
                      if ("JavaSerialization".equals(msgAvaResult.getLanguage())) {
                         try{
                              flightAvaResult = new msgFlightAvailability_Result_List();
                              flightAvaResult = (msgFlightAvailability_Result_List)msgAvaResult.getContentObject();

                              travelGUI.displayAvaFlights(flightAvaResult);
                           
                             
                         } catch (UnreadableException ex) {
                             Logger.getLogger(TravelAgent.class.getName()).log(Level.SEVERE, null, ex);
                         }
                    
                      }  
                      step = 3; //don't accept anymore
                  }
                  break;    
            } 
                     
        }
    } // End of inner class OfferRequestsServer
    
}
