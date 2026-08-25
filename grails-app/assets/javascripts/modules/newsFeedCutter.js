
// modules/newsFeedCutter.js

newsFeedCutter = {

  go: function () {
    newsFeedCutter.init('body')
  },

  init: function () {

      const $newsColumn = $('.news-column');
      const $newsFeed = $('.news-feed');

      if (!$newsColumn.length || !$newsFeed.length) {
        return;
      }

      function fitNewsArticles() {

        $articles = $('.news-feed article.event');
        $articles
            .show()
            .removeClass('last-visible');


        const feedBottom = $('.news-feed')[0]
            .getBoundingClientRect()
            .bottom;

        let $lastVisible = null;
        let hideRest = false;

        $articles.each(function () {
          const $article = $(this)
          if (hideRest) {
            $article.hide();
            return;
          }
          //catch first bottom > feedBottom: 1.)hide this article 2.) hideRest
          if (this.getBoundingClientRect().bottom > feedBottom) {
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

      // Initial
      fitNewsArticles();

      // react to other hight of browser via column
      const resizeObserver = new ResizeObserver(function () {
        fitNewsArticles();
      });

      resizeObserver.observe($newsColumn[0]);

      // and react to browser resize
      window.addEventListener("resize", fitNewsArticles);

  }
}