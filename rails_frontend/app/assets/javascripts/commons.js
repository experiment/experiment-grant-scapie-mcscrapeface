$(document).ready(function() {
    console.log('hello')
    if ($('.toast').text() !== "") {
        Materialize.toast($('.toast').text(), 5000);
    }

    $('.dropdown-button').on('click', function() {
        $(".dropdown-button").dropdown();
    })
})