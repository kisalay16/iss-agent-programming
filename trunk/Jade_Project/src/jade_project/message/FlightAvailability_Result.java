/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jade_project.message;

import java.util.Date;

/**
 *
 * @author henry
 */
public class FlightAvailability_Result {
    private String sAirline;

    public void setAirline(String sInput){
            this.sAirline = sInput;
    }
    public String getAirline(){
            return this.sAirline;
    }

    private String sFlightNo;

    public void setFlightNo(String sInput){
            this.sFlightNo = sInput;
    }
    public String getFlightNo(){
            return this.sFlightNo;
    }
    
    private Date dDeparture_Time;

    public void setDeparture_Time(Date dInput){
        this.dDeparture_Time = dInput;
    }
    public Date getDeparture_Time(){
        return this.dDeparture_Time;
    }
    
    private Date dArrival_Time;

    public void setArrival_Time(Date dInput){
        this.dArrival_Time = dInput;
    }
    public Date getArrival_Time(){
        return this.dArrival_Time;
    }
    
    private String sStop_Over;

    public void setStop_Over(String sInput){
        this.sStop_Over = sInput;
    }
    public String getStop_Over(){
        return this.sStop_Over;
    }
    
    private Double dTotal_AirFare;

    public void setTotal_AirFare(Double dInput){
        this.dTotal_AirFare = dInput;
    }
    public Double getTotal_AirFare(){
        return this.dTotal_AirFare;
    }
}
