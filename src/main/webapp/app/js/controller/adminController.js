function AdminCtrl($scope, User) {
    $scope.users = User.findAll();
}