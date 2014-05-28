"use strict";

angular.module("myqapp").service("userService", ["trelloService", "$resource", function (trelloService, $resource) {

    var self = this;
    this._isEditor = false;
    this._isAdmin = false;

    this.query = function (callback) {
        if (!self.member) {
            self.member = trelloService.getTrelloMemberInformation(function(result){
                self._isEditor = self.hasAuthority(result, "ROLE_EDITOR");
                self._isAdmin = self.hasAuthority(result, "ROLE_ADMIN");

                if (callback) {
                    callback(result);
                }
            });
        } else {
            if (callback) {
                callback(self.member);
            }
        }
    };

    this.isEditor = function () {
        return self._isEditor;
    };

    this.isAdmin = function () {
        return self._isAdmin;
    };

    this.hasAuthority = function(user, authority) {
        if (user && user.authorities) {
            for (var i = 0; i < user.authorities.length; i++) {
                if (user.authorities[i] === authority) {
                    return true;
                }
            }
        }
        return false;
    };

    var adminResource = $resource("api/user/:userId", {}, {
        findAll: {method: "GET", isArray: true},
        query: {method: "GET", isArray: false},
        create: {method: "POST"},
        update: {method: "PUT"}
    });

    this.removeUser = function(email, successCallback, errorCallback) {
        adminResource.remove({userId: email}, successCallback, errorCallback);
    };

    this.findAllUser = function(successCallback, errorCallback) {
        return adminResource.findAll({}, successCallback, errorCallback);
    };

    this.updateUser = function(user, successCallback, errorCallback) {
        adminResource.update({userId: user.email}, user, successCallback, errorCallback);
    };

    this.createUser = function(user, successCallback, errorCallback) {
        adminResource.create(user, successCallback, errorCallback);
    }
}]);