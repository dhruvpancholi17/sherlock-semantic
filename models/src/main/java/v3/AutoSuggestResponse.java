package v3;

import java.util.List;

/**
 * Created by dhruv.pancholi on 18/12/17.
 */
public class AutoSuggestResponse {
    private String payloadId;
    private List<QuerySuggestion> querySuggestions;
    private List<ProductSuggestion> productSuggestions;

    public AutoSuggestResponse() {
    }

    public AutoSuggestResponse(String payloadId, List<QuerySuggestion> querySuggestions, List<ProductSuggestion> productSuggestions) {
        this.payloadId = payloadId;
        this.querySuggestions = querySuggestions;
        this.productSuggestions = productSuggestions;
    }

    public String getPayloadId() {
        return payloadId;
    }

    public List<QuerySuggestion> getQuerySuggestions() {
        return querySuggestions;
    }

    public List<ProductSuggestion> getProductSuggestions() {
        return productSuggestions;
    }
}
