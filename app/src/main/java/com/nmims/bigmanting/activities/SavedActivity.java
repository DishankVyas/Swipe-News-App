package com.nmims.bigmanting.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nmims.bigmanting.R;
import com.nmims.bigmanting.adapters.SavedAdapter;
import com.nmims.bigmanting.helpers.FirestoreHelper;
import com.nmims.bigmanting.models.ArticleModel;

import java.util.ArrayList;
import java.util.List;

/**
 * SavedActivity - Display all saved articles
 * Shows articles saved from main feed
 * Uses Firestore real-time listener for live updates
 */
public class SavedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SavedAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private Toolbar toolbar;

    private FirestoreHelper firestoreHelper;
    private List<ArticleModel> savedArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        // Initialize views
        initViews();

        // Set up toolbar with back button
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Saved Articles");
        }

        // Initialize Firestore helper
        firestoreHelper = new FirestoreHelper();

        // Set up RecyclerView
        setupRecyclerView();

        // Load saved articles
        loadSavedArticles();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view_saved);
        progressBar = findViewById(R.id.progress_bar);
        emptyView = findViewById(R.id.empty_view);
    }

    private void setupRecyclerView() {
        savedArticles = new ArrayList<>();
        adapter = new SavedAdapter(this, savedArticles);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Set deletion listener
        adapter.setOnArticleDeletedListener(() -> {
            // Check if list is empty after deletion
            if (savedArticles.isEmpty()) {
                showEmptyView(true);
            }
        });
    }

    /**
     * Load saved articles from Firestore with real-time listener
     */
    private void loadSavedArticles() {
        showLoading(true);

        firestoreHelper.listenToSavedArticles(new FirestoreHelper.ArticlesCallback() {
            @Override
            public void onSuccess(List<ArticleModel> articles) {
                showLoading(false);

                if (articles != null && !articles.isEmpty()) {
                    savedArticles.clear();
                    savedArticles.addAll(articles);
                    adapter.updateArticles(savedArticles);
                    showEmptyView(false);
                } else {
                    showEmptyView(true);
                }
            }

            @Override
            public void onFailure(String error) {
                showLoading(false);
                showEmptyView(true);
                Toast.makeText(SavedActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Show/hide loading indicator
     */
    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * Show/hide empty view
     */
    private void showEmptyView(boolean show) {
        emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
