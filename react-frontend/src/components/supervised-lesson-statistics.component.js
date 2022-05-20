import React, {Component, Fragment} from "react";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";
import CourseManagementService from "../services/course-management.service"
import CourseStatisticsService from "../services/course-statistics.service"
import UtilService from "../services/util.service"
import Select from 'react-select'
import {ListGroupItem, Table} from "reactstrap";
import {Link} from "react-router-dom";


export default class SupervisedLessonStatistics extends Component {
    // send though props: lesson_id
    constructor(props) {
        super(props);
        this.state = {
            loading: true,
            successful: true,
            message: "",
            lesson_title: "",
            lesson_unknown_words_to_frequency: {},
            lesson_index_inside_course: undefined
        };
    }

    componentDidMount() {
        CourseStatisticsService.getLessonStatistics(this.props.match.params.lesson_id)
            .then(response => {
                if (response.ok) {
                    response.json().then(response => {
                        this.setState({
                            lesson_title: response.title,
                            lesson_index_inside_course: response.indexInsideCourse,
                            lesson_unknown_words_to_frequency: response.unknownWordsToFrequency,
                            course_title: response.courseTitle,
                            loading: false
                        });
                    })
                } else {
                    console.log("Error loading course data")
                }
            })
    }

    render() {
        let index = 0;

        let unknownWordTableRows = [];

        const unknown_words_to_frequency = this.state.lesson_unknown_words_to_frequency;

        Object.keys(this.state.lesson_unknown_words_to_frequency).forEach(function(word) {
            index++;

            unknownWordTableRows.push(
                <tr>
                    <td>{index}</td>
                    <td>{word}</td>
                    <td>{unknown_words_to_frequency[word]}</td>
                </tr>
            )
        })

        const lesson_link = "/supervised_lesson/" + this.props.match.params.lesson_id;

        return (
            <div className="col-md-12">
                <div className="card card-container">
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
                            <h5>{this.state.lesson_index_inside_course}. lesson of course <i>"{this.state.course_title}"</i></h5>

                            <div className="text-center">
                                <Link to={lesson_link}>
                                    <button type="button" className="btn btn btn-outline-secondary">
                                        Back to the Lesson
                                    </button>
                                </Link>
                            </div>

                            <hr/>

                            <Table striped bordered hover>
                                <thead>
                                    <tr>
                                        <th>#</th>
                                        <th>Word</th>
                                        <th>Unknown for ? students</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {unknownWordTableRows}
                                </tbody>
                            </Table>
                        </Fragment>
                    )}
                </div>
            </div>
        );
    }
}