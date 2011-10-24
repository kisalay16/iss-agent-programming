/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Agent;

import GUI.TravelAgentGUI;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import javax.swing.JOptionPane;
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
    
    private msgReqFlightAvailability msgRefFlightAva = new msgReqFlightAvailability();
    
    protected void setup() {
       
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
            System.out.println("Travel-Agent "+getAID().getName()+" terminating.");
    }
    
    public void setMsgFlightAva(msgReqFlightAvailability input){
        msgRefFlightAva = new msgReqFlightAvailability(input);
    }
}