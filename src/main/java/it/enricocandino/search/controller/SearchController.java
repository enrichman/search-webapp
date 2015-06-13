package it.enricocandino.search.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.enricocandino.search.model.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Enrico Candino
 */
@RestController
@RequestMapping(value = "/rest")
public class SearchController {

    @Autowired
    private UserSession userSession;

    @RequestMapping(value = "/search")
    public ResponseEntity<QueryResult> getUser(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean safe,
            @RequestParam(required = false) Integer page
    ) throws Exception {

        int start;
        if(page == null) {
            page = 1;
        }
        start = (page-1) * 10;

        SolrClient solrClient = new HttpSolrClient("http://localhost:8983/solr/warc_core");

        ModifiableSolrParams params = new ModifiableSolrParams();
        //params.set("qt", "query");

        if(!safe)
            params.set("q", q);
        else
            params.set("q", "safe:true AND "+q);

        params.set("defType", "edismax");
        params.set("start", start);

        params.set("spellcheck", "on");
        params.set("spellcheck.q", q);

        params.set("hl", "on");
        params.set("hl.simple.pre", "<em>");
        params.set("hl.simple.post", "</em>");
        params.set("hl.usePhraseHighlighter", "on");

        params.set("qf", "text^0.5 title^10.0");


        QueryResponse response = solrClient.query(params);

        QueryResult result = new QueryResult();

        result.setQ(q);
        result.setqTime(response.getQTime());
        result.setNumFound(response.getResults().getNumFound());

        List<QuerySite> sites = new ArrayList<>();

        SolrDocumentList results = response.getResults();
        for(SolrDocument doc : results) {

            QuerySite site = new QuerySite();

            String id = (String) doc.getFieldValue("id");
            String title = (String) doc.getFieldValue("title");
            String body = (String) doc.getFieldValue("text");

            List<String> highligths = response.getHighlighting().get(id).get("text");
            List<String> titleHighligths = response.getHighlighting().get(id).get("title");

            site.setUrl(id);
            site.setTitle(title);
            site.setBody(body);

            site.setHighlights(highligths);
            site.setTitleHighlights(titleHighligths);

            sites.add(site);
        }

        result.setQuerySites(sites);

        if(response.getSpellCheckResponse() != null) {
            List<SpellCheckResponse.Suggestion> suggestions = response.getSpellCheckResponse().getSuggestions();
            if (suggestions != null) {
                for (SpellCheckResponse.Suggestion suggestion : suggestions) {
                    result.setSuggestions(suggestion.getAlternatives());
                }
            }
        }

        userSession.setQuery(q);
        userSession.setPage(page);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/suggest")
    public ResponseEntity<Set<Suggestion>> getUser(
            @RequestParam String q
    ) throws Exception {

        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet("http://localhost:8983/solr/warc_core/suggest?wt=json&q="+q);
        HttpResponse response = client.execute(request);
        String json = EntityUtils.toString(response.getEntity());

        Set<Suggestion> suggestionList = new TreeSet<>();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        JsonNode suggestions = root.get("suggest").get("suggestQuery").get(q).get("suggestions");
        for(JsonNode sugg : suggestions) {
            Suggestion suggestion = new Suggestion();
            suggestion.setWord(sugg.get("term").textValue());
            Integer weight = q.equals(suggestion.getWord()) ? null : sugg.get("weight").asInt();
            suggestion.setWeight(weight);
            suggestionList.add(suggestion);
        }

        return new ResponseEntity<>(suggestionList, HttpStatus.OK);
    }

}
