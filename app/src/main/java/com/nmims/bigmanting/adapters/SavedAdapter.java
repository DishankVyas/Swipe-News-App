package com.nmims.bigmanting.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nmims.bigmanting.R;
import com.nmims.bigmanting.helpers.FirestoreHelper;
import com.nmims.bigmanting.models.ArticleModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying saved articles in RecyclerView
 * Handles article deletion and sharing via implicit intents
 */
public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.SavedViewHolder> {

    private List<ArticleModel> articles;
    private final Context context;
    private final FirestoreHelper firestoreHelper;
    private OnArticleDeletedListener deleteListener;

    public SavedAdapter(Context context, List<ArticleModel> articles) {
        this.context = context;
        this.articles = articles;
        this.firestoreHelper = new FirestoreHelper();
    }

    @NonNull
    @Override
    public SavedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saved, parent, false);
        return new SavedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedViewHolder holder, int position) {
        ArticleModel article = articles.get(position);

        // Set title
        holder.titleText.setText(article.getTitle() != null ? article.getTitle() : "No Title");

        // Set source
        if (article.getSource() != null && article.getSource().getName() != null) {
            holder.sourceText.setText(article.getSource().getName());
        } else {
            holder.sourceText.setText("Unknown Source");
        }

        // Set date
        holder.dateText.setText(formatDate(article.getPublishedAt()));

        // Load image with Glide
        if (article.getUrlToImage() != null && !article.getUrlToImage().isEmpty()) {
            Glide.with(context)
                    .load(article.getUrlToImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_news)
                    .error(R.drawable.placeholder_news)
                    .centerCrop()
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder_news);
        }

        // Click to open article in browser
        holder.itemView.setOnClickListener(v -> {
            if (article.getUrl() != null && !article.getUrl().isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
                context.startActivity(browserIntent);
            } else {
                Toast.makeText(context, "Article URL not available", Toast.LENGTH_SHORT).show();
            }
        });

        // Share button - Implicit Intent
        holder.shareButton.setOnClickListener(v -> shareArticle(article));

        // Delete button
        holder.deleteButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                deleteArticle(article, adapterPosition);
            }
        });
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
     * Share article using implicit intent
     * @param article Article to share
     */
    private void shareArticle(ArticleModel article) {
        String shareText = article.getTitle() + "\n\n" +
                "Read more: " + article.getUrl() + "\n\n" +
                "Shared via SwipeNews";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, article.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        // Create chooser to show all sharing options
        Intent chooser = Intent.createChooser(shareIntent, "Share article via");
        context.startActivity(chooser);
    }

    /**
     * Delete article from Firestore
     * @param article Article to delete
     * @param position Position in adapter
     */
    private void deleteArticle(ArticleModel article, int position) {
        if (article.getUrl() == null) {
            Toast.makeText(context, "Cannot delete article", Toast.LENGTH_SHORT).show();
            return;
        }

        firestoreHelper.deleteArticle(article.getUrl(), new FirestoreHelper.FirestoreCallback() {
            @Override
            public void onSuccess(String message) {
                // Remove from local list and update UI
                articles.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, articles.size());
                Toast.makeText(context, "Article removed", Toast.LENGTH_SHORT).show();

                // Notify listener
                if (deleteListener != null) {
                    deleteListener.onArticleDeleted();
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context, "Failed to delete: " + error, Toast.LENGTH_SHORT).show();
            }
        });
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
     * Set listener for article deletion
     * @param listener Deletion listener
     */
    public void setOnArticleDeletedListener(OnArticleDeletedListener listener) {
        this.deleteListener = listener;
    }

    /**
     * Interface for article deletion callback
     */
    public interface OnArticleDeletedListener {
        void onArticleDeleted();
    }

    /**
     * ViewHolder for saved article items
     */
    static class SavedViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleText;
        TextView sourceText;
        TextView dateText;
        ImageButton shareButton;
        ImageButton deleteButton;

        public SavedViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.saved_image);
            titleText = itemView.findViewById(R.id.saved_title);
            sourceText = itemView.findViewById(R.id.saved_source);
            dateText = itemView.findViewById(R.id.saved_date);
            shareButton = itemView.findViewById(R.id.btn_share);
            deleteButton = itemView.findViewById(R.id.btn_delete);
        }
    }
}
