package org.clarksnut.models.jpa;

import org.apache.lucene.search.Sort;
import org.clarksnut.models.*;
import org.clarksnut.models.jpa.IndexedManagerType.Type;
import org.clarksnut.models.jpa.entity.DocumentEntity;
import org.clarksnut.query.Query;
import org.clarksnut.query.es.ESQueryParser;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.hibernate.search.elasticsearch.ElasticsearchQueries;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.sort.SortFieldContext;
import org.hibernate.search.query.engine.spi.QueryDescriptor;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateless
@IndexedManagerType(type = Type.ELASTICSEARCH)
public class JpaESDocumentProvider extends JpaAbstractDocumentProvider implements DocumentProvider {

    private static final Logger logger = Logger.getLogger(JpaESDocumentProvider.class);

    @PersistenceContext
    private EntityManager em;

    public String getQuery(DocumentQueryModel query, SpaceModel... space) {
        if (query.getFilters().isEmpty()) {
            throw new IllegalStateException("Invalid query, at least one query should be requested");
        }

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (Query q : query.getFilters()) {
            boolQueryBuilder.filter(ESQueryParser.toQueryBuilder(q, new DocumentFieldMapper()));
        }

        String spaceAssignedIds = Stream.of(space).map(SpaceModel::getAssignedId).collect(Collectors.joining(" "));
        DocumentFieldMapper fieldMapper = new DocumentFieldMapper();
        boolQueryBuilder.should(QueryBuilders.termsQuery(fieldMapper.apply(DocumentModel.SUPPLIER_ASSIGNED_ID), spaceAssignedIds));
        boolQueryBuilder.should(QueryBuilders.termsQuery(fieldMapper.apply(DocumentModel.CUSTOMER_ASSIGNED_ID), spaceAssignedIds));
        boolQueryBuilder.minimumShouldMatch(1);
        return boolQueryBuilder.toString();
    }

    public List<DocumentModel> searchDocuments(String filterText, int limit, SpaceModel... space) {
        return Collections.emptyList();
    }

    @Override
    public List<DocumentModel> getDocuments(String filterText, SpaceModel... space) {
        return getDocuments(filterText, -1, -1, space);
    }

    @Override
    public List<DocumentModel> getDocuments(String filterText, int offset, int limit, SpaceModel... space) {
        return Collections.emptyList();
    }

    @Override
    public SearchResultModel<DocumentModel> searchDocuments(DocumentQueryModel query, SpaceModel... space) {
        String esQuery = getQuery(query, space);

        // No results
        if (esQuery == null) {
            // User do not have any space assigned
            return new EmptySearchResultAdapter<>();
        }

        FullTextEntityManager fullTextEm = Search.getFullTextEntityManager(em);
        QueryDescriptor queryDescriptor = ElasticsearchQueries.fromJson("{\"query\":" + esQuery + "}");

        FullTextQuery fullTextQuery = fullTextEm.createFullTextQuery(queryDescriptor, DocumentEntity.class);

        // Sort
        Sort sort = null;
        if (query.getOrderBy() != null) {
            QueryBuilder queryBuilder = fullTextEm.getSearchFactory().buildQueryBuilder().forEntity(DocumentEntity.class).get();

            SortFieldContext sortFieldContext = queryBuilder.sort().byField(new DocumentFieldMapper().apply(query.getOrderBy()));
            if (query.isAsc()) {
                sort = sortFieldContext.asc().createSort();
            } else {
                sort = sortFieldContext.desc().createSort();
            }
        }

        if (sort != null) {
            fullTextQuery.setSort(sort);
        }

        // Pagination
        if (query.getOffset() != null && query.getOffset() != -1) {
            fullTextQuery.setFirstResult(query.getOffset());
        }
        if (query.getLimit() != null && query.getLimit() != -1) {
            fullTextQuery.setMaxResults(query.getLimit());
        }

        // Result
        List<DocumentEntity> resultList = fullTextQuery.getResultList();
        List<DocumentModel> items = resultList.stream()
                .map(f -> new DocumentAdapter(em, f))
                .collect(Collectors.toList());

        return new SearchResultModel<DocumentModel>() {
            @Override
            public List<DocumentModel> getItems() {
                return items;
            }

            @Override
            public int getTotalResults() {
                return fullTextQuery.getResultSize();
            }

            @Override
            public Map<String, List<FacetModel>> getFacets() {
                return Collections.emptyMap();
            }
        };
    }

}
