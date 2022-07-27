import './App.css';
import React, { Component } from "react";
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';

export default class CurrentlyLoaded extends Component {
    /* this class displays the list containing all the piece cards */
    render() {
        return (
            <div className="info_box">
                <h3>Getting Routes</h3>
                <Form>
                    <Form.Group className="mb-3" controlId="formBasicEmail">
                        <Form.Label>Street 1</Form.Label>
                        <Form.Control type="text" placeholder="Enter street name" />
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="formBasicPassword">
                        <Form.Label>Cross Street 1</Form.Label>
                        <Form.Control type="text" placeholder="Enter cross street" />
                    </Form.Group>
                    <Button variant="success" type="submit">
                        Manual Input
                    </Button>
                </Form>
                <Form>
                    <Form.Group className="mb-3" controlId="formBasicEmail">
                        <Form.Label>Street 2</Form.Label>
                        <Form.Control type="text" placeholder="Enter street name" />
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="formBasicPassword">
                        <Form.Label>Cross Street 2</Form.Label>
                        <Form.Control type="text" placeholder="Enter cross street" />
                    </Form.Group>
                    <Button variant="success" type="submit">
                        Manual Input
                    </Button>
                    <div className="padding">
                        <Button variant="primary" type="submit">
                            Get Route
                        </Button>
                    </div>
                </Form>

            </div>
        )
    }

}