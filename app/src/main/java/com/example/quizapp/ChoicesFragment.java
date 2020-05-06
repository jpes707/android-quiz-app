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
import androidx.fragment.app.FragmentActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ChoicesFragment extends Fragment {
    private static final String SCORE_KEY = "scoreCount";
    private static final String QUESTION_KEY = "currentQuestionIndex";
    private static final String PREFERENCE_KEY = "quizPreferences";
    private static final String TAG = "logTag";

    private int questionIndex;
    private int score;
    private int attempts;

    private String[][] questions;

    private Fragment fragment;
    private SharedPreferences.Editor mEditor;
    private Button choiceA, choiceB, choiceC, choiceD;
    private TextView scoreView, questionView;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation. 
    @SuppressLint("CommitPrefEdits")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        fragment = getFragmentManager().findFragmentById(R.id.choicesFragment);
        SharedPreferences mShared = Objects.requireNonNull(this.getActivity()).getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        mEditor = mShared.edit();

        try {
            int lineCount = 0;
            Scanner lineCounter = new Scanner(Objects.requireNonNull(getContext()).getAssets().open("trivia.txt"));
            while (lineCounter.hasNextLine()) {
                lineCounter.nextLine();
                lineCount++;
            }
            lineCount /= 6;
            questions = new String[lineCount][5];
            Scanner myReader = new Scanner(Objects.requireNonNull(getContext()).getAssets().open("trivia.txt"));
            for (int i = 0; i < lineCount; i++)
            {
                questions[i] = new String[]{myReader.nextLine(), myReader.nextLine(), myReader.nextLine(), myReader.nextLine(), myReader.nextLine()};
                myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            Log.i(TAG, "File not found.");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG, "IO error.");
            e.printStackTrace();
        }

        questionIndex = mShared.getInt(QUESTION_KEY, 0);
        if (questionIndex == -1) {
            generateNewIndex();
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
            if(b.getText() == questions[questionIndex][1]) {
                score += 10;
                attempts = 0;
                generateNewIndex();
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
        questionView.setText(questions[questionIndex][0]);
        String[] choices = questions[questionIndex].clone();
        List<String> choiceList = Arrays.asList(choices).subList(1, 5);
        Collections.shuffle(choiceList);
        choiceList.toArray(choices);
        choiceA.setText(choices[0]);
        choiceB.setText(choices[1]);
        choiceC.setText(choices[2]);
        choiceD.setText(choices[3]);
    }

    private void generateNewIndex() {
        /*int curIndex = questionIndex;
        while (questionIndex == curIndex) {
            questionIndex = (int)(Math.random() * questions.length);
        }*/
        questionIndex = (questionIndex + 1) % questions.length;
        mEditor.putInt(QUESTION_KEY, questionIndex);
        mEditor.apply();
    }
}