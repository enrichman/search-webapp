<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="../../favicon.ico">

    <title>Starter Template for Bootstrap</title>

    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap-theme.min.css">
    <link rel="stylesheet" href="https://gitcdn.github.io/bootstrap-toggle/2.2.0/css/bootstrap-toggle.min.css">
    <link rel="stylesheet" href="../css/typeahead.css">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->

    <style>
        html, body, .container {
            height: 101%;
        }
        .container {
            display: table;
            vertical-align: middle;
        }
        #resultsContainer {
            margin-top: 60px;
        }
        .vertical-center-row {
            display: table-cell;
            vertical-align: middle;
        }
        .count {
            color: grey;
            padding: 10px 0px;
        }
        .suggestions {
            font-style: italic;
            color: #dd4b39;
        }
        .sugg {
            cursor: pointer;
            color: #1a0dab;
            font-weight: bold;
            font-size: 18px;
        }
        .site {
            margin-top: 20px;
            margin-bottom: 20px;
        }
        .site-title {
            margin: 0px;
        }
        .site-url {
            color: green;
        }
        em {
            font-style: normal;
            font-weight: bold;
        }
        .site-title a {
            color: #1a0dab;
        }
        div[data-toggle="toggle"] {
            width: 120px !important;
        }
    </style>

</head>

<body>

<nav class="navbar navbar-inverse navbar-fixed-top" style="display:none;">
    <div class="container">
        <div id="navbar" class="collapse navbar-collapse">
            <form class="navbar-form" id="navQuery">
                <div class="form-group">
                    <div class="form-group">
                        <input type="text" class="form-control queryInput" id="navQueryInput" placeholder="Looking for..">
                    </div>

                    <input id="safeInput" type="checkbox" checked data-toggle="toggle"
                           data-on="Safe" data-off="Not safe"
                           data-onstyle="success" data-offstyle="danger" >
                </div>
            </form>
        </div><!--/.nav-collapse -->
    </div>
</nav>

<div class="container">
    <div id="eventBroker" ></div>

    <div class="row vertical-center-row">
        <div class="col-lg-12" id="mainQuery">
            <div class="row text-center">
                <img src="../img/delight.jpg" />
            </div>
            <div class="row ">
                <div class="col-xs-4 col-xs-offset-3">
                    <form>
                        <div class="form-group">
                            <input type="text" class="form-control queryInput" id="mainQueryInput"
                                   placeholder="Looking for.." style="margin-top: 20px; width: 500px;">
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <div id="resultsContainer" class="row" style="display:none;">
        <div id="resultCount" class="col-lg-12 count">Result count</div>
        <div class="col-lg-12 suggestions">
            <h4>Forse cercavi: <a id="spellcheck" class="sugg"></a></h4>
        </div>
        <hr class="col-lg-12" style="margin: 0px;">

        <div id="paginationContainer" class="text-center">
            <ul class="pagination">
                <li><a href="#" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>
                <li class="active"><a href="#">1</a></li>
                <li><a href="#">2</a></li>
                <li><a href="#">3</a></li>
                <li><a href="#">4</a></li>
                <li><a href="#" aria-label="Previous"><span aria-hidden="true">&raquo;</span></a></li>
            </ul>
        </div>

        <div id="results" class="col-lg-12"></div>
    </div>
</div>


<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js"></script>
<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
<script src="https://gitcdn.github.io/bootstrap-toggle/2.2.0/js/bootstrap-toggle.min.js"></script>
<script src="http://twitter.github.com/typeahead.js/releases/latest/typeahead.bundle.min.js"></script>



<script src="../js/tonu.js"></script>

<script>
    var tonu = new Tonu();

    $(function() {

        $("#eventBroker").on('page.click', function(evt, page) {
            var value = $('#navQueryInput').typeahead('val');
            query(value, page);
        });

        function query(value, page) {
            $("#results").empty();

            var safe = $("#safeInput").is(":checked");

            if(!page)
                page = 1;

            $.ajax({
                url: "rest/search",
                data: { q: value, safe: safe, page: page }
            }).done(function(json) {

                if(!!json.suggestions) {
                    $(".suggestions").show();
                    $("#spellcheck").text(json.suggestions[0]);
                } else {
                    $(".suggestions").hide();
                }

                var results = $("#resultCount");
                var $pagContainer = $("#paginationContainer");

                if(json.numFound == 0) {
                    $pagContainer.hide();
                    results.text("Nessun risultato");

                } else {
                    results.show();
                    results.text("Trovati "+json.numFound+" risultati in "+json.qTime+" ms");

                    $pagContainer.show();
                    $pagContainer.empty();
                    $pagContainer.append(tonu.renderPagination(json, page));
                }

                var $container = $("#results");
                tonu.renderSites($container, json.querySites);
            });
        };

        var toggled = false;
        function toggle() {
            $("nav").show();
            $("#mainQuery").hide();
            $('#navQueryInput').focus();
            toggled = true;
        }

        function search(value, page) {
            if(value == "") {
                $("#resultsContainer").hide();
            } else {
                $("#resultsContainer").show();
            }

            if(!toggled) {
                toggle();
            }
            query(value);
        }

        $('.queryInput').on('input', function() {
            $input = $(this);
            var value = $input.val();

            $('.queryInput').val(value);
            $('#navQueryInput').typeahead('val', value);

            search(value);
        });

        $('.sugg').on('click', function() {
            var value = $(this).text();
            $('#navQueryInput').typeahead('val', value);

            search(value);
        });

        $('#safeInput').on('change', function() {
            var value = $('#navQueryInput').typeahead('val');
            search(value);
        });

        // Instantiate the Bloodhound suggestion engine
        var suggestions = new Bloodhound({
            datumTokenizer: function (datum) {
                return Bloodhound.tokenizers.whitespace(datum.value);
            },
            queryTokenizer: Bloodhound.tokenizers.whitespace,
            remote: {
                url: '/rest/suggest?q=',
                replace: function(url, query) {
                    return url + query;
                }
            }
        });
        // Initialize the Bloodhound suggestion engine
        suggestions.initialize();

        $navQueryInput = $('#navQueryInput');
        $navQueryInput.typeahead(null, {
            displayKey: 'word',
            source: suggestions.ttAdapter()
        });
        $navQueryInput.on('typeahead:select', function(ev, suggestion) {
            search(suggestion.word);
        });

        $("#mainQueryInput").focus();
    });
</script>


</body>
</html>
