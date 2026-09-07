// modules/newsFeedCutter.js

const newsFeedCutter = {

    go: function () {
        newsFeedCutter.init();
    },

    init: function () {
        const $newsColumn = $('.news-column');
        const $newsFeed = $('.news-feed');

        if (!$newsColumn.length || !$newsFeed.length) {
            return;
        }

        function fitNewsArticles() {
            const $articles = $newsFeed.find('article.event');

            // Reset before measuring
            $articles
                .show()
                .removeClass('last-visible');

            // Use the column as the fixed boundary
            const newsColumnBottom = $newsColumn[0]
                .getBoundingClientRect()
                .bottom;

            let $lastVisible = null;
            let hideRest = false;

            $articles.each(function () {
                const $article = $(this);

                if (hideRest) {
                    $article.hide();
                    return;
                }

                const articleBottom = this
                    .getBoundingClientRect()
                    .bottom+125;

                if (articleBottom > newsColumnBottom) {
                    $article.hide();
                    hideRest = true;
                    return;
                }

                $lastVisible = $article;
            });

            if ($lastVisible) {
                $lastVisible.addClass('last-visible');
            }
        }

        // Wait until the current layout is finished
        requestAnimationFrame(fitNewsArticles);

        const resizeObserver = new ResizeObserver(function () {
            requestAnimationFrame(fitNewsArticles);
        });

        resizeObserver.observe($newsColumn[0]);

        $(window).on('resize.newsFeedCutter', function () {
            requestAnimationFrame(fitNewsArticles);
        });
    }
};