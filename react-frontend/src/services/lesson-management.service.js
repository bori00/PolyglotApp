import authHeader from "./auth-header";
import axios from "axios";

const API_URL = "http://localhost:8081/polyglot/";

class LessonManagementService {

    getLessonsContent(lesson_id) {
        var url = new URL(API_URL + "get_lesson_file")

        var params = {"lessonId": lesson_id}
        params = new URLSearchParams(params);
        url.search = new URLSearchParams(params).toString();

        return fetch(url, {
            method: 'GET',
            headers: Object.assign({}, {
                'Content-Type': 'application/json',
                "charset": "UTF-8"
            }, authHeader())
        }).then(res => res.blob())
    }

}
export default new LessonManagementService();