angular.module("myqapi").service("UserService", ["TrelloMember", function (TrelloMember) {

    this._isEditor = true;
    this._isEditor = true;

    this.query = function () {
        if (!this.member) {
            this.member = TrelloMember.query(function(result){
                this._isEditor = this.hasAuthority(result, "ROLE_EDITOR");
                this._isAdmin = this.hasAuthority(result, "ROLE_ADMIN");
            }.bind(this));
        }
        return this.member;
    };

    this.isEditor = function () {
        return this._isEditor;
    };

    this.isAdmin = function () {
        return this._isAdmin;
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
