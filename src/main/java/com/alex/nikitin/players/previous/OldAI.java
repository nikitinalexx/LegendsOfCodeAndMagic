package com.alex.nikitin.players.previous;

import com.alex.nikitin.Card;
import com.alex.nikitin.CardType;

import java.util.*;

public class OldAI {
    public static final int PLAYER_ID = -1;
    private List<Card> myMobsOnBoard = new ArrayList<>();
    private List<Card> opponentMobsOnBoard = new ArrayList<>();
    private List<Card> greenCards = new ArrayList<>();
    private PriorityQueue<Card> handCards = new PriorityQueue<>(Comparator.comparingInt((Card card) -> card.cost).reversed());

    public static void main(String args[]) {


        Scanner in = new Scanner(System.in);
        OldAI oldPlayer = new OldAI();

        // game loop
        while (true) {
            String result = "";
            int myMana = 0;
            for (int i = 0; i < 2; i++) {
                int playerHealth = in.nextInt();
                int playerMana = in.nextInt();
                int playerDeck = in.nextInt();
                int playerRune = in.nextInt();
                int playerDraw = in.nextInt();
                if (i == 0) {
                    myMana = playerMana;
                }
            }
            int opponentHand = in.nextInt();
            int opponentActions = in.nextInt();
            if (in.hasNextLine()) {
                in.nextLine();
            }
            for (int i = 0; i < opponentActions; i++) {
                String cardNumberAndAction = in.nextLine();
            }

            int cardCount = in.nextInt();
            for (int i = 0; i < cardCount; i++) {
                Card card = new Card(
                        in.nextInt(), in.nextInt(),
                        in.nextInt(), in.nextInt(),
                        in.nextInt(), in.nextInt(),
                        in.nextInt(), in.next(),
                        in.nextInt(), in.nextInt(), in.nextInt()
                );
                if (card.cardType == CardType.CREATURE.value) {
                    oldPlayer.addCreatureCard(card);
                }
                if (card.cardType == CardType.GREEN.value) {
                    oldPlayer.addGreenCard(card);
                }

            }

            result += oldPlayer.summonCreatures(myMana);
            result += oldPlayer.applyGreenCards();
            result += oldPlayer.attackCreatures();


            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");
            if (!result.equals("")) {
                System.err.println(result);
                System.out.println(result);
            } else {
                System.out.println("PASS");
            }
        }
    }

    private void addCreatureCard(Card card) {
        if (card.location == 0) {
            handCards.add(card);
        }

        if (card.location == 1) {
            myMobsOnBoard.add(card);
        }
        if (card.location == -1) {
            opponentMobsOnBoard.add(card);
        }
    }

    private String applyGreenCards() {
        String result = "";
        for (int i = 0; i < greenCards.size(); i++) {
            Card greenCard = greenCards.get(i);
            for (int j = 0; j < myMobsOnBoard.size(); j++) {
                if (!myMobsOnBoard.get(j).abilities.contains("G")) {
                    myMobsOnBoard.get(j).abilities += "G";
                    result += useItem(greenCard.instanceId, myMobsOnBoard.get(j).instanceId);
                    break;
                }
            }
        }
        return result;
    }

    private void addGreenCard(Card card) {
        if (card.location == 0) {
            greenCards.add(card);
        }
    }

    private String summonCreatures(int myMana) {
        String result = "";
        Card card;
        while ((card = handCards.poll()) != null) {
            if (card.cost < myMana) {
                result += summon(card.instanceId);
                myMana -= card.cost;
                myMobsOnBoard.add(card);
            }
        }

        Collections.sort(myMobsOnBoard, Comparator.comparingInt((Card cardToShuffle) -> cardToShuffle.attack).reversed());
        return result;
    }

    private String attackCreatures() {
        String result = "";
        for (int i = 0; i < myMobsOnBoard.size(); i++) {
            Card myMob = myMobsOnBoard.get(i);
            int maxLess = PLAYER_ID;
            int maxHealth = 0;
            for (int j = 0; j < opponentMobsOnBoard.size(); j++) {
                Card enemyBoard = opponentMobsOnBoard.get(j);
                if (enemyBoard.defense <= myMob.attack && enemyBoard.defense > maxHealth && (myMob.defense > enemyBoard.attack || enemyBoard.attack / myMob.defense > 1) && !enemyBoard.abilities.contains("G")
                        || enemyBoard.abilities.contains("G")) {
                    maxHealth = enemyBoard.defense;
                    maxLess = enemyBoard.instanceId;
                }
            }
            result += attack(myMob.instanceId, maxLess);
            result += attack(myMob.instanceId, PLAYER_ID);//in case the creature to attack already dead.
        }
        return result;
    }

    private static String attack(int attacker, int defender) {
        return String.format("ATTACK %s %s;", attacker, defender);
    }

    private static String summon(int whoToSummon) {
        return String.format("SUMMON %s;", whoToSummon);
    }

    private static String useItem(int card, int creature) {
        return String.format("USE %s %s;", card, creature);
    }


}
