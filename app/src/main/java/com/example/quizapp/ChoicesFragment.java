package com.example.quizapp;

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

import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class ChoicesFragment extends Fragment {
    private static final String SCORE_KEY = "scoreCount";
    private static final String QUESTION_KEY = "questionIndex";
    private static final String PREFERENCE_KEY = "quizPreferences";

    private int questionIndex;
    private int score;
    private int attempts;

    private String[][][] questions = {{{"Choose A."}, {"A", "B", "C", "D"}}, {{"Choose B."}, {"B", "A", "C", "D"}}};

    private String myTag = "logTag";

    private SharedPreferences mShared;
    private SharedPreferences.Editor mEditor;

    private Button choiceA, choiceB, choiceC, choiceD;
    private TextView scoreView, questionView;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation. 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        mShared = this.getActivity().getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        mEditor = mShared.edit();

        try {
            Scanner myReader = new Scanner(new File("questions.txt"));
            while (myReader.hasNextLine()) {
                String[] question = {myReader.nextLine()};
                String[] choices = {myReader.nextLine(), myReader.nextLine(), myReader.nextLine(), myReader.nextLine()};
                myReader.nextLine();
                //FINISH THIS
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            Log.i(myTag, "An error occurred.");
            e.printStackTrace();
        }

        questionIndex = mShared.getInt(QUESTION_KEY, -1);
        if (questionIndex == -1) {
            generateNewIndex();
        }

        score = mShared.getInt(SCORE_KEY, 0);

        return inflater.inflate(R.layout.choices_fragment, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        questionView = getView().findViewById(R.id.questionView);
        scoreView = getView().findViewById(R.id.scoreView);

        choiceA = getView().findViewById(R.id.choiceA);
        choiceA.setOnClickListener(choiceListener);

        choiceB = getView().findViewById(R.id.choiceB);
        choiceB.setOnClickListener(choiceListener);

        choiceC = getView().findViewById(R.id.choiceC);
        choiceC.setOnClickListener(choiceListener);

        choiceD = getView().findViewById(R.id.choiceD);
        choiceD.setOnClickListener(choiceListener);

        updateDisplay();
        scoreView.setText("Score: " + score);
    }

    View.OnClickListener choiceListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button b = (Button) v;
            if(b.getText() == questions[questionIndex][1][0]) {
                //b.setBackgroundColor(Color.GREEN);
                score += 10;
                updateDisplay();
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
            }
            else {
                attempts++;
                score -= (attempts * 5);
                b.setEnabled(false);
                //b.setBackgroundColor(Color.RED);
            }
            mEditor.putInt(SCORE_KEY, score);
            mEditor.apply();
            scoreView.setText("Score: " + score);
        }
    };

    private void updateDisplay() {
        questionView.setText(questions[questionIndex][0][0]);
        String[] choices = questions[questionIndex][1].clone();
        List<String> choiceList = Arrays.asList(choices);
        Collections.shuffle(choiceList);
        choiceList.toArray(choices);
        choiceA.setText(choices[0]);
        choiceB.setText(choices[1]);
        choiceC.setText(choices[2]);
        choiceD.setText(choices[3]);
    }

    private void generateNewIndex() {
        int curIndex = questionIndex;
        while (questionIndex == curIndex) {
            questionIndex = (int)(Math.random() * questions.length);
        }
        mEditor.putInt(QUESTION_KEY, questionIndex);
        mEditor.apply();
    }
}