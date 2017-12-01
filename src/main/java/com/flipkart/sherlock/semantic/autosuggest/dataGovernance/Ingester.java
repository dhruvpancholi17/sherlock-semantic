package com.flipkart.sherlock.semantic.autosuggest.dataGovernance;


import com.flipkart.seraph.fkint.cp.discover_search.AutoSuggest;
import com.flipkart.seraph.fkint.cp.discover_search.AutoSuggestRequest;
import com.flipkart.seraph.fkint.cp.discover_search.AutoSuggestResponse;
import com.flipkart.seraph.schema.BaseSchema;
import com.flipkart.sherlock.semantic.autosuggest.models.Params;
import com.flipkart.sherlock.semantic.autosuggest.models.v4.V4SuggestionRow;
import exception.TransformException;
import service.Transformer;

import java.util.List;

public class Ingester implements Transformer {

    @Override
    public BaseSchema transform(Object o) throws TransformException {
        AutoSuggest autoSuggest = new AutoSuggest();
        AutoSuggestRequest autoSuggestRequest = new AutoSuggestRequest();
        AutoSuggestResponse autoSuggestResponse = new AutoSuggestResponse();

        return null;
    }

   // public void loadData(Params params, List<V4SuggestionRow> v4SuggestionRows, )
}
