/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

import java.util.Date;

/**
 *
 * @author henry
 */
public class msgReqFlightAvailability {
    
    public msgReqFlightAvailability(){
        
    }
    
    public msgReqFlightAvailability(msgReqFlightAvailability input){
        this.sOrigin_City = input.getOrigin_City();
        this.sOrigin_Country = input.getOrigin_Country();
        this.sDestination_City = input.getDestination_City();
        this.sDestination_Country = input.getDestination_Country();
        this.dOnWard_Date = input.getOnWard_Date();
        this.dReturn_Date = input.getReturn_Date();
        this.iNo_Of_Traveller = input.getNo_Of_Traveller();
        this.iBudget = input.getBudget();
    }
    
    private String sOrigin_Country;
    
    public String getOrigin_Country(){
        return this.sOrigin_Country;
    }
    public void setOrigin_Country(String sValue){
        this.sOrigin_Country = sValue;
    }
    
    private String sOrigin_City;
    
    public String getOrigin_City(){
        return this.sOrigin_City;
    }
    public void setOrigin_City(String sValue){
        this.sOrigin_City = sValue;
    }
    
    private String sDestination_Country;
    
    public String getDestination_Country(){
        return this.sDestination_Country;
    }
    public void setDestination_Country(String sValue){
        this.sDestination_Country = sValue;
    }
    
    private String sDestination_City;
    
    public String getDestination_City(){
        return this.sDestination_City;
    }
    public void setDestination_City(String sValue){
        this.sDestination_City = sValue;
    }
    
    private Date dOnWard_Date;
    
    public Date getOnWard_Date(){
        return this.dOnWard_Date;
    }
    public void setOnWard_Date(Date dValue){
        this.dOnWard_Date = dValue;
    }
    
    private Date dReturn_Date;
    
    public Date getReturn_Date(){
        return this.dReturn_Date;
    }
    public void setReturn_Date(Date dValue){
        this.dReturn_Date = dValue;
    }
    
    private Integer iNo_Of_Traveller;
    
    public Integer getNo_Of_Traveller(){
        return this.iNo_Of_Traveller;
    }
    public void setNo_Of_Traveller(Integer iValue){
        this.iNo_Of_Traveller = iValue;
    }
    
    private Integer iBudget;
    
    public Integer getBudget(){
        return this.iBudget;
    }
    public void setBudget(Integer iValue){
        this.iBudget = iValue;
    }
}
