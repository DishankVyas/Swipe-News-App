package com.nmims.bigmanting.utils;

import com.nmims.bigmanting.BuildConfig;

/**
 * Constants used throughout the app
 */
public class Constants {

    // NewsAPI Configuration
    // API key is loaded from local.properties file (not committed to version control)
    // Get your free key at: https://newsapi.org/register
    // Add it to local.properties as: NEWS_API_KEY=your_api_key_here
    public static final String NEWS_API_KEY = BuildConfig.NEWS_API_KEY;

    // News Categories
    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_BUSINESS = "business";
    public static final String CATEGORY_TECHNOLOGY = "technology";
    public static final String CATEGORY_SPORTS = "sports";
    public static final String CATEGORY_ENTERTAINMENT = "entertainment";
    public static final String CATEGORY_HEALTH = "health";
    public static final String CATEGORY_SCIENCE = "science";

    // Countries
    public static final String COUNTRY_US = "us";
    public static final String COUNTRY_IN = "in";
    public static final String COUNTRY_GB = "gb";

    // Intent extras
    public static final String EXTRA_ARTICLE = "extra_article";
}


