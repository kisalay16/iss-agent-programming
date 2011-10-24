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
import java.util.Date;
import javax.swing.JOptionPane;
import message.msgFlightAvailability_Result;
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
    private msgFlightAvailability_Result flightAvaList; //to keep track of all the available flight list
    
    private msgReqFlightAvailability msgRefFlightAva = new msgReqFlightAvailability();
    
    protected void setup() {
       System.out.println("Flight Agent Ready"); 
        
       //set up the flights available for booking
       flightAvaList = new msgFlightAvailability_Result("SIA", "Flight001", new Date(2011, 6, 11, 20, 30), new Date(2011, 6, 12, 7, 20), 1500.00, "Singapore", "London");
       flightAvaList = new msgFlightAvailability_Result("Qantas", "Flight002", new Date(2011, 6, 20, 8, 30), new Date(2011, 6, 21, 10, 35), 2000.00, "London", "Singapore");
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
}
