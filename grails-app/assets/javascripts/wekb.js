// FileName: wekb.js
// the order must be observed!!!

//=require /jquery-3.6.0.min                                //-- externalLibs

//=require /jquery-ui.min.js                  //jquery-ui datepicker

//= require /jquery.poshytip.js                              //-- externalLibs

//= require /jquery-editable-poshytip.js                     //-- externalLibs


//=require /semantic.min.js                                 //-- semantic

//=require inline-content.js

//=require loadEcharts.js

//=require modules/verticalNavi.js

//=require modules/paginationNavi.js

//=require modules/responsiveTables.js

console.log('+ wekb.js')


$(function () {

    verticalNavi.go();
    paginationNavi.go();
    responsiveTables.go();

    $('.ui.sticky')
        .sticky({
            context: '#rightBox',
            pushing: true,
            setSize: true
        })
    ;

    $('.ui.dropdown')
        .dropdown()
    ;

    $('.tabular.menu .item')
        .tab()
    ;

    $('.ui.accordion')
        .accordion()
    ;

    $('.ui.checkbox')
        .checkbox()
    ;

    $('.ui.popup').each(function() {
        $(this).popup()
    });

    //Editable
    $.fn.editable.defaults.mode = 'inline';
    $.fn.editable.defaults.onblur = 'ignore';
    $.fn.editableform.buttons = '<button type="submit" class="ui icon primary button editable-submit"><i aria-hidden="true" class="check icon"></i></button>' +
        '<button type="button" class="ui icon primary button editable-cancel"><i aria-hidden="true" class="times icon"></i></button>';
    $.fn.editableform.buttonImage = "images/ui-bg_glass_95_fef1ec_1x400.png";
    $.fn.editableform.template =
        '<form class="ui form editableform">' +
        '	<div class="control-group">' +
        '		<div class="ui calendar xEditable-datepicker">' +
        '			<div class="ui input right icon editable-input">' +
        '			</div>' +
        '			<div class="editable-buttons">' +
        '			</div>' +
        '		</div>' +
        '        <div class="editable-error-block"></div>' +
        '        <div id="characters-count"></div>' +
        '		</div>' +
        '	</div>' +
        '</form>';
    $.fn.editableform.loading =
        '<div class="ui active inline loader"></div>';
    $('.xEditableValue').editable({
        validate: function(value) {
            if ($(this).attr('data-format') && value) {
                if(! (value.match(/^\d{4}-\d{1,2}-\d{1,2}$/)) ) {
                    return "Invalid format.";
                }
            }

            if ($(this).attr('data-required')) {
                if($.trim(value) == '') {
                    return "This field is required";
                }
            }
            // custom validate functions via semui:xEditable validation="xy"
            var dVal = $(this).attr('data-validation')
            if (dVal) {
                if (dVal.includes('notEmpty')) {
                    if($.trim(value) == '') {
                        return "This field cannot be left empty.";
                    }
                }
                if (dVal.includes('url')) {
                    var regex = /^(https?|ftp|http):\/\/(.)*/;
                    var test = regex.test($.trim(value)) || $.trim(value) == ''
                    if (! test) {
                        return "The URL must begin with \"http://\", \"https://\", or \"ftp://\"."
                    }
                }
                if (dVal.includes('email')) {
                    let regex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)+$/
                    let test = regex.test($.trim(value)) || $.trim(value) === ''
                    if(!test) {
                        return "Please enter a valid email address."
                    }
                }
                if (dVal.includes('maxlength')) {
                    if(value.length > $(this).attr("data-maxlength")) {
                        return "The value is too long.";
                    }
                }
            }
        },
        success: function(response, newValue) {
            if(response) {
                if (!response.success) return response.msg;
            }
        },
        error: function(response, newValue) {
            if(response.status === 500) {
                return "Service unavailable. Please try again later.";
            } else {
                return response.responseText;
            }
        }
    }).on('save', function(e, params){
        if ($(this).attr('data-format')) {
            console.log(params)
        }
    }).on('shown', function() {
        if ($(this).attr('data-format')) {
            $('.xEditable-datepicker').calendar({
                type: 'date',
                formatter: {
                    date: function (date, settings) {
                        if (!date) return '';
                        var day = ('0' + date.getDate()).slice(-2);
                        var month = ('0' + (date.getMonth() + 1)).slice(-2);
                        var year = date.getFullYear();
                        return year + '-' + month + '-' + day   ;
                    }
                }
            });
            $('.editable-clear-x').click(function() {
                $('.calendar').calendar('clear');
            });
        }else {
            var dType = $(this).attr('data-type')
            if (dType == "text" && $(this).attr('data-validation') && $(this).attr('data-validation').includes("maxlength")) {
                var maxLength = 255;
                $('input').keyup(function () {
                    if($(this).attr('type') == 'text') {
                        var textlen = maxLength - $(this).val().length;
                        $('#characters-count').text(textlen + '/' + maxLength);
                    }
                });
            }
        }

    });

    $('.xEditableManyToOne').editable({
        tpl: '<select class="ui search selection dropdown"></select>',
        success: function(response, newValue) {
            if(response) {
                if(!response.success) return response.msg; //msg will be shown in editable form
            }
        }
    }).on('shown', function(e, obj) {
        obj.input.$input.dropdown({clearable: true}) // reference to current dropdown
    });

    $('#spotlightSearch').search({
        error : {
            source      : 'Cannot perform search. No source specified, and Semantic API module not included.',
            noResults   : 'No results found for your search.',
            logging     : 'Error encountered during debug logging; operation aborted.',
            noTemplate  : 'No valid template name specified.',
            serverError : 'An issue occurred while querying the server.',
            maxResults  : 'The maxResults setting requires an array of results.',
            method      : 'The specified method is not defined.'
        },

        type: 'category',
        minCharacters: 3,
        maxResults: 10,
        preserveHTML : false,
        apiSettings: {

            url: spotlightSearchUrl + "/?q={query}",
            onResponse: function(elasticResponse) {
                var response = { results : {} };
                // translate Elasticsearch API response to work with semantic ui search
                $.each(elasticResponse.results, function(index, item) {
                    var
                        category   = item.category || 'Unknown',
                        maxResults = 10
                    ;
                    if(index >= maxResults) {
                        response.action = {
                            "url": spotlightSearchUrl.replace('spotlightSearch', 'index') + "/?q="+item.query,
                                "text": "View all "+item.hitsCount+" results"
                        }
                        return response;
                    }
                    // create new object category
                    if (response.results[category] === undefined) {
                        response.results[category] = {
                            name    : category,
                            results : []
                        };
                    }
                    // add result to category
                    response.results[category].results.push({
                        title       : item.title,
                        description : item.description,
                        url         : item.url
                    });
                });
                return response;
            },
            onError: function(errorMessage) {
                // invalid response
                console.log(errorMessage);
            }
        }
    });
    $('#spotlightSearch .prompt').removeAttr('autocomplete');
    $('.search.dropdown .search').removeAttr('autocomplete');


    $(".simpleReferenceDropdown").each(function() {
        var simpleReferenceDropdownURL = ajaxLookUp + "/?baseClass="+$(this).children('input')[0].getAttribute('data-domain')+"&filter1="+$(this).children('input')[0].getAttribute('data-filter1')+"&q={query}"

        $(this).dropdown({
            clearable: true,
            forceSelection: false,
            preserveHTML : false,
            error : {
                source      : 'Cannot perform search. No source specified, and Semantic API module not included.',
                noResults   : 'No results found for your search.',
                logging     : 'Error encountered during debug logging; operation aborted.',
                noTemplate  : 'No valid template name specified.',
                serverError : 'An issue occurred while querying the server.',
                maxResults  : 'The maxResults setting requires an array of results.',
                method      : 'The specified method is not defined.'
            },
            apiSettings: {
                // this url parses query server side and returns filtered results
                url: simpleReferenceDropdownURL,
                //url: simpleReferenceDropdownURL + "/?baseClass=wekb.RefdataValue&filter1=Component.Status&q={query}"
                cache: false
            },
            fields: {
                remoteValues: 'values', // grouping for api results
                values: 'values', // grouping for all dropdown values
                name: 'text',   // displayed dropdown text
                value: 'id'   //
            },
            onShow: function () {
                current = $(this).val();
                $(this).dropdown('set selected', current);
            }
        });
        $(this).find('.search').removeAttr('autocomplete');
    });

    $('.message .close')
        .on('click', function() {
            $(this)
                .closest('.message')
                .transition('fade')
            ;
        })
    ;

    // ----- pagination -----
    $('.wekb.popup').each(function() {
        $(this).popup()
    });

});

