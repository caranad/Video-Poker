package com.example.christopher.videopoker;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.*;
import android.view.*;
import java.util.*;
import android.media.*;
import java.io.*;
import android.content.*;

public class MainActivity extends Activity
{
    private final int STARTING_COST = 100;
    private final int BET_AMOUNT = 10;

    private ImageView card1, card2, card3, card4, card5;
    private Button rCard1, rCard2, rCard3, rCard4, rCard5;
    private TextView scoreText;
    private ArrayList<Integer> x = new ArrayList<Integer>();
    private Player player;
    private Deck deck;
    private int state = 0;

    private MediaPlayer s;
    private AlertDialog.Builder instructions;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        s = MediaPlayer.create(this, R.drawable.game_sound);
        s.setLooping(true);
        s.start();

        scoreText = (TextView)findViewById(R.id.txtScore);

        card1 = (ImageView)findViewById(R.id.cardSlot1);
        card2 = (ImageView)findViewById(R.id.cardSlot2);
        card3 = (ImageView)findViewById(R.id.cardSlot3);
        card4 = (ImageView)findViewById(R.id.cardSlot4);
        card5 = (ImageView)findViewById(R.id.cardSlot5);

        rCard1 = (Button)findViewById(R.id.btnRemove1);
        rCard2 = (Button)findViewById(R.id.btnRemove2);
        rCard3 = (Button)findViewById(R.id.btnRemove3);
        rCard4 = (Button)findViewById(R.id.btnRemove4);
        rCard5 = (Button)findViewById(R.id.btnRemove5);

        deck = new Deck();

        // Read saved information
        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        state = prefs.getInt("state", 0);

        String a = prefs.getString("a", "NULL OF NULL");
        String b = prefs.getString("b", "NULL OF NULL");
        String c = prefs.getString("c", "NULL OF NULL");
        String d = prefs.getString("d", "NULL OF NULL");
        String e = prefs.getString("e", "NULL OF NULL");

        player = new Player(prefs.getInt("score", STARTING_COST));
        scoreText.setText("Coins left : " + player.getCash() + " coins.");

        player.setCard(0, a);
        player.setCard(1, b);
        player.setCard(2, c);
        player.setCard(3, d);
        player.setCard(4, e);

        setCardImage(card1, 0);
        setCardImage(card2, 1);
        setCardImage(card3, 2);
        setCardImage(card4, 3);
        setCardImage(card5, 4);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void instructionsDialog(View v)
    {
        instructions = new AlertDialog.Builder(this);
        instructions.setTitle("INSTRUCTIONS");
        instructions.setMessage("To play Video Poker you have to select the cards that you want to remove and make a poker hand." +
                "\n After removing several cards, new cards will be dealt from the top of the deck. If a poker hand is made, then" +
                "\n you will win a certain amount of coins." +
                "\n To learn about the poker hands, read about it on Google.");

        instructions.setPositiveButton("GOT IT!", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface d, int which)
            {
                // Resume game
            }
        });

        AlertDialog i = instructions.create();
        i.show();
    }

    public void onPause()
    {
        super.onPause();
        s.pause();
    }

    public void onStop()
    {
        super.onStop();

        //setting preferences
        SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("score", player.getCash());
        editor.putInt("state", state);
        editor.putString("a", player.getHand()[0].toString());
        editor.putString("b", player.getHand()[1].toString());
        editor.putString("c", player.getHand()[2].toString());
        editor.putString("d", player.getHand()[3].toString());
        editor.putString("e", player.getHand()[4].toString());
        editor.commit();
    }

    public void onResume()
    {
        super.onResume();
        s.setLooping(true);
        s.seekTo(s.getCurrentPosition());
        s.start();
    }

    public void sortHand(Card[] h)
    {
        for (int i = 0; i < h.length; i++)
        {
            for (int j = 1; j < h.length - i; j++)
            {
                if (h[j-1].getRank() > h[j].getRank())
                {
                    Card x = h[j-1];
                    h[j-1] = h[j];
                    h[j] = x;
                }
            }
        }
    }

    // Checks if this is a royal flush combination
    public boolean isRoyalFlush(Card[] h)
    {
        if (h[0].getRank() == 1 && h[1].getRank() == 10 && h[2].getRank() == 11 &&
                h[3].getRank() == 12 && h[4].getRank() == 13)
        {
            if (h[0].getSuit().equals(h[1].getSuit())
                    && h[1].getSuit().equals(h[2].getSuit())
                    && h[2].getSuit().equals(h[3].getSuit())
                    && h[3].getSuit().equals(h[4].getSuit()))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public boolean isFlush(Card[] h)
    {
        if (h[0].getSuit().equals(h[1].getSuit())
                && h[1].getSuit().equals(h[2].getSuit())
                && h[2].getSuit().equals(h[3].getSuit())
                && h[3].getSuit().equals(h[4].getSuit()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    // Finds the number of pairs
    public int getNumPairs(Card[] h)
    {
        Set<Integer> pairs = new HashSet<Integer>();

        for (int i = 0; i < h.length; i++)
        {
            for (int j = i + 1; j < h.length; j++)
            {
                if (h[i].getRank() == h[j].getRank())
                {
                    pairs.add(h[i].getRank());
                }
            }
        }

        return pairs.size();
    }

    // Is it a pair of aces, kings, queens or jacks?
    public boolean isValidPair(Card[] h)
    {
        Set<Integer> pairs = new HashSet<Integer>();

        for (int i = 0; i < h.length; i++)
        {
            for (int j = i + 1; j < h.length; j++)
            {
                if (h[i].getRank() == h[j].getRank()
                        && (h[i].getRank() == 1 || (h[i].getRank() >= 11 && h[i].getRank() <= 13)))
                {
                    pairs.add(h[i].getRank());
                }
            }
        }

        return pairs.size() == 1;
    }

    public boolean isFullHouse(Card[] h)
    {
        ArrayList<Integer> x = new ArrayList<Integer>();

        // Check if we have 2 of something, 3 of other
        for (int i = 0; i < 5; i++)
        {
            int count = 0;

            for (int j = 0; j < 5; j++)
            {
                if (h[i].getRank() == h[j].getRank())
                {
                    count++;
                }
            }

            if (!x.contains(new Integer(count)))
            {
                x.add(count);
            }
        }

        if (x.size() != 2)
        {
            return false;
        }
        else
        {
            if (x.get(0).equals(2) && x.get(1).equals(3) ||
                    x.get(0).equals(3) && x.get(1).equals(2))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    public boolean isStraightFlush(Card[] h)
    {
        int[][] validArrays = { { 1, 10, 11, 12, 13 }, { 1, 2, 11, 12, 13 }, { 1, 2, 3, 12, 13 },
                { 1, 2, 3, 4, 13 } };
        sortHand(h);

        int[] x = new int[5];

        for (int i = 0; i < 5; i++) {
            x[i] = h[i].getRank();
        }

        if ((h[1].getRank() - h[0].getRank() == 1 && h[2].getRank() - h[1].getRank() == 1
                && h[3].getRank() - h[2].getRank() == 1 && h[4].getRank() - h[3].getRank() == 1) ||
                (Arrays.equals(x, validArrays[0]) || Arrays.equals(x, validArrays[1])
                        || Arrays.equals(x, validArrays[2]) || Arrays.equals(x, validArrays[3])))
        {
            if (h[1].getSuit().equals(h[0].getSuit()) && h[2].getSuit().equals(h[1].getSuit())
                    && h[3].getSuit().equals(h[2].getSuit()) && h[4].getSuit().equals(h[3].getSuit()))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    public boolean isStraight(Card[] h)
    {
        int[][] validArrays = { { 1, 10, 11, 12, 13 }, { 1, 2, 11, 12, 13 }, { 1, 2, 3, 12, 13 },
                { 1, 2, 3, 4, 13 } };
        sortHand(h);

        int[] x = new int[5];

        for (int i = 0; i < 5; i++) {
            x[i] = h[i].getRank();
        }

        if ((h[1].getRank() - h[0].getRank() == 1 && h[2].getRank() - h[1].getRank() == 1
                && h[3].getRank() - h[2].getRank() == 1 && h[4].getRank() - h[3].getRank() == 1) ||
                (Arrays.equals(x, validArrays[0]) || Arrays.equals(x, validArrays[1])
                        || Arrays.equals(x, validArrays[2]) || Arrays.equals(x, validArrays[3])))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isFourKind(Card[] h)
    {
        for (int i = 0; i < h.length; i++)
        {
            int matches = 0;

            for (int j = 0; j < h.length; j++)
            {
                if (h[i].getRank() == h[j].getRank())
                {
                    matches = matches + 1;
                }
            }

            if (matches == 4)
            {
                return true;
            }
        }

        return false;
    }

    public boolean isThreeKind(Card[] h)
    {
        for (int i = 0; i < h.length; i++)
        {
            int matches = 0;

            for (int j = 0; j < h.length; j++)
            {
                if (h[i].getRank() == h[j].getRank())
                {
                    matches = matches + 1;
                }
            }

            if (matches == 3)
            {
                return true;
            }
        }

        return false;
    }

    // Check winning hand
    public boolean checkWinningHand(Card[] h, Player p)
    {
        if (isRoyalFlush(h))
        {
            Toast.makeText(this.getApplicationContext(), "Royal Flush! Prize: 5000 coins.", Toast.LENGTH_SHORT).show();
            p.addCash(p.getCash() + 5000);
            scoreText.setText("Coins left : " + player.getCash() + " coins.");
            return true;
        }
        else if (isStraightFlush(h))
        {
            Toast.makeText(this.getApplicationContext(), "Straight Flush! Prize: 1000 coins.", Toast.LENGTH_SHORT).show();
            p.addCash(p.getCash() + 1000);
            scoreText.setText("Coins left : " + player.getCash() + " coins.");
            return true;
        }
        else if (isFourKind(h))
        {
            Toast.makeText(this.getApplicationContext(), "Four of a Kind! Prize: 500 coins.", Toast.LENGTH_SHORT).show();
            p.addCash(500);
            scoreText.setText("Coins left : " + player.getCash() + " coins.");
            return true;
        }
        else if (isStraight(h))
        {
            Toast.makeText(this.getApplicationContext(), "Regular Straight! Prize: 200 coins.", Toast.LENGTH_SHORT).show();
            p.addCash(200);
            scoreText.setText("Coins left : " + player.getCash() + " coins.");
            return true;
        }
        else if (isFlush(h))
        {
            Toast.makeText(this.getApplicationContext(), "Flush! Prize: 100 coins.", Toast.LENGTH_SHORT).show();
            p.addCash(100);
            scoreText.setText("Coins left : " + player.getCash() + " coins.");
            return true;
        }
        else if (isFullHouse(h))
        {
            Toast.makeText(this.getApplicationContext(), "Full House! Prize: 50 coins.", Toast.LENGTH_SHORT).show();
            p.addCash(50);
            scoreText.setText("Coins left : " + player.getCash() + " coins.");
            return true;
        }
        else if (isThreeKind(h))
        {
            Toast.makeText(this.getApplicationContext(), "Three Of A Kind! Prize: 30 coins.", Toast.LENGTH_SHORT).show();
            p.addCash(30);
            scoreText.setText("Coins left : " + player.getCash() + " coins.");
            return true;
        }
        else if (getNumPairs(h) == 2)
        {
            Toast.makeText(this.getApplicationContext(), "Two Pairs! Prize: 20 coins.", Toast.LENGTH_SHORT).show();
            p.addCash(20);
            scoreText.setText("Coins left : " + player.getCash() + " coins.");
            return true;
        }
        else if (isValidPair(h))
        {
            Toast.makeText(this.getApplicationContext(), "One Pair! Prize: 10 coins.", Toast.LENGTH_SHORT).show();
            p.addCash(10);
            scoreText.setText("Coins left : " + player.getCash() + " coins.");
            return true;
        }

        Toast.makeText(this.getApplicationContext(), "High Card! But you won nothing.", Toast.LENGTH_SHORT).show();
        return false;
    }

    public void nextHand(View v)
    {
        if (state == 1)
        {
            // Change cards
            for (int i = 0; i < x.size(); i++)
            {
                player.getHand()[x.get(i)] = deck.getCard(i + 6);
            }

            // New set of cards
            setCardImage(card1, 0);
            setCardImage(card2, 1);
            setCardImage(card3, 2);
            setCardImage(card4, 3);
            setCardImage(card5, 4);

            sortHand(player.getHand());
            checkWinningHand(player.getHand(), player);

            state = 0;
        }
        else
        {
            Toast.makeText(this.getApplicationContext(), "You can only get a next hand after the deal.", Toast.LENGTH_LONG).show();
        }
    }

    public void remove1(View v)
    {
        if (!x.contains(0))
        {
            x.add(0);
            card1.setImageDrawable(getResources().getDrawable(R.drawable.card_back));
            //Toast.makeText(this.getApplicationContext(), "Removing card at 0", Toast.LENGTH_SHORT).show();
        }
        else
        {
            x.remove(new Integer(0));
            setCardImage(card1, 0);
            //Toast.makeText(this.getApplicationContext(), "Not removing card at 0", Toast.LENGTH_SHORT).show();
        }
    }

    public void remove2(View v)
    {
        if (!x.contains(1))
        {
            x.add(1);
            card2.setImageDrawable(getResources().getDrawable(R.drawable.card_back));
            //Toast.makeText(this.getApplicationContext(), "Removing card at 1", Toast.LENGTH_SHORT).show();
        }
        else
        {
            x.remove(new Integer(1));
            setCardImage(card2, 1);
            //Toast.makeText(this.getApplicationContext(), "Not removing card at 1", Toast.LENGTH_SHORT).show();
        }
    }

    public void remove3(View v)
    {
        if (!x.contains(2))
        {
            x.add(2);
            card3.setImageDrawable(getResources().getDrawable(R.drawable.card_back));
            // Toast.makeText(this.getApplicationContext(), "Removing card at 2", Toast.LENGTH_SHORT).show();
        }
        else
        {
            x.remove(new Integer(2));
            setCardImage(card3, 2);
            // Toast.makeText(this.getApplicationContext(), "Not removing card at 2", Toast.LENGTH_SHORT).show();
        }
    }

    public void remove4(View v)
    {
        if (!x.contains(3))
        {
            x.add(3);
            card4.setImageDrawable(getResources().getDrawable(R.drawable.card_back));
            // Toast.makeText(this.getApplicationContext(), "Removing card at 3", Toast.LENGTH_SHORT).show();
        }
        else
        {
            x.remove(new Integer(3));
            setCardImage(card4, 3);
            // Toast.makeText(this.getApplicationContext(), "Not removing card at 3", Toast.LENGTH_SHORT).show();
        }
    }

    public void remove5(View v)
    {
        if (!x.contains(4))
        {
            x.add(4);
            card5.setImageDrawable(getResources().getDrawable(R.drawable.card_back));
            // Toast.makeText(this.getApplicationContext(), "Removing card at 4", Toast.LENGTH_SHORT).show();
        }
        else
        {
            x.remove(new Integer(4));
            setCardImage(card5, 4);
            // Toast.makeText(this.getApplicationContext(), "Not removing card at 4", Toast.LENGTH_SHORT).show();
        }
    }

    public void getHand(View v)
    {
        if (state == 0)
        {
            deck.shuffleDeck();
            player.dealCards(deck);
            player.placeBet(BET_AMOUNT);
            scoreText.setText("Coins left : " + player.getCash() + " coins.");
            x.clear();

            if (player.getCash() < 0)
            {
                s.stop();
                finish();
                Toast.makeText(this.getApplicationContext(), "Out of cash! Please reload game.", Toast.LENGTH_LONG).show();
            }

            // Deal the cards
            setCardImage(card1, 0);
            setCardImage(card2, 1);
            setCardImage(card3, 2);
            setCardImage(card4, 3);
            setCardImage(card5, 4);

            state = 1;
        }
        else
        {
            Toast.makeText(this.getApplicationContext(), "You can only deal a new hand after the play.", Toast.LENGTH_LONG).show();
        }
    }

    // Set image provided the card is made
    public void setCardImage(ImageView view, int index)
    {
        if(player.getCard(index).toString().equals("ACE OF HEARTS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.acehearts));
        }
        else if (player.getCard(index).toString().equals("2 OF HEARTS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.twohearts));
        }
        else if (player.getCard(index).toString().equals("3 OF HEARTS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.threehearts));
        }
        else if (player.getCard(index).toString().equals("4 OF HEARTS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.fourhearts));
        }
        else if (player.getCard(index).toString().equals("5 OF HEARTS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.fivehearts));
        }
        else if (player.getCard(index).toString().equals("6 OF HEARTS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.sixhearts));
        }
        else if (player.getCard(index).toString().equals("7 OF HEARTS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.sevenhearts));
        }
        else if (player.getCard(index).toString().equals("8 OF HEARTS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.eighthearts));
        }
        else if (player.getCard(index).toString().equals("9 OF HEARTS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.ninehearts));
        }
        else if (player.getCard(index).toString().equals("10 OF HEARTS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.tenhearts));
        }
        else if (player.getCard(index).toString().equals("JACK OF HEARTS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.jackhearts));
        }
        else if (player.getCard(index).toString().equals("QUEEN OF HEARTS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.queenhearts));
        }
        else if (player.getCard(index).toString().equals("KING OF HEARTS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.kinghearts));
        }
        else if(player.getCard(index).toString().equals("ACE OF DIAMONDS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.acediamonds));
        }
        else if (player.getCard(index).toString().equals("2 OF DIAMONDS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.twodiamonds));
        }
        else if (player.getCard(index).toString().equals("3 OF DIAMONDS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.threediamonds));
        }
        else if (player.getCard(index).toString().equals("4 OF DIAMONDS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.fourdiamonds));
        }
        else if (player.getCard(index).toString().equals("5 OF DIAMONDS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.fivediamonds));
        }
        else if (player.getCard(index).toString().equals("6 OF DIAMONDS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.sixdiamonds));
        }
        else if (player.getCard(index).toString().equals("7 OF DIAMONDS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.sevendiamonds));
        }
        else if (player.getCard(index).toString().equals("8 OF DIAMONDS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.eightdiamonds));
        }
        else if (player.getCard(index).toString().equals("9 OF DIAMONDS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.ninediamonds));
        }
        else if (player.getCard(index).toString().equals("10 OF DIAMONDS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.tendiamonds));
        }
        else if (player.getCard(index).toString().equals("JACK OF DIAMONDS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.jackdiamonds));
        }
        else if (player.getCard(index).toString().equals("QUEEN OF DIAMONDS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.queendiamonds));
        }
        else if (player.getCard(index).toString().equals("KING OF DIAMONDS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.kingdiamonds));
        }
        else if(player.getCard(index).toString().equals("ACE OF SPADES"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.acespades));
        }
        else if (player.getCard(index).toString().equals("2 OF SPADES"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.twospades));
        }
        else if (player.getCard(index).toString().equals("3 OF SPADES"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.threespades));
        }
        else if (player.getCard(index).toString().equals("4 OF SPADES"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.fourspades));
        }
        else if (player.getCard(index).toString().equals("5 OF SPADES"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.fivespades));
        }
        else if (player.getCard(index).toString().equals("6 OF SPADES"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.sixspades));
        }
        else if (player.getCard(index).toString().equals("7 OF SPADES"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.sevenspades));
        }
        else if (player.getCard(index).toString().equals("8 OF SPADES"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.eightspades));
        }
        else if (player.getCard(index).toString().equals("9 OF SPADES"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.ninespades));
        }
        else if (player.getCard(index).toString().equals("10 OF SPADES"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.tenspades));
        }
        else if (player.getCard(index).toString().equals("JACK OF SPADES"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.jackspades));
        }
        else if (player.getCard(index).toString().equals("QUEEN OF SPADES"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.queenspades));
        }
        else if (player.getCard(index).toString().equals("KING OF SPADES"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.kingspades));
        }
        else if(player.getCard(index).toString().equals("ACE OF CLUBS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.aceclubs));
        }
        else if (player.getCard(index).toString().equals("2 OF CLUBS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.twoclubs));
        }
        else if (player.getCard(index).toString().equals("3 OF CLUBS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.threeclubs));
        }
        else if (player.getCard(index).toString().equals("4 OF CLUBS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.fourclubs));
        }
        else if (player.getCard(index).toString().equals("5 OF CLUBS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.fiveclubs));
        }
        else if (player.getCard(index).toString().equals("6 OF CLUBS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.sixclubs));
        }
        else if (player.getCard(index).toString().equals("7 OF CLUBS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.sevenclubs));
        }
        else if (player.getCard(index).toString().equals("8 OF CLUBS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.eightclubs));
        }
        else if (player.getCard(index).toString().equals("9 OF CLUBS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.nineclubs));
        }
        else if (player.getCard(index).toString().equals("10 OF CLUBS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.tenclubs));
        }
        else if (player.getCard(index).toString().equals("JACK OF CLUBS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.jackclubs));
        }
        else if (player.getCard(index).toString().equals("QUEEN OF CLUBS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.queenclubs));
        }
        else if (player.getCard(index).toString().equals("KING OF CLUBS"))
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.kingclubs));
        }
        else
        {
            view.setImageDrawable(getResources().getDrawable(R.drawable.card_back));
        }
    }
}
