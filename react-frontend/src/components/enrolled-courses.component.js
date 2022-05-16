import React, {Component} from "react";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";
import CourseManagementService from "../services/course-management.service"
import UtilService from "../services/util.service"
import Select from 'react-select'
import {ListGroup, ListGroupItem} from "reactstrap";


export default class EnrolledCourses extends Component {
    constructor(props) {
        super(props);
        this.state = {
            loading: true,
            courses: []
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
                            courses: response
                        });
                    })
                } else {
                    this.setState({
                        courses: []
                    });
                    console.log("Error loading enrolled courses")
                }
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
                className="d-flex justify-content-between align-items-start"
            >
                <div className="ms-2 me-auto">
                    <div className="fw-bold">{course.title}</div>
                    {course.language}
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

                    <ListGroup as="ol">
                        {courseListGroupItems}
                    </ListGroup>
                </div>
            </div>
        );
    }
}