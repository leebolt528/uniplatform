import React, { Component } from 'react';

class App extends Component {

  constructor( props ) {
    super( props );

    this.state= {
      count: null
    };

    this.handleClick = this.handleClick.bind( this );
  }

  handleClick() {
    this.setState( { count: this.state.count + 1 } );
  }

  render() {
    return (
      <div>
        <div style={ { float: 'float' } }>
        { <button onClick={ this.handleClick }>点击一次触发一次</button> }
        &nbsp;&nbsp;效果显示：{ this.state.count }
        </div>
      </div>
    );
  }

}

export default App;
