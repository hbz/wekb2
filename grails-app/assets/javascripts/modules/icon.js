// module: assets/javascripts/modules/a11y/elements/icon.js

a11yIcon = {

    go: function () {
        a11yIcon.init()
    },

    init: function () {


        $('.ui.icon.button i.icon').attr( {
            'aria-hidden' : 'true',
            'focusable' : 'false'
        });
    }
}

