/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concept;

import java.util.Date;

/**
 *
 * @author henry
 */
public class TravelConcept {
    
    public TravelConcept(){
        
    }
    
    String sAirliner;
    public String getAirliner(){
        return this.sAirliner;
    }
    public void setAirliner(String sValue){
        this.sAirliner = sValue;
    }
    
    private String sFlightID;
    public String getFlightID(){
        return this.sFlightID;
    }
    public void setFlightID(String sValue){
        this.sFlightID = sValue;
    }
    
    private Date dDepartureDate;
    public Date getDepartureDate(){
        return this.dDepartureDate;
    }
    public void setDepartureDate(Date dValue){
        this.dDepartureDate = dValue;
    }
    
    private Date dArrivalDate;
    public Date getArrivalDate(){
        return this.dDepartureDate;
    }
    public void setArrivalDate(Date dValue){
        this.dArrivalDate = dValue;
    }
    
    private Double dAirFareTotal;
    public Double getAirFareTotal(){
        return this.dAirFareTotal;
    }
    public void setAirFareTotal(Double dValue){
        this.dAirFareTotal = dValue;
    }
    
    private String sDeparture_City;
    public String getDeparture_City(){
        return this.sDeparture_City;
    }
    public void setDeparture_City(String sValue){
        this.sDeparture_City = sValue;
    }
    
    private String sDestination_City;
    public String getDestination_City(){
        return this.sDestination_City;
    }
    public void setDestination_City(String sValue){
        this.sDestination_City = sValue;
    }
    
    private Boolean bIsTaken;  //to see if the flight is taken or not
    public Boolean getIsTaken(){
        return this.bIsTaken;
    }
    public void setIsTaken(Boolean bValue){
        this.bIsTaken = bValue;
    }
}
