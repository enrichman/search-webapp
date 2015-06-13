var Tonu = function() {

    this.renderSites = function($container, sites) {

        $(sites).each(function() {
            var site = this;

            $div = $('<div/>', { "class" : "col-xs site"});

            var title;
            if(site.titleHighlights != null && site.titleHighlights.length > 0) {
                title = site.titleHighlights[0];
            } else {
                title = site.title;
            }

            $h4 = $('<h4/>', { "class": "site-title" });
            $a = $('<a/>', { "href": site.url, "html": title, "target": "_blank" });
            $h4.append($a);
            $div.append($h4);

            $url = $('<div/>', { "html": site.url, "class": "site-url" });
            $div.append($url);

            var body;
            if(site.highlights != null && site.highlights.length > 0) {
                body = site.highlights[0];
            } else {
                body = site.body;
            }
            $highlight = $('<div/>', { "html": body });
            $div.append($highlight);

            $container.append($div);
        });
    };

    this.renderPagination = function(results, page) {
        var pages = Math.ceil(results.numFound / 10);

        var start;
        var end;

        if(pages < 10) {
            start = 1;
            end = pages;

        } else {
            start = page-5;
            end = page+4;

            if(start<1) {
                var diff = Math.abs(start);
                start = 1;
                end += diff+1;
            }
            if(end>pages) {
                var diffEnd = end-pages;
                start -= diffEnd;
                end -= diffEnd;
            }
        }

        var $ul = $('<ul/>', { class : "pagination" });
        var $prevA = $('<a/>', {"href":"#", html: "<span aria-hidden=\"true\">&laquo;</span>" });
        var $prevLi = $('<li/>').append($prevA);

        if(page > 1) {
            $prevA.attr('data-page', (page-1));
        } else {
            $prevLi.addClass("disabled");
        }

        $ul.append($prevLi);

        for(var p = start; p<=end; p++) {
            var $li = $('<li/>').append($('<a/>', { "href":"#", text: p, 'data-page':p }));

            if(page == p) {
                $li.addClass("active");
            }

            $ul.append($li);
        }

        var $nextA = $('<a/>', {"href":"#", html: "<span aria-hidden=\"true\">&raquo;</span>" });
        var $nextLi = $('<li/>').append($nextA);
        $ul.append($nextLi);

        if(page < pages) {
            $nextA.attr('data-page', (page+1));
        } else {
            $nextLi.addClass("disabled");
        }

        var currentPage = page;
        $ul.on('click', 'a', function(evt) {
            var page = $(evt.target).closest("a").data("page");
            if(page !== undefined && page != currentPage)
                $("#eventBroker").trigger("page.click", page);
        });

        return $ul;
    }

};