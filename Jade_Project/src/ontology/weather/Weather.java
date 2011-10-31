/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OntologyWeatherForecast;

import jade.content.Concept;

/**
 *
 * @author rsoon
 */
public class Weather implements Concept {

    private String temperature;
    private String humidity;
    private String condition;

    public Weather() {
    }

    public Weather(String temperature, String humidity, String condition) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.condition = condition;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getTemperature() {
        return this.temperature;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getHumidity() {
        return this.humidity;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return this.condition;
    }
}
