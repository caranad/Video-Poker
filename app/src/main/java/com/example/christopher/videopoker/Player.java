package com.example.christopher.videopoker;

import java.io.Serializable;
import java.util.*;

public class Player
{
    private Card[] hand;
    private Random r;
    private int cash;

    public Player(int cash)
    {
        this.hand = new Card[5];
        r = new Random();
        this.cash = cash;
    }

    public int getCash()
    {
        return this.cash;
    }

    public void placeBet(int c)
    {
        this.cash = this.cash - c;
    }

    public void addCash(int c)
    {
        this.cash = this.cash + c;
    }

    public Card[] getHand()
    {
        return this.hand;
    }

    public Card getCard(int i)
    {
        return hand[i];
    }

    public void setCard(int i, Card c)
    {
        hand[i] = c;
    }

    public void setCard(int i, String c)
    {
        String[] x = c.split(" ");

        for (int j = 0; j < x.length; j++)
        {
            System.out.println(x[j]);
        }

        // Make card
        if (x[0].equals("ACE"))
        {
            hand[i] = new Card("ACE", x[2], 1);
        }
        else if (x[0].equals("2"))
        {
            hand[i] = new Card("2", x[2], 2);
        }
        else if (x[0].equals("3"))
        {
            hand[i] = new Card("3", x[2], 3);
        }
        else if (x[0].equals("4"))
        {
            hand[i] = new Card("4", x[2], 4);
        }
        else if (x[0].equals("5"))
        {
            hand[i] = new Card("5", x[2], 5);
        }
        else if (x[0].equals("6"))
        {
            hand[i] = new Card("6", x[2], 6);
        }
        else if (x[0].equals("7"))
        {
            hand[i] = new Card("7", x[2], 7);
        }
        else if (x[0].equals("8"))
        {
            hand[i] = new Card("8", x[2], 8);
        }
        else if (x[0].equals("9"))
        {
            hand[i] = new Card("9", x[2], 9);
        }
        else if (x[0].equals("10"))
        {
            hand[i] = new Card("10", x[2], 10);
        }
        else if (x[0].equals("JACK"))
        {
            hand[i] = new Card("JACK", x[2], 11);
        }
        else if (x[0].equals("QUEEN"))
        {
            hand[i] = new Card("QUEEN", x[2], 12);
        }
        else if (x[0].equals("KING"))
        {
            hand[i] = new Card("KING", x[2], 13);
        }
        else
        {
            hand[i] = new Card("NULL", x[2], 0);
        }
    }

    public void dealCards(Deck d)
    {
        for (int i = 0; i < 5; i++)
        {
            hand[i] = d.getCard(i);
        }
    }

    public String toString()
    {
        String hand = "|| ";

        for (Card c : this.hand)
        {
            hand = hand  + c + " || ";
        }

        return hand;
    }
}
