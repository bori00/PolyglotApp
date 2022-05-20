import React, {Component, Fragment} from "react";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";
import CourseManagementService from "../services/course-management.service"
import UtilService from "../services/util.service"
import Select from 'react-select'
import {ListGroup, ListGroupItem} from "reactstrap";
import {Link} from "react-router-dom";


export default class SupervisedCourse extends Component {
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
            lesson_ids_to_title: {},
            lessons: [],
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
                            lesson_ids_to_title: response.lessonIdsToTitle,
                            loading: false
                        });
                    })
                } else {
                    console.log("Error loading course data")
                }
            })
    }

    render() {

        let lessonListGroupItems = [];
        console.log(this.state.lesson_ids_to_title)
        const lesson_ids_to_title = this.state.lesson_ids_to_title;
        Object.keys(this.state.lesson_ids_to_title).forEach(function(id) {
            const href_link = "/supervised_lesson/" + id
            lessonListGroupItems.push(
                <ListGroupItem
                        as="li"
                        key={id}
                        tag='a'
                        action
                        href={href_link}
                        className="d-flex justify-content-between align-items-start"
                    >
                        <div className="ms-2 me-auto">
                            <div className="fw-bold">{lesson_ids_to_title[id]}</div>
                        </div>
                </ListGroupItem>
            )
        })

        const create_lesson_link = "/create_lesson/" + this.props.match.params.course_id  + "/true";

        const supervised_course_statistics_link = "/supervised_course_statistics/" + this.props.match.params.course_id;

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
                            <h6>Nr. of Students: {this.state.course_nr_of_students}</h6>
                            <h6>{this.state.course_language}</h6>

                            <div className="text-center">
                                <Link to={supervised_course_statistics_link}>
                                    <button type="button" className="btn btn-outline-secondary">
                                        Course Statistics
                                    </button>
                                </Link>
                            </div>


                            <hr/>

                            <div className="text-center">
                                <Link to={create_lesson_link}>
                                    <button type="button" className="btn btn-primary btn-block">
                                        Add New Lesson
                                    </button>
                                </Link>
                            </div>

                            <ListGroup as="ol">
                                {lessonListGroupItems}
                            </ListGroup>
                        </Fragment>
                    )}
                </div>
            </div>
        );
    }
}