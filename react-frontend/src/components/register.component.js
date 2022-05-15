import React, {Component} from "react";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";
import AuthService from "../services/auth.service";
import UtilService from "../services/util.service"
import Select from 'react-select'

export default class Register extends Component {
    constructor(props) {
        super(props);
        this.handleRegister = this.handleRegister.bind(this);
        this.onChangeUsername = this.onChangeUsername.bind(this);
        this.onChangePassword = this.onChangePassword.bind(this);
        this.onChangeEmailAddress = this.onChangeEmailAddress.bind(this);
        this.onChangeAccountType = this.onChangeAccountType.bind(this);
        this.onChangeNativeLanguage = this.onChangeNativeLanguage.bind(this)
        this.state = {
            username: "",
            password: "",
            emailAddress: "",
            teacher: false,
            nativeLanguage: "",
            successful: false,
            message: "",
            possibleLanguages: []
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

    onChangeUsername(e) {
        this.setState({
            username: e.target.value
        });
    }


    onChangePassword(e) {
        this.setState({
            password: e.target.value
        });
    }

    onChangeEmailAddress(e) {
        this.setState({
            emailAddress: e.target.value
        });
    }

    onChangeAccountType(e) {
        this.setState({
            teacher: e.target.checked
        });
    }

    onChangeNativeLanguage(e) {
        this.setState({
            nativeLanguage: e.value
        })
    }

    handleRegister(e) {
        e.preventDefault();
        this.setState({
            message: "",
            successful: false
        });

        this.form.validateAll();

        if (this.checkBtn.context._errors.length === 0) {
            AuthService.register(
                this.state.username,
                this.state.password,
                this.state.emailAddress,
                this.state.teacher,
                this.state.nativeLanguage
            ).then(response => {
                    if (response.ok) {
                        this.setState({
                            message: "Successful registration.",
                            successful: true
                        });
                    } else {
                        response.json().then(response => response.messages.join("\n")).then(errorMsg => {
                            this.setState({
                                successful: false,
                                message: errorMsg
                            });
                        })
                    }}
                );

        }
    }

    render() {
        return (
            <div className="col-md-12">
                <div className="card card-container">
                    <img
                        src="//ssl.gstatic.com/accounts/ui/avatar_2x.png"
                        alt="profile-img"
                        className="img-card round-img"
                    />
                    <Form
                        onSubmit={this.handleRegister}
                        ref={c => {
                            this.form = c;
                        }}
                    >
                        {!this.state.successful && (
                            <div>
                                <div className="form-group">
                                    <label htmlFor="username">Username</label>
                                    <Input
                                        type="text"
                                        className="form-control"
                                        name="username"
                                        value={this.state.username}
                                        onChange={this.onChangeUsername}
                                        validations={[required, vusername]}
                                    />
                                </div>
                                <div className="form-group">
                                    <label htmlFor="password">Password</label>
                                    <Input
                                        type="password"
                                        className="form-control"
                                        name="password"
                                        value={this.state.password}
                                        onChange={this.onChangePassword}
                                        validations={[required, vpassword]}
                                    />
                                </div>
                                <div className="form-group">
                                    <label htmlFor="email">Email Address</label>
                                    <Input
                                        type="email"
                                        className="form-control"
                                        name="email"
                                        value={this.state.emailAddress}
                                        onChange={this.onChangeEmailAddress}
                                        validations={[required, vemailaddress]}
                                    />
                                </div>
                                <div className="form-group">
                                    <label htmlFor="accountType">Teacher Account?</label>
                                    <Input
                                        type="checkbox"
                                        name="accountType"
                                        value={this.state.teacher}
                                        onChange={this.onChangeAccountType}
                                    />
                                </div>
                                <div className="form-group">
                                    <label htmlFor="nativeLanguage">Native Language:</label>
                                    <Select options={this.state.possibleLanguages}
                                            name="nativeLanguage"
                                            onChange={this.onChangeNativeLanguage}
                                    />
                                </div>
                                <div className="form-group text-center">
                                    <button className="btn btn-primary btn-block">Sign Up</button>
                                </div>
                            </div>
                        )}
                        {this.state.message && (
                            <div className="form-group">
                                <div
                                    className={
                                        this.state.successful
                                            ? "alert alert-success"
                                            : "alert alert-danger"
                                    }
                                    role="alert"
                                >
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

const vusername = value => {
    if (value.length < 3 || value.length > 30) {
        return (
            <div className="alert alert-danger" role="alert">
                The username must be between 3 and 30 characters.
            </div>
        );
    }
};

const vpassword = value => {
    if (value.length < 1 || value.length > 100) {
        return (
            <div className="alert alert-danger" role="alert">
                The password must be between 3 and 100 characters.
            </div>
        );
    }
};

const vemailaddress = value => {
    if (value.length < 1 || value.length > 100) {
        return (
            <div className="alert alert-danger" role="alert">
                The email address must be between 3 and 100 characters.
            </div>
        );
    }
    if (!(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(value)))
    {
        return (
            <div className="alert alert-danger" role="alert">
                Please provide a valide email address.
            </div>
        );
    }
};