angular.module("myqapi").service("UserService", ["TrelloMember", function (TrelloMember) {

    var self = this;
    this._isEditor = true;
    this._isEditor = true;

    this.query = function () {
        if (!this.member) {
            this.member = TrelloMember.query(function(result){
                self._isEditor = self.hasAuthority(result, "ROLE_EDITOR");
                self._isAdmin = self.hasAuthority(result, "ROLE_ADMIN");
            });
        }
        return this.member;
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
