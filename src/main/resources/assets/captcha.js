(function() {
	'use strict';
	var module = angular.module('captcha', []);

	module.controller('CaptchaController', ['$http', '$scope', function ($http, $scope) {
		$scope.successful = 0;
		$scope.errors = 0;
		$scope.captchaId = null;

		$scope.requestCaptcha = function () {
			$http.get('/service/captcha').success(function(data){
				$scope.captchaId = data;
			});
		};

		$scope.doGuess = function () {
			$http.post('/service/captcha/' + $scope.captchaId, { 'term' : $scope.guess }).success(function (data, status, headers, config) {
				$scope.successful += 1;
				$scope.requestCaptcha();
			}).error(function (data, status, headers, config) {
				$scope.errors += 1;
				$scope.requestCaptcha();
			});
			$scope.captchaId = null;
			$scope.guess = null;
		}

		$scope.requestCaptcha();
	}]);
}());