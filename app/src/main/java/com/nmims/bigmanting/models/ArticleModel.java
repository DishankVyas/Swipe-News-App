package com.nmims.bigmanting.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * POJO (Plain Old Java Object) model for News Articles
 * Used for both NewsAPI response parsing and Firestore storage
 * Implements Serializable to pass between activities via Intent
 */
public class ArticleModel implements Serializable {

    private String title;
    private String description;
    private String content;
    private String url;
    private String urlToImage;
    private String publishedAt;
    private String author;
    private SourceModel source;

    // Firestore-specific fields
    @Exclude
    private String articleId; // Document ID in Firestore
    private long savedTimestamp; // When the article was saved

    // Empty constructor required for Firestore deserialization
    public ArticleModel() {
    }

    // Full constructor
    public ArticleModel(String title, String description, String content, String url,
                        String urlToImage, String publishedAt, String author, SourceModel source) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
        this.author = author;
        this.source = source;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public SourceModel getSource() {
        return source;
    }

    public void setSource(SourceModel source) {
        this.source = source;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public long getSavedTimestamp() {
        return savedTimestamp;
    }

    public void setSavedTimestamp(long savedTimestamp) {
        this.savedTimestamp = savedTimestamp;
    }

    /**
     * Convert ArticleModel to Map for Firestore storage
     * @return Map representation of the article
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("description", description);
        map.put("content", content);
        map.put("url", url);
        map.put("urlToImage", urlToImage);
        map.put("publishedAt", publishedAt);
        map.put("author", author);
        map.put("savedTimestamp", savedTimestamp);

        if (source != null) {
            Map<String, Object> sourceMap = new HashMap<>();
            sourceMap.put("id", source.getId());
            sourceMap.put("name", source.getName());
            map.put("source", sourceMap);
        }

        return map;
    }

    /**
     * Create ArticleModel from Firestore Map
     * @param map Map from Firestore document
     * @return ArticleModel instance
     */
    public static ArticleModel fromMap(Map<String, Object> map) {
        ArticleModel article = new ArticleModel();
        article.setTitle((String) map.get("title"));
        article.setDescription((String) map.get("description"));
        article.setContent((String) map.get("content"));
        article.setUrl((String) map.get("url"));
        article.setUrlToImage((String) map.get("urlToImage"));
        article.setPublishedAt((String) map.get("publishedAt"));
        article.setAuthor((String) map.get("author"));

        Object timestamp = map.get("savedTimestamp");
        if (timestamp instanceof Long) {
            article.setSavedTimestamp((Long) timestamp);
        }

        // Parse source if exists
        Object sourceObj = map.get("source");
        if (sourceObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> sourceMap = (Map<String, Object>) sourceObj;
            SourceModel source = new SourceModel(
                    (String) sourceMap.get("id"),
                    (String) sourceMap.get("name")
            );
            article.setSource(source);
        }

        return article;
    }
}
