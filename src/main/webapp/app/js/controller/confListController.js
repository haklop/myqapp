function ConfListCtrl($scope, Confs, UserService) {
    $scope.alerts = [];

    $scope.newconf = {};

    $scope.confs = Confs.query();

    $scope.isEditor = UserService.isEditor;

    $scope.createConf = function () {
        if ($scope.newconfForm.$valid) {
            Confs.save($scope.newconf, function () {
                $scope.alerts.push({"title": "Conference ajoutée", "type": "success", "content": ""});
                $scope.confs = Confs.query();
                $scope.newconf = {};

            }, function () {
                $scope.alerts.push({"title": "Erreur lors de la création de la conference", "type": "error", "content": ""});
            });
        }
    };

    var date = new Date();
    var d = date.getDate();
    var m = date.getMonth();
    var y = date.getFullYear();

    $('#conf-calendar').fullCalendar({
        header: {
            left: 'prev,next today',
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
                    // our hypothetical feed requires UNIX timestamps
                    start: start.getTime(),
                    end: end.getTime()
                },
                success: function (doc) {
                    console.log(doc);
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