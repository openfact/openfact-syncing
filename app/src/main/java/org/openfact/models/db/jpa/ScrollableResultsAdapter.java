package org.openfact.models.db.jpa;

import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.openfact.models.ScrollableResultsModel;

import java.util.function.Function;

public class ScrollableResultsAdapter<T, R> implements ScrollableResultsModel<R> {

    private final Session session;
    private final StatelessSession statelessSession;
    private final ScrollableResults scroll;
    private final Function<T, R> mapper;

    public ScrollableResultsAdapter(Session session, StatelessSession statelessSession, ScrollableResults scroll, Function<T, R> mapper) {
        this.session = session;
        this.statelessSession = statelessSession;
        this.scroll = scroll;
        this.mapper = mapper;
    }

    @Override
    public void close() {
        scroll.close();
        statelessSession.close();
        session.close();
    }

    @Override
    public boolean next() {
        return scroll.next();
    }

    @Override
    public boolean previous() {
        return scroll.previous();
    }

    @Override
    public boolean scroll(int positions) {
        return scroll.scroll(positions);
    }

    @Override
    public boolean last() {
        return scroll.last();
    }

    @Override
    public boolean first() {
        return scroll.first();
    }

    @Override
    public void beforeFirst() {
        scroll.beforeFirst();
    }

    @Override
    public void afterLast() {
        scroll.afterLast();
    }

    @Override
    public boolean isFirst() {
        return scroll.isFirst();
    }

    @Override
    public boolean isLast() {
        return scroll.isLast();
    }

    @Override
    public int getRowNumber() {
        return scroll.getRowNumber();
    }

    @Override
    public boolean setRowNumber(int rowNumber) {
        return scroll.setRowNumber(rowNumber);
    }

    @Override
    public R get() {
        return mapper.apply((T) scroll.get(0));
    }

}