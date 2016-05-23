package com.example.tal.blackjack.Logic;

import android.util.Log;

import com.example.tal.blackjack.Model.Card;
import com.example.tal.blackjack.Model.Deck;
import com.example.tal.blackjack.Model.Player;

import java.util.ArrayList;

public class Game
{
    private Deck gameDeck;
    private ArrayList<Player> players;
    private int currPlayer;
    public boolean isHoleFlipped;
    public int cardCounting;
    public boolean isGameOver;

    public Game(String name,double balance,double bet) {
        gameDeck = new Deck();

        players = new ArrayList<>();
        players.add(new Player("Dealer"));
        players.add(new Player(name,balance,bet));


        // dealers hole card is flipped initially
        isHoleFlipped = true;

        // Start with first player that is not dealer
        currPlayer = 1;

        isGameOver=false;

        dealHands();
    }


    public void disposeGame()
    {
        players.clear();
        gameDeck.disposeDeck();

        players=null;
        gameDeck=null;

    }

    public Player scoreHands() {
        Player winner = null;
        double maxScore = Double.MIN_VALUE;
        for(Player p: players) {
            p.setScore(score(p));
            if(p.getScore() > maxScore && p.getScore() <= 21) {
                winner = p;
                winner.setWinner(true);
                maxScore = p.getScore();
            }
        }


        return winner;
    }

    public void updateBalance()
    {
        boolean isWinner;
        double balance;
        double bet;

        Player player =players.get(1);
        balance=  player.getBalance();
        bet= player.getBet();
        isWinner=player.getWinner();

        if(isWinner)
        {
            bet=bet*1.5;
            balance+=bet;
        }
        else
        {
            double temp=balance-bet;
            if(temp<0)
            {
                Log.d("money","Player loose all its money");
            }
            else
                balance=temp;


        }

        player.setBalance(balance);

    }

    /**
     *
     * @param bet
     * @return if bet> curr balance of player
     */
    public boolean checkBetToBalance(double bet)
    {

        double balance;


        Player player =players.get(1);
        balance=  player.getBalance();

        double temp;

        temp=balance-bet;
         if (temp<0)
             return false;
        return true;

    }

    /**
     *
     * @param p Player
     * @return score of Player
     */
    public int score(Player p) {
        int score = 0;
        int numAces = 0;

        for(Card c : p.getHand()) {
            int rank = c.getRank();
            if(rank <= 10)
                score += rank;
            else if(rank <= 13)
                score += 10;
            else {
                score += 11;
                numAces++;
            }
            while(score > 21 && numAces > 0) {
                score -= 10;
                numAces--;
            }
        }
        return score;
    }

    /**
     * Deal cards to players
     */

    public void dealHands() {
        for(Player p : players)
            for(int i = 0; i < 2; i++) {
                Card c = gameDeck.deal();
                countCard(c);
                p.giveCard(c);
            }

        Log.i("Hand Size after deal", "size: " + players.get(0).getHand().size());
    }

    /**
     *  start new Round with new bet
     * @param bet
     */

    public void newGame(double bet) {
        for(Player p : players) {
            p.takeHand();
        }
        // dealers hole card is flipped initially
        isHoleFlipped = true;
        dealHands();
        // Start with first player that is not dealer
        currPlayer = 1;

        isGameOver=false;

        //players set new bet
        Player p=players.get(1);

        p.setBet(bet);

    }

    public void countCard(Card c) {
        if(c.getRank() >=2 && c.getRank() <= 6)
            cardCounting+=1;
        else if(c.getRank() >= 10 && c.getRank() <= 14)
            cardCounting-=1;
        else
            ;
    }

    public void hit() {
        Card c = gameDeck.deal();
        getCurrentPlayer().giveCard(c);
        countCard(c);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void nextPlayer() {
        currPlayer = (++currPlayer)%players.size();
    }

    public Player getCurrentPlayer() {
        return players.get(currPlayer);
    }

}
