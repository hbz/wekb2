
// modules/paginationNavi.js

paginationNavi = {

  go: function () {
    paginationNavi.init('body')
  },

  init: function () {
    var pagination = $('.ui.pagination.menu')
    $.each( pagination, function( i ) {
      $(this).attr( "aria-label", "pagination-label-"+(i+1));
    });
  }
}