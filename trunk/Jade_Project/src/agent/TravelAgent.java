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
public class TravelAgent extends Agent{
    //refer to http://www.iro.umontreal.ca/~dift6802/jade/src/examples/bookTrading/BookBuyerAgent.java
    
    private TravelAgentGUI travelGUI;
    private DFAgentDescription dfd;
    private ServiceDescription sd;
    
    private AID[] flightAgents; //all known flight agents available
    
    private msgReqFlightAvailability msgRefFlightAva = new msgReqFlightAvailability();
    
    protected void setup() {
        //to show the travelAgent UI
        travelGUI = new TravelAgentGUI(this);
        travelGUI.showGUI();
        
        // Add a TickerBehaviour that schedules a request to seller agents every minute
        addBehaviour(new TickerBehaviour(this, 60000) {
            protected void onTick() {
            // Update the list of seller agents
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("flight-selling");
            template.addServices(sd);
            try {
                DFAgentDescription[] result = DFService.search(myAgent, template); 
                System.out.println("Found the following Flight Agents:");
                flightAgents = new AID[result.length];
                for (int i = 0; i < result.length; ++i) {
                    flightAgents[i] = result[i].getName();
                    System.out.println(flightAgents[i].getName());
                }
            }
            catch (FIPAException fe) {
                    fe.printStackTrace();
            }

            // Perform the request
            //myAgent.addBehaviour(new RequestPerformer());
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
