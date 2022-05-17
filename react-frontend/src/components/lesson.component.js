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
        this.handleSaveUnknownWord = this.handleSaveUnknownWord.bind(this)
        this.onChangeWord = this.onChangeWord.bind(this);
        this.state = {
            loading: true,
            lesson_file_url: undefined,
            word: undefined,
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

    onChangeWord(e) {
        this.setState({
            word: e.target.value
        })
    }

    handleSaveUnknownWord(e) {
        e.preventDefault();
        this.form.validateAll();

        if (this.checkBtn.context._errors.length === 0) {
            CourseManagementService.saveUnknownWord(this.props.match.params.lesson_id, this.state.word)
                .then(response => {
                    if (response.ok) {
                        this.setState({
                            message: "Saved " + this.state.word,
                            successful: true
                        })
                    } else {
                        this.setState({
                            message: "Could not save " + this.state.word,
                            successful: false
                        })
                    }
                })
        }
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

                            <button className="btn btn-primary btn-block"
                                    disabled={this.state.loading}>
                                Practice
                            </button>

                            <hr></hr>

                            <Form
                                onSubmit={this.handleSaveUnknownWord}
                                ref={c => {
                                    this.form = c;
                                }}
                                history={this.props.history}
                            >
                                <h5>Add new Word to Learn</h5>
                                <div className="form-group">
                                    <label htmlFor="word">Unknown Word:</label>
                                    <Input
                                        type="text"
                                        className="form-control"
                                        name="word"
                                        maxlength="50"
                                        value={this.state.word}
                                        onChange={this.onChangeWord}
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
                                        <span>Save Word</span>
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