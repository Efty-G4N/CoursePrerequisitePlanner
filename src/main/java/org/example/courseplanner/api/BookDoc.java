package org.example.courseplanner.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BookDoc {

    private String title;
    private List<String> author_name;
    private Integer first_publish_year;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(List<String> author_name) {
        this.author_name = author_name;
    }

    public Integer getFirst_publish_year() {
        return first_publish_year;
    }

    public void setFirst_publish_year(Integer first_publish_year) {
        this.first_publish_year = first_publish_year;
    }

    // a simple helper to build a readable one-line display string
    public String toDisplayString() {
        String authors = (author_name == null || author_name.isEmpty())
                ? "Unknown author"
                : String.join(", ", author_name);

        String year = (first_publish_year == null)
                ? "year unknown"
                : String.valueOf(first_publish_year);

        return title + " — " + authors + " (" + year + ")";
    }
}