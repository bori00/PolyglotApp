const API_URL = "http://localhost:8081/polyglot/";

class AuthService {
    login(username, password) {

        let body = {"name": username, "password": password}

        return fetch(API_URL + "login", {
                method: 'POST',
                headers: {
                    Accept: 'application/json',
                    'Content-Type': 'application/json',
                    "charset": "UTF-8"
                },
                body: JSON.stringify(body)
            }).then(response => {
                if (response.ok) {
                    return response.json().then(responseJson => {
                        localStorage.setItem("user", JSON.stringify(responseJson));
                        return response;
                    })
                } else {
                    return response;
                }
            })
    }

    logout() {
        localStorage.removeItem("user");
    }

    async register(username, password, emailAddress, isTeacher, nativeLanguage) {

        const body = {"name": username,
            "password": password,
            "emailAddress": emailAddress,
            "userType": isTeacher ? "TEACHER" : "STUDENT",
            "nativeLanguage": nativeLanguage};

        return fetch(API_URL + "register", {
            method: 'POST',
            headers: {
                Accept: 'application/json',
                'Content-Type': 'application/json',
                "charset": "UTF-8"
            },
            body: JSON.stringify(body)
        })
    }

    getCurrentUser() {
        return JSON.parse(localStorage.getItem('user'));
    }

    guaranteeUserHasRole(role, component) {
        const user = this.getCurrentUser();
        if (user.role !== role) {
            this.logout();
            component.props.history.push("/login");
            window.location.reload();
            window.alert("Please sign in with a " + role + " account to access this" +
                " functionality.");
        }
    }
}
export default new AuthService();