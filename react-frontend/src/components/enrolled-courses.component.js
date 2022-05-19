import React, {Component, Fragment} from "react";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";
import CourseManagementService from "../services/course-management.service"
import UtilService from "../services/util.service"
import Select from 'react-select'
import {ListGroup, ListGroupItem} from "reactstrap";
import {Link} from "react-router-dom";


export default class EnrolledCourses extends Component {
    constructor(props) {
        super(props);
        this.handleJoinCourse = this.handleJoinCourse.bind(this)
        this.onChangeJoiningCode = this.onChangeJoiningCode.bind(this);
        this.state = {
            loading: true,
            courses: [],
            joining_code: undefined,
            message: ""
        };
    }

    getDictOfValue(v) {
        return { value: v, label: v }
    }

    componentDidMount() {
        CourseManagementService.getAllEnrolledCourses()
            .then(response => {
                if (response.ok) {
                    response.json().then(response => {
                        this.setState({
                            courses: response,
                            loading: false
                        });
                    })
                } else {
                    this.setState({
                        courses: [],
                        loading: false
                    });
                    console.log("Error loading enrolled courses")
                }
            })
    }

    handleJoinCourse(e) {
        CourseManagementService.joinSupervisedCourse(this.state.joining_code)
            .then(response => {
                if (response.ok) {
                    this.componentDidMount()
                } else {
                    response.json().then(response => response.messages.join("\n")).then(errorMsg => {
                        this.setState({
                            successful: false,
                            message: errorMsg
                        });
                    })
                }
            })
    }

    onChangeJoiningCode(e) {
        this.setState({
            joining_code: e.target.value
        })
    }

    render() {

        const courseListGroupItems = this.state.courses.map(course => {
            const href_link = "/course/" + course.id;
            return  <ListGroupItem
                as="li"
                tag='a'
                key={course.id}
                action
                href={href_link}
                // className="d-flex justify-content-between align-items-start"
            >
                <div className="ms-2 me-auto">
                    <div className="fw-bold">{course.title}</div>
                    <p>Language: {course.language}</p>
                    {course.teacher && (
                        <p>By: {course.teacher}</p>
                    )}
                </div>
            </ListGroupItem>
        })

        return (
            <div className="col-md-12">
                <div className="card card-container">
                    <img
                        src={require('../assets/course_list.png')}
                        alt="course_list"
                        className="img-card scale-down"
                    />
                    <h1>Enrolled Courses</h1>

                    <hr/>

                    <Form
                        onSubmit={this.handleJoinCourse}
                        ref={c => {
                            this.form = c;
                        }}
                        history={this.props.history}
                    >
                        <h5>Join new Course</h5>
                        <div className="form-group">
                            <label htmlFor="joining_code">Code:</label>
                            <Input
                                type="text"
                                className="form-control"
                                name="joining_code"
                                maxLength="50"
                                value={this.state.joining_code}
                                onChange={this.onChangeJoiningCode}
                                validations={[required]}
                            />
                        </div>
                        <div className="form-group text-center">
                            <button
                                className="btn btn-secondary btn-outline"
                                disabled={this.state.loading}
                            >
                                {this.state.loading && (
                                    <span className="spinner-border spinner-border-sm"/>
                                )}
                                <span>Join!</span>
                            </button>
                        </div>
                        {this.state.message && (
                            <div className="form-group">
                                <div className={
                                    this.state.successful
                                        ? "alert alert-success"
                                        : "alert alert-danger"
                                }
                                     role="alert">
                                    {this.state.message}
                                </div>
                            </div>
                        )}
                        <CheckButton
                            style={{display: "none"}}
                            ref={c => {
                                this.checkBtn = c;
                            }}
                        />
                    </Form>

                    <hr/>

                    <ListGroup as="ol">
                        {courseListGroupItems}
                    </ListGroup>
                </div>
            </div>
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