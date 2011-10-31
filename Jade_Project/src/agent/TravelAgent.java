/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Agent;

import GUI.TravelAgentGUI;
import OntologyCreditCard.BelongsTo;
import OntologyCreditCard.CreditCardOntology;
import OntologyCreditCard.Person;
import jade.content.abs.AbsObject;
import jade.content.abs.AbsPredicate;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.lang.sl.SLVocabulary;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SequentialBehaviour;
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
import jade.proto.SimpleAchieveREInitiator;
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
    private AID reader = new AID();
    private AID selectedAID;
    private AID creditCardAgent;
    private AID weatherForecastAgent;
    
    private Codec codec;
    
    private msgReqFlightAvailability msgRefFlightAva = new msgReqFlightAvailability();
    
    protected void setup() {
          travelGUI = new TravelAgentGUI(this);
          travelGUI.showGUI();
          /** Search with the DF for the name of the ObjectReaderAgent **/
          
          codec = new SLCodec();
          
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
              travelGUI.notifyUser(getLocalName()+" search with DF is not succeeded because of " + fe.getMessage());
              doDelete();
          }
        
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
    
    //-------------------------------all methods for the UI------------------------------------
    
    //to request for flight retails based on req.
    public void getFlightDetails(msgReqFlightAvailability input){
        //add new behaviour
        try{
            addBehaviour(new RequestFlightDetails(input));
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, reader);
        }
    }
    
    //to request for flight retails based on req.
    public void bookFlight(String flightNo, AID selectedAID){
        //add new behaviour
        try{
            addBehaviour(new BookFlight(flightNo, selectedAID));
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, reader);
        }
    }
    
    //for making payment
    public void makeCCPayment(BelongsTo bt){
        //add new behaviour
        try{
            HandleCreditCardTransactionBehavior ccPayment = new HandleCreditCardTransactionBehavior(bt, this);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, reader);
        }
    }
    
    //-------------------------------all methods for the UI------------------------------------
    
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
                      
                      selectedAID = new AID();
                      selectedAID = msgAvaResult.getSender();  //hardcode for the time being
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
                    else if(msgAvaResult.getPerformative() == ACLMessage.INFORM){
                        String reply = msgAvaResult.getContent();
                        travelGUI.notifyUser(reply);
                    }
                  break;    
            } 
                     
        }
    } // End of inner class OfferRequestsServer
    
    
    private class BookFlight extends CyclicBehaviour {
        private String sFlightNo;
        private MessageTemplate mt; // The template to receive replies
        int step = 0;
        AID selectedFlightAgentID;
        
        public BookFlight(String flightNo, AID id){
            sFlightNo = flightNo;
            selectedFlightAgentID = id;
        }
        
        public void action() {
            switch(step){
                case 0: 
                    // Send the purchase order to the seller that provided the best offer
                    ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    order.addReceiver(selectedAID);
                    order.setContent(sFlightNo);
                    order.setConversationId("flight-trade");
                    order.setReplyWith("order" +System.currentTimeMillis());
                    myAgent.send(order);
                    // Prepare the template to get the purchase order reply
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
                                    MessageTemplate.MatchInReplyTo(order.getReplyWith()));
                    step = 1;
                    break;
                case 1:
                    ACLMessage msgAvaResult = blockingReceive(); 
                    
                    if(msgAvaResult.getPerformative() == ACLMessage.CONFIRM){
                       travelGUI.notifyUser("Flight Confirm!!!");
                       step = 0;
                    }
                    else if(msgAvaResult.getPerformative() == ACLMessage.REFUSE){
                       travelGUI.notifyUser("Flight Refused!!!");
                       step = 0;
                    }
                    else{
                        block();
                    }
                    
                    break;    
            } 
                     
        }
    } // End of inner class OfferRequestsServer
    
    public class HandleCreditCardTransactionBehavior extends SequentialBehaviour{
        private BelongsTo belongTo;
        private Behaviour creditCardQueryBehaviour = null;
        private ACLMessage queryMsg;
        
        public HandleCreditCardTransactionBehavior(BelongsTo input, Agent myAgent){
            super(myAgent);
            belongTo = new BelongsTo();
            belongTo = input;
            
            onStart();
        }
        
         public void onStart() {
            try {
                Ontology o = myAgent.getContentManager().lookupOntology(CreditCardOntology.NAME);
                
                // Create an ACL message to query the engager agent if the above fact is true or false
                ACLMessage queryMsg = new ACLMessage(ACLMessage.QUERY_IF);
                queryMsg.addReceiver(((TravelAgent) myAgent).creditCardAgent);
                queryMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
                queryMsg.setOntology(CreditCardOntology.NAME);

                try {
                    myAgent.getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL0);
                    myAgent.getContentManager().fillContent(queryMsg, belongTo);
                } catch (Exception e) {
                    travelGUI.notifyUser(e.getMessage());
                }

                // Create and add a behaviour to query the engager agent whether
                // person p already works for company c following a FIPAQeury protocol
                creditCardQueryBehaviour = new CheckCreditCardTransactionBehavior(myAgent, queryMsg);
                addSubBehaviour(creditCardQueryBehaviour);
 
            } catch (Exception ex) {
                travelGUI.notifyUser(ex.getMessage());
            }
        }
    }
    
    class CheckCreditCardTransactionBehavior extends SimpleAchieveREInitiator {
        // Constructor

        public CheckCreditCardTransactionBehavior(Agent myAgent, ACLMessage queryMsg) {
            super(myAgent, queryMsg);
            queryMsg.setProtocol(FIPANames.InteractionProtocol.FIPA_QUERY);
        }

        protected void handleInform(ACLMessage msg) {
            try {
                AbsPredicate cs = (AbsPredicate) myAgent.getContentManager().extractAbsContent(msg);
                Ontology o = myAgent.getContentManager().lookupOntology(CreditCardOntology.NAME);
                if (cs.getTypeName().equals(CreditCardOntology.BELONGS_TO)) {
                    // The indicated person is already working for company c. 
                    // Inform the user
                    BelongsTo bt = (BelongsTo) o.toObject((AbsObject) cs);
                    Person p = (Person) bt.getPerson();
                    System.out.println("SUCCESS: Customer " + p.getName() + "'s credit card transaction is SUCCESSFUL. Please proceed");
                } else if (cs.getTypeName().equals(SLVocabulary.NOT)) {
                    // The indicated person is NOT already working for company c.
                    // Get person and company details and create an object representing the engagement action
                    BelongsTo bt = (BelongsTo) o.toObject(cs.getAbsObject(SLVocabulary.NOT_WHAT));
                    Person p = (Person) bt.getPerson();
                    System.out.println("ERROR: Customer " + p.getName() + "'s credit card transaction is UNSUCCESSFUL. Please proceed");
                } else {
                    // Unexpected response received from the engager agent.
                    // Inform the user
                    System.out.println("Unexpected response from engager agent");
                }
            } // End of try
            catch (Codec.CodecException fe) {
                System.err.println("FIPAException in fill/extract Msgcontent:" + fe.getMessage());
            } catch (OntologyException fe) {
                System.err.println("OntologyException in getRoleName:" + fe.getMessage());
            }
        }
    }
}
