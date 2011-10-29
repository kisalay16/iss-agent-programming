/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ontology;

import jade.content.onto.*;
import concept.Travel;
import jade.content.onto.OntologyException;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PrimitiveSchema;
import java.util.Date;

/**
 *
 * @author henry
 */
public class TravelOntology extends Ontology{
    public static final String NAME = "travel-ontology";

    // VOCABULARY
    // Concepts
    public static final String TRAVEL = "travel";
    public static final String ORIGIN_CITY = "origin_city";
    public static final String ORIGIN_COUNTRY = "origin_country";
    public static final String DESTINATION_CITY = "destination_city";
    public static final String DESTINATION_COUNTRY = "origin_country";
    public static final String ONWARD_DATE = "onward_date";
    public static final String RETURN_DATE = "return_date";
    public static final String NO_OF_TRAVELLERS = "no_of_traveller";
    public static final String BUDGET = "budget";
    
    private static Ontology theInstance = new TravelOntology();
    
    public static Ontology getInstance() {
        return theInstance;
   }
    
    private TravelOntology() {
    //__CLDC_UNSUPPORTED__BEGIN
  	super(NAME, BasicOntology.getInstance());


        try {
            //for adding schema
            add(new ConceptSchema(TRAVEL), Travel.class);
            
            ConceptSchema csTravel = (ConceptSchema)getSchema(TRAVEL);
            csTravel.add(ORIGIN_CITY, (PrimitiveSchema)getSchema(BasicOntology.STRING));
            csTravel.add(ORIGIN_COUNTRY, (PrimitiveSchema)getSchema(BasicOntology.STRING));
            csTravel.add(DESTINATION_CITY, (PrimitiveSchema)getSchema(BasicOntology.STRING));
            csTravel.add(DESTINATION_COUNTRY, (PrimitiveSchema)getSchema(BasicOntology.STRING));
            csTravel.add(ONWARD_DATE, (PrimitiveSchema)getSchema(BasicOntology.INTEGER), ObjectSchema.OPTIONAL);
            csTravel.add(RETURN_DATE, (PrimitiveSchema)getSchema(BasicOntology.INTEGER), ObjectSchema.OPTIONAL);
            csTravel.add(BUDGET, (PrimitiveSchema)getSchema(BasicOntology.INTEGER), ObjectSchema.OPTIONAL);
            
            
        }
        catch(OntologyException oe) {
          oe.printStackTrace();
        }
    }
}
