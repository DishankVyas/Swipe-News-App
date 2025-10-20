package com.nmims.bigmanting.helpers;

import com.nmims.bigmanting.models.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit interface for NewsAPI.org
 * Defines API endpoints for fetching news articles
 * Documentation: https://newsapi.org/docs/endpoints
 */
public interface NewsApiService {

    /**
     * Get top headlines
     * @param country Country code (e.g., "us", "in")
     * @param category Category (e.g., "technology", "sports", "business")
     * @param apiKey Your NewsAPI key
     * @return NewsResponse with articles
     */
    @GET("v2/top-headlines")
    Call<NewsResponse> getTopHeadlines(
            @Query("country") String country,
            @Query("category") String category,
            @Query("apiKey") String apiKey
    );

    /**
     * Search for news articles
     * @param query Search query
     * @param language Language code (e.g., "en")
     * @param sortBy Sort by (e.g., "publishedAt", "relevancy", "popularity")
     * @param apiKey Your NewsAPI key
     * @return NewsResponse with articles
     */
    @GET("v2/everything")
    Call<NewsResponse> searchNews(
            @Query("q") String query,
            @Query("language") String language,
            @Query("sortBy") String sortBy,
            @Query("apiKey") String apiKey
    );

    /**
     * Get top headlines by category only
     * @param category Category (e.g., "technology", "sports")
     * @param apiKey Your NewsAPI key
     * @return NewsResponse with articles
     */
    @GET("v2/top-headlines")
    Call<NewsResponse> getHeadlinesByCategory(
            @Query("category") String category,
            @Query("apiKey") String apiKey
    );

    /**
     * Get everything with query and date range
     * @param query Search query
     * @param from Start date (ISO 8601 format)
     * @param sortBy Sort by
     * @param apiKey Your NewsAPI key
     * @return NewsResponse with articles
     */
    @GET("v2/everything")
    Call<NewsResponse> getNewsByQueryAndDate(
            @Query("q") String query,
            @Query("from") String from,
            @Query("sortBy") String sortBy,
            @Query("apiKey") String apiKey
    );
}
