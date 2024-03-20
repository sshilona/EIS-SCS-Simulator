$(document).ready(function () {
    var fGetDefaultPress = false;
    $('#jsonInput').on('input', function () {
        fGetDefaultPress = true
    });
    var connectionDetails = getFromCookies();
    var defaults = {
        "createClient": { "ip": connectionDetails.ip, "port": connectionDetails.port },
        "channelSetup": { "channelId": 111 },
        "channelClose": { "channelId": 111 },
        "channelTest": { "channelId": 111 },
        "channelReset": { "channelId": 111 },
        "scgProvision": {
            "channelId": 111,
            "scgId": 2,
            "transportStreamId": 10,
            "originalNetworkId": 1,
            "scgReferenceId": 1463935,
            "recommendedCpDuration": 0,
            "serviceId": 1,
            "activationTime": {
                "yearMSB": 0, // Most Significant Byte of the year
                "yearLSB": 24, // Least Significant Byte of the year (for 2024, for example)
                "month": 4, // Month (April)
                "day": 5, // Day of the month
                "hour": 14, // Hour of the day
                "minute": 30, // Minute of the hour
                "second": 0, // Second of the minute
                "hundredthOfSecond": 0 // Hundredth of a second
            },
            "ecmGroups": [
                { "ecmId": 1001, "superCasId": "10000", "accessCriteria": "AABBCCDDEEFF", "acChangeFlag": true },
                { "ecmId": 1002, "superCasId": "10000", "accessCriteria": "112233445566", "acChangeFlag": false }
            ]
        },
        "scgDeProvision": {
            "channelId": 111,
            "scgId": 2,
            "transportStreamId": 10,
            "originalNetworkId": 1,
            "scgReferenceId": 1463935,
            "recommendedCpDuration": 0,
            "serviceId": 1,
            "ecmGroups": []
        },
        "scgListRequest": {
            "channelId": 111
        },
        "scgTest": {
            "channelId": 111,
            "scgId": 2
        }
    };

    function setDefault(jsonKey) {
        $('#jsonInput').val(JSON.stringify(defaults[jsonKey], null, 2));
    }

    $('.default-btn').click(function () {
        var operation = this.id.replace('getDefault', '');
        setDefault(operation);
        fGetDefaultPress = true;
    });

    function saveToCookies() {
        var jsonData = $('#jsonInput').val();
        try {
            var obj = JSON.parse(jsonData);
            if (obj.ip && obj.port) {
                // Save to cookies for 7 days
                document.cookie = "ip=" + obj.ip + "; max-age=" + (7 * 24 * 60 * 60);
                document.cookie = "port=" + obj.port + "; max-age=" + (7 * 24 * 60 * 60);
            }
        } catch (e) {
            console.error("Error parsing JSON: ", e);
        }
    }

    function getCookie(name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
        }
        return null;
    }

    function getFromCookies() {
        var ip = getCookie('ip');
        var port = getCookie('port');

        if (!ip) {
            ip = "10.42.21.223"; // Default IP
        }
        if (!port) {
            port = 11000; // Default port
        }
        return { ip: ip, port: port };
    } 

    function sendRequest(id, endpoint, requestBody = $('#jsonInput').val()) {
        saveToCookies();
        if (!fGetDefaultPress) {
            $("#getDefault" + id).click();
            requestBody = $('#jsonInput').val()
        }
        fGetDefaultPress = false;
        $.ajax({
            url: endpoint,
            type: 'POST',
            contentType: 'application/json',
            data: requestBody,
            success: function (response) {
                var formattedResponse = response.replace(/\n/g, "<br>");
                let parts = formattedResponse.split("---");
                $('#responseArea2').html("");
                if (parts.length >= 2) {
                    $('#responseArea').html(parts[0]);
                    $('#responseArea2').html(parts[1]);
                }
                else {
                    $('#responseArea').html(formattedResponse);
                }
            },
            error: function (xhr, status, error) {
                $('#responseArea').html("Error: " + status + " " + error);
            }
        });
    }
    $('#createClient').click(function () { sendRequest(this.id, '/eis/create-client'); });
    $('#channelSetup').click(function () { sendRequest(this.id, '/eis/channel-setup'); });
    $('#channelClose').click(function () { sendRequest(this.id, '/eis/channel-close'); });
    $('#channelTest').click(function () { sendRequest(this.id, '/eis/channel-test'); });
    $('#channelReset').click(function () { sendRequest(this.id, '/eis/channel-reset'); });
    $('#runScript').click(function () { sendRequest(this.id, '/eis/run-script', '{}'); }); // Assuming runScript doesn't take a body
    $('#scgProvision').click(function () { sendRequest(this.id, '/eis/scg-provision'); });
    $('#scgDeProvision').click(function () { sendRequest(this.id, '/eis/scg-provision'); }); // Corrected the endpoint
    $('#scgListRequest').click(function () { sendRequest(this.id, '/eis/scg-list'); });
    $('#scgTest').click(function () { sendRequest(this.id, '/eis/scg-test'); });

});
