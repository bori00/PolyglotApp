import React, { Component } from "react";

export default class Home extends Component {
    constructor(props) {
        super(props);
        this.state = {
            content: ""
        };
    }

    render() {
        return (
            <div className="container">
                <header className="jumbotron">
                    <h3>"To learn a language is to have one more window from which to look at the world."</h3>
                </header>
            </div>
        );
    }
}