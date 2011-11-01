/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Agent;

import GUI.TravelAgentGUI;
import OntologyCreditCard.BelongsTo;
import OntologyCreditCard.CreditCardOntology;
import OntologyCreditCard.Person;
import OntologyWeatherForecast.City;
import OntologyWeatherForecast.IsWeatherForecastAvailable;
import OntologyWeatherForecast.Weather;
import OntologyWeatherForecast.WeatherForecastOntology;
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
          
          getContentManager().registerLanguage(codec, FIPANames.ContentLanguage.FIPA_SL0);
          getContentManager().registerOntology(CreditCardOntology.getInstance());
          getContentManager().registerOntology(WeatherForecastOntology.getInstance());
          
          
          DFAgentDescription template = new DFAgentDescription();
          ServiceDescription sd = new ServiceDescription();
          sd.setType("flightAgent");
          template.addServices(sd);

          try {
            DFAgentDescription[] result = DFService.search(this, template);
            System.out.println("Found the following weather forecast agent:");
            reader = new AID();
            reader = result[0].getName();
          } catch (FIPAException fe) {
            fe.printStackTrace();
          }
        
          sd.setType("CreditCardTransaction");
          template.addServices(sd);

          try {
            DFAgentDescription[] result = DFService.search(this, template);
            System.out.println("Found the following Credit Card forecast agent:");
            creditCardAgent = new AID();
            creditCardAgent = result[0].getName();
            System.out.println(creditCardAgent.getName());
          } catch (FIPAException fe) {
            fe.printStackTrace();
          }
          
          sd.setType("WeatherForecast");
          template.addServices(sd);

          try {
            DFAgentDescription[] result = DFService.search(this, template);
            System.out.println("Found the following Credit Card forecast agent:");
            weatherForecastAgent = new AID();
            weatherForecastAgent = result[0].getName();
            System.out.println(weatherForecastAgent.getName());
          } catch (FIPAException fe) {
            fe.printStackTrace();
          }
        
          this.flight = new msgReqFlightAvailability();
          
          addBehaviour(new PaymentServer());
        
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
            addBehaviour(new HandleWeatherReuestBehavior(this, new City(input.getDestination_City())));
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
    
    private class RequestFlightDetails extends SequentialBehaviour {
        private MessageTemplate mt; // The template to receive replies
        int step = 0;
        
        public RequestFlightDetails(msgReqFlightAvailability input){
            flight = new msgReqFlightAvailability(input);
            
            onStart();
        }
        
        public void onStart() {
            switch(step){
                case 0: 
                      // Send the cfp to all sellers
                      ACLMessage cfp = new ACLMessage(ACLMessage.CFP);

                      cfp.addReceiver(reader);

                      try{
                          //please refer to \jade_example\src\examples\Base64
                          //MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                          //ACLMessage msg = myAgent.receive(mt);
                         
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
                              if(flightAvaResult.getSize() == 0){
                                  travelGUI.notifyUser("No Flight Available!!!");
                              }
                              else{
                                  travelGUI.notifyUser("Flights available!!!");
                                  for(int i = 0; i < flightAvaResult.getSize(); i++){
                                      System.out.println(flightAvaResult.getByIndex(i).getFlightID());
                                  }
                              }
                           
                             
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
    
    
    private class BookFlight extends SequentialBehaviour{
        private String sFlightNo;
        private MessageTemplate mt; // The template to receive replies
        int step = 0;
        AID selectedFlightAgentID;
        
        public BookFlight(String flightNo, AID id){
            sFlightNo = flightNo;
            selectedFlightAgentID = id;
            
            onStart();
        }
        
        public void onStart() {
            switch(step){
                case 0: 
                    // Send the cfp to all sellers
                      ACLMessage acceptPro = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);

                      acceptPro.addReceiver(selectedAID);

                      try{
                          //please refer to \jade_example\src\examples\Base64
                          //MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                          //ACLMessage msg = myAgent.receive(mt);
                         
                          acceptPro.setContent(sFlightNo);
                          acceptPro.setLanguage("JavaSerialization");

                          acceptPro.setDefaultEnvelope();
                          send(acceptPro);
                      }
                      catch(Exception ex){
                          travelGUI.notifyUser(ex.getMessage());
                          return;
                      }

                      acceptPro.setConversationId("flight-trade");
                      acceptPro.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
                      send(acceptPro);

                      // Prepare the template to get proposals
                      mt = MessageTemplate.and(
                        MessageTemplate.MatchConversationId("flight-trade"),
                        MessageTemplate.MatchInReplyTo(acceptPro.getReplyWith()));
                        step = 1; //waiting for reply
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
                queryMsg.addReceiver(creditCardAgent);
                queryMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
                queryMsg.setOntology(CreditCardOntology.NAME);

                try {
                    myAgent.getContentManager().fillContent(queryMsg, belongTo);
                    myAgent.send(queryMsg);
                   
                } catch (Exception e) {
                    travelGUI.notifyUser(e.getMessage());
                }
 
            } catch (Exception ex) {
                travelGUI.notifyUser(ex.getMessage());
            }
        }
    }
    
    private class PaymentServer extends CyclicBehaviour {
        
        public void action() {
            System.out.println(getLocalName()+" is waiting for a payment result");
            ACLMessage msg = blockingReceive(); 
            System.out.println(getLocalName()+ " rx msg"+msg); 

            if(msg.getPerformative() == ACLMessage.CONFIRM){
                travelGUI.notifyUser("Transaction Accpeted");
            }
            else if(msg.getPerformative() == ACLMessage.REFUSE){
                travelGUI.notifyUser("Transaction Rejected");
            }
        }
    }
    
    //------------------------------weather-----------------------------
    public class HandleWeatherReuestBehavior extends SequentialBehaviour{
        private City city;
        private Behaviour weatherQueryBehaviour = null;
        private ACLMessage queryMsg;
        Behaviour weatherForecastQueryBehaviour = null;
        
        public HandleWeatherReuestBehavior(Agent myAgent, City input){
            super(myAgent);
            city = input;
            
            onStart();
        }
        
         public void onStart() {
            try {
                Ontology o = myAgent.getContentManager().lookupOntology(WeatherForecastOntology.NAME);
                
                // Create an ACL message to query the engager agent if the above fact is true or false
                ACLMessage queryMsg = new ACLMessage(ACLMessage.QUERY_IF);
                queryMsg.addReceiver(weatherForecastAgent);
                queryMsg.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
                queryMsg.setOntology(CreditCardOntology.NAME);

                IsWeatherForecastAvailable wf = new IsWeatherForecastAvailable();
                wf.setCity(city);
                Weather w = new Weather("NA", "NA", "NA");
                wf.setWeather(w);
                wf.setDate("2011/11/01");
                
                try {
                    myAgent.getContentManager().fillContent(queryMsg, wf);
                    myAgent.send(queryMsg);
                   
                } catch (Exception e) {
                    travelGUI.notifyUser(e.getMessage());
                }
                
                weatherForecastQueryBehaviour = new CheckAvailableWeatherForecastBehavior(myAgent, queryMsg);
                addSubBehaviour(weatherForecastQueryBehaviour);
 
            } catch (Exception ex) {
                travelGUI.notifyUser(ex.getMessage());
            }
        }
    }
    
    class CheckAvailableWeatherForecastBehavior extends SimpleAchieveREInitiator {
        // Constructor

        public CheckAvailableWeatherForecastBehavior(Agent myAgent, ACLMessage queryMsg) {
            super(myAgent, queryMsg);
            queryMsg.setProtocol(FIPANames.InteractionProtocol.FIPA_QUERY);
        }

        protected void handleInform(ACLMessage msg) {
            try {
                AbsPredicate cs = (AbsPredicate) myAgent.getContentManager().extractAbsContent(msg);
                Ontology o = myAgent.getContentManager().lookupOntology(WeatherForecastOntology.NAME);
                if (cs.getTypeName().equals(WeatherForecastOntology.WEATHER_FORECAST)) {

                    IsWeatherForecastAvailable wf = (IsWeatherForecastAvailable) o.toObject((AbsObject) cs);
                    Weather w = (Weather) wf.getWeather();
                    System.out.println("SUCCESS: querying weather forecast");
                    System.out.println("Temperature: " + w.getTemperature());
                    System.out.println("Humidity: " + w.getHumidity());
                    System.out.println("Condition: " + w.getCondition());

                } else if (cs.getTypeName().equals(SLVocabulary.NOT)) {
                    // The indicated person is NOT already working for company c.
                    // Get person and company details and create an object representing the engagement action
                    IsWeatherForecastAvailable wf = (IsWeatherForecastAvailable) o.toObject(cs.getAbsObject(SLVocabulary.NOT_WHAT));
                    City c = (City) wf.getCity();
                    String d = (String) wf.getDate();
                    System.out.println("ERROR: cannot get forecast for " + c.getCityName() + " on this date " + d);
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
