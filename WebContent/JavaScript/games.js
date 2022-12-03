/**
 *
 */

$(document).ready(function() {
    loadMain();
});

function loadMain() {
    $("#startQuasar").click(function() {
        location.replace("/WEBApp/home/games/quasar.html")
    });

    $("#startPazaak").click(function() {
        location.replace("/WEBApp/home/games/pazaak.html")
    });

}