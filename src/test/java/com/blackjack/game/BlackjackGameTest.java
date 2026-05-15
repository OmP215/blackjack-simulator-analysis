package com.blackjack.game;

import com.blackjack.model.Card;
import com.blackjack.model.GameResult;
import com.blackjack.model.Hand;
import com.blackjack.player.HumanPlayer;
import com.blackjack.player.AlgorithmPlayer;
import com.blackjack.player.Player;
import com.blackjack.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class BlackjackGameTest {

    private BlackjackGame game;

    @BeforeEach
    void setUp() {
        game = new BlackjackGame();
    }

    @Test
    void testAlgorithmRoundReturnsOutcome() {
        AlgorithmPlayer bot = new AlgorithmPlayer("TestBot", new BasicStrategy());
        GameResult.Outcome outcome = game.playAlgorithmRound(bot);
        assertNotNull(outcome);
    }

    @Test
    void testAlgorithmRoundRecordsHand() {
        AlgorithmPlayer bot = new AlgorithmPlayer("TestBot", new BasicStrategy());
        game.playAlgorithmRound(bot);
        assertEquals(1, bot.getGameResult().getHandsPlayed());
    }

    @Test
    void testOutcomeCountsAddUp() {
        AlgorithmPlayer bot = new AlgorithmPlayer("TestBot", new BasicStrategy());
        int rounds = 100;
        for (int i = 0; i < rounds; i++) {
            game.playAlgorithmRound(bot);
        }
        GameResult result = bot.getGameResult();
        //wins(+bj) + losses + pushes should equal hands played
        //sidenote: bj's are counted as wins and bj, so
        // wins +losses + pushes >= handsPlayed becuase of splits recording 2 outcomes
        assertTrue(result.getHandsPlayed() >= rounds);
        assertTrue(result.getWins() + result.getLosses() + result.getPushes() >= result.getHandsPlayed());
    }

    @Test
    void testPlayRoundWithAlwaysStandStrategy() {
        AlgorithmPlayer bot = new AlgorithmPlayer("StandBot", new BlackjackStrategy() {
            @Override
            public Action decide(Hand hand, Card dealerUp) {
                return Action.STAND;
            }

            @Override
            public double getBetSize(double min, double max) {
                return min;
            }
        });
        GameResult.Outcome outcome = game.playAlgorithmRound(bot);
        assertNotNull(outcome);
        assertEquals(1, bot.getGameResult().getHandsPlayed());
    }

    @Test
    void testPlayRoundWithAlwaysHitStrategy() {
        AlgorithmPlayer bot = new AlgorithmPlayer("HitBot", new BlackjackStrategy() {
            @Override
            public Action decide(Hand hand, Card dealerUp) {
                return Action.HIT;
            }

            @Override
            public double getBetSize(double min, double max) {
                return min;
            }
        });
        GameResult.Outcome outcome = game.playAlgorithmRound(bot);
        assertNotNull(outcome);
    }

    @Test
    void testDealerHandIsAvailable() {
        assertNotNull(game.getDealerHand());
    }

    @Test
    void testDealCardReducesDeck() {
        Card card = game.dealCard();
        assertNotNull(card);
    }

    @Test
    void testMinBetAndMaxBet() {
        assertTrue(game.getMinBet() > 0);
        assertTrue(game.getMaxBet() > game.getMinBet());
    }

    @Test
    void testHumanPlayerRoundWithCallback() {
        //simulate a human always standing
        HumanPlayer player = new HumanPlayer("Tester",
                new Scanner(new ByteArrayInputStream("100\n".getBytes())));

        GameResult.Outcome outcome = game.playRound(player,
                (hand, dealerUp) -> BlackjackStrategy.Action.STAND);

        assertNotNull(outcome);
        assertEquals(1, player.getGameResult().getHandsPlayed());
    }

}
