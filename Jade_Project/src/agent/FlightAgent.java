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
    private msgFlightAvailability_Result_List flightAvaList; //to keep track of all the available flight list
    
    private msgReqFlightAvailability msgRefFlightAva = new msgReqFlightAvailability();
    
    protected void setup() {
       System.out.println("Flight Agent Ready"); 
        
       //set up the flights available for booking
       flightAvaList.addFlight(new msgFlightAvailability_Result("SIA", "Flight001", new Date(2011, 6, 11, 20, 30), new Date(2011, 6, 12, 7, 20), 1500.00, "Singapore", "London"));
       flightAvaList.addFlight(new msgFlightAvailability_Result("Qantas", "Flight002", new Date(2011, 6, 20, 8, 30), new Date(2011, 6, 21, 10, 35), 2000.00, "London", "Singapore"));
       
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
        // Close the GUI
        travelGUI.dispose();
        // Printout a dismissal message
        System.out.println("Flight-Agent "+getAID().getName()+" terminating.");
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
    private class OfferFlightRequestsServer extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                try {
                    // Message received. Process it
                    msgReqFlightAvailability req = (msgReqFlightAvailability) msg.getContentObject();
                } catch (UnreadableException ex) {
                    Logger.getLogger(FlightAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
                ACLMessage reply = msg.createReply();
                //get all available flights according requirement
                msgFlightAvailability_Result_List flightResult = flightAvaList.getFlightsAccordingToSpecs(msgRefFlightAva);
                
                if(flightResult.getSize() == 0){
                    // The requested book is NOT available for sale.
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("not-available");
                }
                else{
                    // The requested book is available for sale. Reply with the price
                    reply.setPerformative(ACLMessage.PROPOSE);
                    
                    try{
                        msg.setContentObject(flightResult);
                        msg.setLanguage("JavaSerialization");
                        
                        msg.setDefaultEnvelope();
                        msg.getEnvelope().setAclRepresentation(FIPANames.ACLCodec.BITEFFICIENT);
                        send(msg);
                        System.out.println(getLocalName()+" sent 1st msg with bit-efficient aclCodec "+ msg);

                        msg.getEnvelope().setAclRepresentation(FIPANames.ACLCodec.XML); 
                        send(msg);
                        System.out.println(getLocalName()+" sent 1st msg with xml aclCodec "+ msg);
                    }
                    catch(IOException ex){
                        return;
                    }
                }
               
                myAgent.send(reply);
            }
        }
    } // End of inner class OfferRequestsServer
    
    
    
}