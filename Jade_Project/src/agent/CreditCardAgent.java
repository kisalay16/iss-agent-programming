/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Agent;

import GUI.TravelAgentGUI;
import OntologyCreditCard.Address;
import OntologyCreditCard.BelongsTo;
import OntologyCreditCard.CreditCard;
import OntologyCreditCard.CreditCardOntology;
import OntologyCreditCard.Person;
import jade.content.Predicate;
import jade.content.abs.AbsPredicate;
import jade.content.lang.sl.SLVocabulary;
import jade.content.onto.Ontology;
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
import java.util.Iterator;
import java.util.List;
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
public class CreditCardAgent extends Agent{
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
          
          /** Registration with the DF */
          DFAgentDescription dfd = new DFAgentDescription();    
          ServiceDescription sd = new ServiceDescription();
          sd.setType("CreditCardTransaction"); 
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
        addBehaviour(new MakePayment());
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
    private class MakePayment extends CyclicBehaviour {
        private int step = 0;
        
        public void action() {
            try {
                System.out.println(getLocalName()+" is waiting for a booking");
                
                ACLMessage msg = blockingReceive(); 
                ACLMessage reply = msg.createReply();
                System.out.println(getLocalName()+ " rx msg"+msg); 
      
                if (msg.getPerformative() != ACLMessage.QUERY_IF) {
                    reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                    String content = "(" + msg.toString() + ")";
                    reply.setContent(content);
                    send(reply);
                }
                
                else if(msg.getPerformative() == ACLMessage.QUERY_IF){
                    // Get the predicate for which the truth is queried	
                    try{ 
                        Predicate pred = (Predicate) myAgent.getContentManager().extractContent(msg);
                        if (!(pred instanceof BelongsTo)) {
                            reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                            String content = "(" + msg.toString() + ")";
                            reply.setContent(content);
                            send(reply);
                        }
                        
                        // Reply 
                        reply.setPerformative(ACLMessage.INFORM);
                        BelongsTo bt = (BelongsTo) pred;
                        Address a = bt.getPerson().getAddress();
                        Person p = bt.getPerson();
                        CreditCard c = bt.getCreditCard();
                        List approvedCreditCardList = ReturnDummyCreditCardList();
                        if (((CreditCardAgent) myAgent).CardIsBelongingTo(approvedCreditCardList, a, p, c)) {
                            reply.setContent(msg.getContent());
                        } else {
                            Ontology o = getContentManager().lookupOntology(CreditCardOntology.NAME);
                            AbsPredicate not = new AbsPredicate(SLVocabulary.NOT);
                            not.set(SLVocabulary.NOT_WHAT, o.fromObject(bt));
                            myAgent.getContentManager().fillContent(reply, not);
                        }
                        
                    }
                    catch(Exception ex){
                        
                    }
                }
            }
            catch(Exception ex){
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }       
        }
    }
    
    private List ReturnDummyCreditCardList() {
        List authorisedCardList;
        
        Address a1 = new Address("550", "Bedok North Avenue 1", "11-516", "460550");
        Person p1 = new Person("Ryan Soon", a1);
        CreditCard c1 = new CreditCard("Mastercard", "1234123412341234");
        BelongsTo bT1 = new BelongsTo(p1, c1);

        Address a2 = new Address("508", "Chai Chee Lane", "01-01", "469032");
        Person p2 = new Person("Gerrard Soon", a1);
        CreditCard c2 = new CreditCard("VISA", "4321432143214321");
        BelongsTo bT2 = new BelongsTo(p2, c2);

        authorisedCardList = new ArrayList();
        authorisedCardList.add(bT1);
        authorisedCardList.add(bT2);

        return authorisedCardList;
    }
    
    private boolean CardIsBelongingTo(List authCL, Address a, Person p, CreditCard c) {
        boolean isBelongingTo = false;

        Iterator i = authCL.iterator();
        while (i.hasNext()) {
            BelongsTo bt = (BelongsTo) i.next();

            if (bt.DoYouHaveThisCard(p, a, c)) {
                isBelongingTo = true;
                break;
            }
        }

        return isBelongingTo;
    }
}
