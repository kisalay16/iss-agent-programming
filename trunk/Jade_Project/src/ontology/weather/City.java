/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OntologyWeatherForecast;

import jade.content.Concept;
import java.util.Date;

/**
 *
 * @author rsoon
 */
public class City implements Concept {

    private String cityName;
    
    public City() {
    }

    public City(String cityName) {
        this.cityName = cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return this.cityName;
    }

    public boolean AreYouThisCity(City c) {
        boolean sameCity = false;

        if (cityName.equalsIgnoreCase(c.getCityName())){
            sameCity = true;
        }

        return sameCity;
    }
}
