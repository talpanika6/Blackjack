package com.example.tal.blackjack.Model;

public class Card
{
    private int rank;
    private int suit;

    public Card(int rank, int suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public String toString() {
        String val;
        // empty string because 1 indexed I dont like this
        String[] suitList = {"", "Clubs", "Diamonds", "Hearts", "Spades"}; // This is kinda dumb
        if(rank == 1) val = "Ace";
        else if(rank == 11) val = "Jack";
        else if(rank == 12) val = "Queen";
        else if(rank == 13) val = "King";
        else val = String.valueOf(rank);
        return val + " of " + suitList[suit];
    }

    public int getRank() {
        return rank;
    }

    public int getSuit() {
        return suit;
    }

    public int getCardIndex() {
        return (rank - 2) + (suit - 1)  * 13;
    }


}
