package com.example.tal.blackjack.Model;

import java.util.ArrayList;


public class Player
{
    private String name;
    private boolean isWinner;
    private ArrayList<Card> hand;
    private double chips;
    private double bet;
    private double score;

    public double getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public Player(String name) {
        this.name = name;
        hand = new ArrayList<>();
        this.isWinner=false;
    }

    public Player(String name, double chips,double bet) {
        this.name = name;
        hand = new ArrayList<>();
        this.chips = chips;
        this.bet = bet;
        this.isWinner=false;
    }


    public void takeHand() {
        hand.clear();
    }

    public void giveCard(Card card) {
        hand.add(card);
    }

    public double getBalance() {
        return chips;
    }
    public void setBalance(double chips) {
        this.chips = chips;
    }

    public boolean getWinner(){ return isWinner;}
    public void setWinner(boolean win){this.isWinner=win;}

    public void setBet(double b) {this.bet=b;}
    public double getBet() {return bet;}

    public ArrayList<Card> getHand() {
        return hand;
    }

    public String getName() {
        return name;
    }
}
