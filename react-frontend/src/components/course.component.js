import React, {Component} from "react";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";
import CourseManagementService from "../services/course-management.service"
import UtilService from "../services/util.service"
import Select from 'react-select'
import {ListGroup, ListGroupItem} from "reactstrap";
import {Link} from "react-router-dom";


export default class Course extends Component {
    // send though props: course_id
    constructor(props) {
        super(props);
        this.state = {
            loading: true,
            lessons: []
        };
    }

    getDictOfValue(v) {
        return { value: v, label: v }
    }

    render() {

        // const courseListGroupItems = this.state.courses.map(course => {
        //     return  <ListGroupItem
        //         as="li"
        //         key={course.id}
        //         className="d-flex justify-content-between align-items-start"
        //     >
        //         <div className="ms-2 me-auto">
        //             <div className="fw-bold">{course.title}</div>
        //             {course.language}
        //             {course.teacher && (
        //                 <p>By: {course.teacher}</p>
        //             )}
        //         </div>
        //     </ListGroupItem>
        // })

        return (
            <div className="col-md-12">
                <div className="card card-container">
                    <img
                        src={require('../assets/course.png')}
                        alt="course"
                        className="img-card scale-down"
                    />
                    <h1>{this.props.match.params.course_id}</h1>

                    <hr/>

                    {/*<ListGroup as="ol">*/}
                    {/*    {courseListGroupItems}*/}
                    {/*</ListGroup>*/}
                </div>
            </div>
        );
    }
}