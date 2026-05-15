package com.blackjack.strategy;

import com.blackjack.model.Card;
import com.blackjack.model.Hand;

public class BasicStrategy implements BlackjackStrategy {

    @Override
    public Action decide(Hand playerHand, Card dealerUpCard) {
        int playerVal = playerHand.getValue();
        int dealerVal = dealerUpCard.getValue();

        if (playerHand.canSplit()) {
            Action splitAction = decideSplit(playerHand, dealerVal);
            if (splitAction == Action.SPLIT) { return Action.SPLIT; }
        }

        if (playerHand.isSoft()) {
            return decideSoftHand(playerVal, dealerVal, playerHand.canDoubleDown());
        }
       return decideHardHand(playerVal, dealerVal, playerHand.canDoubleDown());
    }

    private Action decideSplit(Hand playerHand, int dealerVal) {
        Card.Rank pairRank = playerHand.getCards().get(0).getRank();

        return switch (pairRank) {
            case ACE, EIGHT -> Action.SPLIT;
            case NINE -> {
                if (dealerVal != 7 && dealerVal != 10 && dealerVal != 11) {
                    yield Action.SPLIT;
                }
                yield Action.STAND;
            }
            case SEVEN, THREE, TWO -> {
                if (dealerVal >= 2 && dealerVal <= 7) {
                    yield Action.SPLIT;
                }
                yield Action.HIT;
            }
            case SIX -> {
                if (dealerVal >= 2 && dealerVal <= 6) {
                    yield Action.SPLIT;
                }
                yield Action.HIT;
            }
            case FOUR -> {
                if (dealerVal == 5 || dealerVal == 6) {
                    yield Action.SPLIT;
                }
                yield Action.HIT;
            }
            case FIVE -> Action.STAND;
            case TEN, JACK, QUEEN, KING -> Action.STAND;
            default -> Action.HIT;
        };
    }

    private Action decideSoftHand(int playerVal, int dealerVal, boolean canDouble) {
        //soft 20 -always stand
        if (playerVal >= 20) { return Action.STAND; }

        //soft 19 double if dealer has 6
        if (playerVal == 19) {
            if (canDouble && dealerVal == 6) { return Action.DOUBLE_DOWN; }
            return Action.STAND;
        }

        //soft 18
        if (playerVal == 18) {
            if (canDouble && dealerVal >= 2 && dealerVal <= 6) { return Action.DOUBLE_DOWN; }

            if (dealerVal == 2 || (dealerVal >=7 && dealerVal <= 8)) { return Action.STAND; }
            return Action.HIT;
        }

        //soft 17
        if (playerVal == 17) {
            if (canDouble && dealerVal >= 3 && dealerVal <= 6) { return Action.DOUBLE_DOWN; }
            return Action.HIT;
        }

        //soft 15+16
        if (playerVal == 15 || playerVal == 16) {
            if (canDouble && dealerVal >= 4 && dealerVal <= 6) { return Action.DOUBLE_DOWN; }
            return Action.HIT;
        }

        //soft 13-14
        if (canDouble && dealerVal >= 5 && dealerVal <= 6) { return Action.DOUBLE_DOWN; }


        return Action.HIT;
    }

    private Action decideHardHand(int playerVal, int dealerVal, boolean canDouble) {
        if (playerVal >= 17) { return Action.STAND; }
        if (playerVal >= 13 && dealerVal <=6 ) { return Action.STAND; }
        if (playerVal == 12 && dealerVal >= 4 && dealerVal <= 6) { return Action.STAND; }
        if (playerVal == 11) { return canDouble ? Action.DOUBLE_DOWN : Action.HIT; }
        if (playerVal == 10 && canDouble && dealerVal <= 9) { return Action.DOUBLE_DOWN; }
        if (playerVal == 9 && canDouble && dealerVal >= 3 && dealerVal <= 6) { return  Action.DOUBLE_DOWN; }
        return Action.HIT;
    }

    @Override
    public double getBetSize(double minBet, double maxBet) { return minBet; }

    @Override
    public String toString() { return "Basic Strategy"; }
}
