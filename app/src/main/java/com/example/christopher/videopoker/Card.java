package com.example.christopher.videopoker;

import java.io.Serializable;

public class Card implements Serializable
{
    private String number;
    private String suit;
    private int rank;

    public Card(String n, String s, int r)
    {
        this.number = n;
        this.suit = s;
        this.rank = r;
    }

    public int getRank()
    {
        return this.rank;
    }

    public void setRank(int r)
    {
        this.rank = r;
    }

    public String getNum()
    {
        return this.number;
    }

    public String getSuit()
    {
        return this.suit;
    }

    public void setNum(String n)
    {
        this.number = n;
    }

    public void setSuit(String s)
    {
        this.suit = s;
    }

    @Override
    public String toString()
    {
        return this.number + " OF " + this.suit;
    }
}
