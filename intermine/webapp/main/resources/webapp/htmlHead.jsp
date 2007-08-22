<head>
  <html:base/>
  <link rel="stylesheet" type="text/css" href="css/webapp.css"/>
  <link rel="stylesheet" type="text/css" href="css/${pageName}.css"/>
  <link rel="stylesheet" type="text/css" href="model/css/model.css"/>

  <script type="text/javascript" src="js/prototype.js"></script>
  <script type="text/javascript" src="js/scriptaculous.js"></script>

      <script type='text/javascript' src='dwr/interface/AjaxServices.js'></script>
      <script type='text/javascript' src='dwr/engine.js'></script>
      <script type='text/javascript' src='dwr/util.js'></script>
  <script type="text/javascript" src="js/imdwr.js"></script>
  <script type="text/javascript" src="js/imutils.js" ></script>
  <script type="text/javascript" src="js/dropdown.js" ></script>

  <meta content="microarray, bioinformatics, drosophila, genomics" name="keywords"/>
  <meta content="Integrated queryable database for Drosophila and Anopheles genomics"
        name="description"/>
  <meta content="text/html; charset=iso-8859-1" http-equiv="Content-Type"/>

  <title>
    <tiles:get name="title"/>
  </title>

       <script type="text/javascript" src="js/niftycube.js"></script>

  <script type="text/javascript">
  <!--
    function showFeedbackForm()
    {
      document.getElementById('feedbackFormDiv').style.display='';
      document.getElementById('feedbackFormDivButton').style.display='none';
      window.scrollTo(0, 99999);
      document.getElementById("fbname").focus();
    }

    var editingTag;

    function addTag(uid, type) {
      var tag = $('tagValue-'+uid).value;
      new Ajax.Request('<html:rewrite action="/inlineTagEditorChange"/>',
          {parameters:'method=add&uid='+uid+'&type='+type+'&tag='+tag, asynchronous:false});
      refreshTags(uid, type);
      $('tagValue-'+uid).value='';
    }

    function startEditingTag(uid) {
      if (editingTag) {
        stopEditingTag();
      }
      editingTag = uid;
      $('tagsEdit-'+editingTag).style.display='';
      $('addLink-'+editingTag).style.display='none';
      $('tagValue-'+editingTag).focus();
    }

    function stopEditingTag() {
      if (editingTag) {
        $('tagsEdit-'+editingTag).style.display='none';
        $('addLink-'+editingTag).style.display='';
      }
      editingTag = '';
    }

    function refreshTags(uid, type) {
      new Ajax.Updater('currentTags-'+uid, '<html:rewrite action="/inlineTagEditorChange"/>',
          {parameters:'method=currentTags&uid='+uid+'&type='+type, asynchronous:true});
    }
  //-->
  </script>

</head>
