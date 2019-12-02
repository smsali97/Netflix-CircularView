package com.hci.project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.lukedeighton.wheelview.WheelView;
import com.lukedeighton.wheelview.adapter.WheelArrayAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    private TextView movieTitle;
    private TextView movieDesc;
    private Button watch_trailer;
    private static final int ITEM_COUNT = 10;
    private float previousAngle = 0;

    private MovieList movieList = new MovieList();

    private Movie currMovie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WheelView wheelView = (WheelView) findViewById(R.id.wheelview);

        movieList.addDefault(this.getBaseContext());

        viewFlipper = (ViewFlipper)findViewById(R.id.image_view_flipper);
        movieTitle = findViewById(R.id.movie_title);
        movieDesc = findViewById(R.id.movie_desc);
        watch_trailer = findViewById(R.id.watch_trailer);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
        viewFlipper.setAutoStart(true);

        //create data for the adapter
//        List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(ITEM_COUNT);
//        for (int i = 0; i < ITEM_COUNT; i++) {
//            Map.Entry<String, Integer> entry = MaterialColor.random(this, "\\D*_500$");
//            entries.add(entry);
//        }

        //populate the adapter, that knows how to draw each item (as you would do with a ListAdapter)
//        wheelView.setAdapter(new MaterialColorAdapter(entries));

        wheelView.setAdapter(new MovieAdapter(movieList.getMovie_list()));
        //a listener for receiving a callback for when the item closest to the selection angle changes
        wheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectListener() {
            @Override
            public void onWheelItemSelected(WheelView parent, Drawable itemDrawable, int position) {
                //get the item at this position
//                Map.Entry<String, Integer> selectedEntry = ((MaterialColorAdapter) parent.getAdapter()).getItem(position);
//                parent.setSelectionColor(getContrastColor(selectedEntry));


                currMovie = (( MovieAdapter) parent.getAdapter()).getItem(position);


                SpannableString ss1=  new SpannableString(currMovie.name + " (" + currMovie.year + ")\n");
                ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, currMovie.name.length(), 0);// set color
                ss1.setSpan(new RelativeSizeSpan(2f), 0,currMovie.name.length(), 0); // set size

                String myRating = getStars(currMovie.rating);

                SpannableString ss12 =  new SpannableString(myRating);
                ss12.setSpan(new ForegroundColorSpan(Color.YELLOW), 13, myRating.length(), 0);// set color

                SpannableString ss3=  new SpannableString("Synopsis:\n" + currMovie.synopsis);
                ss3.setSpan(new RelativeSizeSpan(1.75f), 0,10, 0); // set size

                movieTitle.setText(TextUtils.concat(ss1,ss12,"Genre: " + currMovie.genre));
                movieDesc.setText(ss3);
                viewFlipper.removeAllViews();
                for (String img: currMovie.screenshots) {
                    try
                    {
                        // get input stream
                        InputStream ims = getAssets().open(img);
                        // load image as Drawable
                        Drawable d = Drawable.createFromStream(ims, null);
                        // set image to ImageView
                        ImageView mImage = new ImageView(getApplicationContext());
                        mImage.setImageDrawable(d);
                        viewFlipper.addView(mImage);
                        ims .close();
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                        return;
                    }
                }


            }
        });


        watch_trailer.setOnClickListener(v -> {
            if (currMovie != null ) {
                watchYoutubeVideo(this,currMovie.trailer);
            }
        });



        wheelView.setOnWheelItemClickListener(new WheelView.OnWheelItemClickListener() {
            @Override
            public void onWheelItemClick(WheelView parent, int position, boolean isSelected) {
                currMovie = (( MovieAdapter) parent.getAdapter()).getItem(position);
                String msg = String.format("You selected to watch %s!", currMovie.name);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        wheelView.setSelected(0);
        wheelView.getOnWheelItemSelectListener().onWheelItemSelected(wheelView,wheelView.getSelectionDrawable(),0);
        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //wheelView.setSelectionAngle(-wheelView.getAngleForPosition(5));
                wheelView.setMidSelected();
            }
        }, 3000); */
    }

    //get the materials darker contrast
    private int getContrastColor(Map.Entry<String, Integer> entry) {
        String colorName = MaterialColor.getColorName(entry);
        return MaterialColor.getContrastColor(colorName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    static class MovieAdapter extends WheelArrayAdapter<Movie> {
        MovieAdapter(List<Movie> entries) {
            super(entries);
        }
        @Override
        public Drawable getDrawable(int position) {
            Drawable[] drawable = new Drawable[] {
                getItem(position).drawable
            };
            return new LayerDrawable(drawable);
        }

    }

    static class MaterialColorAdapter extends WheelArrayAdapter<Map.Entry<String, Integer>> {
        MaterialColorAdapter(List<Map.Entry<String, Integer>> entries) {
            super(entries);
        }

        @Override
        public Drawable getDrawable(int position) {

            Drawable[] drawable = new Drawable[] {
                    createOvalDrawable(getItem(position).getValue()),
                    new TextDrawable(String.valueOf(position))
            };
            return new LayerDrawable(drawable);
        }

        private Drawable createOvalDrawable(int color) {
            ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
            shapeDrawable.getPaint().setColor(color);
            return shapeDrawable;
        }
    }

    private void setFlipperImage(int res) {
        Log.i("Set Filpper Called", res+"");
        ImageView image = new ImageView(getApplicationContext());
        image.setBackgroundResource(res);
        viewFlipper.addView(image);
    }

    public static String getTextSize(String text,int size) {
        return "<span style=\"size:"+size+"\" >"+text+"</span>";

    }

    public static String getStars(double rating) {
        rating /= 2;
        String myStars = "IMDB Rating: ";
        for (int i = 0; i < (int) rating; i++) {
            myStars += "\u2605";
        }
        myStars += " ("+ rating*2 +"/10)\n";

        return myStars;
    }

    public static void watchYoutubeVideo(Activity activity, String id){

        Log.d("YOUTUBE",id);
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(id));
        activity.startActivity(webIntent);

    }
}
