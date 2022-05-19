import authHeader from "./auth-header";
import axios from 'axios';

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

    saveNewSupervisedCourse(title, language, wordTarget) {

        let body = {"title": title, "minPointsPerWord": wordTarget, "language": language}

        return fetch(API_URL + "create_supervised_course", {
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

    getAllSupervisedCourses() {
        return fetch(API_URL + "get_all_taught_courses", {
            method: 'GET',
            headers: Object.assign({}, {
                Accept: 'application/json',
                'Content-Type': 'application/json',
                "charset": "UTF-8"
            }, authHeader())
        })
    }

    getEnrolledCourse(course_id) {
        var url = new URL(API_URL + "get_enrolled_course_data")

        var params = {"courseId": course_id}
        params = new URLSearchParams(params);
        url.search = new URLSearchParams(params).toString();

        return fetch(url, {
            method: 'GET',
            headers: Object.assign({}, {
                Accept: 'application/json',
                'Content-Type': 'application/json',
                "charset": "UTF-8"
            }, authHeader())
        })
    }

    getTaughtCourse(course_id) {
        var url = new URL(API_URL + "get_taught_course_data")

        var params = {"courseId": course_id}
        params = new URLSearchParams(params);
        url.search = new URLSearchParams(params).toString();

        return fetch(url, {
            method: 'GET',
            headers: Object.assign({}, {
                Accept: 'application/json',
                'Content-Type': 'application/json',
                "charset": "UTF-8"
            }, authHeader())
        })
    }

    saveNewSelfTaughtLesson(title, file, courseId, onUploadProgress) {
        let formData = new FormData();
        formData.append("file", file);
        formData.append("title", title);
        formData.append("courseId", courseId);
        return axios.post(API_URL + "add_new_self_taught_lesson", formData, {
            headers: Object.assign({}, {
                "Content-Type": "multipart/form-data",
            }, authHeader()),
            onUploadProgress,
        });
    }

    saveNewSupervisedLesson(title, file, courseId, onUploadProgress) {
        let formData = new FormData();
        formData.append("file", file);
        formData.append("title", title);
        formData.append("courseId", courseId);
        return axios.post(API_URL + "add_new_supervised_lesson", formData, {
            headers: Object.assign({}, {
                "Content-Type": "multipart/form-data",
            }, authHeader()),
            onUploadProgress,
        });
    }

    saveUnknownWord(lesson_id, word) {
        let body = {"lessonId": lesson_id, "word": word}

        return fetch(API_URL + "add_unknown_word", {
            method: 'POST',
            headers: Object.assign({}, {
                Accept: 'application/json',
                'Content-Type': 'application/json',
                "charset": "UTF-8"
            }, authHeader()),
            body: JSON.stringify(body)
        })
    }
}
export default new CourseManagementService();