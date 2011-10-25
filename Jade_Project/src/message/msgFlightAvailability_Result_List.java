/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

import jade.util.leap.Serializable;
import java.util.ArrayList;

/**
 *
 * @author henry
 */
public class msgFlightAvailability_Result_List implements Serializable{
    private ArrayList<msgFlightAvailability_Result> flightResultList = new ArrayList<msgFlightAvailability_Result>();
    
    public msgFlightAvailability_Result_List(){
        flightResultList = new ArrayList<msgFlightAvailability_Result>();
    }
    
    //to add new flight
    public void addFlight(msgFlightAvailability_Result input){
        flightResultList.add(input);
    }
    
    //to get a particular flight detail
    public msgFlightAvailability_Result getByIndex(Integer i){
        return flightResultList.get(i);
    }
    
    //to get all flights that suit the criteria
    public msgFlightAvailability_Result_List getFlightsAccordingToSpecs(msgReqFlightAvailability input){
        
        msgFlightAvailability_Result_List result = new msgFlightAvailability_Result_List();
        
        for(int i = 0; i < flightResultList.size(); i++){
            //get all available flights to the destination
            //orgin -> des
            if(flightResultList.get(i).getDeparture_City().compareTo(input.getOrigin_City()) == 0 && flightResultList.get(i).getDestination_City().compareTo(input.getOrigin_City()) == 0 && flightResultList.get(i).getDepartureDate().equals(input.getOnWard_Date())){
                result.addFlight(flightResultList.get(i));
            }
            
            //get all available flights to the destination
            //des -> origin
            if(flightResultList.get(i).getDeparture_City().compareTo(input.getDestination_City()) == 0 && flightResultList.get(i).getDestination_City().compareTo(input.getOrigin_City()) == 0 && flightResultList.get(i).getDepartureDate().equals(input.getOnWard_Date())){
                result.addFlight(flightResultList.get(i));
            }
        }
                
        return result;
    }
    
    public int getSize(){
       return flightResultList.size();
    }
}
