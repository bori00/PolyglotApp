import authHeader from "./auth-header";

const API_URL = "http://localhost:8081/polyglot/";

class UtilService {

    getAllLanguages() {
        return fetch(API_URL + "util/get_all_languages", {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                "charset": "UTF-8"
            }
        })
    }

}
export default new UtilService();