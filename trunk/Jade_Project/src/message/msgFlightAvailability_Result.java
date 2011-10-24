/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

import jade.util.leap.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author henry
 */
public class msgFlightAvailability_Result implements Serializable {
    
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
   
    private ArrayList<String> sAirliner = new ArrayList<String>();
    private ArrayList<String> sFlightID = new ArrayList<String>();
    private ArrayList<Date> dDepartureDate = new ArrayList<Date>();
    private ArrayList<Date> dArrivalTime = new ArrayList<Date>();
    private ArrayList<Double> dAirFareTotal = new ArrayList<Double>();
    private ArrayList<String> sOrigin_City = new ArrayList<String>();
    private ArrayList<String> sDestination_City = new ArrayList<String>();
    
    //get number of flights available
    public int getCount(){
        return sAirliner.size();
    }
    
    //to get all the flights that is according to user's specification
    public msgFlightAvailability_Result getFlightsAccordingToSpecs(msgReqFlightAvailability input){
        msgFlightAvailability_Result result = new msgFlightAvailability_Result();
        
        //if found the right flight, then add to the list
        for(int i = 0; i < sAirliner.size(); i++){
            if(dDepartureDate.get(i).compareTo(input.getOnWard_Date()) == 0 && dAirFareTotal.get(i) <= input.getBudget() && sOrigin_City.get(i).compareTo(input.getOrigin_City()) == 0 && sDestination_City.get(i).compareTo(input.getDestination_City()) == 0){
                result = new msgFlightAvailability_Result(sAirliner.get(i), sFlightID.get(i), dDepartureDate.get(i), dArrivalTime.get(i), dAirFareTotal.get(i), sOrigin_City.get(i), sDestination_City.get(i));
            }
        }
        
        return result;
    }
    
}
