package com.pixellore.gallerysearch.utils;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pixellore.gallerysearch.R;

import java.util.ArrayList;

public class TutorialRecyclerAdapter extends RecyclerView.Adapter<TutorialRecyclerAdapter.TutorialViewHolder> {

    private ArrayList<TutorialPage> mTutorialPagesData;
    private Context mContext;
    private OnItemClickListener mClickListener;

    public TutorialRecyclerAdapter(ArrayList<TutorialPage> pageData, Context context, OnItemClickListener listener) {
        mTutorialPagesData = pageData;
        mContext = context;
        mClickListener = listener;
    }

    @NonNull
    @Override
    public TutorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tutorial_item, parent, false);
        return new TutorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TutorialViewHolder holder, int position) {
        TutorialPage page = mTutorialPagesData.get(position);

        holder.heading.setText(page.getHeading());
        holder.explanation.setText(page.getExplanation());
        holder.graphics.setImageResource(page.getGraphicsId());

        // If isSkip() returns "true", set text as 'SKIP', else set text as 'DONE'
        if (page.isSkip()) {
            holder.skipDone.setText(R.string.skip);
            holder.skipDone.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.mipmap.outline_keyboard_double_arrow_right_white_24, 0,0,0);
        } else {
            holder.skipDone.setText(R.string.done);
            holder.skipDone.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.mipmap.outline_done_all_white_24,0,0,0);
        }

        // set click listener for the page
        holder.bind(page, mClickListener);
    }

    @Override
    public int getItemCount() {
        return mTutorialPagesData.size();
    }

    public class TutorialViewHolder extends RecyclerView.ViewHolder{

        TextView heading;
        ImageView graphics;
        TextView explanation;
        TextView skipDone;

        public TutorialViewHolder(@NonNull View itemView) {
            super(itemView);

            heading = itemView.findViewById(R.id.heading);
            graphics = itemView.findViewById(R.id.graphics);
            explanation = itemView.findViewById(R.id.explanation);
            skipDone = itemView.findViewById(R.id.skip_done);
        }

        public void bind(TutorialPage page, OnItemClickListener listener) {
            // set the click listener on the 'skipDone' view instead of the whole itemView
            skipDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(page);
                }
            });
        }
    }


    public interface OnItemClickListener {
        void onItemClick(TutorialPage pageItem);
    }

}
