package com.example.quizapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChoicesFragment extends Fragment {
    private static final String SCORE_KEY = "scoreCount";
    private static final String QUESTION_KEY = "questionIndex";
    private static final String PREFERENCE_KEY = "quizPreferences";

    private int questionIndex;
    private int score;
    private int attempts;

    private String[][] questions;

    private SharedPreferences mShared;
    private SharedPreferences.Editor mEditor;

    private Button choiceA, choiceB, choiceC, choiceD;

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation. 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        mShared = this.getActivity().getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        mEditor = mShared.edit();

        questionIndex = mShared.getInt(QUESTION_KEY, (int)(Math.random() * questions.length));
        score = mShared.getInt(SCORE_KEY, 0);

        mEditor.putInt(SCORE_KEY, score);
        mEditor.apply();

        return inflater.inflate(R.layout.choices_fragment, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);a
        choiceA = getView().findViewById(R.id.choiceA);
        choiceB = getView().findViewById(R.id.choiceB);
        choiceC = getView().findViewById(R.id.choiceC);
        choiceD = getView().findViewById(R.id.choiceD);
    }

    View.OnClickListener choiceListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button b = (Button) v;
            if(b.getText() == questions[questionIndex][0]) {
                score += 10;
                attempts = 0;
            }
            else {
                attempts++;
                score -= (attempts * 3);
            }
        }
    };

    public void setNewQuestions(int curIndex) {
        while (questionIndex == curIndex) {
            questionIndex = (int)(Math.random() * questions.length);
        }
        String[] choices = questions[questionIndex];
        List<String> choiceList = Arrays.asList(choices);
        Collections.shuffle(choiceList);
        choiceList.toArray(choices);
        choiceA.setText(choices[0]);
        choiceB.setText(choices[1]);
        choiceC.setText(choices[2]);
        choiceD.setText(choices[3]);

        mEditor.putInt(QUESTION_KEY, questionIndex);
    }
}