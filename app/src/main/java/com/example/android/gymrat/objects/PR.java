package com.example.android.gymrat.objects;

/**
 * Created by Artur on 24-Oct-16.
 */

public class PR {
    private String exercise;
    private double reps;
    private double weight;
    private boolean hasVid;
    private String vidLink;

    public PR(String exercise, double reps, double weight, boolean hasVid, String vidLink) {
        this.exercise = exercise;
        this.reps = reps;
        this.weight = weight;
        this.hasVid = hasVid;
        this.vidLink = vidLink;
    }

    public PR(String exercise, double reps, double weight, boolean hasVid) {
        this.exercise = exercise;
        this.reps = reps;
        this.weight = weight;
        this.hasVid = hasVid;
    }

    public String getExercise() {
        return exercise;
    }

    public String getVidLink() {
        if (hasVid) {
            return vidLink;
        } else return null;
    }

    public double getReps() {
        return reps;
    }

    public double getWeight() {
        return weight;
    }

    public boolean isHasVid() {
        return hasVid;
    }
}
