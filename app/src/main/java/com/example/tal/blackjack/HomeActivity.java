package com.example.tal.blackjack;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDTintHelper;



public class HomeActivity extends AppCompatActivity {


    private HomeActivity act;
    private View positiveAction;
    private EditText playerInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // inside your activity
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // set an exit transition
            getWindow().setEnterTransition(new Slide(Gravity.START));
            // set an exit transition
            getWindow().setExitTransition(new Slide(Gravity.END));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        act=this;
        Button bStartGame,bExistGame;

        bStartGame=(Button)findViewById(R.id.start_btn);
        bExistGame=(Button)findViewById(R.id.exist_btn);

        bStartGame.setOnClickListener((View view)-> showNewGameView());

        bExistGame.setOnClickListener((View view) ->finish());
    }



    public void showNewGameView() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.new_game)
                .customView(R.layout.start_game_dialog, true)
                .positiveText(R.string.start)
                .negativeText(android.R.string.cancel)
                .onPositive((@NonNull MaterialDialog dialogB, @NonNull DialogAction which)-> {
                        //  showToast("Password: " + passwordInput.getText().toString());
                        Intent intent = new Intent(act, GameActivity.class);
                        intent.putExtra("player", playerInput.getText().toString());
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(act).toBundle());

                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        //noinspection ConstantConditions

        playerInput = (EditText) dialog.getCustomView().findViewById(R.id.player);
        playerInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        // int widgetColor = ThemeSingleton.get().widgetColor;


        MDTintHelper.setTint(playerInput, getResources().getColor(R.color.material_indigo_500));

        dialog.show();
        positiveAction.setEnabled(false); // disabled by default
    }
}
