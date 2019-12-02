package com.hci.project;

import android.graphics.drawable.Drawable;

public class Movie {

    String name;
    String release_date;
    double rating;
    String synopsis;
    String imageLoc;
    String trailer;
    String posterLoc;
    String genre;
    Drawable drawable;
    String[] screenshots;
    int year;



    public Movie(String name, String release_date,double rating, String synopsis,String folder,String trailer,String genre, int year) {
        this.name = name;
        this.release_date = release_date;
        this.rating = rating;
        this.synopsis = synopsis;
        this.imageLoc = folder;
        this.posterLoc = folder;
        this.trailer = trailer;
        this.genre = genre;
        this.year = year;

        this.screenshots = new String[] { posterLoc + "/s1.jpg" , posterLoc + "/s2.jpg", posterLoc + "/s3.jpg"};

    }

    public Movie(String name, String release_date,double rating, String synopsis,String folder,String poster_loc,String trailer,String genre, int year) {
        this(name, release_date,rating, synopsis,folder,trailer,genre,year);
        this.posterLoc = poster_loc;

    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String getName() {
        return name;
    }

    public String getRelease_date() {
        return release_date;
    }

    public double getRating() {
        return rating;
    }

    public String getImageLoc() {
        return imageLoc;
    }

    public String getPosterLoc() {
        return posterLoc;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public void setImageLoc(String imageLoc) {
        this.imageLoc = imageLoc;
    }

    public void setPosterLoc(String posterLoc) {
        this.posterLoc = posterLoc;
    }

    public int getYear() {
        return year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        String details = "Name: " + getName() + "\n\nRelease date: " + getRelease_date() + "\n\nYear: " + getYear() + "\n\nTrailer: " + getTrailer()
                 + "\n\nSynopsis:\n" + getSynopsis() + "\n\nGenre: " + getGenre() + "\n\nRating: " + getRating();
        return details;
    }
}
