package com.alex.nikitin.players.current;

import com.alex.nikitin.Card;
import com.alex.nikitin.CardType;

import java.util.*;
import java.util.stream.Collectors;


public class ActualAI {
    public static final int PLAYER_ID = -1;
    public static final int NEED_ACTION_CARDS = 1;
    public static final int NEED_CREATURE_CARDS = 25;

    private List<Card> myMobsOnBoard = new ArrayList<>();
    private List<Card> handCardsFromExpensiveToLessExpensive = new ArrayList<>();
    private Map<Integer, Card> opponentMobsOnBoard = new HashMap<>();
    private HashMap<CardType, List<Card>> actionCardsByType = new HashMap<>();

    private HashMap<Integer, List<Card>> draftCreatureCards = new HashMap<>();//only for draft
    private HashMap<CardType, List<Card>> draftActionCards = new HashMap<>();//only for draft

    private List<Card> currentDraftCards = new ArrayList<>();
    private boolean draftPhase = false;

    private Player myPlayer;
    private Player hisPlayer;

    private StringBuilder log = new StringBuilder();

    public ActualAI(ActualAI previousState) {
        draftCreatureCards = previousState.draftCreatureCards;
        draftActionCards = previousState.draftActionCards;
    }

    public ActualAI() {
        draftActionCards.put(CardType.GREEN, new ArrayList<>());
        draftActionCards.put(CardType.BLUE, new ArrayList<>());
        draftActionCards.put(CardType.RED, new ArrayList<>());

        for (int i = 1; i <= 10; i++) {
            draftCreatureCards.put(i, new ArrayList<>());
        }
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        ActualAI actualAI = new ActualAI();

        // game loop
        while (true) {
            actualAI = new ActualAI(actualAI);
            actualAI.init(in);

            String result = actualAI.calculateResult();

            System.err.println(actualAI.getLogInfo());

            if (!result.equals("")) {
                System.out.println(result);
            } else {
                System.out.println("PASS");
            }
        }
    }

    private String getLogInfo() {
        return log.toString();
    }

    private void init(Scanner in) {
        myPlayer = new Player(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());
        hisPlayer = new Player(in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt(), in.nextInt());

        int opponentHand = in.nextInt();
        int opponentActions = in.nextInt();
        if (in.hasNextLine()) {
            in.nextLine();
        }

        for (int i = 0; i < opponentActions; i++) {
            String cardNumberAndAction = in.nextLine();
        }

        int cardCount = in.nextInt();

        draftPhase = (myPlayer.playerMana == 0);
        for (int i = 0; i < cardCount; i++) {
            Card card = new Card(
                    in.nextInt(), in.nextInt(),
                    in.nextInt(), in.nextInt(),
                    in.nextInt(), in.nextInt(),
                    in.nextInt(), in.next(),
                    in.nextInt(), in.nextInt(), in.nextInt()
            );
            if (draftPhase) {
                currentDraftCards.add(card);
            } else {
                if (card.cardType == CardType.CREATURE.value) {
                    addCreatureCard(card);
                } else {
                    addActionCard(card);
                }
            }
        }
    }

    private String calculateResult() {
        String result = "";
        if (draftPhase) {
            result += performDraft();
        } else {
            result += summonCreatures();
            result += applyGreenCards();
            result += applyRedCards();
            result += applyBlueCards();
            result += attackCreatures();
        }

        return result;
    }

    private String performDraft() {
        int chosenCard = 0;
        for (int i = 0; i < currentDraftCards.size(); i++) {
            Card card = currentDraftCards.get(i);
            if (card.cardType == CardType.CREATURE.value) {
                int cost = card.cost;
                if (cost >= 7) {
                    cost = 7;
                }
                int requiredAmount = cost == 7 ? 5 : cost >= 4 ? 3 : 2;

                List<Card> cards = draftCreatureCards.computeIfAbsent(cost, k -> new ArrayList<>());
                if (cards.size() < requiredAmount || i == 2) {
                    cards.add(card);
                    chosenCard = i;
                    break;
                }
            } else {
                List<Card> actionCards = draftActionCards.computeIfAbsent(CardType.ofValue(card.cardType), k -> new ArrayList<>());
                if (actionCards.size() < NEED_ACTION_CARDS || i == 2) {
                    actionCards.add(card);
                    chosenCard = i;
                    break;
                }
            }
        }

        return "PICK " + chosenCard;
    }

    private void addCreatureCard(Card card) {
        if (card.location == 0) {
            handCardsFromExpensiveToLessExpensive.add(card);
        }

        if (card.location == 1) {
            myMobsOnBoard.add(card);
        }
        if (card.location == -1) {
            opponentMobsOnBoard.put(card.instanceId, card);
        }
    }

    private String applyGreenCards() {
        String result = "";
        List<Card> greenCards = actionCardsByType.get(CardType.GREEN);
        if (greenCards == null) {
            return result;
        }
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

    private String applyRedCards() {
        String result = "";
        List<Card> redCards = actionCardsByType.get(CardType.RED);
        if (redCards == null) {
            return result;
        }
        for (int i = 0; i < redCards.size(); i++) {
            Card redCard = redCards.get(i);
            for (Map.Entry<Integer, Card> instanceIdWithCard : opponentMobsOnBoard.entrySet()) {
                result += useItem(redCard.instanceId, instanceIdWithCard.getKey());
            }
        }
        return result;
    }

    private String applyBlueCards() {
        String result = "";
        List<Card> blueCards = actionCardsByType.get(CardType.BLUE);
        if (blueCards == null) {
            return result;
        }
        for (Card blueCard : blueCards) {
            result += useItem(blueCard.instanceId, PLAYER_ID);
        }
        return result;
    }

    private void addActionCard(Card card) {
        if (card.location == 0) {
            List<Card> cards = actionCardsByType.computeIfAbsent(CardType.ofValue(card.cardType), k -> new ArrayList<>());
            cards.add(card);
        }
    }

    private String summonCreatures() {
        String result = "";
        List<Card> cardsWithGuards = handCardsFromExpensiveToLessExpensive
                .stream()
                .filter(card -> card.abilities.contains("G"))
                .sorted(Comparator.comparingInt((Card card) -> card.cost).reversed())
                .collect(Collectors.toList());

        List<Card> cardsWithoutGuards = handCardsFromExpensiveToLessExpensive
                .stream()
                .filter(card -> !card.abilities.contains("G"))
                .sorted(Comparator.comparingInt((Card card) -> card.cost).reversed())
                .collect(Collectors.toList());

        cardsWithGuards.addAll(cardsWithoutGuards);


        for (Card card : cardsWithGuards) {
            if (card.cost < myPlayer.playerMana) {
                result += summon(card.instanceId);
                myPlayer.playerMana -= card.cost;
                myMobsOnBoard.add(card);
            }
        }


        Collections.sort(myMobsOnBoard, Comparator.comparingInt((Card cardToShuffle) -> cardToShuffle.attack).reversed());
        return result;
    }

    private String attackCreatures() {
        String result = "";
        for (Card myMob : myMobsOnBoard) {
            int chosenMobId = PLAYER_ID;
            int chosenMobDefence = 0;
            boolean guardChosen = false;
            for (Card enemyMob : opponentMobsOnBoard.values()) {
                if (myMob.defense > enemyMob.attack && myMob.attack >= enemyMob.defense && enemyMob.defense > chosenMobDefence && !enemyMob.abilities.contains("G")
                        || enemyMob.abilities.contains("G")) {
                    if (!guardChosen) {
                        chosenMobDefence = enemyMob.defense;
                        chosenMobId = enemyMob.instanceId;
                    }
                    if (enemyMob.abilities.contains("G")) {
                        guardChosen = true;
                    }
                }
            }
            if (!guardChosen) {
                for (Card enemyMob : opponentMobsOnBoard.values()) {
                    if (enemyMob.abilities.contains("L")) {
                        chosenMobId = enemyMob.instanceId;
                    }
                }
            }

            Card chosenMob = opponentMobsOnBoard.get(chosenMobId);
            if (chosenMob != null) {
                chosenMob.defense -= myMob.attack;
                if (chosenMob.defense <= 0) {
                    opponentMobsOnBoard.remove(chosenMobId);
                }
            }

            result += attack(myMob.instanceId, chosenMobId);
            result += attack(myMob.instanceId, PLAYER_ID);
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
