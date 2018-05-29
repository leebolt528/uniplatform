import React, { Component } from 'react';
import {Select,Option,Input,FormItem} from 'epm-ui';

class QueryAll extends Component {
  
  constructor( props ) {
    super( props );
    
    this.state= {
      query_string: {},
      inputVal : ''
    }

    this.getQuery = this.getQuery.bind(this);
  } 

  componentDidMount() {
      this.getQuery();
  }

  getQuery(){
    let query_string = {},datafield = {};
    let inputVal = this.state.inputVal;
    let queryString = {
      default_field : this.props.queryAll,
      query : inputVal
    };
    datafield["query_string"] = queryString;
    query_string["data"] = datafield;
    this.setState({ query_string });
    this.props.getQueryAll(query_string);
  }

  changeInput(option,data) {
      let inputVal = data;
      this.setState({ inputVal },()=> this.getQuery());
  }

  render() {
    let { inputVal } = this.state;
    return (
       <div style={{display: 'inline-block'}} className='queryAll'>
        <div style={{display: 'inline-block',marginRight:'8px'}}>
          <Select value={ 'query_string' } >
              <Option value="query_string">query_string</Option>
          </Select>
        </div>
        <div style={{display: 'inline-block'}}>
          <Input type="text" value={inputVal} onChange={this.changeInput.bind(this,"field")}/>
        </div>
      </div>
    );
  }

}

export default QueryAll;
export {QueryAll};