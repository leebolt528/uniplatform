import React, { Component } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip } from 'recharts';

/*const CustomizedAxisTick = React.createClass({
    render () {
        const {x, y, stroke, payload} = this.props;

        return (
            <g transform={`translate(${x},${y})`}>
                <text x={0} y={0} dy={16} textAnchor="end" fill="#666" transform="rotate(-10)">{payload.value}</text>
            </g>
        );
    }                           //tick={<CustomizedAxisTick/>}
});*/

class Chartbar extends Component {

    render() {
        return ( 
          <BarChart width={450} height={250} data={this.props.data}>
            <XAxis dataKey="name" />
            <YAxis />
            <CartesianGrid strokeDasharray="3 3" />
            <Tooltip />
            <Bar dataKey="value" fill="#82ca9d" maxBarSize={66} />
          </BarChart>
        );
    }
}

export default Chartbar;