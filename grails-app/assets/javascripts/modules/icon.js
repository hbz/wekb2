// module: assets/javascripts/modules/a11y/elements/icon.js

a11yIcon = {

    go: function () {
        a11yIcon.init()
    },

    init: function () {

        $('i.icon').not('.dropdown.icon').attr( {
            'aria-hidden' : 'true',
            'focusable' : 'false' // no focusstop additional to the focusstop at button or link
        });
    }
}

