import './App.css';
import React, { Component } from "react";
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';


export default class CurrentlyLoaded extends Component {
    /* this class displays the list containing all the piece cards */
    render() {
        return (
            <div className="info_box">
                <div>
                    <h3>Currently Loaded:</h3>
                    <p>data/maps/maps.sqlite3</p>
                </div>
                <div>
                    <Form.Group controlId="formFile" className="mb-3">
                        <Form.Label>Select Database</Form.Label>
                        <Form.Control type="file" />
                    </Form.Group>
                </div>
                <Button variant="primary">Load DB</Button>{' '}
            </div>
        )
    }

}