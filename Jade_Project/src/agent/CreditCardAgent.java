/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Agent;

import OntologyCreditCard.*;

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

/**
 *
 * @author rsoon
 */
public class CreditCardAgent extends Agent {

    class HandleCreditCardQueriesBehavior extends SimpleAchieveREResponder {

        public HandleCreditCardQueriesBehavior(Agent myAgent) {
            super(myAgent, MessageTemplate.and(
                    MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_QUERY),
                    MessageTemplate.MatchOntology(CreditCardOntology.NAME)));
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
                if (!(pred instanceof BelongsTo)) {
                    reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                    String content = "(" + msg.toString() + ")";
                    reply.setContent(content);
                    return (reply);
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
            } catch (Codec.CodecException fe) {
                System.err.println(myAgent.getLocalName() + " Fill/extract content unsucceeded. Reason:" + fe.getMessage());
            } catch (OntologyException oe) {
                System.err.println(myAgent.getLocalName() + " getRoleName() unsucceeded. Reason:" + oe.getMessage());
            }

            return (reply);
        }
    }

    protected void setup() {
        System.out.println("Hello, credit card agent at your service");
        RegisterAgent();

        getContentManager().registerLanguage(new SLCodec(), FIPANames.ContentLanguage.FIPA_SL0);
        getContentManager().registerOntology(CreditCardOntology.getInstance());
        addBehaviour(new HandleCreditCardQueriesBehavior(this));
    }

    private void RegisterAgent() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("CreditCardTransaction");
        sd.setName("JadeTravelAgent");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
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
