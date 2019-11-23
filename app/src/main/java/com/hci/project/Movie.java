public class Movie {

    String name;
    String release_date;
    double rating;
    String synopsis;
    String imageLoc;
    String posterLoc;



    public Movie(String name, String release_date,double rating, String synopsis,String folder) {
        this.name = name;
        this.release_date = release_date;
        this.rating = rating;
        this.synopsis = synopsis;
        this.imageLoc = folder;
        this.posterLoc = folder;
    }

    public Movie(String name, String release_date,double rating, String synopsis,String folder,String poster_loc) {
        this.name = name;
        this.release_date = release_date;
        this.rating = rating;
        this.synopsis = synopsis;
        this.imageLoc = folder;
        this.posterLoc = poster_loc;
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

    @Override
    public String toString() {
        String details = "Name: " + getName() + "\n\nRelease date: " + getRelease_date() + "\n\nSynopsis:\n" + getSynopsis() + "\n\nRating: " + getRating();
        return details;
    }
}
