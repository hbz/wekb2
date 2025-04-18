// module: assets/javascripts/modules/responsiveTables.js

responsiveTables = {

  go: function() {
    responsiveTables.init('body')
  },

  init: function() {


    // smaller then 1200px
    if (window.matchMedia('(max-width: 1200px)').matches) {
      responsiveTables.setDataLabel()
    }

    // Resize the Window
    $(window).resize(function () {
      // smaller then 1200px
      if (window.matchMedia('(max-width: 1200px)').matches) {
        responsiveTables.setDataLabel()
      }
    });
  },
  setDataLabel: function() {
    $('.ui.table').each(function () {
      let currentTable = $(this);
      $('>tbody>tr', this).each(function () {
        $('>td', this).each(function () {

          let th = $(currentTable.find('th')).eq($(this).index());
          if ((th.length === 0)) {
            return false;
          }

          // table header is icon
          if( (th.html().includes("la-popup-tooltip")))  {
            let dataContent = th.find('.la-popup-tooltip').attr("data-content");
            $(this).attr('data-label', dataContent + ':');
          }

          else
            // table header is empty
            if (th.text() === 0) {
            }
            // table header is dropdown menu
            else if( th.html().includes("menu"))  {
              $(this).attr('data-label',th.find('.text').text() + ':');
            }
            else {
              // table header has only text
              $(this).attr('data-label',th.text() + ':');
            }
        });
      });
    });
  }
}