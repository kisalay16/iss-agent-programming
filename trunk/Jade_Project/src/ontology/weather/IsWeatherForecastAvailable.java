/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OntologyWeatherForecast;

import jade.content.Predicate;

/**
 *
 * @author rsoon
 */
public class IsWeatherForecastAvailable implements Predicate {

    private City city;
    private Weather weather;
    private String date;

    public IsWeatherForecastAvailable() {
    }

    public IsWeatherForecastAvailable(City city, Weather weather, String date) {
        this.city = city;
        this.weather = weather;
        this.date = date;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public City getCity() {
        return this.city;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public Weather getWeather() {
        return this.weather;
    }
    
    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return this.date;
    }
    
    public boolean DoYouHaveThisForecast(City c, String d) {
        boolean haveThisForecast = false;

        if (city.AreYouThisCity(c) && date.equalsIgnoreCase(d)) {
            haveThisForecast = true;
        }

        return haveThisForecast;
    }
}
