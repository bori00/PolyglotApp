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

export default class SupervisedLesson extends Component {
    // send though props: lesson_id
    constructor(props) {
        super(props);
        this.state = {
            loading: true,
            lesson_file_url: undefined,
            message: "",
            successful: false
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

                            <hr></hr>

                            <iframe src={this.state.lesson_file_url} width="100%" height="700"/>

                        </Fragment>
                    )}
                </div>
            </Fragment>
        );
    }
}

const required = value => {
    if (!value) {
        return (
            <div className="alert alert-danger" role="alert">
                This field is required!
            </div>
        );
    }
};