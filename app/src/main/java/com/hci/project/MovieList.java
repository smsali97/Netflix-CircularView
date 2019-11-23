import java.util.ArrayList;

public class MovieList {

    private ArrayList<Movie> movie_list;
    private int Loc = -1;
    public MovieList() {
        movie_list = new ArrayList<Movie>();
        Loc = 0;
    }

    public MovieList(Movie Movie) {
        this();
        movie_list.add(Movie);
    }

    public MovieList(ArrayList Movies) {
        movie_list = Movies;
        Loc = 0;
    }

    public MovieList(String name, String release_date,double rating, String synopsis,String folder) {
        this();
        movie_list.add(new Movie(name, release_date, rating, synopsis,folder));
    }

    public MovieList(String name, String release_date,double rating, String synopsis,String folder, String poster_loc) {
        this();
        movie_list.add(new Movie(name, release_date, rating, synopsis,folder,poster_loc));
    }

    public ArrayList<Movie> getMovie_list() {
        return movie_list;
    }

    public void move_left() {
        if (Loc > 0)
        {
            Loc--;
        }
    }

    public void  move_right() {
        if (Loc < movie_list.size() - 1)
        {
            Loc++;
        }
    }

    public int getCurrent_movie() {
        return Loc;
    }

    public void setCurrent_movie(int current_movie) {
        this.Loc = current_movie;
    }

    public  void add(Movie movie) {
        movie_list.add(movie);
    }

    public void add(String name, String release_date,double rating, String synopsis,String folder) {
        movie_list.add(new Movie(name, release_date, rating, synopsis,folder));
    }

    public void add(String name, String release_date,double rating, String synopsis,String folder, String poster_loc) {
        movie_list.add(new Movie(name, release_date, rating, synopsis,folder,poster_loc));
    }

    public void delete(Movie movie) {
        if (movie != null) {
            movie_list.remove(movie);
        }
    }

    public Movie find(Movie movie) {
        return movie_list.get(movie_list.indexOf(movie));
    }

    public Movie find(String name) {
        for (int i = 0; i < movie_list.size(); i++) {
            if (movie_list.get(i).getName().compareToIgnoreCase(name) == 0) {
                return movie_list.get(i);
            }
        }

        return null;
    }

    public void delete(String name) {
        Movie movie = this.find(name);
        if (movie != null) {
            movie_list.remove(movie);
        }
    }

    public Movie getCurrentMovie() {
        return movie_list.get(Loc);
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i<movie_list.size(); i++) {
            result += movie_list.get(i).toString() + "\n\n";
        }
        return result;
    }
}
