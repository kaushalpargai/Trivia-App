package com.example.trivia;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.trivia.controller.AppController;
import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.Repository;
import com.example.trivia.databinding.ActivityMainBinding;
import com.example.trivia.model.Question;
import com.example.trivia.util.Prefs;
import com.google.android.material.snackbar.Snackbar;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    private int scoreCounter=0;
    private Score score;
    private Prefs prefs;
private ActivityMainBinding binding;
    List<Question> questionlist;
private int currentQuestionIndex=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        score=new Score();
prefs=new Prefs(MainActivity.this);
//Retreiving the last state
currentQuestionIndex=prefs.getState();
       // Log.d("high score", "onCrete:  "+prefs.getHighestScore());

        //Log.d("Prefs", "onClick: "+prefs.getHighestScore());
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
binding= DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.scoreText.setText(String.valueOf(MessageFormat.format("Score: {0}", score.getScoreCounter())));
        binding.highestScoreText.setText("Highest: " +String.valueOf(prefs.getHighestScore()));


//        Log.d("highscorechecker", "onCreate: "+prefs.getHighestScore());


questionlist=new Repository().getQuestions(new AnswerListAsyncResponse() {
    @Override
    public void processFinished(ArrayList<Question> questionArrayList) {
        binding.questionTextView.setText( questionlist.get(currentQuestionIndex).getAnswer());
binding.textViewOutOf.setText(String.format(getString(R.string.text_formatted), currentQuestionIndex + 1, questionArrayList.size()));
    }

});
binding.buttonNext.setOnClickListener(new View.OnClickListener(){
    @Override
    public void onClick(View v) {
        getNextQuestion();
        binding.textViewOutOf.setText(String.format(getString(R.string.text_formatted), currentQuestionIndex + 1, questionlist.size()));
//prefs.saveHighestScore(scoreCounter);
        binding.highestScoreText.setText(String.valueOf(prefs.getHighestScore()));
       Log.d("Prefs", "onClick: "+prefs.getHighestScore());

    }


});

 binding.buttonTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
checkAnswer(true);
                binding.textViewOutOf.setText(String.format(getString(R.string.text_formatted), currentQuestionIndex + 1, questionlist.size()));

                updateQuestion();
            }
        });
 binding.buttonFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
checkAnswer(false);
                binding.textViewOutOf.setText(String.format(getString(R.string.text_formatted), currentQuestionIndex + 1, questionlist.size()));

                updateQuestion();
            }


        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void checkAnswer(boolean userChoseCorrect) {
Boolean answer=questionlist.get(currentQuestionIndex).isAnswerTrue();

int snackMessageId=0;
if(userChoseCorrect==answer){
    snackMessageId=R.string.correct_answer;
    fadeAnimation();
    addPoints();
}
else{
    snackMessageId=R.string.incorrect;
    shakeAnimation();
    deductPoints();
}
        Snackbar.make(binding.cardView,snackMessageId,Snackbar.LENGTH_SHORT).show();
    }
private void shakeAnimation(){
    Animation shake= AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_animation);
    binding.cardView.setAnimation(shake);
    shake.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            binding.questionTextView.setTextColor(Color.RED);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
binding.questionTextView.setTextColor(Color.WHITE);
            getNextQuestion();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    });
}
    public void updateQuestion(){
        binding.questionTextView.setText( questionlist.get(currentQuestionIndex).getAnswer());
    }
    private void fadeAnimation(){
        Animation shake= AnimationUtils.loadAnimation(MainActivity.this,R.anim.fade_in);
        binding.cardView.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextView.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextView.setTextColor(Color.WHITE);
                getNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    private void fadeAnimation1(){

        AlphaAnimation alphaAnimation=new AlphaAnimation(1.0f,0.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        binding.cardView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextView.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextView.setTextColor(Color.WHITE);
                getNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
    private void deductPoints(){
        if(score.getScoreCounter()<=0){
            score.setScoreCounter(0);
        }if(score.getScoreCounter()>0){
        scoreCounter-=50;
        score.setScoreCounter(scoreCounter);}
        binding.scoreText.setText(String.valueOf(MessageFormat.format("Score: {0}", score.getScoreCounter())));

    }
    private void addPoints(){
        scoreCounter+=100;
        score.setScoreCounter(scoreCounter);
      binding.scoreText.setText(String.valueOf(MessageFormat.format("Score: {0}", score.getScoreCounter())));

    }
    private void getNextQuestion(){
        currentQuestionIndex=(currentQuestionIndex+1)%questionlist.size();
        updateQuestion();
    }
    @Override
    protected void onPause() {
        prefs.saveHighestScore(score.getScoreCounter());
        prefs.setState(currentQuestionIndex);
        Log.d("state", "onPause:   "+prefs.getState());
        //Log.d("pause", "onPause: saving score  "+prefs.getHighestScore());
        super.onPause();
    }
}