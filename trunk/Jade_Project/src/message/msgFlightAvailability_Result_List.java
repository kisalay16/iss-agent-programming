/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

import jade.util.leap.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author henry
 */
public class msgFlightAvailability_Result_List implements Serializable{
    private ArrayList flightResultList = new ArrayList();
    
    public msgFlightAvailability_Result_List(){
        flightResultList = new ArrayList<msgFlightAvailability_Result>();
    }
    
    //to add new flight
    public void addFlight(msgFlightAvailability_Result input){
        flightResultList.add(input);
    }
    
    //to get a particular flight detail
    public msgFlightAvailability_Result getByIndex(Integer i){
        return  (msgFlightAvailability_Result)flightResultList.get(i);
    }
    
    //to get all flights that suit the criteria
    public msgFlightAvailability_Result_List getFlightsAccordingToSpecs(msgReqFlightAvailability input){
        
        msgFlightAvailability_Result_List result = new msgFlightAvailability_Result_List();
        
        for(int i = 0; i < flightResultList.size(); i++){
            //get all available flights to the destination
            //orgin -> des
            if(getByIndex(i).getDeparture_City().compareTo(input.getOrigin_City()) == 0 && getByIndex(i).getDestination_City().compareTo(input.getDestination_City()) == 0 && getByIndex(i).getAirFareTotal() <= input.getBudget() && getByIndex(i).getIsTaken() == false){
                result.addFlight(getByIndex(i));
            }
            
            //get all available flights to the destination
            //des -> origin
            if(getByIndex(i).getDeparture_City().compareTo(input.getDestination_City()) == 0 && getByIndex(i).getDestination_City().compareTo(input.getOrigin_City()) == 0 && getByIndex(i).getAirFareTotal() <= input.getBudget()){
                result.addFlight(getByIndex(i));
            }
        }
                
        return result;
    }
    
    public int getSize(){
       return flightResultList.size();
    }
    
    //public Boolean checkIfFlightIsBooked(String sFlightNo){
    public Boolean bookFlight(String sFlightNo){
        
        for(int i = 0; i < getSize(); i++){
            if(getByIndex(i).getFlightID().compareTo(sFlightNo) == 0){
                if(getByIndex(i).getIsTaken() == false){
                    getByIndex(i).setIsTaken(true);
                    return true;
                }
            }
        }
        return false;
        
    }
}
