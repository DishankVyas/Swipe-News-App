package com.nmims.bigmanting.helpers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nmims.bigmanting.models.ArticleModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for Firebase Firestore operations
 * Handles all database read/write operations for saved articles
 * Path structure: users/{uid}/saved_articles/{articleId}
 */
public class FirestoreHelper {

    private static final String TAG = "FirestoreHelper";
    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_SAVED_ARTICLES = "saved_articles";

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public FirestoreHelper() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }

    /**
     * Save an article to Firestore for the current user
     * @param article Article to save
     * @param callback Callback with success/failure
     */
    public void saveArticle(@NonNull ArticleModel article, @NonNull FirestoreCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onFailure("User not authenticated");
            return;
        }

        // Set saved timestamp
        article.setSavedTimestamp(System.currentTimeMillis());

        // Generate unique article ID based on URL
        String articleId = generateArticleId(article.getUrl());
        article.setArticleId(articleId);

        // Save to Firestore: users/{uid}/saved_articles/{articleId}
        db.collection(COLLECTION_USERS)
                .document(user.getUid())
                .collection(COLLECTION_SAVED_ARTICLES)
                .document(articleId)
                .set(article.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Article saved successfully: " + article.getTitle());
                    callback.onSuccess("Article saved successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving article", e);
                    callback.onFailure("Failed to save article: " + e.getMessage());
                });
    }

    /**
     * Check if an article is already saved
     * @param articleUrl URL of the article
     * @param callback Callback with result
     */
    public void isArticleSaved(@NonNull String articleUrl, @NonNull SavedCheckCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onResult(false);
            return;
        }

        String articleId = generateArticleId(articleUrl);

        db.collection(COLLECTION_USERS)
                .document(user.getUid())
                .collection(COLLECTION_SAVED_ARTICLES)
                .document(articleId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    callback.onResult(documentSnapshot.exists());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error checking saved status", e);
                    callback.onResult(false);
                });
    }

    /**
     * Delete a saved article
     * @param articleUrl URL of the article
     * @param callback Callback with success/failure
     */
    public void deleteArticle(@NonNull String articleUrl, @NonNull FirestoreCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onFailure("User not authenticated");
            return;
        }

        String articleId = generateArticleId(articleUrl);

        db.collection(COLLECTION_USERS)
                .document(user.getUid())
                .collection(COLLECTION_SAVED_ARTICLES)
                .document(articleId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Article deleted successfully");
                    callback.onSuccess("Article removed from saved");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting article", e);
                    callback.onFailure("Failed to delete article: " + e.getMessage());
                });
    }

    /**
     * Get all saved articles for the current user
     * Sorted by saved timestamp (most recent first)
     * @param callback Callback with list of articles
     */
    public void getSavedArticles(@NonNull ArticlesCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onFailure("User not authenticated");
            return;
        }

        db.collection(COLLECTION_USERS)
                .document(user.getUid())
                .collection(COLLECTION_SAVED_ARTICLES)
                .orderBy("savedTimestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ArticleModel> articles = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        ArticleModel article = ArticleModel.fromMap(doc.getData());
                        article.setArticleId(doc.getId());
                        articles.add(article);
                    }
                    Log.d(TAG, "Retrieved " + articles.size() + " saved articles");
                    callback.onSuccess(articles);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error retrieving saved articles", e);
                    callback.onFailure("Failed to load saved articles: " + e.getMessage());
                });
    }

    /**
     * Listen to real-time updates for saved articles
     * @param listener Listener for real-time updates
     */
    public void listenToSavedArticles(@NonNull ArticlesCallback listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            listener.onFailure("User not authenticated");
            return;
        }

        db.collection(COLLECTION_USERS)
                .document(user.getUid())
                .collection(COLLECTION_SAVED_ARTICLES)
                .orderBy("savedTimestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Listen failed", error);
                        listener.onFailure("Failed to listen to changes: " + error.getMessage());
                        return;
                    }

                    if (queryDocumentSnapshots != null) {
                        List<ArticleModel> articles = new ArrayList<>();
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            ArticleModel article = ArticleModel.fromMap(doc.getData());
                            article.setArticleId(doc.getId());
                            articles.add(article);
                        }
                        Log.d(TAG, "Real-time update: " + articles.size() + " articles");
                        listener.onSuccess(articles);
                    }
                });
    }

    /**
     * Generate a unique article ID from URL
     * Uses hash code to create consistent IDs
     * @param url Article URL
     * @return Unique article ID
     */
    private String generateArticleId(String url) {
        if (url == null || url.isEmpty()) {
            return String.valueOf(System.currentTimeMillis());
        }
        // Use URL hash as ID for consistency
        return "article_" + Math.abs(url.hashCode());
    }

    // Callback interfaces
    public interface FirestoreCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }

    public interface ArticlesCallback {
        void onSuccess(List<ArticleModel> articles);
        void onFailure(String error);
    }

    public interface SavedCheckCallback {
        void onResult(boolean isSaved);
    }
}
