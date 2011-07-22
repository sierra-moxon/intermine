<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<style>
  #gene-expression-atlas div.chart { float:left; }
  #gene-expression-atlas h3 { background-image:url("images/icons/ebi.gif"); background-position:6px 2px; background-repeat:no-repeat;
    line-height:20px; padding-left:28px; }
  #gene-expression-atlas div.sidebar { float:right; width:35%; }
  #gene-expression-atlas div.sidebar p.small { font-size:11px; margin:5px 0 16px 0; }
  #gene-expression-atlas div.sidebar div.legend ul { margin-top:4px; }
  #gene-expression-atlas div.sidebar div.legend span { border:1px solid #000; display:inline-block; height:15px; width:20px; }
  #gene-expression-atlas div.sidebar div.legend span.up { background:#59BB14; }
  #gene-expression-atlas div.sidebar div.legend span.down { background:#0000FF; }
  #gene-expression-atlas div.sidebar input.update { font-weight:bold; }
  #gene-expression-atlas div.sidebar input.update.inactive { font-weight:normal; }
  #gene-expression-atlas div.settings ul.sort { margin-bottom:10px; }
  #gene-expression-atlas div.settings ul.sort li { margin-left:10px !important; background:url('images/icons/sort-up.gif') no-repeat center left;
    padding-left:16px; cursor:pointer; }
  #gene-expression-atlas div.settings ul.sort li.active { background:url('images/icons/sort.gif') no-repeat center left; font-weight:bold; }
  #gene-expression-atlas fieldset { border:0; }
  #gene-expression-atlas fieldset input[type="checkbox"] { margin-right:10px; vertical-align:bottom }
</style>

<div id="gene-expression-atlas">
<h3 class="goog">Gene Expression Atlas Expressions</h3>

<div class="sidebar">
  <div class="legend">
    <strong>Expression</strong>*
    <ul class="expression">
      <li><span class="up"></span> Upregulation</li>
      <li><span class="down"></span> Downregulation</li>
    </ul>
    <p class="small">* Provide displayer description please.</p>
  </div>

  <div class="settings">
    <strong>Sort</strong>
    <ul class="sort">
      <li class="active" title="byName">By tissue name</li>
      <li title="byTStatistic">By t-statistic</li>
      <li title="byPValue">By p-value</li>
    </ul>

    <strong>1) Show regulation type</strong>
    <fieldset class="regulation-type">
      <label for="upregulation-check">Upregulation:</label>
      <input type="checkbox" id="upregulation-check" title="UP" checked="checked" autocomplete="off" />
      <label for="downregulation-check">Downregulation:</label>
      <input type="checkbox" id="downregulation-check" title="DOWN" checked="checked" autocomplete="off" />
    </fieldset>

    <strong>2) Adjust the p value</strong>
    <fieldset class="p-value">
      <tiles:insert name="geneExpressionAtlasDisplayerNonLinearSlider.jsp">
        <tiles:put name="sliderIdentifier" value="p-value" />
        <tiles:put name="defaultValue" value="${defaultPValue}" />
      </tiles:insert>
    </fieldset>

    <strong>3) Adjust the t-statistic</strong>
    <fieldset class="t-statistic">
      <tiles:insert name="geneExpressionAtlasDisplayerLinearSlider.jsp">
        <tiles:put name="sliderIdentifier" value="t-statistic" />
        <tiles:put name="defaultValue" value="${defaultTValue}" />
      </tiles:insert>
    </fieldset>

    <strong>4)</strong>
    <input class="update inactive" type="button" value="Update" title="Update the chart"></input>
  </div>
</div>

<div class="chart" id="gene-expression-atlas-chart"></div>

  <script type="text/javascript">
    <%-- stuff this goodie bag --%>
    var geneExpressionAtlasDisplayer = {};

    <%-- call me to tell me settings have updated --%>
    geneExpressionAtlasDisplayer.settingsUpdated = function() {
      jQuery("#gene-expression-atlas div.settings input.update").removeClass('inactive');
    };

    <%-- call me to draw me --%>
    function drawChart(liszt, redraw) {
      if (redraw) {
        googleChart();
      } else {
        google.setOnLoadCallback(googleChart);
      }

      <%-- the Goog draws here --%>
      function googleChart() {
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Cell type');

        data.addColumn('number', 'Downregulation');
        data.addColumn('number', 'Upregulation');

        var chartDirections = {'up': false, 'down':false};

        var n = 0;
        <%-- for each cell type --%>
        for (x in liszt) {
          var cellType = liszt[x];
          data.addRows(cellType.expressions.length);

          <%-- for each expression (one bar) in this cell type --%>
          for (y in cellType.expressions) {
            var expression = cellType.expressions[y];
            var tStatistic = expression.tStatistic;
            data.setValue(n, 0, cellType.condition);

            var formattedString = tStatistic + ' (t-statistic), ' + expression.pValue + ' (p-value)';

            if (tStatistic > 0) { <%-- UP --%>
              data.setValue(n, 1, 0);
              data.setValue(n, 2, tStatistic);

              data.setFormattedValue(n, 2, formattedString);

              chartDirections.up = true;
            } else {  <%-- DOWN --%>
              data.setValue(n, 1, tStatistic);
              data.setValue(n, 2, 0);

              data.setFormattedValue(n, 1, formattedString);

              chartDirections.down = true;
            }

            n++;
          }
        }

        <%-- modify the chart properties --%>
        var options = {
          isStacked:		true,
          width:			800,
          height:			(9 * n) + 50,
          chartArea:		{left: 400, top: 0, height: 9 * n},
          backgroundColor: 	["0", "CCCCCC", "0.2", "FFFFFF", "0.2"],
          colors: 			['#0000FF', '#59BB14'],
          fontName: 		"Lucida Grande,Verdana,Geneva,Lucida,Helvetica,Arial,sans-serif",
          fontSize: 		11,
          vAxis: 			{title: 'Condition', titleTextStyle: {color: '#1F7492'}},
          hAxis:			{title: 'Expression Value', titleTextStyle: {color: '#1F7492'}},
          legend: 			'none',
          hAxis:			{minValue: geneExpressionAtlasDisplayer.peaks.down - 2, maxValue: geneExpressionAtlasDisplayer.peaks.up + 2}
        };

        // TODO: switch off any loading messages

        var chart = new google.visualization.BarChart(document.getElementById("gene-expression-atlas-chart"));
        chart.draw(data, options);
      }
    }

    <%-- filter expressions in the chart given a variety of filters --%>
    function filterAndDrawChart(redraw) {
      // TODO: chart loading msg

      var filters = geneExpressionAtlasDisplayer.currentFilter;

      <%-- should the expression be included? --%>
      function iCanIncludeExpression(expression, filters) {
        <%-- regulation type (UP/DOWN/NONE) --%>
        if ("regulationType" in filters) {
          if (expression.tStatistic > 0) {
            if (filters.regulationType.indexOf("UP") < 0) {
              return false;
            }
          } else {
            if (expression.tStatistic < 0) {
              if (filters.regulationType.indexOf("DOWN") < 0) {
                return false;
              }
            } else {
              if (filters.regulationType.indexOf("NONE") < 0) return false;
            }
          }
        }

        <%-- p-value --%>
        if ("pValue" in filters) {
          if (expression.pValue > filters.pValue) {
            return false;
          }
        }

        <%-- t-statistic --%>
        if ("tStatistic" in filters) {
          if (expression.tStatistic > filters.tStatistic) {
            return false;
          }
        }

        <%-- all fine --%>
        return true;
      }

      <%-- go through the original list here --%>
      var liszt = new Array();
      if (filters) {
        for (x in geneExpressionAtlasDisplayer.originalList) {
          var oldCellType = geneExpressionAtlasDisplayer.originalList[x]
          var newExpressions = new Array();
          <%-- traverse the unfiltered expressions --%>
          for (y in oldCellType.expressions) {
            var expression = oldCellType.expressions[y];
            <%-- I can not haz dis 1? --%>
            if (iCanIncludeExpression(expression, filters)) {
              newExpressions.push(expression);
            }
          }

          if (newExpressions.length > 0) { <%-- one/some expression(s) met the bar --%>
            var newCellType = {
              'condition': oldCellType.condition,
              'expressions': newExpressions
            };
            liszt.push(newCellType);
          }
        }
      } else {
        liszt = geneExpressionAtlasDisplayer.originalList;
      }

      geneExpressionAtlasDisplayer.newList = liszt;

      <%-- re-/draw the chart --%>
      drawChart(liszt, redraw);
    }

    <%-- sort order separate from filters to work faster --%>
    function sortAndDrawChart(sortOrder) {
      liszt = (geneExpressionAtlasDisplayer.newList) ? geneExpressionAtlasDisplayer.newList : geneExpressionAtlasDisplayer.originalList;

      var sorted = new Array();

      // byName

    }

    function updateCurrentFilter() {
      <%-- regulation type (UP/DOWN/NONE) --%>
      geneExpressionAtlasDisplayer.currentFilter.regulationType = new Array();
      jQuery("#gene-expression-atlas div.settings fieldset.regulation-type input:checked").each(function() {
        geneExpressionAtlasDisplayer.currentFilter.regulationType.push(jQuery(this).attr('title'));
      });

      <%-- p-value --%>
      geneExpressionAtlasDisplayer.currentFilter.pValue = jQuery("#gene-expression-atlas div.settings fieldset.p-value input.value").val();

      <%-- t-statistic --%>
      geneExpressionAtlasDisplayer.currentFilter.tStatistic = jQuery("#gene-expression-atlas div.settings fieldset.t-statistic input.value").val();

      jQuery("#gene-expression-atlas-chart-organism_part.chart").empty();
    }

    <%-- load the Goog and create the initial bag from Java --%>
    (function() {
      google.load("visualization", "1", {packages:["corechart"]});

      <%-- Java to JavaScript --%>
      geneExpressionAtlasDisplayer.originalList = new Array();
      geneExpressionAtlasDisplayer.peaks = {"up":0, "down":0}

      <c:forEach var="cellType" items="${expressions.byName}">
        var expressions = new Array();
        <c:set var="expressions" value="${cellType.value}"/>
        <c:forEach var="expression" items="${expressions.values}">
          var tStatistic = ${expression.tStatistic};
          var expression = {
            'pValue': ${expression.pValue},
            'tStatistic': tStatistic
          };
          expressions.push(expression);

          if (tStatistic > 0) {
            if (tStatistic > geneExpressionAtlasDisplayer.peaks.up) {
                geneExpressionAtlasDisplayer.peaks.up = tStatistic;
            }
          } else {
            if (tStatistic < geneExpressionAtlasDisplayer.peaks.down) {
                geneExpressionAtlasDisplayer.peaks.down = tStatistic;
            }
          }
        </c:forEach>

        var expression = {
          'condition': '${cellType.key}',
          'expressions': expressions
        };
        geneExpressionAtlasDisplayer.originalList.push(expression);
      </c:forEach>;

      <%-- create filter --%>
      geneExpressionAtlasDisplayer.currentFilter = {};
      updateCurrentFilter();

      <%-- let's rumble --%>
      filterAndDrawChart();
    })();

    <%-- attache events to the sidebar settings, set as filters and redraw --%>
    (function() {
      jQuery("#gene-expression-atlas div.settings input.update").click(function() {
        updateCurrentFilter();
        <%-- redraw --%>
        filterAndDrawChart(true);
        <%-- update button not highlighted --%>
        jQuery(this).addClass('inactive');
      });
    })();

    <%-- attache monitoring for regulation type checkbox change --%>
    (function() {
      jQuery("#gene-expression-atlas div.settings fieldset.regulation-type input").click(function() {
        if (typeof geneExpressionAtlasDisplayer == 'object') {
          geneExpressionAtlasDisplayer.settingsUpdated();
        }
      });
    })();
  </script>

<div style="clear:both;"></div>
</div>