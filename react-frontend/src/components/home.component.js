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
            <div className="col-md-12">
                <div className="card card-container">
                    <div className="container">
                        <img
                            src={require('../assets/languages_large.png')}
                            alt="world"
                            className="img-card scale-down"
                        />

                        <header className="jumbotron">
                            <h4 className="lead"><i>"To learn a language is to have one more window from which to look at the world."</i></h4>
                            <footer className="blockquote-footer">Chinese Prowerb</footer>
                        </header>

                        <hr/>

                        <h3>Get started today with Polyglot!</h3>
                    </div>
                </div>
            </div>
        );
    }
}