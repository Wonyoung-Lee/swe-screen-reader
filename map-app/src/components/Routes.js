import './App.css';
import React, { Component } from "react";

export default class Routes extends Component {
    /* this class displays the list containing all the piece cards */
    render() {
        return (
            <div className="info_box">
                <div>
                    <h3>Routes</h3>
                    <p>these are the routes to take between the two locations:</p>
                </div>
                <div className="padding placeholder">
                </div>
            </div>
        )
    }

}