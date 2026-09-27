package org.example.courseplanner.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BookSearchResult {

    private List<BookDoc> docs;

    public List<BookDoc> getDocs() {
        return docs;
    }

    public void setDocs(List<BookDoc> docs) {
        this.docs = docs;
    }
}