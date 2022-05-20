import React, {Component, Fragment} from "react";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";
import CourseManagementService from "../services/course-management.service"
import CourseStatisticsService from "../services/course-statistics.service"
import UtilService from "../services/util.service"
import Select from 'react-select'
import {Table} from "reactstrap";
import {Link} from "react-router-dom";


export default class SupervisedCourseStatistics extends Component {
    // send though props: course_id
    constructor(props) {
        super(props);
        this.state = {
            loading: true,
            course_title: "",
            course_language: "",
            course_teacher: "",
            course_joining_code: "",
            course_nr_of_students: undefined,
            course_lesson_titles: [],
            course_avg_nr_of_unknown_words: [],
            lesson_ids_to_title: {},
        };
    }

    componentDidMount() {
        CourseManagementService.getTaughtCourse(this.props.match.params.course_id)
            .then(response => {
                if (response.ok) {
                    response.json().then(response => {
                        this.setState({
                            course_title: response.title,
                            course_language: response.language,
                            course_joining_code: response.joiningCode,
                            course_nr_of_students: response.nrOfStudents,
                        });
                    })
                } else {
                    console.log("Error loading course data")
                }
            }).then(
                CourseStatisticsService.getCourseStatistics(this.props.match.params.course_id)
                    .then(response => {
                        if (response.ok) {
                            response.json().then(response => {
                                this.setState({
                                    course_lesson_titles: response.lessonTitles,
                                    course_avg_nr_of_unknown_words: response.avgNrOfUnknownWordsPerLesson,
                                    loading: false
                                });
                            })
                        } else {
                            console.log("Error loading course data")
                        }
                    }))
    }

    render() {

        let lessonListGroupItems = [];

        const lesson_titles = this.state.course_lesson_titles;
        const lesson_values = this.state.course_avg_nr_of_unknown_words;

        lesson_titles.forEach((title, index) => {
            const value = lesson_values[index];
            lessonListGroupItems.push(
                <tr>
                    <td>{index+1}</td>
                    <td>{title}</td>
                    <td>{value}</td>
                </tr>
            )
        });

        const course_link = "/supervised_course/" + this.props.match.params.course_id;

        return (
            <div className="col-md-12">
                <div className="card card-container">
                    <img
                        src={require('../assets/course.png')}
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
                            <h1>{this.state.course_title}</h1>
                            <h4>Joining Code: <b>{this.state.course_joining_code}</b></h4>
                            <h6>{this.state.course_language}</h6>

                            <div className="text-center">
                                <Link to={course_link}>
                                    <button type="button" className="btn btn btn-outline-secondary">
                                        Back to the Course
                                    </button>
                                </Link>
                            </div>

                            <hr/>

                            <Table striped bordered hover>
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Lesson</th>
                                        <th>Avg. number of unknown words</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {lessonListGroupItems}
                                </tbody>
                            </Table>
                        </Fragment>
                    )}
                </div>
            </div>
        );
    }
}