package it.enricocandino.search.model;

import java.util.List;

/**
 * @author Enrico Candino
 */
public class QuerySite {

    private String url;
    private String title;
    private String body;

    private List<String> highlights;
    private List<String> titleHighlights;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getHighlights() {
        return highlights;
    }

    public void setHighlights(List<String> highlights) {
        this.highlights = highlights;
    }

    public List<String> getTitleHighlights() {
        return titleHighlights;
    }

    public void setTitleHighlights(List<String> titleHighlights) {
        this.titleHighlights = titleHighlights;
    }
}
