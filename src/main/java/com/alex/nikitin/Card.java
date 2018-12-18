package com.alex.nikitin;

public class Card {
    public int cardNumber;
    public int instanceId;
    public int location;
    public int cardType;
    public int cost;
    public int attack;
    public int defense;
    public String abilities;
    public int myHealthChange;
    public int opponentHealthChange;
    public int cardDraw;

    public Card(int cardNumber, int instanceId, int location, int cardType, int cost, int attack, int defense, String abilities, int myHealthChange, int opponentHealthChange, int cardDraw) {
        this.cardNumber = cardNumber;
        this.instanceId = instanceId;
        this.location = location;
        this.cardType = cardType;
        this.cost = cost;
        this.attack = attack;
        this.defense = defense;
        this.abilities = abilities;
        this.myHealthChange = myHealthChange;
        this.opponentHealthChange = opponentHealthChange;
        this.cardDraw = cardDraw;
    }

}
