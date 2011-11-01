/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agent;

import OntologyWeatherForecast.*;

import jade.core.Agent;
import jade.content.abs.AbsPredicate;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.lang.sl.SLVocabulary;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPANames;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;

import jade.proto.SimpleAchieveREResponder;
import jade.content.Predicate;

import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;
import java.util.Date;

/**
 *
 * @author rsoon
 */
public class WeatherForecastAgent extends Agent {

    class HandleWeatherForecastQueriesBehavior extends SimpleAchieveREResponder {

        public HandleWeatherForecastQueriesBehavior(Agent myAgent) {
            super(myAgent, MessageTemplate.and(
                    MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_QUERY),
                    MessageTemplate.MatchOntology(WeatherForecastOntology.NAME)));
        }

        public ACLMessage prepareResponse(ACLMessage msg) {
            ACLMessage reply = msg.createReply();

            if (msg.getPerformative() != ACLMessage.QUERY_IF) {
                reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                String content = "(" + msg.toString() + ")";
                reply.setContent(content);
                return (reply);
            }

            try {
                // Get the predicate for which the truth is queried	
                Predicate pred = (Predicate) myAgent.getContentManager().extractContent(msg);
                if (!(pred instanceof IsWeatherForecastAvailable)) {
                    reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                    String content = "(" + msg.toString() + ")";
                    reply.setContent(content);
                    return (reply);
                }

                // Reply 
                reply.setPerformative(ACLMessage.INFORM);
                IsWeatherForecastAvailable wf = (IsWeatherForecastAvailable) pred;
                City c = wf.getCity();
                String d = wf.getDate();
                List availableWeatherForecastList = ReturnDummyWeatherForecastList();

                IsWeatherForecastAvailable retrievedWf = ForecastAvailable(availableWeatherForecastList, c, d);

                if (!retrievedWf.getWeather().getTemperature().equalsIgnoreCase("NA") && !retrievedWf.getWeather().getHumidity().equalsIgnoreCase("NA")
                        && !retrievedWf.getWeather().getCondition().equalsIgnoreCase("NA")) {

                    myAgent.getContentManager().fillContent(reply, retrievedWf);
                    //reply.setContent(msg.getContent());
                } else {
                    Ontology o = getContentManager().lookupOntology(WeatherForecastOntology.NAME);
                    AbsPredicate not = new AbsPredicate(SLVocabulary.NOT);
                    not.set(SLVocabulary.NOT_WHAT, o.fromObject(wf));
                    myAgent.getContentManager().fillContent(reply, not);
                }
            } catch (Codec.CodecException fe) {
                System.err.println(myAgent.getLocalName() + " Fill/extract content unsucceeded. Reason:" + fe.getMessage());
            } catch (OntologyException oe) {
                System.err.println(myAgent.getLocalName() + " getRoleName() unsucceeded. Reason:" + oe.getMessage());
            }

            return (reply);
        }
    }

    protected void setup() {
        System.out.println("Hi, weather agent at your service");
        RegisterAgent();

        getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
        getContentManager().registerOntology(WeatherForecastOntology.getInstance());
        addBehaviour(new HandleWeatherForecastQueriesBehavior(this));
    }

    private void RegisterAgent() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("WeatherForecast");
        sd.setName("JadeTravelAgent");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private List ReturnDummyWeatherForecastList() {

        List weatherForecastList;

        City c1 = new City("Singapore");
        Weather w1 = new Weather("33", "88", "Sunny");
        IsWeatherForecastAvailable wf1 = new IsWeatherForecastAvailable(c1, w1, "2011/12/01");

        City c2 = new City("Johor");
        Weather w2 = new Weather("30", "78", "Partly Cloudy");
        IsWeatherForecastAvailable wf2 = new IsWeatherForecastAvailable(c2, w2, "2011/12/02");
        
        City c3 = new City("Jakarta");
        Weather w3 = new Weather("27", "68", "Thunderstorm");
        IsWeatherForecastAvailable wf3 = new IsWeatherForecastAvailable(c3, w3, "2011/12/03");

        weatherForecastList = new ArrayList();
        weatherForecastList.add(wf1);
        weatherForecastList.add(wf2);
        weatherForecastList.add(wf3);

        return weatherForecastList;
    }

    private IsWeatherForecastAvailable ForecastAvailable(List availList, City c, String d) {

        Weather w = new Weather("NA", "NA", "NA");
        IsWeatherForecastAvailable tempWf = new IsWeatherForecastAvailable(c, w, d);

        Iterator i = availList.iterator();
        while (i.hasNext()) {
            IsWeatherForecastAvailable wf = (IsWeatherForecastAvailable) i.next();

            if (wf.DoYouHaveThisForecast(c, d)) {
                tempWf = wf;
                break;
            }
        }

        return tempWf;
    }
}
