$(document).ready(function() {
    console.log('hello')
    if ($('.toast').text() !== "") {
        Materialize.toast($('.toast').text(), 5000);
    }
})