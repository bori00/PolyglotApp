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
import AuthService from "../services/auth.service";

export default class SupervisedLesson extends Component {
    // send though props: lesson_id
    constructor(props) {
        super(props);
        this.state = {
            loading: true,
            lesson_file_url: undefined,
            lesson_title: undefined,
            course_title: undefined,
            course_id: undefined,
            index_inside_course: undefined,
            message: "",
            successful: false
        };
    }

    componentDidMount() {
        AuthService.guaranteeUserHasRole("TEACHER", this);

        LessonManagementService.getLessonsContent(this.props.match.params.lesson_id)
            .then(response => {
                this.setState({
                    lesson_file_url: URL.createObjectURL(response)
                })
            }).then(() => {
            LessonManagementService.getLessonsData(this.props.match.params.lesson_id)
                .then(response => {
                    response.json().then(response => {
                        this.setState({
                            lesson_title: response.title,
                            course_title: response.courseTitle,
                            course_id: response.courseId,
                            index_inside_course: response.indexInsideCourse,
                            loading: false
                        })
                    })
                })
            })
    }

    render() {

        const supervised_lesson_statistics_link = "/supervised_lesson_statistics/" + this.props.match.params.lesson_id;

        const course_link = "/supervised_course/" + this.state.course_id;

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
                            <h1>{this.state.lesson_title}</h1>
                            <h5>Lesson nr. {this.state.index_inside_course} of course <i>"{this.state.course_title}"</i></h5>

                            <div className="text-center">
                                <Link to={course_link}>
                                    <button type="button" className="btn btn btn-outline-secondary">
                                        Back to the Course
                                    </button>
                                </Link>
                            </div>

                            <div className="text-center">
                                <Link to={supervised_lesson_statistics_link}>
                                    <button type="button" className="btn btn-outline-secondary">
                                        Lesson Statistics
                                    </button>
                                </Link>
                            </div>

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