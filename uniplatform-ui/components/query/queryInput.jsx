import React, { Component } from 'react';
import {Select,Option,Input,FormItem} from 'epm-ui';

class QueryInput extends Component {
  
  constructor( props ) {
    super( props );
    
    this.state= {
      inputVal : ''
    }

  } 

  changeInput(option,data) {
      let inputVal = data;
      this.setState({ inputVal });
      this.props.getQueryInput(inputVal);
  }

  render() {
    let { inputVal } = this.state;
    return (
       <div style={{display: 'inline-block'}}>
          <Input type="text" value={inputVal} onChange={this.changeInput.bind(this,"field")}/>
        </div>
    );
  }

}

export default QueryInput;
export {QueryInput};