$(document).ready(function() {
    $(document).on('click', '.number-spinner button', function () {
        var btn = $(this),
        input = btn.closest('.number-spinner').find('input'),
        value = Number(input.attr('value').trim());

        if (btn.attr('data-action') === 'plus') {
            if (value < Number(input.attr('max'))) {
                value++;
            }
            enableButton(findBtnMinus(input));
        } 
        if (btn.attr('data-action') === 'minus') {
            if (value > Number(input.attr('min'))) {
                value--;
            }
            enableButton(findBtnPlus(input));
        }
        input.attr('value', value);

        enableDisableBtnOnBoundaryValues(value, input);

        updateDatabase($(this).attr('href'), value);
    });

    function findBtnMinus(input) {
        return input.parent().parent().find('.btn-minus');
    }

    function findBtnPlus(input) {
        return input.parent().parent().find('.btn-plus');
    }

    function disableButton(btn) {
        btn.attr('disabled', true);
    }

    function enableButton(btn) {
        btn.removeAttr('disabled');
    }

    function enableDisableBtnOnBoundaryValues(value, input) {
        if (value >= input.attr('max')) {
            disableButton(findBtnPlus(input));
        }
        if (value <= input.attr('min')) {
            disableButton(findBtnMinus(input));
        }
    }

    function updateDatabase(href, value) {
        $.ajax({
            type: "PATCH",
            url: href.replace("{value}", value),
            success: function(result) {
                // TODO this is bad practice since AJAX is used!
                window.location.reload();
            },
			error: function(err) {
				console.log("could not update device information value: " + JSON.stringify(err.responseJSON));
				alert("Something went wrong!\nCheck logs for more information.");
            }
		});
    }
});