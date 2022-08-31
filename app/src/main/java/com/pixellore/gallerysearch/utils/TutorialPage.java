package com.pixellore.gallerysearch.utils;


/**
 * This class defines the data in each page of the Tutorial
 * The data includes  heading, explanation/content, an illustration and a boolean to determine if
 * skip button or done button to be shown (based on if this is the last page or not)
 * */
public class TutorialPage {

    private int mHeading;
    private  int mExplanation;
    private int mGraphicsId;
    private boolean mSkip;

    /**
     * Constructor to create an object with no states
     * */
    public TutorialPage() {

    }

    public TutorialPage(int heading, int explanation, int graphicsId, boolean skip) {
        this.mHeading = heading;
        this.mExplanation = explanation;
        this.mGraphicsId = graphicsId;
        this.mSkip = skip;
    }

    public int getHeading() {
        return mHeading;
    }

    public int getExplanation() {
        return mExplanation;
    }

    public int getGraphicsId() {
        return mGraphicsId;
    }

    public boolean isSkip() {
        return mSkip;
    }

}
