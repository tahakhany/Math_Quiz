package com.Taha.mathquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    TextView livesRemainingTxt;
    TextView scoreTxt;
    TextView questionBoxTxt;
    TextView highScoreTxt;
    EditText answerBoxEdt;
    Button nextQuizBtn;
    Button endStartQuizBtn;
    int score;
    int highScore;
    int lives;
    int answer;
    SharedPreferences sharedPreferences;
    final static String FILE_NAME = "quiz_save_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing the views___________________________________________
        livesRemainingTxt = findViewById(R.id.main_activity_life_count_txt);
        scoreTxt = findViewById(R.id.main_activity_score_number_txt);
        questionBoxTxt = findViewById(R.id.main_activity_question_box_txt);
        highScoreTxt = findViewById(R.id.main_activity_highscore_number_txt);
        answerBoxEdt = findViewById(R.id.main_activity_answer_edt);
        nextQuizBtn = findViewById(R.id.main_activity_next_quiz_btn);
        endStartQuizBtn = findViewById(R.id.main_activity_end_start_quiz_btn);
        //Initializing the views___________________________________________

        //Initializing the values___________________________________________
        startGameProcedure();
        //Initializing the values___________________________________________

        nextQuizBtn.setOnClickListener(view -> checkAnswer());

        endStartQuizBtn.setOnClickListener(view -> {
            if (endStartQuizBtn.getText().equals(getString(R.string.main_activity_end_quiz_btn_text))) {
                endGameProcedure();
            } else if (endStartQuizBtn.getText().equals(getString(R.string.main_activity_start_btn_text))) {
                startGameProcedure();
            }
        });

        answerBoxEdt.setOnKeyListener((view, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                checkAnswer();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveAppData(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadAppData();
    }

    private int initializeNewQuiz() {
        Quiz quiz = new Quiz();
        updateScoresAndLives();
        questionBoxTxt.setText(quiz.getQuestion());
        answerBoxEdt.setText("");
        answer = quiz.getAnswer();
        return answer;
    }

    private void startGameProcedure() {
        loadAppData();
        updateScoresAndLives();
        answer = initializeNewQuiz();
        answerBoxEdt.setEnabled(true);
    }

    private void endGameProcedure() {
        Toast.makeText(MainActivity.this, "Game Over!", Toast.LENGTH_LONG).show();
        endStartQuizBtn.setText(R.string.main_activity_start_btn_text);
        answerBoxEdt.setEnabled(false);
        if (score > highScore) {
            highScore = score;
        }
        updateScoresAndLives();
        endStartQuizBtn.setText(getString(R.string.main_activity_end_quiz_btn_text));
        saveAppData(true);
    }

    private void saveAppData(boolean isGameEnded) {
        sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        if (isGameEnded) {
            sharedPreferences.edit()
                    .putInt("lives", 3)
                    .putInt("score", 0)
                    .putInt("highScore", this.highScore)
                    .putString("answerEdtContents", "")
                    .apply();
        } else {
            sharedPreferences.edit()
                    .putInt("lives", this.lives)
                    .putInt("score", this.score)
                    .putInt("highScore", this.highScore)
                    .putString("answerEdtContents", String.valueOf(answerBoxEdt.getText()))
                    .apply();
        }
    }

    private void loadAppData() {
        sharedPreferences = getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        this.lives = sharedPreferences.getInt("lives", 3);
        this.score = sharedPreferences.getInt("score", 0);
        this.highScore = sharedPreferences.getInt("highScore", 0);
        this.answerBoxEdt.setText(sharedPreferences.getString("answerEdtContents", ""));
    }

    private void updateScoresAndLives() {
        scoreTxt.setText(String.valueOf(this.score));
        livesRemainingTxt.setText(String.valueOf(this.lives));
        highScoreTxt.setText(String.valueOf(this.highScore));
    }

    private void checkAnswer() {
        if (String.valueOf(answerBoxEdt.getText()).equals(String.valueOf(answer))) {
            score++;
            if (score > highScore) highScore = score;
            updateScoresAndLives();
            Toast.makeText(MainActivity.this, "Correct!", Toast.LENGTH_SHORT).show();
            initializeNewQuiz();
        } else {
            lives--;
            if (lives == 0) {
                endGameProcedure();
            } else {
                Toast.makeText(MainActivity.this, "Wrong!\n" +
                        "Correct Answer: " + answer, Toast.LENGTH_SHORT).show();
                updateScoresAndLives();
                initializeNewQuiz();
            }
        }
    }

    static class Quiz {

        private String question;
        private int answer;

        @SuppressLint("DefaultLocale")
        public Quiz() {

            int a = (int) Math.floor(Math.random() * 100);
            int b = (int) Math.floor(Math.random() * 100);
            int operator = (int) Math.floor(Math.random() * 4);
            int max;
            int min;
            if (a > b) {
                max = a;
                min = b;
            } else {
                max = b;
                min = a;
            }

            switch (operator) {
                case 0:
                    this.question = String.format("%d + %d = ", a, b);
                    this.answer = (a + b);
                    break;
                case 1:
                    this.question = String.format("%d - %d = ", max, min);
                    this.answer = (max - min);
                    break;
                case 2:
                    this.question = String.format("%d * %d = ", a, b);
                    this.answer = (a * b);
                    break;
                case 3:
                    if (max % min != 0) {
                        max = min * (max / min);
                    }
                    this.question = String.format("%d / %d = ", max, min);
                    this.answer = max / min;
                    break;
            }
        }

        public String getQuestion() {
            return question;
        }

        public int getAnswer() {
            return answer;
        }
    }
}