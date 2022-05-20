import authHeader from "./auth-header";
import axios from 'axios';

const API_URL = "http://localhost:8081/polyglot/";

class CourseStatisticsService {

    getCourseStatistics(course_id) {
        var url = new URL(API_URL + "get_course_statistics")

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
}
export default new CourseStatisticsService();