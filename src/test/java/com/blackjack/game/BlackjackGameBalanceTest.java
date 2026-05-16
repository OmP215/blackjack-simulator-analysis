package com.blackjack.game;

import com.blackjack.model.GameResult;
import com.blackjack.player.AlgorithmPlayer;
import com.blackjack.player.HumanPlayer;
import com.blackjack.strategy.BasicStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

class BlackjackGameBalanceTest {

    private BlackjackGame game;
    private AlgorithmPlayer player;

    @BeforeEach
    void setUp() {
        game = new BlackjackGame();
        player = new AlgorithmPlayer("TestBot", new BasicStrategy());
    }

    // Balance Update on Outcomes

    @Test
    void testBalanceDecreasesOnLoss() {
        double initialBalance = player.getBalance();
        double bet = 50.0;

        // Simulate a loss by updating balance manually
        player.updateBalance(-bet);

        assertEquals(initialBalance - bet, player.getBalance());
    }

    @Test
    void testBalanceIncreasesOnWin() {
        double initialBalance = player.getBalance();
        double bet = 50.0;

        // Simulate a win by updating balance manually
        player.updateBalance(bet);

        assertEquals(initialBalance + bet, player.getBalance());
    }

    @Test
    void testBalanceUnchangedOnPush() {
        double initialBalance = player.getBalance();

        // Simulate a push (no balance change)
        player.updateBalance(0);

        assertEquals(initialBalance, player.getBalance());
    }

    @Test
    void testBalanceIncreasesCorrectlyOnBlackjack() {
        double initialBalance = player.getBalance();
        double bet = 50.0;
        double blackjackWinnings = bet * 1.5;

        // Simulate blackjack win
        player.updateBalance(blackjackWinnings);

        assertEquals(initialBalance + blackjackWinnings, player.getBalance());
    }

    // Bet Validation Tests

    @Test
    void testPlayerCannotBetMoreThanBalance() {
        player.setBalance(25.0);
        assertFalse(player.canPlaceBet(100.0));
    }

    @Test
    void testPlayerCanBetEqualToBalance() {
        player.setBalance(100.0);
        assertTrue(player.canPlaceBet(100.0));
    }

    @Test
    void testPlayerCanBetLessThanBalance() {
        player.setBalance(100.0);
        assertTrue(player.canPlaceBet(50.0));
    }

    @Test
    void testPlayerCannotBetWhenZeroBalance() {
        player.setBalance(0.0);
        assertFalse(player.canPlaceBet(10.0));
    }

    // Bankruptcy Tests

    @Test
    void testPlayerBankruptAfterSignificantLoss() {
        player.setBalance(100.0);
        player.updateBalance(-100.0);
        assertEquals(0.0, player.getBalance());
    }

    @Test
    void testPlayerCannotPlayAfterBankruptcy() {
        player.setBalance(0.0);
        assertFalse(player.canPlaceBet(10.0));
    }

    @Test
    void testPlayerCanRecoverFromNearBankruptcy() {
        player.setBalance(10.0);
        player.updateBalance(490.0);
        assertEquals(500.0, player.getBalance());
    }

    // Multiple Rounds Balance Tests

    @Test
    void testBalanceAfterMultipleRounds() {
        player.setBalance(500.0);

        // Simulate a series of hands
        player.updateBalance(-50.0);  // Loss
        player.updateBalance(75.0);   // Win
        player.updateBalance(-25.0);  // Loss
        player.updateBalance(100.0);  // Win

        assertEquals(600.0, player.getBalance());
    }

    @Test
    void testBalanceTracksNetProfit() {
        player.setBalance(500.0);
        double totalWins = 250.0;
        double totalLosses = 100.0;

        player.updateBalance(totalWins);
        player.updateBalance(-totalLosses);

        assertEquals(500.0 + totalWins - totalLosses, player.getBalance());
    }

    @Test
    void testPlayerStopsPlayingAtZeroBalance() {
        player.setBalance(100.0);
        player.updateBalance(-100.0);

        assertEquals(0.0, player.getBalance());
        assertFalse(player.canPlaceBet(10.0));
    }
}