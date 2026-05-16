package com.blackjack.player;

import com.blackjack.model.Hand;
import com.blackjack.model.GameResult;

public interface Player {

    String getName();

    Hand getHand();

    GameResult getGameResult();

    double placeBet(double minBet, double maxBet);

    void resetHand();

    //balance interface
    double getBalance();
    void setBalance(double amount);
    void updateBalance(double amount);
    boolean canPlaceBet(double bet);
}
