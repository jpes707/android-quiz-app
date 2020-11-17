package com.example.quizapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.util.Objects;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ChoicesFragment extends Fragment {
    private static final String ERROR_QUESTION = "{\"question\": \"If you are seeing this question, your internet is not connected or is too slow.\", \"correct_answer\": \"I fixed it\", \"choices\": [\"I fixed it\", \"I did not fix it\", \"I did not fix it\", \"I did not fix it\"]}";

    private static final String PREFERENCE_KEY = "preferences";
    private static final String SCORE_KEY = "score";
    private static final String CURRENT_QUESTION_KEY = "currentQuestion";
    private static final String NEXT_QUESTION_KEY = "nextQuestion";
    private static final String TAG = "logTag";

    private int score;
    private int attempts;

    private QuizQuestion currentQuestion;
    private QuizQuestion nextQuestion;

    private Fragment fragment;
    private SharedPreferences.Editor mEditor;
    private Button choiceA, choiceB, choiceC, choiceD;
    private TextView scoreView, questionView;

    private Gson mGson = new GsonBuilder().create();
    private SharedPreferences mShared;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation. 
    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        fragment = getFragmentManager().findFragmentById(R.id.choicesFragment);
        mShared = Objects.requireNonNull(this.getActivity()).getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        mEditor = mShared.edit();

        currentQuestion = mGson.fromJson(mShared.getString(CURRENT_QUESTION_KEY, "{\"question\": \"Welcome to the quiz! Select the correct answer to begin.\", \"correct_answer\": \"Correct answer\", \"choices\": [\"Correct answer\", \"Wrong answer\", \"Wrong answer\", \"Wrong answer\"]}"), QuizQuestion.class);
        nextQuestion = mGson.fromJson(mShared.getString(NEXT_QUESTION_KEY, ERROR_QUESTION), QuizQuestion.class);
        if (nextQuestion.equals(ERROR_QUESTION)) {
            fetchNextQuestion();
        }

        score = mShared.getInt(SCORE_KEY, 0);

        return inflater.inflate(R.layout.choices_fragment, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        questionView = Objects.requireNonNull(getView()).findViewById(R.id.questionView);
        scoreView = getView().findViewById(R.id.scoreView);

        choiceA = getView().findViewById(R.id.choiceA);
        choiceA.setOnClickListener(choiceListener);

        choiceB = getView().findViewById(R.id.choiceB);
        choiceB.setOnClickListener(choiceListener);

        choiceC = getView().findViewById(R.id.choiceC);
        choiceC.setOnClickListener(choiceListener);

        choiceD = getView().findViewById(R.id.choiceD);
        choiceD.setOnClickListener(choiceListener);

        scoreView.setText("Score: " + score);

        /*choiceA.setBackgroundColor(Color.LTGRAY);
        choiceB.setBackgroundColor(Color.LTGRAY);
        choiceC.setBackgroundColor(Color.LTGRAY);
        choiceD.setBackgroundColor(Color.LTGRAY);*/

        updateDisplay();
    }

    private View.OnClickListener choiceListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button b = (Button) v;
            if(currentQuestion.checkAnswer((String) b.getText())) {
                score += 10;
                attempts = 0;
                currentQuestion = nextQuestion;
                mEditor.putString(CURRENT_QUESTION_KEY, mShared.getString(NEXT_QUESTION_KEY, ERROR_QUESTION));
                nextQuestion = mGson.fromJson(ERROR_QUESTION, QuizQuestion.class);
                fetchNextQuestion();
                updateDisplay();
                choiceA.setEnabled(true);
                choiceB.setEnabled(true);
                choiceC.setEnabled(true);
                choiceD.setEnabled(true);
                /*choiceA.setBackgroundColor(Color.LTGRAY);
                choiceB.setBackgroundColor(Color.LTGRAY);
                choiceC.setBackgroundColor(Color.LTGRAY);
                choiceD.setBackgroundColor(Color.LTGRAY);*/
                fragment.getView().setBackgroundColor(Color.parseColor("#20DF00"));
            }
            else {
                //b.setBackgroundColor(Color.parseColor("#FFCCCB"));
                attempts++;
                score -= (attempts * 5);
                b.setEnabled(false);
                if (attempts == 1) {
                    fragment.getView().setBackgroundColor(Color.parseColor("#609F00"));
                }
                else if (attempts == 2) {
                    fragment.getView().setBackgroundColor(Color.parseColor("#9F6000"));
                }
                else if (attempts == 3) {
                    fragment.getView().setBackgroundColor(Color.parseColor("#DF2000"));
                }
            }
            mEditor.putInt(SCORE_KEY, score);
            mEditor.apply();
            scoreView.setText("Score: " + score);
        }
    };

    private void updateDisplay() {
        questionView.setText(currentQuestion.getQuestion());
        String[] choices = currentQuestion.getChoices();
        // List<String> choiceList = Arrays.asList(choices).subList(1, 5);
        // Collections.shuffle(choiceList);
        // choiceList.toArray(choices);
        choiceA.setText(choices[0]);
        choiceB.setText(choices[1]);
        choiceC.setText(choices[2]);
        choiceD.setText(choices[3]);
    }

    private void fetchNextQuestion() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url ="https://quiz-api.sites.tjhsst.edu/get_question/";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        // Log.i(TAG, response);
                        mEditor.putString(NEXT_QUESTION_KEY, response);
                        mEditor.apply();
                        nextQuestion = mGson.fromJson(response, QuizQuestion.class);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, String.valueOf(error));
            }
        });
        queue.add(stringRequest);
    }
}

class QuizQuestion {
    private String question;
    private String correct_answer;
    private String[] choices;

    public String getQuestion() {
        return question;
    }

    public String[] getChoices() {
        return choices;
    }

    public boolean checkAnswer(String answer) {
        return answer.equals(correct_answer);
    }
}
