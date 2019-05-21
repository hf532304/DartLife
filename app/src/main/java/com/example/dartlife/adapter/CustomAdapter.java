package com.example.dartlife.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import com.example.dartlife.R;
import com.example.dartlife.activity.MainActivity;
import com.example.dartlife.activity.MovieInfoActivity;
import com.example.dartlife.model.MovieBookEntry;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import static android.support.v4.content.ContextCompat.startActivity;


public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private ArrayList<MovieBookEntry> movies;
    private Context mContext;

    private static final int TYPE_TOP_FILTER = 0;
    private static final int TYPE_MOVIES = 1;
    private static final String MOVIE_ID = "idKey";
    private static final String MOVIE_TITLE = "titleKey";
    private static final String MOVIE_YEAR = "yearKey";
    private static final String MOVIE_SCORE = "scoreKey";
    private static final String MOVIE_INFO = "infoKey";
    private static final String MOVIE_DESCRIPTION = "descriptionKey";
    private static final String MOVIE_REVIEWS = "reviewsKey";
    private static final String MOVIE_URL = "urlKey";



    public CustomAdapter(Context applicationContext) {
        mContext = applicationContext;

    }

    public void setMovies(ArrayList<MovieBookEntry> movielist) {
        this.movies = movielist;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder holder = getViewHolderByViewType(viewType);
        Log.d("adapter", "onCreateViewHolder");



        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(i == 0) {

        }
        if(i > 0) {
            String url = movies.get(i - 1).getUrl();
            Log.d("adapter", url);
            Picasso.get().load(url).into( ((ViewHolder_Movies) viewHolder).moviePic);

            ((ViewHolder_Movies) viewHolder).movieNameYear.setText(movies.get(i - 1).getTitle() + " (" + movies.get(i - 1).getYear() + ")");
            ((ViewHolder_Movies) viewHolder).movieScore.setText(String.valueOf(movies.get(i - 1).getScore()));
            ((ViewHolder_Movies) viewHolder).movieInfo.setText(movies.get(i - 1).getInfo());
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return TYPE_TOP_FILTER;
        }
        if (position > 0 && position <= movies.size()) {
            return TYPE_MOVIES;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return 1 + movies.size();
    }

    private RecyclerView.ViewHolder getViewHolderByViewType(int viewType) {
        View view_movie_top5_filter = View.inflate(mContext, R.layout.movie_top_filter, null);
        View view_movie_list = View.inflate(mContext, R.layout.movie_list_view, null);
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case TYPE_TOP_FILTER:
                holder = new ViewHolder_Top_Filter(view_movie_top5_filter);
                break;
            case TYPE_MOVIES:
                holder = new ViewHolder_Movies(view_movie_list);
                break;
        }

        return holder;
    }




    class ViewHolder_Top_Filter extends RecyclerView.ViewHolder {
        public ImageView top_background;
        public TextView top_text;
        public TextView top1_movie;
        public TextView top2_movie;

        public SearchView search_view;
        public MultiStateToggleButton category_first;
        public MultiStateToggleButton category_second;

        public ViewHolder_Top_Filter(View itemView) {
            super(itemView);
            top_background = itemView.findViewById(R.id.MovieTop);
            top_text = itemView.findViewById(R.id.TopText);
            top1_movie = itemView.findViewById(R.id.Movie1);
            top2_movie = itemView.findViewById(R.id.Movie2);

            search_view = itemView.findViewById(R.id.searchView);
            category_first = itemView.findViewById(R.id.select_area);
            category_second = itemView.findViewById(R.id.select_type);


            category_first.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
                @Override
                public void onValueChanged(int position) {
                    Log.d("heyhey", "Position: " + position);
                    //update the movie list


                }
            });

        }
    }

    class ViewHolder_Movies extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView moviePic;
        public TextView movieNameYear;
        public TextView movieScore;
        public TextView movieInfo;


        public ViewHolder_Movies(View itemView) {
            super(itemView);
            moviePic = itemView.findViewById(R.id.MoviePic);
            movieNameYear = itemView.findViewById(R.id.MovieNameYear);
             movieScore = itemView.findViewById(R.id.MovieScore);
             movieInfo = itemView.findViewById(R.id.MovieInfo);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Log.d("click",   movies.get(getAdapterPosition() - 1).getTitle());
            // jump to the movie info activity
            Intent intent = new Intent(mContext, MovieInfoActivity.class);

            //intent.putExtra(MOVIE_ID, "" + movies.get(getAdapterPosition() - 1).getId());
            intent.putExtra(MOVIE_TITLE, movies.get(getAdapterPosition() - 1).getTitle());
            intent.putExtra(MOVIE_INFO, movies.get(getAdapterPosition() - 1).getInfo());
            intent.putExtra(MOVIE_SCORE, movies.get(getAdapterPosition() - 1).getScore());
            intent.putExtra(MOVIE_DESCRIPTION, movies.get(getAdapterPosition() - 1).getDescription());
            intent.putExtra(MOVIE_REVIEWS, movies.get(getAdapterPosition() - 1).getReviews());
            intent.putExtra(MOVIE_URL, movies.get(getAdapterPosition() - 1).getUrl());
            intent.putExtra(MOVIE_YEAR, movies.get(getAdapterPosition() - 1).getYear());

            Pair<View, String> p1 = Pair.create((View)moviePic, "profile");
            Pair<View, String> p2 = Pair.create((View) movieNameYear, "nameYear");
            Pair<View, String> p3 = Pair.create((View) movieInfo, "information");

            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((MainActivity)mContext, p1, p2, p3);

            mContext.startActivity(intent, options.toBundle());

        }
    }

}
