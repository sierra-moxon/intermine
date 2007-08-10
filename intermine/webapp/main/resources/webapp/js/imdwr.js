function setFavourite(name, type, image){
    var img = image.src;
    var isFavourite;
    if (img.substring(img.length-12, img.length) == 'unactive.gif') {
        isFavourite = false;
    } else {
        isFavourite = true;
    }
    AjaxServices.setFavourite(name, type, isFavourite);
    // already a favourite.  turning off.
    if (isFavourite) {
        image.src='images/star_unactive.gif';
        image.title='Set as favourite';
        // not a favourite.  turn on.
    } else {
        image.src='images/star_active.gif';
        image.title='This is a favourite';
    }
}

function precomputeTemplate(templateName){
    document.getElementById('precompute_'+templateName).innerHTML="Precomputing..";
    AjaxServices.preCompute(templateName,function(str) {
        document.getElementById('precompute_'+templateName).style.color="#777";
        document.getElementById('precompute_'+templateName).innerHTML="Precomputed";
     });
}

function summariseTemplate(templateName){
    document.getElementById('summarise_'+templateName).innerHTML="Summarising..";
    AjaxServices.summarise(templateName,function(str) {
        document.getElementById('summarise_'+templateName).style.color="#777";
        document.getElementById('summarise_'+templateName).innerHTML="Summarised";
    });
}

function editName(name){
  document.getElementById('form_'+name).style.display="block";
  document.getElementById('name_'+name).style.display="none";
}

function renameElement(name, type, index){
    document.getElementById('form_'+name).style.display="none";
    document.getElementById('name_'+name).innerHTML="<i>saving...</i>";
    document.getElementById('name_'+name).style.display="block";
    AjaxServices.rename(name,type, (document.getElementById('newName_'+name).value).replace(/^\s*|\s*$/g,""), function(str){
        document.getElementById('name_'+name).innerHTML=str;
        // coming from mymine
        if (document.getElementById('selected_' + type + '_' + index) != null) {
            document.getElementById('selected_' + type + '_' + index).value=str;
            // coming from bags/templates pages
        } else {
            document.getElementById('selected_user_' + type + '_' + index).value=str;
        }
    });
}

function changeViewPathDescription(pathName){
    var pathString = pathName.replace(/\@sep\@/g,".");
    var pathEnd = pathString.substring(pathString.lastIndexOf('.') + 1);
    document.getElementById('form_'+pathName).style.display = "none";
    document.getElementById('name_'+pathName+'_inner').innerHTML = "<i>saving...</i>";
    document.getElementById('name_'+pathName).style.display = "block";
    var newDescription = document.getElementById('newName_' + pathName).value;
    var callBack = function(prefixDescription){
        if (prefixDescription == null) {
            prefixDescription = '(no description)';
        }
        document.getElementById('name_' + pathName + '_inner').innerHTML =
          '<span  class="viewPathDescription">' + prefixDescription +
          '</span> &gt; ' + pathEnd;
    }
    AjaxServices.changeViewPathDescription(pathString, newDescription, callBack);
}

function saveBagDescription(bagName){
    var textarea = document.getElementById('textarea').value;
    textarea = textarea.replace(/[\n\r]+/g, "\n\r<br/>");
    document.getElementById('bagDescriptionDiv').innerHTML = '<i>Saving...</i>';
    AjaxServices.saveBagDescription(bagName,textarea, function(str){
                                        document.getElementById('bagDescriptionDiv').innerHTML = str;
                                    });
    swapDivs('bagDescriptionTextarea','bagDescriptionDiv');
}

function swapDivs(div1,div2){
    document.getElementById(div1).style.display = 'none';
    document.getElementById(div2).style.display = 'block';
}


function roundToSignificantDecimal(n) {
    var log10n = Math.log(Math.abs(n)) / Math.log(10);
    if(log10n<1 && n!=0) {
        var rf = 100*Math.pow(10,-Math.round(log10n));
        return rf;
    } else return 100;
}

function updateCountInColumnSummary() {
    var countString = document.resultsCountText;
    if (countString == null) {
        setTimeout("updateCountInColumnSummary()", 1000);
        return;
    }

    document.getElementById('summary_row_count').innerHTML = "<p>" + countString + "</p>";

    var est = document.getElementById('resultsCountEstimate');
    if (est == null || est.style.display != 'none') {
        setTimeout("updateCountInColumnSummary()", 500);
        return;
    }
}

function updateUniqueCountInColumnSummary(uniqueCountQid) {
    getResults(uniqueCountQid, 1000, resultsCountCallback, null);
}

function resultsCountCallback(results) {
    var size = results[0][0];

    if (size > 1) {
        var summaryUniqueCountElement = document.getElementById('summary_unique_count');
        summaryUniqueCountElement.style.display='inline';
        summaryUniqueCountElement.innerHTML='Total unique values: ' + size;
    }
    return true;
}

function getColumnSummary(tableName, columnName, columnDisplayName) {
    document.getElementById('summary_loaded').style.display = "none";
    document.getElementById('summary_loading').style.display = "block";
    Effect.Appear('summary', { duration: 0.30 });
    AjaxServices.getColumnSummary(tableName, columnName, function(str){
        var rows = str[0];
        function rounder(cell) {
            if (cell==null) {
                return "[no value]" ;
            } if (! isNaN(cell)) {
                var rf = roundToSignificantDecimal(cell+0);
                return Math.round(cell*rf)/rf;
            } else {
                return cell;
            }
        }
        var headerText;
        if(rows[0].length == 2){
            headerText = '<tr><th>Value</th><th>Count</th></tr>';
        } else {
            headerText = '<tr><th>Min</th><th>Max</th><th>Sample Mean</th><th>Standard Deviation</th></tr>';
        }

        document.getElementById('summary_loading').style.display = "none";
        document.getElementById('summary_loaded').style.display = "block";

        var bodyText = '';

        for (rowIndex = 0; rowIndex < rows.length; rowIndex++) {
            var row = rows[rowIndex];
            bodyText += '<tr>';
            for (colIndex = 0; colIndex < row.length; colIndex++) {
                var cellValue = rounder(row[colIndex]);
                bodyText += '<td>' + cellValue + '</td>';
            }
            bodyText += '</tr>';
        }

        var html = '<div class="box">                                         \
                      <div class="title">                                     \
                        Column Summary for ' + columnDisplayName + '          \
                      </div>                                                  \
                      <div id="summary_row_count"></div>                      \
                      <div id="summary_unique_count"></div>                   \
                      <br/>                                                   \
                      <table class="results" cellpadding="0" cellspacing="0"> \
                        <thead id="summary_head">' + headerText +'</thead>    \
                        <tbody id="summary_table">' + bodyText + '</tbody>    \
                      </table>                                                \
                    </div>';

        var summaryLoadedElement = document.getElementById('summary_loaded');
        summaryLoadedElement.innerHTML = html;
        var uniqueCountQid = str[1];
        setTimeout("updateCountInColumnSummary()", 200);
        setTimeout("updateUniqueCountInColumnSummary(" + uniqueCountQid + ")", 300);
                                  });
}

function getResultsPoller(qid, timeout, userCallback, userData) {
    var callback = function(results) {
        if (results == null) {
            // try again
            getResults(qid, timeout, userCallback, userData);
        } else {
            if (!userCallback(results, userData)) {
                getResults(qid, timeout, userCallback, userData);
            }
        }
    }

    AjaxServices.getResults(qid, callback);
}

function getResults(qid, timeout, userCallback, userData) {
    setTimeout("getResultsPoller(" + qid + ", " + timeout + ", " + userCallback + ","
               + userData + ")", timeout);
}

// delete all the child nodes of the node given by parentElement and create a child
// element of type childTag, setting the text in the new child element to be
// childText
function setChild(parentElement, childText, childTag) {
    var newChild = document.createElement(childTag);
    newChild.innerHTML = childText;
    if (parentElement.firstChild != null) {
        while (parentElement.hasChildNodes()) {
            parentElement.removeChild(parentElement.firstChild);
        }
    }
    parentElement.appendChild(newChild);
    return newChild;
}

var callId = 1;

// Give each ajax call an id and remember what our current pending id is
var currentFilterCallbacks = new Array();

// Give each setTimeout call an id and remember what our current pending id is
var futureFilterCalls = new Array();

function filterWebSearchablesHandler(event, object, scope, type, wsListId) {
    if (window.event) {
        event = window.event;
    }
    if (event) {
        if (event.keyCode == 27) {
            object.value = '';
            return;
        }
        if (event.keyCode == 13) {
            return;
        }
    }

    futureFilterCalls[wsListId + "_" + type] = callId;

    if (tags == null) {
        tags = [];
    }

    setTimeout('filterWebSearchables("' + object.id + '", "' + scope + '","' + type + '","' + callId + '","' + wsListId + '")', 500);
    callId++;
}

// given a list of WebSearchables names,scores and descriptions, filter the
// wsFilterList given by the wsListId and type parameters
function do_filtering(filteredList, type, wsListId) {
    if (filteredList.length == 0) {
        showAll(wsListId, type);
    } else {
        var scoreHash = new Array();
        var descHash = new Array();
        var hitHash = new Array();

        for (var el in filteredList) {
            var wsName = filteredList[el][0];
            var wsDesc = filteredList[el][1];
            descHash[wsListId + '_' + type + '_item_line_' + wsName] = wsDesc;
            var wsScore = filteredList[el][2];
            scoreHash[wsListId + '_' + type + '_item_line_' + wsName] = wsScore;
            hitHash[wsListId + '_' + type + '_item_line_' + wsName] = 1;
        }

        var pattern = new RegExp('^' + wsListId + '_' + type + '_item_line_(.*)');
        var inputArray = document.getElementsByTagName("div");
        for(var i=0; i<inputArray.length; i++) {
	        result = pattern.exec(inputArray[i].id);
            if (result != null ) {
                if (hitHash[inputArray[i].id]) {
                    inputArray[i].style.display='block';
                    var highlightText = descHash[inputArray[i].id];

                    if (highlightText) {
                        var descId = wsListId + '_' + type + '_item_description_' + result[1];
                        var desc = $(descId);
                        desc.style.display = 'none';

                        var descHighlightId = wsListId + '_' + type + '_item_description_' + result[1] + '_highlight';
                        var descHighlight = $(descHighlightId);
                        descHighlight.style.display = 'block';

                        var descChild = setChild(descHighlight, highlightText, 'p');
                    }

                    if (scoreHash[inputArray[i].id]) {
                        var scoreWsName = result[1];
                        var score = scoreHash[inputArray[i].id];
                        var intScore = parseInt(score * 10);
                        var scoreId = wsListId + '_' + type + '_item_score_' + scoreWsName;
                        var scoreSpan = $(scoreId);
                        // we do this instead of scoreSpan.innerHTML = "stuff"
                        // because it's buggy in Internet Explorer
                        var heatImage = 'heat' + intScore + '.gif';
                        var heatText = '<img height="10" width="' +
                            (intScore * 3) + '" src="images/' + heatImage + '"/>';
                        setChild(scoreSpan, heatText, 'span');
                    }
                } else if(document.getElementById(wsListId + '_' + type + '_chck_' + result[1]).checked != true){
                    inputArray[i].style.display='none';
                }
            }
        }

        function sortWsFilter(el1, el2) {
            var el1score = scoreHash[el1.id];
            var el2score = scoreHash[el2.id]
                if (el1score > el2score) {
                    return -1;
                } else {
                    if (el1score < el2score) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
        }

        var parent = $(wsListId + '_' + type + '_ws_list');
        var divs = new Array();

        for (var i in parent.childNodes) {
            var child = parent.childNodes[i];
            if (child.tagName == 'DIV' && scoreHash[child.id]) {
                divs.push(child);
            }
        }

        divs.sort(sortWsFilter);

        for (var i = 0; i < divs.length; i++) {
            parent.appendChild(divs[i]);
        }
        $(wsListId + '_' + type + '_spinner').style.display = 'none';
        $(wsListId + '_' + type + '_container').style.display = 'block';
    }
}

// un-hide all the rows in the webSearchableList
function showAll(wsListId, type) {
    var pattern = new RegExp('^' + wsListId + '_' + type + '_item_line_(.*)');
    var inputArray = document.getElementsByTagName("div");
    for(var i=0; i<inputArray.length; i++) {
        var result;
        if ((result = pattern.exec(inputArray[i].id)) != null) {
            inputArray[i].style.display='block';
            var scoreId = wsListId + '_' + type + '_item_score_' + result[1];
            var scoreSpan = $(scoreId);
            // we do this instead of scoreSpan.innerHTML = "stuff"
            // because it's buggy in Internet Explorer
            setChild(scoreSpan, '', 'span');

            var descId = wsListId + '_' + type + '_item_description_' + result[1];
            var desc = $(descId);
            desc.style.display = 'block';

            var descHighlightId = wsListId + '_' + type + '_item_description_' + result[1] + '_highlight';
            var descHighlight = $(descHighlightId);
            descHighlight.style.display = 'none';
        }
    }

    // reset descriptions
    var descPattern = new RegExp('^' + wsListId + '_' + type + '_item_description_(.*)_highlight');
    var inputArray = document.getElementsByTagName("div");
    for(var i=0; i<inputArray.length; i++) {
        var result;
        if ((result = descPattern.exec(inputArray[i].id)) != null) {
        }
    }
    $(wsListId + '_' + type + '_spinner').style.visibility = 'hidden';
}


// call AjaxServices.filterWebSearchables() then hide those WebSearchables in
// the webSearchableList that don't match
function filterWebSearchables(objectId, scope, type, callId, wsListId) {
    if (futureFilterCalls[wsListId + "_" + type] != callId) {
        // filterWebSearchablesHandler() has been called again since this
        // timeout was set, so ignore as another timeout will be along
        // shortly
        return;
    }

    var object = document.getElementById(objectId);
    var value = object.value;
    var filterAction = document.getElementById('filterAction' + '_' + wsListId + '_' + type).value;


    if ( (value != null) || (tags != null && tags.length > 1)) {
        function filterCallBack(cbResult) {
            var callId = cbResult[0];
            var filteredList = cbResult.slice(1);

            if (currentFilterCallbacks[wsListId + "_" + type] != callId) {
                // we've started another call since this one started, so
                // ignore this one
                return;
            }

            do_filtering(filteredList, type, wsListId);

            showWSList(wsListId, type);
        }

        $(wsListId + '_' + type + '_spinner').style.display = 'block';
        $(wsListId + '_' + type + '_container').style.display = 'none';
        currentFilterCallbacks[wsListId + "_" + type] = callId;
        var tagList = null;

        /*
          We need to transform our map into a proper Array
        */
        tagList = new Array();
        if(tags['favourites_' + wsListId] != null && tags['favourites_' + wsListId] != '') {
            tagList[tagList.length]=tags['favourites_' + wsListId];
        } if(tags['aspects_' + wsListId] != null && tags['aspect_' + wsListId] != '') {
            tagList[tagList.length]=tags['aspects_' + wsListId];
        }

        /*  filterAction toggles favourites off and on */
        AjaxServices.filterWebSearchables(scope, type, tagList, object.value, filterAction,
                                          callId++, filterCallBack);
    } else {
        showAll(wsListId, type);
    }

    futureFilterCalls[wsListId + "_" + type] = 0;
}

var tags = new Array();

function filterFavourites(type, wsListId) {
    var id = 'filterAction_'+wsListId+'_'+type;
    // var tags = '';
    var scope = document.getElementById('filterScope_'+wsListId+'_'+type).value;

    // favourites OFF
    if(document.getElementById(id).value == "favourites") {
        document.getElementById(id).value = "";
        document.getElementById('filter_favourites_'+wsListId+'_'+type).src = 'images/filter_favourites.png';
        delete tags['favourites_' + wsListId];
    // favourites ON
    } else {
        document.getElementById(id).value = "favourites";
        document.getElementById('filter_favourites_'+wsListId+'_'+type).src = 'images/filter_favourites_active.png';
        // tags = (tags == '' ? 'favourite' : tags + '|favourite');
        tags['favourites_' + wsListId] = 'favourite';
    }
    var filterTextElement = document.getElementById(wsListId+'_'+type+'_filter_text');
    return filterWebSearchablesHandler(null, filterTextElement, scope, type, wsListId);
}

function filterAspect(type, wsListId) {
    var id = 'filterAction_'+wsListId+'_'+type;
    var aspect = document.getElementById(wsListId+'_'+type+'_filter_aspect').value;
    var scope = document.getElementById('filterScope_'+wsListId+'_'+type).value;

    // aspects ON
    if(aspect != null && aspect.length > 1) {
        aspect = 'aspect:'+ aspect;
        tags['aspects_' + wsListId] = aspect;
    } else {
        delete tags['aspects_' + wsListId]
    }

    var filterTextElement = document.getElementById(wsListId+'_'+type+'_filter_text');
    return filterWebSearchablesHandler(null, filterTextElement, scope, type, wsListId);
}

function changeScope(type, wsListId) {
    var id = 'filterScope_'+wsListId+'_'+type;
    var scope = document.getElementById(id).value;

    if(scope == 'all') {
        document.getElementById(id).value = 'user';
        document.getElementById('filter_scope_'+wsListId+'_'+type).src = 'images/filter_my_active.png';
    } else if(scope == 'user') {
        document.getElementById(id).value = 'all';
        document.getElementById('filter_scope_'+wsListId+'_'+type).src = 'images/filter_all.png';
    }
    var filterTextElement = document.getElementById(wsListId+'_'+type+'_filter_text');
    return filterWebSearchablesHandler(null, filterTextElement, document.getElementById(id).value, type, wsListId);
}

function clearFilter(type, wsListId) {
    var scopeId = 'filterScope_'+wsListId+'_'+type;
    var favId = 'filterAction_'+wsListId+'_'+type;
    var aspectId = wsListId+'_'+type+'_filter_aspect';

    document.getElementById(scopeId).value = 'all';
    var scopeElement = document.getElementById('filter_scope_'+wsListId+'_'+type);
    if (scopeElement != null) {
        scopeElement.src = 'images/filter_all.png';
    }
    delete tags['favourites_' + wsListId];

    document.getElementById(favId).value = "";
    var favElement = document.getElementById('filter_favourites_'+wsListId+'_'+type);
    if (favElement != null) {
        favElement.src = 'images/filter_favourites.png';
    }

    delete tags['aspects_' + wsListId];

    if ($(aspectId) != null) {
        $(aspectId).value = '';
    }
    var filterTextElement = document.getElementById(wsListId+'_'+type+'_filter_text');
    filterTextElement.value = '';

    showAll(wsListId, type);
    return false;
}
