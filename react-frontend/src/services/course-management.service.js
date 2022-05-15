import authHeader from "./auth-header";

const API_URL = "http://localhost:8081/polyglot/";

class CourseManagementService {
   saveNewSelfTaughtCourse(title, language, wordTarget) {

        let body = {"title": title, "minPointsPerWord": wordTarget, "language": language}

        return fetch(API_URL + "create_self_taught_course", {
            method: 'POST',
            headers: Object.assign({}, {
                Accept: 'application/json',
                'Content-Type': 'application/json',
                "charset": "UTF-8"
            }, authHeader()),
            body: JSON.stringify(body)
        })
    }

    getAllEnrolledCourses() {
        return fetch(API_URL + "get_all_enrolled_courses", {
            method: 'GET',
            headers: Object.assign({}, {
                Accept: 'application/json',
                'Content-Type': 'application/json',
                "charset": "UTF-8"
            }, authHeader())
        })
    }
}
export default new CourseManagementService();