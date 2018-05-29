import React, { Component } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';

class StackedBarChart extends Component {

    render() {
        return (
            <BarChart width={450} height={250} data={this.props.data}>
                <XAxis dataKey="name"/>
                <YAxis/>
                <CartesianGrid strokeDasharray="3 3"/>
                <Tooltip/>
                <Legend />
                <Bar dataKey="low" stackId="a" fill="#82ca9d" maxBarSize={66} />
                <Bar dataKey="middle" stackId="a" fill="#e1de60" maxBarSize={66} />
                <Bar dataKey="high" stackId="a" fill="#ffa717" maxBarSize={66} />
                <Bar dataKey="top" stackId="a" fill="#d52b0b" maxBarSize={66} />
            </BarChart>
        );
    }
}

export default StackedBarChart;