package com.flipkart.sherlock.semantic.autosuggest.flow;

import com.flipkart.sherlock.semantic.autosuggest.dao.StorePathCanonicalTitleDao;
import com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer;
import com.flipkart.sherlock.semantic.autosuggest.models.AutoSuggestDoc;
import com.flipkart.sherlock.semantic.autosuggest.models.ProductStore;
import com.flipkart.sherlock.semantic.autosuggest.models.Store;
import com.flipkart.sherlock.semantic.autosuggest.utils.JsonSeDe;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer.DEFAULT_MARKET_PLACE_IDS;
import static com.flipkart.sherlock.semantic.autosuggest.helpers.MarketAnalyzer.FLIP_MART;

/**
 * Created by dhruv.pancholi on 27/04/17.
 */
@Singleton
public class StoreHandler {

    @Inject
    private JsonSeDe jsonSeDe;

    @Inject
    private MarketAnalyzer marketAnalyzer;

    @Inject
    private StorePathCanonicalTitleDao storePathCanonicalTitleDao;

    private static final List<Store> EMPTY_STORE_LIST = new ArrayList<>();

    private static final List<String> ALL_STORES = Arrays.asList("search.flipkart.com", "all", "m.flipkart.com", "flipkart.com");

    /**
     * @param productStores   A string field retrieved from meta
     * @param maxStores       Maximum number of stores to be returned
     * @param contextualStore Return only the stores, which starts with this store as root
     * @param marketPlaceIds  Filter out stores, whose marketPlaceId is present in this list
     * @param storeClassifier
     * @return
     */
    public List<Store> getStoresFromProductStore(List<ProductStore> productStores, int maxStores, String contextualStore, List<String> marketPlaceIds, Map<String, Double> storeClassifier) {

        if (productStores == null) return EMPTY_STORE_LIST;

        if (storeClassifier != null) {
            int totalCount = 0;
            double totalDdCount = 0;
            List<ProductStore> productStoreList = new ArrayList<>(productStores.size());
            for (ProductStore productStore : productStores) {
                ProductStore newProductStore = null;
                if (storeClassifier.containsKey(productStore.getStore())) {
                    newProductStore = new ProductStore(
                            (int) (productStore.getCount() * storeClassifier.get(productStore.getStore()) * 100),
                            productStore.getDdCount() * storeClassifier.get(productStore.getStore()) * 100,
                            productStore.getContrib(),
                            productStore.getDdContrib(),
                            productStore.getStore());
                } else {
                    newProductStore = productStore;
                }
                totalCount += newProductStore.getCount();
                totalDdCount += newProductStore.getDdCount();
                productStoreList.add(newProductStore);
            }
            productStores.clear();
            for (ProductStore productStore : productStoreList) {
                productStores.add(new ProductStore(productStore.getCount(),
                        productStore.getDdCount(),
                        productStore.getCount() * 100 / totalCount,
                        productStore.getDdCount() * 100 / totalDdCount,
                        productStore.getStore()));
            }
        }

        productStores.sort((o1, o2) -> Double.compare(o2.getDdCount(), o1.getDdCount()));

        if (contextualStore == null) contextualStore = "";
        if (marketPlaceIds == null) marketPlaceIds = DEFAULT_MARKET_PLACE_IDS;

        double cumulativeContrib = 0;
        List<Store> stores = new ArrayList<>();

        for (ProductStore productStore : productStores) {
            if (cumulativeContrib > 90) break;
            cumulativeContrib += productStore.getDdContrib();

            if (productStore.getDdContrib() <= 3) continue;
            String store = productStore.getStore();
            if (store == null) continue;
            store = removeAllStores(store);
            if (!store.startsWith(contextualStore)) continue;

            String marketPlaceId = marketAnalyzer.getMarketPlaceId(store);
            if (!marketPlaceIds.contains(marketPlaceId)) continue;
            String canonicalTitle = storePathCanonicalTitleDao.getCanonicalTitle(store);
            if (canonicalTitle == null) continue;
            stores.add(new Store(store, canonicalTitle, FLIP_MART.equals(marketPlaceId) ? "GROCERY" : "FLIPKART"));
        }

        stores = (stores.size() > maxStores) ? stores.subList(0, maxStores) : stores;

        return stores;
    }

    public int getNumberOfDocsWithStores(List<AutoSuggestDoc> autoSuggestDocs) {
        if (autoSuggestDocs.size() < 2) return 0;
        AutoSuggestDoc firstDoc = autoSuggestDocs.get(0);
        AutoSuggestDoc secondDoc = autoSuggestDocs.get(1);
        return (secondDoc.getCtrObj().getPHits() >= 0.25 * firstDoc.getCtrObj().getPHits()) ? 2 : 1;
    }

    public static String removeAllStores(String store) {
        if (store == null) return "";
        String[] nodes = store.split("/");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < nodes.length; i++) {
            if (ALL_STORES.contains(nodes[i])) continue;
            if (i == nodes.length - 1) sb.append(nodes[i]);
            else sb.append(nodes[i] + "/");
        }
        return sb.toString();
    }
}
