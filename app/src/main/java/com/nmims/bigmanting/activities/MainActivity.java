package com.nmims.bigmanting.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.nmims.bigmanting.R;
import com.nmims.bigmanting.adapters.CardAdapter;
import com.nmims.bigmanting.helpers.FirestoreHelper;
import com.nmims.bigmanting.helpers.NewsApiService;
import com.nmims.bigmanting.helpers.RetrofitClient;
import com.nmims.bigmanting.models.ArticleModel;
import com.nmims.bigmanting.models.NewsResponse;
import com.nmims.bigmanting.utils.Constants;
import com.nmims.bigmanting.utils.SharedPrefsHelper;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * MainActivity - News Feed with Swipeable Cards
 * Fetches news from NewsAPI and allows Tinder-style swiping
 * Swipe right = save to Firestore, Swipe left = skip
 */
public class MainActivity extends AppCompatActivity implements CardStackListener {

    private static final String TAG = "MainActivity";

    private CardStackView cardStackView;
    private CardStackLayoutManager layoutManager;
    private CardAdapter cardAdapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private ChipGroup categoryChips;
    private FloatingActionButton fabSaved;
    private Toolbar toolbar;

    private List<ArticleModel> articles;
    private NewsApiService newsApiService;
    private FirestoreHelper firestoreHelper;
    private SharedPrefsHelper prefsHelper;
    private String currentCategory = Constants.CATEGORY_GENERAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize helpers
        newsApiService = RetrofitClient.getInstance().getNewsApiService();
        firestoreHelper = new FirestoreHelper();
        prefsHelper = new SharedPrefsHelper(this);

        // Initialize views
        initViews();

        // Set up toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("SwipeNews");
        }

        // Set up CardStackView
        setupCardStackView();

        // Set up category filter chips
        setupCategoryChips();

        // Load news
        loadNews(currentCategory);

        // FAB to open saved articles
        fabSaved.setOnClickListener(v -> {
            Intent intent = new Intent(this, SavedActivity.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        cardStackView = findViewById(R.id.card_stack_view);
        progressBar = findViewById(R.id.progress_bar);
        emptyView = findViewById(R.id.empty_view);
        categoryChips = findViewById(R.id.category_chips);
        fabSaved = findViewById(R.id.fab_saved);
    }

    /**
     * Set up CardStackView with swipe configuration
     */
    private void setupCardStackView() {
        layoutManager = new CardStackLayoutManager(this, this);
        layoutManager.setStackFrom(StackFrom.Top);
        layoutManager.setVisibleCount(3);
        layoutManager.setTranslationInterval(8.0f);
        layoutManager.setScaleInterval(0.95f);
        layoutManager.setSwipeThreshold(0.3f);
        layoutManager.setMaxDegree(20.0f);
        layoutManager.setDirections(Direction.HORIZONTAL);
        layoutManager.setCanScrollHorizontal(true);
        layoutManager.setCanScrollVertical(false);
        layoutManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);

        articles = new ArrayList<>();
        cardAdapter = new CardAdapter(articles);

        cardStackView.setLayoutManager(layoutManager);
        cardStackView.setAdapter(cardAdapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }

    /**
     * Set up category filter chips
     */
    private void setupCategoryChips() {
        String[] categories = {
                Constants.CATEGORY_GENERAL,
                Constants.CATEGORY_TECHNOLOGY,
                Constants.CATEGORY_BUSINESS,
                Constants.CATEGORY_SPORTS,
                Constants.CATEGORY_ENTERTAINMENT,
                Constants.CATEGORY_HEALTH,
                Constants.CATEGORY_SCIENCE
        };

        for (String category : categories) {
            Chip chip = new Chip(this);
            chip.setText(category.substring(0, 1).toUpperCase() + category.substring(1));
            chip.setCheckable(true);
            chip.setChecked(category.equals(currentCategory));
            chip.setOnClickListener(v -> {
                currentCategory = category;
                loadNews(currentCategory);
            });
            categoryChips.addView(chip);
        }
    }

    /**
     * Load news from NewsAPI
     */
    private void loadNews(String category) {
        showLoading(true);

        // Get API key from Constants or SharedPrefs
        String apiKey = prefsHelper.getNewsApiKey();
        if (apiKey.isEmpty()) {
            apiKey = Constants.NEWS_API_KEY;
        }

        String finalApiKey = apiKey;
        Call<NewsResponse> call = newsApiService.getHeadlinesByCategory(category, finalApiKey);

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    NewsResponse newsResponse = response.body();
                    if (newsResponse.getArticles() != null && !newsResponse.getArticles().isEmpty()) {
                        articles.clear();
                        articles.addAll(newsResponse.getArticles());
                        cardAdapter.notifyDataSetChanged();
                        showEmptyView(false);
                        Log.d(TAG, "Loaded " + articles.size() + " articles");
                    } else {
                        showEmptyView(true);
                        Toast.makeText(MainActivity.this, "No articles found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showEmptyView(true);
                    Toast.makeText(MainActivity.this, "Failed to load news: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "API Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                showLoading(false);
                showEmptyView(true);
                Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Network error", t);
            }
        });
    }

    // CardStackListener methods
    @Override
    public void onCardDragging(Direction direction, float ratio) {
        // Optional: Add visual feedback while dragging
    }

    @Override
    public void onCardSwiped(Direction direction) {
        int position = layoutManager.getTopPosition() - 1;

        if (position < 0 || position >= articles.size()) {
            return;
        }

        ArticleModel swipedArticle = articles.get(position);

        if (direction == Direction.Right) {
            // Swipe right - Save article to Firestore
            saveArticle(swipedArticle);
        } else if (direction == Direction.Left) {
            // Swipe left - Skip (no action needed)
            Log.d(TAG, "Article skipped: " + swipedArticle.getTitle());
        }

        // Check if no more cards
        if (layoutManager.getTopPosition() == articles.size()) {
            showEmptyView(true);
            Toast.makeText(this, "No more articles. Pull to refresh!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCardRewound() {
        Log.d(TAG, "Card rewound");
    }

    @Override
    public void onCardCanceled() {
        Log.d(TAG, "Card canceled");
    }

    @Override
    public void onCardAppeared(View view, int position) {
        Log.d(TAG, "Card appeared: " + position);
    }

    @Override
    public void onCardDisappeared(View view, int position) {
        Log.d(TAG, "Card disappeared: " + position);
    }

    /**
     * Save article to Firestore
     */
    private void saveArticle(ArticleModel article) {
        if (article == null || article.getUrl() == null) {
            Toast.makeText(this, "Cannot save article", Toast.LENGTH_SHORT).show();
            return;
        }

        firestoreHelper.saveArticle(article, new FirestoreHelper.FirestoreCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(MainActivity.this, "Article saved!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Article saved: " + article.getTitle());
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(MainActivity.this, "Failed to save: " + error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Save error: " + error);
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
        cardStackView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            loadNews(currentCategory);
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Logout user
     */
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        prefsHelper.clearAll();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
