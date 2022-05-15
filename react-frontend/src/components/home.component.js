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
                    <h3>"People who love to eat are always the best people."</h3>
                </header>
            </div>
        );
    }
}