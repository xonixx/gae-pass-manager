angular.module('poc-files', [])
    .config(['$compileProvider', function ($compileProvider) {
        $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|data):/);
    }])
    .controller('Ctrl', ['$scope', function Ctrl($scope) {
        var PASS = 'secret123';

        $scope.enc = function () {
            var files = $('#files')[0].files;
            console.info(111,files)

            if (files.length) {
                var file = files[0];
                $scope.fileName = file.name;

                var reader = new FileReader();
                reader.onload = function (e) {
                    var srcData = e.target.result;
                    // console.info(222,srcData);
                    $scope.srcData = srcData;
                    $scope.srcDataEnc = encrypt(PASS,srcData);
                    $scope.$apply();
                };
                reader.readAsDataURL(file);
            }
        };

        $scope.dec = function () {
            $scope.fileDecDataUrl = decrypt(PASS, $scope.srcDataEnc);
        }
    }]);

function encrypt(password, text) {
    // aes-256
    return sjcl.encrypt(password, text, {ks: 256})
}

function decrypt(password, encryptedText) {
    try {
        return sjcl.decrypt(password, encryptedText);
    } catch (e) {
        return null;
    }
}