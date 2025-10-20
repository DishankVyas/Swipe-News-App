package com.nmims.bigmanting.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nmims.bigmanting.R;
import com.nmims.bigmanting.models.ArticleModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for CardStackView - displays news articles in swipeable cards
 * Handles card binding and image loading with Glide
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<ArticleModel> articles;

    public CardAdapter(List<ArticleModel> articles) {
        this.articles = articles;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        ArticleModel article = articles.get(position);

        // Set title
        holder.titleText.setText(article.getTitle() != null ? article.getTitle() : "No Title");

        // Set description
        holder.descriptionText.setText(article.getDescription() != null
                ? article.getDescription() : "No description available");

        // Set source
        if (article.getSource() != null && article.getSource().getName() != null) {
            holder.sourceText.setText(article.getSource().getName());
        } else {
            holder.sourceText.setText("Unknown Source");
        }

        // Set published date
        holder.dateText.setText(formatDate(article.getPublishedAt()));

        // Set author
        if (article.getAuthor() != null && !article.getAuthor().isEmpty()) {
            holder.authorText.setText("By " + article.getAuthor());
            holder.authorText.setVisibility(View.VISIBLE);
        } else {
            holder.authorText.setVisibility(View.GONE);
        }

        // Load image with Glide
        if (article.getUrlToImage() != null && !article.getUrlToImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(article.getUrlToImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_news)
                    .error(R.drawable.placeholder_news)
                    .centerCrop()
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder_news);
        }
    }

    @Override
    public int getItemCount() {
        return articles != null ? articles.size() : 0;
    }

    /**
     * Update articles list
     * @param newArticles New list of articles
     */
    public void updateArticles(List<ArticleModel> newArticles) {
        this.articles = newArticles;
        notifyDataSetChanged();
    }

    /**
     * Get article at position
     * @param position Position in list
     * @return ArticleModel
     */
    public ArticleModel getArticleAt(int position) {
        if (position >= 0 && position < articles.size()) {
            return articles.get(position);
        }
        return null;
    }

    /**
     * Format ISO 8601 date to readable format
     * @param dateString ISO date string
     * @return Formatted date string
     */
    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "Unknown date";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return date != null ? outputFormat.format(date) : dateString;
        } catch (ParseException e) {
            return dateString;
        }
    }

    /**
     * ViewHolder for card items
     */
    static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleText;
        TextView descriptionText;
        TextView sourceText;
        TextView dateText;
        TextView authorText;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.card_image);
            titleText = itemView.findViewById(R.id.card_title);
            descriptionText = itemView.findViewById(R.id.card_description);
            sourceText = itemView.findViewById(R.id.card_source);
            dateText = itemView.findViewById(R.id.card_date);
            authorText = itemView.findViewById(R.id.card_author);
        }
    }
}
