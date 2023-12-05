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

console.log('+ wekb.js')


$(function () {

    verticalNavi.go();

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
    $.fn.editableform.buttons = '<button type="submit" class="ui icon black button editable-submit"><i aria-hidden="true" class="check icon"></i></button>' +
        '<button type="button" class="ui icon black button editable-cancel"><i aria-hidden="true" class="times icon"></i></button>';
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
        '        <div id="characters-count"></div>' +
        '		<div class="editable-error-block">' +
        '		</div>' +
        '	</div>' +
        '</form>';
    $.fn.editableform.loading =
        '<div class="ui active inline loader"></div>';
    $('.xEditableValue').editable({
        validate: function(value) {
            if ($(this).attr('data-format') && value) {
                if(! (value.match(/^\d{4}-\d{1,2}-\d{1,2}$/)) ) {
                    return "Wrong format";
                }
            }

            if ($(this).attr('data-required')) {
                if($.trim(value) == '') {
                    return 'This field is required';
                }
            }
            // custom validate functions via semui:xEditable validation="xy"
            var dVal = $(this).attr('data-validation')
            if (dVal) {
                if (dVal.includes('notEmpty')) {
                    if($.trim(value) == '') {
                        return "This field is not allowed to be empty";
                    }
                }
                if (dVal.includes('url')) {
                    var regex = /^(https?|ftp):\/\/(.)*/;
                    var test = regex.test($.trim(value)) || $.trim(value) == ''
                    if (! test) {
                        return "The url must beginn with 'http://' or 'https://' or 'ftp://'."
                    }
                }
                if (dVal.includes('email')) {
                    let regex = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)+$/
                    let test = regex.test($.trim(value)) || $.trim(value) === ''
                    if(!test) {
                        return "Please check your mail-addres!"
                    }
                }
                if (dVal.includes('maxlength')) {
                    if(value.length > $(this).attr("data-maxlength")) {
                        return "The value is to long!";
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
                return 'Service unavailable. Please try later.';
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
            source      : 'Cannot search. No source used, and Semantic API module was not included',
            noResults   : 'Your search returned no results',
            logging     : 'Error in debug logging, exiting.',
            noTemplate  : 'A valid template name was not specified.',
            serverError : 'There was an issue with querying the server.',
            maxResults  : 'Results must be an array to use maxResults setting',
            method      : 'The method you called is not defined.'
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

    $(".simpleReferenceDropdown").each(function() {
        var simpleReferenceDropdownURL = ajaxLookUp + "/?baseClass="+$(this).children('input')[0].getAttribute('data-domain')+"&filter1="+$(this).children('input')[0].getAttribute('data-filter1')+"&q={query}"

        $(this).dropdown({
            clearable: true,
            forceSelection: false,
            preserveHTML : false,
            error : {
                source      : 'Cannot search. No source used, and Semantic API module was not included',
                noResults   : 'Your search returned no results',
                logging     : 'Error in debug logging, exiting.',
                noTemplate  : 'A valid template name was not specified.',
                serverError : 'There was an issue with querying the server.',
                maxResults  : 'Results must be an array to use maxResults setting',
                method      : 'The method you called is not defined.'
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

    $('nav.pagination').each (function () {
        const $pagination = $(this)
        console.log("Moe")
        const $input      = $pagination.find('.wekb-pagination-custom-input');
        const $inputInput = $pagination.find('.wekb-pagination-custom-input input');
        const $inputForm  = $pagination.find('.wekb-pagination-custom-input .ui.form');
        const $link       = $pagination.find('.wekb-pagination-custom-link');

        let stepsValue = $input.attr('data-steps');
        let baseHref   = $link.attr('href');

        $.fn.form.settings.rules.smallerEqualThanTotal = function (inputValue, validationValue) {
            return parseInt(inputValue) <= validationValue;
        };
        $.fn.form.settings.rules.biggerThan = function (inputValue, validationValue) {
            return parseInt(inputValue) > validationValue;
        };

        $inputInput.on ('input', function() {
                let newOffset = ($(this).val() - 1) * $input.attr('data-max');
                $link.attr('href', baseHref + '&offset=' + newOffset);
            })
            .bind ('keypress', function(event) {
                if (event.keyCode === 13){
                    if ( validateInput() ) {
                        $('html').css('cursor', 'wait');
                        location.href = $link.attr('href');
                    }
                    else { event.preventDefault(); }
                }
            });

        $link.on ('click', function(event) {
            if ( validateInput() ) {
                $('html').css('cursor', 'wait');
            }
            else { event.preventDefault(); }
        });

        let validateInput = function() {
            console.log("Nie")
            $inputForm.form({
                inline: true,
                fields: {
                    paginationCustomValidation: {
                        identifier: $inputInput.attr('data-validate'),
                        rules: [
                            {
                                type: "empty", prompt: 'Please enter a page number!'
                            },
                            {
                                type: "integer", prompt: 'Please enter a page number!'
                            },
                            {
                                type: "smallerEqualThanTotal[" + stepsValue + "]", prompt: 'Please enter a smaller page number!'
                            },
                            {
                                type: "biggerThan[0]", prompt: 'Please enter a smaller page number!'
                            }
                        ]
                    }
                },
                onInvalid: function() { return false; },
                onValid: function()   { return true; }
            });

            return $inputForm.form('validate form');
        }
    });

});

