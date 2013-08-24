function AdminCtrl($scope, User, UserService) {
    $scope.users = User.findAll();

    $scope.remove = function (user) {
        var answer = confirm("Voulez-vous vraiment supprimer l'utilisateur " + user.email);
        if (answer) {
            User.remove({userId: user.email});
            $scope.refresh();
        }
    };

    $scope.refresh = function () {
        $scope.users = User.findAll();
    };

    $scope.formatRoles = function (user) {
        var s = "";
        var separator = "";
        if (user.authorities) {
            for (var i = 0; i < user.authorities.length; i++) {
                s += separator;
                s += user.authorities[i];
                separator = ", ";
            }
        }
        return s;
    };

    $scope.updateUser = function (user) {
        var authorities = [];
        for (var i = 0; i < user.roles.length; i++) {
            if (user.roles[i].checked) {
                authorities.push(user.roles[i].name)
            }
        }

        user.authorities = authorities;
        User.update({userId: user.email}, user, function () {
            user.edited = false;
            $scope.refresh();
        });
    };

    $scope.getRoles = function(user) {
        var roles = $scope.roles.slice(0);
        for (var i = 0; i < roles.length; i++) {
            roles[i].checked = $scope.hasAuthority(user, roles[i].name);
        }

        return roles;
    };

    $scope.hasAuthority = function(user, role) {
        return UserService.hasAuthority(user, role);
    };

    $scope.roles = [
        {
            name: 'ROLE_EDITOR',
            checked: false
        },
        {
            name: 'ROLE_ADMIN',
            checked: false
        }
    ];
}