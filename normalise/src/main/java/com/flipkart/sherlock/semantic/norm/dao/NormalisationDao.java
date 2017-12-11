package com.flipkart.sherlock.semantic.norm.dao;

import com.flipkart.sherlock.semantic.norm.entity.BrandInfo;
import com.flipkart.sherlock.semantic.norm.entity.PluralSingularInfo;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by anurag.laddha on 28/11/17.
 */

/**
 * DAO for fetching normalisation relatd info from data store
 */
public interface NormalisationDao {

    /**
     * Get all brands from data store
     */
    @RegisterMapper(BrandInfoMapper.class)
    @SqlQuery("SELECT * FROM brand_info")
    List<BrandInfo> getAllBrands();


    @RegisterMapper(PluralSingularInfoMapper.class)
    @SqlQuery("SELECT * FROM plural_singular")
    List<PluralSingularInfo> getAllPluralSingularEntries();


    /**
     * Mapper to create Pojo for plural singular tuple from underlying data store
     */
    class PluralSingularInfoMapper implements ResultSetMapper<PluralSingularInfo> {
        @Override
        public PluralSingularInfo map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return new PluralSingularInfo(r.getString("plural"), r.getString("singular"));
        }
    }


    /**
     * Mapper to create Pojo for brand info from underlying data store
     */
    class BrandInfoMapper implements ResultSetMapper<BrandInfo> {
        @Override
        public BrandInfo map(int index, ResultSet r, StatementContext ctx) throws SQLException {
            return new BrandInfo(r.getString("brand"), r.getString("store"));
        }
    }
}
