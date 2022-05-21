import authHeader from "./auth-header";
import axios from "axios";
import download from "downloadjs"

const API_URL = "http://localhost:8081/polyglot/";

class LessonPracticeService {

    getWordQuestion(lesson_id) {
        var url = new URL(API_URL + "get_word_question")

        var params = {"lessonId": lesson_id}
        params = new URLSearchParams(params);
        url.search = new URLSearchParams(params).toString();

        return fetch(url, {
            method: 'GET',
            headers: Object.assign({}, {
                'Content-Type': 'application/json',
                "charset": "UTF-8"
            }, authHeader())
        })
    }

    answerWordQuestion(word_to_learn_id, submitted_translation, foreign_to_native) {
        let body = {"wordToLearnId": word_to_learn_id, "submittedTranslation": submitted_translation, "foreignToNative": foreign_to_native}

        return fetch(API_URL + "answer_word_question", {
            method: 'POST',
            headers: Object.assign({}, {
                Accept: 'application/json',
                'Content-Type': 'application/json',
                "charset": "UTF-8"
            }, authHeader()),
            body: JSON.stringify(body)
        })
    }

    getLessonVocabularyPDF(lesson_id, lesson_title) {
        var url = new URL(API_URL + "get_lesson_vocabulary_in_pdf")

        var params = {"lessonId": lesson_id}

        url.search = new URLSearchParams(params).toString();

        return fetch(url, {
            method: 'GET',
            headers: Object.assign({}, {
                'Content-Type': 'application/json',
                "charset": "UTF-8"
            }, authHeader())
        }).then(res => res.blob())
            .then( blob => {
                download(blob,  lesson_title + "_Vocabulary.pdf");
            });
    }

}
export default new LessonPracticeService();