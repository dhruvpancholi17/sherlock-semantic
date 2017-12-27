package com.flipkart.sherlock.semantic.autosuggest.dao;

import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.flipkart.sherlock.semantic.common.dao.mysql.CompleteTableDao;
import com.flipkart.sherlock.semantic.common.dao.mysql.CompleteTableDao.AutoSuggestColdStart;
import com.flipkart.sherlock.semantic.v4.V4FlashSuggestion;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by dhruv.pancholi on 31/05/17.
 */
@Slf4j
@Singleton
public class AutoSuggestColdStartDao extends AbstractReloadableMapCache<List<V4FlashSuggestion>> {

    private static final String DUMMY_VERSION = "dummy_version";

    @Inject
    public AutoSuggestColdStartDao(CompleteTableDao completeTableDao, JsonSeDe jsonSeDe) {
        super(completeTableDao, jsonSeDe, 1, TimeUnit.DAYS);
    }

    @Override
    protected Map<String, List<V4FlashSuggestion>> getFromSource() {
        List<V4FlashSuggestion> v4FlashSuggestions = new ArrayList<>();

        List<AutoSuggestColdStart> autoSuggestColdStarts = completeTableDao.getAutoSuggestColdStarts();
        autoSuggestColdStarts.sort(Comparator.comparingInt(AutoSuggestColdStart::getPosition));

        for (AutoSuggestColdStart autoSuggestColdStart : autoSuggestColdStarts) {
            try {
                V4FlashSuggestion v4FlashSuggestion = jsonSeDe.readValue(autoSuggestColdStart.getContent(), V4FlashSuggestion.class);
                v4FlashSuggestions.add(v4FlashSuggestion);
            } catch (Exception e) {
                log.error("Unable to process the row: {} {} {}",
                        autoSuggestColdStart.getPosition(),
                        autoSuggestColdStart.getContent(),
                        e);
            }
        }
        return ImmutableMap.of(DUMMY_VERSION, v4FlashSuggestions);
    }

    public List<V4FlashSuggestion> getColdStartRows() {
        return getCached().get(DUMMY_VERSION);
    }
}
