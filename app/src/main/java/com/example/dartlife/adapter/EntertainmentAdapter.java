package com.example.dartlife.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dartlife.R;
import com.example.dartlife.model.Entertainment;
import com.squareup.picasso.Picasso;

import java.util.List;


public class EntertainmentAdapter extends RecyclerView.Adapter<EntertainmentAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<Entertainment> mData;
    private ItemClickListener mClickListener;

    public EntertainmentAdapter(Context context, List<Entertainment> data){
        mInflater = LayoutInflater.from(context);
        mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = mInflater.inflate(R.layout.recycler_entertainment, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EntertainmentAdapter.ViewHolder viewHolder, int i) {
        Entertainment mCurrent = mData.get(i);
        viewHolder.EntertainmentTitle.setText(mCurrent.getTitle());
        Picasso.get()
                .load(mCurrent.getImageUrl())
                .into(viewHolder.EntertainmentImage);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView EntertainmentTitle;
        private ImageView EntertainmentImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            EntertainmentImage= itemView.findViewById(R.id.EntertainmentPicture);
            EntertainmentTitle = itemView.findViewById(R.id.EntertainmentTitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickListener != null){
                mClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }

}
