import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import com.alex.nikitin.players.current.ActualAI;
import com.alex.nikitin.players.previous.OldAI;
import com.codingame.game.engine.Constants;
import com.codingame.gameengine.runner.MultiplayerGameRunner;
import com.codingame.gameengine.runner.dto.GameResult;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        boolean runMultipleTimes = true;

        if (runMultipleTimes) {
            //1764 1638
            AtomicInteger firstWon = new AtomicInteger(0);
            AtomicInteger secondWon = new AtomicInteger(0);
            goMultiple(ActualAI.class, OldAI.class, firstWon, secondWon);
            goMultiple(OldAI.class, ActualAI.class, secondWon, firstWon);
            while (true) {
                Thread.sleep(1000);
                System.out.println("First " + firstWon.intValue() + ". Second " + secondWon.intValue());
            }
        } else {
            goSingle();
        }
    }

    private static void goSingle() {
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

        Properties gameParameters = new Properties();
        // set game parameters here
        //        gameRunner.setSeed(1279960l);
        //        gameParameters.setProperty("draftChoicesSeed", "-5113144502819146988");
        //        gameParameters.setProperty("shufflePlayer0Seed", "127");
        //        gameParameters.setProperty("shufflePlayer1Seed", "333");
        //        gameParameters.setProperty("predefinedDraftIds", "91 92 93,94 95 96,97 98 99,100 101 102,103 104 105,106 107 108,109 110 111,112 113 114,115 116 117,118 119 120,121 122 123,124 125 126,127 128 129,130 131 132,133 134 135,136 137 138,139 140 141,142 143 144,145 146 147,148 149 150,151 152 153,154 155 156,157 158 159,160 160 160,160 160 160,160 160 160,160 160 160,160 160 160,160 160 160,160 160 160");
        gameRunner.setGameParameters(gameParameters);

        gameRunner.addAgent(ActualAI.class);
        gameRunner.addAgent(OldAI.class);

        Constants.VERBOSE_LEVEL = 2;

        //set ruleset here
        System.setProperty("league.level", "4");

        gameRunner.start();
    }

    private static void goMultiple(Class firstPlayer, Class secondPlayer, AtomicInteger firstWon, AtomicInteger secondWon) throws InterruptedException {
        for (int t = 0; t < 50; t++) {
            Thread thread = new Thread(() -> {
                for (int i = 0; i < 50; i++) {
                    MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();

                    Properties gameParameters = new Properties();
                    // set game parameters here
                    //        gameRunner.setSeed(1279960l);
                    //        gameParameters.setProperty("draftChoicesSeed", "-5113144502819146988");
                    //        gameParameters.setProperty("shufflePlayer0Seed", "127");
                    //        gameParameters.setProperty("shufflePlayer1Seed", "333");
                    //        gameParameters.setProperty("predefinedDraftIds", "91 92 93,94 95 96,97 98 99,100 101 102,103 104 105,106 107 108,109 110 111,112 113 114,115 116 117,118 119 120,121 122 123,124 125 126,127 128 129,130 131 132,133 134 135,136 137 138,139 140 141,142 143 144,145 146 147,148 149 150,151 152 153,154 155 156,157 158 159,160 160 160,160 160 160,160 160 160,160 160 160,160 160 160,160 160 160,160 160 160");
                    gameRunner.setGameParameters(gameParameters);

                    gameRunner.addAgent(firstPlayer);
                    gameRunner.addAgent(secondPlayer);

                    Constants.VERBOSE_LEVEL = 0;

                    //set ruleset here
                    System.setProperty("league.level", "4");


                    GameResult simulate = gameRunner.simulate();
                    if (simulate.scores.get(0) == 1) {
                        firstWon.addAndGet(1);
                    }
                    if (simulate.scores.get(1) == 1) {
                        secondWon.addAndGet(1);
                    }
                }
            });
            thread.start();
        }
    }

}
