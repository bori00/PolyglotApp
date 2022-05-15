import React, {Component} from "react";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";
import CourseManagementService from "../services/course-management.service"
import UtilService from "../services/util.service"
import Select from 'react-select'


export default class CreateLesson extends Component {
    constructor(props) {
        super(props);
        this.handleSaveLesson = this.handleSaveCourse.bind(this)
        this.onChangeTitle = this.onChangeTitle.bind(this);
        this.onChangeLanguage = this.onChangeLanguage.bind(this);
        this.onChangeWordTarget = this.onChangeWordTarget.bind(this)
        this.state = {
            title: "",
            language: "",
            wordTarget: 0.0,
            possibleLanguages: [],
            successful: false,
            message: "",
            loading: false
        };
    }

    getDictOfValue(v) {
        return { value: v, label: v }
    }

    componentDidMount() {
        UtilService.getAllLanguages()
            .then(response => {
                if (response.ok) {
                    response.json().then(response => {
                        this.setState({
                            possibleLanguages: response.map(language => this.getDictOfValue(language))
                        });
                    })
                } else {
                    this.setState({
                        possibleLanguages: []
                    });
                    console.log("Error loading possible languages")
                }
            })
    }

    onChangeTitle(e) {
        this.setState({
            title: e.target.value
        });
    }

    onChangeWordTarget(e) {
        this.setState({
            wordTarget: e.target.value
        });
    }

    onChangeLanguage(e) {
        this.setState({
            language: e.value
        });
    }

    handleSaveCourse(e) {

        e.preventDefault();

        this.setState({
            message: "",
            successful: false,
            loading: false
        });

        this.form.validateAll();
        if (this.checkBtn.context._errors.length === 0) {
            CourseManagementService.saveNewSelfTaughtCourse(this.state.title, this.state.language, this.state.wordTarget)
                .then(response => {
                        if (response.ok) {
                            this.props.history.push("/home");
                            window.location.reload();
                        } else {
                            response.json().then(response => response.messages.join("\n")).then(errorMsg => {
                                this.setState({
                                    successful: false,
                                    message: errorMsg
                                });
                            })
                        }
                    }
                );
        } else {
            this.setState({
                loading: false
            });
        }
    }

    render() {

        return (
            <div className="col-md-12">
                <div className="card card-container">
                    <img
                        src={require('../assets/course.png')}
                        alt="food-img"
                        className="img-card scale-down"
                    />
                    <Form
                        onSubmit={this.handleSaveCourse}
                        ref={c => {
                            this.form = c;
                        }}
                        history={this.props.history}
                    >
                        <h1>Create a new Course</h1>
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
                        <div className="form-group">
                            <label htmlFor="wordTarget">Word Target:</label>
                            <Input
                                type="number"
                                className="form-control"
                                name="wordTarget"
                                min="1"
                                max="25"
                                value={this.state.wordTarget}
                                onChange={this.onChangeWordTarget}
                                validations={[required]}
                            />
                        </div>
                        <div className="form-group">
                            <label htmlFor="language">Target Language:</label>
                            <Select options={this.state.possibleLanguages}
                                    name="language"
                                    onChange={this.onChangeLanguage}
                            />
                        </div>
                        <div className="form-group text-center">
                            <button
                                className="btn btn-primary btn-block"
                                disabled={this.state.loading}
                            >
                                {this.state.loading && (
                                    <span className="spinner-border spinner-border-sm"/>
                                )}
                                <span>Save Course</span>
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