package it.enricocandino.search.controller;

import it.enricocandino.search.model.QueryResult;
import it.enricocandino.search.model.QuerySite;
import it.enricocandino.search.model.User;
import it.enricocandino.search.model.UserSession;
import org.apache.solr.client.solrj.SolrClient;
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

/**
 * @author Enrico Candino
 */
@RestController
@RequestMapping(value = "/rest/search")
public class SearchController {

    @Autowired
    private UserSession userSession;

    @RequestMapping(value = "/{query}", method = RequestMethod.GET)
    public ResponseEntity<QueryResult> getUser(
            @PathVariable String query,
            @RequestParam(required = false) Integer page
    ) throws Exception {

        int start;
        if(page == null) {
            page = 1;
        }
        start = (page-1) * 10;

        SolrClient solrClient = new HttpSolrClient("http://localhost:8983/solr/warc_core");

        // http://localhost:8983/solr/spellCheckCompRH?q=epod&spellcheck=on&spellcheck.build=true
        ModifiableSolrParams params = new ModifiableSolrParams();
        params.set("qt", "query");
        params.set("q", query);
        params.set("start", start);

        params.set("spellcheck", "on");
        params.set("spellcheck.q", query);

        params.set("hl", "on");
        params.set("hl.simple.pre", "<em>");
        params.set("hl.simple.post", "</em>");
        params.set("hl.usePhraseHighlighter", "on");

        params.set("qf", "text^0.5 title^10.0");


        QueryResponse response = solrClient.query(params);

        QueryResult result = new QueryResult();

        result.setQ(query);
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

        List<SpellCheckResponse.Suggestion> suggestions = response.getSpellCheckResponse().getSuggestions();
        if(suggestions != null) {
            for(SpellCheckResponse.Suggestion suggestion : suggestions) {
                result.setSuggestions(suggestion.getAlternatives());
            }
        }

        userSession.setQuery(query);
        userSession.setPage(page);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
