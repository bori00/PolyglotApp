import React, {Component, Fragment, View, Button} from "react";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";
import LessonPracticeService from "../services/lesson-practice.service"
import {Link} from "react-router-dom";

const API_URL = "http://localhost:8081/polyglot/";


export default class WordQuestion extends Component {
    // send though props: lesson_id
    constructor(props) {
        super(props);
        this.handleSubmitAnswer = this.handleSubmitAnswer.bind(this)
        this.onChangeUserTranslation = this.onChangeUserTranslation.bind(this);
        this.loadNewQuestion = this.loadNewQuestion.bind(this);
        this.state = {
            loading: true,
            wordToLearnId: undefined,
            word: "",
            currentPoints: undefined,
            targetPoints: undefined,
            sourceLanguage: "",
            targetLanguage: "",
            foreignToNative: undefined,
            userTranslation: "",
            evaluated: false,
            officialTranslation: "",
            accepted: undefined,
            errorMessage: ""
        };
    }

    loadNewQuestion() {
        this.setState({
            loading: true,
            userTranslation: ""
        })
        LessonPracticeService.getWordQuestion(this.props.match.params.lesson_id)
            .then(response => {
                if (response.ok) {
                    response.json().then(response => {
                        this.setState({
                            wordToLearnId: response.wordToLearnId,
                            word: response.word,
                            currentPoints: response.currentPoints,
                            targetPoints: response.targetPoints,
                            sourceLanguage: response.sourceLanguage,
                            targetLanguage: response.targetLanguage,
                            foreignToNative: response.foreignToNative,
                            loading: false,
                            evaluated: false,
                            officialTranslation: "",
                            accepted: undefined,
                            errorMessage: "",
                        });
                    })
                } else {
                    response.json().then(response => response.messages.join("\n")).then(errorMsg => {
                        this.setState({
                            errorMessage: errorMsg,
                        });
                        console.log("Error loading exercise")
                    })
                }

            })
    }

    componentDidMount() {
        this.loadNewQuestion()
    }

    onChangeUserTranslation(e) {
        this.setState({
            userTranslation: e.target.value
        })
    }

    handleSubmitAnswer(e) {
        e.preventDefault();
        this.form.validateAll();

        if (this.checkBtn.context._errors.length === 0) {
            LessonPracticeService.answerWordQuestion(this.state.wordToLearnId, this.state.userTranslation, this.state.foreignToNative)
                .then(response => {
                    if (response.ok) {
                        response.json().then(response => {
                            this.setState({
                                wordToLearnId: response.wordToLearnId,
                                word: response.word,
                                currentPoints: response.currentPoints,
                                targetPoints: response.targetPoints,
                                sourceLanguage: response.sourceLanguage,
                                targetLanguage: response.targetLanguage,
                                foreignToNative: response.foreignToNative,
                                loading: false,
                                evaluated: true,
                                officialTranslation: response.officialTranslation,
                                submittedTranslation: response.submittedTranslation,
                                accepted: response.accepted,
                            });
                        })
                    } else {
                       console.lof("ERROR AT EVAL")
                    }
                })
        }
    }


    render() {

        const lesson_link = "/lesson/" + this.props.match.params.lesson_id;

        return (
            <div className="col-md-12">
                <div className="card card-container">
                    <img
                        src={require('../assets/question.png')}
                        alt="course"
                        className="img-card scale-down"
                    />

                    {this.state.loading && (
                        <Fragment>
                            <p>Loading...</p>
                            <p style={{color: "green"}}>{this.state.errorMessage}</p>
                        </Fragment>
                    )}
                    {!this.state.loading && (
                        <Fragment>
                            <h1>Practice Question</h1>

                            <hr/>

                            <Form
                                onSubmit={this.handleSubmitAnswer}
                                ref={c => {
                                    this.form = c;
                                }}
                                history={this.props.history}
                            >
                                <h5>Translate the Word!</h5>

                                {/*<br/>*/}

                                <p><b>{this.state.currentPoints}/{this.state.targetPoints} points</b></p>

                                <div className="form-group">
                                    <label htmlFor="word">Word <i>(in {this.state.sourceLanguage})</i>:</label>
                                    <Input
                                        readOnly
                                        type="text"
                                        className="form-control"
                                        name="word"
                                        value={this.state.word}
                                    />
                                </div>
                                <div className="form-group">
                                    <label htmlFor="translation">Your Translation <i>(in {this.state.targetLanguage})</i>:</label>
                                    <Input
                                        type="text"
                                        className="form-control"
                                        name="translation"
                                        value={this.state.userTranslation}
                                        onChange={this.onChangeUserTranslation}
                                        validations={[required]}
                                    />
                                </div>
                                <div className="form-group text-center">
                                    <button
                                        className="btn btn-primary btn-outline"
                                        disabled={this.state.loading || (this.state.evaluated)}
                                    >
                                        {this.state.loading && (
                                            <span className="spinner-border spinner-border-sm"/>
                                        )}
                                        <span>Evaluate Answer</span>
                                    </button>
                                </div>
                                <CheckButton
                                    style={{display: "none"}}
                                    ref={c => {
                                        this.checkBtn = c;
                                    }}
                                />
                            </Form>

                            {/*<br/>*/}

                            {!this.state.evaluated && (
                                <p>Instructions: a correct answer is worth 1 point, but you loose 2 points for every wrong answer.</p>
                            )}

                            {this.state.evaluated && this.state.accepted && (
                                    <Fragment>
                                        <img
                                            src={require('../assets/correct.png')}
                                            alt="course"
                                            className="img-card scale-down"
                                            width={50}
                                        />

                                        {(this.state.submittedTranslation != this.state.officialTranslation) && (
                                            <p>Official Translation: {this.state.officialTranslation}</p>
                                        )}

                                        <button
                                            className="btn btn-primary btn-outline"
                                            onClick={this.loadNewQuestion}
                                        >
                                            <span>Next</span>
                                        </button>
                                    </Fragment>

                            )}

                            {this.state.evaluated && !this.state.accepted && (
                                <Fragment>
                                    <img
                                        src={require('../assets/incorrect.png')}
                                        alt="course"
                                        className="img-card scale-down"
                                        width={50}
                                    />

                                     <p>Correct Translation: {this.state.officialTranslation}</p>


                                    <button
                                        className="btn btn-primary btn-outline"
                                        onClick={this.loadNewQuestion}
                                    >
                                        <span>Next</span>
                                    </button>

                                </Fragment>

                            )}

                            <div className="text-center">
                                <Link to={lesson_link}>
                                    <button type="button" className="btn btn-secondary btn-block">
                                        Back to the Lesson
                                    </button>
                                </Link>
                            </div>


                        </Fragment>
                    )}
                </div>
            </div>
        );
    }
};

const required = value => {
    if (!value) {
        return (
            <div className="alert alert-danger" role="alert">
                This field is required!
            </div>
        );
    }
};