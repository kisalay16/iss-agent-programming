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
public class Person implements Concept {

    private String name;
    private Address address;

    public Person() {
    }

    public Person(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getAddress() {
        return this.address;
    }

    public boolean AreYouThisPerson(Person p, Address a) {
        boolean thisPerson = false;

        if (name.equalsIgnoreCase(p.getName()) && address.AreYouHavingThisAddress(a)) {
            thisPerson = true;
        }

        return thisPerson;
    }
}