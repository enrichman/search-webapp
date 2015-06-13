package it.enricocandino.search.model;

import java.util.List;

/**
 * @author Enrico Candino
 */
public class QueryResult {

    private String q;
    private Integer qTime;
    private Long numFound;
    private List<String> suggestions;
    private List<QuerySite> querySites;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public Integer getqTime() {
        return qTime;
    }

    public void setqTime(Integer qTime) {
        this.qTime = qTime;
    }

    public Long getNumFound() {
        return numFound;
    }

    public void setNumFound(Long numFound) {
        this.numFound = numFound;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    public List<QuerySite> getQuerySites() {
        return querySites;
    }

    public void setQuerySites(List<QuerySite> querySites) {
        this.querySites = querySites;
    }
}
