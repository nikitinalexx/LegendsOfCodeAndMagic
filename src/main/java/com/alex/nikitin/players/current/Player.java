package com.alex.nikitin.players.current;

public class Player {
    public int playerHealth;
    public int playerMana;
    public int playerDeck;
    public int playerRune;
    public int playerDraw;
    public boolean inDraftPhase;

    public Player(int playerHealth, int playerMana, int playerDeck, int playerRune, int playerDraw) {
        this.playerHealth = playerHealth;
        this.playerMana = playerMana;
        this.playerDeck = playerDeck;
        this.playerRune = playerRune;
        this.playerDraw = playerDraw;
        inDraftPhase = playerMana == 0;
    }

}
