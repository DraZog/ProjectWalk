package com.sage.projectwalk.InfoGraphs;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sage.projectwalk.Data.Country;
import com.sage.projectwalk.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by tahmidulislam on 27/11/2015.
 */

public class FactCards extends Fragment{

    private TextView factBody;
    private TextView factTitle;
    private ImageView imageHolder;
    private Country mCountryOne;
    private Country mCountryTwo;
    Animation slideOutAnimation;
    Animation slideInFromLeftAnim;
    RelativeLayout relativeLayout;
    ArrayList<String> factTitles;
    ArrayList<String> facts;
    GestureDetector gestureScanner;

    public void changeFact(){
        //Randomly select a fact
        Random randomGenerator = new Random();
        int randomNumber = randomGenerator.nextInt(facts.size());
        factTitle.setText(factTitles.get(randomNumber));
        factBody.setText(facts.get(randomNumber));
        //This generates the resource Id for that flag image
        int imageResource = getActivity().getResources().getIdentifier("drawable/fact"+randomNumber,null,getActivity().getPackageName());
        Drawable factImage = getActivity().getResources().getDrawable(imageResource);
        imageHolder.setImageDrawable(factImage);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fact_cards, container, false);

        factBody = (TextView) view.findViewById(R.id.factBody);
        factTitle = (TextView) view.findViewById(R.id.factTitle);
        imageHolder = (ImageView) view.findViewById(R.id.imageHolder);
        populateFacts();

        slideOutAnimation = AnimationUtils.loadAnimation(getActivity(),R.anim.slide_out_right);
        slideOutAnimation.setDuration(300);
        slideOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                changeFact();
                relativeLayout.startAnimation(slideInFromLeftAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        slideInFromLeftAnim = AnimationUtils.loadAnimation(getActivity(),R.anim.slide_in_left);
        slideInFromLeftAnim.setDuration(300);

        relativeLayout = (RelativeLayout) view.findViewById(R.id.factCardsLayout);

        gestureScanner = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }


                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {
                        Log.i(SyncStateContract.Constants.CONTENT_DIRECTORY, "onFling has been called!");
                        final int SWIPE_MIN_DISTANCE = 20;
                        final int SWIPE_MAX_OFF_PATH = 50;
                        final int SWIPE_THRESHOLD_VELOCITY = 10;
                        try {
                            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                                return false;
                             if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                             //   Log.i(SyncStateContract.Constants.ACCOUNT_NAME, "Left to Right");
                            } else { return false; }
                        } catch (Exception e) {
                            // nothing
                        }
                        return super.onFling(e1, e2, velocityX, velocityY);
                    }
                });

        factBody.setOnTouchListener(new OnSwipeTouchListener(container.getContext()) {
            @Override
            public void onSwipeRight() {
                relativeLayout.startAnimation(slideOutAnimation);
            }
        });


        return view;


    }

    /**
     * Populates the general facts
     * Which are stored in the assets folder
     */
    public void populateFacts(){
        factTitles = new ArrayList<>();
        facts = new ArrayList<>();

        factTitles = getFileAsset("factTitles.txt");
        facts = getFileAsset("facts.txt");
        changeFact();

    }

    public ArrayList<String> getFileAsset(String fileName){
        //Load from assets folder
        AssetManager assetManager = getActivity().getAssets();
        try{
            ArrayList<String> fileData = new ArrayList<>();
            InputStream input = assetManager.open(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(input);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            //Reads contents of file, line by line
            String line;
            while((line = bufferedReader.readLine()) != null){
                if(line.length() > 0){
                    fileData.add(line);
                }
            }
            return fileData;
        }catch (Exception e){
            return null;
        }
    }



}
