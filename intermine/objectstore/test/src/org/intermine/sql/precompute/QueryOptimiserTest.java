package org.intermine.sql.precompute;

/*
 * Copyright (C) 2002-2005 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import junit.framework.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.HashSet;

import org.intermine.sql.Database;
import org.intermine.sql.DatabaseFactory;
import org.intermine.sql.query.*;
import org.intermine.util.StringUtil;
import org.intermine.util.ConsistentSet;

public class QueryOptimiserTest extends TestCase
{
    private Query singleTableQuery, singleTableQueryWithFieldAlias;
    private Query singleTableQueryWithTableAlias, singleTableQueryNoConstraints;
    private Query twoSameTableQuery;
    private Database database;
    private Connection con;

    public QueryOptimiserTest(String arg1) {
        super(arg1);
        try {
            database = DatabaseFactory.getDatabase("db.unittest");
        } catch (Exception e) {
        }
    }

    public void setUp() throws Exception {
        singleTableQuery = new Query("SELECT mytable.a FROM mytable WHERE mytable.a = 1");
        singleTableQueryWithFieldAlias = new Query("SELECT mytable.a AS alias FROM mytable WHERE mytable.a = 1");
        singleTableQueryWithTableAlias = new Query("SELECT table1.a FROM mytable table1 WHERE table1.a = 1");
        singleTableQueryNoConstraints = new Query("SELECT mytable.a FROM mytable");
        twoSameTableQuery = new Query("SELECT table1.b, table2.a FROM mytable table1, mytable table2 WHERE table1.a = 1 AND table2.b < 3 AND table1.a = table2.join");
        con = database.getConnection();
    }

    public void tearDown() throws Exception {
        con.close();
    }

    public void testRemapAliases() throws Exception {
        AbstractTable t1 = new Table("table1", "alias1");
        AbstractTable t2 = new Table("table2", "alias2");
        AbstractTable t3 = new Table("table3", "alias3");
        AbstractTable t4 = new Table("table1", "alias4");
        AbstractTable t5 = new Table("table2", "alias5");
        AbstractTable t6 = new Table("table3", "alias6");
        AbstractTable t7 = new Table("table4", "alias1");

        Map map = new HashMap();
        map.put(t1, t4);
        map.put(t2, t5);
        map.put(t3, t6);

        Set tables = new ConsistentSet();
        tables.add(t4);
        tables.add(t5);
        tables.add(t6);
        tables.add(t7);

        QueryOptimiser.remapAliases(map, tables);

        assertEquals(t1, map.get(t1));
        assertEquals(t2, map.get(t2));
        assertEquals(t3, map.get(t3));
        assertTrue(!"alias1".equals(t7.getAlias()));
    }

    public void testCompareConstraints() throws Exception {
        Set equalsSet = new HashSet();
        assertTrue(QueryOptimiser.compareConstraints(twoSameTableQuery.getWhere(),
                                                     twoSameTableQuery.getWhere(), equalsSet));
        assertEquals(twoSameTableQuery.getWhere(), equalsSet);
        equalsSet = new HashSet();
        assertTrue(QueryOptimiser.compareConstraints(singleTableQueryNoConstraints.getWhere(),
                                                     singleTableQuery.getWhere(), equalsSet));
        assertEquals(singleTableQueryNoConstraints.getWhere(), equalsSet);
        equalsSet = new HashSet();
        assertTrue(QueryOptimiser.compareConstraints(singleTableQueryWithTableAlias.getWhere(),
                                                     twoSameTableQuery.getWhere(), equalsSet));
        assertEquals(singleTableQueryWithTableAlias.getWhere(), equalsSet);

        equalsSet = new HashSet();
        assertFalse(QueryOptimiser.compareConstraints(singleTableQuery.getWhere(),
                                                      singleTableQueryNoConstraints.getWhere(),
                                                      equalsSet));
        // equalsSet is now undefined.
        equalsSet = new HashSet();
        assertFalse(QueryOptimiser.compareConstraints(twoSameTableQuery.getWhere(),
                                                      singleTableQueryWithTableAlias.getWhere(),
                                                      equalsSet));
        // equalsSet is now undefined.
    }

    public void testCompareSelectLists() throws Exception {
        SelectValue v1 = new SelectValue(new Constant("hello"), "alias1");
        SelectValue v2 = new SelectValue(new Constant("hello"), "alias2");
        SelectValue v3 = new SelectValue(new Constant("a"), "alias3");
        SelectValue v4 = new SelectValue(new Constant("a"), "alias4");
        SelectValue v5 = new SelectValue(new Constant("a"), "alias5");
        SelectValue v6 = new SelectValue(new Constant("flibble"), "alias6");

        List l1 = new ArrayList();
        l1.add(v1);
        l1.add(v3);
        List l2 = new ArrayList();
        l2.add(v2);
        l2.add(v4);
        l2.add(v5);
        List l3 = new ArrayList();
        l3.add(v2);
        l3.add(v4);
        l3.add(v6);
        List l4 = new ArrayList();

        assertTrue(QueryOptimiser.compareSelectLists(l1, l2));
        assertTrue(QueryOptimiser.compareSelectLists(l2, l1));
        assertTrue(QueryOptimiser.compareSelectLists(l1, l1));
        assertTrue(QueryOptimiser.compareSelectLists(l1, l3));
        assertFalse(QueryOptimiser.compareSelectLists(l3, l1));
        assertTrue(QueryOptimiser.compareSelectLists(l4, l1));
        assertFalse(QueryOptimiser.compareSelectLists(l1, l4));
        assertTrue(QueryOptimiser.compareSelectLists(l4, l4));
    }

    public void testFindTableForAlias() throws Exception {
        Set set = new HashSet();
        Table t1 = new Table("table1", "alias1");
        Table t2 = new Table("table2", "alias2");
        Table t3 = new Table("table2", "alias3");
        set.add(t1);
        set.add(t2);
        set.add(t3);

        assertEquals(t1, QueryOptimiser.findTableForAlias("alias1", set));
        assertEquals(t2, QueryOptimiser.findTableForAlias("alias2", set));
        assertEquals(t3, QueryOptimiser.findTableForAlias("alias3", set));
        assertNull(QueryOptimiser.findTableForAlias("alias4", set));
    }

    public void testReconstructAbstractValue() throws Exception {
        Collection values = new HashSet();
        Table t1 = new Table("table1", "tablealias1");
        Table t2 = new Table("table2", "tablealias2");
        AbstractValue v1 = new Constant("c1");
        AbstractValue v2 = new Field("field1", t1);
        Function v3 = new Function(Function.PLUS);
        v3.add(v1);
        v3.add(v2);
        AbstractValue v4 = new Constant("c4");
        Function v6 = new Function(Function.MINUS);
        v6.add(v1);
        v6.add(v2);
        Function v7 = new Function(Function.MAX);
        v7.add(v2);
        AbstractValue v8 = new Function(Function.COUNT);
        AbstractValue v9 = new Field("field2", t1);
        AbstractValue v10 = new Field("field3", t2);
        Function v11 = new Function(Function.MULTIPLY);
        v11.add(v9);
        v11.add(v10);
        SelectValue s1 = new SelectValue(v1, "alias1");
        SelectValue s2 = new SelectValue(v2, "alias2");
        SelectValue s3 = new SelectValue(v3, "alias3");
        Map valueMap = new HashMap();
        valueMap.put(v1, s1);
        valueMap.put(v2, s2);
        valueMap.put(v3, s3);

        Table precomputedSqlTable = new Table("precomp1", "precompalias1");
        Set tableSet = new HashSet();
        tableSet.add(t1);

        AbstractValue ev1 = new Field("alias1", precomputedSqlTable);
        AbstractValue ev2 = new Field("alias2", precomputedSqlTable);
        AbstractValue ev3 = new Field("alias3", precomputedSqlTable);
        AbstractValue ev4 = v4;
        Function ev6 = new Function(Function.MINUS);
        ev6.add(new Field("alias1", precomputedSqlTable));
        ev6.add(new Field("alias2", precomputedSqlTable));
        Function ev7 = new Function(Function.MAX);
        ev7.add(new Field("alias2", precomputedSqlTable));
        AbstractValue ev8 = v8;
        AbstractValue ev10 = v10;

        assertEquals(ev1, QueryOptimiser.reconstructAbstractValue(v1, precomputedSqlTable, valueMap, tableSet, true));
        assertEquals(ev1, QueryOptimiser.reconstructAbstractValue(v1, precomputedSqlTable, valueMap, tableSet, false));
        assertEquals(ev2, QueryOptimiser.reconstructAbstractValue(v2, precomputedSqlTable, valueMap, tableSet, true));
        assertEquals(ev2, QueryOptimiser.reconstructAbstractValue(v2, precomputedSqlTable, valueMap, tableSet, false));
        assertEquals(ev3, QueryOptimiser.reconstructAbstractValue(v3, precomputedSqlTable, valueMap, tableSet, true));
        assertEquals(ev3, QueryOptimiser.reconstructAbstractValue(v3, precomputedSqlTable, valueMap, tableSet, false));
        assertEquals(ev4, QueryOptimiser.reconstructAbstractValue(v4, precomputedSqlTable, valueMap, tableSet, true));
        assertEquals(ev4, QueryOptimiser.reconstructAbstractValue(v4, precomputedSqlTable, valueMap, tableSet, false));
        assertEquals(ev6, QueryOptimiser.reconstructAbstractValue(v6, precomputedSqlTable, valueMap, tableSet, true));
        assertEquals(ev6, QueryOptimiser.reconstructAbstractValue(v6, precomputedSqlTable, valueMap, tableSet, false));
        try {
            QueryOptimiser.reconstructAbstractValue(v7, precomputedSqlTable, valueMap, tableSet, true);
            fail("Expected QueryOptimiserException");
        } catch (QueryOptimiserException e) {
        }
        assertEquals(ev7, QueryOptimiser.reconstructAbstractValue(v7, precomputedSqlTable, valueMap, tableSet, false));
        try {
            QueryOptimiser.reconstructAbstractValue(v8, precomputedSqlTable, valueMap, tableSet, true);
            fail("Expected QueryOptimiserException");
        } catch (QueryOptimiserException e) {
        }
        assertEquals(ev8, QueryOptimiser.reconstructAbstractValue(v8, precomputedSqlTable, valueMap, tableSet, false));
        try {
            QueryOptimiser.reconstructAbstractValue(v9, precomputedSqlTable, valueMap, tableSet, true);
            fail("Expected QueryOptimiserException");
        } catch (QueryOptimiserException e) {
        }
        try {
            QueryOptimiser.reconstructAbstractValue(v9, precomputedSqlTable, valueMap, tableSet, false);
            fail("Expected QueryOptimiserException");
        } catch (QueryOptimiserException e) {
        }
        assertEquals(ev10, QueryOptimiser.reconstructAbstractValue(v10, precomputedSqlTable, valueMap, tableSet, true));
        assertEquals(ev10, QueryOptimiser.reconstructAbstractValue(v10, precomputedSqlTable, valueMap, tableSet, false));
        try {
            QueryOptimiser.reconstructAbstractValue(v11, precomputedSqlTable, valueMap, tableSet, true);
            fail("Expected QueryOptimiserException");
        } catch (QueryOptimiserException e) {
        }
        try {
            QueryOptimiser.reconstructAbstractValue(v11, precomputedSqlTable, valueMap, tableSet, false);
            fail("Expected QueryOptimiserException");
        } catch (QueryOptimiserException e) {
        }

        List list = new ArrayList();
        list.add(v1);
        list.add(v2);
        list.add(v3);
        list.add(v4);
        list.add(v6);
        list.add(v7);
        list.add(v8);
        list.add(v10);
        List elist = new ArrayList();
        elist.add(ev1);
        elist.add(ev2);
        elist.add(ev3);
        elist.add(ev4);
        elist.add(ev6);
        elist.add(ev7);
        elist.add(ev8);
        elist.add(ev10);
        List newList = new ArrayList();
        QueryOptimiser.reconstructAbstractValues(list, precomputedSqlTable, valueMap, tableSet, false, newList);
        assertEquals(elist, newList);
    }

    public void testReconstructSelectValues() throws Exception {
        Collection values = new HashSet();
        AbstractValue v1 = new Constant("c1");
        AbstractValue v2 = new Constant("c2");
        AbstractValue v3 = new Constant("c3");
        AbstractValue v4 = new Constant("c4");
        SelectValue sq1 = new SelectValue(v1, "queryalias1");
        SelectValue sq2 = new SelectValue(v2, "queryalias2");
        SelectValue sq3 = new SelectValue(v3, "queryalias3");
        SelectValue sq4 = new SelectValue(v4, "queryalias4");
        SelectValue sp1 = new SelectValue(v1, "precompalias1");
        SelectValue sp2 = new SelectValue(v2, "precompalias2");
        Map valueMap = new HashMap();
        valueMap.put(v1, sp1);
        valueMap.put(v2, sp2);
        List oldSelect = new ArrayList();
        oldSelect.add(sq1);
        oldSelect.add(sq2);
        oldSelect.add(sq3);
        oldSelect.add(sq4);
        Query newQuery = new Query();
        Table precomputedSqlTable = new Table("precomp1", "precomptablealias1");
        Set tableSet = new HashSet();
        QueryOptimiser.reconstructSelectValues(oldSelect, precomputedSqlTable, valueMap, tableSet, true, newQuery);

        SelectValue se1 = new SelectValue(new Field("precompalias1", precomputedSqlTable), "queryalias1");
        SelectValue se2 = new SelectValue(new Field("precompalias2", precomputedSqlTable), "queryalias2");
        SelectValue se3 = new SelectValue(v3, "queryalias3");
        SelectValue se4 = new SelectValue(v4, "queryalias4");
        List expectedSelect = new ArrayList();
        expectedSelect.add(se1);
        expectedSelect.add(se2);
        expectedSelect.add(se3);
        expectedSelect.add(se4);

        assertEquals(expectedSelect, newQuery.getSelect());
    }

    public void testReconstructAbstractConstraint() throws Exception {
        Table precomputedSqlTable = new Table("precomp1", "precomptablelias");
        Table t1 = new Table("table1", "tableAlias1");
        AbstractValue v1 = new Field("field1", t1);
        AbstractValue v2 = new Field("field2", t1);
        AbstractValue v3 = new Field("field3", t1);
        AbstractValue v4 = new Field("field4", t1);
        SelectValue s1 = new SelectValue(v1, "alias1");
        SelectValue s2 = new SelectValue(v2, "alias2");
        SelectValue s3 = new SelectValue(v3, "alias3");
        SelectValue s4 = new SelectValue(v4, "alias4");
        Map valueMap = new HashMap();
        valueMap.put(v1, s1);
        valueMap.put(v2, s2);
        valueMap.put(v3, s3);
        valueMap.put(v4, s4);
        Set tableSet = new HashSet();
        tableSet.add(t1);
        AbstractValue ev1 = new Field("alias1", precomputedSqlTable);
        AbstractValue ev2 = new Field("alias2", precomputedSqlTable);
        AbstractValue ev3 = new Field("alias3", precomputedSqlTable);
        AbstractValue ev4 = new Field("alias4", precomputedSqlTable);

        Constraint c1 = new Constraint(v1, Constraint.EQ, v2);
        NotConstraint c2 = new NotConstraint(c1);
        Constraint c3 = new Constraint(v3, Constraint.LT, v4);
        ConstraintSet c4 = new ConstraintSet();
        c4.add(c1);
        c4.add(c3);

        Constraint ec1 = new Constraint(ev1, Constraint.EQ, ev2);
        NotConstraint ec2 = new NotConstraint(ec1);
        Constraint ec3 = new Constraint(ev3, Constraint.LT, ev4);
        ConstraintSet ec4 = new ConstraintSet();
        ec4.add(ec1);
        ec4.add(ec3);

        assertEquals(ec1, QueryOptimiser.reconstructAbstractConstraint(c1, precomputedSqlTable, valueMap, tableSet, false, null, 0, null, false));
        assertEquals(ec2, QueryOptimiser.reconstructAbstractConstraint(c2, precomputedSqlTable, valueMap, tableSet, false, null, 0, null, false));
        assertEquals(ec3, QueryOptimiser.reconstructAbstractConstraint(c3, precomputedSqlTable, valueMap, tableSet, false, null, 0, null, false));
        assertEquals(ec4, QueryOptimiser.reconstructAbstractConstraint(c4, precomputedSqlTable, valueMap, tableSet, false, null, 0, null, false));

        Set set = new HashSet();
        set.add(c1);
        set.add(c2);
        set.add(c3);
        set.add(c4);
        Set eset = new HashSet();
        eset.add(ec1);
        eset.add(ec2);
        eset.add(ec3);
        eset.add(ec4);
        Set newSet = new HashSet();
        Set constraintEqualsSet = new HashSet();
        QueryOptimiser.reconstructAbstractConstraints(set, precomputedSqlTable, valueMap, tableSet, false, newSet, constraintEqualsSet, null, 0, null, false);
        assertEquals(eset, newSet);

        newSet = new HashSet();
        eset = new HashSet();
        eset.add(ec1);
        eset.add(ec2);
        eset.add(ec4);
        constraintEqualsSet.add(c3);
        QueryOptimiser.reconstructAbstractConstraints(set, precomputedSqlTable, valueMap, tableSet, false, newSet, constraintEqualsSet, null, 0, null, false);
        assertEquals(eset, newSet);
    }

    public void testMergeGroupByFits() throws Exception {
        Query q1 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b, count(*) as stuff from table as table1, somethingelse as table2 WHERE table1.c = table2.a GROUP BY table1.a, table1.b, table1.d HAVING table1.d = 'five' ORDER BY table1.a LIMIT 100 OFFSET 0");
        Query pq1 = new Query("SELECT table3.a AS sahjg, table3.b AS aytq, count(*) AS hksf, table3.d AS fdjsa FROM table AS table3, somethingelse AS table4 WHERE table3.c = table4.a GROUP BY table3.a, table3.b, table3.d");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        Query eq1 = new Query("SELECT P42.sahjg AS t1_a, P42.aytq AS t1_b, P42.hksf AS stuff from precomp1 AS P42 WHERE P42.fdjsa = 'five' ORDER BY P42.sahjg LIMIT 100 OFFSET 0");
        Set eSet = new HashSet();
        eSet.add(eq1);

        StringUtil.setNextUniqueNumber(42);
        Set newSet = QueryOptimiser.mergeGroupBy(pt1, q1, q1);

        assertEquals(eSet, newSet);
    }

    public void testMergeGroupByWrongTables() throws Exception {
        Query q1 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b, count(*) as stuff from table as table1, somethingdifferent as table2 WHERE table1.c = table2.a GROUP BY table1.a, table1.b, table1.d HAVING table1.d = 'five' ORDER BY table1.a LIMIT 100 OFFSET 0");
        Query pq1 = new Query("SELECT table3.a AS sahjg, table3.b AS aytq, count(*) AS hksf, table3.d AS fdjsa FROM table AS table3, somethingelse AS table4 WHERE table3.c = table4.a GROUP BY table3.a, table3.b, table3.d");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        Set eSet = new HashSet();

        Set newSet = QueryOptimiser.mergeGroupBy(pt1, q1, q1);

        assertEquals(eSet, newSet);
    }

    public void testMergeGroupByExtraSelect() throws Exception {
        Query q1 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b, table1.e AS t1_e, count(*) as stuff from table as table1, somethingelse as table2 WHERE table1.c = table2.a GROUP BY table1.a, table1.b, table1.d HAVING table1.d = 'five' ORDER BY table1.a LIMIT 100 OFFSET 0");
        Query pq1 = new Query("SELECT table3.a AS sahjg, table3.b AS aytq, count(*) AS hksf, table3.d AS fdjsa FROM table AS table3, somethingelse AS table4 WHERE table3.c = table4.a GROUP BY table3.a, table3.b, table3.d");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        Set eSet = new HashSet();

        Set newSet = QueryOptimiser.mergeGroupBy(pt1, q1, q1);

        assertEquals(eSet, newSet);
    }

    public void testMergeGroupByDifferentWhere() throws Exception {
        Query q1 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b, count(*) as stuff from table as table1, somethingelse as table2 WHERE table1.c = table2.b GROUP BY table1.a, table1.b, table1.d HAVING table1.d = 'five' ORDER BY table1.a LIMIT 100 OFFSET 0");
        Query pq1 = new Query("SELECT table3.a AS sahjg, table3.b AS aytq, count(*) AS hksf, table3.d AS fdjsa FROM table AS table3, somethingelse AS table4 WHERE table3.c = table4.a GROUP BY table3.a, table3.b, table3.d");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        Set eSet = new HashSet();

        Set newSet = QueryOptimiser.mergeGroupBy(pt1, q1, q1);

        assertEquals(eSet, newSet);
    }

    public void testMergeGroupByDifferentGroupBy() throws Exception {
        Query q1 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b, count(*) as stuff from table as table1, somethingelse as table2 WHERE table1.c = table2.a GROUP BY table1.a, table1.b, table1.d, table1.e HAVING table1.d = 'five' ORDER BY table1.a LIMIT 100 OFFSET 0");
        Query pq1 = new Query("SELECT table3.a AS sahjg, table3.b AS aytq, count(*) AS hksf, table3.d AS fdjsa FROM table AS table3, somethingelse AS table4 WHERE table3.c = table4.a GROUP BY table3.a, table3.b, table3.d");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        Set eSet = new HashSet();

        Set newSet = QueryOptimiser.mergeGroupBy(pt1, q1, q1);

        assertEquals(eSet, newSet);
    }

    public void testMergeGroupByWrongHaving() throws Exception {
        Query q1 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b, count(*) as stuff from table as table1, somethingelse as table2 WHERE table1.c = table2.a GROUP BY table1.a, table1.b, table1.d HAVING table1.d = 'five' ORDER BY table1.a LIMIT 100 OFFSET 0");
        Query pq1 = new Query("SELECT table3.a AS sahjg, table3.b AS aytq, count(*) AS hksf, table3.d AS fdjsa FROM table AS table3, somethingelse AS table4 WHERE table3.c = table4.a GROUP BY table3.a, table3.b, table3.d HAVING table3.b = 'five'");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        Set eSet = new HashSet();

        Set newSet = QueryOptimiser.mergeGroupBy(pt1, q1, q1);

        assertEquals(eSet, newSet);
    }

    public void testMergeGroupByDifferentDistinct() throws Exception {
        Query q1 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b, count(*) as stuff from table as table1, somethingelse as table2 WHERE table1.c = table2.a GROUP BY table1.a, table1.b, table1.d HAVING table1.d = 'five' ORDER BY table1.a LIMIT 100 OFFSET 0");
        Query pq1 = new Query("SELECT DISTINCT table3.a AS sahjg, table3.b AS aytq, count(*) AS hksf, table3.d AS fdjsa FROM table AS table3, somethingelse AS table4 WHERE table3.c = table4.a GROUP BY table3.a, table3.b, table3.d");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        Set eSet = new HashSet();

        Set newSet = QueryOptimiser.mergeGroupBy(pt1, q1, q1);

        assertEquals(eSet, newSet);
    }

    public void testMergeGroupByWithUselessConstraint() throws Exception {
        Query q1 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b, count(*) as stuff from table as table1, somethingelse as table2 WHERE table1.c = table2.a GROUP BY table1.a, table1.b, table1.d HAVING table1.d = 'five' ORDER BY table1.a LIMIT 100 OFFSET 0");
        Query pq1 = new Query("SELECT table3.a AS sahjg, table3.b AS aytq, count(*) AS hksf, table3.d AS fdjsa FROM table AS table3, somethingelse AS table4 WHERE table3.c = table4.a GROUP BY table3.a, table3.b, table3.d HAVING table3.d = 'five'");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        Query eq1 = new Query("SELECT P42.sahjg AS t1_a, P42.aytq AS t1_b, P42.hksf AS stuff from precomp1 AS P42 ORDER BY P42.sahjg LIMIT 100 OFFSET 0");
        Set eSet = new HashSet();
        eSet.add(eq1);

        StringUtil.setNextUniqueNumber(42);
        Set newSet = QueryOptimiser.mergeGroupBy(pt1, q1, q1);

        assertEquals(eSet, newSet);
    }

    public void testAddNonCoveredFrom() throws Exception {
        Set input = new HashSet();
        Set subtract = new HashSet();
        input.add("item1");
        input.add("item2");
        input.add("item3");
        subtract.add("item2");
        Set output = new HashSet();
        QueryOptimiser.addNonCoveredFrom(input, subtract, output);

        Set expectedOutput = new HashSet();
        expectedOutput.add("item1");
        expectedOutput.add("item3");

        assertEquals(expectedOutput, output);
    }

    public void testMergeSimple() throws Exception {
        Query q1 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b from table as table1 WHERE table1.c = 'five'");
        Query pq1 = new Query("SELECT table2.a AS kjfd, table2.b AS ddfw FROM table as table2 WHERE table2.c = 'five'");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        Query eq1 = new Query("SELECT P42.kjfd AS t1_a, P42.ddfw AS t1_b FROM precomp1 AS P42");

        Set eSet = new HashSet();
        eSet.add(eq1);

        StringUtil.setNextUniqueNumber(42);
        Set newSet = QueryOptimiser.merge(pt1, q1, q1);

        assertEquals(eSet, newSet);
    }

    public void testMergeOkayDistinct1() throws Exception {
        Query q1 = new Query("SELECT DISTINCT table1.a AS t1_a, table1.b AS t1_b from table as table1 WHERE table1.c = 'five'");
        Query pq1 = new Query("SELECT table2.a AS kjfd, table2.b AS ddfw FROM table as table2 WHERE table2.c = 'five'");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        Query eq1 = new Query("SELECT DISTINCT P42.kjfd AS t1_a, P42.ddfw AS t1_b FROM precomp1 AS P42");

        Set eSet = new HashSet();
        eSet.add(eq1);

        StringUtil.setNextUniqueNumber(42);
        Set newSet = QueryOptimiser.merge(pt1, q1, q1);

        assertEquals(eSet, newSet);
    }

    public void testMergeOkayDistinct2() throws Exception {
        Query q1 = new Query("SELECT DISTINCT table1.a AS t1_a, table1.b AS t1_b from table as table1 WHERE table1.c = 'five'");
        Query pq1 = new Query("SELECT DISTINCT table2.a AS kjfd, table2.b AS ddfw FROM table as table2 WHERE table2.c = 'five'");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        Query eq1 = new Query("SELECT DISTINCT P42.kjfd AS t1_a, P42.ddfw AS t1_b FROM precomp1 AS P42");

        Set eSet = new HashSet();
        eSet.add(eq1);

        StringUtil.setNextUniqueNumber(42);
        Set newSet = QueryOptimiser.merge(pt1, q1, q1);

        assertEquals(eSet, newSet);
    }

    public void testMergeWrongDistinct() throws Exception {
        Query q1 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b from table as table1 WHERE table1.c = 'five'");
        Query pq1 = new Query("SELECT DISTINCT table2.a AS kjfd, table2.b AS ddfw FROM table as table2 WHERE table2.c = 'five'");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);

        Set eSet = new HashSet();

        Set newSet = QueryOptimiser.merge(pt1, q1, q1);

        assertEquals(eSet, newSet);
    }

    public void testMergeWrongWhere() throws Exception {
        Query q1 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b from table as table1 WHERE table1.c = 'five'");
        Query pq1 = new Query("SELECT table2.a AS kjfd, table2.b AS ddfw FROM table as table2 WHERE table2.b = 'five'");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);

        Set eSet = new HashSet();

        Set newSet = QueryOptimiser.merge(pt1, q1, q1);

        assertEquals(eSet, newSet);
    }

    public void testMergeWrongSelect() throws Exception {
        Query q1 = new Query("SELECT table1.c AS t1_c, table1.b AS t1_b from table as table1");
        Query pq1 = new Query("SELECT table2.a AS kjfd, table2.b AS ddfw FROM table as table2");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);

        Set eSet = new HashSet();

        Set newSet = QueryOptimiser.merge(pt1, q1, q1);

        assertEquals(eSet, newSet);
    }

    public void testMergeOkayGroupBy() throws Exception {
        Query q1 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b from table as table1 WHERE table1.c = 'five' GROUP BY table1.a, table1.b HAVING table1.a = 'six'");
        Query pq1 = new Query("SELECT table2.a AS kjfd, table2.b AS ddfw FROM table as table2 WHERE table2.c = 'five'");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        Query eq1 = new Query("SELECT P42.kjfd AS t1_a, P42.ddfw AS t1_b FROM precomp1 AS P42 GROUP BY P42.kjfd, P42.ddfw HAVING P42.kjfd = 'six'");

        Set eSet = new HashSet();
        eSet.add(eq1);

        StringUtil.setNextUniqueNumber(42);
        Set newSet = QueryOptimiser.merge(pt1, q1, q1);

        assertEquals(eSet, newSet);
    }

    public void testMergeOkayOverlapping() throws Exception {
        Query q1 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b, table2.a AS t2_a, table2.b AS t2_b from table as table1, table as table2 WHERE table1.c = 'five' AND table2.c = 'five'");
        Query pq1 = new Query("SELECT table3.a AS kjfd, table3.b AS ddfw FROM table as table3 WHERE table3.c = 'five'");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        Query eq1 = new Query("SELECT P44.kjfd AS t1_a, P44.ddfw AS t1_b, P46.kjfd AS t2_a, P46.ddfw AS t2_b FROM precomp1 AS P44, precomp1 AS P46");
        Query eq2 = new Query("SELECT P49.a AS t1_a, P49.b AS t1_b, P50.kjfd AS t2_a, P50.ddfw AS t2_b FROM table AS P49, precomp1 AS P50 WHERE P49.c = 'five'");
        Query eq3 = new Query("SELECT P48.kjfd AS t1_a, P48.ddfw AS t1_b, table3.a AS t2_a, table3.b AS t2_b FROM precomp1 AS P48, table AS table3 WHERE table3.c = 'five'");

        Set eSet = new HashSet();
        eSet.add(eq1);
        eSet.add(eq2);
        eSet.add(eq3);

        StringUtil.setNextUniqueNumber(42);
        Set newSet = QueryOptimiser.merge(pt1, q1, q1);
/*
        String expected = "";
        Iterator expectedIter = eSet.iterator();
        boolean needComma = false;
        while (expectedIter.hasNext()) {
            expected += (needComma ? ", " : "");
            needComma = true;
            expected += ((Query) expectedIter.next()).getSQLString();
        }

        String got = "";
        Iterator gotIter = newSet.iterator();
        needComma = false;
        while (gotIter.hasNext()) {
            got += (needComma ? ", " : "");
            needComma = true;
            got += ((Query) gotIter.next()).getSQLString();
        }

        assertEquals(expected, got);*/
        assertEquals(eSet, newSet);
    }

    public void testMergeOkayNonOverlapping() throws Exception {
        Query q1 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b, table2.a AS t2_a, table2.b AS t2_b, table4.a AS t4_a from table as table1, table as table2, anothertable AS table4 WHERE table1.c = 'five' AND table2.c = 'five' AND table4.a = table1.a AND table4.a = table2.a");
        Query pq1 = new Query("SELECT table3.a AS kjfd, table3.b AS ddfw, table5.a AS fhds FROM table as table3, anothertable AS table5 WHERE table3.c = 'five' AND table3.a = table5.a");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        Query eq1 = new Query("SELECT P46.kjfd AS t1_a, P46.ddfw AS t1_b, P45.a AS t2_a, P45.b AS t2_b, P46.fhds AS t4_a FROM precomp1 AS P46, table AS P45 WHERE P45.a = P46.fhds AND P45.c = 'five'");
        Query eq2 = new Query("SELECT table3.a AS t1_a, table3.b AS t1_b, P44.kjfd AS t2_a, P44.ddfw AS t2_b, P44.fhds AS t4_a FROM table AS table3, precomp1 AS P44 WHERE table3.a = P44.fhds AND table3.c = 'five'");

        Set eSet = new HashSet();
        eSet.add(eq1);
        eSet.add(eq2);

        StringUtil.setNextUniqueNumber(42);
        Set newSet = QueryOptimiser.merge(pt1, q1, q1);

        String expected = "";
        Iterator expectedIter = eSet.iterator();
        boolean needComma = false;
        while (expectedIter.hasNext()) {
            expected += (needComma ? ", " : "");
            needComma = true;
            expected += ((Query) expectedIter.next()).getSQLString();
        }

        String got = "";
        Iterator gotIter = newSet.iterator();
        needComma = false;
        while (gotIter.hasNext()) {
            got += (needComma ? ", " : "");
            needComma = true;
            got += ((Query) gotIter.next()).getSQLString();
        }

        //assertEquals(expected, got);
        //System.out.println(expected);
        //System.out.println(got);

        assertEquals(eSet, newSet);
    }
   /* 
    public void testMergeClashingAliases() throws Exception {
        Query q1 = new Query("SELECT P42.a AS t1_a, P42.b AS t1_b, table1.a AS t2_a from tab as P42, table as table1 WHERE table1.c = 'five'");
        Query pq1 = new Query("SELECT table2.a AS kjfd, table2.b AS ddfw FROM table as table2 WHERE table2.c = 'five'");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        Query eq1 = new Query("SELECT P42.a AS t1_a, P42.b AS t1_b, P42.kjfd AS t2_a FROM precomp1 AS P42, tab AS P42");

        Set eSet = new HashSet();
        eSet.add(eq1);

        StringUtil.setNextUniqueNumber(42);
        Set newSet = QueryOptimiser.merge(pt1, q1, q1);

        assertEquals(eSet, newSet);
    }
*/

    public void testMergeMultiple() throws Exception {
        Query q1 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b, table2.a AS t2_a, table2.b AS t2_b FROM table1, table2 WHERE table1.c = 'five' AND table2.c = 'six'");
        Query pq1 = new Query("SELECT table1.a AS fhjs, table1.b AS sjhf FROM table1 WHERE table1.c = 'five'");
        Query pq2 = new Query("SELECT table2.a AS kjsd, table2.b AS hjas FROM table2 WHERE table2.c = 'six'");
        Query pq3 = new Query("SELECT table2.a AS kjsd, table2.b AS hjas FROM table2 WHERE table2.c = 'seven'");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        PrecomputedTable pt2 = new PrecomputedTable(pq2, "precomp2", con);
        PrecomputedTable pt3 = new PrecomputedTable(pq3, "precomp3", con);
        Set precomps = new HashSet();
        precomps.add(pt1);
        precomps.add(pt2);
        precomps.add(pt3);
        
        Query eq1 = new Query("SELECT P42.fhjs AS t1_a, P42.sjhf AS t1_b, table2.a AS t2_a, table2.b AS t2_b FROM precomp1 AS P42, table2 WHERE table2.c = 'six'");
        Query eq2 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b, P43.kjsd AS t2_a, P43.hjas AS t2_b FROM table1, precomp2 AS P43 WHERE table1.c = 'five'");
        Map eMap = new HashMap();
        eMap.put(pt1, Collections.singleton(eq1));
        eMap.put(pt2, Collections.singleton(eq2));

        StringUtil.setNextUniqueNumber(42);
        SortedMap newMap = QueryOptimiser.mergeMultiple(precomps, q1, q1);

        assertEquals(eMap, newMap);
    }

    public void testRecursiveOptimise1() throws Exception {
        Query q1 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b, table2.a AS t2_a, table2.b AS t2_b FROM table1, table2 WHERE table1.c = 'five' AND table2.c = 'six'");
        Query pq1 = new Query("SELECT table1.a AS fhjs, table1.b AS sjhf FROM table1 WHERE table1.c = 'five'");
        Query pq2 = new Query("SELECT table2.a AS kjsd, table2.b AS hjas FROM table2 WHERE table2.c = 'six'");
        Query pq3 = new Query("SELECT table2.a AS kjsd, table2.b AS hjas FROM table2 WHERE table2.c = 'seven'");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        PrecomputedTable pt2 = new PrecomputedTable(pq2, "precomp2", con);
        PrecomputedTable pt3 = new PrecomputedTable(pq3, "precomp3", con);
        Set precomps = new HashSet();
        precomps.add(pt1);
        precomps.add(pt2);
        precomps.add(pt3);
        
        Query eq1 = new Query("SELECT P42.fhjs AS t1_a, P42.sjhf AS t1_b, table2.a AS t2_a, table2.b AS t2_b FROM precomp1 AS P42, table2 WHERE table2.c = 'six'");
        Query eq2 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b, P43.kjsd AS t2_a, P43.hjas AS t2_b FROM table1, precomp2 AS P43 WHERE table1.c = 'five'");
        Query eq3 = new Query("SELECT P44.fhjs AS t1_a, P44.sjhf AS t1_b, P43.kjsd AS t2_a, P43.hjas AS t2_b FROM precomp1 AS P44, precomp2 AS P43");
        Set eSet = new ConsistentSet();
        eSet.add(eq1);
        eSet.add(eq2);
        eSet.add(eq3);

        StringUtil.setNextUniqueNumber(42);
        BestQueryStorer bestQuery = new BestQueryStorer();
        QueryOptimiser.recursiveOptimise(precomps, q1, bestQuery, q1);

        assertEquals(eSet, bestQuery.getQueries());
    }

    public void testRecursiveOptimise2() throws Exception {
        Query q1 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b, table2.a AS t2_a, table2.b AS t2_b FROM table AS table1, table AS table2 WHERE table1.c = 'five' AND table2.c = 'five'");
        Query pq1 = new Query("SELECT table1.a AS fhjs, table1.b AS sjhf FROM table AS table1 WHERE table1.c = 'five'");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        Set precomps = new HashSet();
        precomps.add(pt1);
        
        Query eq1 = new Query("SELECT P51.fhjs AS t1_a, P51.sjhf AS t1_b, P50.a AS t2_a, P50.b AS t2_b FROM precomp1 AS P51, table AS P50 WHERE P50.c = 'five'");
        Query eq2 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b, P49.fhjs AS t2_a, P49.sjhf AS t2_b FROM table AS table1, precomp1 AS P49 WHERE table1.c = 'five'");
        Query eq3 = new Query("SELECT P47.fhjs AS t1_a, P47.sjhf AS t1_b, P45.fhjs AS t2_a, P45.sjhf AS t2_b FROM precomp1 AS P47, precomp1 AS P45");
        Set eSet = new ConsistentSet();
        eSet.add(eq1);
        eSet.add(eq2);
        eSet.add(eq3);

        StringUtil.setNextUniqueNumber(42);
        BestQueryStorer bestQuery = new BestQueryStorer();
        QueryOptimiser.recursiveOptimise(precomps, q1, bestQuery, q1);

        assertEquals(eSet, bestQuery.getQueries());
    }

    public void testRecursiveOptimise3() throws Exception {
        Query q1 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b, table2.a AS t2_a, table2.b AS t2_b FROM table1, table2 WHERE table1.c = 'five' AND table2.c = 'six' AND table1.d = table2.d");
        Query pq1 = new Query("SELECT table1.a AS fhjs, table1.b AS sjhf, table1.d AS kjhds FROM table1 WHERE table1.c = 'five'");
        Query pq2 = new Query("SELECT table2.a AS kjsd, table2.b AS hjas, table2.d AS kjhsd FROM table2 WHERE table2.c = 'six'");
        Query pq3 = new Query("SELECT table2.a AS kjsd, table2.b AS hjas, table2.d AS jsdff FROM table2 WHERE table2.c = 'seven'");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        PrecomputedTable pt2 = new PrecomputedTable(pq2, "precomp2", con);
        PrecomputedTable pt3 = new PrecomputedTable(pq3, "precomp3", con);
        Set precomps = new HashSet();
        precomps.add(pt1);
        precomps.add(pt2);
        precomps.add(pt3);
        
        Query eq1 = new Query("SELECT P42.fhjs AS t1_a, P42.sjhf AS t1_b, table2.a AS t2_a, table2.b AS t2_b FROM precomp1 AS P42, table2 WHERE table2.c = 'six' AND P42.kjhds = table2.d");
        Query eq2 = new Query("SELECT table1.a AS t1_a, table1.b AS t1_b, P43.kjsd AS t2_a, P43.hjas AS t2_b FROM table1, precomp2 AS P43 WHERE table1.c = 'five' AND table1.d = P43.kjhsd");
        Query eq3 = new Query("SELECT P44.fhjs AS t1_a, P44.sjhf AS t1_b, P43.kjsd AS t2_a, P43.hjas AS t2_b FROM precomp1 AS P44, precomp2 AS P43 WHERE P44.kjhds = P43.kjhsd");
        Set eSet = new ConsistentSet();
        eSet.add(eq1);
        eSet.add(eq2);
        eSet.add(eq3);

        StringUtil.setNextUniqueNumber(42);
        BestQueryStorer bestQuery = new BestQueryStorer();
        QueryOptimiser.recursiveOptimise(precomps, q1, bestQuery, q1);

        assertEquals(eSet, bestQuery.getQueries());
    }

    public void testRemapAliasesToAvoidPrecomputePrefix() throws Exception {
        Query q1 = new Query("SELECT table1.a, Putty.b FROM table1, table AS Putty");
        Query eq1 = new Query("SELECT table1.a, P42.b FROM table1, table AS P42");

        StringUtil.setNextUniqueNumber(42);
        QueryOptimiser.remapAliasesToAvoidPrecomputePrefix(q1);

        assertEquals(eq1, q1);
    }

    public void testOrderByField() throws Exception {
        Query pq1 = new Query("SELECT ta.id AS a, tb.id AS b, tc.id AS c, tc.name AS name FROM Company AS ta, Department AS tb, Employee AS tc ORDER BY ta.id, tb.id, tc.id");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        Set precomps = new HashSet();
        precomps.add(pt1);

        Query q, eq;
        Set eSet;
        BestQueryStorer bestQuery;

        q = new Query("SELECT ta.id AS a, tb.id AS b, tc.id AS c FROM Company AS ta, Department AS tb, Employee AS tc WHERE ta.id > 25 OR ta.id IS NULL ORDER BY ta.id, tb.id, tc.id");
        eq = new Query("SELECT P42.a AS a, P42.b AS b, P42.c AS c FROM precomp1 AS P42 WHERE P42.orderby_field > 255000000000000000000050000000000000000000 ORDER BY P42.orderby_field");
        eSet = new ConsistentSet();
        eSet.add(eq);
        StringUtil.setNextUniqueNumber(42);
        bestQuery = new BestQueryStorer();
        QueryOptimiser.recursiveOptimise(precomps, q, bestQuery, q);
        assertEquals(eSet, bestQuery.getQueries());

        q = new Query("SELECT ta.id AS a, tb.id AS b, tc.id AS c FROM Company AS ta, Department AS tb, Employee AS tc WHERE ta.id > 25 OR ta.id IS NULL ORDER BY ta.id, tb.id, tc.name");
        eq = new Query("SELECT P42.a AS a, P42.b AS b, P42.c AS c FROM precomp1 AS P42 WHERE P42.a > 25 OR P42.a IS NULL ORDER BY P42.a, P42.b, P42.name");
        eSet = new ConsistentSet();
        eSet.add(eq);
        StringUtil.setNextUniqueNumber(42);
        bestQuery = new BestQueryStorer();
        QueryOptimiser.recursiveOptimise(precomps, q, bestQuery, q);
        assertEquals(eSet, bestQuery.getQueries());

        q = new Query("SELECT ta.id AS a, tb.id AS b, tc.id AS c FROM Company AS ta, Department AS tb, Employee AS tc WHERE ta.id > 25 OR ta.id IS NULL ORDER BY tc.id, ta.id, tb.id");
        eq = new Query("SELECT P42.a AS a, P42.b AS b, P42.c AS c FROM precomp1 AS P42 WHERE P42.a > 25 OR P42.a IS NULL ORDER BY P42.c, P42.a, P42.b");
        eSet = new ConsistentSet();
        eSet.add(eq);
        StringUtil.setNextUniqueNumber(42);
        bestQuery = new BestQueryStorer();
        QueryOptimiser.recursiveOptimise(precomps, q, bestQuery, q);
        assertEquals(eSet, bestQuery.getQueries());

        q = new Query("SELECT ta.id AS a, tb.id AS b, tc.id AS c FROM Company AS ta, Department AS tb, Employee AS tc WHERE tc.id > 25 OR tc.id IS NULL ORDER BY tc.id, ta.id, tb.id");
        eq = new Query("SELECT P42.a AS a, P42.b AS b, P42.c AS c FROM precomp1 AS P42 WHERE P42.c > 25 OR P42.c IS NULL ORDER BY P42.c, P42.a, P42.b");
        eSet = new ConsistentSet();
        eSet.add(eq);
        StringUtil.setNextUniqueNumber(42);
        bestQuery = new BestQueryStorer();
        QueryOptimiser.recursiveOptimise(precomps, q, bestQuery, q);
        assertEquals(eSet, bestQuery.getQueries());

        q = new Query("SELECT DISTINCT ta.id AS a, tb.id AS b, tc.id AS c FROM Company AS ta, Department AS tb, Employee AS tc ORDER BY ta.id, tb.id, tc.id");
        eq = new Query("SELECT DISTINCT P42.a AS a, P42.b AS b, P42.c AS c, P42.orderby_field FROM precomp1 AS P42 ORDER BY P42.orderby_field");
        eSet = new ConsistentSet();
        eSet.add(eq);
        StringUtil.setNextUniqueNumber(42);
        bestQuery = new BestQueryStorer();
        QueryOptimiser.recursiveOptimise(precomps, q, bestQuery, q);
        assertEquals(eSet, bestQuery.getQueries());

        q = new Query("SELECT ta.id AS a, tb.id AS b, tc.age AS c FROM Company AS ta, Department AS tb, Employee AS tc WHERE ta.id > 25 ORDER BY ta.id, tc.id, tb.id");
        pq1 = new Query("SELECT ta.id AS a, tb.id AS b, tc.age AS c FROM Company AS ta, Department AS tb, Employee AS tc");
        pt1 = new PrecomputedTable(pq1, "precomp1", con);
        precomps = new HashSet();
        precomps.add(pt1);

        bestQuery = new BestQueryStorer();
        QueryOptimiser.recursiveOptimise(precomps, q, bestQuery, q);
        assertEquals(Collections.EMPTY_SET, bestQuery.getQueries());
    }

    public void testKimsBug() throws Exception {
        Query q1 = new Query("SELECT a1_.id AS a2_, a3_.OBJECT AS a3_, a3_.id AS a3_id, a4_.OBJECT AS a4_, a4_.id AS a4_id FROM Chromosome AS a1_, BioEntity AS a3_, Location AS a4_ WHERE a4_.objectId = a1_.id AND a4_.subjectId = a3_.id ORDER BY a1_.id, a3_.id, a4_.id");
        Query q2 = new Query("SELECT a1_.id AS a2_, a3_.OBJECT AS a3_, a3_.id AS a3_id, a4_.OBJECT AS a4_, a4_.id AS a4_id FROM Chromosome AS a1_, BioEntity AS a3_, Location AS a4_ WHERE (a4_.objectId = a1_.id AND a4_.subjectId = a3_.id) AND a1_.id > 5325019 ORDER BY a1_.id, a3_.id, a4_.id");
        Query pq1 = new Query("SELECT a1_.id AS a2_, a3_.OBJECT AS a3_, a3_.id AS a3_id, a4_.OBJECT AS a4_, a4_.id AS a4_id FROM Chromosome AS a1_, BioEntity AS a3_, Location AS a4_ WHERE a4_.objectId = a1_.id AND a4_.subjectId = a3_.id ORDER BY a1_.id, a3_.id, a4_.id");
        Query pq2 = new Query("SELECT a1_.id AS a2_, a3_.OBJECT AS a3_, a3_.id AS a3_id, a4_.OBJECT AS a4_, a4_.id AS a4_id FROM Chromosome AS a1_, BioEntity AS a3_, Location AS a4_ WHERE a4_.objectId = a1_.id AND a4_.subjectId = a3_.id AND a1_.id = 10669827 ORDER BY a1_.id, a3_.id, a4_.id");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        PrecomputedTable pt2 = new PrecomputedTable(pq2, "precomp2", con);
        Set precomps = new HashSet();
        precomps.add(pt1);
        precomps.add(pt2);
        
        Query eq1 = new Query("SELECT P42.a2_, P42.a3_, P42.a3_id, P42.a4_, P42.a4_id FROM precomp1 AS P42 ORDER BY P42.a2_, P42.a3_id, P42.a4_id");
        Set eSet = new ConsistentSet();
        eSet.add(eq1);
        StringUtil.setNextUniqueNumber(42);
        BestQueryStorer bestQuery = new BestQueryStorer();
        QueryOptimiser.recursiveOptimise(precomps, q1, bestQuery, q1);
        assertEquals(eSet, bestQuery.getQueries());

        Query eq2 = new Query("SELECT P42.a2_, P42.a3_, P42.a3_id, P42.a4_, P42.a4_id FROM precomp1 AS P42 WHERE P42.a2_ > 5325019 ORDER BY P42.a2_, P42.a3_id, P42.a4_id");
        eSet = new ConsistentSet();
        eSet.add(eq2);
        StringUtil.setNextUniqueNumber(42);
        bestQuery = new BestQueryStorer();
        QueryOptimiser.recursiveOptimise(precomps, q2, bestQuery, q2);
        assertEquals(eSet, bestQuery.getQueries());
    }

    public void testKimsBug2() throws Exception {
        Table t = new Table("a", "b");
        Constraint c1 = new Constraint(new Constant("5325019"), Constraint.LT, new Field("b", t));
        Constraint c2 = new Constraint(new Constant("10669827"), Constraint.EQ, new Field("b", t));
        Constraint c3 = new Constraint(new Constant("1066982"), Constraint.EQ, new Field("b", t));
        Constraint c4 = new Constraint(new Constant("5325020"), Constraint.LT, new Field("b", t));

        assertEquals(Constraint.IMPLIED_BY, c1.compare(c2));
        assertEquals(Constraint.EXCLUDES, c1.compare(c3));
        assertEquals(Constraint.IMPLIED_BY, c1.compare(c4));

        Set set1 = Collections.singleton(c2);
        Set set2 = Collections.singleton(c1);
        Set equalsSet = new HashSet();
        assertFalse(QueryOptimiser.compareConstraints(set1, set2, equalsSet));
        assertTrue(equalsSet.isEmpty());
    }

    public void testKimsBug3() throws Exception {
        Query q1 = new Query("SELECT a1_.a AS a2_ FROM Chromosome AS a1_ ORDER BY a1_.a");
        Query q2 = new Query("SELECT a1_.a AS a2_ FROM Chromosome AS a1_ WHERE a1_.a > 5325019 ORDER BY a1_.a");
        Query pq1 = new Query("SELECT a1_.a AS a2_ FROM Chromosome AS a1_ ORDER BY a1_.a");
        Query pq2 = new Query("SELECT a1_.a AS a2_ FROM Chromosome AS a1_ WHERE a1_.a = 10669827 ORDER BY a1_.a");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        PrecomputedTable pt2 = new PrecomputedTable(pq2, "precomp2", con);
        Set precomps = new HashSet();
        precomps.add(pt1);
        precomps.add(pt2);
        
        Query eq1 = new Query("SELECT P42.a2_ FROM precomp1 AS P42 ORDER BY P42.a2_");
        Set eSet = new ConsistentSet();
        eSet.add(eq1);
        StringUtil.setNextUniqueNumber(42);
        BestQueryStorer bestQuery = new BestQueryStorer();
        QueryOptimiser.recursiveOptimise(precomps, q1, bestQuery, q1);
        assertEquals(eSet, bestQuery.getQueries());

        Query eq2 = new Query("SELECT P42.a2_ FROM precomp1 AS P42 WHERE P42.a2_ > 5325019 ORDER BY P42.a2_");
        eSet = new ConsistentSet();
        eSet.add(eq2);
        StringUtil.setNextUniqueNumber(42);
        bestQuery = new BestQueryStorer();
        QueryOptimiser.recursiveOptimise(precomps, q2, bestQuery, q2);
        assertEquals(eSet, bestQuery.getQueries());
    }

    public void testKimsBug4() throws Exception {
        try {
            con.createStatement().execute("CREATE TABLE LocatedSequenceFeature (id int)");
            con.createStatement().execute("CREATE TABLE OverlapRelation (id int);");
            Query q1 = new Query("SELECT a1_.id AS a1_id, a2_.id AS a2_id, a3_.id AS a3_id FROM LocatedSequenceFeature AS a1_, OverlapRelation AS a2_, LocatedSequenceFeature AS a3_, BioEntitiesRelations AS indirect0, BioEntitiesRelations AS indirect1 WHERE a2_.id = indirect0.BioEntities AND indirect0.Relations = a1_.id AND a2_.id = indirect1.BioEntities AND indirect1.Relations = a3_.id AND a1_.id > 24081631 ORDER BY a1_.id, a2_.id, a3_.id");
            Query pq1 = new Query("SELECT a1_.id AS a1_id, a2_.id AS a2_id, a3_.id AS a3_id FROM LocatedSequenceFeature AS a1_, OverlapRelation AS a2_, LocatedSequenceFeature AS a3_, BioEntitiesRelations AS indirect0, BioEntitiesRelations AS indirect1 WHERE a2_.id = indirect0.BioEntities AND indirect0.Relations = a1_.id AND a2_.id = indirect1.BioEntities AND indirect1.Relations = a3_.id ORDER BY a1_.id, a2_.id, a3_.id");
            PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
            Set precomps = new HashSet();
            precomps.add(pt1);

            System.out.println(pt1.getSQLString() + " ---- " + pt1.getOrderByField());

            StringUtil.setNextUniqueNumber(42);
            BestQueryStorer bestQuery = new BestQueryStorer();
            QueryOptimiser.recursiveOptimise(precomps, q1, bestQuery, q1);
            Set eSet = new ConsistentSet();
            eSet.add(new Query("SELECT P48.a3_id AS a1_id, P48.a2_id, P48.a1_id AS a3_id FROM precomp1 AS P48 WHERE 24081631 < P48.a3_id ORDER BY P48.a3_id, P48.a2_id, P48.a1_id"));
            eSet.add(new Query("SELECT P51.a1_id, P51.a2_id, P51.a3_id FROM precomp1 AS P51 WHERE 24081631 < P51.a1_id ORDER BY P51.orderby_field"));
            assertEquals(eSet, bestQuery.getQueries());
        } finally {
            try {
                con.createStatement().execute("DROP TABLE LocatedSequenceFeature");
            } catch (SQLException e) {
            }
            try {
                con.createStatement().execute("DROP TABLE OverlapRelation");
            } catch (SQLException e) {
            }
        }
    }

    public void testKimsBug5() throws Exception {
        try {
            con.createStatement().execute("CREATE TABLE LocatedSequenceFeature (id int NOT NULL)");
            con.createStatement().execute("CREATE TABLE OverlapRelation (id int NOT NULL);");
            Query q1 = new Query("SELECT a1_.id AS a1_id, a2_.id AS a2_id, a3_.id AS a3_id FROM LocatedSequenceFeature AS a1_, OverlapRelation AS a2_, LocatedSequenceFeature AS a3_, BioEntitiesRelations AS indirect0, BioEntitiesRelations AS indirect1 WHERE a2_.id = indirect0.BioEntities AND indirect0.Relations = a1_.id AND a2_.id = indirect1.BioEntities AND indirect1.Relations = a3_.id AND a1_.id > 24081631 ORDER BY a1_.id, a2_.id, a3_.id");
            Query pq1 = new Query("SELECT a1_.id AS a1_id, a2_.id AS a2_id, a3_.id AS a3_id FROM LocatedSequenceFeature AS a1_, OverlapRelation AS a2_, LocatedSequenceFeature AS a3_, BioEntitiesRelations AS indirect0, BioEntitiesRelations AS indirect1 WHERE a2_.id = indirect0.BioEntities AND indirect0.Relations = a1_.id AND a2_.id = indirect1.BioEntities AND indirect1.Relations = a3_.id ORDER BY a1_.id, a2_.id, a3_.id");
            PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
            Set precomps = new HashSet();
            precomps.add(pt1);

            System.out.println(pt1.getSQLString() + " ---- " + pt1.getOrderByField());

            StringUtil.setNextUniqueNumber(42);
            BestQueryStorer bestQuery = new BestQueryStorer();
            QueryOptimiser.recursiveOptimise(precomps, q1, bestQuery, q1);
            Set eSet = new ConsistentSet();
            eSet.add(new Query("SELECT P48.a3_id AS a1_id, P48.a2_id, P48.a1_id AS a3_id FROM precomp1 AS P48 WHERE 24081631 < P48.a3_id ORDER BY P48.a3_id, P48.a2_id, P48.a1_id"));
            eSet.add(new Query("SELECT P51.a1_id, P51.a2_id, P51.a3_id FROM precomp1 AS P51 WHERE 240816315000000000000000000050000000000000000000 < P51.orderby_field ORDER BY P51.orderby_field"));
            assertEquals(eSet, bestQuery.getQueries());
        } finally {
            try {
                con.createStatement().execute("DROP TABLE LocatedSequenceFeature");
            } catch (SQLException e) {
            }
            try {
                con.createStatement().execute("DROP TABLE OverlapRelation");
            } catch (SQLException e) {
            }
        }
    }

/*    public void testRichardsBug() throws Exception {
        Query q1 = new Query("SELECT DISTINCT a1_.identifier AS a17_, a15_.primaryAccession AS a18_, a15_.identifier AS a19_, a12_.identifier AS a20_, a12_.primaryAccession AS a21_, a14_.shortName AS a22_ FROM Gene AS a1_, Orthologue AS a2_, Gene AS a3_, Protein AS a4_, ProteinInteractor AS a5_, ProteinInteraction AS a6_, ProteinInteractor AS a7_, Protein AS a8_, Gene AS a9_, Orthologue AS a10_, Gene AS a11_, Protein AS a12_, Organism AS a13_, Organism AS a14_, Protein AS a15_, Organism AS a16_, GeneOrthologues AS indirect0, GenesProteins AS indirect1, GenesProteins AS indirect2, GeneOrthologues AS indirect3, GenesProteins AS indirect4, GenesProteins AS indirect5 WHERE a8_.id != a4_.id AND a16_.id = a13_.id AND LOWER(a1_.identifier) = 'cg3481' AND a1_.id = indirect0.Orthologues AND indirect0.Gene = a2_.id AND a2_.subjectId = a3_.id AND a3_.id = indirect1.Proteins AND indirect1.Genes = a4_.id AND a4_.id = a5_.proteinId AND a5_.interactionId = a6_.id AND a6_.id = a7_.interactionId AND a7_.proteinId = a8_.id AND a8_.id = indirect2.Genes AND indirect2.Proteins = a9_.id AND a9_.id = indirect3.Orthologues AND indirect3.Gene = a10_.id AND a10_.subjectId = a11_.id AND a11_.id = indirect4.Proteins AND indirect4.Genes = a12_.id AND a12_.organismId = a13_.id AND a3_.organismId = a14_.id AND a1_.id = indirect5.Proteins AND indirect5.Genes = a15_.id AND a15_.organismId = a16_.id ORDER BY a1_.identifier, a15_.primaryAccession, a15_.identifier, a12_.identifier, a12_.primaryAccession, a14_.shortName");
        Query pq1 = new Query("SELECT DISTINCT a1_.identifier AS a17_, a15_.primaryAccession AS a18_, a15_.identifier AS a19_, a12_.identifier AS a20_, a12_.primaryAccession AS a21_, a14_.shortName AS a22_, a1_.identifier AS a23_ FROM Gene AS a1_, Orthologue AS a2_, Gene AS a3_, Protein AS a4_, ProteinInteractor AS a5_, ProteinInteraction AS a6_, ProteinInteractor AS a7_, Protein AS a8_, Gene AS a9_, Orthologue AS a10_, Gene AS a11_, Protein AS a12_, Organism AS a13_, Organism AS a14_, Protein AS a15_, Organism AS a16_, GeneOrthologues AS indirect0, GenesProteins AS indirect1, GenesProteins AS indirect2, GeneOrthologues AS indirect3, GenesProteins AS indirect4, GenesProteins AS indirect5 WHERE a8_.id != a4_.id AND a16_.id = a13_.id AND a1_.id = indirect0.Orthologues AND indirect0.Gene = a2_.id AND a2_.subjectId = a3_.id AND a3_.id = indirect1.Proteins AND indirect1.Genes = a4_.id AND a4_.id = a5_.proteinId AND a5_.interactionId = a6_.id AND a6_.id = a7_.interactionId AND a7_.proteinId = a8_.id AND a8_.id = indirect2.Genes AND indirect2.Proteins = a9_.id AND a9_.id = indirect3.Orthologues AND indirect3.Gene = a10_.id AND a10_.subjectId = a11_.id AND a11_.id = indirect4.Proteins AND indirect4.Genes = a12_.id AND a12_.organismId = a13_.id AND a3_.organismId = a14_.id AND a1_.id = indirect5.Proteins AND indirect5.Genes = a15_.id AND a15_.organismId = a16_.id ORDER BY a1_.identifier, a15_.primaryAccession, a15_.identifier, a12_.identifier, a12_.primaryAccession, a14_.shortName, a1_.identifier");
        PrecomputedTable pt1 = new PrecomputedTable(pq1, "precomp1", con);
        Set precomps = new HashSet();
        precomps.add(pt1);

        StringUtil.setNextUniqueNumber(42);
        BestQueryStorer bestQuery = new BestQueryStorer();
        QueryOptimiser.recursiveOptimise(precomps, q1, bestQuery, q1);
        Set eSet = new HashSet();
        eSet.add(new Query("SELECT DISTINCT P1710730.a23_ AS a17_, P1710730.a18_, P1710730.a19_, P1710730.a20_, P1710730.a21_, P1710730.a22_ FROM precomp1 AS P1710730 WHERE LOWER(P1710730.a23_) = 'cg3481' ORDER BY P1710730.a23_, P1710730.a18_, P1710730.a19_, P1710730.a20_, P1710730.a21_, P1710730.a22_"));
        assertEquals(eSet, bestQuery.getQueries());
    }*/
}
