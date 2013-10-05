function ConfListCtrl($scope, $rootScope, Confs, UserService) {
    $scope.newconf = {};

    $scope.isEditor = UserService.isEditor;

    $scope.createConf = function () {
        if ($scope.newconfForm.$valid) {
            Confs.save($scope.newconf, function () {
                $rootScope.$broadcast('handleAlert', {"title": "Conference ajoutée", "type": "success", "content": "",
                    category: 'message'});
                $scope.newconf = {};
                $('#conf-calendar').fullCalendar('refetchEvents');

            }, function () {
                $rootScope.$broadcast('handleAlert', {"title": "Erreur lors de la création de la conference", "type": "error", "content": "",
                    category: 'message'});
            });
        }
    };

    $scope.nextMonth = function() {
        $('#conf-calendar').fullCalendar('next');
    };

    $scope.previousMonth = function() {
        $('#conf-calendar').fullCalendar('prev');
    };

    $scope.today = function() {
        $('#conf-calendar').fullCalendar('today');
    };

    var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();

    $('#conf-calendar').fullCalendar({
        header: {
            left: '',
            center: 'title',
            right: ''
        },
        firstDay: 1,
        weekMode: 'liquid',
        editable: false,

        events: function (start, end, callback) {
            $.ajax({
                url: '/api/conf/period',
                dataType: 'json',
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
            })
        }
    });

}