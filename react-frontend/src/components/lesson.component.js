import React, {Component, Fragment} from "react";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";
import CourseManagementService from "../services/course-management.service"
import UtilService from "../services/util.service"
import Select from 'react-select'
import {ListGroup, ListGroupItem} from "reactstrap";
import {Link} from "react-router-dom";
import LessonManagementService from "../services/lesson-management.service"

const API_URL = "http://localhost:8081/polyglot/";


export default class Lesson extends Component {
    // send though props: lesson_id
    constructor(props) {
        super(props);
        this.state = {
            loading: true,
            lesson_file_url: undefined
            // course_title: "",
            // course_language: "",
            // course_teacher: "",
            // lesson_ids_to_title: {},
            // lessons: []
        };
    }

    componentDidMount() {
        LessonManagementService.getLessonsContent(this.props.match.params.lesson_id)
            .then(response => {
                this.setState({
                    lesson_file_url: URL.createObjectURL(response)
                })
            })
        this.setState({
            loading: false
        })
    }


    render() {

        return (
            <Fragment>
                <div className="text-center">
                    <img
                        src={require('../assets/lesson.png')}
                        alt="course"
                        className="img-card scale-down"
                    />

                    {this.state.loading && (
                        <Fragment>
                            <p>Loading...</p>
                        </Fragment>
                    )}
                    {!this.state.loading && (
                        <Fragment>
                            <h1>Lesson</h1>

                            <iframe src={this.state.lesson_file_url} width="100%" height="700"/>

                        </Fragment>
                    )}
                </div>
            </Fragment>
        );
    }
}