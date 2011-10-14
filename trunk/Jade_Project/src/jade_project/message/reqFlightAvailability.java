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
public class reqFlightAvailability {
    private String sOrigin_City;
    
    public String getOrigin_City(){
        return this.sOrigin_City;
    }
    public void setOrigin_City(String sInput){
        this.sOrigin_City = sInput;
    }
    
    private String sOrigin_Country;
    
    public String getOrigin_Country(){
        return this.sOrigin_Country;
    }
    public void setOrigin_Country(String sInput){
        this.sOrigin_Country = sInput;
    }
    
    private String sDestination_City;
    
    public String getDestination_City(){
        return this.sDestination_City;
    }
    public void setDestination_City(String sInput){
        this.sDestination_City = sInput;
    }
    
    private String sDestination_Country;
    
    public String getDestination_Country(){
        return this.sDestination_Country;
    }
    public void setDestination_Country(String sInput){
        this.sDestination_Country = sInput;
    }
    
    private Date dOnWard_Date;
    
    public Date getOnWard_Date(){
        return this.dOnWard_Date;
    }
    public void setOnWard_Date(Date dInput){
        this.dOnWard_Date = dInput;
    }
    
    private Date dReturn_Date;
    
    public Date getReturn_Date(){
        return this.dReturn_Date;
    }
    public void setReturn_Date(Date dInput){
        this.dReturn_Date = dInput;
    }
    
    private Integer iNoOfTraveller;
    
    public Integer getNoOfTraveller(){
        return this.iNoOfTraveller;
    }
    public void setNoOfTraveller(Integer iInput){
        this.iNoOfTraveller = iInput;
    }
    
    private Double dBudget;
    
    public Double getBudget(){
        return this.dBudget;
    }
    public void setBudget(Double dInput){
        this.dBudget = dInput;
    }
}
