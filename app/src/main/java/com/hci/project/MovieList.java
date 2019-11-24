import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MovieList {

    private ArrayList<Movie> movie_list;
    private int loc = -1;
    public MovieList() {
        movie_list = new ArrayList<Movie>();
    }

    public MovieList(Movie Movie) {
        this();
        movie_list.add(Movie);
        loc = 0;
    }

    public MovieList(ArrayList Movies) {
        movie_list = Movies;
        loc = 0;
    }

    public MovieList(String name, String release_date,double rating, String synopsis,String folder,String trailer,String genre, int year) {
        this();
        movie_list.add(new Movie(name, release_date, rating, synopsis,folder,trailer,genre, year));
        loc = 0;
    }

    public MovieList(String name, String release_date,double rating, String synopsis,String folder, String poster_loc,String trailer,String genre, int year) {
        this();
        movie_list.add(new Movie(name, release_date, rating, synopsis,folder,poster_loc,trailer,genre, year));
        loc = 0;
    }

    public ArrayList<Movie> getMovie_list() {
        return movie_list;
    }

    public void moveLeft() {
        if (loc > 0)
        {
            loc--;
        }
    }

    public void  moveRight() {
        if (loc < movie_list.size() - 1)
        {
            loc++;
        }
    }

    public int getloc() {
        return loc;
    }

    public void setloc(int current_movie) {
        this.loc = current_movie;
    }

    public  void add(Movie movie) {
        if (loc == -1) loc++;
        movie_list.add(movie);
    }

    public void add(String name, String release_date,double rating, String synopsis,String folder,String trailer,String genre, int year) {
        this.add(new Movie(name, release_date, rating, synopsis,folder,trailer,genre, year));
    }

    public void add(String name, String release_date,double rating, String synopsis,String folder, String poster_loc,String trailer,String genre, int year) {
        this.add(new Movie(name, release_date, rating, synopsis,folder,poster_loc, trailer,genre,year));
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

    public static void main(String[] args) {
        MovieList a = new MovieList();
        a.addDefault();
        //System.out.print(a);
        //System.out.print(a.getCurrentMovie().getImageLoc());
        String[][] list = a.getImagesForAllMovies();
        for (int i =0; i < list.length; i++) {
            System.out.println(list[i][3]);
        }
    }

    public void delete(String name) {
        Movie movie = this.find(name);
        if (movie != null) {
            movie_list.remove(movie);
        }
    }

    public Movie getCurrentMovie() {
        return movie_list.get(loc);
    }

    public void addDefault() {
        try {
            FileReader fr = new FileReader(new File( "movies\\movies.txt"));
            BufferedReader in = new BufferedReader(fr);
            String name,release_date,synopsis ,imageLoc,trailer,posterLoc,genre;
            double rating;
            int year;

            while ((name = in.readLine()) != null) {
                name = name.split("=")[1];
                release_date = in.readLine().split("=")[1];
                year = Integer.parseInt(in.readLine().split("=")[1]);
                trailer = in.readLine().split("=")[1];
                synopsis = in.readLine().split("=")[1];
                genre = in.readLine().split("=")[1];
                rating = Double.parseDouble(in.readLine().split("=")[1]);
                imageLoc = "movies\\"+name.toLowerCase().replaceAll(" ", "_").replaceAll("[^A-Za-z0-9_]", "");
                this.add(name,release_date,rating,synopsis,imageLoc,trailer,genre,year);
                in.readLine();
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[][] getImagesForAllMovies() {
        String[][] list = new String[movie_list.size()][4];

        for (int i = 0; i < list.length; i++) {
            list[i] = getImages(movie_list.get(i));
        }

        return list;
    }

    public String[] getImages(Movie movie) {
        File folder = new File(movie.imageLoc);
        String[] images = new String[4];
        int count = 0;
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isFile() && fileEntry.getName().compareToIgnoreCase("poster") != 0 && count < 3) {
                //images[count] = fileEntry.getAbsolutePath();
                images[count] = Paths.get("").relativize(fileEntry.toPath()).toString();
                count++;
            }
            else if (fileEntry.isFile() && fileEntry.getName().compareToIgnoreCase("poster.jpg") == 0) {
                images[3] = Paths.get("").relativize(fileEntry.toPath()).toString();s
            }
        }
        return images;
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