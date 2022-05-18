import React, { Component } from "react";
import UtilService from "../services/util.service";
import CourseManagementService from "../services/course-management.service";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import Select from "react-select";
import CheckButton from "react-validation/build/button";
import AuthService from "../services/auth.service"

export default class CreateLesson extends Component {
    // send through props: course_id
    constructor(props) {
        super(props);
        this.handleSaveLesson = this.handleSaveLesson.bind(this)
        this.onChangeTitle = this.onChangeTitle.bind(this);
        this.onSelectFile = this.onSelectFile.bind(this);
        this.state = {
            title: "",
            file: undefined,
            progress: 0,
            message: "",
            fileInfos: [],
            successful: false,
            loading: true
        };
    }

    componentDidMount() {
        this.setState({
            loading: false
        })
    }

    onChangeTitle(e) {
        this.setState({
            title: e.target.value
        });
    }

    onSelectFile(e) {
        this.setState({
            file: e.target.files[0],
        });
    }

    handleSaveLesson(e) {

        e.preventDefault();

        this.setState({
            message: "",
            successful: false,
            progress: 0
        });

        this.form.validateAll();

        if (this.checkBtn.context._errors.length === 0) {
            if (this.state.file.size > 1048576 * 10) { // max 10MB
                this.setState({
                    message: "Max file size: 10MB"
                })
            } else {
                CourseManagementService.saveNewLesson(this.state.title, this.state.file, this.props.match.params.course_id, (event) => {
                    this.setState({
                        progress: Math.round((100 * event.loaded) / event.total),
                    })
                }).then((response) => {
                    this.props.history.push("/course/" + this.props.match.params.course_id)
                    window.location.reload();
                }, (error) => {
                        this.setState({
                            successful: false,
                            message: error.data
                        })
                    }
                )
            }
        } else {
            this.setState({
                loading: false
            })
        }
    }

    render() {

        return (
            <div className="col-md-12">
                <div className="card card-container">
                    <img
                        src={require('../assets/lesson.png')}
                        alt="food-img"
                        className="img-card scale-down"
                    />
                    <Form
                        onSubmit={this.handleSaveLesson}
                        ref={c => {
                            this.form = c;
                        }}
                        history={this.props.history}
                    >
                        <h1>Create a new Lesson</h1>
                        <div className="form-group">
                            <label htmlFor="title">Title:</label>
                            <Input
                                type="text"
                                className="form-control"
                                name="title"
                                value={this.state.title}
                                onChange={this.onChangeTitle}
                                validations={[required]}
                            />
                        </div>
                        {this.state.file && (
                            <div className="progress">
                                <div
                                    className="progress-bar progress-bar-info progress-bar-striped"
                                    role="progressbar"
                                    aria-valuenow={this.state.progress}
                                    aria-valuemin="0"
                                    aria-valuemax="100"
                                    style={{ width: this.state.progress + "%" }}
                                >
                                    {this.state.progress}%
                                </div>
                            </div>
                        )}
                        <label className="btn btn-outline-secondary">
                            <input type="file" onChange={this.onSelectFile} accept=".pdf"/>
                        </label>
                        <p>Max 10MB, PDF only</p>
                        <div className="form-group text-center">
                            <button
                                className="btn btn-primary btn-block"
                                disabled={this.state.loading}
                            >
                                {this.state.loading && (
                                    <span className="spinner-border spinner-border-sm"/>
                                )}
                                <span>Save Lesson</span>
                            </button>
                        </div>
                        {this.state.message && (
                            <div className="form-group">
                                <div className="alert alert-danger" role="alert">
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