/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OntologyWeatherForecast;

import jade.content.onto.*;
import jade.content.schema.*;

/**
 *
 * @author rsoon
 */
public class WeatherForecastOntology extends Ontology {

    public static final String NAME = "weatherforecast-ontology";
    public static final String CITY = "CITY";
    public static final String CITY_NAME = "cityname";
    public static final String WEATHER = "WEATHER";
    public static final String WEATHER_TEMPERATURE = "temperature";
    public static final String WEATHER_HUMIDITY = "humidity";
    public static final String WEATHER_CONDITION = "condition";
    public static final String WEATHER_FORECAST = "WEATHER-FORECAST";
    public static final String WEATHER_FORECAST_CITY = "city";
    public static final String WEATHER_FORECAST_WEATHER = "weather";
    public static final String WEATHER_FORECAST_DATE = "date";
    private static Ontology theInstance = new WeatherForecastOntology();

    public static Ontology getInstance() {
        return theInstance;
    }

    private WeatherForecastOntology() {
        super(NAME, BasicOntology.getInstance());

        try {
            add(new ConceptSchema(CITY), City.class);
            add(new ConceptSchema(WEATHER), Weather.class);
            add(new PredicateSchema(WEATHER_FORECAST), IsWeatherForecastAvailable.class);

            ConceptSchema cs = (ConceptSchema) getSchema(CITY);
            cs.add(CITY_NAME, (PrimitiveSchema) getSchema(BasicOntology.STRING));

            cs = (ConceptSchema) getSchema(WEATHER);
            cs.add(WEATHER_TEMPERATURE, (PrimitiveSchema) getSchema(BasicOntology.STRING));
            cs.add(WEATHER_HUMIDITY, (PrimitiveSchema) getSchema(BasicOntology.STRING));
            cs.add(WEATHER_CONDITION, (PrimitiveSchema) getSchema(BasicOntology.STRING));

            PredicateSchema ps = (PredicateSchema) getSchema(WEATHER_FORECAST);
            ps.add(WEATHER_FORECAST_CITY, (ConceptSchema) getSchema(CITY));
            ps.add(WEATHER_FORECAST_WEATHER, (ConceptSchema) getSchema(WEATHER));
            ps.add(WEATHER_FORECAST_DATE, (PrimitiveSchema) getSchema(BasicOntology.STRING));
        } catch (OntologyException oe) {
            oe.printStackTrace();
        }
    }
}
