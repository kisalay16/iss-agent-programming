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
public class CreditCard implements Concept {

    private String cardType;
    private String cardNumber;

    public CreditCard() {
    }

    public CreditCard(String cardType, String cardNumber) {
        this.cardType = cardType;
        this.cardNumber = cardNumber;
    }

    public void SetCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardType() {
        return this.cardType;
    }

    public void SetCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }

    public boolean AreYouHavingThisCard(CreditCard c) {
        boolean havingThisCard = false;

        if (cardType.equalsIgnoreCase(c.getCardType()) && cardNumber.equalsIgnoreCase(c.getCardNumber())) {
            havingThisCard = true;
        }

        return havingThisCard;
    }
}
