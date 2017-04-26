package org.openfact.models;

import org.openfact.models.search.PagingModel;
import org.openfact.models.search.SearchCriteriaFilterOperator;
import org.openfact.models.search.SearchResultsModel;

import java.util.Date;
import java.util.List;

public interface DocumentQuery {

    DocumentQuery filterText(String filterText);

    DocumentQuery currencyCode(String... currencyCode);

    DocumentQuery documentType(String... documentType);

    /*
     * Supplier and Customer party*/
    SupplierQuery supplier(String assignedAccountId);
    CustomerQuery customer(String assignedAccountId);

    interface SupplierQuery {
        BuildQuery andCustomer(String assignedAccountId);
        BuildQuery orCustomer(String assignedAccountId);
        DocumentQuery buildQuery();
    }

    interface CustomerQuery {
        BuildQuery andSupplier(String assignedAccountId);
        BuildQuery orSupplier(String assignedAccountId);
        DocumentQuery buildQuery();
    }

    interface BuildQuery {
        DocumentQuery buildQuery();
    }

    /**
     * Just equals filters
     */
    DocumentQuery addFilter(String key, Object value, SearchCriteriaFilterOperator operator);

    DocumentQuery fromDate(Date fromDate, boolean include);
    DocumentQuery toDate(Date toDate, boolean include);

    EntityQuery entityQuery();

    CountQuery countQuery();

    interface EntityQuery {
        EntityQuery orderByAsc(String... attribute);

        EntityQuery orderByDesc(String... attribute);

        ListEntityQuery resultList();

        SearchResultEntityQuery searchResult();
    }

    interface ListEntityQuery {
        List<DocumentModel> getResultList();

        ListEntityQuery firstResult(int result);

        ListEntityQuery maxResults(int results);
    }

    interface SearchResultEntityQuery {
        SearchResultsModel<DocumentModel> getSearchResult();

        SearchResultsModel<DocumentModel> getSearchResult(PagingModel pagingModel);
    }

    interface CountQuery {
        int getTotalCount();
    }

}