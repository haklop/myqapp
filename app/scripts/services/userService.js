angularModule.service("userService", ["trelloService", function (trelloService) {

    var self = this;
    this._isEditor = false;
    this._isAdmin = false;

    this.query = function () {
        if (!self.member) {
            self.member = trelloService.getTrelloMemberInformation(function(result){
                self._isEditor = self.hasAuthority(result, "ROLE_EDITOR");
                self._isAdmin = self.hasAuthority(result, "ROLE_ADMIN");
            });
        }
        return self.member;
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
}]);