package domain.content;

import java.util.Objects;

public class Manhwa {
    private int rank;
    private String title;
    private String synopsis;
    private String coverImageUrl;
    private double rating;
    private String chapters;
    private String publishedDate;
    private String tags;
    private String link;

    public Manhwa(int rank, String title, String synopsis, String coverImageUrl, double rating, String chapters, String publishedDate, String tags, String link) {
        this.rank = rank;
        this.title = title;
        this.synopsis = synopsis;
        this.coverImageUrl = coverImageUrl;
        this.rating = rating;
        this.chapters = chapters;
        this.publishedDate = publishedDate;
        this.tags = tags;
        this.link = link;
    }

    // Getters
    public int getRank() { return rank; }
    public String getTitle() { return title; }
    public String getSynopsis() { return synopsis; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public double getRating() { return rating; }
    public String getChapters() { return chapters; }
    public String getPublishedDate() { return publishedDate; }
    public String getTags() { return tags; }
    public String getLink() { return link; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Manhwa manhwa = (Manhwa) o;
        // Compare by link (unique identifier) or title if link is null
        if (link != null && manhwa.link != null) {
            return link.equals(manhwa.link);
        }
        return title != null && title.equalsIgnoreCase(manhwa.title);
    }

    @Override
    public int hashCode() {
        // Use link as primary hash, fallback to title
        return Objects.hash(link != null ? link : (title != null ? title.toLowerCase() : ""));
    }
}