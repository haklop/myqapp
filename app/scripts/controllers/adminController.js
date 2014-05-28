"use strict";

angular.module("myqapp").controller("AdminController", ["$scope", "userService", function ($scope, userService) {

    $scope.users = userService.findAllUser();

    $scope.remove = function (user) {
        var answer = confirm("Voulez-vous vraiment supprimer l'utilisateur " + user.email);
        if (answer) {
            userService.removeUser(user.email, function() {
                $scope.refresh();
            });
        }
    };

    $scope.isRefreshing = false;

    $scope.refresh = function () {
        $scope.isRefreshing = true;
        $scope.users = userService.findAllUser(function () {
            $scope.isRefreshing = false;
        });
    };

    $scope.roles = [
        {
            name: "ROLE_EDITOR",
            checked: false
        },
        {
            name: "ROLE_ADMIN",
            checked: false
        }
    ];

    $scope.newUser = {
        roles: $scope.roles.slice(0),
        created: false
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
                authorities.push(user.roles[i].name);
            }
        }

        user.authorities = authorities;
        userService.updateUser(user, function () {
            user.edited = false;
            $scope.refresh();
        });
    };

    $scope.saveOldMail = function(user){
        if(!user.oldMail) {
            user.oldMail=user.email;
        }
    };

    $scope.createUser = function (user) {
        var authorities = [];
        for (var i = 0; i < user.roles.length; i++) {
            if (user.roles[i].checked) {
                authorities.push(user.roles[i].name);
            }
        }

        user.authorities = authorities;
        userService.createUser(user, function () {
            initNewUser();
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
        return userService.hasAuthority(user, role);
    };

    function initNewUser() {
        $scope.newUser = {
            roles: $scope.roles.slice(0),
            created: false
        };
    }
}]);

