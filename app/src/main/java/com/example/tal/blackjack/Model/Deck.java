package com.example.tal.blackjack.Model;


public class Deck
{
    private Card[] cards;
    private int numCards;

    public Deck() {
        cards = new Card[52];
        fill();
        shuffle();
    }

    private void fill() {
        int index = 0;
        for(int r=2; r <= 14; r++)
            for(int s=1; s<=4; s++) {
                cards[index] = new Card(r, s);
                index++;
            }
        numCards = 52;
    }

    private void shuffle() {
        for(int i = 0; i < numCards - 1; i++)  {
            int r = (int)((numCards-i)*Math.random()+i);
            Card temp = cards[i];
            cards[i] = cards[r];
            cards[r] = temp;
        }
    }

    public Card deal() {
        if(numCards == 0){
            fill();
            shuffle();
        }
        numCards--;
        return cards[numCards];
    }

    public void disposeDeck()
    {
        cards=null;
    }

    public int getNumCards() {
        return numCards;
    }
}
