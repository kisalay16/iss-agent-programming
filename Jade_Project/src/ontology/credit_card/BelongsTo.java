/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package OntologyCreditCard;

import jade.content.Predicate;

/**
 *
 * @author rsoon
 */
public class BelongsTo implements Predicate {

    private Person person;
    private CreditCard creditCard;
    
    public BelongsTo(){
        
    }

    public BelongsTo(Person person, CreditCard creditCard) {
        this.person = person;
        this.creditCard = creditCard;
    }

    public void SetPerson(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return this.person;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public CreditCard getCreditCard() {
        return this.creditCard;
    }

    public boolean DoYouHaveThisCard(Person p, Address a, CreditCard c) {
        boolean haveThisCard = false;

        if (person.AreYouThisPerson(p, a) && c.AreYouHavingThisCard(c)) {
            haveThisCard = true;
        }

        return haveThisCard;
    }
}
