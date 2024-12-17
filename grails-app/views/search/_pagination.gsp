<g:if test="${recset != null}">
<g:set var="s_action" value="${s_action?:actionName}"/>
<g:set var="s_controller" value="${s_controller?:controllerName}"/>

  %{--<semui:paginate controller="${s_controller}" action="${s_action}" id="${params.id}" params="${params}"
                  max="${max}" total="${reccount}"/>--}%

  <semui:paginateNew controller="${s_controller}" action="${s_action}" id="${params.id}" params="${params}"
                  max="${max}" total="${reccount}"/>

<g:javascript>
  $('nav.pagination').each (function () {
        const $pagination = $(this)

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
                $link.attr('href', baseHref + '&jumpOffset=' + newOffset);
            }).bind ('keypress', function(event) {
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
</g:javascript>
</g:if>
