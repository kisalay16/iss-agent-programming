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
	private String sOrigin_Country;
	
	public void setOrigin_Country(String sInput){
		this.sOrigin_Country = sInput;
	}
	public String getOrgin_Country(){
		return this.sOrigin_Country;
	}
	
	private String sOrigin_City;
	
	public void setOrigin_City(String sInput){
		this.sOrigin_City = sInput;
	}
	public String getOrigin_City(){
		return this.sOrigin_City;
	}
	
	private String sDestination_Country;
	
	public void setDestination_Country(String sInput){
		this.sDestination_Country = sInput;
	}
	public String getDestination_Country(){
		return this.sDestination_Country;
	}
	
	private String sDestination_City;
	
	public void setDestination_City(String sInput){
		this.sDestination_City = sInput;
	}
	public String getDestination_City(){
		return this.sDestination_City;
	}
	
	private Date dOnWard_Date;
	
	public void setOnWard_Date(Date dInput){
		this.dOnWard_Date = dInput;
	}
	public Date getOnWard_Date(){
		return this.dOnWard_Date;
	}
	
	private Date dReturn_Date;
	
	public void setReturn_Date(Date dInput){
		this.dReturn_Date = dInput;
	}
	public Date getReturn_Date(){
		return this.dReturn_Date;
	}
	
	private Integer iNo_Of_Travellers;
	
	public void setNo_Of_Travellers(Integer iInput){
		this.iNo_Of_Travellers = iInput;
	}
	public Integer getNo_Of_Travellers(){
		return this.iNo_Of_Travellers;
	}
	
	private Float fBudget;
	
	public void setBudget(Float fInput){
		this.fBudget = fInput;
	}
	public Float getBudget(){
		return this.fBudget;
	}
}

