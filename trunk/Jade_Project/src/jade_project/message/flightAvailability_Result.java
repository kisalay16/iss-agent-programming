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
public class flightAvailability_Result {
    private String sAirline;
    
    public String getAirline(){
        return this.sAirline;
    }
    public void setAirline(String sInput){
        this.sAirline = sInput;
    }
    
    private String sFlightNo;
    
    public String getFlightNo(){
        return this.sFlightNo;
    }
    public void setFlightNo(String sInput){
        this.sFlightNo = sInput;
    }
    
    private Date dDeparture_Time;
    
    public Date getDeparture_Time(){
        return this.dDeparture_Time;
    }
    public void setDeparture_Date(Date dInput){
        this.dDeparture_Time = dInput;
    }
    
    private Date dReturn_Time;
    
    public Date getReturn_Time(){
        return this.dReturn_Time;
    }
    public void setReturn_Date(Date dInput){
        this.dDeparture_Time = dInput;
    }
    
    private String sStop_Over;
    
    public String getStop_Over(){
        return this.sStop_Over;
    }
    public void setStop_Over(String sInput){
        this.sStop_Over = sInput;
    }
    
    private Double dAirFare_Total;
    
    public Double getAirFare_Total(){
        return this.dAirFare_Total;
    }
    public void setAirFare_Total(Double dInput){
        this.dAirFare_Total = dInput;
    }
}
