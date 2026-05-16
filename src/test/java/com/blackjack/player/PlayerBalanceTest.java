package com.blackjack.player;

import com.blackjack.strategy.BasicStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

class PlayerBalanceTest {

    private HumanPlayer humanPlayer;
    private AlgorithmPlayer algorithmPlayer;

    @BeforeEach
    void setUp() {
        humanPlayer = new HumanPlayer("TestPlayer", new Scanner(new ByteArrayInputStream("".getBytes())));
        algorithmPlayer = new AlgorithmPlayer("TestBot", new BasicStrategy());
    }

    // HumanPlayer Balance Tests

    @Test
    void testHumanPlayerStartsWithBalance() {
        assertEquals(500.0, humanPlayer.getBalance());
    }

    @Test
    void testHumanPlayerCanGetBalance() {
        double balance = humanPlayer.getBalance();
        assertNotNull(balance);
        assertTrue(balance > 0);
    }

    @Test
    void testHumanPlayerCanSetBalance() {
        humanPlayer.setBalance(250.0);
        assertEquals(250.0, humanPlayer.getBalance());
    }

    @Test
    void testHumanPlayerSetBalanceNeverGoesNegative() {
        humanPlayer.setBalance(-100.0);
        assertEquals(0.0, humanPlayer.getBalance());
    }

    @Test
    void testHumanPlayerCanUpdateBalancePositive() {
        humanPlayer.updateBalance(100.0);
        assertEquals(600.0, humanPlayer.getBalance());
    }

    @Test
    void testHumanPlayerCanUpdateBalanceNegative() {
        humanPlayer.updateBalance(-50.0);
        assertEquals(450.0, humanPlayer.getBalance());
    }

    @Test
    void testHumanPlayerUpdateBalanceNeverGoesNegative() {
        humanPlayer.updateBalance(-1000.0);
        assertEquals(0.0, humanPlayer.getBalance());
    }

    @Test
    void testHumanPlayerCanPlaceBetWithSufficientBalance() {
        humanPlayer.setBalance(100.0);
        assertTrue(humanPlayer.canPlaceBet(50.0));
    }

    @Test
    void testHumanPlayerCannotPlaceBetWithInsufficientBalance() {
        humanPlayer.setBalance(25.0);
        assertFalse(humanPlayer.canPlaceBet(50.0));
    }

    @Test
    void testHumanPlayerCanPlaceBetWithExactBalance() {
        humanPlayer.setBalance(100.0);
        assertTrue(humanPlayer.canPlaceBet(100.0));
    }

    @Test
    void testHumanPlayerCannotPlaceBetWhenZeroBalance() {
        humanPlayer.setBalance(0.0);
        assertFalse(humanPlayer.canPlaceBet(10.0));
    }

    // AlgorithmPlayer Balance Tests

    @Test
    void testAlgorithmPlayerStartsWithBalance() {
        assertEquals(500.0, algorithmPlayer.getBalance());
    }

    @Test
    void testAlgorithmPlayerCanGetBalance() {
        double balance = algorithmPlayer.getBalance();
        assertNotNull(balance);
        assertTrue(balance > 0);
    }

    @Test
    void testAlgorithmPlayerCanSetBalance() {
        algorithmPlayer.setBalance(300.0);
        assertEquals(300.0, algorithmPlayer.getBalance());
    }

    @Test
    void testAlgorithmPlayerSetBalanceNeverGoesNegative() {
        algorithmPlayer.setBalance(-150.0);
        assertEquals(0.0, algorithmPlayer.getBalance());
    }

    @Test
    void testAlgorithmPlayerCanUpdateBalancePositive() {
        algorithmPlayer.updateBalance(75.0);
        assertEquals(575.0, algorithmPlayer.getBalance());
    }

    @Test
    void testAlgorithmPlayerCanUpdateBalanceNegative() {
        algorithmPlayer.updateBalance(-125.0);
        assertEquals(375.0, algorithmPlayer.getBalance());
    }

    @Test
    void testAlgorithmPlayerUpdateBalanceNeverGoesNegative() {
        algorithmPlayer.updateBalance(-600.0);
        assertEquals(0.0, algorithmPlayer.getBalance());
    }

    @Test
    void testAlgorithmPlayerCanPlaceBetWithSufficientBalance() {
        algorithmPlayer.setBalance(200.0);
        assertTrue(algorithmPlayer.canPlaceBet(100.0));
    }

    @Test
    void testAlgorithmPlayerCannotPlaceBetWithInsufficientBalance() {
        algorithmPlayer.setBalance(40.0);
        assertFalse(algorithmPlayer.canPlaceBet(100.0));
    }

    @Test
    void testAlgorithmPlayerCanPlaceBetWithExactBalance() {
        algorithmPlayer.setBalance(50.0);
        assertTrue(algorithmPlayer.canPlaceBet(50.0));
    }

    @Test
    void testAlgorithmPlayerCannotPlaceBetWhenZeroBalance() {
        algorithmPlayer.setBalance(0.0);
        assertFalse(algorithmPlayer.canPlaceBet(10.0));
    }

    // Bet Placement Tests with Balance Constraints

    @Test
    void testAlgorithmPlayerPlaceBetClampedToBalance() {
        algorithmPlayer.setBalance(50.0);
        double bet = algorithmPlayer.placeBet(10.0, 500.0);
        assertTrue(bet <= 50.0);
    }

    @Test
    void testAlgorithmPlayerPlaceBetClampedToMinBet() {
        algorithmPlayer.setBalance(500.0);
        double bet = algorithmPlayer.placeBet(10.0, 500.0);
        assertTrue(bet >= 10.0);
    }

    @Test
    void testAlgorithmPlayerPlaceBetClampedToMaxBet() {
        algorithmPlayer.setBalance(500.0);
        double bet = algorithmPlayer.placeBet(10.0, 100.0);
        assertTrue(bet <= 100.0);
    }

    // Multiple Transaction Tests

    @Test
    void testMultiplePositiveUpdates() {
        humanPlayer.updateBalance(50.0);
        humanPlayer.updateBalance(50.0);
        humanPlayer.updateBalance(50.0);
        assertEquals(650.0, humanPlayer.getBalance());
    }

    @Test
    void testMultipleNegativeUpdates() {
        humanPlayer.updateBalance(-100.0);
        humanPlayer.updateBalance(-100.0);
        humanPlayer.updateBalance(-100.0);
        assertEquals(200.0, humanPlayer.getBalance());
    }

    @Test
    void testMixedUpdates() {
        humanPlayer.updateBalance(100.0);  // 600
        humanPlayer.updateBalance(-150.0); // 450
        humanPlayer.updateBalance(50.0);   // 500
        humanPlayer.updateBalance(-200.0); // 300
        assertEquals(300.0, humanPlayer.getBalance());
    }

    @Test
    void testBalanceClipsAtZeroNotBelowOnMultipleUpdates() {
        humanPlayer.setBalance(100.0);
        humanPlayer.updateBalance(-50.0);  // 50
        humanPlayer.updateBalance(-75.0);  // 0 (would be -25)
        assertEquals(0.0, humanPlayer.getBalance());
    }
}