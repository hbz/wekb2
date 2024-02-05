
// modules/paginationNavi.js

paginationNavi = {

  go: function (ctxSel) {
    paginationNavi.init(ctxSel)
  },

  init: function (ctxSel) {
    //let pagination_string = (ctxSel + " .ui.pagination.menu");
   // console.log(typeof pagination_string);
    // console.log(pagination_string);
    console.log(ctxSel);



      let test = $(ctxSel + " .ui.pagination.menu").attr( "aria-label", "pagination-label-"+(i+1));
       console.log(typeof test);
      console.log(test);
  }
}