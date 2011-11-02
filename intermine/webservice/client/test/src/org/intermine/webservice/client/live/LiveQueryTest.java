package org.intermine.webservice.client.live;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.intermine.pathquery.PathQuery;
import org.intermine.pathquery.PathQueryBinding;
import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.exceptions.ServiceException;
import org.intermine.webservice.client.results.Page;
import org.intermine.webservice.client.services.QueryService;
import org.intermine.webservice.client.services.QueryService.NumericSummary;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

public class LiveQueryTest {

    static Map<String, PathQuery> queries;
    private static final String baseUrl = "http://localhost/intermine-test/service";
    private static final String authToken = "Z1a3D3U16cicCdS0T6y4bdN1SQh";
    private static final QueryService authorised = new ServiceFactory(baseUrl, authToken).getQueryService();
    private static final QueryService unauthorised = new ServiceFactory(baseUrl).getQueryService();
    private static final Page middle = new Page(5, 5);

    @BeforeClass
    public static void oneTimeSetup() {
        Reader reader = new InputStreamReader(LiveQueryTest.class.getResourceAsStream("queries.xml"));
        queries = PathQueryBinding.unmarshalPathQueries(reader, PathQuery.USERPROFILE_VERSION);
    }

    @Test
    public void count() {
        PathQuery test1 = queries.get("test1");
        assertEquals(15, unauthorised.getCount(test1.toXml()));

        assertEquals(15, unauthorised.getCount(test1));
    }

    @Test
    public void count2() {
        PathQuery test2 = queries.get("test2");
        assertEquals(4, authorised.getCount(test2.toXml()));

        assertEquals(4, authorised.getCount(test2));
    }

    @Test(expected=ServiceException.class)
    public void count3() {
        PathQuery test2 = queries.get("test2");
        assertEquals(4, unauthorised.getCount(test2));
    }

    @Test
    public void allJSON() throws JSONException {
        PathQuery test1 = queries.get("test1");
        List<JSONObject> results = unauthorised.getAllJSONResults(test1);
        assertEquals(15, results.size());
        assertEquals("EmployeeA1", results.get(0).getString("name"));
        assertEquals(29, results.get(14).getInt("age"));
    }

    @Test
    public void allJSON2() throws JSONException {
        PathQuery test1 = queries.get("test1");
        List<JSONObject> results = unauthorised.getAllJSONResults(test1.toXml());
        assertEquals(15, results.size());
        assertEquals("EmployeeA1", results.get(0).getString("name"));
        assertEquals(29, results.get(14).getInt("age"));
    }

    @Test
    public void someJSON() throws JSONException {
        PathQuery test1 = queries.get("test1");
        List<JSONObject> results = unauthorised.getJSONResults(test1, middle);
        assertEquals(5, results.size());
        assertEquals("Kai D\u00f6rfler", results.get(0).getString("name"));
        assertEquals(28, results.get(4).getInt("age"));
    }

    @Test
    public void someJSON2() throws JSONException {
        PathQuery test1 = queries.get("test1");
        List<JSONObject> results = unauthorised.getJSONResults(test1.toXml(), middle);
        assertEquals(5, results.size());
        assertEquals("Kai D\u00f6rfler", results.get(0).getString("name"));
        assertEquals(28, results.get(4).getInt("age"));
    }

    @Test
    public void allStrings() throws JSONException {
        PathQuery test1 = queries.get("test1");
        List<List<String>> results = unauthorised.getAllResults(test1);
        assertEquals(15, results.size());
        assertEquals("Andy Bernard", results.get(0).get(0));
        assertEquals("29", results.get(14).get(1));
    }

    @Test
    public void allStrings2() throws JSONException {
        PathQuery test1 = queries.get("test1");
        List<List<String>> results = unauthorised.getAllResults(test1.toXml());
        assertEquals(15, results.size());
        assertEquals("Andy Bernard", results.get(0).get(0));
        assertEquals("29", results.get(14).get(1));
    }

    @Test
    public void someStrings() throws JSONException {
        PathQuery test1 = queries.get("test1");
        List<List<String>> results = unauthorised.getResults(test1, middle);
        assertEquals(5, results.size());
        assertEquals("EmployeeA2", results.get(0).get(0));
        assertEquals("25", results.get(4).get(1));
    }

    @Test
    public void someStrings2() throws JSONException {
        PathQuery test1 = queries.get("test1");
        List<List<String>> results = unauthorised.getResults(test1.toXml(), middle);
        assertEquals(5, results.size());
        assertEquals("EmployeeA2", results.get(0).get(0));
        assertEquals("25", results.get(4).get(1));
    }

    @Test
    public void allObjects() throws JSONException {
        PathQuery test1 = queries.get("test1");
        List<List<Object>> results = unauthorised.getRowsAsLists(test1);
        assertEquals(15, results.size());
        assertEquals("Andy Bernard", (String) results.get(0).get(0));
        assertEquals(new Integer(29), (Integer) results.get(14).get(1));
    }

    @Test
    public void allObjects2() throws JSONException {
        PathQuery test1 = queries.get("test1");
        List<List<Object>> results = unauthorised.getRowsAsLists(test1.toXml());
        assertEquals(15, results.size());
        assertEquals("Andy Bernard", (String) results.get(0).get(0));
        assertEquals(new Integer(29), (Integer) results.get(14).get(1));
    }

    @Test
    public void someObjects() throws JSONException {
        PathQuery test1 = queries.get("test1");
        List<List<Object>> results = unauthorised.getRowsAsLists(test1, middle);
        assertEquals(5, results.size());
        assertEquals("EmployeeA2", (String) results.get(0).get(0));
        assertEquals(new Integer(25), (Integer) results.get(4).get(1));
    }

    @Test
    public void someObjects2() throws JSONException {
        PathQuery test1 = queries.get("test1");
        List<List<Object>> results = unauthorised.getRowsAsLists(test1.toXml(), middle);
        assertEquals(5, results.size());
        assertEquals("EmployeeA2", (String) results.get(0).get(0));
        assertEquals(new Integer(25), (Integer) results.get(4).get(1));
    }

    @Test
    public void allMaps() {
        PathQuery test1 = queries.get("test1");
        List<Map<String, Object>> results = unauthorised.getRowsAsMaps(test1);
        assertEquals(15, results.size());
        assertEquals("Andy Bernard", (String) results.get(0).get("name"));
        assertEquals(new Integer(29), (Integer) results.get(14).get("age"));
    }

    @Test
    public void allMaps2() {
        PathQuery test1 = queries.get("test1");
        List<Map<String, Object>> results = unauthorised.getRowsAsMaps(test1.toXml());
        assertEquals(15, results.size());
        assertEquals("Andy Bernard", (String) results.get(0).get("name"));
        assertEquals(new Integer(29), (Integer) results.get(14).get("age"));
    }

    @Test
    public void someMaps() {
        PathQuery test1 = queries.get("test1");
        List<Map<String, Object>> results = unauthorised.getRowsAsMaps(test1, middle);
        assertEquals(5, results.size());
        assertEquals("EmployeeA2", (String) results.get(0).get("name"));
        assertEquals(new Integer(25), (Integer) results.get(4).get("age"));
    }

    @Test
    public void someMaps2() {
        PathQuery test1 = queries.get("test1");
        List<Map<String, Object>> results = unauthorised.getRowsAsMaps(test1.toXml(), middle);
        assertEquals(5, results.size());
        assertEquals("EmployeeA2", (String) results.get(0).get("name"));
        assertEquals(new Integer(25), (Integer) results.get(4).get("age"));
    }

    @Test
    public void numericSummary() {
        PathQuery test1 = queries.get("test1");
        NumericSummary summary = unauthorised.getNumericSummary(test1, "age");
        assertEquals("Employee.age", summary.getColumn());
        assertEquals(29, summary.getMax(), 0.0001);
        assertEquals(10, summary.getMin(), 0.0001);
        assertEquals(25.86666666, summary.getAverage(), 0.0001);
        assertEquals(4.983783225, summary.getStandardDeviation(), 0.0001);
    }

    @Test
    public void nonNumericSummary() {
        PathQuery test1 = queries.get("test1");
        Map<String, Integer> summary = unauthorised.getSummary(test1, "department.name");
        assertEquals(new Integer(3), summary.get("Warehouse"));
        int sum = 0;
        for (Integer i: summary.values()) {
            sum += i;
        }
        assertEquals(sum, unauthorised.getCount(test1));
    }
}
