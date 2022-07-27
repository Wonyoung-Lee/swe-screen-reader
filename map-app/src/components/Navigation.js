import './App.css';
import React, { Component } from "react";
import Button from "react-bootstrap/Button";
import ButtonGroup from "react-bootstrap/ButtonGroup";

export default class Routes extends Component {
    /* this class displays the list containing all the piece cards */
    render() {
        return (
            <div className="info_box padding">
                    <h3>Navigation</h3>
                    <p>Use these buttons to pan</p>
                    <ButtonGroup className="mb-2">
                        <Button id={"lt-btn"}>Left</Button>
                        <Button id={"up-btn"}>Up</Button>
                        <Button id={"rt-btn"}>Right</Button>
                        <Button id={"dn-btn"}>Down</Button>
                    </ButtonGroup>
            </div>
        )
    }

}