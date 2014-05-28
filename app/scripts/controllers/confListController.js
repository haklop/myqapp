"use strict";

angular.module("myqapp").controller("ConfListCtrl", ["$scope", "$rootScope", "Confs", "userService", function ($scope, $rootScope, Confs, userService) {
    $scope.newconf = {};

    $scope.isEditor = userService.isEditor;

    $scope.createConf = function () {
        if ($scope.newconfForm.$valid) {
            var confToSubmit = {
                startDate: startPicker.getDate().getTime(),
                endDate: endPicker.getDate().getTime(),
                name: $scope.newconf.name,
                location: $scope.newconf.location,
                website: $scope.newconf.website
            };
            Confs.save(confToSubmit, function () {
                $rootScope.$broadcast("handleAlert", {"title": "Conference ajoutée", "type": "success", "content": "",
                    category: "message"});
                $scope.newconf = {};
                $("#conf-calendar").fullCalendar("refetchEvents");

            }, function () {
                $rootScope.$broadcast("handleAlert", {"title": "Erreur lors de la création de la conference", "type": "error", "content": "",
                    category: "message"});
            });
        }
    };

    $scope.nextMonth = function() {
        $("#conf-calendar").fullCalendar("next");
    };

    $scope.previousMonth = function() {
        $("#conf-calendar").fullCalendar("prev");
    };

    $scope.today = function() {
        $("#conf-calendar").fullCalendar("today");
    };

    $("#conf-calendar").fullCalendar({
        header: {
            left: "",
            center: "title",
            right: ""
        },
        firstDay: 1,
        weekMode: "liquid",
        editable: false,

        events: function (start, end, callback) {
            $.ajax({
                url: "/api/conf/period",
                dataType: "json",
                data: {
                    start: start.getTime(),
                    end: end.getTime()
                },
                success: function (doc) {
                    var i;
                    var events = [];
                    for (i = 0; i < doc.length; i++) {
                        events.push({
                            title: doc[i].name,
                            start: doc[i].startDate / 1000,
                            end: doc[i].endDate / 1000,
                            allDay: true,
                            url: doc[i].website
                        });
                    }
                    callback(events);
                }
            });
        }
    });

    var startPicker = new Pikaday({
        field: $("#inputStartDate")[0],
        firstDay: 1,
        format: "dd/mm/yy",
        onClose: function() {
            endPicker.minDate = startPicker.getDate();
            $scope.newconf.startDate = startPicker.toString();
            $scope.$apply();
            endPicker.show();
        }
    });

    var endPicker = new Pikaday({
        field: $("#inputEndDate")[0],
        firstDay: 1,
        format: "dd/mm/yy",
        onClose: function() {
            startPicker.maxDate = endPicker.getDate();
            $scope.newconf.endDate = endPicker.toString();
            $scope.$apply();
        }
    });

}]);