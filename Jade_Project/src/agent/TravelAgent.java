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
import java.util.Vector;
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
