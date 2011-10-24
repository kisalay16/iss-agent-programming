/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author henry
 */
public class msgFlightAvailability_Result {
    
    //default constructor
    public msgFlightAvailability_Result(){
        
    }
    
    //default constructor
    public msgFlightAvailability_Result(String airline, String flightID, Date departDate, Date arrivalDate, Double total, String origin_city, String des_city){
        sAirliner.add(airline);
        sFlightID.add(flightID);
        dDepartureDate.add(departDate);
        dArrivalTime.add(arrivalDate);
        dAirFareTotal.add(total);
        sOrigin_City.add(origin_city);
        sDestination_City.add(des_city);
    }
   
    private List<String> sAirliner = new ArrayList<String>();
    private List<String> sFlightID = new ArrayList<String>();
    private List<Date> dDepartureDate = new ArrayList<Date>();
    private List<Date> dArrivalTime = new ArrayList<Date>();
    private List<Double> dAirFareTotal = new ArrayList<Double>();
    private List<String> sOrigin_City = new ArrayList<String>();
    private List<String> sDestination_City = new ArrayList<String>();
    
}
