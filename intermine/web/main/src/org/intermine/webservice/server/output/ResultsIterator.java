package org.intermine.webservice.server.output;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.ObjectUtils;
import org.intermine.api.results.ResultElement;
import org.intermine.objectstore.query.Results;

public class ResultsIterator implements Iterator<List<ResultElement>> {

	private static Logger logger = Logger.getLogger(ResultsIterator.class);
	
	private int counter = 0;
	private int start = 0;
	private Integer end = null;
    private String filterTerm = null;
    private List<ResultElement> nextRow = null;
	
	private final Iterator<Object> subIter;
	
	public ResultsIterator(Results res) {
		this.subIter = res.iterator();
	}
	
	public ResultsIterator(Results res, int start, int size, String filterTerm) {
		this.subIter = res.iterator();
		this.start = start;
		this.end = start + size;
        this.filterTerm = StringUtils.lowerCase(filterTerm);
	}
	
	@Override
	public boolean hasNext() {
		scrollToStart();
		if (counter >= start && (end == null || counter < end)) {
            if (StringUtils.isBlank(filterTerm)) {
                return subIter.hasNext();
            } else {
                try {
                    nextRow = next();
                } catch (NoSuchElementException e) {
                    return false;
                }
                return nextRow != null;
            }
		}
		return false;
	}
	
	private void scrollToStart() {
		while (counter < start && subIter.hasNext()) {
			// throw away values we are not interested in.
            List<Object> l = (List<Object>) subIter.next();
            if (StringUtils.isBlank(filterTerm)) {
                counter++;
            } else {
                boolean contained = false;
                for (Object o: l) {
                    String n = ObjectUtils.toString(o).toLowerCase();
                    if (StringUtils.contains(n, filterTerm)) {
                        contained = true;
                    }
                } 
                if (contained) {
                    counter++;
                }
            }
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<ResultElement> next() {
		scrollToStart();
		if (counter >= end) {
			throw new NoSuchElementException();
		}
        if (nextRow != null) {
            List<ResultElement> ret = nextRow;
            nextRow = null;
            return ret;
        }
		List<Object> l = (List<Object>) subIter.next();
        if (StringUtils.isBlank(filterTerm)) {
            counter++;
        } else {
            boolean contained = false;
            for (Object o: l) {
                String n = ObjectUtils.toString(o).toLowerCase();
                if (StringUtils.contains(n, filterTerm)) {
                    contained = true;
                }
            } 
            if (contained) {
                counter++;
            } else {
                return next();
            }
        }
		List<ResultElement> ret = new ArrayList<ResultElement>();
		for (Object o: l) {
			ResultElement re = new ResultElement(o);
			ret.add(re);
		}
		return ret;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
