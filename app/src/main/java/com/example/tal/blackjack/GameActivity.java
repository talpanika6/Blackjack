package com.example.tal.blackjack;


import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.tal.blackjack.Logic.Game;
import com.example.tal.blackjack.Model.Card;
import com.example.tal.blackjack.Model.Player;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class GameActivity extends AppCompatActivity implements Runnable{

    private View positiveAction;
    private Button hit;
    private  Button stay;
    private SurfaceView surface;

    private NumberPicker PlaceBet;
    private TextView balanceText,betText;
    private SurfaceHolder holder;
    private Thread thread;
    private boolean locker=true;

    Game game;


    Bitmap[] cardImages;
    Bitmap mCardBack;
    Bitmap mArrowTurn;

    private String pName;

    boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // set an exit transition
            getWindow().setEnterTransition(new Slide(Gravity.START));
            // set an exit transition
            getWindow().setExitTransition(new Slide(GravityCompat.END));
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        ///start new Game
        Intent intent=this.getIntent();
        pName=intent.getStringExtra("player");

        //view initalzie
        balanceText=(TextView)findViewById(R.id.balance_text) ;
        betText=(TextView)findViewById(R.id.bet_text) ;
        surface = (SurfaceView) findViewById(R.id.gameview);
        hit = (Button) findViewById(R.id.hit);
        stay = (Button) findViewById(R.id.stay);



        //place holder
        holder = surface.getHolder();
        //start thread
        thread = new Thread(this);
        thread.start();

        //load cards
        loadBitmaps();

        //get Bet from player
        placeABet();



    }

    public void placeABet() {
        MaterialDialog dialogB = new MaterialDialog.Builder(this)
                .title(R.string.p_bet)
                .customView(R.layout.place_bit, true)
                .positiveText(R.string.start)
                .negativeText(android.R.string.cancel)
                .onPositive((@NonNull MaterialDialog dialog, @NonNull DialogAction which) ->{

                     StartGame(PlaceBet.getValue());

                }).build();


        PlaceBet=(NumberPicker)dialogB.getCustomView().findViewById(R.id.placeBet) ;
        PlaceBet.setMaxValue(1000);
        PlaceBet.setMinValue(0);
        PlaceBet.setValue(100);


        positiveAction = dialogB.getActionButton(DialogAction.POSITIVE);
        positiveAction.setEnabled(true); // disabled by default

        dialogB.show();


    }

    private void StartGame(int bet)
    {
        //initait game
        game = new Game(pName,1000,bet);

        //update bottom bar
        balanceText.setText("Balance: "+game.getPlayers().get(1).getBalance()+"$");
        betText.setText("Current Bet: "+game.getPlayers().get(1).getBet()+"$");

        hit.setOnClickListener((View v)-> {

                if(game.getCurrentPlayer().getName().equals("Dealer")) {
                    dealerAI();
                }
                else {
                    game.hit();

                    if(game.score(game.getCurrentPlayer()) > 21)
                    {
                        game.isGameOver=true;
                    }
                    else
                      game.nextPlayer();
                }

                if(game.score(game.getCurrentPlayer()) > 21)
                {
                    game.isGameOver=true;
                }



                waitingForInput = false;

        });

        stay.setOnClickListener((View v)-> {

                waitingForInput = false;
                game.nextPlayer();

                if(game.getCurrentPlayer().getName().equals("Dealer")){
                    dealerAI();
                    game.isGameOver=true;

            }
        });
    }

    void dealerAI() {
        Player dealer = game.getCurrentPlayer();

        game.isHoleFlipped = false;
        while(game.score(dealer) < 17)
            game.hit();


        game.nextPlayer();

        waitingForInput = false;
    }

    public void resetGame()
    {

        String winnerName =game.scoreHands().getName();
      // showGameOverDialog(winnerName);

        ExitGame();
    }

    private void showGameOverDialog(String name) {

        String winTxt;

        if (game.getPlayers().get(0).getName().equals(name))
            winTxt="You Lost!!!";
         else
            winTxt="You Win!!!";


        MaterialDialog dialogB = new MaterialDialog.Builder(this)
                .title(R.string.new_game)
                .customView(R.layout.start_game_dialog, true)
                .positiveText(R.string.resume)
                .negativeText(R.string.exit)
                .onPositive((@NonNull MaterialDialog dialog, @NonNull DialogAction which) ->{

                        game.updateBalance();
                        //update bottom bar
                          balanceText.setText("Balance: "+game.getPlayers().get(1).getBalance()+"$");
                          betText.setText("Current Bet: "+game.getPlayers().get(1).getBet()+"$");

                         //game.newGame(); //ToDO get new bet
                        waitingForInput = false;

                }).onNegative((@NonNull MaterialDialog dialog, @NonNull DialogAction which)-> {

                        game.disposeGame();
                        OnBackPreesed();

                })
                .build();


        positiveAction = dialogB.getActionButton(DialogAction.POSITIVE);


        TextView  winnerTxt = (TextView) dialogB.getCustomView().findViewById(R.id.win_text);
        winnerTxt.setText(winTxt);
        // disabled by defaults
        positiveAction.setEnabled(true);

        dialogB.show();


    }

    public void loadBitmaps() {
        Bitmap bitCardBack = BitmapFactory.decodeResource(this.getResources(), R.drawable.cardback);
        mArrowTurn=BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_fast_forward_white_18dp);
        mCardBack= Bitmap.createScaledBitmap(bitCardBack, 352, 400, false);
        AssetManager assetManager = this.getAssets();

        cardImages = new Bitmap[53];

        for(int i = 0; i < 53; i++) {
            Bitmap bitmap = null;
            try {
                String fileName = "standard"+i+".png";
                InputStream is=assetManager.open(fileName);
                bitmap = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                Toast.makeText(this,"error "+e.getMessage(),Toast.LENGTH_LONG).show();
            }
            cardImages[i] = Bitmap.createScaledBitmap(bitmap, 320, 360, false);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            this.onBackPressed();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    boolean waitingForInput = false;


    @Override
    public void run() {
        try {
        while(locker) {
            //checks if the lockCanvas() method will be success,and if not, will check this statement again
            if (!holder.getSurface().isValid())
                continue;
            if (game.isGameOver) {
               Thread.sleep(1500);
                pause();
                resetGame();
            }
            if(waitingForInput)
                continue;

            Canvas canvas = holder.lockCanvas();
            draw(canvas);
            holder.unlockCanvasAndPost(canvas);
            waitingForInput = true;
        }
        } catch (InterruptedException e) {
            Log.i("run",e.getMessage());
        }
    }


 //   int cardCount = 0;

    private void draw(Canvas canvas) {

        canvas.drawColor(Color.rgb(0, 135, 0));
        int playerPlace=4;
        for( int i=0; i<game.getPlayers().size(); i++ )
        {
            ArrayList<Card> hand = game.getPlayers().get(i).getHand();

            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);

            //get score
            int score = game.score(game.getPlayers().get(i));



            if (i<1)
            {//Dealer
                canvas.drawText(game.getPlayers().get(i).getName() + ": " +  score, 20, 100 + i * 400, paint);
               // canvas.drawText("Bet: " + game.getPlayers().get(i).getChips(), 1200, 100 + i * 400, paint);

                if(game.getPlayers().get(i).equals(game.getCurrentPlayer())) {
                      canvas.drawBitmap(mArrowTurn, 20, 150 + i * 400, null);
                }

                if(score > 21) {
                    canvas.drawText("BUST", 20, 300 + i * 400, paint);
                }
            }
             else//Player
            {
                canvas.drawText(game.getPlayers().get(i).getName() + ": " +  score, 20, 100 + i*(playerPlace-1) * 400, paint);
               // canvas.drawText("Bet: " + game.getPlayers().get(i).getChips(), 1200, 100 + i *(playerPlace-1)* 400, paint);

                if(game.getPlayers().get(i).equals(game.getCurrentPlayer())) {
                      canvas.drawBitmap(mArrowTurn, 20, 150 + i *(playerPlace-1)* 400, null);
                }

                if(score > 21) {
                    canvas.drawText("BUST", 20, 300 + i *(playerPlace-1)* 400, paint);
                }
            }


           // canvas.drawText("Count: " + game.cardCounting, 800, 100, paint);



            for( int j=0; j < hand.size(); j++ )
            {
                Card c = hand.get(j);


                if( i == 0 && j == 0 && game.isHoleFlipped)
                {
                    canvas.drawBitmap(mCardBack, 520 + j * 50, 200 + i * 400, null );
                }
                else
                {

                    if (i<1)
                        canvas.drawBitmap(cardImages[c.getCardIndex()], 520 + j * 50, 200 + i * 400, null );
                    else
                        canvas.drawBitmap(cardImages[c.getCardIndex()], 520 + j * 50, 100 + i *playerPlace* 300, null );
                }
            }
        }
    }


    private void OnBackPreesed()
    {
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }

    private void pause() {
        //CLOSE LOCKER FOR run();
        locker = false;
        while(true){
            try {
                //WAIT UNTIL THREAD DIE, THEN EXIT WHILE LOOP AND RELEASE a thread
                thread.join();
            } catch (InterruptedException e) {e.printStackTrace();
            }
            break;
        }
        game = null;
        thread = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume();
    }

    @Override
    public void onBackPressed()
    {
        ExitGame();
    }


    public void ExitGame()
    {
        new MaterialDialog.Builder(this)
                .title(R.string.exit_game)
                .content(R.string.exit_game_sen)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((@NonNull MaterialDialog dialog, @NonNull DialogAction which)-> {
                        game.disposeGame();
                        OnBackPreesed();
                    }
                )
                .show();
    }

    //ToDo: fix new game
    private void resume() {
        //RESTART THREAD AND OPEN LOCKER FOR run();
        locker = true;
        thread = new Thread(this);

        //placeABet();
        game = new Game(pName,1000,500);
        thread.start();
        waitingForInput = false;
    }

}
