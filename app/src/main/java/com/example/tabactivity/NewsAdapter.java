package com.example.tabactivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.example.tabactivity.DatabaseHandler.BookmarkDbHelper;

import java.util.ArrayList;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder>{

    private ArrayList<News> mList;
    private NewsAdapterOnClickHandler mClickHandler;
    private BookmarkDbHelper Db;


    public interface NewsAdapterOnClickHandler{
        void onClick(News news);
    }

    public NewsAdapter(NewsAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new NewsViewHolder(view);
    }

    @Override

    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {

        News news = mList.get(position);
        holder.titleText.setText(news.headline);
        holder.section_text.setText(news.section);
        holder.timeText.setText(news.date);
        GlideApp.with(holder.itemView.getContext()).load(news.imageUrl).fallback(R.mipmap.no_image).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if(mList ==null) return 0;
        return mList.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements RecyclerView.OnClickListener{

        public ImageView imageView;
        public TextView titleText;
        public TextView section_text;
        public Button bookmarkBtn;
        public Button shareButton;
        public TextView timeText;
        public int state=1;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView= itemView.findViewById(R.id.image_view);
            imageView.setClipToOutline(true);
            titleText= itemView.findViewById(R.id.title);
            section_text= itemView.findViewById(R.id.section);
            shareButton = itemView.findViewById(R.id.share_icon);
            timeText = itemView.findViewById(R.id.time);
//            Db= new BookmarkDbHelper(itemView.getContext());

//            bookmarkBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(state==1) {
//                        bookmarkBtn.setBackgroundResource(R.drawable.ic_baseline_bookmark_24);
//                        state=2;
//                        Toast.makeText(itemView.getContext(),"Added to Bookmarks",Toast.LENGTH_SHORT).show();
//                        int position =getAdapterPosition();
//                        News news = mList.get(position);
//                        Db.insertBookmark(news.imageUrl,news.url,news.headline,news.section);
//
//                    }else if(state==2){
//                        bookmarkBtn.setBackgroundResource(R.drawable.ic_baseline_bookmark_border_24);
//                        state=1;
//                    }
//                }
//            });
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent shareIntent =new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT,"SHARED NEWS");
                    int position =getAdapterPosition();
                    News news = mList.get(position);
                    String body= news.headline+"\n"+news.url;
                    shareIntent.putExtra(Intent.EXTRA_TEXT,body);
                    itemView.getContext().startActivity(Intent.createChooser(shareIntent,"Share with:"));
                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int itemPosition = getAdapterPosition();
            News news = mList.get(itemPosition);
            mClickHandler.onClick(news);
        }
    }
    public void setNewsData(ArrayList<News> list){
        mList = list;
        notifyDataSetChanged();
    }
}
