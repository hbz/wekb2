(function($) {

  var modCache = {};

  $(document).ready(function() {

/*    /!** Bootstrap 4.6.  - Loading Tabs from URL with hash **!/
        // Javascript to enable link to tab
    var hash = location.hash;

    if (hash) {
      $('.nav-item a[href="' + hash + '"]').tab('show');

    // Change hash for page-reload
    $('.nav-item a').on('shown.bs.tab', function(e) {
      location.hash = e.target.hash;
    })
    }*/
    // Find every inline link.
    $('a.display-inline').each(function(){
      // The link.
      var link = $(this);

      // The actual url.
      var href = link.attr("href");

      // Selector of the sub=part of the page to pull in.
      var desired_selector = link.attr("data-content-selector");
      var auto_refresh = link.attr("data-auto-refresh");

      if (!desired_selector) desired_selector = "#mainarea";

      // The function to refresh content of the target element from the url.
      var refreshContent = function (target, url, type, the_data) {

        var key = "mod-" + url;

        // Grab the data.
        var grabContent = function() {


          // Default data.
          if (the_data == undefined) the_data = {};

          // Default type
          if (type == undefined) {
            type = "get";

          } else if (type != "get") {

            // Make it a post.
            type = "post";
          }
          $[type](url, the_data, function(data, textStatus, xhr) {
            modCache[key] = xhr.getResponseHeader("Last-Modified");

            // The returned data.
            var dataDom = $("<div>" + data + "</div>");
            var desired_content = dataDom.find(desired_selector);
            if (desired_content.length == 1) {
              target.html(desired_content.html());
            } else {
              target.html(dataDom.html());
            }
          });
        };

        if(modCache[key]) {
          $.ajax({
            url:url,
            type:"head",
            success:function(res,code,xhr) {
              if(modCache[key] != xhr.getResponseHeader("Last-Modified")) {
                // Get the content.
                grabContent();
              } else { /* Content hasn't changed so let's leave it alone */ }
            }
          });

        } else grabContent();

        // Return the target.
        return target;
      };

      // Start with a new div.
      var content = refreshContent($("<div class='inline-content' />"), href);

      // The next thing to do is to bind an event listener to the content,
      // that can intercept links that are nav. These links should reload in the content area
      // rather than navigating away from the page.
      content.on ('click', function(event) {
        // The clicks should bubble up here before being actioned.

        // The clicked item. Get the closest matching a tag.
        var clicked = $(event.target).closest('.nav a, .inline-nav a, a.open-inline');

        if (clicked.length > 0) {
          // Is a nav link. First thing to do is to stop the event default.
          event.preventDefault();

          // Now let's refresh this object.
          refreshContent( $(this), clicked.attr('href') );
        }

        // Else just allow the event to propagate.
      });

      // Add the new submit listener for forms here too.
      // We should catch the form submit and then simply serialise the form and do
      // the get or post n the background using ajax.
      content.on ('submit', function(event) {
        // The clicks should bubble up here before being actioned.

        // The clicked item. Get the closest matching a tag.
        var form = $(event.target).closest('form.open-inline');

        if (form.length > 0) {

          // Is a inline form. First thing to do is to stop the event default.
          event.preventDefault();

          // Now let's refresh this object.
          refreshContent( $(this), form.attr('action'), form.attr('method'), form.serialize());
        }
      });
      var myNum;
      function printANumber(myNum, callback) {
        link.replaceWith(content);
        callback();

      }

// a function which we will use in a driver function as a callback function
      function printFinishMessage() {
        cairo.initDynamicSemuiStuff('.inline-content');
      }

// Driver method
      function event() {
        printANumber(8, );
      }

      event();

      // Then swap out the link for the new content.
/*      link.replaceWith(content, function() {
        alert("test");
        cairo.initDynamicSemuiStuff('.inline-content');
      });*/
/*      link.append(function(){
        return content;
      });*/
      //cairo.initDynamicSemuiStuff('.inline-content');

/*      $.fn.replaceWithCallback = function(replace, callback){
        var ret = $.fn.replaceWith.call(link, replace); // Call replaceWith
        if(typeof callback === 'function'){
          callback.call(ret); // Call your callback
        }
        return ret;  // For chaining
      };
      $.fn.replaceWithCallback(content,$('.inline-content').find('.ui.pagination.menu').attr("aria-label", "pagination-label"));*/

      console.log('---------------------------')
      console.log($('.inline-content').find('.ui.pagination.menu'));


      // Automatically update the content
      if (auto_refresh && !isNaN(auto_refresh)) {
        auto_refresh = parseInt(auto_refresh)

        // Add an autorefresh method.
        setInterval(function() {
          refreshContent( content, href );
        }, auto_refresh);
      }
    });
  });

})(jQuery);