/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OntologyCreditCard;

import jade.content.Concept;

/**
 *
 * @author rsoon
 */
public class Address implements Concept {

    private String blockNumber;
    private String streetName;
    private String unitNumber;
    private String postalCode;

    public Address() {
    }

    public Address(String blockNumber, String streetName, String unitNumber, String postalCode) {
        this.blockNumber = blockNumber;
        this.streetName = streetName;
        this.unitNumber = unitNumber;
        this.postalCode = postalCode;

    }

    public void SetBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getBlockNumber() {
        return this.blockNumber;
    }

    public void SetStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetName() {
        return this.streetName;
    }

    public void SetUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getUnitNumber() {
        return this.unitNumber;
    }

    public void SetPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public boolean AreYouHavingThisAddress(Address a) {
        boolean havingThisAddress = false;

        if (blockNumber.equalsIgnoreCase(a.getBlockNumber()) && streetName.equalsIgnoreCase(a.getStreetName())
                && unitNumber.equalsIgnoreCase(a.getUnitNumber()) && postalCode.equalsIgnoreCase(a.getPostalCode())) {
            havingThisAddress = true;
        }

        return havingThisAddress;
    }
}
