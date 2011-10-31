/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OntologyCreditCard;

import jade.content.onto.*;
import jade.content.schema.*;

/**
 *
 * @author rsoon
 */
public class CreditCardOntology extends Ontology {

    public static final String NAME = "creditcard-ontology";
    public static final String ADDRESS = "ADDRESS";
    public static final String ADDRESS_BLOCKNUMBER = "blocknumber";
    public static final String ADDRESS_STREETNAME = "streetname";
    public static final String ADDRESS_UNITNUMBER = "unitnumber";
    public static final String ADDRESS_POSTALCODE = "postalcode";
    public static final String PERSON = "PERSON";
    public static final String PERSON_NAME = "name";
    public static final String PERSON_ADDRESS = "address";
    public static final String CREDITCARD = "CREDITCARD";
    public static final String CREDITCARD_TYPE = "cardtype";
    public static final String CREDITCARD_NUMBER = "cardnumber";
    public static final String BELONGS_TO = "BELONGS-TO";
    public static final String BELONGS_TO_PERSON = "person";
    public static final String BELONGS_TO_CREDITCARD = "creditcard";
    private static Ontology theInstance = new CreditCardOntology();

    public static Ontology getInstance() {
        return theInstance;
    }

    private CreditCardOntology() {
        super(NAME, BasicOntology.getInstance());

        try {
            add(new ConceptSchema(ADDRESS), Address.class);
            add(new ConceptSchema(PERSON), Person.class);
            add(new ConceptSchema(CREDITCARD), CreditCard.class);
            add(new PredicateSchema(BELONGS_TO), BelongsTo.class);

            ConceptSchema cs = (ConceptSchema) getSchema(ADDRESS);
            cs.add(ADDRESS_BLOCKNUMBER, (PrimitiveSchema) getSchema(BasicOntology.STRING));
            cs.add(ADDRESS_STREETNAME, (PrimitiveSchema) getSchema(BasicOntology.STRING));
            cs.add(ADDRESS_UNITNUMBER, (PrimitiveSchema) getSchema(BasicOntology.STRING));
            cs.add(ADDRESS_POSTALCODE, (PrimitiveSchema) getSchema(BasicOntology.STRING));

            cs = (ConceptSchema) getSchema(PERSON);
            cs.add(PERSON_NAME, (PrimitiveSchema) getSchema(BasicOntology.STRING));
            cs.add(PERSON_ADDRESS, (ConceptSchema) getSchema(ADDRESS));

            cs = (ConceptSchema) getSchema(CREDITCARD);
            cs.add(CREDITCARD_TYPE, (PrimitiveSchema) getSchema(BasicOntology.STRING));
            cs.add(CREDITCARD_NUMBER, (PrimitiveSchema) getSchema(BasicOntology.STRING));

            PredicateSchema ps = (PredicateSchema) getSchema(BELONGS_TO);
            ps.add(BELONGS_TO_PERSON, (ConceptSchema) getSchema(PERSON));
            ps.add(BELONGS_TO_CREDITCARD, (ConceptSchema) getSchema(CREDITCARD));
        } catch (OntologyException oe) {
            oe.printStackTrace();
        }
    }
}
