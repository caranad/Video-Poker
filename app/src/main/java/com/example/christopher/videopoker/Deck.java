package com.example.christopher.videopoker;

import java.util.*;

public class Deck
{
    private Card[] deck = new Card[52];
    private final String[] suits = {"HEARTS", "DIAMONDS", "SPADES", "CLUBS"};
    private Random r;

    public Deck()
    {
        int counter = 0;

        for (int j = 1; j < 14; j++)
        {
            for (String s : this.suits)
            {
                if (j == 1)
                {
                    Card c = new Card("ACE", s, j);
                    deck[counter] = c;
                }
                else if (j == 11)
                {
                    Card c = new Card("JACK", s, j);
                    deck[counter] = c;
                }
                else if (j == 12)
                {
                    Card c = new Card("QUEEN", s, j);
                    deck[counter] = c;
                }
                else if (j == 13)
                {
                    Card c = new Card("KING", s, j);
                    deck[counter] = c;
                }
                else
                {
                    Card c = new Card(Integer.toString(j), s, j);
                    deck[counter] = c;
                }

                counter++;
            }
        }
    }

    public void shuffleDeck()
    {
        int c = 0;
        r = new Random();

        while (c < 200)
        {
            int a = r.nextInt(52);
            int b = r.nextInt(52);

            Card x = this.getCard(a);
            this.deck[a] = this.deck[b];
            this.deck[b] = x;
            c++;
        }
    }

    public Card[] getDeck()
    {
        return this.deck;
    }

    public Card getCard(int i)
    {
        return this.deck[i];
    }

    public void showDeck()
    {
        for (int i = 0; i < this.deck.length; i++)
        {
            System.out.println(deck[i]);
        }
    }
}